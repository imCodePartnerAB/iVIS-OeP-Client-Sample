package se.unlogic.webutils.url;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.RequestUtils;


public class SingleURLRewriter {

	public static final String RELATIVE_URL_MARKER = "??RELATIVE_FROM_CONTEXTPATH??";

	public static String removeAbsoluteLinkURL(String text, HttpServletRequest req) {

		String absoluteContextPathURL = RequestUtils.getFullContextPathURL(req);

		if(text.startsWith(absoluteContextPathURL)){

			text = text.replaceFirst(Pattern.quote(absoluteContextPathURL), RELATIVE_URL_MARKER);

			return text;
		}

		String relativeContextPathURL = req.getContextPath();

		if(!StringUtils.isEmpty(relativeContextPathURL) && text.startsWith(relativeContextPathURL)){

			text = text.replaceFirst(Pattern.quote(relativeContextPathURL), RELATIVE_URL_MARKER);
		}

		return text;
	}

	public static String setAbsoluteLinkURL(String text, HttpServletRequest req, boolean useFullContextPathURL) {

		if(text.startsWith(RELATIVE_URL_MARKER)){

			if(useFullContextPathURL){

				text = text.replace(RELATIVE_URL_MARKER, RequestUtils.getFullContextPathURL(req));

			}else{

				text = text.replace(RELATIVE_URL_MARKER, req.getContextPath());
			}
		}

		return text;
	}

	public static void setAbsoluteLinkURLs(Collection<?> beans, HttpServletRequest req, boolean useFullContextPathURL){

		if(beans != null){

			for(Object bean : beans){

				setAbsoluteLinkURLs(bean, req, useFullContextPathURL);
			}
		}
	}

	public static void setAbsoluteLinkURLs(Object bean, HttpServletRequest req, boolean useFullContextPathURL){

		try {
			List<Field> fields = URLRewriter.getAnnotatedFields(bean.getClass());

			for(Field field : fields){

				Object value = field.get(bean);

				if(value != null){

					if(value instanceof String){

						field.set(bean, setAbsoluteLinkURL((String)value, req, useFullContextPathURL));

					}else if(value instanceof Collection<?>){

						for(Object object : (Collection<?>)value){

							setAbsoluteLinkURLs(object, req, useFullContextPathURL);
						}

					}else{

						setAbsoluteLinkURLs(value, req, useFullContextPathURL);
					}
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}

	public static void removeAbsoluteLinkURLs(Object bean, HttpServletRequest req){

		try {
			List<Field> fields = URLRewriter.getAnnotatedFields(bean.getClass());

			for(Field field : fields){

				Object value = field.get(bean);

				if(value != null){

					if(value instanceof String){

						field.set(bean, removeAbsoluteLinkURL((String)value, req));

					}else if(value instanceof Collection<?>){

						for(Object object : (Collection<?>)value){

							removeAbsoluteLinkURLs(object, req);
						}

					}else{

						removeAbsoluteLinkURLs(value, req);
					}
				}
			}

		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
		}
	}
}
