package com.imcode.oeplatform.flowengine;

import com.imcode.entities.*;
import com.imcode.entities.embed.Decision;
import com.imcode.oeplatform.flowengine.populators.entity.*;
import com.imcode.oeplatform.flowengine.populators.entity.application.ApplicationPopulator;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldQueryInstance;
import com.imcode.oeplatform.oauth2.modules.foreground.IvisOAuth2User;
import com.imcode.services.*;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.beans.Step;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.enums.EventType;
import com.nordicpeak.flowengine.events.SubmitEvent;
import com.nordicpeak.flowengine.interfaces.*;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import imcode.services.IvisServiceFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import se.unlogic.hierarchy.core.annotations.*;
import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.date.PooledSimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


public class IvisModule extends AnnotatedForegroundModule {

    public static final RelationQuery EVENT_ATTRIBUTE_RELATION_QUERY = new RelationQuery(FlowInstanceEvent.ATTRIBUTES_RELATION);

    private static final PooledSimpleDateFormat DATE_TIME_FORMATTER = new PooledSimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
    private static final String DEFAULT_ACCESS_TOKEN_URI = "/oauth/token";
    private static final String CLIENT_CONTEXT_PARAM = "OAuth2ClientContext";

    @ModuleSetting(allowsNull = true)
    @TextAreaSettingDescriptor(name = "Supported actionID's", description = "The action ID's which will trigger export XML to be generated and stored when a submit event is detected")
    protected List<String> supportedActionIDs;

    @ModuleSetting
    @TextFieldSettingDescriptor(name = "OAUTH2 token", description = "Registred OAUTH2 token")
    protected String oauthToken;

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
    @TextFieldSettingDescriptor(name = "Server username", description = "iVIS server username to retrive access token", required = true)
    protected String username;

    @ModuleSetting
    @PasswordSettingDescriptor(name = "Server password", description = "iVIS server user password", required = true)
    protected String password;

    @ModuleSetting(allowsNull = true)
    @TextAreaSettingDescriptor(name = "Flow instance to Application mapping", description = "", required = false)
    protected String flowToApplicationMapping;


    @InstanceManagerDependency(required = true)
    protected QueryHandler queryHandler;

    private FlowEngineDAOFactory daoFactory;

//    private IvisServiceFactory ivisServiceFactory;
//
//    private Map<String, String> fieldComplienceMap = new LinkedHashMap<String, String>();

    @Override
    public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {
        super.init(moduleDescriptor, sectionInterface, dataSource);
    }


    @Override
    public void unload() throws Exception {

//		if(this.equals(systemInterface.getInstanceHandler().getInstance(XMLProvider.class))){
//
//			systemInterface.getInstanceHandler().removeInstance(XMLProvider.class);
//		}

        super.unload();
    }

