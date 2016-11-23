/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.standardutils.xml.XMLUtils;

public abstract class MenuItem implements AccessInterface, Cloneable {

	protected String name;
	protected String description;
	protected Integer menuIndex;
	protected String url;
	protected URLType urlType;
	protected MenuItemType itemType;
	protected Integer sectionID;

	protected boolean anonymousAccess;
	protected boolean userAccess;
	protected boolean adminAccess;
	protected Collection<Integer> allowedGroupIDs;
	protected Collection<Integer> allowedUserIDs;

	protected boolean selected;
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Integer getMenuIndex() {
		return menuIndex;
	}

	public void setMenuIndex(Integer menuIndex) {
		this.menuIndex = menuIndex;
	}

	public String getUrl() {
		return url;
	}

	public MenuItemType getItemType() {
		return itemType;
	}

	public URLType getUrlType() {
		return urlType;
	}

	public Integer getSectionID() {
		return sectionID;
	}

	@Override
	public boolean allowsAdminAccess() {
		return this.adminAccess;
	}

	@Override
	public boolean allowsAnonymousAccess() {
		return this.anonymousAccess;
	}

	@Override
	public boolean allowsUserAccess() {
		return this.userAccess;
	}

	@Override
	public Collection<Integer> getAllowedGroupIDs() {
		return this.allowedGroupIDs;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {
		return this.allowedUserIDs;
	}

	public final Element toXML(Document doc) {

		Element menuItemElement = doc.createElement("menuitem");

		if (this.name != null) {
			menuItemElement.appendChild(XMLUtils.createCDATAElement("name", this.name, doc));
		}

		if (this.description != null) {
			menuItemElement.appendChild(XMLUtils.createCDATAElement("description", this.description, doc));
		}

		if (this.menuIndex != null) {
			menuItemElement.appendChild(XMLUtils.createCDATAElement("menuIndex", this.menuIndex.toString(), doc));
		}

		if (this.url != null) {
			menuItemElement.appendChild(XMLUtils.createCDATAElement("url", this.url, doc));
		}

		if (this.urlType != null) {
			menuItemElement.appendChild(XMLUtils.createCDATAElement("urlType", this.urlType.toString(), doc));
		}

		if (this.itemType != null) {
			menuItemElement.appendChild(XMLUtils.createCDATAElement("itemType", this.itemType.toString(), doc));
		}

		if (this.sectionID != null) {
			menuItemElement.appendChild(XMLUtils.createCDATAElement("sectionID", this.sectionID.toString(), doc));
		}

		Element adminAccess = doc.createElement("adminAccess");
		adminAccess.appendChild(doc.createTextNode(Boolean.toString(this.adminAccess)));
		menuItemElement.appendChild(adminAccess);

		Element userAccess = doc.createElement("userAccess");
		userAccess.appendChild(doc.createTextNode(Boolean.toString(this.userAccess)));
		menuItemElement.appendChild(userAccess);

		Element anonymousAccess = doc.createElement("anonymousAccess");
		anonymousAccess.appendChild(doc.createTextNode(Boolean.toString(this.anonymousAccess)));
		menuItemElement.appendChild(anonymousAccess);

		this.getAdditionalXML(doc,menuItemElement);

		if(selected){
			menuItemElement.appendChild(doc.createElement("selected"));
		}
		
		return menuItemElement;

	}

	protected void getAdditionalXML(Document doc, Element menuItem) {}

	@Override
	public String toString() {
		return this.name;
	}

	public MenuItem getSelectedClone() {

		MenuItem clone;
		
		try {
			clone = (MenuItem) super.clone();
			
		} catch (CloneNotSupportedException e) {

			throw new RuntimeException(e);
		}
		
		clone.selected = true;
		
		return clone;
	}

	
	public boolean isSelected() {
	
		return selected;
	}

}
