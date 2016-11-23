package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.User;
import com.imcode.services.UserService;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import imcode.services.IvisServiceFactory;
import imcode.services.restful.ProxyIvisServiceFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by vitaly on 09.09.15.
 */
@Deprecated
public class ApplicationPopulator {
//    private static final String ROW_SPLITER = "\n";
//    private static final String PAIR_SPLITER = "[=]";
//    private static final String VALUE_SPLITER = "[:]";
//    private static final Class<Application> APP_CLASS = Application.class;
//
//    private List<Entry> entries = new LinkedList<>();
//
//    static class Entry<T> {
//        private final String questionName;
//        private final Method setter;
//        private final ValueResolver<T> resolver;
//
//        Entry(String questionName, Method setter, ValueResolver resolver) {
//            this.questionName = questionName;
//            this.setter = setter;
//            this.resolver = resolver;
//        }
//
//        public String getQuestionName() {
//            return questionName;
//        }
//
//        public void applyValue(Application application, String rawValue) {
//            Object value = resolver.apply(rawValue);
//            try {
//                setter.invoke(application, value);
//            } catch (Exception ignore) {
//            }
//        }
//    }
//
//    private <T> void addEntry(String questionName, String fieldName, ValueResolver<T> valueResolver) {
//        Method setter = getSetter(fieldName);
//        Entry<T> entry = new Entry<>(questionName, setter, valueResolver);
//        entries.add(entry);
//    }
//
//    public Application create(ApplicationForm form) {
//        Application application = new Application();
//
//        pupulate(application, form);
//
//        return application;
//    }
//
//    public void pupulate(Application application, ApplicationForm form) {
//        for (Entry entry : entries) {
//            String questionName = entry.getQuestionName();
//            String rawValue = form.getValue(questionName);
//            if (rawValue != null) {
//                entry.applyValue(application, rawValue);
//            }
//        }
//    }
//
//    private static class SimplePair<K, V> extends AbstractMap.SimpleEntry<K, V> {
//        public SimplePair(K key, V value) {
//            super(key, value);
//        }
//
//        public SimplePair(Map.Entry<? extends K, ? extends V> entry) {
//            super(entry);
//        }
//
//        public static SimplePair<String, String> fromString(String s) {
//            return fromString(s, ",");
//        }
//        public static SimplePair<String, String> fromString(String s, String spliter) {
//            Objects.requireNonNull(s ,"input string can not be null!");
//            String[] values = s.split(spliter);
//            if (values.length != 2) {
//                throw new IllegalArgumentException("\"" + s + "\" is illegal pair(" + spliter + ") expression!");
//            }
//
//            return new SimplePair<String, String>(values[0], values[1]);
//        }
//    }
//
//    private static Map<String, SimplePair<String, String>> mapGroups(String roleGroupString) {
//        Map<String, SimplePair<String, String>> map = new HashMap<>();
//
//        if (roleGroupString != null && !roleGroupString.isEmpty()) {
//            String[] pairStrings = roleGroupString.split(ROW_SPLITER);
//            for (String pairString : pairStrings) {
//                SimplePair<String, String> pair = SimplePair.fromString(pairString, PAIR_SPLITER);
//                map.put(pair.getKey(), SimplePair.fromString(pair.getValue(), VALUE_SPLITER));
//            }
//        }
//
//        return map;
//    }
//
//    private static class IvisValueResolverFactory implements ValueResolverFactory {
//        private final IvisServiceFactory serviceFactory;
//        private final Map<Class, ValueResolver> simpleValueResolvers;
//
//        IvisValueResolverFactory(IvisServiceFactory serviceFactory) {
//            this.serviceFactory = serviceFactory;
//            simpleValueResolvers = new HashMap<>();
//            addValueResolverHelper(String.class, String::trim);
//            addValueResolverHelper(Integer.class, Integer::valueOf);
//            addValueResolverHelper(Boolean.class, s -> !s.trim().isEmpty());
//        }
//
//        private <T> void addValueResolverHelper(Class<T> clazz, ValueResolver<? super T> valueResolver) {
//            simpleValueResolvers.put(clazz, valueResolver);
//        }
//
//        @Override
//        @SuppressWarnings("unchecked")
//        public <T> ValueResolver<T> getValueResolver(Class<T> entityClass) {
//            if (JpaPersonalizedEntity.class.isAssignableFrom(entityClass)) {
//                PersonalizedService<T> service = serviceFactory.getServiceFor(entityClass);
//                return new PersonalizedVlueResolver(service);
//            }
//
//            if (JpaNamedEntity.class.isAssignableFrom(entityClass)) {
//                NamedService<T> service = serviceFactory.getServiceFor(entityClass);
//                return new NamedVlueResolver(service);
//            }
//
//            ValueResolver<T> defaultValueResolver = simpleValueResolvers.get(entityClass);
//
//            if (defaultValueResolver == null)
//                throw new IllegalArgumentException("No such valuet resolver for class \"" + entityClass + "\"");
//
//            return defaultValueResolver;
//        }
//    }
//
//    public static ApplicationPopulator fromString(IvisServiceFactory factory, String options) {
//        ApplicationPopulator populator = new ApplicationPopulator();
//
//        if (options != null) {
//            Map<String, SimplePair<String, String>> map = mapGroups(options);
//            if (!map.isEmpty()) {
//                ValueResolverFactory resolverFactory = new IvisValueResolverFactory(factory);
//
//                for (Map.Entry<String, SimplePair<String, String>> entry :map.entrySet()) {
//                    String fieldName = entry.getKey().trim();
//                    Class<?> fieldType = getFieldType(fieldName);
//                    ValueResolver<?> valueResolver = resolverFactory.getValueResolver(fieldType);
//                    populator.addEntry(entry.getValue().getValue(), fieldName, valueResolver);
//                }
//            }
//        }
//
//        return populator;
//    }
//
//    private static Method getSetter(String fieldName) {
//        StringBuilder sb = new StringBuilder("set")
//                .append(Character.toUpperCase(fieldName.charAt(0)))
//                .append(fieldName, 1, fieldName.length());
//        String setterName = sb.toString();
//
//        try {
//            Class fieldType = getFieldType(fieldName);
//            return APP_CLASS.getMethod(setterName, fieldType);
//        } catch (NoSuchMethodException e) {
//            throw new IllegalArgumentException("Setter for field \"" + fieldName + "\" not found!");
//        }
//    }
//
//    private static Class getFieldType(String fieldName) {
//        StringBuilder sb = new StringBuilder("get")
//                .append(Character.toUpperCase(fieldName.charAt(0)))
//                .append(fieldName, 1, fieldName.length());
//        String getterName = sb.toString();
//        Method getter = null;
//        try {
//            getter = APP_CLASS.getMethod(getterName);
//        } catch (NoSuchMethodException e) {
//            throw new IllegalArgumentException("Getter for field \"" + fieldName + "\" not found!");
//        }
//
//        return getter.getReturnType();
//    }
//

