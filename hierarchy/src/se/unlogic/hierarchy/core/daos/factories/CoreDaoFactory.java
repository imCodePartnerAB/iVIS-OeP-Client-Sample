/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.factories;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleFilterModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.AttributeDAO;
import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.BackgroundModuleSettingDAO;
import se.unlogic.hierarchy.core.daos.interfaces.DataSourceDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleSettingDAO;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleSettingDAO;
import se.unlogic.hierarchy.core.daos.interfaces.MenuIndexDAO;
import se.unlogic.hierarchy.core.daos.interfaces.SectionDAO;
import se.unlogic.hierarchy.core.daos.interfaces.VirtualMenuItemDAO;

public interface CoreDaoFactory {

	public void init(DataSource dataSource) throws Exception;

	public DataSourceDAO getDataSourceDAO();

	public MenuIndexDAO getMenuIndexDAO();

	public ForegroundModuleDAO getForegroundModuleDAO();

	public BackgroundModuleDAO getBackgroundModuleDAO();

	public ForegroundModuleSettingDAO getForegroundModuleSettingDAO();

	public BackgroundModuleSettingDAO getBackgroundModuleSettingDAO();

	public FilterModuleSettingDAO getFilterModuleSettingDAO();

	public SectionDAO getSectionDAO();

	public VirtualMenuItemDAO getVirtualMenuItemDAO();

	public FilterModuleDAO getFilterModuleDAO();

	public AttributeDAO<SimpleForegroundModuleDescriptor> getForegroundModuleAttributeDAO();

	public AttributeDAO<SimpleBackgroundModuleDescriptor> getBackgroundModuleAttributeDAO();

	public AttributeDAO<SimpleFilterModuleDescriptor> getFilterModuleAttributeDAO();

	public AttributeDAO<SimpleSectionDescriptor> getSectionAttributeDAO();
}
