package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.oeplatform.flowengine.interfaces.FieldValue;
import com.imcode.oeplatform.flowengine.interfaces.MultiValueQueryInstance;
import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

//import com.imcode.oeplatform.flowengine.queries.MultiValueQueryInstance;
//import com.imcode.oeplatform.flowengine.queries.textfieldquery2.TextField;

/**
 * Created by vitaly on 10.09.15.
 */
public class FieldQuestionMapper implements QuestionMapper<MultiValueQueryInstance> {
    private static final Predicate<MutableField> DEFAULT_FIELD_MATCHER = mutableField -> mutableField.isExported() && !mutableField.getXsdElementName().isEmpty();

    private final Predicate<Class<?>> matcher;
    private final Predicate<MutableField> fieldMatcher = DEFAULT_FIELD_MATCHER;


    private FieldQuestionMapper(Predicate<Class<?>> matcher) {
        this.matcher = matcher;
    }

    public static FieldQuestionMapper forMapper(Predicate<Class<?>> matcher) {
        return new FieldQuestionMapper(matcher){};
    }

    public static FieldQuestionMapper forClass(Class<?> clazz) {
        return new FieldQuestionMapper(aClass -> aClass == clazz){};
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
    public boolean mach(Class queryClass) {
        return matcher.test(queryClass);
    }

    @Override
    public Collection<ApplicationFormQuestion> getQuestions(MultiValueQueryInstance queryInstance) {
        List<ApplicationFormQuestion> result = new LinkedList<>();
//        String subStepName = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor().getName();
//        for (MutableField mutableField : queryInstance.getQuery().getFields()) {
//            final ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();
//            if (fieldMatcher.test(mutableField)) {
//                ApplicationFormQuestion question = new ApplicationFormQuestion();
//                question.setSubStepName(subStepName);
////                question.setText(descriptor.getName() + TEXT_SEPARATOR + mutableField.getLabel());
//                question.setText(mutableField.getLabel());
//                question.setXsdElementName(descriptor.getXSDElementName() + NAME_SEPARATOR + mutableField.getXsdElementName());
//                question.setValue(getFieldValue(queryInstance, mutableField));
//
//                SortIndexMapper sortIndexMapper = new SortIndexMapper(queryInstance, mutableField::getSortIndex);
//                question.setSortOrder(sortIndexMapper.get());
//
//                fillStep(queryInstance, question);
//
//                result.add(question);
//            }
//        }
        return result;
    }

}
