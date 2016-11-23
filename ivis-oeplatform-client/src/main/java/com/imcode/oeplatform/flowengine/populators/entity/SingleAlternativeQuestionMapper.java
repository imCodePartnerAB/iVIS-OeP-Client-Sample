package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.oeplatform.flowengine.interfaces.FieldValue;
import com.imcode.oeplatform.flowengine.interfaces.MultiValueQueryInstance;
import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.imcode.oeplatform.flowengine.populators.entity.application.BaseFormField;
import com.imcode.oeplatform.flowengine.queries.dropdownquery.DropDownQueryInstance;
import com.imcode.oeplatform.flowengine.queries.radiobuttonquery.RadioButtonQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQueryInstance;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by vitaly on 10.09.15.
 */
public class SingleAlternativeQuestionMapper implements QuestionMapper<FixedAlternativesQueryInstance> {
    private final Predicate<ImmutableAlternative> alternativeFilter;
    private static List<Class> allowedClassList = Arrays.asList(RadioButtonQueryInstance.class,
//            CheckboxQueryInstance.class,
            DropDownQueryInstance.class);

    private static final Predicate<ImmutableAlternative> DEFAULT_FILTER = alternative -> {
        if (alternative instanceof MutableField) {
            MutableField exportedMutableField = (MutableField) alternative;
            return exportedMutableField.isExported() && !exportedMutableField.getXsdElementName().isEmpty();
        }

        return false;
    };

    private static final Function<ImmutableAlternative, ApplicationFormQuestion> mapper = alternative -> new ApplicationFormQuestion();



    public SingleAlternativeQuestionMapper() {
        this(DEFAULT_FILTER);
    }

    public SingleAlternativeQuestionMapper(Predicate<ImmutableAlternative> alternativeFilter) {
        this.alternativeFilter = alternativeFilter;
    }

    @Override
    public Collection<ApplicationFormQuestion> getQuestions(FixedAlternativesQueryInstance queryInstance) {
//        List<ApplicationFormQuestion> result = new ArrayList<>();
        ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();
//
////        for (ImmutableAlternative alternative :queryInstance.getAlternatives()) {
////            ApplicationFormQuestion question = new ApplicationFormQuestion();
////            question.setText(descriptor.getName());
////            question.setXsdElementName(descriptor.getXSDElementName());
////            question.setValue(alternative.getName());
////
////            SortIndexMapper sortIndexMapper = new SortIndexMapper(queryInstance);
////            question.setSortOrder(sortIndexMapper.get());
////
////            fillStep(queryInstance, question);
////
////            result.add(question);
////
////            break;
////        }
////        return result;
//
////        for (ImmutableAlternative alternative :queryInstance.getAlternatives()) {
//            ApplicationFormQuestion question = new ApplicationFormQuestion();
//            question.setText(descriptor.getName());
//            question.setXsdElementName(descriptor.getXSDElementName());
//        question.setValue(getValue(queryInstance));
//
//            SortIndexMapper sortIndexMapper = new SortIndexMapper(queryInstance);
//            question.setSortOrder(sortIndexMapper.get());
//
//            fillStep(queryInstance, question);
//
////            result.add(question);
//
////            break;
////        }
//        return Collections.singleton(question);
        return new ArrayList<>();
    }

    public String getValue(FixedAlternativesQueryInstance queryInstance) {
        String value = null;
        List<? extends ImmutableAlternative> alternatives = queryInstance.getAlternatives();

        if (alternatives != null) {
            Optional<? extends ImmutableAlternative> optionalValue = alternatives.stream().findFirst();
            if (optionalValue.isPresent()) {
                value = optionalValue.get().getName();
            }
        }

        return value;
    }


    private String getXslElementName(ImmutableAlternative alternative) {
        if (alternative instanceof MutableField) {
            return ((MutableField) alternative).getXsdElementName();
        }

        throw new ClassCastException("Cannot cast to IvisExportedMutableField!");
    }

    @Override
    public boolean mach(Class queryClass) {
        return allowedClassList.contains(queryClass);
    }

    private FormField<ImmutableAlternative> createFormField(final ImmutableAlternative field) {
        return new BaseFormField<ImmutableAlternative>(field) {
            @Override
            public String getValue() {
                return this.field.getName();
            }
        };
    }
}
