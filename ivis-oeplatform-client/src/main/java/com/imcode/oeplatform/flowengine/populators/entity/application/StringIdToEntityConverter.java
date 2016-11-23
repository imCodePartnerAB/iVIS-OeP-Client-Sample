package com.imcode.oeplatform.flowengine.populators.entity.application;

import com.imcode.entities.interfaces.JpaEntity;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Created by vitaly on 28.09.15.
 */
public class StringIdToEntityConverter<T extends ConversionService & ConverterRegistry> implements ConditionalGenericConverter {
    private final T conversionService;
////    private Repositories repositories;
    private StringIdToEntityConverter.ToEntityConverter toEntityConverter;
//    private StringIdToEntityConverter.ToIdConverter toIdConverter;
//
    public StringIdToEntityConverter(T conversionService) {
//        this.repositories = Repositories.NONE;
        Objects.requireNonNull(conversionService, "ConversionService must not be null!");
        this.conversionService = conversionService;
        conversionService.addConverter(this.toEntityConverter);
//        conversionService.addConverter(this.toIdConverter);
    }

    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, JpaEntity.class));
    }

    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
//        return JpaEntity.class.isAssignableFrom(targetType.getType()) ? this.toEntityConverter.convert(source, sourceType, targetType) : this.toIdConverter.convert(source, sourceType, targetType);
        return this.toEntityConverter.convert(source, sourceType, targetType);
    }

    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            return this.toEntityConverter.matches(sourceType, targetType);// || this.toIdConverter.matches(sourceType, targetType);
        } catch (Exception e) {
            return false;
        }
    }
//
////    public void setApplicationContext(ApplicationContext context) {
////        this.repositories = new Repositories(context);
////        this.toEntityConverter = new DomainClassConverter.ToEntityConverter(this.repositories, this.conversionService);
////        ((ConverterRegistry) this.conversionService).addConverter(this.toEntityConverter);
////        this.toIdConverter = new DomainClassConverter.ToIdConverter();
////        ((ConverterRegistry) this.conversionService).addConverter(this.toIdConverter);
////    }
//
////    private static final class ConversionMatchAbbreviationException extends RuntimeException {
////        private ConversionMatchAbbreviationException() {
////        }
////    }
//
//    class ToIdConverter implements ConditionalGenericConverter {
//        ToIdConverter() {
//        }
//
//        public Set<ConvertiblePair> getConvertibleTypes() {
//            return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
//        }
//
//        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
//            if (source != null && StringUtils.hasText(source.toString())) {
//                if (sourceType.equals(targetType)) {
//                    return source;
//                } else {
//                    Class domainType = sourceType.getType();
//                    return null;
////                    EntityInformation entityInformation = DomainClassConverter.this.repositories.getEntityInformationFor(domainType);
////                    return DomainClassConverter.this.conversionService.convert(entityInformation.getId(source), targetType.getType());
//                }
//            } else {
//                return null;
//            }
//        }
//
//        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
//            if (sourceType.isAssignableTo(targetType)) {
//                return false;
//            }
////            else if (!DomainClassConverter.this.repositories.hasRepositoryFor(sourceType.getType())) {
////                return false;
////            } else {
////                Class rawIdType = DomainClassConverter.this.repositories.getRepositoryInformationFor(sourceType.getType()).getIdType();
////                return targetType.equals(TypeDescriptor.valueOf(rawIdType)) || DomainClassConverter.this.conversionService.canConvert(rawIdType, targetType.getType());
////            }
//        }
//    }
//
    public static final class ToEntityConverter<TT extends ConversionService & ConverterRegistry> implements ConditionalGenericConverter {
    private final TT conversionService1;

    public ToEntityConverter(TT conversionService1) {
        this.conversionService1 = conversionService1;
        conversionService1.addConverter(this);
    }
//        public ToEntityConverter(Repositories repositories, ConversionService conversionService) {
//            this.repositoryInvokerFactory = new DefaultRepositoryInvokerFactory(repositories, conversionService);
//        }

        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(Object.class, JpaEntity.class));
        }

        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source != null && StringUtils.hasText(source.toString())) {
                if (sourceType.equals(targetType)) {
                    return source;
                } else {
                    Class domainType = targetType.getType();
                    return null;
//                    RepositoryInformation info = DomainClassConverter.this.repositories.getRepositoryInformationFor(domainType);
//                    RepositoryInvoker invoker = this.repositoryInvokerFactory.getInvokerFor(domainType);
//                    return invoker.invokeFindOne((Serializable) DomainClassConverter.this.conversionService.convert(source, info.getIdType()));
                }
            } else {
                return null;
            }
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (sourceType.isAssignableTo(targetType)) {
                return false;
            } else if (!JpaEntity.class.isAssignableFrom(targetType.getType())) {
                return false;
            } else {
                Class rawIdType = Long.class;
//                DomainClassConverter.this.repositories.getRepositoryInformationFor(targetType.getType()).getIdType();
//                if (!sourceType.equals(TypeDescriptor.valueOf(rawIdType)) && !DomainClassConverter.this.conversionService.canConvert(sourceType.getType(), rawIdType)) {
//                    throw new DomainClassConverter.ConversionMatchAbbreviationException(null);
//                } else {
                    return true;
//                }
            }
        }
    }
}
