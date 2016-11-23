package se.unlogic.hierarchy.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.AttributeDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.foregroundmodules.userprofile.AttributeMode;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.validation.StringFormatValidator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.webutils.validation.ValidationUtils;


public class AttributeDescriptorUtils {

	public static final Logger log = Logger.getLogger(AttributeDescriptorUtils.class);
	
	public static List<AttributeDescriptor> parseAttributes(String definitions){
		
		if(definitions == null){

			return null;

		}else{

			String[] stringAttributes = definitions.split("\\n");

			List<AttributeDescriptor> attributeDescriptors = new ArrayList<AttributeDescriptor>(stringAttributes.length);

			for(String attribute : stringAttributes){

				String[] attributeParts = attribute.replace("\r", "").split(":");

				if(attributeParts.length == 0 || attributeParts.length > 4){

					log.warn("Unable to parse format of attribute: " + attribute + ", ignoring attribute.");
					continue;
				}

				String name;
				String displayName = null;
				AttributeMode mode;
				Integer maxLength = null;
				StringFormatValidator validator = null;

				if(attributeParts[0].endsWith("*") && attributeParts[0].length() > 1){

					name = attributeParts[0].substring(0, attributeParts[0].length() - 1);
					mode = AttributeMode.REQUIRED;

				}else if(attributeParts[0].endsWith("!") && attributeParts[0].length() > 1){

					name = attributeParts[0].substring(0, attributeParts[0].length() - 1);
					mode = AttributeMode.DISABLED;

				}else{

					name = attributeParts[0];
					mode = AttributeMode.OPTIONAL;
				}

				if(attributeParts.length > 1){

					displayName = attributeParts[1];

					if(attributeParts.length > 2){

						maxLength = NumberUtils.toInt(attributeParts[2]);

						if(maxLength == null || maxLength < 1){

							log.warn("Unable to parse max length of attribute: " + attribute + ", ignoring attribute.");

							continue;
						}

						if(attributeParts.length > 3){

							try{
								validator = (StringFormatValidator)ReflectionUtils.getInstance(attributeParts[3]);

							}catch(Throwable t){

								log.warn("Unable to instansiate validator of attribute: " + attribute + ", ignoring attribute.", t);
							}
						}
					}
				}

				attributeDescriptors.add(new AttributeDescriptor(name, displayName, mode, maxLength, validator));
			}

			if(attributeDescriptors.isEmpty()){

				return null;

			}else{

				return attributeDescriptors;
			}
		}		
	}

	public static void populateAttributes(MutableAttributeHandler attributeHandler, List<AttributeDescriptor> attributes, HttpServletRequest req, List<ValidationError> validationErrors) {

		if(attributeHandler != null && attributes != null){

			for(AttributeDescriptor attributeDescriptor : attributes){

				if(attributeDescriptor.getAttributeMode() == AttributeMode.DISABLED){

					continue;
				}

				String displayName = attributeDescriptor.getDisplayName();

				if(displayName == null){

					displayName = attributeDescriptor.getName();
				}

				String attributeValue = ValidationUtils.validateParameter("attribute-" + attributeDescriptor.getName(), displayName, req, attributeDescriptor.getAttributeMode() == AttributeMode.REQUIRED, null, attributeDescriptor.getMaxLength(), attributeDescriptor.getStringPopulator(), validationErrors);

				if(validationErrors.isEmpty()){

					attributeHandler.setAttribute(attributeDescriptor.getName(), attributeValue);
				}
			}
		}
	}
}
