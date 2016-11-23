/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.io.PrintWriter;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.cache.DataSourceCache;
import se.unlogic.hierarchy.core.enums.DataSourceStatus;
import se.unlogic.hierarchy.core.exceptions.DataSourceDeletedException;
import se.unlogic.hierarchy.core.exceptions.DataSourceException;

public class DataSourceWrapper implements DataSource {

	private DataSource dataSource;
	private DataSourceCache dataSourceCache;
	private int dataSourceID;
	private boolean deleted;

	public DataSourceWrapper(DataSource dataSource, DataSourceCache dataSourceCache, int dataSourceID) {
		super();
		this.dataSource = dataSource;
		this.dataSourceCache = dataSourceCache;
		this.dataSourceID = dataSourceID;
	}

	public void setDataSource(DataSource dataSource) {

		if (deleted) {
			throw new DataSourceDeletedException();
		}

		this.dataSource = dataSource;
	}

	public void stop() {

		// Null reference to datasource in order to stop access to the datasource and enable garbage collection of the datasource object
		this.dataSource = null;
	}

	public void delete() {

		if (deleted) {
			throw new DataSourceDeletedException();
		}

		// Null reference to datasource in order to stop access to the datasource and enable garbage collection of the datasource object
		this.deleted = true;
		this.dataSource = null;
		this.dataSourceCache = null;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getDataSource().getConnection(username, password);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return getDataSource().getLoginTimeout();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return getDataSource().getLogWriter();
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		getDataSource().setLoginTimeout(seconds);
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		getDataSource().setLogWriter(out);
	}

	public DataSource getDataSource() {

		if (deleted) {
			throw new DataSourceDeletedException();
		}

		// This step is nessecary in order to prevent NullPointers when the stop() is being called by another thread
		DataSource dataSource = this.dataSource;

		if(dataSource == null){

			//Datasource has been stopped try to cache it again
			try{
				this.dataSourceCache.getDataSource(dataSourceID);

			}catch(DataSourceException e){

				throw new RuntimeException(e);
			}

			dataSource = this.dataSource;

			if(dataSource == null){

				throw new IllegalStateException("Unable to get new instance of stopped datasource");
			}
		}

		return dataSource;
	}

	@Override
	public String toString() {

		return "Wrapping datasource: " + this.dataSource;
	}

	public DataSourceStatus getDataSourceStatus() {

		if(dataSource != null){

			return DataSourceStatus.CACHED;

		}else if(deleted){

			return DataSourceStatus.DELETED;

		}else{

			return DataSourceStatus.STOPPED;
		}
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {

		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {

		return false;
	}
	
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return this.dataSource.getParentLogger();
	}
}
