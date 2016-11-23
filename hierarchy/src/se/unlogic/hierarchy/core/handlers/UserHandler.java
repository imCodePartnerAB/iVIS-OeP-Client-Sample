/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.UserTypeDescriptor;
import se.unlogic.hierarchy.core.comparators.PriorityComparator;
import se.unlogic.hierarchy.core.enums.UserField;
import se.unlogic.hierarchy.core.exceptions.UnableToAddUserException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteUserException;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateUserException;
import se.unlogic.hierarchy.core.interfaces.MutableUserProvider;
import se.unlogic.hierarchy.core.interfaces.UserFormProvider;
import se.unlogic.hierarchy.core.interfaces.UserProvider;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.string.StringUtils;

public class UserHandler {

	private static final PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator(Order.ASC);

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	private final Logger log = Logger.getLogger(this.getClass());
	private final ArrayList<UserProvider> userProviders = new ArrayList<UserProvider>();

	public boolean addProvider(UserProvider userProvider) {

		if(userProvider == null){

			return false;
		}

		w.lock();
		try {

			if (!userProviders.contains(userProvider)) {

				userProviders.add(userProvider);

				Collections.sort(userProviders, PRIORITY_COMPARATOR);

				return true;
			}

			return false;

		} finally {
			w.unlock();
		}
	}

	public ArrayList<UserProvider> getUserProviders() {

		r.lock();
		try {
			return new ArrayList<UserProvider>(this.userProviders);
		} finally {
			r.unlock();
		}
	}

	public boolean removeProvider(UserProvider userProvider) {

		w.lock();
		try {

			return userProviders.remove(userProvider);

		} finally {
			w.unlock();
		}
	}

	public void sortProviders(){

		w.lock();
		try {

			Collections.sort(userProviders, PRIORITY_COMPARATOR);

		} finally {
			w.unlock();
		}
	}

