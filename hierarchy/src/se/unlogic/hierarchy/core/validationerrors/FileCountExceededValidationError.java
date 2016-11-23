package se.unlogic.hierarchy.core.validationerrors;

import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name = "validationError")
public class FileCountExceededValidationError extends ValidationError {

	@XMLElement
	private Integer maxFileCount;

	public FileCountExceededValidationError(Integer maxFileCount) {

		super("MaxFileCountReached");
		
		this.maxFileCount = maxFileCount;
	}

	public Integer getMaxFileCount() {

		return maxFileCount;
	}

}
