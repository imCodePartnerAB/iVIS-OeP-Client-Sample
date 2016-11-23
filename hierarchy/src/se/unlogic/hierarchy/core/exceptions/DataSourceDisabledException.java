/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

import se.unlogic.hierarchy.core.beans.DataSourceDescriptor;

public class DataSourceDisabledException extends DataSourceException {

	private static final long serialVersionUID = 3992743409510059185L;
	private DataSourceDescriptor dataSource;

	public DataSourceDisabledException(DataSourceDescriptor dataSourceBean) {
		this.dataSource = dataSourceBean;
	}

	public DataSourceDescriptor getDataSourceBean() {
		return dataSource;
	}

	public void setDataSourceBean(DataSourceDescriptor dataSourceBean) {
		this.dataSource = dataSourceBean;
	}

	@Override
	public String toString() {
		return "Unable to create connection pool for data source " + dataSource;
	}
}
