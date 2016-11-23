package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.nordicpeak.flowengine.managers.FlowInstanceManager;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.core.convert.converter.Converter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vitaly on 14.09.15.
 */

public class FlowToPropertyValuesConverter implements Converter<FlowInstanceManager, PropertyValues> {
    private final Map<String, String> options;

    public FlowToPropertyValuesConverter(String optionsStrting) {
        this.options = mapOptions(optionsStrting);
    }

    @Override
    public PropertyValues convert(FlowInstanceManager source) {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        ApplicationForm form = new ApplicationFormFactory().createApplicationForm(source);
        for (Map.Entry<String, String> option :options.entrySet()) {
            String valueKey = option.getValue();
            Function<String, Object> valueExecutor;
            if (isRawValue(valueKey)) {
                valueExecutor = s -> s.substring(1);
//            } else if (isListValue(valueKey)) {
//                valueExecutor = form::getValues;
            } else {
                valueExecutor = s -> form.getValue(s);
            }
            propertyValues.addPropertyValue(option.getKey(), valueExecutor.apply(option.getValue()));
        }

        return propertyValues;
    }

    private static boolean isListValue(String value) {
        return value.indexOf('.') < 0;
    }

    private static boolean isRawValue(String value) {
        return value.startsWith("#");
    }

    private static Map<String, String> mapOptions(String options) {
        Map<String, String> map = new LinkedHashMap<>();

        if (options != null && !options.isEmpty()) {
            Pattern rowPattern = Pattern.compile("\n");
            Pattern pairPattern = Pattern.compile("=");

            String[] pairStrings = rowPattern.split(options);
            for (String pairString : pairStrings) {
                String[] pair = pairPattern.split(pairString);
                if (pair.length > 1) {
                    final String key = pair[0].trim();
                    final String value = pair[1].trim();
                    if (!key.isEmpty() && !value.isEmpty()) {
                        map.put(key, value);
                    }
                }
            }
        }

        return map;
    }
}
