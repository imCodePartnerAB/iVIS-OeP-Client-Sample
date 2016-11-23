/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery.beans;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.standardutils.xml.XMLUtils;

public class Gallery implements AccessInterface {

	private Integer galleryID;
	private String name;
	private String description;
	private String alias;
	private String url;
	private boolean allowsAdminAccess;
	private boolean allowsAnonymousAccess;
	private boolean allowsUserAccess;
	private Collection<Integer> allowedGroupIDs;
	private Collection<Integer> allowedUserIDs;

	private Collection<Integer> allowedUploadGroupIDs;
	private Collection<Integer> allowedUploadUserIDs;

	public Collection<Integer> getAllowedUploadGroupIDs() {
		return allowedUploadGroupIDs;
	}

	public void setAllowedUploadGroupIDs(Collection<Integer> allowedUploadGroupIDs) {
		this.allowedUploadGroupIDs = allowedUploadGroupIDs;
	}

	public Collection<Integer> getAllowedUploadUserIDs() {
		return allowedUploadUserIDs;
	}

	public void setAllowedUploadUserIDs(Collection<Integer> allowedUploadUserIDs) {
		this.allowedUploadUserIDs = allowedUploadUserIDs;
	}

	public Gallery() {
		super();
	}

	@Override
	public boolean allowsAdminAccess() {
		return this.allowsAdminAccess;
	}

	public void setAdminAccess(boolean allowsAdminAccess) {
		this.allowsAdminAccess = allowsAdminAccess;
	}

	@Override
	public boolean allowsAnonymousAccess() {
		return this.allowsAnonymousAccess;
	}

	public void setAnonymousAccess(boolean allowsAnonymousAccess) {
		this.allowsAnonymousAccess = allowsAnonymousAccess;
	}

	@Override
	public boolean allowsUserAccess() {
		return this.allowsUserAccess;
	}

	public void setUserAccess(boolean allowsUserAccess) {
		this.allowsUserAccess = allowsUserAccess;
	}

	@Override
	public Collection<Integer> getAllowedGroupIDs() {
		return this.allowedGroupIDs;
	}

	public void setAllowedGroupIDs(Collection<Integer> allowedGroupIDs) {
		this.allowedGroupIDs = allowedGroupIDs;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {
		return this.allowedUserIDs;
	}

	public void setAllowedUserIDs(Collection<Integer> allowedUserIDs) {
		this.allowedUserIDs = allowedUserIDs;
	}

	public Integer getGalleryID() {
		return galleryID;
	}

	public void setGalleryID(Integer galleryID) {
		this.galleryID = galleryID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Node toXML(Document doc) {

		Element galleryElement = doc.createElement("gallery");

		if (this.galleryID != null) {
			galleryElement.appendChild(XMLUtils.createElement("galleryID", galleryID.toString(), doc));
		}
		if (this.name != null) {
			galleryElement.appendChild(XMLUtils.createElement("name", name, doc));
		}
		if (this.description != null) {
			galleryElement.appendChild(XMLUtils.createElement("description", description, doc));
		}
		if (this.alias != null) {
			galleryElement.appendChild(XMLUtils.createElement("alias", alias, doc));
		}
		if (this.url != null) {
			galleryElement.appendChild(XMLUtils.createElement("url", url, doc));
		}

		if (this.getAllowedGroupIDs() != null && !this.getAllowedGroupIDs().isEmpty()) {

			Element allowedGroupsElement = doc.createElement("allowedGroupIDs");

			for (Integer groupID : this.getAllowedGroupIDs()) {
				allowedGroupsElement.appendChild(XMLUtils.createElement("groupID", groupID.toString(), doc));
			}

			galleryElement.appendChild(allowedGroupsElement);
		}

		if (this.getAllowedUserIDs() != null && !this.getAllowedUserIDs().isEmpty()) {

			Element allowedUsersElement = doc.createElement("allowedUserIDs");

			for (Integer userID : this.getAllowedUserIDs()) {
				allowedUsersElement.appendChild(XMLUtils.createElement("userID", userID.toString(), doc));
			}

			galleryElement.appendChild(allowedUsersElement);
		}

		if (this.getAllowedUploadGroupIDs() != null && !this.getAllowedUploadGroupIDs().isEmpty()) {

			Element allowedUploadGroupsElement = doc.createElement("allowedUploadGroupIDs");

			for (Integer groupID : this.getAllowedUploadGroupIDs()) {
				allowedUploadGroupsElement.appendChild(XMLUtils.createElement("groupID", groupID.toString(), doc));
			}

			galleryElement.appendChild(allowedUploadGroupsElement);
		}

		if (this.getAllowedUploadUserIDs() != null && !this.getAllowedUploadUserIDs().isEmpty()) {

			Element allowedUploadUsersElement = doc.createElement("allowedUploadUserIDs");

			for (Integer userID : this.getAllowedUploadUserIDs()) {
				allowedUploadUsersElement.appendChild(XMLUtils.createElement("userID", userID.toString(), doc));
			}

			galleryElement.appendChild(allowedUploadUsersElement);
		}

		galleryElement.appendChild(XMLUtils.createElement("anonymousAccess", String.valueOf(allowsAnonymousAccess), doc));
		galleryElement.appendChild(XMLUtils.createElement("userAccess", String.valueOf(allowsUserAccess), doc));
		galleryElement.appendChild(XMLUtils.createElement("adminAccess", String.valueOf(allowsAdminAccess), doc));

		return galleryElement;
	}

	@Override
	public String toString() {
		return this.name + " (ID: " + this.galleryID + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((galleryID == null) ? 0 : galleryID.hashCode());
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
		Gallery other = (Gallery) obj;
		if (galleryID == null) {
			if (other.galleryID != null) {
				return false;
			}
		} else if (!galleryID.equals(other.galleryID)) {
			return false;
		}
		return true;
	}
}
