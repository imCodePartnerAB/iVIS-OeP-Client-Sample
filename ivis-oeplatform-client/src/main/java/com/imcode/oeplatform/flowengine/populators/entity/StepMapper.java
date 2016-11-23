package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.entities.ApplicationFormQuestionGroup;
import com.imcode.entities.ApplicationFormStep;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.managers.ManagedQueryInstance;
import com.nordicpeak.flowengine.managers.ManagedStep;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by vitaly on 16.03.16.
 */
public class StepMapper implements Function<ManagedStep, ApplicationFormStep>{
    private final Set<QuestionGroupMapper<? extends QueryInstance>> questionGroupMappers = new HashSet<>();
    private static final QuestionGroupMapper<QueryInstance> DUMMY_MAPPER = new QuestionGroupMapper<QueryInstance>() {
        @Override
        public void mapQuestions(ApplicationFormQuestionGroup questionGroup, QueryInstance queryInstance) {
        }

        @Override
        public boolean mach(Class queryClass) {
            return false;
        }
    };
    public <T extends QueryInstance> void addMapper(QuestionGroupMapper<T> mapper) {
        questionGroupMappers.add(mapper);
    }

    public <T extends QueryInstance> QuestionGroupMapper<T> getMapper(Class<T> clazz) {
        return getMapper0(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends QueryInstance> QuestionGroupMapper<T> getMapperFor(T instance) {
        Objects.requireNonNull(instance);
        return getMapper0((Class<T>) instance.getClass());
    }

    public <T extends QueryInstance> boolean hasMapper(Class<T> clazz) {
        return getMapper0(clazz) != null;
    }

    public <T extends QueryInstance> boolean hasMapperFor(T instance) {
        Objects.requireNonNull(instance);
        return getMapper0(instance.getClass()) != DUMMY_MAPPER;
    }

    @SuppressWarnings("unchecked")
    private <T extends QueryInstance> QuestionGroupMapper<T> getMapper0(Class<T> clazz) {

        Optional<QuestionGroupMapper<? extends QueryInstance>> result = questionGroupMappers.stream()
                .filter(mapper -> mapper.mach(clazz))
                .findFirst();

        return (QuestionGroupMapper<T>) result.orElse(DUMMY_MAPPER);
    }


    @Override
    public ApplicationFormStep apply(ManagedStep managedStep) {
        ApplicationFormStep step = new ApplicationFormStep();
        try {
            step.setName(managedStep.getStep().getName());
            step.setText(managedStep.getStep().getName());
            step.setSortOrder(managedStep.getStep().getSortIndex());
            List<ApplicationFormQuestionGroup> questionGroups = managedStep.getManagedQueryInstances()
                    .stream()
                    .map(ManagedQueryInstance::getQueryInstance)
                    .filter(this::hasMapperFor)
                    .map(queryInstance -> this.getMapperFor(queryInstance).apply(queryInstance))
                    .collect(Collectors.toList());

            step.setQuestionGroups(questionGroups);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return step;
    }
}
