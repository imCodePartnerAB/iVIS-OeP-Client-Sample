package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.Application;
import com.imcode.entities.ApplicationForm;
import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.entities.ApplicationFormStep;
import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableFlow;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import com.nordicpeak.flowengine.managers.ManagedStep;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ApplicationFormFactory {
    public static final Predicate<QueryInstance> DEFAULT_QUERY_CHECKER;
    public static final Predicate<Object> DEFAULT_FIELD_CHECKER;
    private static final QuestionMapper<QueryInstance> DUMMY_MAPPER = new QuestionMapper<QueryInstance>() {
        @Override
        public boolean mach(Class queryClass) {
            return false;
        }

        @Override
        public Collection<ApplicationFormQuestion> getQuestions(QueryInstance queryInstance) {
            return Collections.emptyList();
        }
    };

    static {
        DEFAULT_QUERY_CHECKER = query -> query.getQueryInstanceDescriptor().getQueryDescriptor().isExported();
//                                        && query.getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN;

        DEFAULT_FIELD_CHECKER = field -> field instanceof MutableField
                && ((MutableField) field).isExported();
    }

    private Predicate<QueryInstance> queryChecker = DEFAULT_QUERY_CHECKER.and(this::hasMapperFor);
    private Predicate<Object> fieldChecker = DEFAULT_FIELD_CHECKER;
    private Collection<QuestionMapper<? extends QueryInstance>> formQuestionMappers;

    public ApplicationFormFactory() {
        this(DEFAULT_QUERY_CHECKER, DEFAULT_FIELD_CHECKER, new HashSet<>());
    }

    public ApplicationFormFactory(Predicate<QueryInstance> queryChecker, Predicate<Object> fieldChecker, Collection<QuestionMapper<? extends QueryInstance>> formQuestionMappers) {
        Objects.requireNonNull(queryChecker);
        Objects.requireNonNull(fieldChecker);
        Objects.requireNonNull(formQuestionMappers);

        this.queryChecker = queryChecker;
        this.fieldChecker = fieldChecker;
        this.formQuestionMappers = formQuestionMappers;
    }

    public <T extends QueryInstance> void addMapper(QuestionMapper<T> mapper) {
        formQuestionMappers.add(mapper);
    }

    public <T extends QueryInstance> QuestionMapper<T> getMapper(Class<T> clazz) {
        return getMapper0(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends QueryInstance> QuestionMapper<T> getMapperFor(T instance) {
        Objects.requireNonNull(instance);
        return getMapper0((Class<T>) instance.getClass());
    }

    public <T extends QueryInstance> boolean hasMapper(Class<T> clazz) {
        return getMapper0(clazz) != null;
    }

    public <T extends QueryInstance> boolean hasMapperFor(T instance) {
        Objects.requireNonNull(instance);
        return getMapper0(instance.getClass()) != null;
    }

    @SuppressWarnings("unchecked")
    private <T extends QueryInstance> QuestionMapper<T> getMapper0(Class<T> clazz) {

        Optional<QuestionMapper<? extends QueryInstance>> result = formQuestionMappers.stream()
                .filter(mapper -> mapper.mach(clazz))
                .findFirst();

        return (QuestionMapper<T>) result.orElse(DUMMY_MAPPER);
    }

    private Class<?> getRequiredTypeInfo(Object converter, Class<?> genericIfc) {
        ResolvableType resolvableType = ResolvableType.forClass(converter.getClass()).as(genericIfc);
        ResolvableType[] generics = resolvableType.getGenerics();

        if (generics.length == 0) {
            return null;
        }

        Class<?> sourceType = generics[0].resolve();

        if (sourceType == null) {
            return null;
        }

        return sourceType;
    }

    @SuppressWarnings("unchecked")
    public ApplicationForm get(FlowInstanceManager manager) {

        Set<ApplicationFormQuestion> questions = null;
        Objects.requireNonNull(manager);

        final ImmutableFlow flow = manager.getFlowInstance().getFlow();

        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setName(flow.getName());
        applicationForm.setVersion(flow.getVersion());
        List<QueryInstance> queryList = manager.getQueries(QueryInstance.class);

        StepMapper stepMapper = new StepMapper();

        stepMapper.addMapper(FieldQuestionGroupMapper.forClass(TextFieldQueryInstance.class));
        stepMapper.addMapper(FieldQuestionGroupMapper.forClass(com.imcode.oeplatform.flowengine.queries.textfieldquery2.TextFieldQueryInstance.class));
        stepMapper.addMapper(new SingleAlternativeQuestionGroupMapper());
        stepMapper.addMapper(new MultipleAlternativeQuestionGroupMapper());
        stepMapper.addMapper(new TextAreaQuestionGroupMapper());

        List<ApplicationFormStep> steps;
        List<ManagedStep> managedSteps;
//        managedSteps = new ArrayList<>();

//        try {
//
//        } catch (IllegalAccessException | NoSuchFieldException e) {
//            e.printStackTrace();
//        }
        try {
            Field managedStepsField = MutableFlowInstanceManager.class.getDeclaredField("managedSteps");
            managedStepsField.setAccessible(true);
            managedSteps = (List<ManagedStep>) managedStepsField.get(manager);
            steps = managedSteps.stream().map(stepMapper).collect(Collectors.toList());
            applicationForm.setSteps(steps);

//            questions = queryList.stream()
//                    .filter(queryChecker)
//                    //                .peek(queryInstanceConsumer)
//                    .flatMap(queryInstance -> this.getMapperFor(queryInstance).getQuestionStream(queryInstance))
//                    .collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

//        applicationForm.setQuestions(questions);
//        if (questions == null) {
//            questions = new LinkedHashSet<>();
//        }
//
////        Map<String, ApplicationForm.Question> questionMap = new LinkedHashMap<>();
////
////        FieldMapFactory fieldMapFactory = new FieldMapFactory();
////        fieldMapFactory.addMapper(new FixedAlternativesQueryMapper());
////        fieldMapFactory.addMapper(new TextFieldQueryMapper());
////        fieldMapFactory.addMapper(new TextAreaQueryMapper());
////        fieldMapFactory.addMapper(new LinkedDropDownQueryMapper());
//
//
//        for (QueryInstance query : queryList) {
//            if (queryChecker.test(query) && hasMapper(query.getClass())) {
//                Map<String, FormField> fieldMap = fieldMapFactory.createFormFieldMap(query);
//                if (fieldMap != null) {
//                    questionMap.put(query.getQueryInstanceDescriptor().getQueryDescriptor().getXSDElementName(), new ApplicationForm.Question(query, fieldMap));
//                }
//            }
//        }

        return applicationForm;
    }

    public Predicate<QueryInstance> getQueryChecker() {
        return queryChecker;
    }

    public void setQueryChecker(Predicate<QueryInstance> queryChecker) {
        this.queryChecker = queryChecker;
    }

    public Predicate<Object> getFieldChecker() {
        return fieldChecker;
    }

    public void setFieldChecker(Predicate<Object> fieldChecker) {
        this.fieldChecker = fieldChecker;
    }
}
