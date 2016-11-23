/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleSettingDAO;


public class MySQLForegroundModuleSettingDAO extends MySQLModuleSettingDAO<SimpleForegroundModuleDescriptor> implements ForegroundModuleSettingDAO{

	protected MySQLForegroundModuleSettingDAO(DataSource ds) {

		super(ds, "openhierarchy_foreground_module_settings");
	}
}
