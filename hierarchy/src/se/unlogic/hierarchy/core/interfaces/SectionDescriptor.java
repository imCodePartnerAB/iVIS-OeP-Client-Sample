/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.sql.SQLException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.enums.HTTPProtocol;

public interface SectionDescriptor extends AccessInterface{

	public abstract Integer getSectionID();

	public abstract Integer getParentSectionID();

	public abstract String getAlias();

	public abstract String getFullAlias();

	public abstract boolean isEnabled();

	@Override
	public abstract boolean allowsAnonymousAccess();

	@Override
	public abstract boolean allowsUserAccess();

	@Override
	public abstract boolean allowsAdminAccess();

	public abstract boolean isVisibleInMenu();

	public abstract String getDescription();

	public abstract String getAnonymousDefaultURI();

	public abstract String getUserDefaultURI();

	public abstract List<SimpleSectionDescriptor> getSubSectionsList();

	public abstract Element toXML(Document doc);

	public abstract String getName();
	
	boolean hasBreadCrumb();

	public abstract HTTPProtocol getRequiredProtocol();

	public MutableAttributeHandler getAttributeHandler();

	/**
	 * This method is used to persist any changes made to the {@link MutableAttributeHandler}
	 *
	 * @param systemInterface
	 * @throws SQLException
	 */
	public void saveAttributes(SystemInterface systemInterface) throws SQLException;

}
