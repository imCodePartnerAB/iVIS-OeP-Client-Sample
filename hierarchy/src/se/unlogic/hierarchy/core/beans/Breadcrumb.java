/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class Breadcrumb {

	private final String name;
	private final String description;
	private final String url;
	private final URLType urlType;

	public Breadcrumb(String name) {
		super();
		this.name = name;
		this.description = name;
		this.url = null;
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	public Breadcrumb(String name, String url) {
		super();
		this.name = name;
		this.description = name;
		this.url = url;
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	public Breadcrumb(String name, String description, String url) {
		super();
		this.name = name;
		this.description = description;
		this.url = url;
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	public Breadcrumb(String name, String url, URLType urlType) {
		super();
		this.name = name;
		this.description = name;
		this.url = url;
		this.urlType = urlType;

		this.validate();
	}

	public Breadcrumb(String name, String description, String url, URLType urlType) {
		super();
		this.name = name;
		this.description = description;
		this.url = url;
		this.urlType = urlType;

		this.validate();
	}

	public Breadcrumb(SectionDescriptor sectionDescriptor, ForegroundModuleDescriptor moduleDescriptor) {

		this.name = moduleDescriptor.getName();
		this.description = moduleDescriptor.getDescription();
		this.url = sectionDescriptor.getFullAlias() + "/" + moduleDescriptor.getAlias();
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	public Breadcrumb(SimpleForegroundModule foregroundModule, String name, String description, String additionalAlias) {

		this.name = name;
		this.description = description;
		this.url = foregroundModule.getFullAlias() + additionalAlias;
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	public Breadcrumb(SimpleForegroundModule foregroundModule, String name, String additionalAlias) {

		this.name = name;
		this.description = name;
		this.url = foregroundModule.getFullAlias() + additionalAlias;
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	public Breadcrumb(AnnotatedForegroundModule foregroundModule, String name, String description, String additionalAlias) {

		this.name = name;
		this.description = description;
		this.url = foregroundModule.getFullAlias() + additionalAlias;
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	public Breadcrumb(AnnotatedForegroundModule foregroundModule, String name, String additionalAlias) {

		this.name = name;
		this.description = name;
		this.url = foregroundModule.getFullAlias() + additionalAlias;
		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}	
	
	public Breadcrumb(SectionDescriptor sectionDescriptor) {

		this.name = sectionDescriptor.getName();
		this.description = sectionDescriptor.getDescription();

		if(!StringUtils.isEmpty(sectionDescriptor.getFullAlias())){
			this.url = sectionDescriptor.getFullAlias();
		}else{
			this.url = "/";
		}

		this.urlType = URLType.RELATIVE_FROM_CONTEXTPATH;

		this.validate();
	}

	private void validate() {

		/*if (name == null) {

			throw new NullPointerException("Name cannot be null!");

		} else if (url == null) {

			throw new NullPointerException("URL cannot be null!");

		} else if (urlType == null) {

			throw new NullPointerException("URLType cannot be null!");
		}*/

		if(url != null && urlType == null){

			throw new NullPointerException("URLType cannot be null if url is set!");

		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public URLType getUrlType() {
		return urlType;
	}

	public Element toXML(Document doc) {

		Element breadcrumbElement = doc.createElement("breadcrumb");

		XMLUtils.appendNewCDATAElement(doc, breadcrumbElement, "name", StringUtils.substring(this.name, 40, "..."));

		if(description != null){

			XMLUtils.appendNewCDATAElement(doc, breadcrumbElement, "description", this.description);

		}

		if(url != null){

			XMLUtils.appendNewCDATAElement(doc, breadcrumbElement, "url", this.url);

		}

		if(urlType != null){

			XMLUtils.appendNewCDATAElement(doc, breadcrumbElement, "urlType", this.urlType);

		}

		return breadcrumbElement;
	}
}
