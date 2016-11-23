package se.unlogic.hierarchy.foregroundmodules.invitation;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.utils.HierarchyAnnotatedDAOFactory;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.SimpleInvitation;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.SimpleInvitationType;
import se.unlogic.hierarchy.foregroundmodules.userproviders.SimpleUser;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;


public class SimpleInvitationModule extends AnnotatedInvitationModule<SimpleInvitation, SimpleInvitationType, SimpleUser> {

	@Override
	protected AnnotatedDAO<SimpleInvitation> createDAO(DataSource dataSource) {

		HierarchyAnnotatedDAOFactory daoFactory = new HierarchyAnnotatedDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());

		return daoFactory.getDAO(SimpleInvitation.class);
	}

	@Override
	protected SimpleUser createNewUserInstance() {

		return new SimpleUser();
	}

	@Override
	protected void setUserGroups(SimpleUser invitedUser, SimpleInvitation invitation) {

		SimpleInvitationType invitationType = invitation.getInvitationType();

		if (!CollectionUtils.isEmpty(invitationType.getGroupIDs())) {
			invitedUser.setGroups(this.sectionInterface.getSystemInterface().getGroupHandler().getGroups(invitationType.getGroupIDs(), false));
		}
	}
}
