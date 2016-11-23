/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.BaseModuleDescriptor;
import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.string.StringUtils;


public abstract class MySQLModuleDAO<Descriptor extends BaseModuleDescriptor> extends BaseDAO {

	protected final MySQLModuleSettingDAO<Descriptor> moduleSettingDAO;
	protected final MySQLModuleAttributeDAO<Descriptor> moduleAttributeDAO;

	protected final String moduleTable;
	protected final String moduleUsersTable;
	protected final String moduleGroupsTable;

	public MySQLModuleDAO(MySQLModuleSettingDAO<Descriptor> mySQLModuleSettingDAO, MySQLModuleAttributeDAO<Descriptor> moduleAttributeDAO, DataSource dataSource, String moduleTable, String moduleUsersTable, String moduleGroupsTable) {

		super(dataSource);
		this.moduleSettingDAO = mySQLModuleSettingDAO;
		this.moduleTable = moduleTable;
		this.moduleUsersTable = moduleUsersTable;
		this.moduleGroupsTable = moduleGroupsTable;
		this.moduleAttributeDAO = moduleAttributeDAO;
	}

	protected abstract BeanResultSetPopulator<Descriptor> getPopulator();

	public ArrayList<Descriptor> getEnabledModules(Integer sectionID) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			ArrayListQuery<Descriptor> query = new ArrayListQuery<Descriptor>(connection, false, "SELECT * FROM " + this.moduleTable + " WHERE enabled = true AND sectionID = ? ORDER BY name", getPopulator());

			query.setObject(1, sectionID);

			ArrayList<Descriptor> modules = query.executeQuery();

			if (modules != null) {
				for (Descriptor descriptor : modules) {
					this.getRelations(descriptor, connection);
				}
			}

			return modules;

		} finally {

			DBUtils.closeConnection(connection);
		}
	}

	public Descriptor getModule(Integer moduleID) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			ObjectQuery<Descriptor> query;

			query = new ObjectQuery<Descriptor>(connection, false, "SELECT * FROM " + this.moduleTable + " WHERE moduleID = ?", getPopulator());

			query.setInt(1, moduleID);

			Descriptor descriptor = query.executeQuery();

			if (descriptor != null) {
				this.getRelations(descriptor, connection);
			}

			return descriptor;

		} finally {

			DBUtils.closeConnection(connection);
		}
	}

	public List<Descriptor> getModules(List<Integer> moduleIDs) throws SQLException {

		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			ArrayListQuery<Descriptor> query;

			query = new ArrayListQuery<Descriptor>(connection, false, "SELECT * FROM " + this.moduleTable + " WHERE moduleID IN (?" + StringUtils.repeatString(",?", moduleIDs.size() - 1) + ")", getPopulator());

			int index = 1;

			for(Integer moduleID : moduleIDs){
				
				query.setInt(index, moduleID);
				
				index++;
			}
			
			List<Descriptor> descriptors = query.executeQuery();

			if (descriptors != null) {
				
				for(Descriptor descriptor : descriptors){
					
					this.getRelations(descriptor, connection);
				}
			}

			return descriptors;

		} finally {

			DBUtils.closeConnection(connection);
		}
	}	
	
	public Descriptor getModule(Integer sectionID, String alias) throws SQLException {

		ObjectQuery<Descriptor> query = new ObjectQuery<Descriptor>(this.dataSource.getConnection(), true, "SELECT * FROM " + this.moduleTable + " WHERE sectionID = ? AND alias = ?", getPopulator());

		query.setInt(1, sectionID);
		query.setString(2, alias);

		return query.executeQuery();
	}

	private void getModuleGroups(Descriptor moduleBean, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT groupID from " + this.moduleGroupsTable + " WHERE moduleID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, moduleBean.getModuleID());

		moduleBean.setAllowedGroupIDs(query.executeQuery());
	}

	public ArrayList<Descriptor> getModules(Integer sectionID) throws SQLException {


		Connection connection = null;

		try {

			connection = this.dataSource.getConnection();

			ArrayListQuery<Descriptor> query = new ArrayListQuery<Descriptor>(connection, false, "SELECT * FROM " + this.moduleTable + " WHERE sectionID = ? ORDER BY name", getPopulator());

			query.setObject(1, sectionID);

			ArrayList<Descriptor> modules = query.executeQuery();

			if (modules != null) {
				for (Descriptor descriptor : modules) {
					this.getRelations(descriptor, connection);
				}
			}

			return modules;

		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	protected void getRelations(Descriptor descriptor, Connection connection) throws SQLException {

		this.moduleSettingDAO.getSettingsHandler(descriptor, connection);
		this.moduleAttributeDAO.getAttributeHandler(descriptor, connection);
		this.getModuleGroups(descriptor, connection);
		this.getModuleUsers(descriptor, connection);
	}

	private void getModuleUsers(Descriptor moduleBean, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT userID from " + this.moduleUsersTable + " WHERE moduleID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, moduleBean.getModuleID());

		moduleBean.setAllowedUserIDs(query.executeQuery());
	}

	protected void setGroups(TransactionHandler transactionHandler, Descriptor Descriptor) throws SQLException {

		for (Integer groupID : Descriptor.getAllowedGroupIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO " + this.moduleGroupsTable + " VALUES (?,?)");

			query.setInt(1, Descriptor.getModuleID());
			query.setInt(2, groupID);

			query.executeUpdate();
		}
	}

	protected void setUsers(TransactionHandler transactionHandler, Descriptor Descriptor) throws SQLException {

		for (Integer userID : Descriptor.getAllowedUserIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO " + this.moduleUsersTable + " VALUES (?,?)");

			query.setInt(1, Descriptor.getModuleID());
			query.setInt(2, userID);

			query.executeUpdate();
		}
	}

	public void delete(Descriptor descriptor) throws SQLException {
		UpdateQuery query;

		query = new UpdateQuery(this.dataSource.getConnection(), true, "DELETE FROM " + moduleTable + " WHERE moduleID = ?");

		query.setInt(1, descriptor.getModuleID());

		query.executeUpdate();
	}

	protected void deleteModuleGroups(TransactionHandler transactionHandler, Descriptor descriptor) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM " + this.moduleGroupsTable + " WHERE moduleID = ?");

		query.setInt(1, descriptor.getModuleID());

		query.executeUpdate();
	}

	protected void deleteModuleUsers(TransactionHandler transactionHandler, Descriptor descriptor) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM " + this.moduleUsersTable + " WHERE moduleID = ?");

		query.setInt(1, descriptor.getModuleID());

		query.executeUpdate();
	}
}
