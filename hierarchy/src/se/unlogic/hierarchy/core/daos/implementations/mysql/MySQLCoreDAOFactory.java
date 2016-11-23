/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleFilterModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.daos.interfaces.AttributeDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleDAO;
import se.unlogic.hierarchy.core.daos.interfaces.FilterModuleSettingDAO;
import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;

public class MySQLCoreDAOFactory implements CoreDaoFactory {

	protected static Logger log = Logger.getLogger(CoreDaoFactory.class);

	private MySQLDataSourceDAO dataSourceDAO;
	private MySQLMenuIndexDAO menuIndexDAO;
	private MySQLForegroundModuleDAO foregroundModuleDAO;
	private MySQLBackgroundModuleDAO backgroundModuleDAO;
	private MySQLFilterModuleDAO filterModuleDAO;
	private MySQLFilterModuleSettingDAO filterModuleSettingDAO;
	private MySQLForegroundModuleSettingDAO foregroundModuleSettingDAO;
	private MySQLBackgroundModuleSettingDAO backgroundModuleSettingDAO;
	private MySQLModuleAttributeDAO<SimpleForegroundModuleDescriptor> foregroundModuleAttributeDAO;
	private MySQLModuleAttributeDAO<SimpleBackgroundModuleDescriptor> backgroundModuleAttributeDAO;
	private MySQLModuleAttributeDAO<SimpleFilterModuleDescriptor> filterModuleAttributeDAO;
	private MySQLSectionAttributeDAO<SimpleSectionDescriptor> sectionAttributeDAO;
	private MySQLSectionDAO sectionDAO;
	private MySQLVirtualMenuItemDAO virtualMenuItemDAO;

	@Override
	public void init(DataSource dataSource) throws TableUpgradeException, SQLException, SAXException, IOException, ParserConfigurationException {

		//New automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, this.getClass().getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("MySQL DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		this.dataSourceDAO = new MySQLDataSourceDAO(dataSource);
		this.menuIndexDAO = new MySQLMenuIndexDAO(dataSource);
		this.virtualMenuItemDAO = new MySQLVirtualMenuItemDAO(dataSource);

		this.backgroundModuleSettingDAO = new MySQLBackgroundModuleSettingDAO(dataSource);
		this.filterModuleSettingDAO = new MySQLFilterModuleSettingDAO(dataSource);
		this.foregroundModuleSettingDAO = new MySQLForegroundModuleSettingDAO(dataSource);

		this.backgroundModuleAttributeDAO = new MySQLModuleAttributeDAO<SimpleBackgroundModuleDescriptor>(dataSource, "openhierarchy_background_module_attributes");
		this.filterModuleAttributeDAO = new MySQLModuleAttributeDAO<SimpleFilterModuleDescriptor>(dataSource, "openhierarchy_filter_module_attributes");
		this.foregroundModuleAttributeDAO = new MySQLModuleAttributeDAO<SimpleForegroundModuleDescriptor>(dataSource, "openhierarchy_foreground_module_attributes");
		this.sectionAttributeDAO = new MySQLSectionAttributeDAO<SimpleSectionDescriptor>(dataSource, "openhierarchy_section_attributes");

		this.backgroundModuleDAO = new MySQLBackgroundModuleDAO(dataSource, backgroundModuleSettingDAO, backgroundModuleAttributeDAO);
		this.filterModuleDAO = new MySQLFilterModuleDAO(dataSource, filterModuleSettingDAO, filterModuleAttributeDAO);
		this.foregroundModuleDAO = new MySQLForegroundModuleDAO(dataSource, foregroundModuleSettingDAO, foregroundModuleAttributeDAO);
		this.sectionDAO = new MySQLSectionDAO(dataSource, sectionAttributeDAO);
	}

	@Override
	public MySQLDataSourceDAO getDataSourceDAO() {

		return dataSourceDAO;
	}

	@Override
	public MySQLMenuIndexDAO getMenuIndexDAO() {

		return menuIndexDAO;
	}

	@Override
	public MySQLForegroundModuleDAO getForegroundModuleDAO() {

		return foregroundModuleDAO;
	}

	@Override
	public MySQLBackgroundModuleDAO getBackgroundModuleDAO() {

		return backgroundModuleDAO;
	}

	@Override
	public MySQLForegroundModuleSettingDAO getForegroundModuleSettingDAO() {

		return foregroundModuleSettingDAO;
	}

	@Override
	public MySQLBackgroundModuleSettingDAO getBackgroundModuleSettingDAO() {

		return backgroundModuleSettingDAO;
	}

	@Override
	public MySQLSectionDAO getSectionDAO() {

		return sectionDAO;
	}

	@Override
	public MySQLVirtualMenuItemDAO getVirtualMenuItemDAO() {

		return virtualMenuItemDAO;
	}

	@Override
	public FilterModuleSettingDAO getFilterModuleSettingDAO() {

		return this.filterModuleSettingDAO;
	}

	@Override
	public FilterModuleDAO getFilterModuleDAO() {

		return this.filterModuleDAO;
	}

	@Override
	public AttributeDAO<SimpleForegroundModuleDescriptor> getForegroundModuleAttributeDAO() {

		return foregroundModuleAttributeDAO;
	}

	@Override
	public AttributeDAO<SimpleBackgroundModuleDescriptor> getBackgroundModuleAttributeDAO() {

		return backgroundModuleAttributeDAO;
	}

	@Override
	public AttributeDAO<SimpleFilterModuleDescriptor> getFilterModuleAttributeDAO() {

		return filterModuleAttributeDAO;
	}

	@Override
	public AttributeDAO<SimpleSectionDescriptor> getSectionAttributeDAO() {

		return sectionAttributeDAO;
	}
}
