/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

import se.unlogic.hierarchy.core.beans.Bundle;
import se.unlogic.hierarchy.core.beans.MenuItem;
import se.unlogic.hierarchy.core.beans.ModuleMenuItem;
import se.unlogic.hierarchy.core.beans.SectionMenuItem;
import se.unlogic.hierarchy.core.beans.VirtualMenuItem;

public interface MenuIndexDAO {

	public abstract void populateSectionMenuIndex(SectionMenuItem sectionMenuItem) throws SQLException;

	public abstract void populateModuleMenuIndex(Collection<ModuleMenuItem> moduleMenuItems) throws SQLException;

	public abstract void populateBundleMenuIndex(Collection<Bundle> bundles) throws SQLException;

	public abstract void populateVirtualMenuIndex(Collection<VirtualMenuItem> virtualMenuItems) throws SQLException;

	public void updateMenuIndex(Set<MenuItem> menuItems) throws SQLException;

	// public abstract SectionMenuItem addSectionMenuIndex(SectionMenuItem sectionMenuItem) throws SQLException;
	//
	// public abstract ModuleMenuItem addModuleMenuIndex(ModuleMenuItem moduleMenuItem) throws SQLException;
	//
	// public abstract Bundle addBundleMenuIndex(Bundle bundle) throws SQLException;
	//
	// public abstract VirtualMenuItem addVirtualMenuIndex(VirtualMenuItem virtualMenuItem) throws SQLException;

	// public abstract void updateSectionMenuIndex(Collection<SectionMenuItem> sectionMenuItems) throws SQLException;
	//	
	// public abstract void updateModuleMenuIndex(Collection<ModuleMenuItem> moduleMenuItems) throws SQLException;
	//	
	// public abstract void updateBundleMenuIndex(Collection<Bundle> bundles) throws SQLException;
	//	
	// public abstract void updateVirtualMenuIndex(Collection<VirtualMenuItem> virtualMenuItems) throws SQLException;

	// public abstract int getHighestMenuIndex(int sectionID) throws SQLException;

}
