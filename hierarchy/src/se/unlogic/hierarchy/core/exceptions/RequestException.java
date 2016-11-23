/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

import java.util.List;

import org.apache.log4j.Priority;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.SectionMenu;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;

public abstract class RequestException extends Exception {

	private static final long serialVersionUID = -7792092694927984953L;
	private SectionDescriptor sectionDescriptor;
	private ForegroundModuleDescriptor moduleDescriptor;
	private List<BackgroundModuleResponse> backgroundModuleResponses;

	private SectionMenu sectionMenu;

	RequestException(SectionDescriptor sectionDescriptor, ForegroundModuleDescriptor moduleDescriptor) {
		super();

		if (sectionDescriptor == null) {
			throw new NullPointerException("SectionDescriptor cannot be null!");
		}

		this.sectionDescriptor = sectionDescriptor;
		this.moduleDescriptor = moduleDescriptor;
	}

	RequestException(SectionDescriptor sectionDescriptor) {
		super();
		if (sectionDescriptor == null) {
			throw new NullPointerException("SectionDescriptor cannot be null!");
		}
		this.sectionDescriptor = sectionDescriptor;
		moduleDescriptor = null;
	}	
	
	RequestException(){}
	
	public final SectionDescriptor getSectionDescriptor() {

		return sectionDescriptor;
	}

	public final ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	public void setMenu(SectionMenu sectionMenu) {

		this.sectionMenu = sectionMenu;
	}

	public SectionMenu getMenu() {

		return this.sectionMenu;
	}

	@Override
	public abstract String toString();

	public abstract Element toXML(Document doc);

	public abstract Integer getStatusCode();

	public void addBackgroundModuleResponses(List<BackgroundModuleResponse> backgroundModuleResponses) {

		if (this.backgroundModuleResponses == null) {

			this.backgroundModuleResponses = backgroundModuleResponses;

		} else {

			this.backgroundModuleResponses.addAll(backgroundModuleResponses);
		}
	}

	public List<BackgroundModuleResponse> getBackgroundModuleResponses() {

		return backgroundModuleResponses;
	}

	public abstract Priority getPriority();

	public abstract Throwable getThrowable();

	public void setSectionDescriptor(SectionDescriptor sectionDescriptor) {

		this.sectionDescriptor = sectionDescriptor;
	}

	public void setModuleDescriptor(ForegroundModuleDescriptor moduleDescriptor) {

		this.moduleDescriptor = moduleDescriptor;
	}
}