    //////////////////////////////////////////////////////////Dummy TEST//////////////////////////////////////////////////////////
    public static OAuth2ProtectedResourceDetails simpleResourceDetails(String serverUrl, String clientId) {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setId("ivis");
        resource.setClientId(clientId);
        resource.setGrantType("password");
        resource.setClientSecret("secret");
        resource.setAccessTokenUri(serverUrl + "/oauth/token");
        resource.setScope(Arrays.asList("read"));
        resource.setUsername("admin");
        resource.setPassword("pass");

        return resource;
    }

    public static IvisServiceFactory createIvisServiceFactory(String serverUrl, OAuth2ProtectedResourceDetails resource) {
        OAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
        String apiPath = "/api/v1/json";
        ProxyIvisServiceFactory factory = new ProxyIvisServiceFactory(serverUrl + apiPath, clientContext, resource);
//        factory.setFavoriteServiceList(Collections.emptyList());
        factory.initialize();

        return factory;
    }

    public static IvisServiceFactory remoteIvisServiceFactory() {
        final String serverUrl = "http://ivis.dev.imcode.com";
        OAuth2ProtectedResourceDetails resouce = simpleResourceDetails(serverUrl, "08d32c33-91cf-4452-8be8-4d120fbc504e");

        return createIvisServiceFactory(serverUrl, resouce);
    }

