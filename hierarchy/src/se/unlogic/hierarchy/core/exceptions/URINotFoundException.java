/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;

public class URINotFoundException extends RequestException {

	private static final Priority PRIORITY = Level.INFO;
	private static final long serialVersionUID = 3838684975659674006L;
	private final String requestedURI;

	public URINotFoundException(String requestedURI) {

		if (requestedURI == null) {
			throw new NullPointerException("requestedURI cannot be null!");
		}

		this.requestedURI = requestedURI;
	}

	public URINotFoundException(URIParser uriParser) {

		if (uriParser == null) {
			throw new NullPointerException("URIParser cannot be null!");
		}

		this.requestedURI = uriParser.getFormattedURI();
	}

	public URINotFoundException(SectionDescriptor sectionDescriptor, URIParser uriParser) {

		super(sectionDescriptor);
		
		if (uriParser == null) {
			throw new NullPointerException("URIParser cannot be null!");
		}

		this.requestedURI = uriParser.getFormattedURI();
	}	
	
	@Override
	public String toString() {

		if (this.getModuleDescriptor() == null) {
			return "Requested URI " + requestedURI + " not found in section " + this.getSectionDescriptor() + ".";
		} else {
			return "Requested URI " + requestedURI + " not found in module " + this.getModuleDescriptor() + " in section " + this.getSectionDescriptor() + ".";
		}
	}

	@Override
	public final Element toXML(Document doc) {

		Element uriNotFoundExceptionElement = doc.createElement("URINotFoundException");

		uriNotFoundExceptionElement.appendChild(XMLUtils.createCDATAElement("requestedURI", this.requestedURI, doc));
		uriNotFoundExceptionElement.appendChild(this.getSectionDescriptor().toXML(doc));

		if (this.getModuleDescriptor() != null) {
			uriNotFoundExceptionElement.appendChild(this.getModuleDescriptor().toXML(doc));
		}

		return uriNotFoundExceptionElement;
	}

	public String getRequestedURI() {
		return requestedURI;
	}

	@Override
	public Integer getStatusCode() {
		return 404;
	}
	
	@Override
	public Priority getPriority() {

		return PRIORITY;
	}

	@Override
	public Throwable getThrowable() {

		return null;
	}	
}
