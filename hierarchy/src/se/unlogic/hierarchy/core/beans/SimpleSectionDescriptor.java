/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.hierarchy.core.handlers.SimpleMutableAttributeHandler;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.xml.XMLUtils;

public class SimpleSectionDescriptor implements SectionDescriptor, Serializable {

	private static final long serialVersionUID = 2062733982637464058L;

	private Integer sectionID = 0;
	private Integer parentSectionID;

	@WebPopulate(required = true, maxLength = 255)
	private String alias;

	private String fullAlias = "";

	@WebPopulate
	private HTTPProtocol requiredProtocol;

	@WebPopulate
	private boolean enabled;

	@WebPopulate
	private boolean anonymousAccess;

	@WebPopulate
	private boolean userAccess;

	@WebPopulate
	private boolean adminAccess;

	@WebPopulate
	private boolean visibleInMenu;

	@WebPopulate
	private boolean breadCrumb;

	@WebPopulate(required = true, maxLength = 255)
	private String name;

	@WebPopulate(required = true, maxLength = 255)
	private String description;

	@WebPopulate(maxLength = 255)
	private String anonymousDefaultURI;

	@WebPopulate(maxLength = 255)
	private String userDefaultURI;

	@WebPopulate(paramName = "group")
	private List<Integer> allowedGroupIDs;

	@WebPopulate(paramName = "user")
	private List<Integer> allowedUserIDs;

	private List<SimpleSectionDescriptor> subSectionsList;

	protected SimpleMutableAttributeHandler attributeHandler;

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getSectionID() */
	@Override
	public Integer getSectionID() {

		return sectionID;
	}

