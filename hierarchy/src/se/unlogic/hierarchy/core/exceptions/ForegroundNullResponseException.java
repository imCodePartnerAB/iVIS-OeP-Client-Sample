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


public class ForegroundNullResponseException extends RequestException {

	private static final Priority PRIORITY = Level.INFO;
	
	private static final long serialVersionUID = 2237262634158628318L;

	@Override
	public Integer getStatusCode() {
		return null;
	}

	@Override
	public String toString() {

		return "Foreground module " + this.getModuleDescriptor() + " in section " + this.getSectionDescriptor() + " returned null as module response without submitting a direct response.";
	}

	@Override
	public Element toXML(Document doc) {

		return doc.createElement("noModuleResponse");
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
