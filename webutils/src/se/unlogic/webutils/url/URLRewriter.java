package se.unlogic.webutils.url;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.annotations.URLRewrite;
import se.unlogic.webutils.http.RequestUtils;

public class URLRewriter {

	public static final String[] TAG_ATTRIBUTES = { "href", "src", "link", "value" };
	public static final String RELATIVE_URL_MARKER = "??RELATIVE_FROM_CONTEXTPATH??";

	public static String removeAbsoluteLinkUrls(String text, HttpServletRequest req) {

		String absoluteContextPathURL = RequestUtils.getFullContextPathURL(req);

		text = replaceAttributeValues(text, absoluteContextPathURL, RELATIVE_URL_MARKER);

		String relativeContextPathURL = req.getContextPath();

		if (!StringUtils.isEmpty(relativeContextPathURL)) {

			text = replaceAttributeValues(text, relativeContextPathURL, RELATIVE_URL_MARKER);
		}

		return text;
	}

	public static String setAbsoluteLinkUrls(String text, HttpServletRequest req) {

		String relativeContextPathURL = req.getContextPath();

		text = replaceAttributeValues(text, RELATIVE_URL_MARKER, relativeContextPathURL);

		return text;
	}

	public static String setFullAbsoluteLinkUrls(String text, HttpServletRequest req) {

		String fullContextPathURL = RequestUtils.getFullContextPathURL(req);

		text = replaceAttributeValues(text, RELATIVE_URL_MARKER, fullContextPathURL);

		return text;
	}

	public static String replaceAttributeValues(String text, String from, String to) {

		for (String attribute : TAG_ATTRIBUTES) {

			text = text.replace(attribute + "=\"" + from, attribute + "=\"" + to);
			text = text.replace(attribute + "='" + from, attribute + "='" + to);
		}

		return text;
	}

	public static void setAbsoluteLinkUrls(Collection<?> beans, HttpServletRequest req) {

		if (beans != null) {

			for (Object bean : beans) {

				setAbsoluteLinkUrls(bean, req);
			}
		}
	}

	public static void setAbsoluteLinkUrls(Object bean, HttpServletRequest req) {
		
		setAbsoluteLinkUrls(bean, req, false);
	}
	
	public static void setAbsoluteLinkUrls(Object bean, HttpServletRequest req, boolean useFullURL) {

		try {
			List<Field> fields = getAnnotatedFields(bean.getClass());

			for (Field field : fields) {

				Object value = field.get(bean);

				if (value != null) {

					if (value instanceof String) {

						if(useFullURL){
							
							field.set(bean, setFullAbsoluteLinkUrls((String) value, req));
							
						}else{

							field.set(bean, setAbsoluteLinkUrls((String) value, req));
						}
						
					} else if (value instanceof Collection<?>) {

						for (Object object : (Collection<?>) value) {

							setAbsoluteLinkUrls(object, req);
						}

					} else {

						setAbsoluteLinkUrls(value, req);
					}
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}
	
	public static void removeAbsoluteLinkUrls(Object bean, HttpServletRequest req) {

		try {
			List<Field> fields = getAnnotatedFields(bean.getClass());

			for (Field field : fields) {

				Object value = field.get(bean);

				if (value != null) {

					if (value instanceof String) {

						field.set(bean, removeAbsoluteLinkUrls((String) value, req));

					} else if (value instanceof Collection<?>) {

						for (Object object : (Collection<?>) value) {

							removeAbsoluteLinkUrls(object, req);
						}

					} else {

						removeAbsoluteLinkUrls(value, req);
					}
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	public static List<Field> getAnnotatedFields(Class<?> clazz) {

		List<Field> fields = ReflectionUtils.getFields(clazz);

		Iterator<Field> iterator = fields.iterator();

		Field field;

		while (iterator.hasNext()) {

			field = iterator.next();

			if (!field.isAnnotationPresent(URLRewrite.class)) {

				iterator.remove();

			} else if (!field.getType().equals(String.class) || Modifier.isFinal(field.getModifiers())) {

				throw new RuntimeException("Error parsing field " + field.getName() + " in " + clazz + ". Only non final String fields can be annotated with @URLRewrite annotation.");
			}

			ReflectionUtils.fixFieldAccess(field);
		}

		return fields;
	}
	
	public static String removeAbsoluteUrls(String url, HttpServletRequest req) {

		String absoluteContextPathURL = RequestUtils.getFullContextPathURL(req);

		url = url.replace(absoluteContextPathURL, URLRewriter.RELATIVE_URL_MARKER);
		
		String relativeContextPathURL = req.getContextPath();

		if(!StringUtils.isEmpty(relativeContextPathURL)){

			url = url.replace(relativeContextPathURL, URLRewriter.RELATIVE_URL_MARKER);
		}

		return url;
		
	}
	
	public static String setAbsoluteUrls(String url, HttpServletRequest req) {

		String fullContextPathURL = RequestUtils.getFullContextPathURL(req);

		return url.replace(URLRewriter.RELATIVE_URL_MARKER, fullContextPathURL);
		
	}
	
}
