/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.cache;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.DataSourceDescriptor;
import se.unlogic.hierarchy.core.beans.DataSourceWrapper;
import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.daos.interfaces.DataSourceDAO;
import se.unlogic.hierarchy.core.enums.DataSourceType;
import se.unlogic.hierarchy.core.exceptions.DataSourceDisabledException;
import se.unlogic.hierarchy.core.exceptions.DataSourceInstantiationException;
import se.unlogic.hierarchy.core.exceptions.DataSourceNotFoundException;
import se.unlogic.hierarchy.core.exceptions.DataSourceNotFoundInContextException;
import se.unlogic.hierarchy.core.utils.DBCPUtils;
import se.unlogic.standardutils.db.DBUtils;

public class DataSourceCache {

	private static final Logger log = Logger.getLogger(DataSourceCache.class);

	private final HashMap<DataSourceDescriptor, DataSourceWrapper> dataSourceMap = new HashMap<DataSourceDescriptor, DataSourceWrapper>();
	private final WeakHashMap<DataSourceDescriptor, DataSourceWrapper> stoppedDataSourceMap = new WeakHashMap<DataSourceDescriptor, DataSourceWrapper>();
	private final DataSourceDAO dataSourceDAO;

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	public DataSourceCache(CoreDaoFactory coreDaoFactory) {

		this.dataSourceDAO = coreDaoFactory.getDataSourceDAO();
	}

	private DataSource cacheDataSource(DataSourceDescriptor dataSourceDescriptor) throws SQLException, DataSourceDisabledException, DataSourceNotFoundInContextException {

		if(dataSourceDescriptor.isEnabled()){

			log.info("Caching datasource " + dataSourceDescriptor);

			DataSourceWrapper stoppableDatasource = this.stoppedDataSourceMap.get(dataSourceDescriptor);

			if(stoppableDatasource != null){

				stoppableDatasource.setDataSource(getDataSourceInstance(dataSourceDescriptor));

				this.stoppedDataSourceMap.remove(dataSourceDescriptor);

			}else{

				stoppableDatasource = new DataSourceWrapper(getDataSourceInstance(dataSourceDescriptor), this, dataSourceDescriptor.getDataSourceID());
			}

			this.dataSourceMap.put(dataSourceDescriptor, stoppableDatasource);

			return stoppableDatasource;
		}else{
			throw new DataSourceDisabledException(dataSourceDescriptor);
		}
	}

	//Factory method
	private DataSource getDataSourceInstance(DataSourceDescriptor dataSourceDescriptor) throws SQLException, DataSourceNotFoundInContextException {

		if(dataSourceDescriptor.getType() == DataSourceType.ContainerManaged){

			try{
				return DBUtils.getDataSource(dataSourceDescriptor.getUrl());
			}catch(NamingException e){
				throw new DataSourceNotFoundInContextException(dataSourceDescriptor, e);
			}

		}else{

			BasicDataSource basicDataSource = DBCPUtils.createConnectionPool(dataSourceDescriptor);

			try{

				//Dummy call to initialize connection pool
				basicDataSource.getLogWriter();

				return basicDataSource;

			}catch(SQLException e){

				if(basicDataSource != null){

					try{
						basicDataSource.close();
					}catch(SQLException e1){

						log.error("Error closing data source after failed initialization", e);
					}
				}

				throw e;
			}
		}
	}

	public DataSource getDataSource(int dataSourceID) throws DataSourceNotFoundException, DataSourceInstantiationException, DataSourceDisabledException, DataSourceNotFoundInContextException {

		try{
			w.lock();

			DataSource dataSource = this.getCachedDataSource(dataSourceID);

			if(dataSource != null){

				return dataSource;

			}else{

				DataSourceDescriptor dataSourceDescriptor;

				try{
					dataSourceDescriptor = this.dataSourceDAO.get(dataSourceID);

					try{
						if(dataSourceDescriptor != null){
							return this.cacheDataSource(dataSourceDescriptor);
						}

					}catch(SQLException e){
						log.debug("Error instantating data source " + dataSourceDescriptor, e);

						throw new DataSourceInstantiationException(dataSourceDescriptor, e);
					}

				}catch(SQLException e){
					log.error("Error getting data source descriptor from DB for data source with ID " + dataSourceID, e);
				}

				throw new DataSourceNotFoundException(dataSourceID);
			}
		}finally{
			w.unlock();
		}
	}

