package se.unlogic.standardutils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public interface ElementableListener<T> {

	/**
	 * @param doc the current XML document
	 * @param element the generated XML element
	 * @param object the object that the element was generated from
	 */
	public void elementGenerated(Document doc, Element element, T object);
}
