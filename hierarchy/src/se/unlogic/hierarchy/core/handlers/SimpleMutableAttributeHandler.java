package se.unlogic.hierarchy.core.handlers;

import java.util.HashMap;

import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLParserPopulateable;


public class SimpleMutableAttributeHandler extends SimpleAttributeHandler implements MutableAttributeHandler, XMLParserPopulateable {

	private static final long serialVersionUID = -308921362713744890L;

	private final int maxNameLength;
	private final int maxValueLength;

	public SimpleMutableAttributeHandler(int maxNameLength, int maxValueLength) {

		super();
		this.maxNameLength = maxNameLength;
		this.maxValueLength = maxValueLength;
	}

	public SimpleMutableAttributeHandler(HashMap<String, String> attributeMap, int maxNameLength, int maxValueLength) {

		super(attributeMap);
		this.maxNameLength = maxNameLength;
		this.maxValueLength = maxValueLength;
	}

	public SimpleMutableAttributeHandler(XMLParser xmlParser, int maxNameLength, int maxValueLength) throws ValidationException {

		super(xmlParser);
		this.maxNameLength = maxNameLength;
		this.maxValueLength = maxValueLength;
	}

	@Override
	public boolean setAttribute(String name, Object value) {

		String valueString = value.toString();

		if(name.length() > maxNameLength || valueString.length() > maxValueLength){

			return false;
		}

		this.attributeMap.put(name, valueString);

		return true;
	}

	@Override
	public void removeAttribute(String name) {

		this.attributeMap.remove(name);
	}

	@Override
	public void clear() {

		this.attributeMap.clear();
	}

	@Override
	public int getMaxNameLength() {

		return maxNameLength;
	}

	@Override
	public int getMaxValueLength() {

		return maxValueLength;
	}

	@Override
	public void populate(XMLParser xmlParser) throws ValidationException {

		super.populate(xmlParser);
	}
}
