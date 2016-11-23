/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

import javax.naming.NamingException;

import se.unlogic.hierarchy.core.beans.DataSourceDescriptor;

public class DataSourceNotFoundInContextException extends DataSourceException {

	private static final long serialVersionUID = -5510874699073913528L;
	private DataSourceDescriptor dataSourceBean;
	private NamingException namingException;

	public DataSourceNotFoundInContextException(DataSourceDescriptor dataSourceBean, NamingException namingException) {
		this.dataSourceBean = dataSourceBean;
		this.namingException = namingException;
	}

	public DataSourceDescriptor getDataSourceBean() {
		return dataSourceBean;
	}

	public NamingException getNamingException() {
		return namingException;
	}

	@Override
	public String toString() {
		return "Unable to find datasource " + dataSourceBean + " in context, error: " + namingException;
	}
}
