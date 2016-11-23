/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.invitation.cruds;

import se.unlogic.hierarchy.foregroundmodules.invitation.SimpleInvitationAdminModule;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.SimpleInvitation;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.SimpleInvitationType;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

public class SimpleInvitationCRUD extends BaseInvitationCRUD<SimpleInvitation, SimpleInvitationType, SimpleInvitationAdminModule> {


	public static final AnnotatedRequestPopulator<SimpleInvitation> POPULATOR = new AnnotatedRequestPopulator<SimpleInvitation>(SimpleInvitation.class);

	public SimpleInvitationCRUD(CRUDDAO<SimpleInvitation, Integer> crudDAO, SimpleInvitationAdminModule callback) {

		super(crudDAO, POPULATOR, "Invitation", "invitation", "", callback);
	}

	@Override
	protected void setInvitationType(SimpleInvitation invitation, SimpleInvitationType invitationType) {

		invitation.setInvitationType(invitationType);
	}
}
