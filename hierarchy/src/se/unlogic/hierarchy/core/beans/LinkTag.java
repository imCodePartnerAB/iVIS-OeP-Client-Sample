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

@XMLElement(name="link")
public class LinkTag implements Elementable {

	@XMLElement
	private String rel;

	@XMLElement
	private String type;

	@XMLElement
	private String href;

	@XMLElement
	private String media;

	public LinkTag(String href) {

		this.rel = "stylesheet";
		this.type = "text/css";
		this.href = href;
		this.media = "screen, projection, print";
	}

	public LinkTag(String rel, String type, String href, String media) {

		super();
		this.rel = rel;
		this.type = type;
		this.href = href;
		this.media = media;
	}

	public String getRel() {

		return rel;
	}

	public String getType() {

		return type;
	}

	public String getHref() {

		return href;
	}

	public String getMedia() {

		return media;
	}

	@Override
	public Element toXML(Document doc) {

		return XMLGenerator.toXML(this, doc);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((href == null) ? 0 : href.hashCode());
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
		LinkTag other = (LinkTag) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		return true;
	}
}
