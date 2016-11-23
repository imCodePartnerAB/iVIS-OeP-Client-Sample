package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.oeplatform.flowengine.populators.entity.FormField;
import com.imcode.oeplatform.flowengine.queries.checkboxquery.CheckboxQueryInstance;
import com.imcode.oeplatform.flowengine.queries.dropdownquery.DropDownQueryInstance;
import com.imcode.oeplatform.flowengine.queries.radiobuttonquery.RadioButtonQueryInstance;
import com.imcode.oeplatform.flowengine.queries.textfieldquery.TextFieldQueryInstance;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;
//import com.nordicpeak.flowengine.queries.textfieldquery.LabelFieldQueryInstance;
//import com.nordicpeak.flowengine.queries.textfieldquery.LabelFieldValue;

import java.io.*;
import java.util.*;

/**
 * Created by vitaly on 19.08.15.
 */
@Deprecated
public class ApplicationForm {
    private final Map<String, Question> questionMap;
    private final String fieldNameSpliter = "\\.";


    public ApplicationForm(Map<String, Question> questionMap) {
        this.questionMap = questionMap;
    }

    public Question getQuestion(String questionName) {
        return questionMap.get(questionName);
    }

    public Map<String, FormField> getFieldMap(String questionName) {
        Question question = getQuestion(questionName);

        if (question != null) {
            return question.getFieldMap();
        }

        return null;
    }

    public FormField getField(String fieldName) {
        String[] splitedName = splitFieldName(fieldName);

        if (splitedName.length != 2) {
            throw new IllegalArgumentException("\"" + fieldName + "\" Unsuported name format!");
        }

        Question question = getQuestion(splitedName[0]);

        if (question != null) {
            return question.getField(splitedName[1]);
        }

        return null;
    }

    public Object getValue(String fieldName) {
        FormField field = getField(fieldName);

        if (field != null) {
            return field.getValue();
        } else {
            return getValues(fieldName);
        }
    }

    public List<String> getValues(String questionName) {
        Question question = getQuestion(questionName);

        if (question != null) {
            return question.getValues();
        }

        return null;
    }

    public boolean hasField(String fieldName) {
        return getField(fieldName) != null;
    }

    private String[] splitFieldName(String fieldName) {
        return fieldName.split(fieldNameSpliter);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        List queries = event.getFlowInstanceManager().getQueries(Object.class);
//        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("/home/vitaly/IdeaProjects/Instance.dat"));
//        stream.writeObject(event);

//        FlowInstanceManager manager = event.getFlowInstanceManager();
//        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("/home/vitaly/IdeaProjects/FlowInstanceManager.dat"));
//        stream.writeObject(manager);
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("/home/vitaly/IdeaProjects/FlowInstanceManager.dat"));
        FlowInstanceManager flowInstanceManager = (FlowInstanceManager) inputStream.readObject();

        ApplicationFormFactory factory = new ApplicationFormFactory();
        factory.setQueryChecker(queryInstance -> queryInstance instanceof TextFieldQueryInstance
                || queryInstance instanceof DropDownQueryInstance
                || queryInstance instanceof CheckboxQueryInstance
                || queryInstance instanceof RadioButtonQueryInstance);

        ApplicationForm form = factory.createApplicationForm(flowInstanceManager);

//        System.out.println(1);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (String questionName : questionMap.keySet()) {
//            String prefix = questionName + ".";
            Map<String, FormField> fieldMap = getFieldMap(questionName);

            for (Map.Entry<String, FormField> fieldEntry : fieldMap.entrySet()) {
                sb.append(questionName)
                        .append('.')
                        .append(fieldEntry.getKey())
                        .append(" = ")
                        .append(fieldEntry.getValue().getValue())
                        .append('\n');
            }

        }

        return sb.toString();
    }

    /**
     * Created by vitaly on 10.09.15.
     */
    public static class Question {
        private QueryInstance query;
        private Map<String, FormField> fieldMap;

        public Question(QueryInstance query, Map<String, FormField> fieldMap) {
            this.query = query;
            this.fieldMap = Collections.unmodifiableMap(fieldMap);
        }

        public QueryInstance getQuery() {
            return query;
        }

        public Map<String, FormField> getFieldMap() {
            return fieldMap;
        }

        public FormField getField(String fieldName) {
            return fieldMap.get(fieldName);
        }

        public List<String> getValues() {
            Collection<FormField> fieldList = fieldMap.values();

            List<String> values = null;

            if (fieldList.size() > 0) {
                values = new ArrayList<>(fieldList.size());

                for (FormField field : fieldList) {
                    values.add(field.getValue());
                }
            }

            return values;
        }
    }
}

//new ObjectOutputStream(new FileOutputStream("/home/vitaly/IdeaProjects/FlowInstanceManager9.dat")).writeObject(instanceManager)