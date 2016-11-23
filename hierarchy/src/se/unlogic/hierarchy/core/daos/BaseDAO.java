/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public abstract class BaseDAO {
	protected Logger log = Logger.getLogger(this.getClass());
	protected DataSource dataSource;
	
	public BaseDAO(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}
}
