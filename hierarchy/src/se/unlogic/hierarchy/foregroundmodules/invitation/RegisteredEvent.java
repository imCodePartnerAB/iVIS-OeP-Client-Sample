package se.unlogic.hierarchy.foregroundmodules.invitation;

import java.io.Serializable;

import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitation;

public class RegisteredEvent<I extends BaseInvitation, U extends MutableUser> implements Serializable {

	private static final long serialVersionUID = -8039486239730982788L;

	private final I invitation;

	private final U invitedUser;

	public RegisteredEvent(I invitation, U invitedUser) {

		super();
		this.invitation = invitation;
		this.invitedUser = invitedUser;
	}

	public I getInvitation() {

		return invitation;
	}

	public U getInvitedUser() {

		return invitedUser;
	}

}
