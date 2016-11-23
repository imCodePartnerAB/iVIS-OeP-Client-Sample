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
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class VirtualMenuItem extends MenuItem {

	private Integer menuItemID;

	public void setMenuItemID(Integer menuItemID) {
		this.menuItemID = menuItemID;
	}

	public Integer getMenuItemID() {
		return menuItemID;
	}

	public void setItemType(MenuItemType itemType) {
		this.itemType = itemType;
	}

	@Override
	public MenuItemType getItemType() {
		return itemType;
	}

	public void setName(String name){
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setMenuIndex(Integer menuIndex) {
		this.menuIndex = menuIndex;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUrlType(URLType urlType) {
		this.urlType = urlType;
	}

	public void setSectionID(Integer sectionID) {
		this.sectionID = sectionID;
	}

	public void setAdminAccess(boolean adminAccess) {
		this.adminAccess = adminAccess;
	}

	public void setAnonymousAccess(boolean anonumousAccess) {
		this.anonymousAccess = anonumousAccess;
	}

	public void setUserAccess(boolean userAccess) {
		this.userAccess = userAccess;
	}

	public void setAllowedGroupIDs(Collection<Integer> allowedGroupIDs) {
		this.allowedGroupIDs = allowedGroupIDs;
	}

	public void setAllowedUserIDs(Collection<Integer> allowedUserIDs) {
		this.allowedUserIDs = allowedUserIDs;
	}

	@Override
	protected void getAdditionalXML(Document doc, Element menuItemElement){

		Element virtualMenuItemElement = doc.createElement("virtualMenuItem");

		virtualMenuItemElement.appendChild(XMLUtils.createCDATAElement("menuItemID", this.menuItemID.toString(), doc));

		menuItemElement.appendChild(virtualMenuItemElement);

	}
	
	public Element getFullXML(Document doc){
		
		Element element = super.toXML(doc);
		
		AccessUtils.appendAllowedGroupAndUserIDs(doc, element, this);
		
		return element;
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((menuItemID == null) ? 0 : menuItemID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VirtualMenuItem other = (VirtualMenuItem) obj;
		if (menuItemID == null) {
			if (other.menuItemID != null) {
				return false;
			}
		} else if (!menuItemID.equals(other.menuItemID)) {
			return false;
		}
		return true;
	}
}
