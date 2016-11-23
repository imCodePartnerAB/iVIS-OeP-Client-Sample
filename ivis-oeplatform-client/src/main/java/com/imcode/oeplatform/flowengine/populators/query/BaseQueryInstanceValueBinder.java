package com.imcode.oeplatform.flowengine.populators.query;

import com.nordicpeak.flowengine.interfaces.QueryInstance;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessor;

import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Created by vitaly on 19.11.15.
 */
public abstract class BaseQueryInstanceValueBinder<T extends QueryInstance> implements QueryInstanceValueBinder<T> {
    public static class CannotReadProperty extends RuntimeException{
        private final Object source;
        private final String propertyName;
        private final String invalidNodeName;

        public CannotReadProperty(Object source, String propertyName, String invalidNodeName) {
            this.source = source;
            this.propertyName = propertyName;
            this.invalidNodeName = invalidNodeName;
        }

        @Override
        public String getMessage() {
            return String.format("%s: Invalid property '%s(%s)' of bean class [%s]: Bean property '%3$s' is not readable or has an invalid getter method: Does the return type of the getter match the parameter type of the setter?", getClass().getSimpleName(), propertyName, invalidNodeName, source.getClass());
        }
    }


    private class RuntimeExceptionHandler implements ExceptionHandler {
        @Override
        public void handle(Throwable t) throws RuntimeException {
            try {
                throw t;
            } catch (CannotReadProperty e) {
                log.warn(e.getMessage());
            } catch (NotReadablePropertyException e) {
                log.warn(String.format("Can't reed property '%s' of object '%s'", e.getPropertyName(), source));
            } catch (BeansException e) {
                log.warn(e);
            } catch (Throwable throwable) {
                throw new RuntimeException(t);
            }
        }
    }

    protected static final UnaryOperator<String> DEFAULT_PROPERTY_NAME_NORMALIZER = name -> {
        StringBuilder sb = new StringBuilder(name.length());
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if (c != '"') {
                sb.append(c);
            }
        }

        return sb.toString();
    };

    protected ExceptionHandler exceptionHandler = new RuntimeExceptionHandler();
    protected final Logger log = Logger.getLogger(this.getClass());
    protected final PropertyAccessor accessor;
    protected final String sourceName;
    protected final Object source;
    protected UnaryOperator<String> propertyNameNormalizer;

    public BaseQueryInstanceValueBinder(Object source, String sourceName, UnaryOperator<String> propertyNameNormalizer) {
        this.source = source;
        this.accessor = new BeanWrapperImpl(source);
        this.sourceName = sourceName;
        this.propertyNameNormalizer = propertyNameNormalizer;
    }

    public BaseQueryInstanceValueBinder(Object source, String sourceName) {
        this(source, sourceName, DEFAULT_PROPERTY_NAME_NORMALIZER);

    }

    protected String getValue(String propertyName) {
        String validPropertyName = propertyNameNormalizer.apply(propertyName);
        Object value = null;
        try {
            value = accessor.getPropertyValue(validPropertyName);
        } catch (NotReadablePropertyException e) {
            exceptionHandler.handle(new CannotReadProperty(source, propertyName, e.getPropertyName()));
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }

        return value == null ? "" : Objects.toString(value);
    }

    public abstract void bindValues(T queryInstance);
}
