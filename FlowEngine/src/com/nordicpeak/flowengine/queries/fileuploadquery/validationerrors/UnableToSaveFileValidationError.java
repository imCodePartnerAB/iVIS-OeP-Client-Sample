package com.nordicpeak.flowengine.queries.fileuploadquery.validationerrors;

import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement(name="validationError")
public class UnableToSaveFileValidationError extends ValidationError {

	@XMLElement
	private final String filename;

	public UnableToSaveFileValidationError(String filename) {

		super("UnableToSaveFile");
		this.filename = filename;
	}

	public String getFilename() {

		return filename;
	}

}
