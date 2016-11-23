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

import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLGenerator;

@XMLElement(name="script")
public class ScriptTag implements Elementable{

	@XMLElement
	private String type;
	
	@XMLElement
	private String src;

	public ScriptTag(String src) {

		this.type = "text/javascript";
		this.src = src;
	}	
	
	public ScriptTag(String type, String src) {

		this.type = type;
		this.src = src;
	}

	public String getType() {

		return type;
	}

	public String getSrc() {

		return src;
	}
	
	@Override
	public Element toXML(Document doc) {

		return XMLGenerator.toXML(this, doc);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((src == null) ? 0 : src.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScriptTag other = (ScriptTag) obj;
		if (src == null) {
			if (other.src != null)
				return false;
		} else if (!src.equals(other.src))
			return false;
		return true;
	}

	
}
