package se.unlogic.standardutils.db.tableversionhandler;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;


public class XMLDBScriptProvider implements DBScriptProvider {

	private XMLParser settingNode;

	public XMLDBScriptProvider(Document doc) {

		settingNode = new XMLParser(doc);
	}

	public XMLDBScriptProvider(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {

		settingNode = new XMLParser(XMLUtils.parseXML(inputStream, false, false));
	}

	public DBScript getScript(int version) {

		XMLParser dbScriptNode = settingNode.getNode("/DBScripts/Script[@version='" + version + "']");

		if(dbScriptNode == null){

			return null;
		}

		return new XMLDBScript(dbScriptNode);
	}
}
