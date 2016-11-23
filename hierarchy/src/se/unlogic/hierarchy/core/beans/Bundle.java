/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.standardutils.xml.XMLUtils;

public class Bundle extends MenuItem implements Cloneable {

	private final Integer moduleID;
	private String uniqueID;
	private ArrayList<ModuleMenuItem> moduleMenuItems;

	public Bundle(BundleDescriptor bundleDescriptor, ForegroundModuleDescriptor descriptor) {

		this.name = bundleDescriptor.getName();
		this.description = bundleDescriptor.getDescription();
		this.url = bundleDescriptor.getUrl();
		this.urlType = bundleDescriptor.getUrlType();
		this.itemType = bundleDescriptor.getItemType();
		this.allowedGroupIDs = bundleDescriptor.getAllowedGroupIDs();
		this.allowedUserIDs = bundleDescriptor.getAllowedUserIDs();
		this.adminAccess = bundleDescriptor.allowsAdminAccess();
		this.userAccess = bundleDescriptor.allowsUserAccess();
		this.anonymousAccess = bundleDescriptor.allowsAnonymousAccess();

		this.moduleMenuItems = new ArrayList<ModuleMenuItem>();

		if(bundleDescriptor.getMenuItemDescriptors() != null){

			List<? extends MenuItemDescriptor> tempMenuItemDescriptors = bundleDescriptor.getMenuItemDescriptors();

			for (MenuItemDescriptor menuItemDescriptor : tempMenuItemDescriptors) {
				this.moduleMenuItems.add(new ModuleMenuItem(menuItemDescriptor, descriptor, true));
			}
		}

		this.sectionID = descriptor.getSectionID();
		this.moduleID = descriptor.getModuleID();
		this.uniqueID = bundleDescriptor.getUniqueID();
	}

	public ArrayList<ModuleMenuItem> getModuleMenuItems() {
		return this.moduleMenuItems;
	}

	public Integer getModuleID() {
		return moduleID;
	}

	@Override
	public void setMenuIndex(Integer menuIndex) {
		this.menuIndex = menuIndex;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	@Override
	protected void getAdditionalXML(Document doc, Element menuItemElement) {

		Element bundleElement = doc.createElement("bundle");

		bundleElement.appendChild(XMLUtils.createCDATAElement("moduleID", this.moduleID.toString(), doc));
		bundleElement.appendChild(XMLUtils.createCDATAElement("uniqueID", this.uniqueID, doc));

		menuItemElement.appendChild(bundleElement);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((moduleID == null) ? 0 : moduleID.hashCode());
		result = prime * result + ((uniqueID == null) ? 0 : uniqueID.hashCode());
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
		Bundle other = (Bundle) obj;
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

	@Override
	public Bundle clone() {

		try {
			Bundle bundle = (Bundle) super.clone();
			bundle.moduleMenuItems = new ArrayList<ModuleMenuItem>(this.moduleMenuItems);
			return bundle;
		} catch (CloneNotSupportedException e) {
			// This can never happen since we implement clonable...
			throw new RuntimeException(e);
		}
	}

	public Element toFullXML(Document doc) {
		Element bundleElement = doc.createElement("bundle");

		bundleElement.appendChild(XMLUtils.createCDATAElement("moduleID", this.moduleID.toString(), doc));

		if (this.name != null) {
			bundleElement.appendChild(XMLUtils.createCDATAElement("name", this.name, doc));
		}

		if (this.description != null) {
			bundleElement.appendChild(XMLUtils.createCDATAElement("description", this.description, doc));
		}

		if (this.menuIndex != null) {
			bundleElement.appendChild(XMLUtils.createCDATAElement("menuIndex", this.menuIndex.toString(), doc));
		}

		if (this.url != null) {
			bundleElement.appendChild(XMLUtils.createCDATAElement("url", this.url, doc));
		}

		if (this.urlType != null) {
			bundleElement.appendChild(XMLUtils.createCDATAElement("urlType", this.urlType.toString(), doc));
		}

		if (this.itemType != null) {
			bundleElement.appendChild(XMLUtils.createCDATAElement("itemType", this.itemType.toString(), doc));
		}

		if (this.sectionID != null) {
			bundleElement.appendChild(XMLUtils.createCDATAElement("sectionID", this.sectionID.toString(), doc));
		}

		if(uniqueID != null){
			bundleElement.appendChild(XMLUtils.createCDATAElement("uniqueID", this.uniqueID.toString(), doc));
		}

		Element adminAccess = doc.createElement("adminAccess");
		adminAccess.appendChild(doc.createTextNode(Boolean.toString(this.adminAccess)));
		bundleElement.appendChild(adminAccess);

		Element userAccess = doc.createElement("userAccess");
		userAccess.appendChild(doc.createTextNode(Boolean.toString(this.userAccess)));
		bundleElement.appendChild(userAccess);

		Element anonymousAccess = doc.createElement("anonymousAccess");
		anonymousAccess.appendChild(doc.createTextNode(Boolean.toString(this.anonymousAccess)));
		bundleElement.appendChild(anonymousAccess);

		XMLUtils.append(doc, bundleElement, "menuitems", this.moduleMenuItems);

		return bundleElement;
	}
}
