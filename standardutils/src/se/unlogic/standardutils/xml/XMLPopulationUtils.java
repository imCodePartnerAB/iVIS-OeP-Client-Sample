package se.unlogic.standardutils.xml;

import java.util.ArrayList;
import java.util.List;

import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;


public class XMLPopulationUtils {

	public static <T extends XMLParserPopulateable> List<T> populateBeans(XMLParser xmlParser, String path, Class<T> clazz, List<ValidationError> errors){

		List<XMLParser> matchingElements = xmlParser.getNodes(path);

		if(matchingElements != null){

			List<T> beans = new ArrayList<T>(matchingElements.size());

			for(XMLParser parser : matchingElements){

				T bean = ReflectionUtils.getInstance(clazz);

				try{
					bean.populate(parser);

					beans.add(bean);
					
				}catch(ValidationException e){

					errors.addAll(e.getErrors());
				}
			}

			if(!beans.isEmpty()){

				return beans;
			}
		}

		return null;
	}

	public static <T extends XMLParserPopulateable> T populateBean(XMLParser xmlParser, String path, Class<T> clazz, List<ValidationError> errors) {

		XMLParser matchingElement = xmlParser.getNode(path);

		if(matchingElement != null){

			T bean = ReflectionUtils.getInstance(clazz);

			try{
				bean.populate(matchingElement);

				return bean;

			}catch(ValidationException e){

				errors.addAll(e.getErrors());
			}
		}

		return null;
	}
}
