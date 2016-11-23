package se.unlogic.hierarchy.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import se.unlogic.hierarchy.core.annotations.FCKContent;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.Page;
import se.unlogic.standardutils.reflection.ReflectionUtils;


public class FCKUtils {

	public static String removeAbsoluteFileUrls(String text, String absoluteFileURL) {

		for (String attribute : BaseFileAccessValidator.TAG_ATTRIBUTES) {

			text = text.replace(attribute + "=\"" + absoluteFileURL, attribute + "=\"" + Page.RELATIVE_PATH_MARKER);
			text = text.replace(attribute + "='" + absoluteFileURL, attribute + "='" + Page.RELATIVE_PATH_MARKER);
		}

		return text;
	}

	public static String setAbsoluteFileUrls(String text, String absoluteFileURL) {

		for (String attribute : BaseFileAccessValidator.TAG_ATTRIBUTES) {

			text = text.replace(attribute + "=\"" + Page.RELATIVE_PATH_MARKER, attribute + "=\"" + absoluteFileURL);
			text = text.replace(attribute + "='" + Page.RELATIVE_PATH_MARKER, attribute + "='" + absoluteFileURL);
		}

		return text;
	}

	public static void setAbsoluteFileUrls(Object bean, String absoluteFileURL){

		try {
			List<Field> fields = getAnnotatedFields(bean.getClass());

			for(Field field : fields){

				Object value = field.get(bean);

				if(value != null){

					if(value instanceof String){

						field.set(bean, setAbsoluteFileUrls((String)value, absoluteFileURL));

					}else if(value instanceof Collection<?>){

						for(Object object : (Collection<?>)value){

							setAbsoluteFileUrls(object, absoluteFileURL);
						}

					}else{

						setAbsoluteFileUrls(value, absoluteFileURL);
					}
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	public static void removeAbsoluteFileUrls(Object bean, String absoluteFileURL){

		try {
			List<Field> fields = getAnnotatedFields(bean.getClass());

			for(Field field : fields){

				Object value = field.get(bean);

				if(value != null){

					if(value instanceof String){

						field.set(bean, removeAbsoluteFileUrls((String)value, absoluteFileURL));

					}else if(value instanceof Collection<?>){

						for(Object object : (Collection<?>)value){

							removeAbsoluteFileUrls(object, absoluteFileURL);
						}

					}else{

						removeAbsoluteFileUrls(value, absoluteFileURL);
					}
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	public static List<Field> getAnnotatedFields(Class<?> clazz){

		List<Field> fields = ReflectionUtils.getFields(clazz);

		Iterator<Field> iterator = fields.iterator();

		Field field;

		while(iterator.hasNext()){

			field = iterator.next();

			if(!field.isAnnotationPresent(FCKContent.class)){

				iterator.remove();

			}else if(Modifier.isFinal(field.getModifiers())){

				throw new RuntimeException("Error parsing field " + field.getName() + " in " + clazz + ". Only non final fields can be annotated with @FCKContent annotation.");
			}

			ReflectionUtils.fixFieldAccess(field);
		}

		return fields;
	}
}
