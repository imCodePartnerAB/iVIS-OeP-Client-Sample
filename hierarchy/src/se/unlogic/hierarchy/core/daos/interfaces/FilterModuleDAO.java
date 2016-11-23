/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. SimpleFilterModuleDescriptorhis program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.core.beans.SimpleFilterModuleDescriptor;


public interface FilterModuleDAO extends ModuleDAO<SimpleFilterModuleDescriptor>{

	public abstract List<SimpleFilterModuleDescriptor> getEnabledModules() throws SQLException;

	public abstract List<SimpleFilterModuleDescriptor> getModules() throws SQLException;	
}