	public void setSectionID(Integer sectionID) {

		this.sectionID = sectionID;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getParentSectionID() */
	@Override
	public Integer getParentSectionID() {

		return parentSectionID;
	}

	public void setParentSectionID(Integer parentSectionID) {

		this.parentSectionID = parentSectionID;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getAlias() */
	@Override
	public String getAlias() {

		return alias;
	}

	public void setAlias(String alias) {

		this.alias = alias;
	}

	@Override
	public HTTPProtocol getRequiredProtocol() {

		return requiredProtocol;
	}

	public void setRequiredProtocol(HTTPProtocol requiredHTTPProtocol) {

		this.requiredProtocol = requiredHTTPProtocol;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getFullAlias() */
	@Override
	public String getFullAlias() {

		return fullAlias;
	}

	public void setFullAlias(String fullAlias) {

		this.fullAlias = fullAlias;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#isEnabled() */
	@Override
	public boolean isEnabled() {

		return enabled;
	}

	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#isAnonymousAccess() */
	@Override
	public boolean allowsAnonymousAccess() {

		return anonymousAccess;
	}

	public void setAnonymousAccess(boolean anonymousAccess) {

		this.anonymousAccess = anonymousAccess;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#isUserAccess() */
	@Override
	public boolean allowsUserAccess() {

		return userAccess;
	}

	public void setUserAccess(boolean userAccess) {

		this.userAccess = userAccess;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#isAdminAccess() */
	@Override
	public boolean allowsAdminAccess() {

		return adminAccess;
	}

	public void setAdminAccess(boolean adminAccess) {

		this.adminAccess = adminAccess;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#isVisibleInMenu() */
	@Override
	public boolean isVisibleInMenu() {

		return visibleInMenu;
	}

	public void setVisibleInMenu(boolean visibleInMenu) {

		this.visibleInMenu = visibleInMenu;
	}

	@Override
	public boolean hasBreadCrumb() {

		return this.breadCrumb;
	}

	public void setBreadCrumb(boolean breadCrumb) {

		this.breadCrumb = breadCrumb;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getDescription() */
	@Override
	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getAnonymousDefaultURI() */
	@Override
	public String getAnonymousDefaultURI() {

		return anonymousDefaultURI;
	}

	public void setAnonymousDefaultURI(String anonymousDefaultURI) {

		this.anonymousDefaultURI = anonymousDefaultURI;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getUserDefaultURI() */
	@Override
	public String getUserDefaultURI() {

		return userDefaultURI;
	}

	public void setUserDefaultURI(String userDefaultURI) {

		this.userDefaultURI = userDefaultURI;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getSubSectionsList() */
	@Override
	public List<SimpleSectionDescriptor> getSubSectionsList() {

		return subSectionsList;
	}

	public void setSubSectionsList(ArrayList<SimpleSectionDescriptor> subSectionsList) {

		this.subSectionsList = subSectionsList;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#toXML(org.w3c.dom.Document) */
	@Override
	public Element toXML(Document doc) {

		Element section = doc.createElement("section");

		if (this.sectionID != null) {
			section.appendChild(XMLUtils.createElement("sectionID", this.sectionID.toString(), doc));
		}

		if (this.parentSectionID != null) {
			section.appendChild(XMLUtils.createElement("parentSectionID", this.parentSectionID.toString(), doc));
		}

		if (this.alias != null) {
			section.appendChild(XMLUtils.createCDATAElement("alias", this.alias, doc));
		}

		if (this.fullAlias != null) {
			section.appendChild(XMLUtils.createCDATAElement("fullAlias", fullAlias, doc));
		}

		section.appendChild(XMLUtils.createElement("enabled", Boolean.toString(this.enabled), doc));
		section.appendChild(XMLUtils.createElement("anonymousAccess", Boolean.toString(this.anonymousAccess), doc));
		section.appendChild(XMLUtils.createElement("userAccess", Boolean.toString(this.userAccess), doc));
		section.appendChild(XMLUtils.createElement("adminAccess", Boolean.toString(this.adminAccess), doc));
		section.appendChild(XMLUtils.createElement("visibleInMenu", Boolean.toString(this.visibleInMenu), doc));
		section.appendChild(XMLUtils.createElement("breadCrumb", Boolean.toString(this.breadCrumb), doc));

		if (this.name != null) {
			section.appendChild(XMLUtils.createCDATAElement("name", this.name, doc));
		}

		if (this.description != null) {
			section.appendChild(XMLUtils.createCDATAElement("description", this.description, doc));
		}

		if (this.anonymousDefaultURI != null) {
			section.appendChild(XMLUtils.createCDATAElement("anonymousDefaultURI", this.anonymousDefaultURI, doc));
		}

		if (this.userDefaultURI != null) {
			section.appendChild(XMLUtils.createCDATAElement("userDefaultURI", this.userDefaultURI, doc));
		}

		if (this.requiredProtocol != null) {
			section.appendChild(XMLUtils.createCDATAElement("requiredProtocol", this.requiredProtocol, doc));
		}

		if (attributeHandler != null) {
			section.appendChild(attributeHandler.toXML(doc));
		}

		AccessUtils.appendAllowedGroupAndUserIDs(doc, section, this);

		return section;
	}

	/* (non-Javadoc)
	 * 
	 * @see se.unlogic.hierarchy.core.beans.SectionDescriptor#getName() */
	@Override
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String toString() {

		if (this.fullAlias != null) {
			return this.name + " (ID: " + this.getSectionID() + ", alias: " + this.getFullAlias() + ")";
		} else {
			return this.name + " (ID: " + this.getSectionID() + ")";
		}
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sectionID == null) ? 0 : sectionID.hashCode());
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
		final SimpleSectionDescriptor other = (SimpleSectionDescriptor) obj;
		if (sectionID == null) {
			if (other.sectionID != null) {
				return false;
			}
		} else if (!sectionID.equals(other.sectionID)) {
			return false;
		}
		return true;
	}

	@Override
	public List<Integer> getAllowedGroupIDs() {

		return allowedGroupIDs;
	}

	public void setAllowedGroupIDs(List<Integer> allowedGroupIDs) {

		this.allowedGroupIDs = allowedGroupIDs;
	}

	@Override
	public List<Integer> getAllowedUserIDs() {

		return allowedUserIDs;
	}

	public void setAllowedUserIDs(List<Integer> allowedUserIDs) {

		this.allowedUserIDs = allowedUserIDs;
	}

	@Override
	public SimpleMutableAttributeHandler getAttributeHandler() {

		if (attributeHandler == null) {

			attributeHandler = new SimpleMutableAttributeHandler(255, 4096);
		}

		return attributeHandler;
	}

	public void setAttributeHandler(SimpleMutableAttributeHandler attributeHandler) {

		this.attributeHandler = attributeHandler;
	}

	@Override
	public void saveAttributes(SystemInterface systemInterface) throws SQLException {

		systemInterface.getCoreDaoFactory().getSectionAttributeDAO().set(this);
	}
}
