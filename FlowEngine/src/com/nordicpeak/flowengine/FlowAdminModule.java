package com.nordicpeak.flowengine;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import se.unlogic.fileuploadutils.MultipartRequest;
import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.InstanceManagerDependency;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.UserMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.EventHandler;
import se.unlogic.hierarchy.core.interfaces.EventListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.AdvancedCRUDCallback;
import se.unlogic.hierarchy.core.utils.GenericCRUD;
import se.unlogic.hierarchy.core.utils.ViewFragmentUtils;
import se.unlogic.hierarchy.core.utils.usergrouplist.UserGroupListConnector;
import se.unlogic.hierarchy.core.validationerrors.FileSizeLimitExceededValidationError;
import se.unlogic.hierarchy.core.validationerrors.InvalidFileExtensionValidationError;
import se.unlogic.hierarchy.core.validationerrors.RequestSizeLimitExceededValidationError;
import se.unlogic.hierarchy.core.validationerrors.UnableToParseFileValidationError;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfileHandler;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.crypto.Base64;
import se.unlogic.standardutils.dao.AdvancedAnnotatedDAOWrapper;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.AnnotatedDAOWrapper;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.LowLevelQuery;
import se.unlogic.standardutils.dao.OrderByCriteria;
import se.unlogic.standardutils.dao.QueryOperators;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.image.ImageUtils;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.serialization.SerializationUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLGeneratorDocument;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.SessionUtils;
import se.unlogic.webutils.http.URIParser;

import com.nordicpeak.flowengine.accesscontrollers.AdminUserFlowInstanceAccessController;
import com.nordicpeak.flowengine.beans.Category;
import com.nordicpeak.flowengine.beans.DefaultStatusMapping;
import com.nordicpeak.flowengine.beans.EvaluatorDescriptor;
import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowAction;
import com.nordicpeak.flowengine.beans.FlowFamily;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.beans.FlowType;
import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.beans.StandardStatus;
import com.nordicpeak.flowengine.beans.Status;
import com.nordicpeak.flowengine.beans.Step;
import com.nordicpeak.flowengine.comparators.FlowVersionComparator;
import com.nordicpeak.flowengine.comparators.QueryDescriptorSortIndexComparator;
import com.nordicpeak.flowengine.comparators.StepSortIndexComparator;
import com.nordicpeak.flowengine.cruds.CategoryCRUD;
import com.nordicpeak.flowengine.cruds.EvaluatorDescriptorCRUD;
import com.nordicpeak.flowengine.cruds.FlowCRUD;
import com.nordicpeak.flowengine.cruds.FlowFamilyCRUD;
import com.nordicpeak.flowengine.cruds.FlowTypeCRUD;
import com.nordicpeak.flowengine.cruds.QueryDescriptorCRUD;
import com.nordicpeak.flowengine.cruds.StandardStatusCRUD;
import com.nordicpeak.flowengine.cruds.StatusCRUD;
import com.nordicpeak.flowengine.cruds.StepCRUD;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.enums.ShowMode;
import com.nordicpeak.flowengine.exceptions.FlowEngineException;
import com.nordicpeak.flowengine.exceptions.evaluation.EvaluationException;
import com.nordicpeak.flowengine.exceptions.evaluationprovider.EvaluationProviderException;
import com.nordicpeak.flowengine.exceptions.flow.FlowDefaultStatusNotFound;
import com.nordicpeak.flowengine.exceptions.flow.FlowNoLongerAvailableException;
import com.nordicpeak.flowengine.exceptions.flowinstance.InvalidFlowInstanceStepException;
import com.nordicpeak.flowengine.exceptions.flowinstance.MissingQueryInstanceDescriptor;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.DuplicateFlowInstanceManagerIDException;
import com.nordicpeak.flowengine.exceptions.flowinstancemanager.FlowInstanceManagerClosedException;
import com.nordicpeak.flowengine.exceptions.queryinstance.QueryRequestException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToGetQueryInstanceShowHTMLException;
import com.nordicpeak.flowengine.exceptions.queryinstance.UnableToSaveQueryInstanceException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryNotFoundInQueryProviderException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderErrorException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderException;
import com.nordicpeak.flowengine.exceptions.queryprovider.QueryProviderNotFoundException;
import com.nordicpeak.flowengine.interfaces.EvaluationHandler;
import com.nordicpeak.flowengine.interfaces.FlowInstanceAccessController;
import com.nordicpeak.flowengine.interfaces.FlowNotificationHandler;
import com.nordicpeak.flowengine.interfaces.FlowProcessCallback;
import com.nordicpeak.flowengine.interfaces.ImmutableFlow;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstance;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.listeners.EvaluatorDescriptorElementableListener;
import com.nordicpeak.flowengine.listeners.QueryDescriptorElementableListener;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager.FlowInstanceManagerRegistery;
import com.nordicpeak.flowengine.validationerrors.EvaluatorImportValidationError;
import com.nordicpeak.flowengine.validationerrors.EvaluatorTypeNotFoundValidationError;
import com.nordicpeak.flowengine.validationerrors.NoQueryDescriptorSortindexValidationError;
import com.nordicpeak.flowengine.validationerrors.NoStepSortindexValidationError;
import com.nordicpeak.flowengine.validationerrors.QueryImportValidationError;
import com.nordicpeak.flowengine.validationerrors.QueryTypeNotAllowedInFlowTypeValidationError;
import com.nordicpeak.flowengine.validationerrors.QueryTypeNotFoundValidationError;

public class FlowAdminModule extends BaseFlowBrowserModule implements EventListener<CRUDEvent<?>>, AdvancedCRUDCallback<User>, AccessInterface, FlowProcessCallback {

	@SuppressWarnings("rawtypes")
	private static final Class[] EVENT_LISTENER_CLASSES = new Class[] { FlowFamily.class, FlowType.class, Flow.class, Category.class, Step.class, QueryDescriptor.class, EvaluatorDescriptor.class, Status.class, FlowInstance.class };

	protected static final RelationQuery ADD_NEW_FLOW_AND_FAMILY_RELATION_QUERY = new RelationQuery(Flow.STATUSES_RELATION, Flow.DEFAULT_FLOW_STATE_MAPPINGS_RELATION, Flow.STEPS_RELATION, Flow.FLOW_FAMILY_RELATION, Step.QUERY_DESCRIPTORS_RELATION, QueryDescriptor.EVALUATOR_DESCRIPTORS_RELATION, Flow.CHECKS_RELATION, Flow.TAGS_RELATION);
	protected static final RelationQuery ADD_NEW_FLOW_VERSION_RELATION_QUERY = new RelationQuery(Flow.STATUSES_RELATION, Flow.DEFAULT_FLOW_STATE_MAPPINGS_RELATION, Flow.STEPS_RELATION, Step.QUERY_DESCRIPTORS_RELATION, QueryDescriptor.EVALUATOR_DESCRIPTORS_RELATION, Flow.CHECKS_RELATION, Flow.TAGS_RELATION);

	private static final StepSortIndexComparator STEP_COMPARATOR = new StepSortIndexComparator();
	private static final QueryDescriptorSortIndexComparator QUERY_DESCRIPTOR_COMPARATOR = new QueryDescriptorSortIndexComparator();
	private static final FlowVersionComparator FLOW_VERSION_COMPARATOR = new FlowVersionComparator();

	private static final AdminUserFlowInstanceAccessController UPDATE_ACCESS_CONTROLLER = new AdminUserFlowInstanceAccessController(true);
	private static final AdminUserFlowInstanceAccessController PREVIEW_ACCESS_CONTROLLER = new AdminUserFlowInstanceAccessController(false);

	@XSLVariable(prefix = "java.")
	private String flowNameCopySuffix = " (copy)";

	private FlowFamilyCRUD flowFamilyCRUD;
	private FlowCRUD flowCRUD;
	private StepCRUD stepCRUD;
	private QueryDescriptorCRUD queryDescriptorCRUD;
	private EvaluatorDescriptorCRUD evaluatorDescriptorCRUD;
	private StatusCRUD statusCRUD;
	private StandardStatusCRUD standardStatusCRUD;
	private FlowTypeCRUD flowTypeCRUD;
	private CategoryCRUD categoryCRUD;

	protected QueryParameterFactory<Flow, FlowFamily> flowFlowFamilyParamFactory;
	protected QueryParameterFactory<Flow, Integer> flowVersionParamFactory;

	protected QueryParameterFactory<FlowInstance, Flow> flowInstanceFlowParamFactory;
	protected QueryParameterFactory<FlowInstance, Status> flowInstanceStatusParamFactory;

	protected QueryParameterFactory<FlowAction, Boolean> flowActionRequiredParamFactory;
	protected QueryParameterFactory<FlowAction, String> flowActionIDParamFactory;

	protected QueryParameterFactory<QueryDescriptor, String> queryDescriptorQueryTypeIDParamFactory;
	protected QueryParameterFactory<EvaluatorDescriptor, String> evaluatorDescriptorEvaluatorTypeIDParamFactory;

	protected OrderByCriteria<Flow> flowVersionOrderByCriteria;

	private LinkedHashMap<Integer, FlowType> flowTypeCacheMap;
	private LinkedHashMap<Integer, Flow> flowCacheMap;
	private HashMap<Integer, FlowFamily> flowFamilyCacheMap;

	@ModuleSetting(allowsNull = true)
	@GroupMultiListSettingDescriptor(name = "Admin groups", description = "Groups allowed to administrate global parts of this module such as standard statuses")
	protected List<Integer> adminGroupIDs;

	@ModuleSetting(allowsNull = true)
	@UserMultiListSettingDescriptor(name = "Admin users", description = "Users allowed to administrate global parts of this module such as standard statuses")
	protected List<Integer> adminUserIDs;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max icon width", description = "Max allowed flow icon width.")
	private int maxFlowIconWidth = 100;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Max icon height", description = "Max allowed flow icon height.")
	private int maxFlowIconHeight = 100;

	@InstanceManagerDependency(required=true)
	protected SiteProfileHandler siteProfileHandler;

	@InstanceManagerDependency
	protected FlowNotificationHandler notificationHandler;

	protected UserGroupListConnector userGroupListConnector;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.userGroupListConnector = new UserGroupListConnector(systemInterface);

