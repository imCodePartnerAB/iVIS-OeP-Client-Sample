package com.imcode.oeplatform.flowengine.queries.linked.dropdownquery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.entities.interfaces.JpaEntity;
import com.imcode.oeplatform.flowengine.populators.dao.DaoPopulatorFactory;
import com.imcode.oeplatform.flowengine.populators.dao.DaoPopulator;
import com.imcode.oeplatform.flowengine.populators.entity.application.ApplicationPopulator;
import com.imcode.oeplatform.flowengine.populators.query.*;
import com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery.LinkedAlternativeQueryUtils;
import com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery.LinkedAlternativesQueryCallback;
import com.imcode.oeplatform.oauth2.modules.foreground.IvisOAuth2User;
import com.imcode.oeplatform.oauth2.utils.OAuth2Utils;
import com.imcode.services.GenericService;
import com.imcode.services.PupilService;
import com.nordicpeak.flowengine.Constants;
import com.nordicpeak.flowengine.accesscontrollers.UserFlowInstanceAccessController;
import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.interfaces.*;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryCRUDCallback;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryProviderModule;
import com.nordicpeak.flowengine.queries.tablequery.SummaryTableQueryCallback;
import com.nordicpeak.flowengine.utils.JTidyUtils;
import com.nordicpeak.flowengine.utils.TextTagReplacer;
import imcode.services.IvisServiceFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyAccessor;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.hierarchy.core.annotations.*;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.*;
import se.unlogic.standardutils.datatypes.Matrix;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLGenerator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;
import se.unlogic.webutils.url.URLRewriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LinkedDropDownQueryProviderModule extends BaseQueryProviderModule<LinkedDropDownQueryInstance> implements BaseQueryCRUDCallback, LinkedAlternativesQueryCallback<LinkedDropDownQuery>, SummaryTableQueryCallback<LinkedDropDownQuery> {

    public static class ClassPopulator implements BeanStringPopulator<String> {
        @Override
        public String getValue(String value) {
            return value;
        }

        @Override
        public Class<? extends String> getType() {
            return String.class;
        }

        @Override
        public String getPopulatorID() {
            return null;
        }

        @Override
        public boolean validateFormat(String value) {
            try {
                Class<?> clazz = Class.forName(value);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
    }

//    public static class ClassStringyfie implements Stringyfier<Class> {
//
//        @Override
//        public String format(Class bean) {
//            return bean.getName();
//        }
//
//    }


    private static final String DEFAULT_CLIENT_SCOPE_STRING = "read\nwrite";

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server Url", description = "iVIS server url \"http://localhost:8080\"", required = true)
    protected String serverUrl;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Client id", description = "iVIS client id", required = true)
    protected String clientId;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Client secret", description = "iVIS secret", required = true)
    protected String clientSecret;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server access token path", description = "iVIS server access token path to retrive new access token \"/oauth/token\";", required = true)
    protected String accessTokenPath = OAuth2Utils.DEFAULT_ACCESS_TOKEN_PATH;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server API path", description = "iVIS server API path to negotiating with server \"/api/v1/json\";", required = true)
    protected String apiPath = OAuth2Utils.DEFAULT_API_PATH;

    @ModuleSetting
    @TextAreaSettingDescriptor(name = "Client scopeList", description = "iVIS client scopeList", required = true)
    protected String clientScope = DEFAULT_CLIENT_SCOPE_STRING;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "Server username", description = "iVIS server username to retrive access token", required = true)
    protected String username;

    @ModuleSetting
    @PasswordSettingDescriptor(name = "Server password", description = "iVIS server user password", required = true)
    protected String password;

    //    @ModuleSetting
//    @TextFieldSettingDescriptor(name = "Entity to alternative converter", description = "Class to convert retriven entities to alternatives", formatValidator = ClassPopulator.class,required = true)
//    protected String entityConvertionServiceClass;

    transient private IvisServiceFactory serviceFactory;

    private AnnotatedDAO<LinkedDropDownQuery> queryDAO;
    private AnnotatedDAO<LinkedDropDownQueryInstance> queryInstanceDAO;

    private LinkedDropDownQueryCRUD queryCRUD;

    private QueryParameterFactory<LinkedDropDownQuery, Integer> queryIDParamFactory;
    private QueryParameterFactory<LinkedDropDownQueryInstance, Integer> queryInstanceIDParamFactory;
    private QueryParameterFactory<LinkedDropDownQueryInstance, LinkedDropDownQuery> queryInstanceQueryParamFactory;

