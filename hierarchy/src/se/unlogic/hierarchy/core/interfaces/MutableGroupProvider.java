package se.unlogic.hierarchy.core.interfaces;

import java.sql.SQLException;

import se.unlogic.hierarchy.core.beans.Group;


public interface MutableGroupProvider extends GroupProvider {

	public boolean canAddGroupClass(Class<? extends Group> groupClass);
	
	public void addGroup(Group group) throws SQLException;
	
	public void updateGroup(Group group, boolean updateAttributes) throws SQLException;

	public void deleteGroup(Group group) throws SQLException;
}
