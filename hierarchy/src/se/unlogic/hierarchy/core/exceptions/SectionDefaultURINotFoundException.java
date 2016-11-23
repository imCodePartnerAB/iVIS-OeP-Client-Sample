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
import se.unlogic.webutils.http.URIParser;

public class SectionDefaultURINotFoundException extends RequestException {

	private static final long serialVersionUID = 7438086793987251298L;
	private static final Priority PRIORITY = Level.INFO;
	private final String requestedURI;
	private final boolean loggedIn;

	public SectionDefaultURINotFoundException(SectionDescriptor sectionDescriptor, URIParser uriParser, boolean loggedIn) {

		super(sectionDescriptor);
		
		if (uriParser == null) {
			throw new NullPointerException("URIParser cannot be null!");
		}

		this.requestedURI = uriParser.getFormattedURI();
		this.loggedIn = loggedIn;
	}	
	
	@Override
	public String toString() {

		if(loggedIn){
						
			return "The default URI " + requestedURI + " for logged in users set in section " + this.getSectionDescriptor() + " was not found.";
			
		}else{
			
			return "The default URI " + requestedURI + " for non-logged in users set in section " + this.getSectionDescriptor() + " was not found.";
		}		
	}

	@Override
	public final Element toXML(Document doc) {

		return doc.createElement("SectionDefaultURINotFoundException");
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
