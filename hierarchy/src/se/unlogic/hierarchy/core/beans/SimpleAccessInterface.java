/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.standardutils.collections.CollectionUtils;

public class SimpleAccessInterface implements AccessInterface , Serializable{

	private static final long serialVersionUID = 8999001518585792348L;

	private boolean adminAccess;
	private boolean anonymousAccess;
	private boolean userAccess;
	private Collection<Integer> allowedGroupIDs;
	private Collection<Integer> allowedUserIDs;

	public SimpleAccessInterface(Collection<Integer> allowedGroupIDs, Collection<Integer> allowedUserIDs) {

		this.allowedGroupIDs = allowedGroupIDs;
		this.allowedUserIDs = allowedUserIDs;
	}

	public SimpleAccessInterface(boolean adminAccess, boolean anonymousAccess, boolean userAccess) {

		this.adminAccess = adminAccess;
		this.anonymousAccess = anonymousAccess;
		this.userAccess = userAccess;
	}

	public SimpleAccessInterface(boolean adminAccess, boolean anonymousAccess, boolean userAccess, Collection<Integer> allowedGroupIDs, Collection<Integer> allowedUserIDs) {

		this.adminAccess = adminAccess;
		this.anonymousAccess = anonymousAccess;
		this.userAccess = userAccess;
		this.allowedGroupIDs = allowedGroupIDs;
		this.allowedUserIDs = allowedUserIDs;
	}

	public SimpleAccessInterface(AccessInterface accessInterface){

		this.adminAccess = accessInterface.allowsAdminAccess();
		this.userAccess = accessInterface.allowsUserAccess();
		this.anonymousAccess = accessInterface.allowsAnonymousAccess();

		if(!CollectionUtils.isEmpty(accessInterface.getAllowedUserIDs())){

			if(!(accessInterface.getAllowedUserIDs() instanceof Serializable)){

				this.allowedUserIDs = new ArrayList<Integer>(accessInterface.getAllowedUserIDs());

			}else{

				this.allowedUserIDs = accessInterface.getAllowedUserIDs();
			}
		}

		if(!CollectionUtils.isEmpty(accessInterface.getAllowedGroupIDs())){

			if(!(accessInterface.getAllowedGroupIDs() instanceof Serializable)){

				this.allowedGroupIDs = new ArrayList<Integer>(accessInterface.getAllowedGroupIDs());

			}else{

				this.allowedGroupIDs = accessInterface.getAllowedGroupIDs();
			}
		}
	}

	@Override
	public boolean allowsAdminAccess() {

		return this.adminAccess;
	}

	@Override
	public boolean allowsAnonymousAccess() {

		return this.anonymousAccess;
	}

	@Override
	public boolean allowsUserAccess() {

		return userAccess;
	}

	@Override
	public Collection<Integer> getAllowedGroupIDs() {

		return this.allowedGroupIDs;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {

		return this.allowedUserIDs;
	}
}
