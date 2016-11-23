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
import se.unlogic.standardutils.xml.XMLUtils;

public class SectionMenuItem extends MenuItem implements Cloneable{

	protected SectionMenu sectionMenu;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((subSectionID == null) ? 0 : subSectionID.hashCode());
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
		SectionMenuItem other = (SectionMenuItem) obj;
		if (subSectionID == null) {
			if (other.subSectionID != null) {
				return false;
			}
		} else if (!subSectionID.equals(other.subSectionID)) {
			return false;
		}
		return true;
	}

	private Integer subSectionID;

	public SectionMenuItem(){
		this.itemType = MenuItemType.SECTION;
	}

	public void setSubSectionID(Integer subSectionID) {
		this.subSectionID = subSectionID;
	}

	public Integer getSubSectionID() {
		return subSectionID;
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

		Element sectionMenuItemElement = doc.createElement("sectionMenuItem");

		sectionMenuItemElement.appendChild(XMLUtils.createCDATAElement("subSectionID", this.subSectionID.toString(), doc));

		menuItemElement.appendChild(sectionMenuItemElement);
		
		if(sectionMenu != null){
		
			menuItemElement.appendChild(sectionMenu.toXML(doc));
		}
	}

	public MenuItem clone(SectionMenu subMenu, boolean selected) {

		try {
			SectionMenuItem clone = (SectionMenuItem) this.clone();
		
			clone.sectionMenu = subMenu;
			
			if(selected){
				clone.selected = true;
			}
			
			return clone;
			
		} catch (CloneNotSupportedException e) {
			
			throw new RuntimeException(e);
		}
	}
}
