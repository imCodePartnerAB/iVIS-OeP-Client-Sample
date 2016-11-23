package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.imcode.oeplatform.flowengine.populators.entity.application.BaseFormField;
import com.imcode.oeplatform.flowengine.queries.checkboxquery.CheckboxQueryInstance;
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
public class MultipleAlternativeQuestionMapper implements QuestionMapper<FixedAlternativesQueryInstance> {
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



    public MultipleAlternativeQuestionMapper() {
        this(DEFAULT_FILTER);
    }

    public MultipleAlternativeQuestionMapper(Predicate<ImmutableAlternative> alternativeFilter) {
        this.alternativeFilter = alternativeFilter;
    }

    @Override
    public Collection<ApplicationFormQuestion> getQuestions(FixedAlternativesQueryInstance queryInstance) {
////        List<ApplicationFormQuestion> result = new ArrayList<>();
//        ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();
//
//        ApplicationFormQuestion question = new ApplicationFormQuestion();
//        question.setText(descriptor.getName());
//        question.setXsdElementName(descriptor.getXSDElementName());
//        String value = queryInstance.getAlternatives().stream().map(ImmutableAlternative::getName).collect(Collectors.joining(", "));
//        question.setValue(value);
////        result.add(question);
//        question.setSortOrder(descriptor.getSortIndex());
//        fillStep(queryInstance, question);
//        List<ApplicationFormQuestion> result = Collections.singletonList(question);
        List<ApplicationFormQuestion> result = new ArrayList<>();

//        for (ImmutableAlternative alternative :queryInstance.getAlternatives()) {
//            ApplicationFormQuestion question = new ApplicationFormQuestion();
//            question.setText(descriptor.getName() + TEXT_SEPARATOR + alternative.getName());
//            question.setXsdElementName(descriptor.getXSDElementName() + NAME_SEPARATOR + ((IvisExportedMutableAlternative)alternative).getXsdElementName());
//            question.setValue("true");
//
//            SortIndexMapper sortIndexMapper = new SortIndexMapper(queryInstance, alternative::getSortIndex);
//            question.setSortOrder(sortIndexMapper.get());
//
//            fillStep(queryInstance, question);
//
//            result.add(question);
//
////            break;
//        }





//        return Collections.singleton(question);


//        queryInstance.getAlternatives().stream()
//                .filter(alternativeFilter).map();
//
//
//        for (ImmutableAlternative alternative : queryInstance.getAlternatives()) {
//            if ()
//            String xsdElementName = getXslElementName(alternative);
//            FormField field = createFormField(alternative);
//
//            fieldMap.put(xsdElementName, field);
//        }

        return result;
    }

//    @Override
//    public Map<String, FormField> map(FixedAlternativesQueryInstance query) {
//        Map<String, FormField> fieldMap = new HashMap<>();
//        for (ImmutableAlternative alternative : query.getAlternatives()) {
//            String xsdElementName = getXslElementName(alternative);
//            FormField field = createFormField(alternative);
//
//            fieldMap.put(xsdElementName, field);
//        }
//
//        return fieldMap;
//    }

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
