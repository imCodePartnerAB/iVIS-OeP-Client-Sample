/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.implementations.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleDataSourceDescriptor;
import se.unlogic.hierarchy.core.daos.interfaces.DataSourceDAO;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.populators.annotated.AnnotatedResultSetPopulator;

public class MySQLDataSourceDAO extends AnnotatedDAO<SimpleDataSourceDescriptor> implements DataSourceDAO {

	private static final AnnotatedResultSetPopulator<SimpleDataSourceDescriptor> POPULATOR = new AnnotatedResultSetPopulator<SimpleDataSourceDescriptor>(SimpleDataSourceDescriptor.class);

	protected MySQLDataSourceDAO(DataSource ds) {
		super(ds, SimpleDataSourceDescriptor.class, null, POPULATOR);
	}

	@Override
	public ArrayList<SimpleDataSourceDescriptor> getAll() throws SQLException {

		return new ArrayListQuery<SimpleDataSourceDescriptor>(this.dataSource.getConnection(), true, "SELECT * FROM openhierarchy_data_sources ORDER BY name, username", POPULATOR).executeQuery();
	}

	@Override
	public ArrayList<SimpleDataSourceDescriptor> getAll(boolean enabled) throws SQLException {

		ArrayListQuery<SimpleDataSourceDescriptor> query = new ArrayListQuery<SimpleDataSourceDescriptor>(this.dataSource.getConnection(), true, "SELECT * FROM openhierarchy_data_sources WHERE enabled = ? ORDER BY name, username", POPULATOR);

		query.setBoolean(1, enabled);

		return query.executeQuery();
	}

	@Override
	public SimpleDataSourceDescriptor get(Integer dataSourceID) throws SQLException {

		ObjectQuery<SimpleDataSourceDescriptor> query = new ObjectQuery<SimpleDataSourceDescriptor>(this.dataSource.getConnection(), true, "SELECT * FROM openhierarchy_data_sources WHERE dataSourceID = ? ORDER BY name, username", POPULATOR);

		query.setInt(1, dataSourceID);

		return query.executeQuery();
	}

	@Override
	public void add(SimpleDataSourceDescriptor bean, TransactionHandler transactionHandler) throws SQLException {

		super.add(bean, transactionHandler, null);
	}

	@Override
	public void update(SimpleDataSourceDescriptor bean, TransactionHandler transactionHandler) throws SQLException {

		super.update(bean, transactionHandler, null);
	}

	@Override
	public SimpleDataSourceDescriptor get(Integer dataSourceID, TransactionHandler transactionHandler) throws SQLException {

		ObjectQuery<SimpleDataSourceDescriptor> query = transactionHandler.getObjectQuery("SELECT * FROM openhierarchy_data_sources WHERE dataSourceID = ? ORDER BY name, username", POPULATOR);

		query.setInt(1, dataSourceID);

		return query.executeQuery();
	}

	@Override
	public List<SimpleDataSourceDescriptor> getAll(TransactionHandler transactionHandler) throws SQLException {

		return super.getAll((HighLevelQuery<SimpleDataSourceDescriptor>)null, transactionHandler);
	}	
}
