/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.registration;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToAddUserException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteUserException;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateUserException;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;


public abstract class UserProviderRegistrationModule<UserType extends MutableUser> extends AnnotatedConfirmationRegistrationModule<UserType> {

	@ModuleSetting(allowsNull=true)
	@GroupMultiListSettingDescriptor(name="Default groups",description="The default groups assigned to new users")
	protected List<Integer> deafultGroupIDs;
	
	protected UserHandler userHandler;
	
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);
		
		userHandler = systemInterface.getUserHandler();
	}	

	@Override
	protected void addUser(UserType newUser) throws SQLException, UnableToAddUserException {

		this.userHandler.addUser(newUser,dataSource);
	}

	@Override
	protected void deleteUser(UserType newUser) throws SQLException, UnableToDeleteUserException {

		this.userHandler.deleteUser(newUser);
	}

	@Override
	protected void enableUserAccount(UserType newUser) throws SQLException, UnableToUpdateUserException {

		newUser.setEnabled(true);

		this.userHandler.updateUser(newUser, false,false, false);
	}

	@Override
	protected User findUserByEmail(String email) throws SQLException {

		return this.userHandler.getUserByEmail(email, false, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected UserType findUserByID(Integer userID) throws SQLException {

		return (UserType) this.userHandler.getUser(userID, false, false);
	}

	@Override
	protected User findUserByUsername(String username) throws SQLException {

		return this.userHandler.getUserByUsername(username, false, false);
	}

	@Override
	protected void setUserDefaultAccess(UserType newUser, HttpServletRequest req) {

		if(this.deafultGroupIDs != null){

			newUser.setGroups(this.systemInterface.getGroupHandler().getGroups(this.deafultGroupIDs, false));
		}
	}
}
