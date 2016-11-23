package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.imcode.oeplatform.flowengine.populators.entity.FieldMapper;
import com.imcode.oeplatform.flowengine.populators.entity.FormField;
import com.imcode.oeplatform.flowengine.queries.checkboxquery.CheckboxQueryInstance;
import com.imcode.oeplatform.flowengine.queries.dropdownquery.DropDownQueryInstance;
import com.imcode.oeplatform.flowengine.queries.radiobuttonquery.RadioButtonQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQueryInstance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vitaly on 10.09.15.
 */
@Deprecated
public class FixedAlternativesQueryMapper implements FieldMapper<FixedAlternativesQueryInstance> {
    private static List<Class> allowedClassList = Arrays.asList(RadioButtonQueryInstance.class,
            CheckboxQueryInstance.class,
            DropDownQueryInstance.class);

    @Override
    public Map<String, FormField> map(FixedAlternativesQueryInstance query) {
        Map<String, FormField> fieldMap = new HashMap<>();
        for (ImmutableAlternative alternative : query.getAlternatives()) {
            String xsdElementName = getXslElementName(alternative);
            FormField field = createFormField(alternative);

            fieldMap.put(xsdElementName, field);
        }

        return fieldMap;
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
