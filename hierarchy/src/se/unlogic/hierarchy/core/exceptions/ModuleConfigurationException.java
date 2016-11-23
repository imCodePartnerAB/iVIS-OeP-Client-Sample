package se.unlogic.hierarchy.core.exceptions;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.xml.XMLUtils;

public class ModuleConfigurationException extends RequestException {

	private static final long serialVersionUID = -7274322530000835429L;
	private static final Priority PRIORITY = Level.WARN;
	private final String message;

	public ModuleConfigurationException() {

		message = null;
	}

	public ModuleConfigurationException(String message) {

		this.message = message;
	}

	@Override
	public String toString() {

		String errorMessage = "Module " + this.getModuleDescriptor() + " in section " + this.getSectionDescriptor() + " is not properly configured.";
		
		if (message != null) {
			return errorMessage + " " + message;
		}

		return errorMessage;
	}

	@Override
	public Element toXML(Document doc) {

		Element moduleConfigurationException = doc.createElement("ModuleConfigurationException");

		if (message != null) {

			moduleConfigurationException.appendChild(XMLUtils.createCDATAElement("message", this.message, doc));
		}

		moduleConfigurationException.appendChild(this.getSectionDescriptor().toXML(doc));
		moduleConfigurationException.appendChild(this.getModuleDescriptor().toXML(doc));

		return moduleConfigurationException;
	}

	@Override
	public Integer getStatusCode() {

		return 503;
	}

	@Override
	public Priority getPriority() {

		return PRIORITY;
	}

	@Override
	public Throwable getThrowable() {

		return null;
	}

}