		if (!systemInterface.getInstanceHandler().addInstance(FlowAdminModule.class, this)) {

			throw new RuntimeException("Unable to register module in global instance handler using key " + FlowAdminModule.class.getSimpleName() + ", another instance is already registered using this key.");
		}
	}

	@Override
	public void unload() throws Exception {

		eventHandler.removeEventListener(CRUDEvent.class, this, EVENT_LISTENER_CLASSES);

		if (this.equals(systemInterface.getInstanceHandler().getInstance(FlowAdminModule.class))) {

			systemInterface.getInstanceHandler().removeInstance(FlowAdminModule.class);
		}

		super.unload();
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);

		flowFlowFamilyParamFactory = daoFactory.getFlowDAO().getParamFactory("flowFamily", FlowFamily.class);
		flowVersionParamFactory = daoFactory.getFlowDAO().getParamFactory("version", Integer.class);

		flowInstanceFlowParamFactory = daoFactory.getFlowInstanceDAO().getParamFactory("flow", Flow.class);
		flowInstanceStatusParamFactory = daoFactory.getFlowInstanceDAO().getParamFactory("status", Status.class);

		flowActionRequiredParamFactory = daoFactory.getFlowActionDAO().getParamFactory("required", boolean.class);
		flowActionIDParamFactory = daoFactory.getFlowActionDAO().getParamFactory("actionID", String.class);

		queryDescriptorQueryTypeIDParamFactory = daoFactory.getQueryDescriptorDAO().getParamFactory("queryTypeID", String.class);
		evaluatorDescriptorEvaluatorTypeIDParamFactory = daoFactory.getEvaluatorDescriptorDAO().getParamFactory("evaluatorTypeID", String.class);

		AnnotatedDAOWrapper<FlowFamily, Integer> flowFamilyDAOWrapper = daoFactory.getFlowFamilyDAO().getWrapper("flowFamilyID", Integer.class);
		flowFamilyDAOWrapper.addRelations(FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION);
		flowFamilyDAOWrapper.setUseRelationsOnGet(true);
		this.flowFamilyCRUD = new FlowFamilyCRUD(flowFamilyDAOWrapper, this);

		AdvancedAnnotatedDAOWrapper<Flow, Integer> flowDAOWrapper = daoFactory.getFlowDAO().getAdvancedWrapper("flowID", Integer.class);
		flowDAOWrapper.getAddQuery().addRelations(Flow.FLOW_FAMILY_RELATION, Flow.STATUSES_RELATION, Status.DEFAULT_STATUS_MAPPINGS_RELATION, Flow.TAGS_RELATION, Flow.CHECKS_RELATION);
		flowDAOWrapper.getUpdateQuery().addRelations(Flow.FLOW_FAMILY_RELATION, Flow.TAGS_RELATION, Flow.CHECKS_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION);

		this.flowCRUD = new FlowCRUD(flowDAOWrapper, this);

		AnnotatedDAOWrapper<QueryDescriptor, Integer> queryDescriptorDAOWrapper = daoFactory.getQueryDescriptorDAO().getWrapper(Integer.class);
		queryDescriptorDAOWrapper.addRelations(QueryDescriptor.STEP_RELATION, Step.FLOW_RELATION, Flow.FLOW_TYPE_RELATION, Flow.CATEGORY_RELATION, FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION, QueryDescriptor.EVALUATOR_DESCRIPTORS_RELATION);
		queryDescriptorDAOWrapper.setUseRelationsOnGet(true);

		this.queryDescriptorCRUD = new QueryDescriptorCRUD(queryDescriptorDAOWrapper, this);

		AnnotatedDAOWrapper<EvaluatorDescriptor, Integer> evaluatorDescriptorDAOWrapper = daoFactory.getEvaluatorDescriptorDAO().getWrapper(Integer.class);
		evaluatorDescriptorDAOWrapper.addRelations(EvaluatorDescriptor.QUERY_DESCRIPTOR_RELATION, QueryDescriptor.STEP_RELATION, Step.FLOW_RELATION, Flow.FLOW_TYPE_RELATION, Flow.CATEGORY_RELATION, FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION);
		evaluatorDescriptorDAOWrapper.setUseRelationsOnGet(true);

		this.evaluatorDescriptorCRUD = new EvaluatorDescriptorCRUD(evaluatorDescriptorDAOWrapper, this);

		AnnotatedDAOWrapper<Step, Integer> stepDAOWrapper = daoFactory.getStepDAO().getWrapper(Integer.class);
		stepDAOWrapper.addRelations(Step.FLOW_RELATION, Flow.FLOW_TYPE_RELATION, Flow.CATEGORY_RELATION, FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION);
		stepDAOWrapper.setUseRelationsOnGet(true);

		stepCRUD = new StepCRUD(stepDAOWrapper, this);

		AnnotatedDAOWrapper<Status, Integer> statusDAOWrapper = daoFactory.getStatusDAO().getWrapper("statusID", Integer.class);
		statusDAOWrapper.addRelations(Status.DEFAULT_STATUS_MAPPINGS_RELATION, Status.FLOW_RELATION, Flow.FLOW_TYPE_RELATION, FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION);
		statusDAOWrapper.setUseRelationsOnGet(true);

		statusCRUD = new StatusCRUD(statusDAOWrapper, this);

		AnnotatedDAOWrapper<StandardStatus, Integer> standardStatusDAOWrapper = daoFactory.getStandardStatusDAO().getWrapper("statusID", Integer.class);
		standardStatusDAOWrapper.addRelations(StandardStatus.DEFAULT_STANDARD_STATUS_MAPPINGS_RELATION);
		standardStatusDAOWrapper.setUseRelationsOnGet(true);

		standardStatusCRUD = new StandardStatusCRUD(standardStatusDAOWrapper, this);

		AnnotatedDAOWrapper<FlowType, Integer> flowTypeDAOWrapper = daoFactory.getFlowTypeDAO().getWrapper("flowTypeID", Integer.class);
		flowTypeDAOWrapper.addRelations(FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_QUERIES_RELATION, FlowType.ALLOWED_USERS_RELATION);
		flowTypeDAOWrapper.setUseRelationsOnAdd(true);
		flowTypeDAOWrapper.setUseRelationsOnUpdate(true);

		this.flowTypeCRUD = new FlowTypeCRUD(flowTypeDAOWrapper, this);

		AnnotatedDAOWrapper<Category, Integer> categoryDAOWrapper = daoFactory.getCategoryDAO().getWrapper("categoryID", Integer.class);
		categoryDAOWrapper.addRelations(Category.FLOW_TYPE_RELATION);
		categoryDAOWrapper.setUseRelationsOnGet(true);

		this.categoryCRUD = new CategoryCRUD(categoryDAOWrapper, this);

		flowVersionOrderByCriteria = daoFactory.getFlowDAO().getOrderByCriteria("version", Order.DESC);

		cacheFlows();
		cacheFlowTypes();

		eventHandler.addEventListener(CRUDEvent.class, this, EVENT_LISTENER_CLASSES);
	}

	protected synchronized void cacheFlowTypes() throws SQLException {

		long startTime = System.currentTimeMillis();

		List<FlowType> flowTypes = daoFactory.getFlowTypeDAO().getAll(new HighLevelQuery<FlowType>(FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION, FlowType.ALLOWED_QUERIES_RELATION, FlowType.CATEGORIES_RELATION));

		if (flowTypes == null) {

			flowTypeCacheMap = new LinkedHashMap<Integer, FlowType>(0);

		} else {

			LinkedHashMap<Integer, FlowType> tempFlowTypeMap = new LinkedHashMap<Integer, FlowType>(flowTypes.size());

			for (FlowType flowType : flowTypes) {

				tempFlowTypeMap.put(flowType.getFlowTypeID(), flowType);
			}

			flowTypeCacheMap = tempFlowTypeMap;
		}

		log.info("Cached " + CollectionUtils.getSize(flowTypes) + " flow types in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - startTime) + " ms");
	}

	public synchronized void cacheFlows() throws SQLException {

		TransactionHandler transactionHandler = null;

		try {

			transactionHandler = new TransactionHandler(dataSource);

			long startTime = System.currentTimeMillis();

			HighLevelQuery<Flow> query = new HighLevelQuery<Flow>(Flow.FLOW_TYPE_RELATION, FlowType.CATEGORIES_RELATION, Flow.CATEGORY_RELATION, Flow.STEPS_RELATION, Flow.STATUSES_RELATION, Step.QUERY_DESCRIPTORS_RELATION, QueryDescriptor.EVALUATOR_DESCRIPTORS_RELATION, Flow.DEFAULT_FLOW_STATE_MAPPINGS_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_USERS_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, DefaultStatusMapping.FLOW_STATE_RELATION, FlowType.ALLOWED_GROUPS_RELATION, FlowType.ALLOWED_USERS_RELATION, FlowType.ALLOWED_QUERIES_RELATION, Flow.TAGS_RELATION, Flow.CHECKS_RELATION);

			List<Flow> flows = daoFactory.getFlowDAO().getAll(query, transactionHandler);

			if (flows == null) {

				flowCacheMap = new LinkedHashMap<Integer, Flow>(0);
				flowFamilyCacheMap = new HashMap<Integer, FlowFamily>();

			} else {

				LinkedHashMap<Integer, Flow> tempFlowCacheMap = new LinkedHashMap<Integer, Flow>(flows.size());
				HashMap<Integer, FlowFamily> tempFlowFamilyMap = new HashMap<Integer, FlowFamily>();

				for (Flow flow : flows) {

					flow.setFlowInstanceCount(getFlowInstanceCount(flow, transactionHandler));
					flow.setLatestVersion(isLatestVersion(flow, transactionHandler));

					setStatusFlowInstanceCount(flow, transactionHandler);

					tempFlowCacheMap.put(flow.getFlowID(), flow);
					tempFlowFamilyMap.put(flow.getFlowFamily().getFlowFamilyID(), flow.getFlowFamily());
				}

				flowCacheMap = tempFlowCacheMap;
				flowFamilyCacheMap = tempFlowFamilyMap;
			}

			log.info("Cached " + flowCacheMap.size() + " flows from " + flowFamilyCacheMap.size() + " flow families in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - startTime) + " ms");

		} finally {

			TransactionHandler.autoClose(transactionHandler);
		}
	}

	private void setStatusFlowInstanceCount(Flow flow, TransactionHandler transactionHandler) throws SQLException {

		if (flow.getStatuses() != null) {

			for (Status status : flow.getStatuses()) {

				status.setFlowInstanceCount(getFlowInstanceCount(status, transactionHandler));
			}
		}
	}

	private Boolean isLatestVersion(Flow flow, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<Flow> query = new HighLevelQuery<Flow>();

		query.addParameter(flowFlowFamilyParamFactory.getParameter(flow.getFlowFamily()));
		query.addParameter(flowVersionParamFactory.getParameter(flow.getVersion(), QueryOperators.BIGGER_THAN));

		return !daoFactory.getFlowDAO().getBoolean(query, transactionHandler);
	}

	public Integer getFlowInstanceCount(Flow flow, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>();

		query.addParameter(flowInstanceFlowParamFactory.getParameter(flow));

		return daoFactory.getFlowInstanceDAO().getCount(query, transactionHandler);
	}

	public Integer getFlowInstanceCount(Flow flow) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>();

		query.addParameter(flowInstanceFlowParamFactory.getParameter(flow));

		return daoFactory.getFlowInstanceDAO().getCount(query);
	}

	public Integer getFlowInstanceCount(Status status) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>();

		query.addParameter(flowInstanceStatusParamFactory.getParameter(status));

		return daoFactory.getFlowInstanceDAO().getCount(query);
	}

	public Integer getFlowInstanceCount(Status status, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>();

		query.addParameter(flowInstanceStatusParamFactory.getParameter(status));

		return daoFactory.getFlowInstanceDAO().getCount(query, transactionHandler);
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return list(req, res, user, uriParser, (List<ValidationError>)null);
	}

	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws ModuleConfigurationException, SQLException {

		return list(req, res, user, uriParser, CollectionUtils.getGenericSingletonList(validationError));
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws ModuleConfigurationException, SQLException {

		log.info("User " + user + " listing flows");

		Document doc = this.createDocument(req, uriParser, user);

		Element listFlowsElement = doc.createElement("ListFlows");

		doc.getDocumentElement().appendChild(listFlowsElement);

		if (hasFlowTypeAccess(user)) {

			listFlowsElement.appendChild(doc.createElement("AddAccess"));

			if (!AccessUtils.checkAccess(user, this)) {

				//Check access and append flows
				for (Flow flow : this.flowCacheMap.values()) {

					if (AccessUtils.checkAccess(user, flow.getFlowType())) {

						//TODO add only a limited XML here
						listFlowsElement.appendChild(flow.toXML(doc));
					}
				}

			} else {

				XMLUtils.append(doc, listFlowsElement, this.flowCacheMap.values());
			}
		}

		if (AccessUtils.checkAccess(user, this)) {
			XMLUtils.appendNewElement(doc, listFlowsElement, "AdminAccess");
		}

		if (validationErrors != null) {

			XMLUtils.append(doc, listFlowsElement, validationErrors);
		}

		return new SimpleForegroundModuleResponse(doc, this.getDefaultBreadcrumb());
	}

	public boolean hasFlowTypeAccess(User user) {

		//Check if the user has access to any flow type
		for (FlowType flowType : this.flowTypeCacheMap.values()) {

			if (AccessUtils.checkAccess(user, flowType)) {

				return true;
			}
		}

		return false;
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse showFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowCRUD.show(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public synchronized ForegroundModuleResponse deleteFlowFamily(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		FlowFamily flowFamily;

		if (uriParser.size() != 3 || !NumberUtils.isInt(uriParser.get(2)) || (flowFamily = flowFamilyCacheMap.get(NumberUtils.toInt(uriParser.get(2)))) == null) {

			return list(req, res, user, uriParser, new ValidationError("RequestedFlowFamilyNotFound"));
		}

		List<Flow> flows = getFlowVersions(flowFamily);

		for (Flow flow : flows) {

			if ((flow.isPublished() && flow.isEnabled()) || flow.getFlowInstanceCount() > 0) {

				return list(req, res, user, uriParser, new ValidationError("FlowFamilyCannotBeDeleted"));
			}
		}

		log.info("User " + user + " deleting flow family " + flowFamily.getFlowFamilyID() + " with " + flows.size() + " flows");

		TransactionHandler transactionHandler = null;

		try{
			transactionHandler = daoFactory.getTransactionHandler();

			for(Flow flow : flows){

				if (flow.getSteps() != null) {

					for (Step step : flow.getSteps()) {

						if (step.getQueryDescriptors() != null) {

							for (QueryDescriptor queryDescriptor : step.getQueryDescriptors()) {

								if (queryDescriptor.getEvaluatorDescriptors() != null) {

									for (EvaluatorDescriptor evaluatorDescriptor : queryDescriptor.getEvaluatorDescriptors()) {

										getEvaluationHandler().deleteEvaluator(evaluatorDescriptor, transactionHandler);
									}
								}

								getQueryHandler().deleteQuery(queryDescriptor, transactionHandler);
							}
						}
					}
				}
			}

			daoFactory.getFlowFamilyDAO().delete(flowFamily);

			transactionHandler.commit();

		}finally{

			TransactionHandler.autoClose(transactionHandler);
		}

		if (!flows.isEmpty()) {

			eventHandler.sendEvent(Flow.class, new CRUDEvent<Flow>(Flow.class, CRUDAction.DELETE, flows), EventTarget.ALL);
		}

		eventHandler.sendEvent(FlowFamily.class, new CRUDEvent<FlowFamily>(CRUDAction.DELETE, flowFamily), EventTarget.ALL);

		redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic(toLowerCase = true)
	public synchronized ForegroundModuleResponse copyFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		Integer flowID = NumberUtils.toInt(req.getParameter("flowID"));

		Flow flow;

		if (flowID == null || (flow = this.getCachedFlow(flowID)) == null) {

			return list(req, res, user, uriParser, new ValidationError("FlowNotFound"));
		}

		Flow flowCopy = SerializationUtils.cloneSerializable(flow);

		flowCopy.setFlowID(null);
		flowCopy.setEnabled(false);
		flowCopy.setIcon(flow.getIcon());
		flowCopy.setPublishDate(null);
		flowCopy.setUnPublishDate(null);

		boolean newFamily = req.getParameter("new_family") != null;

		if (newFamily) {

			log.info("User " + user + " creating new flow based on flow " + flow);

			FlowFamily flowFamily = new FlowFamily();
			flowFamily.setVersionCount(1);
			flowCopy.setFlowFamily(flowFamily);
			flowCopy.setVersion(1);
			flowCopy.setName(flow.getName() + flowNameCopySuffix);

		} else {

			log.info("User " + user + " creating new flow version based on flow " + flow);

			Integer version = flowCopy.getFlowFamily().getVersionCount() + 1;

			flowCopy.getFlowFamily().setVersionCount(version);
			flowCopy.setVersion(version);
		}

		if (flowCopy.getDefaultFlowStateMappings() != null) {

			outer: for (DefaultStatusMapping defaultStatusMapping : flowCopy.getDefaultFlowStateMappings()) {

				for (Status status : flowCopy.getStatuses()) {

					if (status.equals(defaultStatusMapping.getStatus())) {

						defaultStatusMapping.setStatus(status);
						defaultStatusMapping.getStatus().setFlow(flowCopy);
						continue outer;
					}
				}

				throw new RuntimeException("Unable to find status " + defaultStatusMapping.getStatus() + " in statuses of flow " + flowCopy);
			}
		}

		if (flowCopy.getStatuses() != null) {

			for (Status status : flowCopy.getStatuses()) {

				status.setStatusID(null);
			}
		}

		if (flowCopy.getSteps() != null) {

			for (Step step : flowCopy.getSteps()) {

				step.setStepID(null);

				if (step.getQueryDescriptors() != null) {

					for (QueryDescriptor queryDescriptor : step.getQueryDescriptors()) {

						queryDescriptor.setQueryID(null);

						if (queryDescriptor.getEvaluatorDescriptors() != null) {

							for (EvaluatorDescriptor evaluatorDescriptor : queryDescriptor.getEvaluatorDescriptors()) {

								evaluatorDescriptor.setEvaluatorID(null);
							}
						}
					}
				}
			}
		}

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = daoFactory.getFlowDAO().createTransaction();

			if (newFamily) {

				daoFactory.getFlowDAO().add(flowCopy, transactionHandler, ADD_NEW_FLOW_AND_FAMILY_RELATION_QUERY);

			} else {

				daoFactory.getFlowFamilyDAO().update(flowCopy.getFlowFamily(), transactionHandler, null);
				daoFactory.getFlowDAO().add(flowCopy, transactionHandler, ADD_NEW_FLOW_VERSION_RELATION_QUERY);
			}

			if (flow.getSteps() != null) {

				int stepIndex = 0;

				while (stepIndex < flowCopy.getSteps().size()) {

					Step step = flow.getSteps().get(stepIndex);

					if (step.getQueryDescriptors() != null) {

						int queryIndex = 0;

						while (queryIndex < step.getQueryDescriptors().size()) {

							QueryDescriptor queryDescriptor = step.getQueryDescriptors().get(queryIndex);

							queryHandler.copyQuery(queryDescriptor, flowCopy.getSteps().get(stepIndex).getQueryDescriptors().get(queryIndex), transactionHandler);

							if (queryDescriptor.getEvaluatorDescriptors() != null) {

								Query sourceQuery = queryHandler.getQuery(queryDescriptor, transactionHandler);
								Query copyQuery = queryHandler.getQuery(flowCopy.getSteps().get(stepIndex).getQueryDescriptors().get(queryIndex), transactionHandler);

								int evaluatorIndex = 0;

								while (evaluatorIndex < queryDescriptor.getEvaluatorDescriptors().size()) {

									EvaluatorDescriptor sourceEvaluatorDescriptor = queryDescriptor.getEvaluatorDescriptors().get(evaluatorIndex);
									EvaluatorDescriptor copyEvaluatorDescriptor = flowCopy.getSteps().get(stepIndex).getQueryDescriptors().get(queryIndex).getEvaluatorDescriptors().get(evaluatorIndex);

									if (sourceEvaluatorDescriptor.getTargetQueryIDs() != null) {

										copyEvaluatorDescriptor.getTargetQueryIDs().clear();

										for (Integer queryID : sourceEvaluatorDescriptor.getTargetQueryIDs()) {

											int nestedStepIndex = stepIndex;

											nestedStepLoop: while (nestedStepIndex < flow.getSteps().size()) {

												Step nestedStep = flow.getSteps().get(nestedStepIndex);

												if (nestedStep.getQueryDescriptors() != null) {

													int nestedQueryIndex = 0;

													while (nestedQueryIndex < nestedStep.getQueryDescriptors().size()) {

														if (nestedStep.getQueryDescriptors().get(nestedQueryIndex).getQueryID().equals(queryID)) {

															copyEvaluatorDescriptor.getTargetQueryIDs().add(flowCopy.getSteps().get(nestedStepIndex).getQueryDescriptors().get(nestedQueryIndex).getQueryID());

															break nestedStepLoop;
														}

														nestedQueryIndex++;
													}
												}

												nestedStepIndex++;
											}
										}
									}

									daoFactory.getEvaluatorDescriptorDAO().update(copyEvaluatorDescriptor, transactionHandler, null);

									evaluationHandler.copyEvaluator(sourceEvaluatorDescriptor, copyEvaluatorDescriptor, sourceQuery, copyQuery, transactionHandler);

									evaluatorIndex++;
								}
							}

							queryIndex++;
						}
					}

					stepIndex++;
				}
			}

			transactionHandler.commit();

		} finally {

			TransactionHandler.autoClose(transactionHandler);
		}

		eventHandler.sendEvent(Flow.class, new CRUDEvent<Flow>(CRUDAction.ADD, flowCopy), EventTarget.ALL);

		redirectToMethod(req, res, "/showflow/" + flowCopy.getFlowID());

		return null;
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateIcon(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException {

		Flow flow = flowCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.UPDATE);

		if (flow == null) {

			return list(req, res, user, uriParser, new ValidationError("UpdateFailedFlowNotFound"));

		} else if (!AccessUtils.checkAccess(user, flow.getFlowType())) {

			throw new AccessDeniedException("User does not have access to flow type " + flow.getFlowType());
		}

		ValidationError validationError = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {

			MultipartRequest multipartRequest = null;

			try {
				multipartRequest = new MultipartRequest(this.ramThreshold * BinarySizes.KiloByte, this.maxRequestSize * BinarySizes.MegaByte, tempDir, req);

				if (multipartRequest.getParameter("clearicon") != null) {

					log.info("User " + user + " restoring default icon for flow " + flow);

					flow.setIcon(null);
					flow.setIconFileName(null);

					this.daoFactory.getFlowDAO().update(flow);

					eventHandler.sendEvent(Flow.class, new CRUDEvent<Flow>(CRUDAction.UPDATE, flow), EventTarget.ALL);

					redirectToMethod(multipartRequest, res, "/showflow/" + flow.getFlowID());

					return null;

				} else if (multipartRequest.getFileCount() > 0 && !StringUtils.isEmpty(multipartRequest.getFile(0).getName())) {

					FileItem file = multipartRequest.getFile(0);

					String lowerCasefileName = file.getName().toLowerCase();

					if (!(lowerCasefileName.endsWith(".png") || lowerCasefileName.endsWith(".jpg") || lowerCasefileName.endsWith(".gif") || lowerCasefileName.endsWith(".bmp"))) {

						validationError = new ValidationError("InvalidIconFileFormat");

					} else {

						try {
							BufferedImage image = ImageUtils.getImage(file.get());

							if (image.getWidth() > maxFlowIconWidth || image.getHeight() > maxFlowIconHeight) {

								image = ImageUtils.scaleImage(image, maxFlowIconHeight, maxFlowIconWidth, Image.SCALE_SMOOTH, BufferedImage.TYPE_INT_ARGB);

								ByteArrayOutputStream iconStream = new ByteArrayOutputStream();

								ImageIO.write(image, "png", iconStream);

								flow.setIcon(new SerialBlob(iconStream.toByteArray()));
								flow.setIconFileName(FileUtils.replaceFileExtension(file.getName(), "png"));

							} else {

								flow.setIcon(new SerialBlob(file.get()));
								flow.setIconFileName(file.getName());
							}

							log.info("User " + user + " updating icon for flow " + flow);

							this.daoFactory.getFlowDAO().update(flow);

							eventHandler.sendEvent(Flow.class, new CRUDEvent<Flow>(CRUDAction.UPDATE, flow), EventTarget.ALL);

							redirectToMethod(multipartRequest, res, "/showflow/" + flow.getFlowID());

							return null;

						} catch (IOException e) {

							validationError = new ValidationError("UnableToParseIcon");
						}
					}

				} else {

					log.info("User " + user + " submitted update icon form for flow " + flow + " without making any changes.");

					redirectToMethod(multipartRequest, res, "/showflow/" + flow.getFlowID());

					return null;
				}

			} catch (FileUploadException e) {

				log.error("Error?", e);
				validationError = new ValidationError("UnableToParseRequest");

			} finally {

				if (multipartRequest != null) {
					multipartRequest.deleteFiles();
				}
			}
		}

		log.info("User " + user + " requesting update icon form for flow " + flow);

		Document doc = createDocument(req, uriParser, user);

		Element updateFlowIconElement = doc.createElement("UpdateFlowIcon");
		doc.getDocumentElement().appendChild(updateFlowIconElement);

		updateFlowIconElement.appendChild(flow.toXML(doc));

		if (validationError != null) {

			updateFlowIconElement.appendChild(validationError.toXML(doc));
		}

		return new SimpleForegroundModuleResponse(doc);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse sortFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException {

		Flow flow = flowCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.UPDATE);

		if (flow == null) {

			return list(req, res, user, uriParser, new ValidationError("FlowNotFound"));

		} else if (!AccessUtils.checkAccess(user, flow.getFlowType())) {

			throw new AccessDeniedException("User does not have access to flow type " + flow.getFlowType());

		} else if (flow.getSteps() == null) {

			log.info("User " + user + " requested sort steps and queries form for flow " + flow + " which has no steps.");

			redirectToMethod(req, res, "/showflow/" + flow.getFlowID());

			return null;
		}

		ValidationError validationError = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {

			validationError = parseSortPost(flow, req, res, user, uriParser);

			if (validationError == null) {

				return null;
			}
		}

		log.info("User " + user + " requesting sorting form for flow " + flow);

		Document doc = createDocument(req, uriParser, user);

		Element updateFlowIconElement = doc.createElement("SortFlow");
		doc.getDocumentElement().appendChild(updateFlowIconElement);

		updateFlowIconElement.appendChild(flow.toXML(doc));

		if (validationError != null) {

			updateFlowIconElement.appendChild(validationError.toXML(doc));
		}

		return new SimpleForegroundModuleResponse(doc);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateNotifications(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Flow flow = flowCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.SHOW);

		if (flow == null) {

			return list(req, res, user, uriParser, new ValidationError("FlowNotFound"));

		} else if (!AccessUtils.checkAccess(user, flow.getFlowType())) {

			throw new AccessDeniedException("User does not have access to flow type " + flow.getFlowType());

		} else if (notificationHandler == null) {

			log.info("User " + user + " requested notifications settings for flow " + flow + " but no flow notification handler is available");

			redirectToMethod(req, res, "/showflow/" + flow.getFlowID());

			return null;
		}

		ValidationException validationException = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {

			try{
				notificationHandler.updateSettings(flow, req, user, uriParser);

				log.info("User " + user + " updated notification settings for flow " + flow);

				redirectToMethod(req, res, "/showflow/" + flow.getFlowID() + "#notifications");

				return null;

			}catch(ValidationException e){

				validationException = e;
			}

		}

		log.info("User " + user + " notification settings for flow " + flow);

		Document doc = createDocument(req, uriParser, user);

		Element notificationSettingsElement = doc.createElement("UpdateNotifications");
		doc.getDocumentElement().appendChild(notificationSettingsElement);

		notificationSettingsElement.appendChild(flow.toXML(doc));

		ViewFragment viewFragment = notificationHandler.getUpdateSettingsView(flow, req, user, uriParser, validationException);

		notificationSettingsElement.appendChild(viewFragment.toXML(doc));

		return ViewFragmentUtils.appendLinksAndScripts(new SimpleForegroundModuleResponse(doc), viewFragment);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse generateXSD(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, SAXException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, XPathExpressionException, QueryProviderNotFoundException, QueryNotFoundInQueryProviderException, QueryProviderErrorException {

		Flow flow = flowCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.SHOW);

		if (flow == null) {

			return list(req, res, user, uriParser, new ValidationError("FlowNotFound"));

		} else if (!AccessUtils.checkAccess(user, flow.getFlowType())) {

			throw new AccessDeniedException("User does not have access to flow type " + flow.getFlowType());
		}

		log.info("User " + user + " requesting XSD for flow " + flow);

		Document doc = XMLUtils.parseXML(FlowAdminModule.class.getResourceAsStream("xsd/base-schema.xsd"), false, false);

		if (flow.getSteps() != null) {

			XPath xPath = XPathFactory.newInstance().newXPath();

			Element valuesSequenceElement = (Element) xPath.evaluate("complexType[@name='Values']/sequence", doc.getDocumentElement(), XPathConstants.NODE);

			for (Step step : flow.getSteps()) {

				if (step.getQueryDescriptors() != null) {

					for (QueryDescriptor queryDescriptor : step.getQueryDescriptors()) {

						if (!queryDescriptor.isExported()) {

							continue;
						}

						Query query = queryHandler.getQuery(queryDescriptor);

						Element queryElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
						queryElement.setAttribute("name", queryDescriptor.getXSDElementName());
						queryElement.setAttribute("type", query.getXSDTypeName());
						queryElement.setAttribute("minOccurs", "0");
						queryElement.setAttribute("maxOccurs", "1");
						valuesSequenceElement.appendChild(queryElement);

						query.toXSD(doc);
					}
				}
			}
		}

		res.setHeader("Content-Disposition", "attachment; filename=\"schema-" + flow.getFlowID() + ".xsd\"");
		res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
		res.setContentType("text/xml");

		XMLUtils.writeXML(doc, res.getOutputStream(), true, systemInterface.getEncoding());

		return null;
	}

	private ValidationError parseSortPost(Flow flow, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, IOException {

		List<QueryDescriptor> queryDescriptors = new ArrayList<QueryDescriptor>();

		//Set sort index for each step
		for (Step step : flow.getSteps()) {

			Integer sortIndex = NumberUtils.toInt(req.getParameter("step" + step.getStepID()));

			if (sortIndex == null) {

				return new NoStepSortindexValidationError(step);
			}

			step.setSortIndex(sortIndex);

			if (step.getQueryDescriptors() != null) {

				for (QueryDescriptor queryDescriptor : step.getQueryDescriptors()) {

					sortIndex = NumberUtils.toInt(req.getParameter("query" + queryDescriptor.getQueryID()));

					if (sortIndex == null) {

						return new NoQueryDescriptorSortindexValidationError(queryDescriptor);
					}

					queryDescriptor.setSortIndex(sortIndex);

					queryDescriptors.add(queryDescriptor);
				}

				step.getQueryDescriptors().clear();

			} else {

				step.setQueryDescriptors(new ArrayList<QueryDescriptor>());
			}
		}

		Collections.sort(flow.getSteps(), STEP_COMPARATOR);
		Collections.sort(queryDescriptors, QUERY_DESCRIPTOR_COMPARATOR);

		List<QueryDescriptor> queryDescriptorsCopy = new ArrayList<QueryDescriptor>(queryDescriptors);

		Iterator<QueryDescriptor> queryIterator = queryDescriptors.iterator();

		outer: while (queryIterator.hasNext()) {

			QueryDescriptor queryDescriptor = queryIterator.next();

			int stepIndex = flow.getSteps().size() - 1;

			while (stepIndex >= 0) {

				Step step = flow.getSteps().get(stepIndex);

				if (queryDescriptor.getSortIndex() > step.getSortIndex()) {

					step.getQueryDescriptors().add(queryDescriptor);
					queryIterator.remove();
					continue outer;
				}

				stepIndex--;
			}
		}

		if (!queryDescriptors.isEmpty()) {

			return new ValidationError("UnableToFindStepsForAllQueries");
		}

		int stepSortIndex = 0;

		List<Integer> addedQueries = new ArrayList<Integer>();

		for (Step step : flow.getSteps()) {

			step.setSortIndex(stepSortIndex);
			step.setFlow(flow);

			stepSortIndex++;

			int querySortIndex = 0;

			for (QueryDescriptor queryDescriptor : step.getQueryDescriptors()) {

				queryDescriptor.setSortIndex(querySortIndex);

				if (queryDescriptor.getEvaluatorDescriptors() != null) {

					for (EvaluatorDescriptor evaluatorDescriptor : queryDescriptor.getEvaluatorDescriptors()) {

						if (evaluatorDescriptor.getTargetQueryIDs() != null) {

							for (Integer queryID : evaluatorDescriptor.getTargetQueryIDs()) {

								if (addedQueries.contains(queryID)) {

									return new ValidationError("InvalidQuerySortIndex");

								}

							}

						}

					}

				}

				addedQueries.add(queryDescriptor.getQueryID());

				querySortIndex++;
			}
		}

		log.info("User " + user + " updating sorting of flow " + flow);

		HighLevelQuery<Step> query = new HighLevelQuery<Step>();

		query.addRelation(Step.QUERY_DESCRIPTORS_RELATION);

		daoFactory.getStepDAO().update(flow.getSteps(), query);

		eventHandler.sendEvent(Step.class, new CRUDEvent<Step>(Step.class, CRUDAction.UPDATE, flow.getSteps()), EventTarget.ALL);
		eventHandler.sendEvent(QueryDescriptor.class, new CRUDEvent<QueryDescriptor>(QueryDescriptor.class, CRUDAction.UPDATE, queryDescriptorsCopy), EventTarget.ALL);

		redirectToMethod(req, res, "/showflow/" + flow.getFlowID() + "#steps");

		return null;
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addStep(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return stepCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateStep(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return stepCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteStep(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return stepCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addStatus(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return statusCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateStatus(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return statusCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteStatus(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return statusCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(alias = "standardstatuses")
	public ForegroundModuleResponse listStandardStatuses(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return standardStatusCRUD.list(req, res, user, uriParser, null);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addStandardStatus(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return standardStatusCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateStandardStatus(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return standardStatusCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteStandardStatus(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return standardStatusCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return queryDescriptorCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return queryDescriptorCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return queryDescriptorCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addEvaluator(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return evaluatorDescriptorCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateEvaluator(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return evaluatorDescriptorCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteEvaluator(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return evaluatorDescriptorCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse testFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		Integer flowID = null;
		MutableFlowInstanceManager instanceManager;

		try {
			if (uriParser.size() == 3 && (flowID = NumberUtils.toInt(uriParser.get(2))) != null) {

				//Create new instance or get instance from session
				instanceManager = getUnsavedMutableFlowInstanceManager(flowID, UPDATE_ACCESS_CONTROLLER, req.getSession(true), user, uriParser, req, true, false, false);

				if (instanceManager == null) {

					log.info("User " + user + " requested non-existing flow with ID " + flowID + ", listing flows");
					return list(req, res, user, uriParser, FLOW_NOT_FOUND_VALIDATION_ERROR);
				}

			} else {

				log.info("User " + user + " requested invalid URL, listing flows");
				return list(req, res, user, uriParser, INVALID_LINK_VALIDATION_ERROR);
			}

		} catch (FlowNoLongerAvailableException e) {

			log.info("User " + user + " requested flow " + e.getFlow() + " which is no longer available.");
			return list(req, res, user, uriParser, FLOW_NO_LONGER_AVAILABLE_VALIDATION_ERROR);

		} catch (FlowEngineException e) {

			log.error("Unable to get flow instance manager for flowID " + flowID + " requested by user " + user, e);
			return list(req, res, user, uriParser, ERROR_GETTING_FLOW_INSTANCE_MANAGER_VALIDATION_ERROR);
		}

		try {
			return processFlowRequest(instanceManager, this, UPDATE_ACCESS_CONTROLLER, req, res, user, uriParser, false);

		} catch (FlowInstanceManagerClosedException e) {

			log.info("User " + user + " requested flow instance manager for flow instance " + e.getFlowInstance() + " which has already been closed. Removing flow instance manager from session.");

			removeMutableFlowInstanceManagerFromSession(instanceManager, req.getSession(false));

			redirectToMethod(req, res, "/testflow/" + flowID);

			return null;
		}
	}

	@WebPublic(alias = "flowtypes")
	public ForegroundModuleResponse listFlowTypes(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowTypeCRUD.list(req, res, user, uriParser, null);
	}

	@WebPublic(alias = "flowtype")
	public ForegroundModuleResponse showFlowType(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowTypeCRUD.show(req, res, user, uriParser, null);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addFlowType(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowTypeCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateFlowType(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowTypeCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteFlowType(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowTypeCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse addCategory(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		FlowType flowType = flowTypeCRUD.getRequestedBean(req, res, user, uriParser, null);

		if (flowType == null) {

			return flowTypeCRUD.list(req, res, user, uriParser, Collections.singletonList(new ValidationError("AddCategoryFailedFlowTypeNotFound")));

		}

		req.setAttribute("flowType", flowType);

		return categoryCRUD.add(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse updateCategory(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return categoryCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse deleteCategory(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return categoryCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic(alias = "updatemanagers")
	public ForegroundModuleResponse updateFlowFamilyManagers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return flowFamilyCRUD.update(req, res, user, uriParser);
	}

	@WebPublic(alias = "mquery")
	public ForegroundModuleResponse processMutableQueryRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException, SQLException, AccessDeniedException, IOException, FlowDefaultStatusNotFound, EvaluationException, URINotFoundException, QueryRequestException, QueryProviderException, EvaluationProviderException, InvalidFlowInstanceStepException, MissingQueryInstanceDescriptor, DuplicateFlowInstanceManagerIDException {

		return processMutableQueryRequest(req, res, user, uriParser, UPDATE_ACCESS_CONTROLLER, false, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void processEvent(CRUDEvent<?> event, EventSource source) {

		try {
			log.debug("Received crud event regarding " + event.getAction() + " of " + event.getBeans().size() + " beans with " + event.getBeanClass());

			//Increment flow instance count for the given flow if the event is as an add or delete of a flow instance
			if (FlowInstance.class.isAssignableFrom(event.getBeanClass())) {

				for (FlowInstance flowInstance : (List<FlowInstance>) event.getBeans()) {

					Flow flow;

					//Check if the given flow is found in the cache else reload the whole cache
					if (flowInstance.getFlow() == null || (flow = flowCacheMap.get(flowInstance.getFlow().getFlowID())) == null) {

						cacheFlows();
						return;

					} else if (event.getAction() == CRUDAction.ADD) {

						flow.setFlowInstanceCount(flow.getFlowInstanceCount() + 1);

						Status status = getCachedStatus(flow, flowInstance.getStatus());

						if (status != null) {

							status.setFlowInstanceCount(status.getFlowInstanceCount() + 1);
							continue;

						} else {

							cacheFlows();
							return;
						}


					} else if (event.getAction() == CRUDAction.DELETE) {

						flow.setFlowInstanceCount(flow.getFlowInstanceCount() - 1);

						Status status = getCachedStatus(flow, flowInstance.getStatus());

						if (status != null) {

							status.setFlowInstanceCount(status.getFlowInstanceCount() - 1);
							continue;

						} else {

							cacheFlows();
							return;
						}

					}

					//Update operation, reload flow instance count for each status from DB
					TransactionHandler transactionHandler = null;

					try {
						transactionHandler = new TransactionHandler(dataSource);

						if (flow.getStatuses() != null) {

							for (Status status : flow.getStatuses()) {

								status.setFlowInstanceCount(getFlowInstanceCount(status, transactionHandler));
							}
						}

					} finally {

						TransactionHandler.autoClose(transactionHandler);
					}
				}

				return;

			} else if (FlowType.class.isAssignableFrom(event.getBeanClass()) || Category.class.isAssignableFrom(event.getBeanClass())) {

				cacheFlowTypes();

			} else if (Flow.class.isAssignableFrom(event.getBeanClass()) && (event.getAction() != CRUDAction.ADD)) {

				for (Flow flow : (List<Flow>) event.getBeans()) {

					closeInstanceManagers(flow);
				}

			} else if (Step.class.isAssignableFrom(event.getBeanClass())) {

				for (Step step : (List<Step>) event.getBeans()) {

					closeInstanceManagers(step.getFlow());
				}

			} else if (QueryDescriptor.class.isAssignableFrom(event.getBeanClass())) {

				for (QueryDescriptor queryDescriptor : (List<QueryDescriptor>) event.getBeans()) {

					closeInstanceManagers(queryDescriptor.getStep().getFlow());
				}

			} else if (EvaluatorDescriptor.class.isAssignableFrom(event.getBeanClass())) {

				for (EvaluatorDescriptor evaluatorDescriptor : (List<EvaluatorDescriptor>) event.getBeans()) {

					closeInstanceManagers(evaluatorDescriptor.getQueryDescriptor().getStep().getFlow());
				}
			}

			cacheFlows();

		} catch (SQLException e) {
			log.error("Error reloading cache", e);
		}
	}

	private Status getCachedStatus(Flow flow, Status status) {

		if (status == null || flow.getStatuses() == null) {

			return null;
		}

		for (Status cachedStatus : flow.getStatuses()) {

			if (cachedStatus.equals(status)) {

				return cachedStatus;
			}
		}

		return null;
	}

	private void closeInstanceManagers(Flow flow) {

		int closedCount = FlowInstanceManagerRegistery.getInstance().closeInstances(flow, queryHandler);

		if (closedCount > 0) {
			log.info("Closed " + closedCount + " flow instance managers handling instances of flow " + flow);
		}
	}

	@Override
	protected Flow getBareFlow(Integer flowID) throws SQLException {

		return flowCacheMap.get(flowID);
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	public Flow getCachedFlow(Integer flowID) {

		return flowCacheMap.get(flowID);
	}

	@Override
	public int getRamThreshold() {

		return ramThreshold;
	}

	@Override
	public int getMaxRequestSize() {

		return maxRequestSize;
	}

	public Collection<FlowType> getCachedFlowTypes() {

		return this.flowTypeCacheMap.values();
	}

	public FlowType getCachedFlowType(Integer flowTypeID) {

		return flowTypeCacheMap.get(flowTypeID);
	}

	public EventHandler getEventHandler() {

		return eventHandler;
	}

	public Flow getRequestedFlow(HttpServletRequest req, User user, URIParser uriParser) throws AccessDeniedException, SQLException {

		return flowCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.SHOW);
	}

	public QueryDescriptor getQueryDescriptor(int queryID) throws AccessDeniedException, SQLException {

		return queryDescriptorCRUD.getBean(queryID);
	}

	public EvaluatorDescriptor getEvaluatorDescriptor(int evaluatorID) throws AccessDeniedException, SQLException {

		return evaluatorDescriptorCRUD.getBean(evaluatorID);
	}

	public String getFlowQueryRedirectURL(HttpServletRequest req, int flowID) {

		return req.getContextPath() + this.getFullAlias() + "/showflow/" + flowID + "#steps";
	}

	public void checkFlowStructureManipulationAccess(User user, Flow flow) throws AccessDeniedException, SQLException {

		if(!flow.isInternal()) {

			throw new AccessDeniedException("Requested flow is external and cannot be structure manipulated");

		} else if (!AccessUtils.checkAccess(user, flow.getFlowType())) {

			throw new AccessDeniedException("User does not have access to flow type " + flow.getFlowType());

		} else if (flow.isPublished() && flow.isEnabled()) {

			throw new AccessDeniedException("Changes to queries in flow " + flow + " is not allowed since the flow is published");

		} else if ((flow.getFlowInstanceCount() != null && flow.getFlowInstanceCount() > 0) || (flow.getFlowInstanceCount() == null && getFlowInstanceCount(flow) > 0)) {

			throw new AccessDeniedException("Changes to queries in flow " + flow + " is not allowed since the flow has one or more flow instances connected to it.");
		}
	}

	public DataSource getDataSource() {

		return dataSource;
	}

	@Override
	public String getAbsoluteFileURL(URIParser uriParser, Object bean) {

		return null;
	}

	@Override
	public EvaluationHandler getEvaluationHandler() {

		return evaluationHandler;
	}

	public QueryDescriptor getRequestedQueryDescriptor(HttpServletRequest req, User user, URIParser uriParser) throws AccessDeniedException, SQLException {

		return queryDescriptorCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.SHOW);
	}

	public ImmutableFlow getFlow(int flowID) {

		return flowCacheMap.get(flowID);
	}

	public FlowFamily getFlowFamily(int flowFamilyID) {

		return flowFamilyCacheMap.get(flowFamilyID);
	}

	public List<Flow> getFlowVersions(FlowFamily flowFamily) {

		List<Flow> flows = new ArrayList<Flow>(flowFamily.getVersionCount());

		for (Flow flow : flowCacheMap.values()) {

			if (flow.getFlowFamily().getFlowFamilyID().equals(flowFamily.getFlowFamilyID())) {

				flows.add(flow);
			}
		}

		if (!flows.isEmpty()) {

			Collections.sort(flows, FLOW_VERSION_COMPARATOR);
		}

		return flows;
	}

	public List<Flow> getFlows(int flowTypeID) {

		List<Flow> flows = new ArrayList<Flow>();

		for (Flow flow : flowCacheMap.values()) {

			if (flow.getFlowType().getFlowTypeID() == flowTypeID) {

				flows.add(flow);
			}
		}

		return flows;
	}

	public List<FlowFamily> getFlowFamilies(int flowTypeID) {

		HashSet<FlowFamily> flowFamilies = new HashSet<FlowFamily>(this.flowFamilyCacheMap.size());

		List<Flow> flows = getFlows(flowTypeID);

		for (Flow flow : flows) {

			flowFamilies.add(flow.getFlowFamily());
		}

		return new ArrayList<FlowFamily>(flowFamilies);
	}

	public Flow getLatestFlowVersion(FlowFamily flowFamily) {

		List<Flow> flows = getFlowVersions(flowFamily);

		return flows.get(flows.size() - 1);
	}

	public boolean hasFlows(FlowFamily flowFamily, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<Flow> query = new HighLevelQuery<Flow>();

		query.addParameter(flowFlowFamilyParamFactory.getParameter(flowFamily));

		return daoFactory.getFlowDAO().getBoolean(query, transactionHandler);
	}

	public List<FlowAction> getFlowActions(boolean getOptionalActions) throws SQLException {

		HighLevelQuery<FlowAction> query = new HighLevelQuery<FlowAction>();

		if (!getOptionalActions) {

			query.addParameter(flowActionRequiredParamFactory.getParameter(true));

		}

		return daoFactory.getFlowActionDAO().getAll(query);
	}

	@Override
	public boolean allowsAdminAccess() {

		return false;
	}

	@Override
	public boolean allowsUserAccess() {

		return false;
	}

	@Override
	public boolean allowsAnonymousAccess() {

		return false;
	}

	@Override
	public Collection<Integer> getAllowedGroupIDs() {

		return adminGroupIDs;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {

		return adminUserIDs;
	}

	@Override
	public String getSubmitActionID() {

		return null;
	}

	@Override
	public String getSaveActionID() {

		return null;
	}

	@Override
	public String getPaymentActionID() {

		return null;
	}

	@Override
	public String getMultiSigningActionID() {

		return null;
	}

	@Override
	protected FlowInstanceEvent save(MutableFlowInstanceManager instanceManager, User user, HttpServletRequest req, String actionID, EventType eventType) throws FlowInstanceManagerClosedException, UnableToSaveQueryInstanceException, SQLException, FlowDefaultStatusNotFound {

		return null;
	}

	@Override
	protected Flow getFlow(Integer flowID) throws SQLException {

		return SerializationUtils.cloneSerializable(getCachedFlow(flowID));
	}

	public Collection<Flow> getCachedFlows() {

		return this.flowCacheMap.values();
	}

	public Collection<FlowFamily> getCachedFlowFamilies() {

		return this.flowFamilyCacheMap.values();
	}

	@Override
	public Breadcrumb getFlowBreadcrumb(ImmutableFlowInstance flowInstance) {

		return new Breadcrumb(this, flowInstance.getFlow().getName(), "/testflow/" + flowInstance.getFlow().getFlowID());
	}

	public FlowTypeCRUD getFlowTypeCRUD() {

		return flowTypeCRUD;
	}

	public UserHandler getUserHandler() {

		return systemInterface.getUserHandler();
	}

	@Override
	public void appendFormData(Document doc, Element baseElement, MutableFlowInstanceManager instanceManager, User user) {

	}

	public GroupHandler getGroupHandler() {

		return systemInterface.getGroupHandler();
	}

	public FlowType getFlowType(Integer flowTypeID) {

		return flowTypeCacheMap.get(flowTypeID);
	}

	@Override
	protected void redirectToSubmitMethod(MutableFlowInstanceManager flowInstance, HttpServletRequest req, HttpServletResponse res) throws IOException {

		redirectToMethod(req, res, "/submitted/" + flowInstance.getFlowID());
	}

	@Override
	protected void onFlowInstanceClosedRedirect(FlowInstanceManager flowInstanceManager, HttpServletRequest req, HttpServletResponse res) throws IOException {

		redirectToMethod(req, res, "/showflow/" + flowInstanceManager.getFlowID());

	}

	@Override
	protected void closeSubmittedFlowInstanceManager(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		SessionUtils.setAttribute(this.getClass().getName() + "-flow-" + instanceManager.getFlowID(), instanceManager, req);
	}

	@WebPublic(alias = "submitted")
	public ForegroundModuleResponse showSubmittedMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws FlowInstanceManagerClosedException, UnableToGetQueryInstanceShowHTMLException, AccessDeniedException, ModuleConfigurationException, SQLException {

		Integer flowID;

		if (uriParser.size() == 3 && (flowID = uriParser.getInt(2)) != null) {

			MutableFlowInstanceManager instanceManager = (MutableFlowInstanceManager) SessionUtils.getAttribute(this.getClass().getName() + "-flow-" + flowID, req);

			if (instanceManager != null) {

				ForegroundModuleResponse moduleResponse = showFlowInstance(req, res, user, uriParser, instanceManager, PREVIEW_ACCESS_CONTROLLER, "FlowInstanceManagerSubmitted", null, ShowMode.SUBMIT);

				instanceManager.close(queryHandler);

				SessionUtils.removeAttribute(this.getClass().getName() + "-flow-" + flowID, req);

				return moduleResponse;
			}
		}

		log.info("User " + user + " requested invalid URL, listing flows");
		return list(req, res, user, uriParser, INVALID_LINK_VALIDATION_ERROR);
	}

	@Override
	protected String getBaseUpdateURL(HttpServletRequest req, URIParser uriParser, User user, ImmutableFlowInstance flowInstance, FlowInstanceAccessController accessController) {

		if (!accessController.isMutable(flowInstance, user)) {

			return null;
		}

		return req.getContextPath() + uriParser.getFormattedURI();
	}

	@Override
	public String getSignFailURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		return null;
	}

	@Override
	public String getSignSuccessURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		return null;
	}

	@Override
	public String getSigningURL(MutableFlowInstanceManager instanceManager, HttpServletRequest req) {

		return null;
	}

	public SettingHandler getSiteProfileSettingHandler(User user, HttpServletRequest req, URIParser uriParser) {

		if(siteProfileHandler != null) {

			return siteProfileHandler.getCurrentSettingHandler(user, req, uriParser);
		}

		return null;

	}

	public boolean changeQueryTypeID(String oldQueryTypeID, String newQueryTypeID) throws SQLException{

		TransactionHandler transactionHandler = null;

		try{
			transactionHandler = daoFactory.getTransactionHandler();

			AnnotatedDAO<QueryDescriptor> queryDescriptorDAO = daoFactory.getQueryDescriptorDAO();

			//First check that the new query type ID is not taken already
			HighLevelQuery<QueryDescriptor> newIdCheckQuery = new HighLevelQuery<QueryDescriptor>();

			newIdCheckQuery.addParameter(queryDescriptorQueryTypeIDParamFactory.getParameter(newQueryTypeID));

			Integer newMatchCount = queryDescriptorDAO.getCount(newIdCheckQuery, transactionHandler);

			if(newMatchCount != null && newMatchCount > 0){

				log.error("Refusing to change queryTypeID from " + oldQueryTypeID + " to " + newQueryTypeID + " since there already exists " + newMatchCount + " query descriptors of this type in DB");

				return false;
			}

			//Check if there are any query descriptors using the old ID
			HighLevelQuery<QueryDescriptor> oldIdCheckQuery = new HighLevelQuery<QueryDescriptor>();

			oldIdCheckQuery.addParameter(queryDescriptorQueryTypeIDParamFactory.getParameter(oldQueryTypeID));

			Integer oldMatchCount = queryDescriptorDAO.getCount(oldIdCheckQuery, transactionHandler);

			if(oldMatchCount == null || oldMatchCount == 0){

				return true;
			}

			log.info("Changing queryTypeID for " + oldMatchCount + " query descriptors from " + oldQueryTypeID + " to " + newQueryTypeID);

			LowLevelQuery<QueryDescriptor> descriptorsUpdateQuery = new LowLevelQuery<QueryDescriptor>("UPDATE " + queryDescriptorDAO.getTableName() + " SET " + queryDescriptorQueryTypeIDParamFactory.getColumnName() + " = ? WHERE " + queryDescriptorQueryTypeIDParamFactory.getColumnName() + " = ?");

			descriptorsUpdateQuery.addParameter(newQueryTypeID);
			descriptorsUpdateQuery.addParameter(oldQueryTypeID);

			queryDescriptorDAO.update(descriptorsUpdateQuery, transactionHandler);

			LowLevelQuery<QueryDescriptor> flowTypesUpdateQuery = new LowLevelQuery<QueryDescriptor>("UPDATE flowengine_flow_type_allowed_queries SET queryTypeID = ? WHERE queryTypeID = ?");

			flowTypesUpdateQuery.addParameter(newQueryTypeID);
			flowTypesUpdateQuery.addParameter(oldQueryTypeID);

			queryDescriptorDAO.update(flowTypesUpdateQuery, transactionHandler);

			transactionHandler.commit();

			cacheFlows();
			cacheFlowTypes();

			return true;

		}finally{

			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public int getQueryCount(String queryTypeID) throws SQLException{

		AnnotatedDAO<QueryDescriptor> queryDescriptorDAO = daoFactory.getQueryDescriptorDAO();

		HighLevelQuery<QueryDescriptor> query = new HighLevelQuery<QueryDescriptor>();

		query.addParameter(queryDescriptorQueryTypeIDParamFactory.getParameter(queryTypeID));

		return NumberUtils.toPrimitiveInt(queryDescriptorDAO.getCount(query));
	}

	public boolean changeEvaluatorTypeID(String oldEvaluatorTypeID, String newEvaluatorTypeID) throws SQLException {

		TransactionHandler transactionHandler = null;

		try{
			transactionHandler = daoFactory.getTransactionHandler();

			AnnotatedDAO<EvaluatorDescriptor> evaluatorDescriptorDAO = daoFactory.getEvaluatorDescriptorDAO();

			//First check that the new evaluator type ID is not taken already
			HighLevelQuery<EvaluatorDescriptor> newIdCheckQuery = new HighLevelQuery<EvaluatorDescriptor>();

			newIdCheckQuery.addParameter(evaluatorDescriptorEvaluatorTypeIDParamFactory.getParameter(newEvaluatorTypeID));

			Integer newMatchCount = evaluatorDescriptorDAO.getCount(newIdCheckQuery, transactionHandler);

			if(newMatchCount != null && newMatchCount > 0){

				log.error("Refusing to change evaluatorTypeID from " + oldEvaluatorTypeID + " to " + newEvaluatorTypeID + " since there already exists " + newMatchCount + " evaluator descriptors of this type in DB");

				return false;
			}

			//Check if there are any evaluator descriptors using the old ID
			HighLevelQuery<EvaluatorDescriptor> oldIdCheckQuery = new HighLevelQuery<EvaluatorDescriptor>();

			oldIdCheckQuery.addParameter(evaluatorDescriptorEvaluatorTypeIDParamFactory.getParameter(oldEvaluatorTypeID));

			Integer oldMatchCount = evaluatorDescriptorDAO.getCount(oldIdCheckQuery, transactionHandler);

			if(oldMatchCount == null || oldMatchCount == 0){

				return true;
			}

			log.info("Changing evaluatorTypeID for " + oldMatchCount + " evaluator descriptors from " + oldEvaluatorTypeID + " to " + newEvaluatorTypeID);

			LowLevelQuery<EvaluatorDescriptor> descriptorsUpdateQuery = new LowLevelQuery<EvaluatorDescriptor>("UPDATE " + evaluatorDescriptorDAO.getTableName() + " SET " + evaluatorDescriptorEvaluatorTypeIDParamFactory.getColumnName() + " = ? WHERE " + evaluatorDescriptorEvaluatorTypeIDParamFactory.getColumnName() + " = ?");

			descriptorsUpdateQuery.addParameter(newEvaluatorTypeID);
			descriptorsUpdateQuery.addParameter(oldEvaluatorTypeID);

			evaluatorDescriptorDAO.update(descriptorsUpdateQuery, transactionHandler);

			transactionHandler.commit();

			cacheFlows();
			cacheFlowTypes();

			return true;

		}finally{

			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public int getEvaluatorCount(String evaluatorTypeID) throws SQLException {

		AnnotatedDAO<EvaluatorDescriptor> evaluatorDescriptorDAO = daoFactory.getEvaluatorDescriptorDAO();

		HighLevelQuery<EvaluatorDescriptor> query = new HighLevelQuery<EvaluatorDescriptor>();

		query.addParameter(evaluatorDescriptorEvaluatorTypeIDParamFactory.getParameter(evaluatorTypeID));

		return NumberUtils.toPrimitiveInt(evaluatorDescriptorDAO.getCount(query));
	}

	@WebPublic(toLowerCase = true)
	public ForegroundModuleResponse exportFlow(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerFactoryConfigurationError, Exception {

		Flow flow = flowCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.SHOW);

		if (flow == null) {

			return list(req, res, user, uriParser, new ValidationError("FlowNotFound"));

		} else if (!AccessUtils.checkAccess(user, flow.getFlowType())) {

			throw new AccessDeniedException("User does not have access to flow type " + flow.getFlowType());

		}

		log.info("User " + user + " exporting flow " + flow);

		List<ValidationError> validationErrors = new ArrayList<ValidationError>();

		Document doc = XMLUtils.createDomDocument();

		XMLGeneratorDocument xmlGeneratorDocument = new XMLGeneratorDocument(doc);

		xmlGeneratorDocument.addElementableListener(QueryDescriptor.class, new QueryDescriptorElementableListener(queryHandler, validationErrors));
		xmlGeneratorDocument.addElementableListener(EvaluatorDescriptor.class, new EvaluatorDescriptorElementableListener(evaluationHandler, validationErrors));

		doc.appendChild(flow.toXML(xmlGeneratorDocument));

		if(flow.getIcon() != null){

			XMLUtils.appendNewElement(doc, doc.getDocumentElement(), "icon", Base64.encodeBytes(StreamUtils.toByteArray(flow.getIcon().getBinaryStream())));
		}

		if(!validationErrors.isEmpty()){

			return flowCRUD.showBean(flow, req, res, user, uriParser, validationErrors);
		}

		res.setHeader("Content-Disposition", "attachment; filename=\"" + flow.getName() + ".oeflow\"");
		res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
		res.setContentType("text/xml");

		XMLUtils.writeXML(doc, res.getOutputStream(), true, systemInterface.getEncoding());

		return null;
	}

	@WebPublic(toLowerCase = true, alias="importversion")
	public ForegroundModuleResponse importFlowIntoExistingFamily(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerFactoryConfigurationError, Exception {

		Flow flow = flowCRUD.getRequestedBean(req, null, user, uriParser, GenericCRUD.SHOW);

		if (flow == null) {

			return list(req, res, user, uriParser, new ValidationError("FlowNotFound"));

		} else if (!AccessUtils.checkAccess(user, flow.getFlowType())) {

			throw new AccessDeniedException("User does not have access to flow type " + flow.getFlowType());
		}

		return importFlow(flow.getFlowType(), flow, req, res, user, uriParser);
	}

	@WebPublic(toLowerCase = true, alias="importflow")
	public ForegroundModuleResponse importFlowIntoNewFamily(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws TransformerFactoryConfigurationError, Exception {

		FlowType flowType = this.flowTypeCRUD.getRequestedBean(req, res, user, uriParser, FlowCRUD.SHOW);

		if (flowType == null) {

			log.info("User " + user + " listing flow type import targets");

			Document doc = createDocument(req, uriParser, user);
			Element selectImportTargetFamily = doc.createElement("SelectImportTargetType");
			doc.getDocumentElement().appendChild(selectImportTargetFamily);

			appendUserFlowTypes(doc, selectImportTargetFamily, user);

			return new SimpleForegroundModuleResponse(doc);

		}else if (!AccessUtils.checkAccess(user, flowType)) {

			throw new AccessDeniedException("User does not have access to flow type " + flowType);
		}

		return importFlow(flowType, null, req, res, user, uriParser);
	}

	public synchronized ForegroundModuleResponse importFlow(FlowType flowType, Flow relatedFlow, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws InstantiationException, IllegalAccessException, SQLException, IOException{

		ValidationException validationException = null;

		if(req.getMethod().equalsIgnoreCase("POST") && MultipartRequest.isMultipartRequest(req)){

			log.info("User " + user + " importing flow...");

			MultipartRequest multipartRequest = null;

			try{
				multipartRequest = new MultipartRequest(ramThreshold * BinarySizes.KiloByte, maxRequestSize * BinarySizes.MegaByte, req);
				req = multipartRequest;

				if(multipartRequest.getFileCount() == 0){

					throw new ValidationException(new ValidationError("NoAttachedFlowFile"));
				}

				FileItem fileItem = multipartRequest.getFile(0);

				if(!fileItem.getName().endsWith(".oeflow")){

					throw new ValidationException(new InvalidFileExtensionValidationError(fileItem.getName(), "oeflow"));
				}

				InputStream inputStream = null;

				Document doc = null;

				try{
					inputStream = fileItem.getInputStream();

					doc = XMLUtils.parseXML(inputStream, false, false);

				}catch(Exception e){

					log.info("Unable to parse file " + fileItem.getName(), e);

					throw new ValidationException(new UnableToParseFileValidationError(fileItem.getName()));

				}finally{

					StreamUtils.closeStream(inputStream);
				}

				Element docElement = doc.getDocumentElement();

				if(!docElement.getTagName().equals("Flow")){

					log.info("Error parsing file " + fileItem.getName() + ", unable to find flow element");

					throw new ValidationException(new UnableToParseFileValidationError(fileItem.getName()));
				}

				Flow flow = Flow.class.newInstance();

				flow.populate(new XMLParser(docElement));

				flow.setFlowType(flowType);

				if(relatedFlow == null){

					flow.setVersion(1);

					FlowFamily flowFamily = new FlowFamily();
					flowFamily.setVersionCount(1);

					flow.setFlowFamily(flowFamily);

				}else{

					Integer version = relatedFlow.getFlowFamily().getVersionCount() + 1;

					FlowFamily flowFamily = SerializationUtils.cloneSerializable(relatedFlow.getFlowFamily());

					flow.setVersion(version);
					flow.setFlowFamily(flowFamily);
					flowFamily.setVersionCount(version);
				}

				Integer categoryID = NumberUtils.toInt(req.getParameter("categoryID"));

				if (categoryID != null && flowType.getCategories() != null) {

					for (Category category : flowType.getCategories()) {

						if (category.getCategoryID().equals(categoryID)) {

							flow.setCategory(category);
							break;
						}

					}
				}

				//Create translation map for query ID's in order to able to update target queries field of evaluators later on
				HashMap<EvaluatorDescriptor, List<QueryDescriptor>> evaluatorTargetQueriesMap = new HashMap<EvaluatorDescriptor, List<QueryDescriptor>>();

				if(flow.getSteps() != null){

					List<ValidationError> validationErrors = new ArrayList<ValidationError>();

					for(Step step : flow.getSteps()){

						if(step.getQueryDescriptors() != null){

							for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

								//Check if a query provider for this query type is available
								if(queryHandler.getQueryProvider(queryDescriptor.getQueryTypeID()) == null){

									log.info("Unable to find query provider for query type " + queryDescriptor.getQueryTypeID() + " used by query " + queryDescriptor);

									validationErrors.add(new QueryTypeNotFoundValidationError(queryDescriptor));

								}else{

									//Check if this query type is allowed for the select flowtype
									if(flowType.getAllowedQueryTypes() == null || !flowType.getAllowedQueryTypes().contains(queryDescriptor.getQueryTypeID())){

										validationErrors.add(new QueryTypeNotAllowedInFlowTypeValidationError(queryDescriptor, flowType));
									}
								}


								if(queryDescriptor.getEvaluatorDescriptors() != null){

									for(EvaluatorDescriptor evaluatorDescriptor : queryDescriptor.getEvaluatorDescriptors()){

										//Check if a evaluation provider for this evaluator type is available
										if(evaluationHandler.getEvaluationProvider(evaluatorDescriptor.getEvaluatorTypeID()) == null){

											log.info("Unable to find evulation provider for evaluator type " + evaluatorDescriptor.getEvaluatorTypeID() + " used by evaluator " + evaluatorDescriptor);

											validationErrors.add(new EvaluatorTypeNotFoundValidationError(evaluatorDescriptor));
										}

										if(evaluatorDescriptor.getTargetQueryIDs() != null){

											List<QueryDescriptor> targetQueries = getTargetQueries(evaluatorDescriptor.getTargetQueryIDs(), flow.getSteps());

											if(targetQueries != null){

												evaluatorTargetQueriesMap.put(evaluatorDescriptor, targetQueries);
											}

											evaluatorDescriptor.setTargetQueryIDs(null);
										}
									}
								}
							}
						}
					}

					if(!validationErrors.isEmpty()){

						throw new ValidationException(validationErrors);
					}
				}

				//Set correct status references on default flow statuses and check actionID's
				if(flow.getStatuses() != null && flow.getDefaultFlowStateMappings() != null){

					Iterator<DefaultStatusMapping> iterator = flow.getDefaultFlowStateMappings().iterator();

					mappingLoop: while(iterator.hasNext()){

						DefaultStatusMapping statusMapping = iterator.next();

						//If the action for this status mapping does not exist in this installation skip this mapping
						if(!actionExists(statusMapping.getActionID())){

							log.info("Removing default status mapping for action ID " + statusMapping.getActionID() + " from imported flow since it's supported in this installation.");
							iterator.remove();
							continue;
						}

						if(statusMapping.getStatus() == null){

							log.info("Removing default status mapping for action ID " + statusMapping.getActionID() + " since it has no status set.");
							iterator.remove();
							continue;
						}

						Integer statusID = statusMapping.getStatus().getStatusID();

						for(Status status : flow.getStatuses()){

							if(status.getStatusID().equals(statusID)){

								statusMapping.setStatus(status);

								continue mappingLoop;
							}
						}

						//No matching status found in flow status list, skip this mapping
						log.info("Removing default status mapping for action ID " + statusMapping.getActionID() + " since no matching status could be found.");
						iterator.remove();
					}
				}

				//Clear query descriptor ID's
				if(flow.getSteps() != null){

					for(Step step : flow.getSteps()){

						if(step.getQueryDescriptors() != null){

							for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

								queryDescriptor.setQueryID(null);
							}
						}
					}
				}

				//Clear status ID's
				if(flow.getStatuses() != null){

					for(Status status : flow.getStatuses()){

						status.setStatusID(null);
					}
				}

				//Create transaction
				TransactionHandler transactionHandler = null;

				try{
					transactionHandler = daoFactory.getTransactionHandler();

					//Add flow to database
					if(relatedFlow == null){

						daoFactory.getFlowDAO().add(flow, transactionHandler, ADD_NEW_FLOW_AND_FAMILY_RELATION_QUERY);

					}else{

						daoFactory.getFlowFamilyDAO().update(flow.getFlowFamily(), transactionHandler, null);
						daoFactory.getFlowDAO().add(flow, transactionHandler, ADD_NEW_FLOW_VERSION_RELATION_QUERY);
					}

					//Set target query ID's on evaluator descriptors
					for(Entry<EvaluatorDescriptor, List<QueryDescriptor>> entry : evaluatorTargetQueriesMap.entrySet()){

						List<Integer> targetQueryIDs = new ArrayList<Integer>(entry.getValue().size());

						for(QueryDescriptor queryDescriptor : entry.getValue()){

							targetQueryIDs.add(queryDescriptor.getQueryID());
						}

						entry.getKey().setTargetQueryIDs(targetQueryIDs);

						daoFactory.getEvaluatorDescriptorDAO().update(entry.getKey(), transactionHandler, null);
					}

					HashMap<QueryDescriptor, Query> importedQueryMap = new HashMap<QueryDescriptor, Query>();

					//Import queries using QueryHandler
					if(flow.getSteps() != null){

						for(Step step : flow.getSteps()){

							if(step.getQueryDescriptors() != null){

								for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

									try {
										queryDescriptor.setStep(null);
										importedQueryMap.put(queryDescriptor, queryHandler.importQuery(queryDescriptor, transactionHandler));

									} catch (Exception e) {

										log.error("Error importing query " + queryDescriptor + " of type " + queryDescriptor.getQueryTypeID() + " into flow " + flow + " uploaded by user " + user ,e);

										throw new ValidationException(new QueryImportValidationError(queryDescriptor));
									}
								}
							}
						}
					}

					//Import evaluator using EvaluationHandler
					if(flow.getSteps() != null){

						for(Step step : flow.getSteps()){

							if(step.getQueryDescriptors() != null){

								for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

									if(queryDescriptor.getEvaluatorDescriptors() != null){

										for(EvaluatorDescriptor evaluatorDescriptor : queryDescriptor.getEvaluatorDescriptors()){

											try {
												evaluatorDescriptor.setQueryDescriptor(null);
												evaluationHandler.importEvaluator(evaluatorDescriptor, transactionHandler, importedQueryMap.get(queryDescriptor));

											} catch (Exception e) {

												log.error("Error importing evaluator " + evaluatorDescriptor + " of type " + evaluatorDescriptor.getEvaluatorTypeID() + " into flow " + flow + " uploaded by user " + user ,e);

												throw new ValidationException(new EvaluatorImportValidationError(evaluatorDescriptor));
											}
										}
									}
								}
							}
						}
					}

					//Commit
					transactionHandler.commit();

					log.info("User " + user + " succefully imported flow " + flow);

					eventHandler.sendEvent(Flow.class, new CRUDEvent<Flow>(CRUDAction.ADD, flow), EventTarget.ALL);

					redirectToMethod(req, res, "/showflow/" + flow.getFlowID());

				}finally{

					TransactionHandler.autoClose(transactionHandler);
				}

			}catch(ValidationException e){

				validationException = e;

			}catch(SizeLimitExceededException e){

				validationException = new ValidationException(new RequestSizeLimitExceededValidationError(e.getActualSize(), e.getPermittedSize()));

			}catch(FileSizeLimitExceededException e){

				validationException = new ValidationException(new FileSizeLimitExceededValidationError(e.getFileName(), e.getActualSize(), e.getPermittedSize()));

			}catch(FileUploadException e){

				validationException = new ValidationException(new ValidationError("UnableToParseRequest"));

			}finally{

				if(validationException != null){

					log.info("Import of flow by user " + user + " failed due to validation error(s) " + validationException);
				}

				if(multipartRequest != null){

					multipartRequest.deleteFiles();
				}
			}
		}

		if(relatedFlow != null){

			log.info("User " + user + " requested flow version import form for flow familiy " + relatedFlow.getFlowFamily());

		}else{

			log.info("User " + user + " listing flow import form");
		}

		Document doc = this.createDocument(req, uriParser, user);
		Element importFlowElement = doc.createElement("ImportFlow");
		doc.getFirstChild().appendChild(importFlowElement);

		importFlowElement.appendChild(flowType.toXML(doc));
		XMLUtils.append(doc, importFlowElement, relatedFlow);


		if(validationException != null){
			importFlowElement.appendChild(validationException.toXML(doc));
			importFlowElement.appendChild(RequestUtils.getRequestParameters(req, doc, "categoryID"));
		}

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	private boolean actionExists(String actionID) throws SQLException {

		HighLevelQuery<FlowAction> query = new HighLevelQuery<FlowAction>();

		query.addParameter(flowActionIDParamFactory.getParameter(actionID));

		return daoFactory.getFlowActionDAO().getBoolean(query);
	}

	private List<QueryDescriptor> getTargetQueries(List<Integer> targetQueryIDs, List<Step> steps) {

		List<QueryDescriptor> targetQueries = null;

		for(Step step : steps){

			if(step.getQueryDescriptors() != null){

				for(QueryDescriptor queryDescriptor : step.getQueryDescriptors()){

					if(targetQueryIDs.contains(queryDescriptor.getQueryID())){

						targetQueries = CollectionUtils.addAndInstantiateIfNeeded(targetQueries, queryDescriptor);
					}
				}
			}
		}

		return targetQueries;
	}

	public void appendUserFlowTypes(Document doc, Element targetElement, User user) {

		Element flowTypesElement = doc.createElement("FlowTypes");
		targetElement.appendChild(flowTypesElement);

		for (FlowType flowType : getCachedFlowTypes()) {

			if (AccessUtils.checkAccess(user, flowType)) {

				flowTypesElement.appendChild(flowType.toXML(doc));
			}
		}
	}

	@WebPublic(alias = "users")
	public ForegroundModuleResponse getUsers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getUsers(req, res, user, uriParser);
	}

	@WebPublic(alias = "groups")
	public ForegroundModuleResponse getGroups(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getGroups(req, res, user, uriParser);
	}


	public FlowNotificationHandler getNotificationHandler() {

		return notificationHandler;
	}

	@Override
	public int getPriority() {

		return 0;
	}
}
