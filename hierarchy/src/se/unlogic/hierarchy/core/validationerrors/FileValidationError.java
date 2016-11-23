package se.unlogic.hierarchy.core.validationerrors;

import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name="validationError")
public abstract class FileValidationError extends ValidationError {

	@XMLElement
	private String filename;

	public FileValidationError(String messageKey, String filename) {

		super(messageKey);
		this.filename = filename;
	}

	public String getFilename() {

		return filename;
	}
}