    public static IvisServiceFactory localIvisServiceFactory() {
        final String serverUrl = "http://localhost:8080";
        OAuth2ProtectedResourceDetails resouce = simpleResourceDetails(serverUrl, "ff11397c-3e3b-4398-80a9-feba203f1928");

        return createIvisServiceFactory(serverUrl, resouce);
    }

    public static void saveFlowInstanceManagerToFile(FlowInstanceManager manager, String fileName) {
        saveObjectToFile(manager, fileName);
    }

    public static void saveObjectToFile(Object obj, String fileName) {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            stream.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FlowInstanceManager loadFlowInstanceManagerFromFile(String fileName) {
        return loadObjectFromFile(fileName);
    }

    @SuppressWarnings("unchecked")
    public static<T> T loadObjectFromFile(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));) {
            return (T) inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Converter<String, T> converter(Class<T> targetClass, Converter<String, T> converter) {
        return converter;
    }




    public static void main(String[] args) {
//        StringBuilder sb = new StringBuilder()
//                .append("byMobilPhone")                         .append('=').append("SchoolTransportSecurity.SchoolTransportSecurityStudentHasPermobil").append('\n')
//                .append("shorty")                               .append('=').append("SchoolTransportSecurity.SchoolTransportSecurityStudentShorter135").append('\n')
//                .append("wheelchair")                           .append('=').append("SchoolTransportSecurity.SchoolTransportSecurityStudentHasWheelchair").append('\n')
//                .append("accompanyingAssistant")                .append('=').append("SchoolTransportSecurity.SchoolTransportSecurityStudentHasFollower").append('\n')
//                .append("reasone")                              .append('=').append("AditionalInfo.text").append('\n')
//                .append("address.postalCod")                    .append('=').append("StudentRegAddr.StudentRegAddrZip").append('\n')
//                .append("address.city")                         .append('=').append("StudentRegAddr.StudentRegAddrCity").append('\n')
//                .append("address.street")                       .append('=').append("StudentRegAddr.StudentRegAddrStreet1").append('\n')
////                .append("schoolTransport")                      .append('=').append("MeansOfConveyance.MeansOfConveyanceSchoolBus").append('\n')
//                .append("guardian")                             .append('=').append("RequestingGuardian.RequestingGuardianPersId").append('\n')
////                .append("pupil")                                .append('=').append("Student.StudentPersonalIdNumber").append('\n')
//                .append("schoolTransportSchema[0].dayOfWeek")   .append('=').append("#MONDAY").append('\n')
//                .append("schoolTransportSchema[0].to")          .append('=').append("SchoolTransportToDays.SchoolTransportToDaysMonday").append('\n')
//                .append("schoolTransportSchema[0].from")        .append('=').append("SchoolTransportFromDays.SchoolTransportFromDaysMonday").append('\n')
//                .append("schoolTransportSchema[1].dayOfWeek")   .append('=').append("#TUESDAY").append('\n')
//                .append("schoolTransportSchema[1].to")          .append('=').append("SchoolTransportToDays.SchoolTransportToDaysTuesday").append('\n')
//                .append("schoolTransportSchema[1].from")        .append('=').append("SchoolTransportFromDays.SchoolTransportFromDaysTuesday").append('\n')
//                .append("schoolTransportSchema[2].dayOfWeek")   .append('=').append("#WEDNESDAY").append('\n')
//                .append("schoolTransportSchema[2].to")          .append('=').append("SchoolTransportToDays.SchoolTransportToDaysWednesday").append('\n')
//                .append("schoolTransportSchema[2].from")        .append('=').append("SchoolTransportFromDays.SchoolTransportFromDaysWednesday").append('\n')
//                .append("schoolTransportSchema[3].dayOfWeek")   .append('=').append("#THURSDAY").append('\n')
//                .append("schoolTransportSchema[3].to")          .append('=').append("SchoolTransportToDays.SchoolTransportToDaysThursday").append('\n')
//                .append("schoolTransportSchema[3].from")        .append('=').append("SchoolTransportFromDays.SchoolTransportFromDaysThursday").append('\n')
//                .append("schoolTransportSchema[4].dayOfWeek")   .append('=').append("#FRIDAY").append('\n')
//                .append("schoolTransportSchema[4].to")          .append('=').append("SchoolTransportToDays.SchoolTransportToDaysFriday").append('\n')
//                .append("schoolTransportSchema[4].from")        .append('=').append("SchoolTransportFromDays.SchoolTransportFromDaysFriday").append('\n')
//                .append("schoolTransport")                      .append('=').append("SchoolTransport.value").append('\n')
//                .append("pupil")                                .append('=').append("pupil.value").append('\n')
//                .append("status").append('=').append("#created").append('\n');
////                .append("otherInfo")                             .append('=').append("SchoolTransporOtherInfo.SchoolTransporOtherInfoField").append('\n')
////                .append("").append('=').append("").append('\n')
//
        IvisServiceFactory serviceFactory = localIvisServiceFactory();
        UserService userService = serviceFactory.getService(UserService.class);
        User user = userService.getCurrentUser();
        System.out.println(user);
//        FlowInstanceManager manager = loadFlowInstanceManagerFromFile("/home/vitaly/IdeaProjects/FlowInstanceManager10.dat");
//        FlowInstanceManager manager = loadFlowInstanceManagerFromFile("/home/vitaly/Загрузки/manager.dat");
//        Application app = new Application();
//
////        GenericConversionService conversionService = new GenericConversionService();
////        conversionService.addConverter(String.class, Boolean.class, (String source) -> !source.trim().isEmpty());
////        conversionService.addConverterFactory(new IvisConverterFactory(serviceFactory));
//        GenericConversionService conversionService = new DefaultConversionService();
//        conversionService.addConverter(String.class, Boolean.class, (String source) -> !source.trim().isEmpty());
//        conversionService.addConverter(new IdToEntityConverter(conversionService));
//
//
//        PropertyValues values = new FlowToPropertyValuesConverter(sb.toString()).convert(manager);
//
//        DataBinder binder = new DataBinder(app);
//        binder.setConversionService(conversionService);
//        binder.bind(values);
////        List<PropertyValue> values
////        MutablePropertyValues values = new MutablePropertyValues();
////        values.add("byMobilPhone", "trwrtewue");
////        values.add("reasone", "trwrtewue");
////        values.add("reasone", "33333");
////        values.add("address.postalCode", "04114");
////        values.add("address.city", "Kiev");
////        values.add("address.street", "Krasnotkatskaya, 10");
////        values.add("schoolTransport", "Bus");
////        values.add("guardian", "510725-8286");
////        values.add("pupil", "820214-3213");
////        values.add("schoolTransportSchema[0].dayOfWeek", "MONDAY");
////        values.add("schoolTransportSchema[0].to", "");
////        values.add("schoolTransportSchema[0].from", "asdfasdf");
////        values.add("schoolTransportSchema.", "");
//        binder.bind(values);
//        System.out.println(app.getSchoolTransportSchema());
//////        IvisServiceFactory serviceFactory = localIvisServiceFactory();
////        FlowInstanceManager manager = loadFlowInstanceManagerFromFile("/home/vitaly/IdeaProjects/FlowInstanceManager9.dat");
////        ApplicationForm form  = new ApplicationFormFactory().createApplicationForm(manager);
////        IvisServiceFactory serviceFactory = remoteIvisServiceFactory();
////        System.out.println(form);
//
////        Student.StudentFirstName = VITALIY
////        Student.StudentFamilyName = SEREDA
////        Student.StudentPersonalIdNumber = 820214-3213
////        StudentSchool.School2 = Norrbackaskolan
////        StudentRegAddr.StudentRegAddrZip = 04114
////        StudentRegAddr.StudentRegAddrCity = Kiev
////        StudentRegAddr.StudentRegAddrStreet1 = Krasnotkatskaya, 10
////        StudentHasAfterSchoolCenter.StudentHasAfterSchoolCenterFalse = Nej
////        ReasonForSchoolTransport.ReasonForSchoolTransportDistance = Avstånd till skolan
////        MeansOfConveyance.MeansOfConveyanceSchoolBus = Skolbuss
////        SchoolTransportToDays.SchoolTransportToDaysTuesday = Tisdag
////        Disconnected from the target VM, address: '127.0.0.1:50606', transport: 'socket'
////        SchoolTransportToDays.SchoolTransportToDaysMonday = Måndag
////        SchoolTransportFromDays.SchoolTransportFromDaysThursday = Torsdag
////        SchoolTransportFromDays.SchoolTransportFromDaysWednesday = Onsdag
////        SchoolTransportFromDays.SchoolTransportFromDaysFriday = Fredag
////        SchoolTransportSecurity.SchoolTransportSecurityStudentHasFollower = Har medföljande assistent
////        SchoolTransportSecurity.SchoolTransportSecurityStudentHasPermobil = Medför permobil
////        SchoolTransportSecurity.SchoolTransportSecurityStudentShorter135 = Är kortare än 135 centimeter
////        SchoolTransporOtherInfo.SchoolTransporOtherInfoField = Bla Bla Bla
////        RequestingGuardian.RequestingGuardianFirstName = John
////        RequestingGuardian.RequestingGuardianLastName = Admin
////        StudentHasOtherContactPerson.studentHasOtherContactPersonTrue = Kontaktperson om annan än vårdnadshavare
////        OtherContactPers.OtherContactPersId = 510725-8286
//
//
////
////        String options =
//////                            "=Student.StudentFirstName = VITALIY\n" +
//////                            "=Student.StudentFamilyName = SEREDA\n" +
////                            "pupil=:Student.StudentPersonalIdNumber\n" +
//////                            "address.postalCode=StudentRegAddr.StudentRegAddrZip" +
//////                            "address.city=StudentRegAddr.StudentRegAddrCity\n" +
//////                            "address.streed=StudentRegAddr.StudentRegAddrStreet1\n" +
////                            "reasone=:ReasonForSchoolTransport.ReasonForSchoolTransportDistance\n" +
//////                            "=MeansOfConveyance.MeansOfConveyanceSchoolBus = Skolbuss\n" +
//////                            "=SchoolTransportToDays.SchoolTransportToDaysTuesday = Tisdag\n" +
//////                            "=SchoolTransportToDays.SchoolTransportToDaysMonday = Måndag\n" +
//////                            "=SchoolTransportFromDays.SchoolTransportFromDaysThursday = Torsdag\n" +
//////                            "=SchoolTransportFromDays.SchoolTransportFromDaysWednesday = Onsdag\n" +
//////                            "=SchoolTransportFromDays.SchoolTransportFromDaysFriday = Fredag\n" +
////                            "accompanyingAssistant=:SchoolTransportSecurity.SchoolTransportSecurityStudentHasFollower\n" +
////                            "byMobilPhone=:SchoolTransportSecurity.SchoolTransportSecurityStudentHasPermobil\n" +
////                            "shorty=:SchoolTransportSecurity.SchoolTransportSecurityStudentShorter135\n" +
//////                            "=SchoolTransporOtherInfo.SchoolTransporOtherInfoField = Bla Bla Bla\n" +
//////                            "=RequestingGuardian.RequestingGuardianFirstName = John\n" +
//////                            "=RequestingGuardian.RequestingGuardianLastName = Admin\n" +
//////                            "=StudentHasOtherContactPerson.studentHasOtherContactPersonTrue = Kontaktperson om annan än vårdnadshavare\n" +
////                            "guardian=:OtherContactPers.OtherContactPersId";
////
////        ApplicationPopulator populator = ApplicationPopulator.fromString(serviceFactory, options);
////        Application application = populator.create(form);
////
////        System.out.println(populator);
////
//////        populator.addPersonalizedQuestion();
//////        SchoolService service = factory.getService(SchoolService.class);
//////        List<? extends JpaEntity> entityList = service.findAll();
//////        System.out.println(entityList);
    }

}