    @Override
    protected void createDAOs(DataSource dataSource) throws Exception {

        daoFactory = new FlowEngineDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());
    }

    @EventListener(channel = FlowInstanceManager.class, priority = 51)
    public void processEvent(SubmitEvent event, EventSource eventSource) throws Exception {
        IvisOAuth2User user = null;
//        se.unlogic.hierarchy.core.beans.User editor = event.getEvent().getFlowInstance().getPoster();
        if (event.getEvent().getFlowInstance().getPoster() instanceof IvisOAuth2User) {
            user = (IvisOAuth2User) event.getEvent().getFlowInstance().getPoster();
        }else if (event.getEvent().getFlowInstance().getEditor() instanceof IvisOAuth2User) {
            user = (IvisOAuth2User) event.getEvent().getFlowInstance().getEditor();
        } else {
            log.warn("User \"" + user + "\" is not authorized in iVIS/");
            return;
        }

        if (this.supportedActionIDs == null) {

            log.warn("Module " + this.moduleDescriptor + " not properly configured, can not connect to server");
        }

        if (event.getEvent().getEventType() != EventType.SUBMITTED || event.getActionID() == null || !supportedActionIDs.contains(event.getActionID()) || user == null) {

            return;
        }

        final FlowInstanceManager flowInstanceManager = event.getFlowInstanceManager();
        ImmutableFlowInstance flowInstance = flowInstanceManager.getFlowInstance();

        log.info("Creating Application instance for flow instance " + flowInstance);

        Application application = new Application();

        //Уточнить насчет пользователя
//        application.setSubmittedUser();
//        application.setHandledUser();
//        application.setRegardingUser();
        application.setRegistrationNumber(flowInstance.getFlowInstanceID().longValue());
        application.setDecision(new Decision());

        ApplicationFormFactory factory = new ApplicationFormFactory();
//        factory.addMapper(new TextFieldQuestionMapper());
//        factory.addMapper(new FieldQuestionMapper());
        factory.addMapper(FieldQuestionMapper.forClass(TextFieldQueryInstance.class));
        factory.addMapper(FieldQuestionMapper.forClass(com.imcode.oeplatform.flowengine.queries.textfieldquery2.TextFieldQueryInstance.class));
        factory.addMapper(new SingleAlternativeQuestionMapper());
        factory.addMapper(new MultipleAlternativeQuestionMapper());
        factory.addMapper(new TextAreaQuestionMapper());

        populateSteps(flowInstance);


//        try {
            ApplicationForm form = factory.get(flowInstanceManager);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        GenericConversionService conversionService = new DefaultConversionService();
//        conversionService.addConverter(String.class, Boolean.class, (String source) -> !source.trim().isEmpty());
//        conversionService.addConverter(new IdToEntityConverter(conversionService));
////        GenericConversionService conversionService = new GenericConversionService();
////        conversionService.addConverter(String.class, Boolean.class, (String source) -> !source.trim().isEmpty());
////        ToEntityConverter<GenericConversionService> cs = new ToEntityConverter<>(conversionService);
////        conversionService.addConverterFactory(new IvisConverterFactory(ivisServiceFactory));
//
//        PropertyValues values = new FlowToPropertyValuesConverter(flowToApplicationMapping).convert(event.getFlowInstanceManager());
//
//        DataBinder binder = new DataBinder(application);
//        binder.setConversionService(conversionService);
//        binder.bind(values);
        IvisServiceFactory ivisServiceFactory = user.getServiceFactory();
//        ApplicationService applicationService = ivisServiceFactory.getService(ApplicationService.class);
//        ApplicationFormService formService = ivisServiceFactory.getService(ApplicationFormService.class);
//        ApplicationFormStepService stepService = ivisServiceFactory.getService(ApplicationFormStepService.class);
//        ApplicationFormQuestionGroupService groupService = ivisServiceFactory.getService(ApplicationFormQuestionGroupService.class);
//        ApplicationFormQuestionService questionService = ivisServiceFactory.getService(ApplicationFormQuestionService.class);

//        form = formService.save(form);
        application.setApplicationForm(form);
        saveApplication(application, ivisServiceFactory);
//        application = applicationService.save(application);

//        log.info("Application id = " + application.getId() + " created.");

    }

    private void saveApplication1(Application application, IvisServiceFactory ivisServiceFactory) {
        ApplicationPopulator.saveObjectToFile(application, "/home/vitaly/SkypeFiles/Application.dat");
////        ivisServiceFactory = ApplicationPopulator.localIvisServiceFactory();
//        ApplicationService applicationService = ivisServiceFactory.getService(ApplicationService.class);
//        ApplicationFormService formService = ivisServiceFactory.getService(ApplicationFormService.class);
//        ApplicationFormStepService stepService = ivisServiceFactory.getService(ApplicationFormStepService.class);
//        ApplicationFormQuestionGroupService groupService = ivisServiceFactory.getService(ApplicationFormQuestionGroupService.class);
//        ApplicationFormQuestionService questionService = ivisServiceFactory.getService(ApplicationFormQuestionService.class);
//
//        try {
//            applicationService.find(0L);
//            ApplicationForm newForm = application.getApplicationForm();
//            ApplicationForm form = formService.save(newForm);
//            application.setApplicationForm(form);
//            application = applicationService.save(application);
//
////        form.getSteps().stream().peek(step -> {step.setApplicationForm(form); stepService.save(step);})
//            for (ApplicationFormStep newStep :form.getSteps()) {
//                newStep.setApplicationForm(form);
//                ApplicationFormStep step = stepService.save(newStep);
//                for (ApplicationFormQuestionGroup newQuestionGroup :newStep.getQuestionGroups()) {
//                    newQuestionGroup.setStep(step);
//                    ApplicationFormQuestionGroup questionGroup = groupService.save(newQuestionGroup);
//                    for (ApplicationFormQuestion newQuestion :newQuestionGroup.getQuestions()) {
//                        newQuestion.setQuestionGroup(questionGroup);
//                        questionService.save(newQuestion);
//                    }
//
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e);
//        }
//
//
////        List<ApplicationFormStep> steps = form.getSteps();
////        for (int s = 0; s < steps.size(); s++) {
////            ApplicationFormStep step = steps.get(s);
////            List<ApplicationFormQuestionGroup> questionGroups = step.getQuestionGroups();
////            for (int g = 0; g < questionGroups.size(); g++) {
////                ApplicationFormQuestionGroup questionGroup = questionGroups.get(g);
////                List<ApplicationFormQuestion> questions = questionGroup.getQuestions();
////                for (int q = 0; q < questions.size(); q++) {
////                    ApplicationFormQuestion question = questions.get(q);
////                    question = questionService.save(question);
////                    questions.set(q, question);
////                }
////                questionGroup = groupService.save(questionGroup);
////                questionGroups.set(g, questionGroup);
////            }
////            step = stepService.save(step);
////            steps.set(s, step);
////        }
//        application = applicationService.find(application.getId());
//        log.info("Application id = " + application.getId() + " created.");
    }

    private void populateSteps(ImmutableFlowInstance flowInstance) {
        @SuppressWarnings("unchecked")
        List<Step> steps = (List<Step>) flowInstance.getFlow().getSteps();
        for (Step step :steps) {
            List<QueryDescriptor> queryDescriptors = step.getQueryDescriptors();
            for (QueryDescriptor descriptor :queryDescriptors) {
                descriptor.setStep(step);
            }

        }
    }

