package se.unlogic.hierarchy.core.interfaces;

import java.util.List;

import se.unlogic.hierarchy.core.beans.Group;


public interface UserFormCallback {

	public boolean allowGroupAdministration();

	public List<Group> getAvailableGroups();

	public Group getGroup(Integer groupID);

	public boolean allowAdminFlagAccess();
}
