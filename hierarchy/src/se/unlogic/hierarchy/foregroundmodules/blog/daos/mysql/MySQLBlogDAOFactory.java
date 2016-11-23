/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.daos.mysql;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.foregroundmodules.blog.daos.BlogDAOFactory;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.string.StringUtils;

public class MySQLBlogDAOFactory extends BlogDAOFactory {

	private Logger log = Logger.getLogger(this.getClass());

	private MySQLBlogPostDAO mySQLBlogPostDAO;
	private MySQLCommentDAO mySQLCommentDAO;

	@Override
	public void init(DataSource dataSource, UserHandler userHandler) throws SQLException, IOException, TableUpgradeException, SAXException, ParserConfigurationException {

		log.debug("Checking for blog tables in datasource " + dataSource);

		if(TableVersionHandler.getTableGroupVersion(dataSource, MySQLBlogDAOFactory.class.getName()) == null){

			//Old style table version handling
			if (!DBUtils.tableExists(dataSource, "blogposts")) {

				log.info("Creating blogposts table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/BlogPostsTable.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			}

			if (!DBUtils.tableExists(dataSource, "blogcomments")) {

				log.info("Creating blogcomments table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/BlogCommentsTable.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			}

			if (!DBUtils.tableExists(dataSource, "blogtags")) {

				log.info("Creating blogtags table in datasource " + dataSource);

				String sql = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("dbscripts/BlogTagsTable.sql"));

				new UpdateQuery(dataSource.getConnection(), true, sql).executeUpdate();

			}
		}

		//New style table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, MySQLBlogDAOFactory.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("dbscripts/DB script.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		this.mySQLCommentDAO = new MySQLCommentDAO(dataSource, userHandler);
		this.mySQLBlogPostDAO = new MySQLBlogPostDAO(dataSource, userHandler, mySQLCommentDAO);
	}

	@Override
	public MySQLBlogPostDAO getBlogPostDAO() {

		return mySQLBlogPostDAO;
	}

	@Override
	public MySQLCommentDAO getCommentDAO() {

		return mySQLCommentDAO;
	}
}
