package se.unlogic.standardutils.xml;

import org.w3c.dom.Element;

import se.unlogic.standardutils.validation.ValidationException;


public interface XMLPopulateable {

	public void populate(Element element) throws ValidationException;
}