//    public Map<Integer, Field> getFieldList(FlowInstanceManager flowInstanceManager) throws Exception {
//
//        Map<Integer, Field> map = new LinkedHashMap<Integer, Field>();
////        List<Field> fieldList = new LinkedList<Field>();
//        List<ImmutableQueryInstance> queryInstances = flowInstanceManager.getQueries(ImmutableQueryInstance.class);
//
//        for (ImmutableQueryInstance immutableQueryInstance : queryInstances) {
//            if (immutableQueryInstance instanceof LabelFieldQueryInstance) {
//                List<Field> fieldList = Field.fromTextFieldQueryInstance((LabelFieldQueryInstance) immutableQueryInstance);
//                for (Field field : fieldList) {
//                    map.put(field.getId(), field);
//                }
//
//            } else if (immutableQueryInstance instanceof CheckboxQueryInstance) {
//                List<Field> fieldList = Field.fromChecBoxQueryInstance((CheckboxQueryInstance) immutableQueryInstance);
//                for (Field field : fieldList) {
//                    map.put(field.getId(), field);
//                }
//
//            } else if (immutableQueryInstance instanceof RadioButtonQueryInstance) {
//                Field field = new Field((RadioButtonQueryInstance) immutableQueryInstance);
//                map.put(field.getId(), field);
//            } else if (immutableQueryInstance instanceof DropDownQueryInstance) {
//                Field field = new Field((DropDownQueryInstance) immutableQueryInstance);
//                map.put(field.getId(), field);
//            }
//
//        }
//
//
////        for(ImmutableStep managedStep : flowInstance.getFlow().getSteps()){
////
////            for(ImmutableQueryInstance queryInstance : managedStep.getQueryInstances()){
//////
//////                if(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().isExported() && queryInstance.getQueryInstanceDescriptor().isPopulated()){
//////
////////                    Element queryElement = queryInstance.toExportXML(doc, queryHandler);
//////
////////                    if(queryElement != null){
//////
//////                        map.put(queryInstance.toString(), new Object());
////////                    }
//////                }
//////            }
////            System.out.println("sdfas");
////        }
//
//        return map;
//    }

