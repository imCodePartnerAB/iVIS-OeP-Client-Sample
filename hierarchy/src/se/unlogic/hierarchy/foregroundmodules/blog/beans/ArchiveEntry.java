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

import se.unlogic.standardutils.enums.Month;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;

public class ArchiveEntry implements Elementable {

	private Month month;
	private int postCount;
	private int year;

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public int getPostCount() {
		return postCount;
	}

	public void setPostCount(int postCount) {
		this.postCount = postCount;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public Element toXML(Document doc) {
		Element archiveEntryElement = doc.createElement("ArchiveEntry");

		archiveEntryElement.appendChild(XMLUtils.createElement("month", month.toString().toLowerCase(), doc));
		archiveEntryElement.appendChild(XMLUtils.createElement("postCount", postCount + "", doc));
		archiveEntryElement.appendChild(XMLUtils.createElement("year", year + "", doc));

		return archiveEntryElement;
	}
}
