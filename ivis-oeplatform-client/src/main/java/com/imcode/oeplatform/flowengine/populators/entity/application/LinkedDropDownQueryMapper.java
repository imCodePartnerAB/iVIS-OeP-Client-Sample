package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.populators.entity.FieldMapper;
import com.imcode.oeplatform.flowengine.populators.entity.FormField;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownQueryInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitaly on 10.09.15.
 */
public class LinkedDropDownQueryMapper implements FieldMapper<LinkedDropDownQueryInstance> {
    @Override
    public Map<String, FormField> map(LinkedDropDownQueryInstance query) {
        Map<String, FormField> fieldMap = new HashMap<>();

        FormField formField = new FormField() {
            @Override
            public String getValue() {
                return query.getAlternative().getAlternativeID().toString();
            }

            @Override
            public Object getField() {
                return null;
            }
        };

        fieldMap.put("value", formField);

        return fieldMap;
    }

    @Override
    public boolean mach(Class queryClass) {
        return  queryClass == LinkedDropDownQueryInstance.class;
    }
}
