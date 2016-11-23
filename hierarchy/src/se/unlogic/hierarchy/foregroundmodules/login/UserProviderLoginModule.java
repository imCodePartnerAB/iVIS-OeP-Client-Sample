/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.login;

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.webutils.http.URIParser;

public class UserProviderLoginModule extends BaseLoginModule<User> {

	private UserHandler userHandler;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.userHandler = systemInterface.getUserHandler();
	}

	@Override
	protected User findByUsernamePassword(String username, String password) throws SQLException {

		return this.userHandler.getUserByUsernamePassword(username, password, true, true);
	}

	@Override
	protected void setLastLogin(User user) throws SQLException {

		user.setCurrentLogin(new Timestamp(System.currentTimeMillis()));

		if (user instanceof MutableUser) {

			MutableUser mutableUser = (MutableUser) user;

			//TODO there must be a smarter way of solving this
			Timestamp lastLogin = user.getLastLogin();

			mutableUser.setLastLogin(user.getCurrentLogin());

			try {
				userHandler.updateUser(mutableUser, false, false, false);

			} catch (Exception e) {

				log.error("Unable to update last login for user " + user, e);
			}

			mutableUser.setLastLogin(lastLogin);
		}
	}

	@Override
	public boolean loginUser(HttpServletRequest req, URIParser uriParser, User user) throws Exception {

		this.setLoggedIn(req, uriParser, user);
		return true;
	}

}
