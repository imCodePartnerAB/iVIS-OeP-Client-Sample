/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

import java.sql.SQLException;

import se.unlogic.hierarchy.core.beans.DataSourceDescriptor;

public class DataSourceInstantiationException extends DataSourceException {


	private static final long serialVersionUID = 6629731064408860142L;
	private final DataSourceDescriptor dataSourceDescriptor;
	private final SQLException sqlException;

	public DataSourceInstantiationException(DataSourceDescriptor dataSourceDescriptor, SQLException sqlException) {
		this.dataSourceDescriptor = dataSourceDescriptor;
		this.sqlException = sqlException;
	}

	public DataSourceDescriptor getDataSourceDescriptor() {
		return dataSourceDescriptor;
	}

	public SQLException getSqlException() {
		return sqlException;
	}

	@Override
	public String toString() {
		return "Error instantating data source " + dataSourceDescriptor + ", " + sqlException;
	}

	@Override
	public Throwable getCause() {
		return this.sqlException;
	}

}
