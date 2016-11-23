/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.pagemodules.daos;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.foregroundmodules.pagemodules.Page;

public interface PageDAO {

	public abstract Page getPage(int pageID, int sectionID) throws SQLException;

	public abstract List<Page> getPages(int sectionID) throws SQLException;

	public abstract List<Page> getEnabledPages(int sectionID) throws SQLException;

	public abstract void delete(Page page) throws SQLException;

	public abstract void update(Page page) throws SQLException;

	public abstract void add(Page page) throws SQLException;

	public abstract Page getPage(int pageID) throws SQLException;

	public abstract List<Page> getVisibleEnabledPages(int sectionID) throws SQLException;

	public abstract Page getPage(String alias, int sectionID) throws SQLException;

	public abstract boolean sectionHasEnabledPages(int sectionID) throws SQLException;

}