//    public static class FieldMap {
//        private Map<Integer, Field> map;
//
//        public FieldMap() {
//        }
//
//        public FieldMap(Map<Integer, Field> map) {
//            this.map = map;
//        }
//
//        public Object getValue(int id) {
//            Field field = map.get(id);
//
//            if (field != null) {
//                return field.getValue();
//            }
//
//            return null;
//        }
//
//        public String getStringValue(int id) {
//            return (String) getValue(id);
//        }
//
//        public Integer getIntegerValue(int id) {
//            String value = getStringValue(id);
//
//            if (value != null) {
//                try {
//                    return Integer.parseInt(value.trim().replace(" ", ""));
//                } catch (NumberFormatException ignore) {
//                }
//            }
//
//            return null;
//        }
//
//        public boolean hasField(int id) {
//            return map.get(id) != null;
//        }
//
//        public Map<Integer, Field> getMap() {
//            return map;
//        }
//
//        public void setMap(Map<Integer, Field> map) {
//            this.map = map;
//        }
//    }

//    public static class Field {
//        private int id;
//        private Map<Integer, Object> values = new LinkedHashMap<>();
//        private String name;
//        private String description;
//        private Object value;
//        private ImmutableQueryInstance queryInstance;
//
//        public Field() {
//        }
//
//        public Field(int id, String name, String description, Object value) {
//            this.id = id;
//            this.name = name;
//            this.description = description;
//            this.value = value;
//        }
//
//        public Field(RadioButtonQueryInstance queryInstance) {
//            this.queryInstance = queryInstance;
//            this.id = queryInstance.getQuery().getQueryID();
//            this.name = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getName();
//            this.description = "";
//            this.value = queryInstance.getAlternative().getName();
//
//            if (queryInstance.getAlternatives() != null) {
//                for (ImmutableAlternative alternative : queryInstance.getAlternatives()) {
//                    this.values.put(alternative.getAlternativeID(), alternative.getName());
//                }
//            }
//        }
//
////        public Field(CheckboxQueryInstance queryInstance) {
////            this.queryInstance = queryInstance;
////            this.id = queryInstance.getQuery().getQueryID();
////            this.name = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getName();
////            this.description = "";
//////            this.value = queryInstance.getAlternative().getName();
////
////            for (ImmutableAlternative alternative : queryInstance.getAlternatives()) {
////                this.values.put(alternative.getAlternativeID(), alternative.getName());
////            }
////        }
//
//        public Field(DropDownQueryInstance queryInstance) {
//            this.queryInstance = queryInstance;
//            this.id = queryInstance.getQuery().getQueryID();
//            this.name = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getName();
//            this.description = "";
//
//            if (queryInstance.getAlternatives() != null) {
//                for (ImmutableAlternative alternative : queryInstance.getAlternatives()) {
//                    this.values.put(alternative.getAlternativeID(), alternative.getName());
//                }
//            }
//
//            if (queryInstance.getAlternative() != null) {
//                this.value = queryInstance.getAlternative().getName();
//            }
//        }
//
//        public Field(LabelFieldQueryInstance queryInstance, int valueIndex) {
//            this.queryInstance = queryInstance;
//            LabelFieldValue textFieldValue = queryInstance.getValues().get(valueIndex);
////            this.id = textFieldValue.getInstance().getQuery().getQueryID();
//            this.id = textFieldValue.getTextField().getTextFieldID();
//            this.name = textFieldValue.getTextField().getLabel();
//            this.description = "";
//            this.value = textFieldValue.getValue();
//            this.values.put(textFieldValue.getTextFieldValueID(), textFieldValue.getValue());
//        }
//
//        public Field(CheckboxAlternative queryInstance) {
////            this.queryInstance = queryInstance;
////            CheckboxExportedMutableField queryInstance = queryInstance.getValues().get(valueIndex);
////            this.id = textFieldValue.getInstance().getQuery().getQueryID();
//            this.id = queryInstance.getAlternativeID();
//            this.name = queryInstance.getName();
//            this.description = "";
//            this.value = queryInstance.getName();
////            this.values.put(alternative.getTextFieldValueID(), alternative.getValue());
//        }
//
//        public static List<Field> fromTextFieldQueryInstance(LabelFieldQueryInstance queryInstance) {
//            List<Field> fields = new ArrayList<Field>();
//            List<LabelFieldValue> values = queryInstance.getValues();
//
//            if (values != null) {
//                for (int i = 0; i < values.size(); i++) {
//                    fields.add(new Field(queryInstance, i));
//                }
//            }
//
//            return fields;
//        }
//
//        public static List<Field> fromChecBoxQueryInstance(CheckboxQueryInstance queryInstance) {
//            List<Field> fields = new ArrayList<Field>();
//            List<CheckboxAlternative> alternatives = queryInstance.getAlternatives();
//
//            if (alternatives != null) {
//                for (CheckboxAlternative alternative : alternatives) {
//                    fields.add(new Field(alternative));
//                }
//            }
//
//            return fields;
//        }
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//
//        public void setDescription(String description) {
//            this.description = description;
//        }
//
//        public Object getValue() {
//            return value;
//        }
//
//        public void setValue(Object value) {
//            this.value = value;
//        }
//
//        public ImmutableQueryInstance getQueryInstance() {
//            return queryInstance;
//        }
//
//        public void setQueryInstance(ImmutableQueryInstance queryInstance) {
//            this.queryInstance = queryInstance;
//        }
//
//        public Map<Integer, Object> getValues() {
//            return values;
//        }
//
//        public void setValues(Map<Integer, Object> values) {
//            this.values = values;
//        }
//    }

    public List<String> getSupportedActionIDs() {
        return supportedActionIDs;
    }

    public void setSupportedActionIDs(List<String> supportedActionIDs) {
        this.supportedActionIDs = supportedActionIDs;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public QueryHandler getQueryHandler() {
        return queryHandler;
    }

    public void setQueryHandler(QueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    public FlowEngineDAOFactory getDaoFactory() {
        return daoFactory;
    }

    public void setDaoFactory(FlowEngineDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

//    public IvisServiceFactory getIvisServiceFactory() {
//        return ivisServiceFactory;
//    }
//
//    public void setIvisServiceFactory(IvisServiceFactory ivisServiceFactory) {
//        this.ivisServiceFactory = ivisServiceFactory;
//    }

    private OAuth2ClientContext getClientContext(HttpServletRequest request) {

        Object value = request.getSession().getAttribute(CLIENT_CONTEXT_PARAM);

        if (value instanceof OAuth2ClientContext) {
            return (OAuth2ClientContext) value;
        }

        return null;
    }

    private void saveApplication(Application application, IvisServiceFactory ivisServiceFactory) {
//        application = ApplicationPopulator.loadObjectFromFile("/home/vitaly/SkypeFiles/Application.dat");
//        ivisServiceFactory = ApplicationPopulator.localIvisServiceFactory();
        ApplicationService applicationService = ivisServiceFactory.getService(ApplicationService.class);
        ApplicationFormService formService = ivisServiceFactory.getService(ApplicationFormService.class);
        ApplicationFormStepService stepService = ivisServiceFactory.getService(ApplicationFormStepService.class);
        ApplicationFormQuestionGroupService groupService = ivisServiceFactory.getService(ApplicationFormQuestionGroupService.class);
        ApplicationFormQuestionService questionService = ivisServiceFactory.getService(ApplicationFormQuestionService.class);
        EntityVersionService versionService = ivisServiceFactory.getService(EntityVersionService.class);

        try {
            ApplicationForm newForm = application.getApplicationForm();
            ApplicationForm form = formService.save(newForm);
            application.setApplicationForm(form);
            application = applicationService.save(application);
            List<ApplicationFormStep> steps = form.getSteps().stream().peek(step1->step1.setApplicationForm(form)).collect(toList());
            Iterator<ApplicationFormStep> savedSteps = stepService.save(steps).iterator();
            for (ApplicationFormStep step :steps) {
                ApplicationFormStep savedStep = savedSteps.next();
                step.getQuestionGroups().stream().forEach(group->group.setStep(savedStep));
            }

            List<ApplicationFormQuestionGroup> groups = steps.stream().flatMap(step->step.getQuestionGroups().stream()).collect(toList());
            Iterator<ApplicationFormQuestionGroup> savedGroups = groupService.save(groups).iterator();

            for (ApplicationFormQuestionGroup group :groups) {
                ApplicationFormQuestionGroup savedGroup = savedGroups.next();
                group.getQuestions().stream().forEach(question->question.setQuestionGroup(savedGroup));
            }

            List<ApplicationFormQuestion> questions = groups.stream().flatMap(group->group.getQuestions().stream()).collect(toList());
            Iterable<ApplicationFormQuestion> savedQuestions = questionService.save(questions);

            EntityVersion version = new EntityVersion(application);
            version.setTimestamp(application.getCreateDate());
            version = versionService.save(version);
            log.info("Application");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        application = applicationService.find(application.getId());
        log.info("Application id = " + application.getId() + " created.");
    }

    public static void main(String[] args) throws Exception {
//        saveApplication1(null, null);
//        FlowInstanceManager manager = ApplicationPopulator.loadFlowInstanceManagerFromFile("/home/vitaly/Загрузки/manager.dat");
//        ImmutableFlowInstance flowInstance = manager.getFlowInstance();
//
////        log.info("Creating Application instance for flow instance " + flowInstance);
////
//        Application application = new Application();
////        IvisServiceFactory ivisServiceFactory = user.getServiceFactory();
//
//        GenericConversionService conversionService = new DefaultConversionService();
//        conversionService.addConverter(String.class, Boolean.class, (String source) -> !source.trim().isEmpty());
//        conversionService.addConverter(new IdToEntityConverter(conversionService));
////        GenericConversionService conversionService = new GenericConversionService();
////        conversionService.addConverter(String.class, Boolean.class, (String source) -> !source.trim().isEmpty());
////        ToEntityConverter<GenericConversionService> cs = new ToEntityConverter<>(conversionService);
////        conversionService.addConverterFactory(new IvisConverterFactory(ivisServiceFactory));
//
//        PropertyValues values = new FlowToPropertyValuesConverter(flowToApplicationMapping).convert(event.getFlowInstanceManager());
//
//        DataBinder binder = new DataBinder(application);
//        binder.setConversionService(conversionService);
//        binder.bind(values);
//
//        ApplicationService applicationService = ivisServiceFactory.getService(ApplicationService.class);
//
//        application = applicationService.save(application);
//        log.info("Application id = " + application.getId() + " created.");

//        GenericConversionService conversionService = new DefaultConversionService();
//        conversionService.addConverter(String.class, Boolean.class, (String source) -> !source.trim().isEmpty());
//        conversionService.addConverter(new IdToEntityConverter(conversionService));
//        ToEntityConverter cs = new ToEntityConverter(conversionService);
//        java.lang.reflect.Field field = Application.class.getDeclaredField("pupil");
//        System.out.println(conversionService.convert("5", new TypeDescriptor(field)));
//        IvisModule ivisModule = new IvisModule();
//        ivisModule.setServerUrl("http://localhost:8080/ivis");
//        ivisModule.setClientId("b4251265-409d-43b3-928d-a290228a2b59");
//        ivisModule.setClientSecret("secret");
//        ivisModule.setUsername("admin");
//        ivisModule.setPassword("pass");
//        ivisModule.setSupportedActionIDs(Arrays.asList("com.nordicpeak.flowengine.FlowBrowserModule.submit"));
////        ivisModule.set();
////        ivisModule.initializeIvisServiceFactory();
//
//        ApplicationService applicationService = ivisModule.ivisServiceFactory.getService(ApplicationService.class);
//        List<Application> applicationList = applicationService.findAll();
    }
}


