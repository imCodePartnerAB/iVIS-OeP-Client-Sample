package se.unlogic.standardutils.string;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import se.unlogic.standardutils.reflection.ReflectionUtils;


public class AnnotatedBeanTagSourceFactory<T> extends BeanTagSourceFactory<T> {

	public AnnotatedBeanTagSourceFactory(Class<T> beanClass, String defaultPrefix) {

		super(beanClass);

		List<Field> fields = ReflectionUtils.getFields(beanClass);

		for(Field field : fields){

			StringTag stringTag = field.getAnnotation(StringTag.class);

			if(stringTag != null){

				@SuppressWarnings("rawtypes")
				Stringyfier stringyfier = getStringfier(stringTag);

				if(StringUtils.isEmpty(stringTag.name())){

					addFieldMapping(defaultPrefix + field.getName(), field, stringyfier);

				}else{

					addFieldMapping(defaultPrefix + stringTag.name(), field, stringyfier);
				}
			}
		}

		List<Method> methods = ReflectionUtils.getMethods(beanClass);

		for(Method method : methods){

			StringTag stringTag = method.getAnnotation(StringTag.class);

			if(stringTag != null){

				@SuppressWarnings("rawtypes")
				Stringyfier stringyfier = getStringfier(stringTag);

				if(StringUtils.isEmpty(stringTag.name())){

					addMethodMapping(defaultPrefix + method.getName(), method, stringyfier);

				}else{

					addMethodMapping(defaultPrefix + stringTag.name(), method, stringyfier);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private Stringyfier getStringfier(StringTag stringTag) {

		if(!stringTag.valueFormatter().equals(DummyStringyfier.class)){

			try{
				return stringTag.valueFormatter().newInstance();

			}catch(InstantiationException e){

				throw new RuntimeException(e);

			}catch(IllegalAccessException e){

				throw new RuntimeException(e);
			}

		}else{

			return null;
		}
	}
}
