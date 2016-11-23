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
import java.util.Collection;
import java.util.Set;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.Bundle;
import se.unlogic.hierarchy.core.beans.MenuItem;
import se.unlogic.hierarchy.core.beans.ModuleMenuItem;
import se.unlogic.hierarchy.core.beans.SectionMenuItem;
import se.unlogic.hierarchy.core.beans.VirtualMenuItem;
import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.core.daos.interfaces.MenuIndexDAO;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.populators.IntegerPopulator;

public class MySQLMenuIndexDAO extends BaseDAO implements MenuIndexDAO {

	protected MySQLMenuIndexDAO(DataSource ds) {
		super(ds);
	}

	public synchronized Bundle addBundleMenuIndex(Bundle bundle, Connection connection) throws SQLException {

		log.debug("Adding new menuindex for menuitem " + bundle);

		int menuIndex = this.getHighestMenuIndex(bundle.getSectionID(), connection) + 1;

		this.addMenuIndex(bundle.getSectionID(), menuIndex, bundle.getModuleID(), bundle.getUniqueID(), null, null, connection);

		bundle.setMenuIndex(menuIndex);

		return bundle;

	}

	public synchronized ModuleMenuItem addModuleMenuIndex(ModuleMenuItem moduleMenuItem, Connection connection) throws SQLException {

		log.debug("Adding new menuindex for menuitem " + moduleMenuItem);

		int menuIndex = this.getHighestMenuIndex(moduleMenuItem.getSectionID(), connection) + 1;

		this.addMenuIndex(moduleMenuItem.getSectionID(), menuIndex, moduleMenuItem.getModuleID(), moduleMenuItem.getUniqueID(), null, null, connection);

		moduleMenuItem.setMenuIndex(menuIndex);

		return moduleMenuItem;

	}

	public synchronized SectionMenuItem addSectionMenuIndex(SectionMenuItem sectionMenuItem, Connection connection) throws SQLException {

		log.debug("Adding new menuindex for menuitem " + sectionMenuItem);

		int menuIndex = this.getHighestMenuIndex(sectionMenuItem.getSectionID(), connection) + 1;

		this.addMenuIndex(sectionMenuItem.getSectionID(), menuIndex, null, null, sectionMenuItem.getSubSectionID(), null, connection);

		sectionMenuItem.setMenuIndex(menuIndex);

		return sectionMenuItem;

	}

	public synchronized VirtualMenuItem addVirtualMenuIndex(VirtualMenuItem virtualMenuItem, Connection connection) throws SQLException {

		log.debug("Adding new menuindex for menuitem " + virtualMenuItem);

		int menuIndex = this.getHighestMenuIndex(virtualMenuItem.getSectionID(), connection) + 1;

		this.addMenuIndex(virtualMenuItem.getSectionID(), menuIndex, null, null, null, virtualMenuItem.getMenuItemID(), connection);

		virtualMenuItem.setMenuIndex(menuIndex);

		return virtualMenuItem;

	}

