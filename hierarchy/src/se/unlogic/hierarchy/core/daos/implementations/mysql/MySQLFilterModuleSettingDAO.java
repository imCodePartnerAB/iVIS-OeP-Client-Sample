/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleFilterModuleDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleSettingDAO;


public class MySQLFilterModuleSettingDAO extends MySQLModuleSettingDAO<SimpleFilterModuleDescriptor> implements FilterModuleSettingDAO{

	protected MySQLFilterModuleSettingDAO(DataSource ds) {

		super(ds, "openhierarchy_filter_module_settings");
	}
}
