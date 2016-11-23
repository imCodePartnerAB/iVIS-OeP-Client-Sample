package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.entities.ApplicationFormQuestionGroup;
import com.imcode.oeplatform.flowengine.queries.textareaquery.TextAreaQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.Collections;

/**
 * Created by vitaly on 16.03.16.
 */
public class TextAreaQuestionGroupMapper extends QuestionGroupMapper<TextAreaQueryInstance> {
    @Override
    public void mapQuestions(ApplicationFormQuestionGroup questionGroup, TextAreaQueryInstance queryInstance) {
        ApplicationFormQuestion question = new ApplicationFormQuestion();
        ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();

        question.setName(descriptor.getXSDElementName());
        question.setText(descriptor.getName());
        question.setMultiValues(false);
        question.setMultiVariants(false);
        question.setSortOrder(null);
        question.setQuestionType(queryInstance.getClass().getName());
        question.setValue(queryInstance.getValue());
        question.setValues(Collections.singletonList(queryInstance.getValue()));
        question.setVariants(Collections.emptyList());
//        question.setQuestionGroup(questionGroup);
        questionGroup.addQuestion(question);
//        question.set();
//        return question;
    }

    public boolean mach(Class queryClass) {
        return  queryClass == TextAreaQueryInstance.class;
    }
}
