package se.unlogic.hierarchy.core.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.Attribute;
import se.unlogic.hierarchy.core.interfaces.AttributeSource;
import se.unlogic.hierarchy.core.interfaces.MutableAttribute;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.xml.XMLUtils;


public class SourceAttributeHandler implements MutableAttributeHandler {

	private static final long serialVersionUID = -3623349386491168742L;
	
	protected final AttributeSource atttributeSource;
	protected final int maxNameLength;
	protected final int maxValueLength;	
	
	public SourceAttributeHandler(AttributeSource atttributeSource, int maxNameLength, int maxValueLength) {

		super();
		this.atttributeSource = atttributeSource;
		this.maxNameLength = maxNameLength;
		this.maxValueLength = maxValueLength;
	}

	@Override
	public boolean isSet(String name) {

		return getAttribute(name) != null;
	}

	@Override
	public String getString(String name) {
		
		return getAttributeValue(name);
	}

	@Override
	public Integer getInt(String name) {

		return NumberUtils.toInt(getAttributeValue(name));
	}

	@Override
	public Long getLong(String name) {

		return NumberUtils.toLong(getAttributeValue(name));
	}

	@Override
	public Double getDouble(String name) {

		return NumberUtils.toDouble(getAttributeValue(name));
	}

	@Override
	public Boolean getBoolean(String name) {

		String value = getAttributeValue(name);

		if(value == null){

			return null;
		}

		return Boolean.parseBoolean(value);
	}

	@Override
	public boolean isEmpty() {

		List<? extends Attribute> attributes = atttributeSource.getAttributes();
		
		if(attributes != null){
			
			return attributes.isEmpty();
		}
		
		return true;
	}

	@Override
	public Set<String> getNames() {

		List<? extends Attribute> attributes = atttributeSource.getAttributes();
		
		if(attributes == null || attributes.isEmpty()){
			
			return null;
		}
		
		HashSet<String> attributeSet = new HashSet<String>(attributes.size());
		
		for(Attribute attribute : attributes){
			
			attributeSet.add(attribute.getName());
		}
		
		return attributeSet;
	}

	@Override
	public int size() {

		List<? extends Attribute> attributes = atttributeSource.getAttributes();
		
		if(attributes != null){
			
			return attributes.size();
		}
		
		return 0;
	}

	@Override
	public boolean getPrimitiveBoolean(String name) {

		return Boolean.parseBoolean(getAttributeValue(name));
	}

	@Override
	public Map<String, String> getAttributeMap() {

		List<? extends Attribute> attributes = atttributeSource.getAttributes();
		
		if(attributes == null || attributes.isEmpty()){
			
			return null;
		}
		
		HashMap<String,String> attributeMap = new HashMap<String,String>(attributes.size());
		
		for(Attribute attribute : attributes){
			
			attributeMap.put(attribute.getName(), attribute.getValue());
		}
		
		return attributeMap;
	}

	@Override
	public Element toXML(Document doc) {

		Element attributesElement = doc.createElement("Attributes");

		List<? extends Attribute> attributes = atttributeSource.getAttributes();
		
		if(attributes != null){

			for(Attribute attribute : attributes){

				Element attributeElement = doc.createElement("Attribute");
				XMLUtils.appendNewCDATAElement(doc, attributeElement, "Name", attribute.getName());
				XMLUtils.appendNewCDATAElement(doc, attributeElement, "Value", attribute.getValue());
				attributesElement.appendChild(attributeElement);
			}			
		}

		return attributesElement;
	}

	@Override
	public synchronized boolean setAttribute(String name, Object valueObject) {

		if(valueObject == null){
			
			removeAttribute(name);
			
			return true;
		}
		
		String value = valueObject.toString();
		
		if(name.length() > maxNameLength || value.length() > maxValueLength){

			return false;
		}
		
		MutableAttribute attribute = getAttribute(name);
		
		if(attribute != null){
			
			attribute.setValue(value.toString());
			
		}else{
			
			atttributeSource.addAttribute(name, value);
		}
		
		return true;
	}

	@Override
	public synchronized void removeAttribute(String name) {

		List<? extends Attribute> attributeList = atttributeSource.getAttributes();
		
		if(attributeList != null){
			
			for(Attribute attribute : attributeList){
				
				if(attribute.getName().equalsIgnoreCase(name)){
					
					attributeList.remove(attribute);
					return;
				}
			}
		}
	}

	@Override
	public void clear() {

		List<? extends Attribute> attributeList = atttributeSource.getAttributes();
		
		if(attributeList != null){
			
			attributeList.clear();
		}
	}

	@Override
	public int getMaxNameLength() {

		return maxNameLength;
	}

	@Override
	public int getMaxValueLength() {

		return maxValueLength;
	}

	protected MutableAttribute getAttribute(String name) {

		List<? extends MutableAttribute> attributeList = atttributeSource.getAttributes();
		
		if(attributeList != null){
			
			for(MutableAttribute attribute : attributeList){
				
				if(attribute.getName().equalsIgnoreCase(name)){
					
					return attribute;
				}
			}
		}
		
		return null;
	}
	
	protected String getAttributeValue(String name) {

		Attribute attribute = getAttribute(name);
		
		if(attribute == null){
			
			return null;
		}
		
		return attribute.getValue();
	}	
}
