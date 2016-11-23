package se.unlogic.hierarchy.core.validationerrors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.io.BinarySizeFormater;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLUtils;

//TODO fix case...
@XMLElement(name = "validationError")
public class FileSizeLimitExceededValidationError extends FileValidationError {

	private final long size;

	private final long maxFileSize;

	public FileSizeLimitExceededValidationError(String filename, long size, long maxFileSize) {

		super("FileSizeLimitExceeded", filename);
		this.size = size;
		this.maxFileSize = maxFileSize;
	}

	@Override
	public Element toXML(Document doc) {

		Element validationErrorElement = super.toXML(doc);

		XMLUtils.appendNewElement(doc, validationErrorElement, "size", BinarySizeFormater.getFormatedSize(size));
		XMLUtils.appendNewElement(doc, validationErrorElement, "maxFileSize", BinarySizeFormater.getFormatedSize(maxFileSize));

		return validationErrorElement;
	}

	public long getSize() {

		return size;
	}

	public long getMaxFileSize() {

		return maxFileSize;
	}
}