	public DataSource getDataSource(DataSourceDescriptor dataSourceDescriptor) throws SQLException, DataSourceDisabledException, DataSourceNotFoundInContextException {

		try{
			w.lock();

			DataSource dataSource = this.getCachedDataSource(dataSourceDescriptor.getDataSourceID());

			if(dataSource != null){

				return dataSource;

			}else{

				return this.cacheDataSource(dataSourceDescriptor);
			}
		}finally{
			w.unlock();
		}
	}

	public void update(DataSourceDescriptor dataSourceDescriptor) throws SQLException, DataSourceNotFoundInContextException {

		try{
			w.lock();

			Entry<DataSourceDescriptor, DataSourceWrapper> cachedEntry = getCachedDataSourceEntry(dataSourceDescriptor.getDataSourceID());

			if(cachedEntry != null){

				DataSourceDescriptor oldDescritor = cachedEntry.getKey();
				DataSourceWrapper dataSourceWrapper = cachedEntry.getValue();

				log.info("Updating cached datasource " + oldDescritor);

				BasicDataSource oldDataSourceInstance = null;

				if(oldDescritor.getType() == DataSourceType.SystemManaged && dataSourceWrapper.getDataSource() instanceof BasicDataSource){

					// Save the old instance so it can be properly closed when the new one has been started
					oldDataSourceInstance = (BasicDataSource)dataSourceWrapper.getDataSource();
				}

				// Create new datasource instance
				DataSource dataSource;

				try{
					dataSource = this.getDataSourceInstance(dataSourceDescriptor);

				}catch(SQLException e){

					log.error("Error updating data source " + dataSourceDescriptor, e);
					throw e;
				}

				// Switch to new datasource instance in datasource wrapper
				dataSourceWrapper.setDataSource(dataSource);

				// Update key in datasource map
				this.dataSourceMap.remove(dataSourceDescriptor);
				this.dataSourceMap.put(dataSourceDescriptor, dataSourceWrapper);

				if(oldDataSourceInstance != null){
					try{
						oldDataSourceInstance.close();
					}catch(SQLException e){
						log.error("Error closing old instance datasource " + oldDescritor + " after update");
					}
				}
			}
		}finally{
			w.unlock();
		}
	}

	public void stop(int dataSourceID) {

		try{
			w.lock();

			Entry<DataSourceDescriptor, DataSourceWrapper> cachedEntry = getCachedDataSourceEntry(dataSourceID);

			if(cachedEntry != null){

				DataSourceDescriptor descriptor = cachedEntry.getKey();
				DataSourceWrapper dataSourceWrapper = cachedEntry.getValue();

				log.info("Stopping datasource " + descriptor);

				BasicDataSource dataSourceInstance = null;

				if(descriptor.getType() == DataSourceType.SystemManaged && dataSourceWrapper.getDataSource() instanceof BasicDataSource){

					// Save the datasource instance so it can be properly closed after the wrapper has been stopped
					dataSourceInstance = (BasicDataSource)dataSourceWrapper.getDataSource();
				}

				// Stop the datasource
				dataSourceWrapper.stop();

				// Remove key from datasource map
				this.dataSourceMap.remove(descriptor);

				//Add key to stopped datasource map
				this.stoppedDataSourceMap.put(descriptor, dataSourceWrapper);

				if(dataSourceInstance != null){
					try{
						//						String url = dataSourceInstance.getUrl();

						dataSourceInstance.close();

						//						Driver driver = DriverManager.getDriver(url);
						//
						//						if(driver != null){
						//
						//							log.debug("Succesfully unregistered JDBC driver " + driver + " for URL " + url);
						//							DriverManager.deregisterDriver(driver);
						//
						//						}else{
						//
						//							log.warn("Unable to unregister JDBC driver " + driver + " for URL " + url);
						//						}

					}catch(SQLException e){
						log.error("Error closing datasource " + descriptor, e);
					}
				}
			}
		}finally{
			w.unlock();
		}
	}

