package se.unlogic.hierarchy.core.interfaces;

import java.sql.SQLException;

import se.unlogic.hierarchy.core.beans.User;


public interface MutableUserProvider extends UserProvider {

	public boolean canAddUserClass(Class<? extends User> userClass);
	
	public void addUser(User user) throws SQLException;
	
	public void updateUser(User user, boolean encryptPassword, boolean updateGroups, boolean updateAttributes) throws SQLException;

	public void deleteUser(User user) throws SQLException;
}	
