package se.unlogic.hierarchy.core.validationerrors;

import java.util.Collection;
import java.util.Collections;

import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name="validationError")
public class InvalidFileExtensionValidationError extends FileValidationError {

	@XMLElement(fixCase=true, childName="Extension")
	private final Collection<String> allowedExtensions;

	public InvalidFileExtensionValidationError(String filename) {

		super("InvalidFileExtension", filename);

		allowedExtensions = null;
	}

	public InvalidFileExtensionValidationError(String filename, Collection<String> allowedExtensions) {

		super("InvalidFileExtension", filename);

		this.allowedExtensions = allowedExtensions;
	}

	public InvalidFileExtensionValidationError(String filename, String allowedExtension) {

		super("InvalidFileExtension", filename);

		this.allowedExtensions = Collections.singletonList(allowedExtension);
	}

	public Collection<String> getAllowedExtensions() {

		return allowedExtensions;
	}
}
