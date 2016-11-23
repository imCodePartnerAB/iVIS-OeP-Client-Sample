package com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery;

import com.imcode.oeplatform.flowengine.interfaces.LinkedMutableElement;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownAlternative;
import com.imcode.oeplatform.flowengine.queries.linked.dropdownquery.LinkedDropDownQueryProviderModule;
import com.nordicpeak.flowengine.annotations.TextTagReplace;
import com.nordicpeak.flowengine.interfaces.ImmutableAlternative;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.basequery.BaseQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.webutils.annotations.URLRewrite;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public abstract class LinkedAlternativesBaseQuery extends BaseQuery implements LinkedAlternativesQuery {

	private static final long serialVersionUID = 7987991587750437624L;

	@WebPopulate(required = true, maxLength = 255, populator = LinkedDropDownQueryProviderModule.ClassPopulator.class)
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@XMLElement
	private String entityClassname;

//	@WebPopulate(required = true, maxLength = 255, populator = LinkedDropDownQueryProviderModule.StringClassPopulator.class)
//	@TextTagReplace
//	@URLRewrite
//	@DAOManaged
//	@XMLElement(valueFormatter = LinkedDropDownQueryProviderModule.ClassStringyfie.class)
//	private Class<?> entityClassname2;
//
//	public Class<?> getEntityClassname2() {
//		return entityClassname2;
//	}
//
//	public void setEntityClassname2(Class<?> entityClassname2) {
//		this.entityClassname2 = entityClassname2;
//	}

	public String getEntityClassname() {
		return entityClassname;
	}

	public void setEntityClassname(String entityClassname) {
		this.entityClassname = entityClassname;
	}


//	private Map<Integer,Integer> alternativeConversionMap;
//
//	@Override
//	public Map<Integer, Integer> getAlternativeConversionMap() {
//
//		return alternativeConversionMap;
//	}
//
//	public void setAlternativeConversionMap(Map<Integer, Integer> alternativeConversionMap) {
//
//		this.alternativeConversionMap = alternativeConversionMap;
//	}

	public void toXSD(Document doc, int maxOccurs) {

		Element complexTypeElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:complexType");
		complexTypeElement.setAttribute("name", getXSDTypeName());

		Element complexContentElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:complexContent");
		complexTypeElement.appendChild(complexContentElement);

		Element extensionElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:extension");
		extensionElement.setAttribute("base", "Query");
		complexContentElement.appendChild(extensionElement);

		Element sequenceElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:sequence");
		extensionElement.appendChild(sequenceElement);
//todo добавить список альтернатив  объектов
		Element nameElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:element");
		nameElement.setAttribute("name", "Name");
		nameElement.setAttribute("type", "xs:string");
		nameElement.setAttribute("minOccurs", "1");
		nameElement.setAttribute("maxOccurs", "1");
		nameElement.setAttribute("fixed", queryDescriptor.getName());
		sequenceElement.appendChild(nameElement);

		doc.getDocumentElement().appendChild(complexTypeElement);

		if(this.getAlternatives() != null){

			Element valueElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:element");
			valueElement.setAttribute("name", "Value");
			valueElement.setAttribute("type", getXSDTypeName() + "Alternative");

			valueElement.setAttribute("minOccurs", "1");
			valueElement.setAttribute("maxOccurs", Integer.toString(maxOccurs));

			sequenceElement.appendChild(valueElement);

			Element simpleTypeElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:simpleType");
			simpleTypeElement.setAttribute("name", getXSDTypeName() + "Alternative");

			Element restrictionElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:restriction");
			restrictionElement.setAttribute("base", "xs:string");
			simpleTypeElement.appendChild(restrictionElement);

			for(LinkedDropDownAlternative alternative : this.getAlternatives()){

				Element enumerationElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:enumeration");
				enumerationElement.setAttribute("representation", alternative.getName());
				restrictionElement.appendChild(enumerationElement);
			}

//			if(getFreeTextAlternative() != null){
//
//				Element enumerationElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:enumeration");
//				enumerationElement.setAttribute("value", getFreeTextAlternative());
//				restrictionElement.appendChild(enumerationElement);
//
//				Element freeTextElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:element");
//				freeTextElement.setAttribute("name", "TextAlternative");
//				freeTextElement.setAttribute("type", "xs:string");
//				freeTextElement.setAttribute("minOccurs", "0");
//				freeTextElement.setAttribute("maxOccurs", "1");
//				sequenceElement.appendChild(freeTextElement);
//			}

			doc.getDocumentElement().appendChild(simpleTypeElement);
		}
	}

	@Override
	public List<? extends LinkedAlternativesQueryInstance> getInstances(List<Integer> queryInstanceIDs, QueryHandler queryHandler) throws SQLException {

		return LinkedAlternativeQueryUtils.getGenericFixedAlternativesQueryCallback(this.getClass(), queryHandler, getQueryDescriptor().getQueryTypeID()).getQueryInstances(this, queryInstanceIDs);
	}
}
