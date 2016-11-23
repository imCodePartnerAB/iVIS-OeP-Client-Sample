package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by vitaly on 16.10.15.
 */
public interface QuestionMapper<T extends QueryInstance> {
    String TEXT_SEPARATOR = ": ";
    String NAME_SEPARATOR = ".";

    class SortIndexMapper implements Supplier<Integer> {
        private final QueryInstance queryInstance;
        private final Supplier<Integer> fieldIndexSupplier;
        private Integer sortIndex;

        public SortIndexMapper(QueryInstance queryInstance) {
            this(queryInstance, () -> null);
        }

        public SortIndexMapper(QueryInstance queryInstance, Supplier<Integer> fieldIndexSupplier) {
            this.queryInstance = queryInstance;
            this.fieldIndexSupplier = fieldIndexSupplier;
        }

        public Integer get() {
            if (sortIndex == null) {
                Optional<Integer> stepSortOrder = getSortIndexValue(() -> queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getStep().getSortIndex());
                Optional<Integer> querySortOrder = getSortIndexValue(() -> queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getSortIndex());
                Optional<Integer> fieldSortOrder = getSortIndexValue(fieldIndexSupplier);

                sortIndex = 100 * stepSortOrder.orElse(0)
                        + 10 * querySortOrder.orElse(0)
                        + fieldSortOrder.orElse(0);
            }

            return sortIndex;
        }

        private Optional<Integer> getSortIndexValue(Supplier<Integer> valueGetter) {
            try {
                return Optional.of(valueGetter.get());
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    boolean mach(Class queryClass);

    Collection<ApplicationFormQuestion> getQuestions(T queryInstance);

    default boolean mach(T query) {
        return mach(query.getClass());
    }

    default Stream<ApplicationFormQuestion> getQuestionStream(T queryInstance) {
        return getQuestions(queryInstance).stream();
    }

    default void fillStep(QueryInstance queryInstance, ApplicationFormQuestion question) {
//        question.setStepName(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getStep().getName());
//        question.setStepSortOrder(queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getStep().getSortIndex());
    }


}
