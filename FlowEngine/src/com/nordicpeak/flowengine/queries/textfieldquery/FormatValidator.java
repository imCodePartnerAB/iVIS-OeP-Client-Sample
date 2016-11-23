package com.nordicpeak.flowengine.queries.textfieldquery;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLUtils;

@XMLElement
public class FormatValidator extends GeneratedElementable {

	@XMLElement
	private String name;
	
	@XMLElement
	private String className;

	@XMLElement
	private String validationMessage;

	public FormatValidator() {}
	
	public FormatValidator(String name, String className, String validationMessage) {
		this.name = name;
		this.className = className;
		this.validationMessage = validationMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}

	@Override
	public Element toXML(Document doc) {
		
		Element validatorElement = super.toXML(doc);
		
		XMLUtils.appendNewElement(doc, validatorElement, "formatValidatorID", className.replace(".", "_"));
		
		return validatorElement;
	}

	
	
}