	@Override
	public void updateMenuIndex(Set<MenuItem> menuItems) throws SQLException {

		log.debug("updating menuindexes..");

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			for (MenuItem menuItem : menuItems) {

				if (menuItem instanceof ModuleMenuItem) {

					this.updateModuleMenuIndex((ModuleMenuItem) menuItem, transactionHandler);

				} else if (menuItem instanceof VirtualMenuItem) {

					this.updateVirtualMenuIndex((VirtualMenuItem) menuItem, transactionHandler);

				} else if (menuItem instanceof SectionMenuItem) {

					this.updateSectionMenuIndex((SectionMenuItem) menuItem, transactionHandler);

				} else if (menuItem instanceof Bundle) {

					this.updateBundleMenuIndex((Bundle) menuItem, transactionHandler);
				}
			}

			transactionHandler.commit();
		} finally {

			if (transactionHandler != null && !transactionHandler.isClosed()) {
				transactionHandler.abort();
			}
		}
	}

	public void updateBundleMenuIndex(Bundle bundle, TransactionHandler transactionHandler) throws SQLException {

		log.debug("Updating menu index for bundle " + bundle);

		UpdateQuery updateQuery = transactionHandler.getUpdateQuery("UPDATE openhierarchy_menu_index SET menuIndex = ? WHERE moduleID = ? AND sectionID = ? AND uniqueID = ?");

		updateQuery.setInt(1, bundle.getMenuIndex());
		updateQuery.setInt(2, bundle.getModuleID());
		updateQuery.setInt(3, bundle.getSectionID());
		updateQuery.setString(4, bundle.getUniqueID());

		updateQuery.executeUpdate();

		log.debug("Menu index for bundle " + bundle + " updated!");

	}

	public void updateSectionMenuIndex(SectionMenuItem sectionMenuItem, TransactionHandler transactionHandler) throws SQLException {

		log.debug("Updating menu index for section menuitem " + sectionMenuItem);

		UpdateQuery updateQuery = transactionHandler.getUpdateQuery("UPDATE openhierarchy_menu_index SET menuIndex = ? WHERE moduleID IS NULL AND uniqueID IS NULL AND sectionID = ? AND subsectionID = ?;");

		updateQuery.setInt(1, sectionMenuItem.getMenuIndex());
		updateQuery.setInt(2, sectionMenuItem.getSectionID());
		updateQuery.setInt(3, sectionMenuItem.getSubSectionID());

		updateQuery.executeUpdate();

		log.debug("Menu index for section menuitem " + sectionMenuItem + " updated!");

	}

	public void updateVirtualMenuIndex(VirtualMenuItem virtualMenuItem, TransactionHandler transactionHandler) throws SQLException {

		log.debug("Updating menu index for virtual menuitem " + virtualMenuItem);

		UpdateQuery updateQuery = transactionHandler.getUpdateQuery("UPDATE openhierarchy_menu_index SET menuIndex = ? WHERE moduleID IS NULL AND uniqueID IS NULL AND subsectionID IS NULL AND sectionID = ? AND menuItemID = ?");

		updateQuery.setInt(1, virtualMenuItem.getMenuIndex());
		updateQuery.setInt(2, virtualMenuItem.getSectionID());
		updateQuery.setInt(3, virtualMenuItem.getMenuItemID());

		updateQuery.executeUpdate();

		log.debug("Menuindex for virtual menuitem " + virtualMenuItem + " updated!");

	}

	public void updateModuleMenuIndex(ModuleMenuItem moduleMenuItem, TransactionHandler transactionHandler) throws SQLException {

		log.debug("Updating menu index for module menuitem " + moduleMenuItem);

		UpdateQuery updateQuery = transactionHandler.getUpdateQuery("UPDATE openhierarchy_menu_index SET menuIndex = ? WHERE moduleID = ? AND uniqueID = ? AND subsectionID IS NULL AND sectionID = ?");

		updateQuery.setInt(1, moduleMenuItem.getMenuIndex());
		updateQuery.setInt(2, moduleMenuItem.getModuleID());
		updateQuery.setString(3, moduleMenuItem.getUniqueID());
		updateQuery.setInt(4, moduleMenuItem.getSectionID());

		updateQuery.executeUpdate();

		log.debug("Menuindex for module menuitem " + moduleMenuItem + " updated!");

	}

	@Override
	public void populateBundleMenuIndex(Collection<Bundle> bundles) throws SQLException {

		log.debug("Getting menu index for bundles...");
		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			for (Bundle bundle : bundles) {

				ObjectQuery<Integer> objectQuery = new ObjectQuery<Integer>(connection, false, "SELECT menuIndex FROM openhierarchy_menu_index WHERE uniqueID = ? AND moduleID = ? AND sectionID = ?", IntegerPopulator.getPopulator());

				objectQuery.setString(1, bundle.getUniqueID());
				objectQuery.setInt(2, bundle.getModuleID());
				objectQuery.setInt(3, bundle.getSectionID());

				Integer menuIndex = objectQuery.executeQuery();

				if (menuIndex != null) {
					bundle.setMenuIndex(menuIndex);
				} else {
					log.debug("Adding new menu index to bundle " + bundle);
					this.addBundleMenuIndex(bundle, connection);
				}

			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void populateModuleMenuIndex(Collection<ModuleMenuItem> moduleMenuItems) throws SQLException {

		log.debug("Getting menuidex for moduleMenuItems...");
		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			for (ModuleMenuItem moduleMenuItem : moduleMenuItems) {

				ObjectQuery<Integer> objectQuery = new ObjectQuery<Integer>(connection, false, "SELECT menuIndex FROM openhierarchy_menu_index WHERE uniqueID = ? AND moduleID = ? AND sectionID = ?", IntegerPopulator.getPopulator());

				objectQuery.setString(1, moduleMenuItem.getUniqueID());
				objectQuery.setInt(2, moduleMenuItem.getModuleID());
				objectQuery.setInt(3, moduleMenuItem.getSectionID());

				Integer menuIndex = objectQuery.executeQuery();

				if (menuIndex != null) {
					moduleMenuItem.setMenuIndex(menuIndex);
				} else {
					log.debug("Adding new menu index to module menuitem " + moduleMenuItem);
					this.addModuleMenuIndex(moduleMenuItem, connection);
				}

			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	@Override
	public void populateVirtualMenuIndex(Collection<VirtualMenuItem> virtualMenuItems) throws SQLException {

		log.debug("Getting menuidex for virtualMenuItems...");
		Connection connection = null;

		try {
			// Create a new connection using DBhandler class
			connection = this.dataSource.getConnection();

			for (VirtualMenuItem virtualMenuItem : virtualMenuItems) {

				ObjectQuery<Integer> objectQuery = new ObjectQuery<Integer>(connection, false, "SELECT menuIndex FROM openhierarchy_menu_index WHERE uniqueID IS NULL AND moduleID IS NULL AND sectionID = ? AND menuItemID = ?", IntegerPopulator.getPopulator());

				objectQuery.setInt(1, virtualMenuItem.getSectionID());
				objectQuery.setInt(2, virtualMenuItem.getMenuItemID());

				Integer menuIndex = objectQuery.executeQuery();

				if (menuIndex != null) {
					virtualMenuItem.setMenuIndex(menuIndex);
				} else {
					log.debug("Adding new menu index to virtual menuitem " + virtualMenuItem);
					this.addVirtualMenuIndex(virtualMenuItem, connection);
				}
			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}

	}

	@Override
	public void populateSectionMenuIndex(SectionMenuItem sectionMenuItem) throws SQLException {

		log.debug("Getting menuidex for sectionMenuItem");
		Connection connection = null;

		try {
			// Create a new connection using DBhandler class
			connection = this.dataSource.getConnection();

			ObjectQuery<Integer> objectQuery = new ObjectQuery<Integer>(connection, false, "SELECT menuIndex FROM openhierarchy_menu_index WHERE moduleID IS NULL AND uniqueID IS NULL AND sectionID = ? AND subsectionID = ?", IntegerPopulator.getPopulator());

			objectQuery.setInt(1, sectionMenuItem.getSectionID());
			objectQuery.setInt(2, sectionMenuItem.getSubSectionID());

			Integer menuIndex = objectQuery.executeQuery();

			if (menuIndex != null) {
				sectionMenuItem.setMenuIndex(menuIndex);
			} else {
				log.debug("Adding new menuIndex to " + sectionMenuItem);
				this.addSectionMenuIndex(sectionMenuItem, connection);
			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}

	}

	private void addMenuIndex(Integer sectionID, Integer menuIndex, Integer moduleID, String uniqueID, Integer subSectionID, Integer menuItemID, Connection connection) throws SQLException {

		UpdateQuery updateQuery = new UpdateQuery(connection, false, "INSERT INTO openhierarchy_menu_index VALUES (null,?,?,?,?,?,?);");

		updateQuery.setObject(1, sectionID);
		updateQuery.setObject(2, menuIndex);
		updateQuery.setObject(3, moduleID);
		updateQuery.setObject(4, uniqueID);
		updateQuery.setObject(5, subSectionID);
		updateQuery.setObject(6, menuItemID);

		updateQuery.executeUpdate();

	}

	public synchronized int getHighestMenuIndex(int sectionID, Connection connection) throws SQLException {

		log.debug("Getting highest menu index for section " + sectionID);

		ObjectQuery<Integer> objectQuery = new ObjectQuery<Integer>(connection, false, "SELECT MAX(menuIndex) as max FROM openhierarchy_menu_index WHERE sectionID = ?", IntegerPopulator.getPopulator());

		objectQuery.setInt(1, sectionID);

		return objectQuery.executeQuery();
	}
}
