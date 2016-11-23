/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;

public class SectionMenu {

	private final SectionDescriptor sectionDescriptor;
	private final ArrayList<MenuItem> menuItems;

	public SectionMenu(ArrayList<MenuItem> menuItems, SectionDescriptor sectionDescriptor) {

		super();
		this.menuItems = menuItems;
		this.sectionDescriptor = sectionDescriptor;
	}

	public Element toXML(Document doc) {

		Element menuitems = doc.createElement("menu");
		menuitems.setAttribute("sectionID", sectionDescriptor.getSectionID().toString());
		menuitems.setAttribute("sectionName", sectionDescriptor.getName());
		menuitems.setAttribute("sectionAlias", sectionDescriptor.getFullAlias());

		for (MenuItem menuItem : this.menuItems) {
			menuitems.appendChild(menuItem.toXML(doc));
		}

		return menuitems;
	}

	public Integer getSectionID() {

		return sectionDescriptor.getSectionID();
	}

	public ArrayList<MenuItem> getMenuItems() {

		return menuItems;
	}

	
	public SectionDescriptor getSectionDescriptor() {
	
		return sectionDescriptor;
	}
}
