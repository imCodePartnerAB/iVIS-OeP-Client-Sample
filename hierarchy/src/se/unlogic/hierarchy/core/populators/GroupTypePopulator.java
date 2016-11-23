/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.populators;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;

public class GroupTypePopulator implements BeanStringPopulator<Group> {

	private final GroupHandler groupHandler;
	private final boolean getAttributes;
	
	public GroupTypePopulator(GroupHandler groupHandler) {

		super();
		this.groupHandler = groupHandler;
		this.getAttributes = false;
	}	
	
	public GroupTypePopulator(GroupHandler groupHandler, boolean getAttributes) {

		super();
		this.groupHandler = groupHandler;
		this.getAttributes = getAttributes;
	}

	@Override
	public String getPopulatorID() {

		return null;
	}

	@Override
	public Class<? extends Group> getType() {

		return Group.class;
	}

	@Override
	public Group getValue(String value) {

		return this.groupHandler.getGroup(Integer.parseInt(value), getAttributes);
	}

	@Override
	public boolean validateFormat(String value) {

		return NumberUtils.isInt(value);
	}
}
