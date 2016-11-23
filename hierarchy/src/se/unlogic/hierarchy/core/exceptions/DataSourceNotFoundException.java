/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.exceptions;

public class DataSourceNotFoundException extends DataSourceException {

	private static final long serialVersionUID = 5057058041459429652L;
	private Integer dataSourceID;

	public DataSourceNotFoundException(int dataSourceID) {
		this.dataSourceID = dataSourceID;
	}

	public Integer getDataSourceID() {
		return dataSourceID;
	}

	public void setDataSourceID(Integer dataSourceID) {
		this.dataSourceID = dataSourceID;
	}

	@Override
	public String toString() {
		return "Unable to find datasource with dataSourceID: " + this.dataSourceID;
	}
}
