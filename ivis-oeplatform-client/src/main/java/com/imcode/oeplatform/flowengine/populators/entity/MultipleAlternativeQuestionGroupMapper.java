package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.entities.ApplicationFormQuestionGroup;
import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.imcode.oeplatform.flowengine.populators.entity.application.BaseFormField;
import com.imcode.oeplatform.flowengine.queries.checkboxquery.CheckboxAlternative;
import com.imcode.oeplatform.flowengine.queries.checkboxquery.CheckboxQuery;
import com.imcode.oeplatform.flowengine.queries.checkboxquery.CheckboxQueryInstance;
import com.imcode.oeplatform.flowengine.queries.dropdownquery.DropDownQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQueryInstance;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by vitaly on 10.09.15.
 */
public class MultipleAlternativeQuestionGroupMapper extends QuestionGroupMapper<CheckboxQueryInstance> {
    private final Predicate<ImmutableAlternative> alternativeFilter;
    private static List<Class> allowedClassList = Arrays.asList(
//            RadioButtonQueryInstance.class,
            CheckboxQueryInstance.class
//            DropDownQueryInstance.class
    );

    private static final Predicate<ImmutableAlternative> DEFAULT_FILTER = alternative -> {
        if (alternative instanceof MutableField) {
            MutableField exportedMutableField = (MutableField) alternative;
            return exportedMutableField.isExported() && !exportedMutableField.getXsdElementName().isEmpty();
        }

        return false;
    };

    private static final Function<ImmutableAlternative, ApplicationFormQuestion> mapper = alternative -> new ApplicationFormQuestion();



    public MultipleAlternativeQuestionGroupMapper() {
        this(DEFAULT_FILTER);
    }

    public MultipleAlternativeQuestionGroupMapper(Predicate<ImmutableAlternative> alternativeFilter) {
        this.alternativeFilter = alternativeFilter;
    }

    @Override
    public void mapQuestions(ApplicationFormQuestionGroup questionGroup, CheckboxQueryInstance queryInstance) {
         ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();
        CheckboxQuery query = queryInstance.getQuery();
        ApplicationFormQuestion question = new ApplicationFormQuestion();
        question.setName(descriptor.getXSDElementName());
        question.setText(descriptor.getName());
        question.setMultiValues(true);
        question.setMultiVariants(true);
        question.setSortOrder(descriptor.getSortIndex());
        question.setQuestionType(queryInstance.getClass().getName());
        List<String> values = Collections.emptyList();

        if (queryInstance.getAlternatives() != null) {
            values = queryInstance.getAlternatives().stream().map(ImmutableAlternative::getName).collect(Collectors.toList());
        }

        question.setValue(values.stream().collect(Collectors.joining()));
        question.setValues(values);
        question.setVariants(query.getAlternatives().stream().map(CheckboxAlternative::getName).collect(Collectors.toList()));
//        question.setQuestionGroup(questionGroup);
        questionGroup.addQuestion(question);
    }

    @Override
    public boolean mach(Class queryClass) {
        return allowedClassList.contains(queryClass);
    }
}
