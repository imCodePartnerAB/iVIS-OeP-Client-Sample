/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;

public interface SectionDAO {

	public abstract ArrayList<SimpleSectionDescriptor> getSubSections(SimpleSectionDescriptor section, boolean getSubSections) throws SQLException;

	public abstract SimpleSectionDescriptor getRootSection(boolean getSubSections) throws SQLException;

	public abstract ArrayList<SimpleSectionDescriptor> getEnabledSubSections(SectionDescriptor sectionDescriptor, boolean getSubSections) throws SQLException;

	public abstract SimpleSectionDescriptor getSection(int sectionID, boolean fullAlias) throws SQLException;

	public abstract void getReverseFullAlias(SimpleSectionDescriptor simpleSectionDescriptor) throws SQLException;

	public abstract void update(SimpleSectionDescriptor simpleSectionDescriptor) throws SQLException;

	public abstract void add(SimpleSectionDescriptor simpleSectionDescriptor) throws SQLException;

	public abstract void delete(SimpleSectionDescriptor simpleSectionDescriptor) throws SQLException;

	public abstract SimpleSectionDescriptor getSection(Integer sectionID, String alias) throws SQLException;

	public abstract List<SimpleSectionDescriptor> getSectionsByIDs(List<Integer> sectionIDs, boolean fullAlias) throws SQLException;

	public abstract List<SimpleSectionDescriptor> getSectionsByAttribute(String name, boolean fullAlias) throws SQLException;

	public abstract List<SimpleSectionDescriptor> getSectionsByAttribute(String name, String value, boolean fullAlias) throws SQLException;

}