	public void delete(int dataSourceID) {

		try{
			w.lock();

			Entry<DataSourceDescriptor, DataSourceWrapper> cachedEntry = getCachedDataSourceEntry(dataSourceID);

			if(cachedEntry != null){

				DataSourceDescriptor descriptor = cachedEntry.getKey();
				DataSourceWrapper dataSourceWrapper = cachedEntry.getValue();

				log.info("Deleting datasource " + descriptor);

				BasicDataSource dataSourceInstance = null;

				if(descriptor.getType() == DataSourceType.SystemManaged && dataSourceWrapper.getDataSource() instanceof BasicDataSource){

					// Save the datasource instance so it can be properly closed after the wrapper has been stopped
					dataSourceInstance = (BasicDataSource)dataSourceWrapper.getDataSource();
				}

				// Stop the datasource
				dataSourceWrapper.delete();

				// Remove key from datasource map
				this.dataSourceMap.remove(descriptor);

				// Remove key from stopped datasource map
				this.stoppedDataSourceMap.remove(descriptor);

				if(dataSourceInstance != null){
					try{
						dataSourceInstance.close();
					}catch(SQLException e){
						log.error("Error closing datasource " + descriptor);
					}
				}
			}
		}finally{
			w.unlock();
		}
	}

	private DataSourceWrapper getCachedDataSource(int dataSourceID) {

		Entry<DataSourceDescriptor, DataSourceWrapper> cachedEntry = getCachedDataSourceEntry(dataSourceID);

		if(cachedEntry != null){

			return cachedEntry.getValue();
		}

		return null;
	}

	private Entry<DataSourceDescriptor, DataSourceWrapper> getCachedDataSourceEntry(int dataSourceID) {

		for(Entry<DataSourceDescriptor, DataSourceWrapper> entry : this.dataSourceMap.entrySet()){

			if(entry.getKey().getDataSourceID() == dataSourceID){
				return entry;
			}
		}

		return null;
	}

	public DataSourceDescriptor getCachedDataSourceDescriptor(int dataSourceID) {

		try{
			r.lock();

			Entry<DataSourceDescriptor, DataSourceWrapper> entry = this.getCachedDataSourceEntry(dataSourceID);

			if(entry != null){

				return entry.getKey();
			}

		}finally{
			r.unlock();
		}

		return null;
	}

	public boolean isCached(int dataSourceID) {

		try{
			r.lock();

			return this.getCachedDataSource(dataSourceID) != null;
		}finally{
			r.unlock();
		}
	}

	public boolean isCached(DataSourceDescriptor dataSourceDescriptor) {

		try{
			r.lock();

			return this.dataSourceMap.containsKey(dataSourceDescriptor);
		}finally{
			r.unlock();
		}
	}

	public boolean isEmpty() {

		try{
			r.lock();

			return this.dataSourceMap.isEmpty();
		}finally{
			r.unlock();
		}
	}

	public int size() {

		try{
			r.lock();

			return this.dataSourceMap.size();
		}finally{
			r.unlock();
		}
	}

	public ArrayList<DataSourceDescriptor> getCachedDataSourceDescriptors() {

		try{
			r.lock();

			return new ArrayList<DataSourceDescriptor>(this.dataSourceMap.keySet());
		}finally{
			r.unlock();
		}
	}

	public void unload() {

		try{
			w.lock();

			ArrayList<DataSourceDescriptor> descriptorList = this.getCachedDataSourceDescriptors();

			for(DataSourceDescriptor descriptor : descriptorList){

				this.stop(descriptor.getDataSourceID());
			}

			for(Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();){

				Driver driver = e.nextElement();

				if(driver.getClass().getClassLoader() == getClass().getClassLoader()){

					try{
						log.info("Deregistering JDBC driver " + driver);
						DriverManager.deregisterDriver(driver);

					}catch(SQLException e1){

						log.error("Error deregistering JDBC driver " + driver);
					}
				}
			}

			log.info("Datasource cache unloaded");

		}finally{
			w.unlock();
		}
	}
}
