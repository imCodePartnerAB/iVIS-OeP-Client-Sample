/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import org.apache.commons.dbcp.BasicDataSource;

import se.unlogic.hierarchy.core.beans.DataSourceDescriptor;

public class DBCPUtils {

	public static BasicDataSource createConnectionPool(DataSourceDescriptor dataSourceDescriptor){

		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(dataSourceDescriptor.getDriver());
		basicDataSource.setUsername(dataSourceDescriptor.getUsername());
		basicDataSource.setPassword(dataSourceDescriptor.getPassword());
		basicDataSource.setUrl(dataSourceDescriptor.getUrl());
		basicDataSource.setDefaultCatalog(dataSourceDescriptor.getDefaultCatalog());
		basicDataSource.setLogAbandoned(dataSourceDescriptor.logAbandoned());
		basicDataSource.setRemoveAbandoned(dataSourceDescriptor.removeAbandoned());

		if(dataSourceDescriptor.getRemoveTimeout() != null){
			basicDataSource.setRemoveAbandonedTimeout(dataSourceDescriptor.getRemoveTimeout());
		}

		basicDataSource.setTestOnBorrow(dataSourceDescriptor.testOnBorrow());
		basicDataSource.setValidationQuery(dataSourceDescriptor.getValidationQuery());
		basicDataSource.setMaxWait(dataSourceDescriptor.getMaxWait());
		basicDataSource.setMaxActive(dataSourceDescriptor.getMaxActive());
		basicDataSource.setMaxIdle(dataSourceDescriptor.getMaxIdle());
		basicDataSource.setMinIdle(dataSourceDescriptor.getMinIdle());

		return basicDataSource;
	}
}
