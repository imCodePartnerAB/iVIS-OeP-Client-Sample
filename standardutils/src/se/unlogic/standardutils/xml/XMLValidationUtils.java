package se.unlogic.standardutils.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;

public class XMLValidationUtils {

	public static String validateNotEmptyParameter(String fieldName, XMLParser xmlParser, List<ValidationError> errors) {

		String parameter = xmlParser.getString(fieldName);

		if (StringUtils.isEmpty(parameter)) {
			errors.add(new ValidationError(fieldName, ValidationErrorType.RequiredField));
			return null;
		} else {
			return parameter;
		}
	}

	public static <T> T validateParameter(String fieldName, XMLParser xmlParser, boolean required, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		return validateParameter(fieldName, xmlParser, required, null, null, typePopulator, errors);
	}

	public static <T> T validateParameter(String fieldName, XMLParser xmlParser, boolean required, Integer minLength, Integer maxLength, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		String value = xmlParser.getString(fieldName);

		if (StringUtils.isEmpty(value)) {

			if (required) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.RequiredField));

			}

			return null;
		}

		value = value.trim();

		if (maxLength != null && value.length() > maxLength) {

			errors.add(new ValidationError(fieldName, ValidationErrorType.TooLong));
			return null;

		} else if (minLength != null && value.length() < minLength) {

			errors.add(new ValidationError(fieldName, ValidationErrorType.TooShort));
			return null;
		}

		if (!typePopulator.validateFormat(value)) {

			errors.add(new ValidationError(fieldName, ValidationErrorType.InvalidFormat));
			return null;
		}

		return typePopulator.getValue(value);
	}

	public static <T> List<T> validateParameters(String fieldName, XMLParser xmlParser, boolean required, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		return validateParameters(fieldName, xmlParser, required, null, null, typePopulator, errors);
	}

	public static <T> List<T> validateParameters(String fieldName, XMLParser xmlParser, boolean required, Integer minLength, Integer maxLength, BeanStringPopulator<T> typePopulator, Collection<ValidationError> errors) {

		List<String> parameterValues = xmlParser.getStrings(fieldName);

		if (parameterValues == null || parameterValues.isEmpty() || !XMLValidationUtils.checkParameterValueLengths(parameterValues, 1)) {

			if (required) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.RequiredField));

			}

			return null;
		}

		List<T> values = new ArrayList<T>();

		for (String value : parameterValues) {

			value = value.trim();

			if (maxLength != null && value.length() > maxLength) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.TooLong));
				return null;

			} else if (minLength != null && value.length() < minLength) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.TooShort));
				return null;
			}

			if (!typePopulator.validateFormat(value)) {

				errors.add(new ValidationError(fieldName, ValidationErrorType.InvalidFormat));
				return null;
			}

			values.add(typePopulator.getValue(value));
		}

		return values;

	}

	private static boolean checkParameterValueLengths(List<String> parameterValues, int minLength) {

		for (String parameterValue : parameterValues) {
			
			if (parameterValue.length() < minLength) {
		
				return false;
			}
		}
		
		return true;
	}
}
