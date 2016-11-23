package com.nordicpeak.flowengine.queries.basequery;

import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLParserPopulateable;

import com.nordicpeak.flowengine.interfaces.MutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.queries.DescribedQuery;


public abstract class BaseQuery extends GeneratedElementable implements Query, Serializable, DescribedQuery, XMLParserPopulateable {

	private static final long serialVersionUID = 7384942784185489658L;

	@XMLElement
	protected String configURL;

	@XMLElement
	protected MutableQueryDescriptor queryDescriptor;

	public void init(MutableQueryDescriptor mutableQueryDescriptor, String configURL) {

		this.queryDescriptor = mutableQueryDescriptor;
		this.configURL = configURL;
	}

	public abstract Integer getQueryID();

	@Override
	public String getConfigAlias() {

		return configURL;
	}

	@Override
	public MutableQueryDescriptor getQueryDescriptor() {

		return queryDescriptor;
	}

	@Override
	public void toXSD(Document doc) {

		Element complexTypeElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:complexType");
		complexTypeElement.setAttribute("name", getXSDTypeName());

		Element complexContentElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:complexContent");
		complexTypeElement.appendChild(complexContentElement);

		Element extensionElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:extension");
		extensionElement.setAttribute("base", "Query");
		complexContentElement.appendChild(extensionElement);

		Element sequenceElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:sequence");
		extensionElement.appendChild(sequenceElement);

		Element nameElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:element");
		nameElement.setAttribute("name", "Name");
		nameElement.setAttribute("type", "xs:string");
		nameElement.setAttribute("minOccurs", "1");
		nameElement.setAttribute("maxOccurs", "1");
		nameElement.setAttribute("fixed", queryDescriptor.getName());
		sequenceElement.appendChild(nameElement);

		Element valueElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:element");
		valueElement.setAttribute("name", "Value");
		valueElement.setAttribute("type", "xs:string");
		valueElement.setAttribute("minOccurs", "0");
		valueElement.setAttribute("maxOccurs", "1");
		sequenceElement.appendChild(valueElement);

		doc.getDocumentElement().appendChild(complexTypeElement);
	}
	
}
