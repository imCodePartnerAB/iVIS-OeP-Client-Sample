/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.invitation.beans;

import java.util.List;

import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.XMLElement;

@Table(name="user_invitation_types")
@XMLElement(name="InvitationType")
public class SimpleInvitationType extends BaseInvitationType {

	@DAOManaged
	@OneToMany(autoAdd = true, autoGet = true, autoUpdate = true)
	@SimplifiedRelation(addTablePrefix=true,deplurifyTablePrefix=true,table="_groups",remoteValueColumnName="groupID")
	@WebPopulate(paramName="groupid")
	@XMLElement(childName="groupID")
	private List<Integer> groupIDs;

	@DAOManaged
	@OneToMany
	@XMLElement
	private List<SimpleInvitation> invitations;


	public List<Integer> getGroupIDs() {

		return groupIDs;
	}


	public void setGroupIDs(List<Integer> groupIDs) {

		this.groupIDs = groupIDs;
	}


	public List<SimpleInvitation> getInvitations() {

		return invitations;
	}


	public void setInvitations(List<SimpleInvitation> simpleInvitations) {

		this.invitations = simpleInvitations;
	}
}
