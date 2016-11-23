package se.unlogic.hierarchy.foregroundmodules.groupadmin;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;


public interface GroupAccessHandler {

	public boolean isGroupAdmin(User user, Group group) throws SQLException;

	public List<Group> getUserAdminGroups(User user) throws SQLException;
}
