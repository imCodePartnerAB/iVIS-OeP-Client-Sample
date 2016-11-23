package se.unlogic.hierarchy.foregroundmodules.test.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;

import se.unlogic.hierarchy.foregroundmodules.rest.ResponseHandler;
import se.unlogic.standardutils.xml.XMLUtils;


public class XMLResponseHandler implements ResponseHandler<Document> {

	private final String encoding;
	
	public XMLResponseHandler(String encoding) {

		super();
		this.encoding = encoding;
	}

	@Override
	public Class<? extends Document> getType() {

		return Document.class;
	}

	@Override
	public void handleResponse(Document doc, HttpServletResponse res) throws IOException, TransformerFactoryConfigurationError, TransformerException {

		res.setContentType("text/xml");
		
		XMLUtils.writeXML(doc, res.getOutputStream(), true, encoding);
	}
}
