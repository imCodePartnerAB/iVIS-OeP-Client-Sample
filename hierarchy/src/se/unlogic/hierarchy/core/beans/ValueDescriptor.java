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

import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class ValueDescriptor {

	private final String name;
	private final String value;

	public ValueDescriptor(String name, Object value) {
		
		this(name,value.toString());
	}
	
	public ValueDescriptor(String name, String value) {
		super();

		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Name cannot be empty");
		} else if (StringUtils.isEmpty(value)) {
			throw new IllegalArgumentException("Value cannot be empty");
		}

		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public Element toXML(Document doc) {

		Element valueDescriptorElement = doc.createElement("valueDescriptor");

		valueDescriptorElement.appendChild(XMLUtils.createElement("name", this.name, doc));
		valueDescriptorElement.appendChild(XMLUtils.createElement("value", this.value, doc));

		return valueDescriptorElement;
	}
}
