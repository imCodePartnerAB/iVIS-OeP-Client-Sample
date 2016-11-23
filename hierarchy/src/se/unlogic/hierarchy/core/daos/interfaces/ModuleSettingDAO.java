/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.TransactionHandler;

public interface ModuleSettingDAO<Descriptor> {

	public abstract void set(Descriptor descriptor, TransactionHandler transactionHandler) throws SQLException;
	
	public abstract void set(Descriptor descriptor) throws SQLException;

	public abstract void getSettingsHandler(Descriptor descriptor, Connection connection) throws SQLException;

}
