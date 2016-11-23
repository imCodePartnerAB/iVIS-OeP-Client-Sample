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

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.VirtualMenuItem;
import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.core.daos.interfaces.VirtualMenuItemDAO;
import se.unlogic.hierarchy.core.populators.VirtualMenuItemPopulator;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.IntegerKeyCollector;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.populators.IntegerPopulator;

public class MySQLVirtualMenuItemDAO extends BaseDAO implements VirtualMenuItemDAO {

	private static VirtualMenuItemPopulator virtualMenuItemPopulator = new VirtualMenuItemPopulator();

	protected MySQLVirtualMenuItemDAO(DataSource ds) {
		super(ds);
	}

	@Override
	public VirtualMenuItem getMenuItem(Integer menuItemID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ObjectQuery<VirtualMenuItem> query = new ObjectQuery<VirtualMenuItem>(connection, false, "SELECT * FROM openhierarchy_virtual_menu_items WHERE menuItemID = ?", virtualMenuItemPopulator);

			query.setInt(1, menuItemID);

			VirtualMenuItem virtualMenuItem = query.executeQuery();

			if (virtualMenuItem != null) {
				this.getMenuItemGroups(virtualMenuItem, connection);
				this.getMenuItemUsers(virtualMenuItem, connection);
			}

			return virtualMenuItem;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
			}
		}
	}

	@Override
	public void delete(VirtualMenuItem virtualMenuItem) throws SQLException {

		UpdateQuery query = new UpdateQuery(this.dataSource.getConnection(), true, "DELETE FROM openhierarchy_virtual_menu_items WHERE menuItemID = ?");

		query.setInt(1, virtualMenuItem.getMenuItemID());

		query.executeUpdate();
	}

	@Override
	public void update(VirtualMenuItem virtualMenuItem) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("UPDATE openhierarchy_virtual_menu_items SET itemtype = ?, name = ?, description = ?, url = ?, anonymousAccess = ?, userAccess = ?, adminAccess = ?, sectionID = ? WHERE menuItemID = ?");

			query.setString(1, virtualMenuItem.getItemType().toString());
			query.setObject(2, virtualMenuItem.getName());
			query.setObject(3, virtualMenuItem.getDescription());
			query.setObject(4, virtualMenuItem.getUrl());
			query.setBoolean(5, virtualMenuItem.allowsAnonymousAccess());
			query.setBoolean(6, virtualMenuItem.allowsUserAccess());
			query.setBoolean(7, virtualMenuItem.allowsAdminAccess());
			query.setInt(8, virtualMenuItem.getSectionID());
			query.setInt(9, virtualMenuItem.getMenuItemID());

			query.executeUpdate();

			this.deleteMenuItemUsers(transactionHandler, virtualMenuItem);

			if (virtualMenuItem.getAllowedUserIDs() != null && !virtualMenuItem.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, virtualMenuItem);
			}

			this.deleteMenuItemGroups(transactionHandler, virtualMenuItem);

			if (virtualMenuItem.getAllowedGroupIDs() != null && !virtualMenuItem.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, virtualMenuItem);
			}

			transactionHandler.commit();
		} finally {
			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}
	}

	@Override
	public ArrayList<VirtualMenuItem> getMenuItemsInSection(Integer sectionID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ArrayListQuery<VirtualMenuItem> query = new ArrayListQuery<VirtualMenuItem>(connection, false, "SELECT * FROM openhierarchy_virtual_menu_items WHERE sectionID = ?", virtualMenuItemPopulator);

			query.setInt(1, sectionID);

			ArrayList<VirtualMenuItem> virtualMenuItems = query.executeQuery();

			if (virtualMenuItems != null) {
				for (VirtualMenuItem virtualMenuItem : virtualMenuItems) {
					this.getMenuItemGroups(virtualMenuItem, connection);
					this.getMenuItemUsers(virtualMenuItem, connection);
				}
			}

			return virtualMenuItems;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
			}
		}

	}

	@Override
	public void add(VirtualMenuItem virtualMenuItem) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO openhierarchy_virtual_menu_items VALUES(null,?,?,?,?,?,?,?,?)");

			query.setString(1, virtualMenuItem.getItemType().toString());
			query.setObject(2, virtualMenuItem.getName());
			query.setObject(3, virtualMenuItem.getDescription());
			query.setObject(4, virtualMenuItem.getUrl());
			query.setBoolean(5, virtualMenuItem.allowsAnonymousAccess());
			query.setBoolean(6, virtualMenuItem.allowsUserAccess());
			query.setBoolean(7, virtualMenuItem.allowsAdminAccess());
			query.setInt(8, virtualMenuItem.getSectionID());

			IntegerKeyCollector keyCollector = new IntegerKeyCollector();

			query.executeUpdate(keyCollector);

			virtualMenuItem.setMenuItemID(keyCollector.getKeyValue());

			if (virtualMenuItem.getAllowedUserIDs() != null && !virtualMenuItem.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, virtualMenuItem);
			}

			if (virtualMenuItem.getAllowedGroupIDs() != null && !virtualMenuItem.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, virtualMenuItem);
			}

			transactionHandler.commit();
		} finally {
			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}
	}

	private void getMenuItemGroups(VirtualMenuItem virtualMenuItem, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT groupID from openhierarchy_virtual_menu_item_groups WHERE menuItemID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, virtualMenuItem.getMenuItemID());

		virtualMenuItem.setAllowedGroupIDs(query.executeQuery());
	}

	private void getMenuItemUsers(VirtualMenuItem virtualMenuItem, Connection connection) throws SQLException {

		ArrayListQuery<Integer> query = new ArrayListQuery<Integer>(connection, false, "SELECT userID from openhierarchy_virtual_menu_item_users WHERE menuItemID = ?", IntegerPopulator.getPopulator());

		query.setInt(1, virtualMenuItem.getMenuItemID());

		virtualMenuItem.setAllowedUserIDs(query.executeQuery());
	}

	private void deleteMenuItemGroups(TransactionHandler transactionHandler, VirtualMenuItem virtualMenuItem) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM openhierarchy_virtual_menu_item_groups WHERE menuItemID = ?");

		query.setInt(1, virtualMenuItem.getMenuItemID());

		query.executeUpdate();

	}

	private void deleteMenuItemUsers(TransactionHandler transactionHandler, VirtualMenuItem virtualMenuItem) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM openhierarchy_virtual_menu_item_users WHERE menuItemID = ?");

		query.setInt(1, virtualMenuItem.getMenuItemID());

		query.executeUpdate();
	}

	private void setGroups(TransactionHandler transactionHandler, VirtualMenuItem virtualMenuItem) throws SQLException {

		for (Integer groupID : virtualMenuItem.getAllowedGroupIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO openhierarchy_virtual_menu_item_groups VALUES (?,?)");

			query.setInt(1, virtualMenuItem.getMenuItemID());
			query.setInt(2, groupID);

			query.executeUpdate();
		}

	}

	private void setUsers(TransactionHandler transactionHandler, VirtualMenuItem virtualMenuItem) throws SQLException {

		for (Integer userID : virtualMenuItem.getAllowedUserIDs()) {
			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO openhierarchy_virtual_menu_item_users VALUES (?,?)");

			query.setInt(1, virtualMenuItem.getMenuItemID());
			query.setInt(2, userID);

			query.executeUpdate();
		}

	}
}
