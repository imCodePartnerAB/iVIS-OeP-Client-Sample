package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.oeplatform.flowengine.queries.textareaquery.TextAreaQuery;
import com.imcode.oeplatform.flowengine.queries.textareaquery.TextAreaQueryInstance;
import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 10.09.15.
 */
public class TextAreaQuestionMapper implements QuestionMapper<TextAreaQueryInstance> {

    @Override
    public Collection<ApplicationFormQuestion> getQuestions(TextAreaQueryInstance queryInstance) {

        ApplicationFormQuestion question = new ApplicationFormQuestion();
////        final TextAreaQuery query = queryInstance.getQuery();
//        ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();
//
//        question.setText(descriptor.getName());
//        question.setXsdElementName(descriptor.getXSDElementName());
//        question.setValue(queryInstance.getValue());
//
//        SortIndexMapper sortIndexMapper = new SortIndexMapper(queryInstance);
//        question.setSortOrder(sortIndexMapper.get());
//
//        fillStep(queryInstance, question);

        return Collections.singleton(question);
    }

    @Override
    public boolean mach(Class queryClass) {
        return  queryClass == TextAreaQueryInstance.class;
    }

    public static void main(String[] args) {
        System.out.println(Period.between(LocalDate.of(2015, 7, 10), LocalDate.now()));
        System.out.println(Period.between(LocalDate.of(2015, 7, 10), LocalDate.of(2015, 9, 27)));
    }
}
