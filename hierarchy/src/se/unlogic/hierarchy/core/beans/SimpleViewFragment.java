package se.unlogic.hierarchy.core.beans;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement(name="ViewFragment")
public class SimpleViewFragment extends GeneratedElementable implements ViewFragment{

	@XMLElement(name="HTML")
	private final String html;
	private final Document debugXML;
	private final List<ScriptTag> scripts;
	private final List<LinkTag> links;

	public SimpleViewFragment(String html, List<ScriptTag> scripts, List<LinkTag> links) {

		super();
		this.html = html;
		this.scripts = scripts;
		this.links = links;
		this.debugXML = null;
	}

	public SimpleViewFragment(String html, Document debugXML, List<ScriptTag> scripts, List<LinkTag> links) {

		super();
		this.html = html;
		this.debugXML = debugXML;
		this.scripts = scripts;
		this.links = links;
	}

	public SimpleViewFragment(String html) {

		super();
		this.html = html;
		this.debugXML = null;
		this.scripts = null;
		this.links = null;
	}

	@Override
	public List<ScriptTag> getScripts() {

		return scripts;
	}

	@Override
	public List<LinkTag> getLinks() {

		return links;
	}

	@Override
	public String getHTML() {

		return html;
	}

	public Document getDebugXML() {

		return debugXML;
	}

	@Override
	public Element toXML(Document doc) {

		Element queryResponseElement = super.toXML(doc);

		if(this.debugXML != null){

			Element debugXMLElement = doc.createElement("DebugXML");
			queryResponseElement.appendChild(debugXMLElement);

			debugXMLElement.appendChild(doc.adoptNode(debugXML.getDocumentElement()));
		}

		return queryResponseElement;
	}
}
