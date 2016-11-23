/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting.daos;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import se.unlogic.standardutils.db.tableversionhandler.TableUpgradeException;

public abstract class MailDAOFactory {

	public abstract void init(DataSource dataSource) throws SQLException, IOException, TableUpgradeException, SAXException, ParserConfigurationException;

	public abstract MailDAO getMailDAO();
}
