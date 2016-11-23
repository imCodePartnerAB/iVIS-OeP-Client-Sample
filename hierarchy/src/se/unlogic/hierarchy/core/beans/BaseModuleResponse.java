/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.ResponseType;
import se.unlogic.hierarchy.core.interfaces.ModuleResponse;

public abstract class BaseModuleResponse implements ModuleResponse {

	protected ResponseType responseType;
	protected Element element;
	protected String html;
	protected Document document;
	protected Transformer transformer;

	protected List<ScriptTag> scripts;
	protected List<LinkTag> links;

	public BaseModuleResponse(Element response) {

		this.element = response;
		this.responseType = ResponseType.XML_FOR_CORE_TRANSFORMATION;
	}

	public BaseModuleResponse(String response) {

		this.html = response;
		this.responseType = ResponseType.HTML;
	}

	public BaseModuleResponse(Document response) {

		this.document = response;
		this.responseType = ResponseType.XML_FOR_SEPARATE_TRANSFORMATION;
	}

	public BaseModuleResponse(Document response, Transformer transformer) {

		this.document = response;
		this.transformer = transformer;
		this.responseType = ResponseType.XML_FOR_SEPARATE_TRANSFORMATION;
	}

	@Override
	public void addLink(LinkTag linkTag) {

		if(this.links == null){
			
			this.links = new ArrayList<LinkTag>();
		}

		this.links.add(linkTag);
	}

	@Override
	public void addLinks(List<LinkTag> linkTags) {

		if(this.links == null){
			
			this.links = new ArrayList<LinkTag>();
		}

		this.links.addAll(linkTags);
	}

	@Override
	public void addScript(ScriptTag scriptTag) {

		if(this.scripts == null){
			
			this.scripts = new ArrayList<ScriptTag>();
		}

		this.scripts.add(scriptTag);
	}

	@Override
	public void addScripts(List<ScriptTag> scriptTags) {

		if(this.scripts == null){
			
			this.scripts = new ArrayList<ScriptTag>();
		}

		this.scripts.addAll(scriptTags);
	}

	@Override
	public List<LinkTag> getLinks() {

		return this.links;
	}

	@Override
	public List<ScriptTag> getScripts() {

		return this.scripts;
	}
}
