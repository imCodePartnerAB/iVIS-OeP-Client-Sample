/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.annotated;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.PageDAO;
import se.unlogic.hierarchy.foregroundmodules.pagemodules.daos.PageDAOFactory;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.string.StringUtils;


public class AnnotatedPageDAOFactory extends PageDAOFactory {

	private static Logger log = Logger.getLogger(AnnotatedPageDAOFactory.class);

	private AnnotatedPageDAO pageDAO;

	@Override
	public void init(DataSource dataSource) throws SQLException, IOException, TableUpgradeException, SAXException, ParserConfigurationException {

		log.debug("Checking for page tables in datasource " + dataSource);

		if(TableVersionHandler.getTableGroupVersion(dataSource, AnnotatedPageDAOFactory.class.getName()) == null){

			if (!DBUtils.tableExists(dataSource, "pages")) {

				log.info("Creating pages table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/PagesTable.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			} else if (DBUtils.getTableColumnCount(dataSource, "pages") == 11) {

				log.info("Adding breadCrumb column to page table in datasource " + dataSource);

				new UpdateQuery(dataSource.getConnection(), true, "ALTER TABLE `pages` ADD COLUMN `breadCrumb` BOOLEAN NOT NULL DEFAULT 1 AFTER `alias`;").executeUpdate();
			}

			if (!DBUtils.tableExists(dataSource, "pagegroups")) {

				log.info("Creating pagegroups table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/PageGroupsTable.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}

			if (!DBUtils.tableExists(dataSource, "pageusers")) {

				log.info("Creating pageusers table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/PageUsersTable.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}
		}

		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, AnnotatedPageDAOFactory.class.getName(), new XMLDBScriptProvider(AnnotatedPageDAOFactory.class.getResourceAsStream("dbscripts/DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		this.pageDAO = new AnnotatedPageDAO(dataSource);

	}

	@Override
	public PageDAO getPageDAO() {

		return pageDAO;
	}
}
