package se.unlogic.standardutils.xml;

import se.unlogic.standardutils.validation.ValidationException;


public interface XMLParserPopulateable {

	public void populate(XMLParser xmlParser) throws ValidationException;
}
