/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.ForegroundModuleDAO;
import se.unlogic.hierarchy.core.populators.ForegroundModuleDescriptorPopulator;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.IntegerKeyCollector;
import se.unlogic.standardutils.dao.querys.UpdateQuery;

public class MySQLForegroundModuleDAO extends MySQLModuleDAO<SimpleForegroundModuleDescriptor> implements ForegroundModuleDAO {

	private static final ForegroundModuleDescriptorPopulator POPULATOR = new ForegroundModuleDescriptorPopulator();

	protected MySQLForegroundModuleDAO(DataSource ds, MySQLForegroundModuleSettingDAO moduleSettingDAO, MySQLModuleAttributeDAO<SimpleForegroundModuleDescriptor> moduleAttributeDAO){

		super(moduleSettingDAO, moduleAttributeDAO ,ds ,"openhierarchy_foreground_modules", "openhierarchy_foreground_module_users", "openhierarchy_foreground_module_groups");
	}

	@Override
	public void add(SimpleForegroundModuleDescriptor descriptor) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO openhierarchy_foreground_modules VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			query.setObject(1, descriptor.getModuleID());
			query.setString(2, descriptor.getClassname());
			query.setString(3, descriptor.getName());
			query.setString(4, descriptor.getAlias());
			query.setString(5, descriptor.getDescription());
			query.setString(6, descriptor.getXslPath());

			if (descriptor.getXslPathType() != null) {
				query.setString(7, descriptor.getXslPathType().toString());
			} else {
				query.setString(7, null);
			}

			query.setBoolean(8, descriptor.allowsAnonymousAccess());
			query.setBoolean(9, descriptor.allowsUserAccess());
			query.setBoolean(10, descriptor.allowsAdminAccess());
			query.setBoolean(11, descriptor.isEnabled());
			query.setBoolean(12, descriptor.isVisibleInMenu());
			query.setObject(13, descriptor.getSectionID());
			query.setObject(14, descriptor.getDataSourceID());
			query.setObject(15, descriptor.getStaticContentPackage());
			if(descriptor.getRequiredProtocol() != null) {
				query.setString(16, descriptor.getRequiredProtocol().toString());
			} else {
				query.setString(16, null);
			}

			IntegerKeyCollector keyCollector = new IntegerKeyCollector();

			query.executeUpdate(keyCollector);

			descriptor.setModuleID(keyCollector.getKeyValue());

			if (descriptor.getAllowedUserIDs() != null && !descriptor.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, descriptor);
			}

			if (descriptor.getAllowedGroupIDs() != null && !descriptor.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, descriptor);
			}

			if (descriptor.getMutableSettingHandler() != null && !descriptor.getMutableSettingHandler().isEmpty()) {
				this.moduleSettingDAO.set(descriptor, transactionHandler);
			}

			if(descriptor.getAttributeHandler() != null && !descriptor.getAttributeHandler().isEmpty()){
				this.moduleAttributeDAO.set(descriptor, transactionHandler);
			}

			transactionHandler.commit();
		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	@Override
	public void update(SimpleForegroundModuleDescriptor descriptor) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("UPDATE openhierarchy_foreground_modules SET name = ?, alias = ?, description = ?, xslPath = ?, xslPathType = ?, anonymousAccess = ?, userAccess = ?, adminAccess = ?, enabled = ?, visibleInMenu = ?, sectionID = ?, classname = ?, dataSourceID = ?, staticContentPackage = ?, requiredProtocol = ? WHERE moduleID = ?");

			query.setString(1, descriptor.getName());
			query.setString(2, descriptor.getAlias());
			query.setString(3, descriptor.getDescription());
			query.setString(4, descriptor.getXslPath());

			if (descriptor.getXslPathType() != null) {
				query.setString(5, descriptor.getXslPathType().toString());
			} else {
				query.setString(5, null);
			}

			query.setBoolean(6, descriptor.allowsAnonymousAccess());
			query.setBoolean(7, descriptor.allowsUserAccess());
			query.setBoolean(8, descriptor.allowsAdminAccess());
			query.setBoolean(9, descriptor.isEnabled());
			query.setBoolean(10, descriptor.isVisibleInMenu());
			query.setObject(11, descriptor.getSectionID());
			query.setString(12, descriptor.getClassname());
			query.setObject(13, descriptor.getDataSourceID());
			query.setObject(14, descriptor.getStaticContentPackage());
			if (descriptor.getRequiredProtocol() != null) {
				query.setString(15, descriptor.getRequiredProtocol().toString());
			} else {
				query.setString(15, null);
			}
			query.setInt(16, descriptor.getModuleID());

			query.executeUpdate();

			this.deleteModuleUsers(transactionHandler, descriptor);

			if (descriptor.getAllowedUserIDs() != null && !descriptor.getAllowedUserIDs().isEmpty()) {
				this.setUsers(transactionHandler, descriptor);
			}

			this.deleteModuleGroups(transactionHandler, descriptor);

			if (descriptor.getAllowedGroupIDs() != null && !descriptor.getAllowedGroupIDs().isEmpty()) {
				this.setGroups(transactionHandler, descriptor);
			}

			this.moduleSettingDAO.set(descriptor, transactionHandler);
			this.moduleAttributeDAO.set(descriptor, transactionHandler);

			transactionHandler.commit();
		} finally {

			TransactionHandler.autoClose(transactionHandler);
		}
	}

	@Override
	protected BeanResultSetPopulator<SimpleForegroundModuleDescriptor> getPopulator() {

		return POPULATOR;
	}

	@Override
	public List<SimpleForegroundModuleDescriptor> getModulesByAttribute(String name, String value) throws SQLException {

		List<Integer> moduleIDs = this.moduleAttributeDAO.getModulesIDsByAttribute(name, value);
		
		if(moduleIDs == null){
			
			return null;
		}
		
		return getModules(moduleIDs);
	}
}
