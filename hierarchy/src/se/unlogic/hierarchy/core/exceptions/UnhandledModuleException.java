/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.standardutils.xml.XMLUtils;

public class UnhandledModuleException extends RequestException {

	private static final Priority PRIORITY = Level.ERROR;
	
	private static final long serialVersionUID = -7765550737630555135L;
	private final Throwable throwable;

	public UnhandledModuleException(SectionDescriptor sectionDescriptor, ForegroundModuleDescriptor moduleDescriptor, Throwable throwable) {
		super(sectionDescriptor, moduleDescriptor);

		if (this.getModuleDescriptor() == null) {
			throw new NullPointerException("ModuleDescriptor cannot be null!");
		} else if (throwable == null) {
			throw new NullPointerException("Throwable cannot be null!");
		}

		this.throwable = throwable;
	}

	@Override
	public String toString() {

		return throwable + " thrown by foreground module " + this.getModuleDescriptor() + " in section " + this.getSectionDescriptor() + ".";
	}

	@Override
	public final Element toXML(Document doc) {

		Element unhandledModuleExceptionElement = doc.createElement("unhandledModuleException");

		unhandledModuleExceptionElement.appendChild(XMLUtils.createCDATAElement("throwable", this.throwable.toString(), doc));
		unhandledModuleExceptionElement.appendChild(this.getSectionDescriptor().toXML(doc));
		unhandledModuleExceptionElement.appendChild(this.getModuleDescriptor().toXML(doc));

		return unhandledModuleExceptionElement;
	}

	@Override
	public final Throwable getThrowable() {
		return throwable;
	}

	@Override
	public Integer getStatusCode() {
		return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	}
	
	@Override
	public Priority getPriority() {

		return PRIORITY;
	}	
}
