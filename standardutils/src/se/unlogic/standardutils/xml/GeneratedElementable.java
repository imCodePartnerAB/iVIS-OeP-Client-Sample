package se.unlogic.standardutils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class GeneratedElementable implements Elementable {

	public Element toXML(Document doc) {

		return XMLGenerator.toXML(this, doc);
	}

}
