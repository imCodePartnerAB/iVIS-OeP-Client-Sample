package se.unlogic.hierarchy.core.validationerrors;

import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement(name = "validationError")
public class InvalidFormatValidationError extends ValidationError {

	@XMLElement
	private final String invalidFormatMessage; 
	
	public InvalidFormatValidationError(String fieldName, String invalidFormatMessage) {

		super(fieldName, ValidationErrorType.InvalidFormat);
		this.invalidFormatMessage = invalidFormatMessage;
	}

	public String getInvalidFormatMessage() {

		return invalidFormatMessage;
	}

}
