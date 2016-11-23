package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.populators.entity.FieldMapper;
import com.imcode.oeplatform.flowengine.populators.entity.FormField;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldQueryInstance;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 10.09.15.
 */
public class TextFieldQueryMapper implements FieldMapper<TextFieldQueryInstance> {

    @Override
    public Map<String, FormField> map(TextFieldQueryInstance query) {
        Map<String, FormField> fieldMap = new HashMap<>();
        if (query.getValues() != null) {
            for (TextFieldValue textFieldValue : query.getValues()) {
                if (textFieldValue.getTextField().isExported() && !textFieldValue.getTextField().getXsdElementName().isEmpty()) {
                    String xsdElementName = textFieldValue.getTextField().getXsdElementName();
                    FormField field = createFormField(textFieldValue);

                    fieldMap.put(xsdElementName, field);
                }
            }
        }

        return fieldMap;
    }

    private FormField<TextFieldValue> createFormField(final TextFieldValue field) {
        return new BaseFormField<TextFieldValue>(field) {
            @Override
            public String getValue() {
                return this.field.getValue();
            }
        };
    }

    @Override
    public boolean mach(Class queryClass) {
        return queryClass == TextFieldQueryInstance.class;
    }
}
