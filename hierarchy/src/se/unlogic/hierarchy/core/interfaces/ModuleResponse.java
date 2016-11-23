/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.util.List;

import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.LinkTag;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.enums.ResponseType;

public interface ModuleResponse {

	Element getElement();

	String getHtml();

	Document getDocument();

	ResponseType getResponseType();

	Transformer getTransformer();

	void setTransformer(Transformer transformer);

	ModuleDescriptor getModuleDescriptor();
	
	void addScripts(List<ScriptTag> scriptTags);

	void addLinks(List<LinkTag> linkTags);
	
	void addScript(ScriptTag scriptTag);

	void addLink(LinkTag linkTag);
	
	List<ScriptTag> getScripts();
	
	List<LinkTag> getLinks();
}
