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

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.standardutils.xml.XMLUtils;

public class AccessDeniedException extends RequestException {

	private static final Priority PRIORITY = Level.WARN;
	
	private static final long serialVersionUID = -7472197255590972057L;
	private String message;

	public AccessDeniedException(SectionDescriptor sectionDescriptor, ForegroundModuleDescriptor moduleDescriptor) {
		super(sectionDescriptor, moduleDescriptor);
	}

	public AccessDeniedException(SectionDescriptor sectionDescriptor) {
		super(sectionDescriptor);
	}

	public AccessDeniedException(String message) {

		this.message = message;
	}

	@Override
	public String toString() {

		if(message != null){

			return "Access denied by module " + this.getModuleDescriptor() + " in section " + this.getSectionDescriptor() + ". Reason: " + message;

		}else if(this.getModuleDescriptor() != null){

			return "Access denied to module " + this.getModuleDescriptor() + " in section " + this.getSectionDescriptor();

		}else{
			return "Access denied to section " + this.getSectionDescriptor();
		}
	}

	@Override
	public final Element toXML(Document doc) {

		Element accessDeniedExceptionElement = doc.createElement("accessDeniedException");

		if(this.message != null){
			accessDeniedExceptionElement.appendChild(XMLUtils.createCDATAElement("message", this.message, doc));
		}

		accessDeniedExceptionElement.appendChild(this.getSectionDescriptor().toXML(doc));

		if(this.getModuleDescriptor() != null){
			accessDeniedExceptionElement.appendChild(this.getModuleDescriptor().toXML(doc));
		}

		return accessDeniedExceptionElement;
	}


	@Override
	public Integer getStatusCode() {
		return 403;
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
