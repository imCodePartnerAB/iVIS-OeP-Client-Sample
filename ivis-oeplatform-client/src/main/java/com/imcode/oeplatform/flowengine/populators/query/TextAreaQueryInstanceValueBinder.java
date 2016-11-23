package com.imcode.oeplatform.flowengine.populators.query;

import com.imcode.oeplatform.flowengine.queries.DependentField;
import com.imcode.oeplatform.flowengine.queries.textareaquery.TextAreaQuery;
import com.imcode.oeplatform.flowengine.queries.textareaquery.TextAreaQueryInstance;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import org.springframework.beans.DirectFieldAccessor;
import se.unlogic.standardutils.string.StringUtils;

import java.util.Objects;

/**
 * Created by vitaly on 19.11.15.
 */
public class TextAreaQueryInstanceValueBinder extends BaseQueryInstanceValueBinder<TextAreaQueryInstance> {

    public TextAreaQueryInstanceValueBinder(Object source, String sourceName) {
        super(source, sourceName);
    }

    @Override
    public boolean match(QueryInstance queryInstance) {
        return queryInstance instanceof TextAreaQueryInstance;
    }

    @Override
    public void bindValues(TextAreaQueryInstance queryInstance) {
        ImmutableQueryDescriptor queryDescriptor = getQueryDescriptor(queryInstance);
        TextAreaQuery textAreaQuery = queryInstance.getQuery();

        if (queryDescriptor != null
                && textAreaQuery != null
                && textAreaQuery.isDependsOn()
                && sourceName.equals(textAreaQuery.getDependencySourceName())
                && !StringUtils.isEmpty(textAreaQuery.getDependencyFieldName())) {
            try {
//                String value = Objects.toString(accessor.getPropertyValue(textAreaQuery.getDependencyFieldName()));
                String value = getValue(textAreaQuery.getDependencyFieldName());
                queryInstance.setValue(value);
            } catch (Exception e) {
                exceptionHandler.handle(e);
            }
        }
    }
}
