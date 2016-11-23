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

import se.unlogic.hierarchy.core.beans.SimpleDataSourceDescriptor;
import se.unlogic.standardutils.dao.CRUDDAO;

public interface DataSourceDAO extends CRUDDAO<SimpleDataSourceDescriptor, Integer>{

	@Override
	public abstract ArrayList<SimpleDataSourceDescriptor> getAll() throws SQLException;

	public abstract ArrayList<SimpleDataSourceDescriptor> getAll(boolean enabled) throws SQLException;

	@Override
	public abstract SimpleDataSourceDescriptor get(Integer dataSourceID) throws SQLException;

	@Override
	public abstract void update(SimpleDataSourceDescriptor dsb) throws SQLException;

	@Override
	public abstract void delete(SimpleDataSourceDescriptor dsb) throws SQLException;

	@Override
	public abstract void add(SimpleDataSourceDescriptor dsb) throws SQLException;

}
