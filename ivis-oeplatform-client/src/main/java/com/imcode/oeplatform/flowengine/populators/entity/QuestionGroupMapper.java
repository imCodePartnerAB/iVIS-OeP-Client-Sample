package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestionGroup;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.managers.ManagedQueryInstance;

import java.util.function.Function;

/**
 * Created by vitaly on 16.03.16.
 */
public abstract class QuestionGroupMapper<T extends QueryInstance> implements Function<T, ApplicationFormQuestionGroup> {
    @Override
    public ApplicationFormQuestionGroup apply(T queryInstance) {
        ApplicationFormQuestionGroup questionGroup = new ApplicationFormQuestionGroup();
//        T queryInstance = getQueryInstance(managedQueryInstance);
        ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();

        questionGroup.setName(descriptor.getXSDElementName());
        questionGroup.setText(descriptor.getName());
        questionGroup.setSortOrder(descriptor.getSortIndex());
        questionGroup.setQuestionType(queryInstance.getClass().getName());
        mapQuestions(questionGroup, queryInstance);

        return questionGroup;
    }

    public abstract void mapQuestions(ApplicationFormQuestionGroup questionGroup, T queryInstance);

    public abstract boolean mach(Class queryClass);

//    @SuppressWarnings("unchecked")
//    private <T> T getQueryInstance(QueryInstance queryInstance) {
//        return (T) managedQueryInstance.getQueryInstance();
//    }

    public boolean mach(T query) {
        return mach(query.getClass());
    }

}