//    protected ConversionService entityConvertionService;

    @Override
    protected synchronized void moduleConfigured() throws Exception {
        super.moduleConfigured();

        //Initializing ivis factory for retrying linked entities
        OAuth2ProtectedResourceDetails resource = OAuth2Utils.createPsswordResourceDetails(clientId,
                clientSecret,
                serverUrl + accessTokenPath,
                Arrays.asList(clientScope.split("\\s+")),
                username, password);

        serviceFactory = OAuth2Utils.createIvisServiceFactory(serverUrl + apiPath, resource);

        //Creating convertion Service
//        @SuppressWarnings("unchecked")
//        Class<ConversionService> conversionServiceClass = (Class<ConversionService>) Class.forName(entityConvertionServiceClass);
//        entityConvertionService = conversionServiceClass.newInstance();

    }


    @Override
    protected void createDAOs(DataSource dataSource) throws Exception {

        //Automatic table version handling
        UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, LinkedDropDownQueryProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

        if (upgradeResult.isUpgrade()) {

            log.info(upgradeResult.toString());
        }

        SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);
        final Function<String, Class> classNameParser = s -> {
            try {
                return Class.forName(s);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Class not found", e);
            }
        };
        final DaoPopulator<Class> classDaoPopulator = DaoPopulatorFactory.get(Class.class, classNameParser, o -> ((Class) o).getName());
        daoFactory.addBeanStringPopulator(classDaoPopulator);
        daoFactory.addQueryParameterPopulator(classDaoPopulator);

        final DaoPopulator<Serializable> alternativePopulator = DaoPopulatorFactory.get(LinkedDropDownAlternative.class);

        daoFactory.addBeanStringPopulator(alternativePopulator);
        daoFactory.addQueryParameterPopulator(alternativePopulator);

        queryDAO = daoFactory.getDAO(LinkedDropDownQuery.class);
        queryInstanceDAO = daoFactory.getDAO(LinkedDropDownQueryInstance.class);

        queryCRUD = new LinkedDropDownQueryCRUD(queryDAO.getWrapper(Integer.class), new AnnotatedRequestPopulator<LinkedDropDownQuery>(LinkedDropDownQuery.class), "LinkedDropDownQuery", "query", null, this);

        queryIDParamFactory = queryDAO.getParamFactory("queryID", Integer.class);
        queryInstanceIDParamFactory = queryInstanceDAO.getParamFactory("queryInstanceID", Integer.class);
        queryInstanceQueryParamFactory = queryInstanceDAO.getParamFactory("query", LinkedDropDownQuery.class);

    }

    @Override
    public Query createQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws SQLException {

        LinkedDropDownQuery query = new LinkedDropDownQuery();

        query.setQueryID(descriptor.getQueryID());

        this.queryDAO.add(query, transactionHandler, null);

        query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

        return query;
    }

    @Override
    public Query getQuery(MutableQueryDescriptor descriptor) throws SQLException {

        LinkedDropDownQuery query = this.getQuery(descriptor.getQueryID());

        if (query == null) {

            return null;
        }

        query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

        return query;
    }

    @Override
    public Query getQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

        LinkedDropDownQuery query = this.getQuery(descriptor.getQueryID(), transactionHandler);

        if (query == null) {

            return null;
        }

        query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

        return query;
    }

    private LinkedDropDownQueryInstance getQueryInstance(Integer queryInstanceID) throws SQLException {

//		HighLevelQuery<LinkedDropDownQueryInstance> query = new HighLevelQuery<LinkedDropDownQueryInstance>(LinkedDropDownQueryInstance.ALTERNATIVE_RELATION, LinkedDropDownQueryInstance.QUERY_RELATION);
        HighLevelQuery<LinkedDropDownQueryInstance> query = new HighLevelQuery<LinkedDropDownQueryInstance>(LinkedDropDownQueryInstance.QUERY_RELATION);

        query.addParameter(queryInstanceIDParamFactory.getParameter(queryInstanceID));

        return queryInstanceDAO.get(query);
    }

    @Override
    public Query importQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

        LinkedDropDownQuery query = new LinkedDropDownQuery();

        query.setQueryID(descriptor.getQueryID());

        query.populate(descriptor.getImportParser().getNode(XMLGenerator.getElementName(query.getClass())));

        List<?> oldAlternativeIDs = LinkedAlternativeQueryUtils.getAlternativeIDs(query);

        LinkedAlternativeQueryUtils.clearAlternativeIDs(query.getAlternatives());

        this.queryDAO.add(query, transactionHandler, null);

