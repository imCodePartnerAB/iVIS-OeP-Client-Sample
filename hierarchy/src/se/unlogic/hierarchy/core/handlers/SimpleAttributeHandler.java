package se.unlogic.hierarchy.core.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;


public class SimpleAttributeHandler implements AttributeHandler {

	private static final long serialVersionUID = -8731009004825362810L;

	protected final HashMap<String,String> attributeMap;

	public SimpleAttributeHandler(){

		this.attributeMap = new HashMap<String,String>(0);
	}

	public SimpleAttributeHandler(HashMap<String,String> attributeMap){

		this.attributeMap = attributeMap;
	}

	public SimpleAttributeHandler(XMLParser xmlParser) throws ValidationException{

		attributeMap = new HashMap<String,String>();

		populate(xmlParser);
	}

	@Override
	public boolean isSet(String name) {

		return attributeMap.containsKey(name);
	}

	@Override
	public String getString(String name) {

		return attributeMap.get(name);
	}

	@Override
	public Integer getInt(String name) {

		return NumberUtils.toInt(attributeMap.get(name));
	}

	@Override
	public Long getLong(String name) {

		return NumberUtils.toLong(attributeMap.get(name));
	}

	@Override
	public Double getDouble(String name) {

		return NumberUtils.toDouble(attributeMap.get(name));
	}

	@Override
	public Boolean getBoolean(String name) {

		String value = attributeMap.get(name);

		if(value == null){

			return null;
		}

		return Boolean.parseBoolean(value);
	}

	@Override
	public boolean isEmpty() {

		return attributeMap.isEmpty();
	}

	@Override
	public Set<String> getNames() {

		return new HashSet<String>(attributeMap.keySet());
	}

	@Override
	public int size() {

		return attributeMap.size();
	}

	@Override
	public boolean getPrimitiveBoolean(String name) {

		return Boolean.parseBoolean(attributeMap.get(name));
	}

	@Override
	public Map<String, String> getAttributeMap() {

		return new HashMap<String, String>(attributeMap);
	}

	@Override
	public Element toXML(Document doc) {

		Element attributesElement = doc.createElement("Attributes");

		for(Entry<String,String> entry : attributeMap.entrySet()){

			Element attributeElement = doc.createElement("Attribute");
			XMLUtils.appendNewCDATAElement(doc, attributeElement, "Name", entry.getKey());
			XMLUtils.appendNewCDATAElement(doc, attributeElement, "Value", entry.getValue());
			attributesElement.appendChild(attributeElement);
		}

		return attributesElement;
	}

	protected void populate(XMLParser xmlParser) throws ValidationException {

		List<XMLParser> attributes = xmlParser.getNodes("Attribute");

		for(XMLParser attribute : attributes){

			String name = attribute.getString("Name");

			if(StringUtils.isEmpty(name)){

				continue;
			}

			String value = attribute.getString("Value");

			if(value != null){

				this.attributeMap.put(name, value);
			}
		}
	}
}
