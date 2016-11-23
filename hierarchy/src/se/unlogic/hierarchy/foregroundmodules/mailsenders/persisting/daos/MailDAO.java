/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos;

import java.sql.SQLException;

import se.unlogic.emailutils.framework.Email;
import se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.QueuedEmail;

public interface MailDAO {

	void add(Email email) throws SQLException;

	QueuedEmail get(long resendIntervall, int databaseID) throws SQLException;

	void delete(QueuedEmail email) throws SQLException;

	void updateAndRelease(QueuedEmail email) throws SQLException;

	void releaseAll(int databaseID) throws SQLException;

	long getMailCount() throws SQLException;
}