	public User getUser(Integer userID, boolean groups, boolean attributes) {

		r.lock();
		try {
			User user = null;

			for (UserProvider userProvider : this.userProviders) {

				try {
					user = userProvider.getUser(userID, groups, attributes);

					if (user != null) {

						if (log.isDebugEnabled()) {
							log.debug("found user " + user + " in user provider " + userProvider);
						}

						return user;
					}

				} catch (Exception e) {

					log.error("Error getting user with userID " + userID + " from user provider " + userProvider, e);
				}
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public User getUserByUsernamePassword(String username, String password, boolean groups, boolean attributes) {

		r.lock();
		try {
			User user = null;

			for (UserProvider userProvider : this.userProviders) {
				try {
					user = userProvider.getUserByUsernamePassword(username, password, groups, attributes);

					if (user != null) {

						if (log.isDebugEnabled()) {
							log.debug("found user " + user + " in user provider " + userProvider);
						}

						return user;
					}

				} catch (Exception e) {

					log.error("Error getting user with username " + username + " from user provider " + userProvider, e);
				}
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public ArrayList<User> getUsers(boolean groups, boolean attributes) {

		r.lock();
		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				Collection<? extends User> users;

				try {
					users = userProvider.getUsers(groups, attributes);

					if (users != null) {
						userList.addAll(users);
					}

				} catch (Exception e) {

					log.error("Error getting users from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public boolean contains(UserProvider userProvider) {

		r.lock();
		try {
			return userProviders.contains(userProvider);
		} finally {
			r.unlock();
		}
	}

	public boolean containsAll(Collection<UserProvider> userProviderList) {

		r.lock();
		try {
			return userProviders.containsAll(userProviderList);
		} finally {
			r.unlock();
		}
	}

	public boolean isEmpty() {

		r.lock();
		try {
			return userProviders.isEmpty();
		} finally {
			r.unlock();
		}
	}

	public int size() {

		r.lock();
		try {
			return userProviders.size();
		} finally {
			r.unlock();
		}
	}

	public List<User> getUsers(Collection<Integer> userIDs, boolean groups, boolean attributes) {

		r.lock();
		try {
			ArrayList<Integer> usersLeft = new ArrayList<Integer>(userIDs);

			ArrayList<User> users = new ArrayList<User>();

			int position = 1;

			for (UserProvider userProvider : this.userProviders) {

				try {
					List<? extends User> providerUsers = userProvider.getUsers(usersLeft, groups, attributes);

					if (providerUsers != null) {

						users.addAll(providerUsers);

						if (position < userProviders.size()) {

							for (User user : providerUsers) {

								usersLeft.remove(user.getUserID());

								if (usersLeft.isEmpty()) {

									return users;
								}
							}
						}
					}

				} catch (Exception e) {

					log.error("Error getting users with userID in " + StringUtils.toCommaSeparatedString(usersLeft) + " from user provider " + userProvider, e);
				}

				position++;
			}

			if (!users.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(users, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				return users;
			}

			return null;

		} finally {
			r.unlock();
		}
	}

	public List<User> searchUsers(String query, boolean groups, boolean attributes, Integer maxHits) {

		if(maxHits != null && maxHits < 1){

			throw new RuntimeException("maxHits has to be null or > 0 ");
		}

		r.lock();
		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.searchUsers(query, groups, attributes, maxHits);

					if (users != null) {
						userList.addAll(users);
					}

				} catch (Exception e) {

					log.error("Error getting users using search query " + query + " from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				if(maxHits != null && userList.size() > maxHits){

					return userList.subList(0, maxHits);
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public User getUserByUsername(String username, boolean groups, boolean attributes) {

		r.lock();

		try {
			User user = null;

			for (UserProvider userProvider : this.userProviders) {

				try {
					user = userProvider.getUserByUsername(username, groups, attributes);

					if (user != null) {

						if (log.isDebugEnabled()) {
							log.debug("found user " + user + " in user provider " + userProvider);
						}

						return user;
					}

				} catch (Exception e) {

					log.error("Error getting user with username " + username + " from user provider " + userProvider, e);
				}
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public User getUserByEmail(String email, boolean groups, boolean attributes) {

		r.lock();

		try {
			User user = null;

			for (UserProvider userProvider : this.userProviders) {

				try {
					user = userProvider.getUserByEmail(email, groups, attributes);

					if (user != null) {

						if (log.isDebugEnabled()) {
							log.debug("found user " + user + " in user provider " + userProvider);
						}

						return user;
					}

				} catch (Exception e) {

					log.error("Error getting user with email " + email + " from user provider " + userProvider, e);
				}
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public User getUserByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes){

		r.lock();

		try {
			User user = null;

			for (UserProvider userProvider : this.userProviders) {

				try {
					user = userProvider.getUserByAttribute(attributeName, attributeValue, groups, attributes);

					if (user != null) {

						if (log.isDebugEnabled()) {
							log.debug("found user " + user + " in user provider " + userProvider);
						}

						return user;
					}

				} catch (Exception e) {

					log.error("Error getting users by attribute name " + attributeName + " with value " + attributeValue + " from user provider " + userProvider, e);
				}
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<User> getUsersByAttribute(String attributeName, boolean groups, boolean attributes) {

		r.lock();

		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.getUserByAttribute(attributeName, groups, attributes);

					if (users != null) {

						if (users != null) {
							userList.addAll(users);
						}
					}

				} catch (Exception e) {

					log.error("Error getting users by attribute name " + attributeName + " from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<User> getUsersWithoutAttribute(String attributeName, boolean groups, boolean attributes) {

		r.lock();

		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.getUsersWithoutAttribute(attributeName, groups, attributes);

					if (users != null) {

						if (users != null) {
							userList.addAll(users);
						}
					}

				} catch (Exception e) {

					log.error("Error getting users wtihout attribute name " + attributeName + " from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public User getUserByEmailPassword(String email, String password, boolean groups, boolean attributes) {

		r.lock();
		try {
			User user = null;

			for (UserProvider userProvider : this.userProviders) {
				try {
					user = userProvider.getUserByEmailPassword(email, password, groups, attributes);

					if (user != null) {

						if (log.isDebugEnabled()) {
							log.debug("found user " + user + " in user provider " + userProvider);
						}

						return user;
					}

				} catch (Exception e) {

					log.error("Error getting user with email " + email + " from user provider " + userProvider, e);
				}
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public Integer getUserCount() {

		r.lock();
		try {
			Integer userCount = 0;

			for (UserProvider userProvider : this.userProviders) {

				try {
					userCount += userProvider.getUserCount();

				} catch (Exception e) {

					log.error("Error getting user count from user provider " + userProvider, e);
				}
			}

			return userCount;
		} finally {
			r.unlock();
		}
	}

	public Integer getUserCountByGroup(int groupID) {

		r.lock();
		try {
			Integer userCount = 0;

			for (UserProvider userProvider : this.userProviders) {

				try {
					userCount += userProvider.getUserCountByGroup(groupID);

				} catch (Exception e) {

					log.error("Error getting user count for group " + groupID + " from user provider " + userProvider, e);
				}
			}

			return userCount;
		} finally {
			r.unlock();
		}
	}

	public Integer getDisabledUserCount() {

		r.lock();
		try {
			Integer userCount = 0;

			for (UserProvider userProvider : this.userProviders) {

				try {
					userCount += userProvider.getDisabledUserCount();

				} catch (Exception e) {

					log.error("Error getting disabled user count from user provider " + userProvider, e);
				}
			}

			return userCount;
		} finally {
			r.unlock();
		}
	}

	public Set<Character> getUserFirstLetterIndex(UserField filteringField) {

		r.lock();
		try {
			TreeSet<Character> letterIndex = new TreeSet<Character>();

			for (UserProvider userProvider : this.userProviders) {

				try {
					List<Character> providerLetterIndex = userProvider.getUserFirstLetterIndex(filteringField);

					if (providerLetterIndex != null) {

						letterIndex.addAll(providerLetterIndex);
					}

				} catch (Exception e) {

					log.error("Error getting user first letter index from user provider " + userProvider, e);
				}
			}

			if (!letterIndex.isEmpty()) {

				return letterIndex;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<User> getUsers(UserField sortingField, Order order, boolean groups, boolean attributes) {

		r.lock();
		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.getUsers(sortingField, order, groups, attributes);

					if (users != null) {
						userList.addAll(users);
					}

				} catch (Exception e) {

					log.error("Error getting users from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, sortingField.getComparator(order));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<User> getUsers(UserField filteringField, char startsWith, Order order, boolean groups, boolean attributes) {

		r.lock();
		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.getUsers(filteringField, startsWith, order, groups, attributes);

					if (users != null) {
						userList.addAll(users);
					}

				} catch (Exception e) {

					log.error("Error getting users from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, filteringField.getComparator(order));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public boolean canAddUserClass(Class<? extends User> userClass) {

		return canAddUserClass(userClass, null);
	}

	public boolean canAddUserClass(Class<? extends User> userClass, DataSource dataSource) {

		r.lock();
		try {
			for (UserProvider userProvider : this.userProviders) {

				if (userProvider instanceof MutableUserProvider) {

					MutableUserProvider mutableUserProvider = (MutableUserProvider) userProvider;

					if (mutableUserProvider.canAddUserClass(userClass) && (dataSource == null || dataSource.equals(mutableUserProvider.getDataSource()))) {

						return true;
					}
				}
			}

			return false;
		} finally {
			r.unlock();
		}
	}

	public boolean canUpdate(User user) {

		r.lock();
		try {
			for (UserProvider userProvider : this.userProviders) {

				if (userProvider instanceof MutableUserProvider && userProvider.isProviderFor(user)) {

					return true;
				}
			}

			return false;
		} finally {
			r.unlock();
		}
	}

	public void addUser(User user) throws UnableToAddUserException {

		addUser(user, null);
	}

	public void addUser(User user, DataSource dataSource) throws UnableToAddUserException {

		r.lock();
		try {
			for (UserProvider userProvider : this.userProviders) {

				if (userProvider instanceof MutableUserProvider) {

					MutableUserProvider mutableUserProvider = (MutableUserProvider) userProvider;

					if (mutableUserProvider.canAddUserClass(user.getClass()) && (dataSource == null || dataSource.equals(userProvider.getDataSource()))) {

						try {
							((MutableUserProvider) userProvider).addUser(user);

							if (log.isDebugEnabled()) {

								log.debug("Added user " + user + " using user provider " + userProvider);
							}

							return;

						} catch (Exception e) {

							log.error("Error adding user " + user + " using user provider " + userProvider, e);
						}
					}
				}
			}

			if (dataSource == null) {

				throw new UnableToAddUserException("No suitable user provider found for user " + user.getClass());

			} else {

				throw new UnableToAddUserException("No suitable user provider found for user " + user.getClass() + " and datasource " + dataSource);
			}

		} finally {
			r.unlock();
		}
	}

	public void updateUser(User user, boolean encryptPassword, boolean updateGroups, boolean updateAttributes) throws UnableToUpdateUserException {

		r.lock();
		try {
			for (UserProvider userProvider : this.userProviders) {

				if (userProvider instanceof MutableUserProvider && userProvider.isProviderFor(user)) {

					try {
						((MutableUserProvider) userProvider).updateUser(user, encryptPassword, updateGroups, updateAttributes);

						if (log.isDebugEnabled()) {

							log.debug("Updated user " + user + " using user provider " + userProvider);
						}

						return;

					} catch (Exception e) {

						log.error("Error updating user " + user + " using user provider " + userProvider, e);
					}
				}
			}

			throw new UnableToUpdateUserException("No suitable user provider found for user " + user.getClass());

		} finally {
			r.unlock();
		}
	}

	public void deleteUser(User user) throws UnableToDeleteUserException {

		r.lock();
		try {
			for (UserProvider userProvider : this.userProviders) {

				if (userProvider instanceof MutableUserProvider && userProvider.isProviderFor(user)) {

					try {
						((MutableUserProvider) userProvider).deleteUser(user);

						if (log.isDebugEnabled()) {

							log.debug("Deleted user " + user + " using user provider " + userProvider);
						}

						return;

					} catch (Exception e) {

						log.error("Deleted user " + user + " using user provider " + userProvider, e);
					}
				}
			}

			throw new UnableToDeleteUserException("No suitable user provider found for user " + user.getClass());
		} finally {
			r.unlock();
		}
	}

	public int getUserProviderCount() {

		r.lock();
		try {

			return userProviders.size();

		} finally {
			r.unlock();
		}
	}

	public List<User> getUsersByGroup(Integer groupID, boolean groups, boolean attributes) {

		r.lock();
		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.getUsersByGroup(groupID, groups, attributes);

					if (users != null) {
						userList.addAll(users);
					}

				} catch (Exception e) {

					log.error("Error getting users by groupID " + groupID + " from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<User> getUsersByAttribute(String attributeName, String attributeValue, boolean groups, boolean attributes){

		r.lock();
		try {
			ArrayList<User> userList = new ArrayList<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.getUsersByAttribute(attributeName, attributeValue, groups, attributes);

					if (users != null) {
						userList.addAll(users);
					}

				} catch (Exception e) {

					log.error("Error getting users by attribute name " + attributeName + " with value " + attributeValue + " from user provider " + userProvider, e);
				}
			}

			if (!userList.isEmpty()) {

				if (userProviders.size() > 1) {

					Collections.sort(userList, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<User> getUsersByGroups(List<Integer> groupIDs, boolean attributes) {

		r.lock();
		try {

			Set<User> userSet = new HashSet<User>();

			for (UserProvider userProvider : this.userProviders) {

				List<? extends User> users;

				try {
					users = userProvider.getUsersByGroups(groupIDs, attributes);

					if (users != null) {
						userSet.addAll(users);
					}

				} catch (Exception e) {

					log.error("Error getting users by groupIDs " + StringUtils.toCommaSeparatedString(groupIDs) + " from user provider " + userProvider, e);
				}
			}

			if (!userSet.isEmpty()) {

				List<User> userList = new ArrayList<User>(userSet);

				if (userProviders.size() > 1) {

					Collections.sort(userList, UserField.FIRSTNAME.getComparator(Order.ASC));
				}

				return userList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}


	/**
	 *  Removes all user providers from the user handler.
	 */
	public void clear(){

		w.lock();
		try {

			Iterator<UserProvider> iterator = this.userProviders.iterator();

			while(iterator.hasNext()){

				log.info("Removing user provider " + iterator.next() + " from user handler");

				iterator.remove();
			}

		} finally {
			w.unlock();
		}
	}

	public boolean hasFormAddableUserTypes(){

		r.lock();
		try {

			for (UserProvider userProvider : this.userProviders) {

				if(!(userProvider instanceof UserFormProvider)){

					continue;
				}

				UserTypeDescriptor userTypeDescriptor  = ((UserFormProvider)userProvider).getAddableUserType();

				if(userTypeDescriptor != null){

					return true;
				}
			}

			return false;

		} finally {
			r.unlock();
		}
	}

	public List<UserTypeDescriptor> getFormAddableUserTypes(){

		r.lock();
		try {

			List<UserTypeDescriptor> userTypeDescriptors = null;

			for (UserProvider userProvider : this.userProviders) {

				if(!(userProvider instanceof UserFormProvider)){

					continue;
				}

				UserTypeDescriptor userTypeDescriptor  = ((UserFormProvider)userProvider).getAddableUserType();

				if(userTypeDescriptor != null){

					userTypeDescriptors = CollectionUtils.addAndInstantiateIfNeeded(userTypeDescriptors, userTypeDescriptor);
				}
			}

			if (userTypeDescriptors != null) {

				if(userTypeDescriptors.size() > 1){

					Collections.sort(userTypeDescriptors);
				}

				return userTypeDescriptors;
			}

			return null;

		} finally {
			r.unlock();
		}
	}

	public UserFormProvider getUserFormProvider(String userTypeID){

		r.lock();
		try {
			for (UserProvider userProvider : this.userProviders) {

				if (userProvider instanceof UserFormProvider) {

					UserTypeDescriptor userTypeDescriptor = ((UserFormProvider)userProvider).getAddableUserType();

					if(userTypeDescriptor != null && userTypeDescriptor.getUserTypeID().equals(userTypeID)){

						return ((UserFormProvider) userProvider);
					}
				}
			}

			return null;

		} finally {
			r.unlock();
		}
	}

	public UserFormProvider getUserFormProvider(User user) {

		r.lock();
		try {
			for (UserProvider userProvider : this.userProviders) {

				if (userProvider instanceof UserFormProvider && userProvider.isProviderFor(user)) {

					return (UserFormProvider)userProvider;
				}
			}

			return null;

		} finally {
			r.unlock();
		}
	}
}
