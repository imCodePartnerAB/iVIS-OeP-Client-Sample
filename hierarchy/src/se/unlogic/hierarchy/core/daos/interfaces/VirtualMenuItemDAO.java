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

import se.unlogic.hierarchy.core.beans.VirtualMenuItem;


public interface VirtualMenuItemDAO {

	public abstract VirtualMenuItem getMenuItem(Integer menuItemID) throws SQLException;

	public abstract void delete(VirtualMenuItem virtualMenuItem) throws SQLException;

	public abstract void update(VirtualMenuItem virtualMenuItem) throws SQLException;

	public abstract ArrayList<VirtualMenuItem> getMenuItemsInSection(Integer sectionID) throws SQLException;

	public abstract void add(VirtualMenuItem virtualMenuItem) throws SQLException;

}
