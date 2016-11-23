package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.interfaces.MutableField;
import com.imcode.oeplatform.flowengine.populators.entity.FormField;
import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
@Deprecated
public class ApplicationFormFactory {
    public static final Predicate<QueryInstance> DEFAULT_QUERY_CHECKER;
    public static final Predicate<Object> DEFAULT_FIELD_CHECKER = field -> field instanceof MutableField && ((MutableField) field).isExported();

    static {
        DEFAULT_QUERY_CHECKER = query -> query.getQueryInstanceDescriptor().getQueryDescriptor().isExported()
                                        && query.getQueryInstanceDescriptor().getQueryState() != QueryState.HIDDEN;
    }

    private Predicate<QueryInstance> queryChecker = DEFAULT_QUERY_CHECKER;
    private Predicate<Object> fieldChecker = DEFAULT_FIELD_CHECKER;

    public ApplicationForm createApplicationForm(FlowInstanceManager manager) {
        List<QueryInstance> queryList = manager.getQueries(QueryInstance.class);
        Map<String, ApplicationForm.Question> questionMap = new LinkedHashMap<>();

        FieldMapFactory fieldMapFactory = new FieldMapFactory();
        fieldMapFactory.addMapper(new FixedAlternativesQueryMapper());
        fieldMapFactory.addMapper(new TextFieldQueryMapper());
        fieldMapFactory.addMapper(new TextAreaQueryMapper());
        fieldMapFactory.addMapper(new LinkedDropDownQueryMapper());

        for (QueryInstance query : queryList) {
            if (queryChecker.test(query)) {
                Map<String, FormField> fieldMap = fieldMapFactory.createFormFieldMap(query);
                if (fieldMap != null) {
                    questionMap.put(query.getQueryInstanceDescriptor().getQueryDescriptor().getXSDElementName(), new ApplicationForm.Question(query, fieldMap));
                }
            }
        }

        return new ApplicationForm(questionMap);
    }

    public Predicate<QueryInstance> getQueryChecker() {
        return queryChecker;
    }

    public void setQueryChecker(Predicate<QueryInstance> queryChecker) {
        this.queryChecker = queryChecker;
    }

    public Predicate<Object> getFieldChecker() {
        return fieldChecker;
    }

    public void setFieldChecker(Predicate<Object> fieldChecker) {
        this.fieldChecker = fieldChecker;
    }
}
