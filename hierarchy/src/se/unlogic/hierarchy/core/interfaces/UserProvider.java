/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.UserField;
import se.unlogic.standardutils.enums.Order;

public interface UserProvider extends Prioritized{

	public User getUser(Integer userID, boolean groups, boolean attributes) throws SQLException;

	public List<? extends User> getUsers(boolean groups, boolean attributes) throws SQLException;

	public List<? extends User> searchUsers(String query, boolean groups, boolean attributes, Integer maxHits) throws SQLException;

	public List<? extends User> getUsers(Collection<Integer> userIDs, boolean groups, boolean attributes) throws SQLException;

	public List<? extends User> getUsersByGroup(Integer groupID, boolean groups, boolean attributes) throws SQLException;

	public List<? extends User> getUsersByGroups(Collection<Integer> groupIDs, boolean attributes) throws SQLException;

	public List<? extends User> getUsersByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException;

	public User getUserByUsername(String username, boolean groups, boolean attributes) throws SQLException;

	public User getUserByUsernamePassword(String username, String password, boolean groups, boolean attributes) throws SQLException;

	public User getUserByEmail(String email, boolean groups, boolean attributes) throws SQLException;

	public User getUserByEmailPassword(String email, String password, boolean groups, boolean attributes) throws SQLException;

	public List<? extends User> getUserByAttribute(String attributeName, boolean groups, boolean attributes) throws SQLException;

	public User getUserByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes) throws SQLException;

	public List<? extends User> getUsersWithoutAttribute(String attributeName, boolean groups, boolean attributes) throws SQLException;

	public int getUserCount() throws SQLException;

	public int getUserCountByGroup(Integer groupID) throws SQLException;

	public int getDisabledUserCount() throws SQLException;

	public List<Character> getUserFirstLetterIndex(UserField filteringField) throws SQLException;

	public List<? extends User> getUsers(UserField filteringField, char startsWith, Order order, boolean groups, boolean attributes) throws SQLException;

	public boolean isProviderFor(User user);

	public DataSource getDataSource();

	public List<? extends User> getUsers(UserField sortingField, Order order, boolean groups, boolean attributes) throws SQLException;

}
