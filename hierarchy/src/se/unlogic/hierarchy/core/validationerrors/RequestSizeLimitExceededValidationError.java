package se.unlogic.hierarchy.core.validationerrors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.io.BinarySizeFormater;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLUtils;

@XMLElement(name="validationError")
public class RequestSizeLimitExceededValidationError extends ValidationError {

	private final long actualSize;
	private final long maxAllowedSize;

	public RequestSizeLimitExceededValidationError(long actualSize, long maxAllowedSize) {

		super("RequestSizeLimitExceeded");
		this.actualSize = actualSize;
		this.maxAllowedSize = maxAllowedSize;
	}

	public long getActualSize() {

		return actualSize;
	}

	public long getMaxAllowedSize() {

		return maxAllowedSize;
	}

	@Override
	public Element toXML(Document doc) {

		Element validationErrorElement = super.toXML(doc);

		XMLUtils.appendNewElement(doc, validationErrorElement, "actualSize", BinarySizeFormater.getFormatedSize(actualSize));
		XMLUtils.appendNewElement(doc, validationErrorElement, "maxAllowedSize", BinarySizeFormater.getFormatedSize(maxAllowedSize));

		return validationErrorElement;
	}
}
