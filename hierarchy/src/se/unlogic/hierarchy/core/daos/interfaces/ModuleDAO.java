/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public interface ModuleDAO<T extends ModuleDescriptor> {

	public abstract void add(T moduleDescriptor) throws SQLException;

	public abstract void update(T moduleDescriptor) throws SQLException;

	public abstract void delete(T moduleDescriptor) throws SQLException;

	public abstract T getModule(Integer moduleID) throws SQLException;
	
	public abstract List<T> getModulesByAttribute(String name, String value)  throws SQLException;
}
