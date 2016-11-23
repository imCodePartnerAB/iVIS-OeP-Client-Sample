/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.beans;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;

public class TagEntry implements Elementable {

	private String tagName;
	private int postCount;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public int getPostCount() {
		return postCount;
	}

	public void setPostCount(int postCount) {
		this.postCount = postCount;
	}

	@Override
	public Element toXML(Document doc) {
		Element archiveEntryElement = doc.createElement("TagEntry");

		archiveEntryElement.appendChild(XMLUtils.createCDATAElement("tagName", tagName, doc));
		archiveEntryElement.appendChild(XMLUtils.createElement("postCount", postCount + "", doc));

		return archiveEntryElement;
	}
}
