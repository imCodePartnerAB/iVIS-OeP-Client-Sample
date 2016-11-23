package com.imcode.oeplatform.flowengine.populators.entity;

import com.imcode.entities.ApplicationFormQuestion;
import com.imcode.entities.ApplicationFormQuestionGroup;
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
import java.util.stream.Collectors;

/**
 * Created by vitaly on 10.09.15.
 */
public class SingleAlternativeQuestionGroupMapper extends QuestionGroupMapper<FixedAlternativesQueryInstance> {
    private final Predicate<ImmutableAlternative> alternativeFilter;
    private static List<Class> allowedClassList = Arrays.asList(
            RadioButtonQueryInstance.class,
            DropDownQueryInstance.class);

    private static final Predicate<ImmutableAlternative> DEFAULT_FILTER = alternative -> {
        if (alternative instanceof MutableField) {
            MutableField exportedMutableField = (MutableField) alternative;
            return exportedMutableField.isExported() && !exportedMutableField.getXsdElementName().isEmpty();
        }

        return false;
    };

    private static final Function<ImmutableAlternative, ApplicationFormQuestion> mapper = alternative -> new ApplicationFormQuestion();


    public SingleAlternativeQuestionGroupMapper() {
        this(DEFAULT_FILTER);
    }

    public SingleAlternativeQuestionGroupMapper(Predicate<ImmutableAlternative> alternativeFilter) {
        this.alternativeFilter = alternativeFilter;
    }

    @Override
    public void mapQuestions(ApplicationFormQuestionGroup questionGroup, FixedAlternativesQueryInstance queryInstance) {
        ImmutableQueryDescriptor descriptor = queryInstance.getQueryInstanceDescriptor().getQueryDescriptor();

        List<? extends ImmutableAlternative> alternatives = queryInstance.getAlternatives();
        ApplicationFormQuestion question = new ApplicationFormQuestion();
        question.setName(descriptor.getXSDElementName());
        question.setText(descriptor.getName());
        question.setMultiValues(false);
        question.setMultiVariants(true);
//        question.setSortOrder(alternative.getSortIndex());
        question.setQuestionType(queryInstance.getClass().getName());
        if (alternatives != null) {
            for (ImmutableAlternative alternative : alternatives) {
                String value = alternative.getName();
                question.setValue(value);
                question.setValues(Collections.singletonList(value));
                break;
            }
        }

        if (queryInstance instanceof RadioButtonQueryInstance) {
            RadioButtonQueryInstance instance = (RadioButtonQueryInstance) queryInstance;
            question.setVariants(instance.getQuery().getAlternatives().stream().map(ImmutableAlternative::getName).collect(Collectors.toList()));
        } else if (queryInstance instanceof DropDownQueryInstance) {
            DropDownQueryInstance instance = (DropDownQueryInstance) queryInstance;
            question.setVariants(instance.getQuery().getAlternatives().stream().map(ImmutableAlternative::getName).collect(Collectors.toList()));
        } else {
            question.setVariants(Collections.emptyList());
        }

//                question.setQuestionGroup(questionGroup);
        questionGroup.addQuestion(question);
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
