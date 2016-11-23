/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.Collection;

import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;

public class SimpleMenuItemDescriptor implements MenuItemDescriptor {

	private String name;
	private String description;
	private String uniqueID;
	private String url;
	private URLType urlType;
	private MenuItemType itemType;
	private Integer moduleID;

	protected boolean anonymousAccess;
	protected boolean userAccess;
	protected boolean adminAccess;
	protected Collection<Integer> allowedGroupIDs;
	protected Collection<Integer> allowedUserIDs;

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	@Override
	public String getUniqueID() {
		return this.uniqueID;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public URLType getUrlType() {
		return urlType;
	}

	public void setUrlType(URLType urlType) {
		this.urlType = urlType;
	}

	@Override
	public MenuItemType getItemType() {
		return itemType;
	}

	public void setItemType(MenuItemType itemType) {
		this.itemType = itemType;
	}

	public void setAdminAccess(boolean adminAccess) {
		this.adminAccess = adminAccess;
	}

	@Override
	public boolean allowsAdminAccess() {
		return adminAccess;
	}

	public void setAnonymousAccess(boolean anonymousAccess) {
		this.anonymousAccess = anonymousAccess;
	}

	@Override
	public boolean allowsAnonymousAccess() {
		return anonymousAccess;
	}

	public void setUserAccess(boolean userAccess) {
		this.userAccess = userAccess;
	}

	@Override
	public boolean allowsUserAccess() {
		return userAccess;
	}

	public void setAllowedGroupIDs(Collection<Integer> allowedGroupIDs) {
		this.allowedGroupIDs = allowedGroupIDs;
	}

	@Override
	public Collection<Integer> getAllowedGroupIDs() {
		return allowedGroupIDs;
	}

	public void setAllowedUserIDs(Collection<Integer> allowedUserIDs) {
		this.allowedUserIDs = allowedUserIDs;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {
		return allowedUserIDs;
	}

	public void setAccess(AccessInterface accessInterface) {

		this.allowedGroupIDs = accessInterface.getAllowedGroupIDs();
		this.allowedUserIDs = accessInterface.getAllowedUserIDs();
		this.adminAccess = accessInterface.allowsAdminAccess();
		this.userAccess = accessInterface.allowsUserAccess();
		this.anonymousAccess = accessInterface.allowsAnonymousAccess();
	}


	@Override
	public Integer getModuleID() {
		return moduleID;
	}

	public void setModuleID(Integer moduleID) {
		this.moduleID = moduleID;
	}
}
