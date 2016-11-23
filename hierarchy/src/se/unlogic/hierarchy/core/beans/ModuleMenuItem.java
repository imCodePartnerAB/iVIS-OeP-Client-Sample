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

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;

public class ModuleMenuItem extends MenuItem implements Elementable{

	private final Integer moduleID;
	private String uniqueID;
	private final boolean bundled;

	public ModuleMenuItem(MenuItemDescriptor menuItemDescriptor, ForegroundModuleDescriptor descriptor, boolean bundled){

		this.name = menuItemDescriptor.getName();
		this.description = menuItemDescriptor.getDescription();
		this.url = menuItemDescriptor.getUrl();
		this.urlType = menuItemDescriptor.getUrlType();
		this.itemType = menuItemDescriptor.getItemType();
		this.allowedGroupIDs = menuItemDescriptor.getAllowedGroupIDs();
		this.allowedUserIDs = menuItemDescriptor.getAllowedUserIDs();
		this.adminAccess = menuItemDescriptor.allowsAdminAccess();
		this.userAccess = menuItemDescriptor.allowsUserAccess();
		this.anonymousAccess = menuItemDescriptor.allowsAnonymousAccess();
		this.uniqueID = menuItemDescriptor.getUniqueID();
		this.sectionID = descriptor.getSectionID();
		this.bundled = bundled;
		
		if(descriptor.getModuleID() != null){

			this.moduleID = descriptor.getModuleID();

		}else{

			this.moduleID = menuItemDescriptor.getModuleID();
		}
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public Integer getModuleID() {
		return moduleID;
	}

	@Override
	public void setMenuIndex(Integer menuIndex){
		this.menuIndex = menuIndex;
	}


	@Override
	protected void getAdditionalXML(Document doc, Element menuItemElement){

		Element moduleMenuItemElement = doc.createElement("moduleMenuItem");

		moduleMenuItemElement.appendChild(XMLUtils.createCDATAElement("moduleID", this.moduleID.toString(), doc));

		if(uniqueID != null){
			moduleMenuItemElement.appendChild(XMLUtils.createCDATAElement("uniqueID", this.uniqueID.toString(), doc));
		}

		menuItemElement.appendChild(moduleMenuItemElement);
		
		if(this.bundled) {
			menuItemElement.setAttribute("bundled", "true");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((moduleID == null) ? 0 : moduleID.hashCode());
		result = prime * result
		+ ((uniqueID == null) ? 0 : uniqueID.hashCode());
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
		ModuleMenuItem other = (ModuleMenuItem) obj;
		if (moduleID == null) {
			if (other.moduleID != null) {
				return false;
			}
		} else if (!moduleID.equals(other.moduleID)) {
			return false;
		}
		if (uniqueID == null) {
			if (other.uniqueID != null) {
				return false;
			}
		} else if (!uniqueID.equals(other.uniqueID)) {
			return false;
		}
		return true;
	}

}
