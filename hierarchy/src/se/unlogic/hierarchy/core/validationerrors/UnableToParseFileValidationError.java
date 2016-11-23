package se.unlogic.hierarchy.core.validationerrors;

import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name="validationError")
public class UnableToParseFileValidationError extends FileValidationError {

	public UnableToParseFileValidationError(String filename) {

		super("UnableToParseFile", filename);
	}
}
