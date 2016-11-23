/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.populators;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;

public class UserTypePopulator implements BeanStringPopulator<User> {

	private final UserHandler userHandler;
	private boolean getGroups;
	private boolean getAttrbutes;

	public UserTypePopulator(UserHandler userHandler, boolean getGroups, boolean getAttributes) {

		this.userHandler = userHandler;
		this.getGroups = getGroups;
		this.setGetAttrbutes(getAttributes);
	}

	public boolean isGetGroups() {

		return getGroups;
	}

	public void setGetGroups(boolean getGroups) {

		this.getGroups = getGroups;
	}

	@Override
	public String getPopulatorID() {

		return null;
	}

	@Override
	public Class<? extends User> getType() {

		return User.class;
	}

	@Override
	public User getValue(String value) {

		return this.userHandler.getUser(Integer.parseInt(value), this.getGroups, this.getAttrbutes);
	}

	@Override
	public boolean validateFormat(String value) {

		return NumberUtils.isInt(value);
	}

	public boolean isGetAttrbutes() {

		return getAttrbutes;
	}

	public void setGetAttrbutes(boolean getAttrbutes) {

		this.getAttrbutes = getAttrbutes;
	}
}
