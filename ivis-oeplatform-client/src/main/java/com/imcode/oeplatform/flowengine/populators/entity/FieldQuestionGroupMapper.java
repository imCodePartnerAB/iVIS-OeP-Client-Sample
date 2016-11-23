package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.entities.ApplicationFormQuestionGroup;
import com.imcode.oeplatform.flowengine.interfaces.FieldValue;
import com.imcode.oeplatform.flowengine.interfaces.MultiValueQueryInstance;
import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.imcode.oeplatform.flowengine.queries.textareaquery.TextAreaQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created by vitaly on 16.03.16.
 */
public class FieldQuestionGroupMapper extends QuestionGroupMapper<MultiValueQueryInstance> {
    private static final Predicate<MutableField> DEFAULT_FIELD_MATCHER = mutableField -> mutableField.isExported() && !mutableField.getXsdElementName().isEmpty();

    private final Predicate<Class<?>> matcher;
    private final Predicate<MutableField> fieldMatcher = DEFAULT_FIELD_MATCHER;

    public FieldQuestionGroupMapper(Predicate<Class<?>> matcher) {
        this.matcher = matcher;
    }

    public static FieldQuestionGroupMapper forClass(Class<?> clazz) {
        return new FieldQuestionGroupMapper(aClass -> aClass == clazz){};
    }

    public String getFieldValue(MultiValueQueryInstance queryInstance, MutableField textField) {
        String value = null;
        List<? extends FieldValue> values = queryInstance.getValues();

        if (values != null) {
            Optional<? extends FieldValue> optionalValue = values.stream().filter(v -> textField.equals(v.getTextField())).findFirst();
            if (optionalValue.isPresent()) {
                value = optionalValue.get().getValue();
            }
        }

        return value;
    }

    @Override
    public void mapQuestions(ApplicationFormQuestionGroup questionGroup, MultiValueQueryInstance queryInstance) {
        for (MutableField mutableField : queryInstance.getQuery().getFields()) {
            if (fieldMatcher.test(mutableField)) {
                ApplicationFormQuestion question = new ApplicationFormQuestion();
                question.setName(mutableField.getXsdElementName());
                question.setText(mutableField.getLabel());
                question.setMultiValues(false);
                question.setMultiVariants(false);
                question.setSortOrder(mutableField.getSortIndex());
                question.setQuestionType(queryInstance.getClass().getName());
                String fieldValue = getFieldValue(queryInstance, mutableField);
                question.setValue(fieldValue);
                question.setValues(Collections.singletonList(fieldValue));
                question.setVariants(Collections.emptyList());
//                question.setQuestionGroup(questionGroup);
                questionGroup.addQuestion(question);
            }
        }
    }

    @Override
    public boolean mach(Class queryClass) {
        return matcher.test(queryClass);
    }
}