//		query.setAlternativeConversionMap(FixedAlternativeQueryUtils.getAlternativeConversionMap(query.getAlternatives(), oldAlternativeIDs));

        return query;
    }

    protected void initQuery(LinkedDropDownQuery query, IvisServiceFactory factory) {
        String entityClassname = query.getEntityClassname();
        Class clazz;
        try {
            clazz = Class.forName(entityClassname);
        } catch (Exception e) {
            log.error("Cannot parse entity class for name \"" + entityClassname + "\"", e);
            return;
        }

        GenericService entityService = factory.getServiceFor(clazz);

        query.setEntityService(entityService);
        @SuppressWarnings("unchecked")
        List<JpaEntity> allEntities = entityService.findAll();

        List<LinkedDropDownAlternative> alternatives = allEntities.stream()
                .map(entity -> new LinkedDropDownAlternative((Long) entity.getId(), entity.toString()))
                .collect(Collectors.toList());

        query.setAlternatives(alternatives);
        query.setEntities(allEntities);
    }

//    protected void initQuery(LinkedDropDownQuery query) {
//        initQuery(query, serviceFactory);
//    }

    @Override
    public QueryInstance getQueryInstance(MutableQueryInstanceDescriptor descriptor, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws SQLException {

        LinkedDropDownQueryInstance queryInstance;

        //Check if we should create a new instance or get an existing one
        if (descriptor.getQueryInstanceID() == null) {

            queryInstance = new LinkedDropDownQueryInstance();

        } else {

            queryInstance = getQueryInstance(descriptor.getQueryInstanceID());

            if (queryInstance == null) {

                return null;
            }
        }

        LinkedDropDownQuery query = getQuery(descriptor.getQueryDescriptor().getQueryID());

        if (user instanceof IvisOAuth2User) {
            IvisOAuth2User<?> ivisUser = (IvisOAuth2User) user;
            IvisServiceFactory factory = ivisUser.getServiceFactory();

            if (factory != null) {
                initQuery(query,factory);
            }
        }

        queryInstance.setQuery(query);

        if (queryInstance.getQuery() == null) {

            return null;
        }

        if (req != null) {

            FCKUtils.setAbsoluteFileUrls(queryInstance.getQuery(), RequestUtils.getFullContextPathURL(req) + ckConnectorModuleAlias);

            URLRewriter.setAbsoluteLinkUrls(queryInstance.getQuery(), req, true);
        }

        TextTagReplacer.replaceTextTags(queryInstance.getQuery(), instanceMetadata.getSiteProfile());

        queryInstance.set(descriptor);

        //If this is a new query instance copy the default values
        if (descriptor.getQueryInstanceID() == null) {

            queryInstance.copyQueryValues();
        }

        return queryInstance;
    }

    public LinkedDropDownQuery getQuery(Integer queryID) throws SQLException {

        HighLevelQuery<LinkedDropDownQuery> query = new HighLevelQuery<LinkedDropDownQuery>(LinkedDropDownQuery.ALTERNATIVES_RELATION);

        query.addParameter(queryIDParamFactory.getParameter(queryID));

        final LinkedDropDownQuery linkedQuery = queryDAO.get(query);
//        initQuery(linkedQuery);

        return linkedQuery;
    }

    public LinkedDropDownQuery getQuery(Integer queryID, TransactionHandler transactionHandler) throws SQLException {

        HighLevelQuery<LinkedDropDownQuery> query = new HighLevelQuery<LinkedDropDownQuery>(LinkedDropDownQuery.ALTERNATIVES_RELATION);

        query.addParameter(queryIDParamFactory.getParameter(queryID));

        final LinkedDropDownQuery linkedQuery = queryDAO.get(query, transactionHandler);
//        initQuery(linkedQuery);

        return linkedQuery;
    }

    @Override
    public void save(LinkedDropDownQueryInstance queryInstance, TransactionHandler transactionHandler) throws Throwable {

        if (queryInstance.getQueryInstanceID() == null || !queryInstance.getQueryInstanceID().equals(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID())) {

            queryInstance.setQueryInstanceID(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID());

            this.queryInstanceDAO.add(queryInstance, transactionHandler, null);

        } else {

            this.queryInstanceDAO.update(queryInstance, transactionHandler, null);
        }
    }

    @Override
    public void populate(LinkedDropDownQueryInstance queryInstance, HttpServletRequest req, User user, boolean allowPartialPopulation, MutableAttributeHandler attributeHandler) throws ValidationException {

        List<LinkedDropDownAlternative> availableAlternatives = queryInstance.getQuery().getAlternatives();

        if (CollectionUtils.isEmpty(availableAlternatives)) {

            //If the parent query doesn't have any alternatives then there is no population to do
            queryInstance.reset(attributeHandler);
            return;
        }

        Integer alternativeID = NumberUtils.toInt(req.getParameter("q" + queryInstance.getQuery().getQueryID() + "_alternative"));

        boolean alternativeSelected = false;

        LinkedDropDownAlternative selectedAlternative = null;

        if (alternativeID != null) {

            for (LinkedDropDownAlternative alternative : availableAlternatives) {

                if (alternative.getAlternativeID().equals(alternativeID)) {

                    selectedAlternative = alternative;
                    alternativeSelected = true;
                    break;
                }
            }
        }

        List<ValidationError> validationErrors = new ArrayList<ValidationError>();

//		String freeTextAlternativeValue = null;
//
//		if(!alternativeSelected) {
//
//			freeTextAlternativeValue = FreeTextAlternativePopulator.populate(queryInstance.getQuery().getQueryID(), "_alternative", req, validationErrors);
//
//			if(freeTextAlternativeValue != null) {
//				alternativeSelected = true;
//			}
//
//		}

        //If partial population is allowed, skip validation
//		if (allowPartialPopulation && freeTextAlternativeValue == null) {
        if (allowPartialPopulation) {

            queryInstance.setAlternative(selectedAlternative);
//			queryInstance.setEntityClassname(null);
            queryInstance.getQueryInstanceDescriptor().setPopulated(selectedAlternative != null);
            return;
        }

        //Check if this query is required and if the user has selected any alternative
        if (queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED && !alternativeSelected) {

            validationErrors.add(new ValidationError("RequiredQuery"));
        }

        if (!validationErrors.isEmpty()) {
            throw new ValidationException(validationErrors);
        }

//		queryInstance.setEntityClassname(freeTextAlternativeValue);
        queryInstance.setAlternative(selectedAlternative);
        queryInstance.getQueryInstanceDescriptor().setPopulated(selectedAlternative != null);
    }

    //	@WebPublic
//	public ForegroundModuleResponse addDummyAlternatives(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {
//
//		//Add random alternatives to queries which don't have any alternatives
//
//		HighLevelQuery<LinkedDropDownQuery> query = new HighLevelQuery<LinkedDropDownQuery>(LinkedDropDownQuery.ALTERNATIVES_RELATION);
//
//		List<LinkedDropDownQuery> queries = this.queryDAO.getAll(query);
//
//		if (queries == null) {
//
//			return null;
//		}
//
//		for (LinkedDropDownQuery dropDownQuery : queries) {
//
//			if (dropDownQuery.getAlternatives() != null) {
//
//				continue;
//			}
//
//			log.info("Adding dummy alternatives to query " + dropDownQuery);
//
//			dropDownQuery.setShortDescription("Select an alternative");
//
//			dropDownQuery.setAlternatives(new ArrayList<LinkedDropDownAlternative>(3));
//
//			dropDownQuery.getAlternatives().add(new LinkedDropDownAlternative("MutableAlternative 1", 0));
//			dropDownQuery.getAlternatives().add(new LinkedDropDownAlternative("MutableAlternative 2", 1));
//			dropDownQuery.getAlternatives().add(new LinkedDropDownAlternative("MutableAlternative 3", 2));
//
//			this.queryDAO.update(dropDownQuery, query);
//		}
//
//		return null;
    @WebPublic(alias = "update")
    public ForegroundModuleResponse setValue(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {
        Long entityId;
        Integer queryId;
        Integer flowInstanceID = NumberUtils.toInt(uriParser.get(4));
        MutableFlowInstanceManager instanceManager;
        if (uriParser.size() == 5
                && (queryId = NumberUtils.toInt(uriParser.get(2))) != null
                && (entityId = NumberUtils.toLong(uriParser.get(3))) != null
                ) {

            HttpSession session = req.getSession(true);
            Pattern pattern = Pattern.compile(Constants.FLOW_INSTANCE_SESSION_PREFIX + "\\d+:" + flowInstanceID);
            //Get saved instance from session
            Optional<String> flowInstanceName = Collections.list(session.getAttributeNames()).stream().filter(pattern.asPredicate()).findFirst();

            if (flowInstanceName.isPresent()) {
                instanceManager = (MutableFlowInstanceManager) session.getAttribute(flowInstanceName.get());
                List<QueryInstance> queryInstanceList = instanceManager.getQueries(QueryInstance.class);
                //get Leading query instance (LinkedDropDownQueryInstance)
                Optional<QueryInstance> queryInstance = queryInstanceList.stream().filter(new QueryIdPredicate<>(queryId)).findFirst();
                if (queryInstance.isPresent()) {
                    LinkedDropDownQueryInstance leadingQueryInstance = (LinkedDropDownQueryInstance) queryInstance.get();
                    LinkedDropDownQuery linkedDropDownQuery = leadingQueryInstance.getQuery();
                    ImmutableQueryDescriptor leadingQueryDescriptor = getQueryDescriptor(leadingQueryInstance);
                    if (leadingQueryDescriptor != null && linkedDropDownQuery != null) {
                        String leadingQueryXsdName = leadingQueryDescriptor.getXSDElementName();
                        List<JpaEntity> entityList = linkedDropDownQuery.getEntities();
                        if (!StringUtils.isEmpty(leadingQueryXsdName) && entityList != null) {
                            Optional<JpaEntity> entityOptional = entityList.stream().filter(jpaEntity -> entityId.equals(jpaEntity.getId())).findFirst();
                            if (entityOptional.isPresent()) {
                                QueryInstanceValueBinder<QueryInstance> valueBinder = new ValueBinder()
                                        .addBinder(new TextAreaQueryInstanceValueBinder(entityOptional.get(), leadingQueryXsdName))
                                        .addBinder(new TextFieldQueryInstanceValueBinder(entityOptional.get(), leadingQueryXsdName))
                                        .addBinder(new LabelFieldQueryInstanceValueBinder(entityOptional.get(), leadingQueryXsdName));

                                queryInstanceList.stream().forEach(valueBinder::bindValues);

                                ObjectMapper mapper = new ObjectMapper();
                                String objString = mapper.writeValueAsString(entityOptional.get());
                                return new SimpleForegroundModuleResponse(objString);

                            }
                        }
                    }
                }
            }

        }
        return new SimpleForegroundModuleResponse("{}");
    }

    private static ImmutableQueryDescriptor getQueryDescriptor(QueryInstance queryInstance) {
        ImmutableQueryDescriptor queryDescriptor = null;
        MutableQueryInstanceDescriptor queryInstanceDescriptor = queryInstance.getQueryInstanceDescriptor();

        if (queryInstanceDescriptor != null) {
            queryDescriptor = queryInstanceDescriptor.getQueryDescriptor();
        }

        return queryDescriptor;
    }


    private static class QueryIdPredicate<T> implements Predicate<QueryInstance> {
        private final T id;

        public QueryIdPredicate(T id) {
            this.id = id;
        }

        @Override
        public boolean test(QueryInstance queryInstance) {
            ImmutableQueryDescriptor queryDescriptor = getQueryDescriptor(queryInstance);

            return queryDescriptor != null && id.equals(queryDescriptor.getQueryID());

        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//    private static class ValueApplyer implements Consumer<DependentFieldValue> {
//        //        private final Object valueSupplier;
//        private final DirectFieldAccessor accessor;
//
//        private ValueApplyer(Object valueSupplier) {
////            this.valueSupplier = valueSupplier;
//            accessor = new DirectFieldAccessor(valueSupplier);
//        }
//
//        @Override
//        public void accept(DependentFieldValue dependentFieldValue) {
//            try {
//                Object value = accessor.getPropertyValue(dependentFieldValue.getDependencyFieldName());
//                if (Objects.nonNull(value)) {
//                    dependentFieldValue.setValue(value.toString());
//                }
//            } catch (Exception e) {
//                //todo log
//            }
//        }
//    }

//    private static class DependencyPredicate implements Predicate<DependentField> {
//        private final String masterQuaryName;
//
//        public DependencyPredicate(String masterQuaryName) {
//            this.masterQuaryName = masterQuaryName;
//        }
//
//        @Override
//        public boolean test(DependentField dependentField) {
//            return masterQuaryName.equals(dependentField.getDependencySourceName());
//        }
//    }
//    protected MutableFlowInstanceManager getSavedMutableFlowInstanceManager(int flowID, int flowInstanceID, FlowInstanceAccessController callback, HttpSession session, User user, URIParser uriParser, HttpServletRequest req, boolean loadFromDBIfNeeded, boolean checkPublishDate, boolean checkEnabled) throws FlowNoLongerAvailableException, SQLException, FlowInstanceNoLongerAvailableException, AccessDeniedException, FlowNotPublishedException, FlowDisabledException, DuplicateFlowInstanceManagerIDException, MissingQueryInstanceDescriptor, QueryProviderNotFoundException, InvalidFlowInstanceStepException, QueryProviderErrorException, QueryInstanceNotFoundInQueryProviderException, FlowDisabledException, EvaluationProviderNotFoundException, EvaluationProviderErrorException, EvaluatorNotFoundInEvaluationProviderException {
//
//        if (session == null) {
//
//            throw new RuntimeException("Session cannot be null");
//        }
//
//        synchronized (session) {
//
//            //TODO check if the status has changed since this instance was opened!
//
//            // Check if the user already has an instance of this flow open in
//            // his session
//            MutableFlowInstanceManager instanceManager = getMutableFlowInstanceManagerFromSession(flowID, flowInstanceID, session);
//
//            if (instanceManager != null) {
//
//                checkFlow(instanceManager, session, checkPublishDate, checkEnabled);
//
//                FlowInstance dbFlowInstance;
//
//                // Check if the flow instance still exists in DB
//                if ((dbFlowInstance = this.getFlowInstance(instanceManager.getFlowInstanceID(), null, FlowInstance.FLOW_RELATION, FlowInstance.FLOW_STATE_RELATION, Flow.FLOW_TYPE_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION)) == null) {
//
//                    this.removeMutableFlowInstanceManagerFromSession(instanceManager, session);
//
//                    throw new FlowInstanceNoLongerAvailableException(instanceManager.getFlowInstance());
//                }
//
//                // Check the status of flow instance if it is still user mutable etc...
//                try {
//                    callback.checkFlowInstanceAccess(dbFlowInstance, user);
//
//                } catch (AccessDeniedException e) {
//
//                    this.removeMutableFlowInstanceManagerFromSession(instanceManager, session);
//                    throw e;
//                }
//
//                if (dbFlowInstance.getUpdated() != null && (instanceManager.getFlowInstance().getUpdated() == null || instanceManager.getFlowInstance().getUpdated().before(dbFlowInstance.getUpdated()))) {
//
//                    instanceManager.setConcurrentModificationLock(true);
//                }
//
//                if (log.isDebugEnabled()) {
//
//                    log.debug("Found flow instance " + instanceManager.getFlowInstance() + " in session of user " + user);
//                }
//
//                return instanceManager;
//
//            } else if (!loadFromDBIfNeeded) {
//
//                return null;
//            }
//
//            // User does not have the requested flow instance open, get flow instance from DB and create a new instance manager
//            FlowInstance flowInstance = getFlowInstance(flowInstanceID);
//
//            if (flowInstance == null) {
//
//                return null;
//            }
//
//            callback.checkFlowInstanceAccess(flowInstance, user);
//
//            if (checkEnabled && (!flowInstance.getFlow().isEnabled() || isOperatingStatusDisabled(flowInstance.getFlow()))) {
//
//                throw new FlowDisabledException(flowInstance.getFlow());
//            }
//
//            if (checkPublishDate && !flowInstance.getFlow().isPublished()) {
//
//                throw new FlowNotPublishedException(flowInstance.getFlow());
//            }
//
//            log.info("Opening copy of flow instance " + flowInstance + " for user " + user);
//
//            InstanceMetadata instanceMetadata = new DefaultInstanceMetadata(getCurrentSiteProfile(req, user, uriParser));
//
//            // TODO handle IllegalStateException's from session object
//            instanceManager = new MutableFlowInstanceManager(flowInstance, queryHandler, evaluationHandler, getNewInstanceManagerID(user), req, user, instanceMetadata);
//
//            // TODO handle IllegalStateException's from session object
//            addMutableFlowInstanceManagerToSession(flowID, flowInstanceID, instanceManager, session);
//
//            return instanceManager;
//        }
//    }
//
//    private static String getNewInstanceManagerID(User user) {
//
//        if (user != null && user.getUserID() != null) {
//
//            return "userid-" + user.getUserID() + "-uuid-" + UUID.randomUUID();
//        }
//
//        return "anonymous-uuid-" + UUID.randomUUID();
//    }


    @WebPublic(alias = "config")
    public ForegroundModuleResponse configureQuery(HttpServletRequest req, HttpServletResponse res, User
            user, URIParser uriParser) throws Exception {

        return this.queryCRUD.update(req, res, user, uriParser);
    }

    @Override
    public boolean deleteQuery(ImmutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws
            SQLException {

        LinkedDropDownQuery query = getQuery(descriptor.getQueryID());

        if (query == null) {

            return false;
        }

        this.queryDAO.delete(query, transactionHandler);

        return true;
    }

    @Override
    public boolean deleteQueryInstance(ImmutableQueryInstanceDescriptor descriptor, TransactionHandler
            transactionHandler) throws Throwable {

        LinkedDropDownQueryInstance queryInstance = this.getQueryInstance(descriptor.getQueryInstanceID());

        if (queryInstance == null) {

            return false;
        }

        this.queryInstanceDAO.delete(queryInstance, transactionHandler);

        return true;
    }

//	}

    @Override
    public String getTitlePrefix() {

        return this.moduleDescriptor.getName();
    }

    @Override
    public void copyQuery(MutableQueryDescriptor sourceQueryDescriptor, MutableQueryDescriptor
            copyQueryDescriptor, TransactionHandler transactionHandler) throws SQLException {

        LinkedDropDownQuery query = getQuery(sourceQueryDescriptor.getQueryID(), transactionHandler);

        query.setQueryID(copyQueryDescriptor.getQueryID());

//		if(query.getAlternatives() != null){
//
//			for(LinkedDropDownAlternative alternative : query.getAlternatives()){
//
//				alternative.setAlternativeID(null);
//			}
//		}

//        this.queryDAO.add(query, transactionHandler, new RelationQuery(LinkedDropDownQuery.ALTERNATIVES_RELATION));
        this.queryDAO.add(query, transactionHandler, null);
    }

    @Override
    public List<LinkedDropDownQueryInstance> getQueryInstances(LinkedDropDownQuery
                                                                       dropDownQuery, List<Integer> queryInstanceIDs) throws SQLException {

        HighLevelQuery<LinkedDropDownQueryInstance> query = new HighLevelQuery<LinkedDropDownQueryInstance>();

//		query.addRelation(LinkedDropDownQueryInstance.ALTERNATIVE_RELATION);

        query.addParameter(queryInstanceQueryParamFactory.getParameter(dropDownQuery));
        query.addParameter(queryInstanceIDParamFactory.getWhereInParameter(queryInstanceIDs));

        return this.queryInstanceDAO.getAll(query);
    }

    //todo пофиксить этот вызов метода
    @Override
    public Matrix<String> getSummaryTable(LinkedDropDownQuery query, List<Integer> queryInstanceIDs) throws
            SQLException {

//		if(query.getAlternatives() == null){
//
//			return null;
//		}
//
//		List<LinkedDropDownQueryInstance> instances;
//
//		if(queryInstanceIDs != null){
//
//			instances = getQueryInstances(query, queryInstanceIDs);
//
//		}else{
//
//			instances = null;
//		}
//
//		Matrix<String> table = new Matrix<String>(query.getFreeTextAlternative() != null ? query.getAlternatives().size() + 2 : query.getAlternatives().size() + 1, 2);
//
//		table.setCell(0, 0, alternativesText);
//		table.setCell(0, 1, countText);
//
//		int currentRow = 1;
//
//		for(DropDownAlternative alternative : query.getAlternatives()){
//
//			table.setCell(currentRow, 0, alternative.getName());
//
//			int selectionCount = 0;
//
//			if(instances != null){
//
//				for(LinkedDropDownQueryInstance instance : instances){
//
//					if(instance.getAlternative() != null && instance.getAlternative().equals(alternative)){
//
//						selectionCount++;
//					}
//				}
//			}
//
//			table.setCell(currentRow, 1, String.valueOf(selectionCount));
//
//			currentRow++;
//		}
//
//		if(query.getFreeTextAlternative() != null){
//
//			table.setCell(currentRow, 0, query.getFreeTextAlternative());
//
//			int selectionCount = 0;
//
//			if(instances != null){
//
//				for(LinkedDropDownQueryInstance instance : instances){
//
//					if(instance.getEntityClassname() != null){
//
//						selectionCount++;
//					}
//				}
//			}
//
//			table.setCell(currentRow, 1, String.valueOf(selectionCount));
//		}
//
//		return table;
        throw new UnsupportedOperationException();
    }

    @Override
    protected void appendPDFData(Document doc, Element showQueryValuesElement, LinkedDropDownQueryInstance
            queryInstance) {

        super.appendPDFData(doc, showQueryValuesElement, queryInstance);

        if (queryInstance.getQuery().getDescription() != null) {

            XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "Description", JTidyUtils.getXHTML(queryInstance.getQuery().getDescription()));
            XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "isHTMLDescription", queryInstance.getQuery().getDescription().contains("<") && queryInstance.getQuery().getDescription().contains(">"));
        }
    }

    public static void main(String[] args) {
        Path objFile = Paths.get("/home/vitaly/Загрузки/obj.dat");
        if (!Files.exists(objFile)) {
            System.out.println("sadfasd");
            IvisServiceFactory serviceFactory = ApplicationPopulator.remoteIvisServiceFactory();
            PupilService service = serviceFactory.getService(PupilService.class);
            Object object = service.find(22L);
            ApplicationPopulator.saveObjectToFile(object, objFile.toString());
        }
        Object obj = ApplicationPopulator.loadObjectFromFile(objFile.toString());

        Object value = getPropertyValue(obj);

        System.out.println(value);
    }

    private static Object getPropertyValue(Object obj) {
        PropertyAccessor accessor = new BeanWrapperImpl(obj);
        Object value = null;

        try {
            value = accessor.getPropertyValue("person.addresses[REGISTERED].street");
        } catch (BeansException ignore) {
            ignore.printStackTrace();
        } //There is no appropriate value

        return value;
    }
}

