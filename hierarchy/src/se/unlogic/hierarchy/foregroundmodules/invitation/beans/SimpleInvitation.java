/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.invitation.beans;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.XMLElement;

@Table(name="user_invitations")
@XMLElement(name="Invitation")
public class SimpleInvitation extends BaseInvitation {

	@DAOManaged(columnName="invitationTypeID")
	@ManyToOne(autoGet = true)
	@XMLElement
	private SimpleInvitationType simpleInvitationType;


	@SuppressWarnings("unchecked")
	@Override
	public SimpleInvitationType getInvitationType() {

		return simpleInvitationType;
	}


	public void setInvitationType(SimpleInvitationType simpleInvitationType) {

		this.simpleInvitationType = simpleInvitationType;
	}
}
