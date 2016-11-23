/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.mysql;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos.MailDAOFactory;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.string.StringUtils;

public class MySQLMailDAOFactory extends MailDAOFactory {

	private static Logger log = Logger.getLogger(MySQLMailDAOFactory.class);

	private MySQLMailDAO mailDAO;

	@Override
	public MySQLMailDAO getMailDAO() {

		return mailDAO;
	}

	@Override
	public void init(DataSource dataSource) throws SQLException, IOException, TableUpgradeException, SAXException, ParserConfigurationException {

		log.debug("Checking for email tables in datasource " + dataSource);

		if(TableVersionHandler.getTableGroupVersion(dataSource, MySQLMailDAOFactory.class.getName()) == null){

			if (!DBUtils.tableExists(dataSource, "emails")) {

				log.info("Creating email table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/emails.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			}

			if (!DBUtils.tableExists(dataSource, "attachments")) {

				log.info("Creating attachments table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/attachments.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}

			if (!DBUtils.tableExists(dataSource, "bccrecipients")) {

				log.info("Creating bccrecipients table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/bccrecipients.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}

			if (!DBUtils.tableExists(dataSource, "ccrecipients")) {

				log.info("Creating ccrecipients table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/ccrecipients.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}

			if (!DBUtils.tableExists(dataSource, "recipients")) {

				log.info("Creating recipients table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/recipients.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}

			if (!DBUtils.tableExists(dataSource, "replyto")) {

				log.info("Creating replyto table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/replyto.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();
			}
		}

		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, MySQLMailDAOFactory.class.getName(), new XMLDBScriptProvider(MySQLMailDAOFactory.class.getResourceAsStream("dbscripts/DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		this.mailDAO = new MySQLMailDAO(dataSource);
	}
}
