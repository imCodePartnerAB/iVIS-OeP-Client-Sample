package se.unlogic.standardutils.validation;

import se.unlogic.standardutils.xml.XMLElement;

@XMLElement(name = "validationError")
public class TooLongContentValidationError extends ValidationError {

	@XMLElement
	private final long currentLength;

	@XMLElement
	private final long maxLength;

	public TooLongContentValidationError(int currentLength, long maxLength) {

		super("TooLongFieldContent");
		this.currentLength = currentLength;
		this.maxLength = maxLength;
	}

	public TooLongContentValidationError(String fieldName, long currentLength, long maxLength) {

		super(fieldName, ValidationErrorType.TooLong);
		this.currentLength = currentLength;
		this.maxLength = maxLength;
	}

	public long getMaxLength() {

		return maxLength;
	}

	public long getCurrentLength() {

		return currentLength;
	}
}
