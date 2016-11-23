package se.unlogic.hierarchy.core.enums;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.xml.XMLUtils;

public enum HTTPProtocol {
	HTTP,
	HTTPS;
	
	public static Element getProtocols(Document doc) {
	
		Element protocolsElement = doc.createElement("protocols");
		
		Element protocolElement = doc.createElement("protocol");
		XMLUtils.appendNewElement(doc, protocolElement, "name", "ANY");
		protocolsElement.appendChild(protocolElement);
		
		//TODO it's better to create a new template in common xsl than to do this wrapping
		for(HTTPProtocol hTTPProtocol : HTTPProtocol.values()) {
			protocolElement = doc.createElement("protocol");
			XMLUtils.appendNewElement(doc, protocolElement, "name", hTTPProtocol.toString()); // Common.xsl -> createDropdown requires this wrapping
			XMLUtils.appendNewElement(doc, protocolElement, "value", hTTPProtocol.toString()); // Common.xsl -> createDropdown requires this wrapping
			protocolsElement.appendChild(protocolElement);
		}
		return protocolsElement;
	}
}
