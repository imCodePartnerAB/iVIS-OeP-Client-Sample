package com.imcode.oeplatform.flowengine.populators.query;

import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextField;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldQuery;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldQueryInstance;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldValue;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import org.springframework.beans.DirectFieldAccessor;
import se.unlogic.standardutils.string.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by vitaly on 19.11.15.
 */
public class TextFieldQueryInstanceValueBinder extends BaseQueryInstanceValueBinder<TextFieldQueryInstance> {

    public TextFieldQueryInstanceValueBinder(Object source, String sourceName) {
        super(source, sourceName);
    }

    @Override
    public boolean match(QueryInstance queryInstance) {
        return queryInstance instanceof TextFieldQueryInstance;
    }

    @Override
    public void bindValues(TextFieldQueryInstance queryInstance) {
        TextFieldQuery query = queryInstance.getQuery();
        if (query != null) {
            List<TextField> fieldList = query.getFields();
            List<TextFieldValue> valueList = queryInstance.getValues();

            if (valueList == null) {
                valueList = new ArrayList<>();
                queryInstance.setValues(valueList);
            }

            for (TextField textField : fieldList) {
                if (textField.isDependsOn()
                        && sourceName.equals(textField.getDependencySourceName())
                        && !StringUtils.isEmpty(textField.getDependencyFieldName())) {
                    try {
//                        String value = Objects.toString(accessor.getPropertyValue(textField.getDependencyFieldName()));
                        String value = getValue(textField.getDependencyFieldName());
                        Optional<TextFieldValue> optionalTextFieldValue = valueList.stream()
                                .filter(tfValue -> textField.equals(tfValue.getQueryField()))
                                .findFirst();

                        TextFieldValue textFieldValue = optionalTextFieldValue.orElseGet(() -> new TextFieldValue(textField, value));

                        if (!optionalTextFieldValue.isPresent()) {
                            valueList.add(textFieldValue);
                        } else {
                            textFieldValue.setValue(value);
                        }
                    } catch (Exception e) {
                        exceptionHandler.handle(e);
                    }
                }
            }
        }
    }
}
