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
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.BaseModuleDescriptor;
import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.core.handlers.SimpleSettingHandler;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.populators.StringEntryPopulator;

public class MySQLModuleSettingDAO<Descriptor extends BaseModuleDescriptor> extends BaseDAO {

	public static final StringEntryPopulator POPULATOR = new StringEntryPopulator();

	private final String tableName;
	
	protected MySQLModuleSettingDAO(DataSource ds, String tableName) {
		super(ds);
		this.tableName = tableName;
	}

	public void set(Descriptor moduleDescriptor) throws SQLException {
		
		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = new TransactionHandler(dataSource);
			set(moduleDescriptor, transactionHandler);
			transactionHandler.commit();
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}
	
	public void set(Descriptor moduleDescriptor, TransactionHandler transactionHandler) throws SQLException {

		UpdateQuery deleteQuery = transactionHandler.getUpdateQuery("DELETE FROM " + tableName + " WHERE moduleID = ?");

		deleteQuery.setInt(1, moduleDescriptor.getModuleID());

		deleteQuery.executeUpdate();

		if (moduleDescriptor.getMutableSettingHandler() != null && !moduleDescriptor.getMutableSettingHandler().isEmpty()) {

			for (String id : moduleDescriptor.getMutableSettingHandler().getIDs()) {

				for (String value : moduleDescriptor.getMutableSettingHandler().getStrings(id)) {

					UpdateQuery updateQuery = transactionHandler.getUpdateQuery("INSERT INTO " + tableName + " VALUES (null,?,?,?)");

					updateQuery.setInt(1, moduleDescriptor.getModuleID());
					updateQuery.setString(2, id);
					updateQuery.setString(3, value);

					updateQuery.executeUpdate();
				}
			}
		}
	}

	public void getSettingsHandler(Descriptor moduleDescriptor, Connection connection) throws SQLException {
		
		ArrayListQuery<SimpleEntry<String, String>> query = new ArrayListQuery<SimpleEntry<String, String>>(connection, false, "SELECT id, value FROM " + tableName + " WHERE moduleID = ?", POPULATOR);

		query.setInt(1, moduleDescriptor.getModuleID());

		List<SimpleEntry<String, String>> valueList = query.executeQuery();

		if (valueList == null) {
			moduleDescriptor.setMutableSettingHandler(new SimpleSettingHandler());
			return;
		}

		HashMap<String, List<String>> valueMap = new HashMap<String, List<String>>();

		for (SimpleEntry<String, String> entry : valueList) {

			List<String> list = valueMap.get(entry.getKey());

			if (list == null) {
				list = new ArrayList<String>();
				valueMap.put(entry.getKey(), list);
			}

			list.add(entry.getValue());
		}

		moduleDescriptor.setMutableSettingHandler(new SimpleSettingHandler(valueMap));
	}
}
