/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleSettingDAO;


public class MySQLBackgroundModuleSettingDAO extends MySQLModuleSettingDAO<SimpleBackgroundModuleDescriptor> implements BackgroundModuleSettingDAO{

	protected MySQLBackgroundModuleSettingDAO(DataSource ds) {

		super(ds, "openhierarchy_background_module_settings");
	}

}
