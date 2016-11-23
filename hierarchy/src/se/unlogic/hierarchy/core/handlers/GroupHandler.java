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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.comparators.PriorityComparator;
import se.unlogic.hierarchy.core.exceptions.UnableToAddGroupException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteGroupException;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateGroupException;
import se.unlogic.hierarchy.core.interfaces.GroupProvider;
import se.unlogic.hierarchy.core.interfaces.MutableGroupProvider;
import se.unlogic.standardutils.collections.ExternalMethodComparator;
import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.string.StringUtils;

public class GroupHandler {

	private static final Comparator<Group> ASC_GROUP_COMPARATOR = new ExternalMethodComparator<Group, String>(Group.class, String.class, "getName", Order.ASC, String.CASE_INSENSITIVE_ORDER);
	private static final Comparator<Group> DESC_GROUP_COMPARATOR = new ExternalMethodComparator<Group, String>(Group.class, String.class, "getName", Order.DESC, String.CASE_INSENSITIVE_ORDER);

	private static final PriorityComparator PRIORITY_COMPARATOR = new PriorityComparator(Order.ASC);

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	private final Logger log = Logger.getLogger(this.getClass());

	private final ArrayList<GroupProvider> groupProviders = new ArrayList<GroupProvider>();

	public static Comparator<Group> getGroupComparator(Order order) {

		if (order == Order.ASC) {

			return ASC_GROUP_COMPARATOR;
		}

		return DESC_GROUP_COMPARATOR;
	}

	public boolean addProvider(GroupProvider groupProvider) {

		if(groupProvider == null){

			return false;
		}

		w.lock();
		try {

			if (!groupProviders.contains(groupProvider)) {

				groupProviders.add(groupProvider);

				Collections.sort(groupProviders, PRIORITY_COMPARATOR);

				return true;
			}

			return false;

		} finally {
			w.unlock();
		}
	}

	public ArrayList<GroupProvider> getGroupProviders() {

		r.lock();
		try {
			return new ArrayList<GroupProvider>(this.groupProviders);
		} finally {
			r.unlock();
		}
	}

	public boolean removeProvider(GroupProvider groupProvider) {

		w.lock();
		try {

			return groupProviders.remove(groupProvider);

		} finally {
			w.unlock();
		}
	}

	public void sortProviders(){

		w.lock();
		try {

			Collections.sort(groupProviders, PRIORITY_COMPARATOR);

		} finally {
			w.unlock();
		}
	}


	public boolean contains(GroupProvider groupProvider) {

		r.lock();
		try {
			return groupProviders.contains(groupProvider);
		} finally {
			r.unlock();
		}
	}

	public boolean containsAll(Collection<GroupProvider> groupProviderList) {

		r.lock();
		try {
			return groupProviders.containsAll(groupProviderList);
		} finally {
			r.unlock();
		}
	}

	public boolean isEmpty() {

		r.lock();
		try {
			return groupProviders.isEmpty();
		} finally {
			r.unlock();
		}
	}

	public int size() {

		r.lock();
		try {
			return groupProviders.size();
		} finally {
			r.unlock();
		}
	}

	public ArrayList<Group> getGroups(boolean attributes) {

		r.lock();
		try {
			ArrayList<Group> groupList = new ArrayList<Group>();

			for (GroupProvider groupProvider : this.groupProviders) {

				Collection<? extends Group> groups;
				try {
					groups = groupProvider.getGroups(attributes);

					if (groups != null) {
						groupList.addAll(groups);
					}

				} catch (Exception e) {

					log.error("Error getting groups from group provider " + groupProvider, e);
				}
			}

			if (!groupList.isEmpty()) {

				if (groupProviders.size() > 1) {

					Collections.sort(groupList, ASC_GROUP_COMPARATOR);
				}

				return groupList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<Group> searchGroups(String query, boolean attributes, Integer maxHits) {

		r.lock();
		try {
			ArrayList<Group> groupList = new ArrayList<Group>();

			for (GroupProvider groupProvider : this.groupProviders) {

				Collection<? extends Group> groups;
				try {
					groups = groupProvider.searchGroups(query, attributes, maxHits);

					if (groups != null) {
						groupList.addAll(groups);
					}

				} catch (Exception e) {

					log.error("Error getting groups using search query " + query + " from group provider " + groupProvider, e);
				}
			}

			if (!groupList.isEmpty()) {

				if (groupProviders.size() > 1) {

					Collections.sort(groupList, ASC_GROUP_COMPARATOR);
				}

				if(maxHits != null && groupList.size() > maxHits){

					return groupList.subList(0, maxHits);
				}

				return groupList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<Group> searchGroupsWithAttribute(String query, boolean attributes, String attributeName, Integer maxHits) {

		r.lock();
		try {
			ArrayList<Group> groupList = new ArrayList<Group>();

			for (GroupProvider groupProvider : this.groupProviders) {

				Collection<? extends Group> groups;
				try {
					groups = groupProvider.searchGroupsWithAttribute(query, attributes, attributeName, maxHits);

					if (groups != null) {
						groupList.addAll(groups);
					}

				} catch (Exception e) {

					log.error("Error getting groups using search query " + query + " and attribute " + attributeName + " from group provider " + groupProvider, e);
				}
			}

			if (!groupList.isEmpty()) {

				if (groupProviders.size() > 1) {

					Collections.sort(groupList, ASC_GROUP_COMPARATOR);
				}

				if(maxHits != null && groupList.size() > maxHits){

					return groupList.subList(0, maxHits);
				}

				return groupList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public Group getGroup(Integer groupID, boolean attributes) {

		r.lock();
		try {
			Group group = null;

			for (GroupProvider groupProvider : this.groupProviders) {

				try {
					group = groupProvider.getGroup(groupID, attributes);

					if (group != null) {

						if (log.isDebugEnabled()) {
							log.debug("found group " + group + " in group provider " + groupProvider);
						}

						return group;
					}

				} catch (Exception e) {

					log.error("Error getting group with groupID " + groupID + " from group provider " + groupProvider, e);
				}
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public List<Group> getGroups(Collection<Integer> groupIDs, boolean attributes) {

		r.lock();
		try {
			ArrayList<Integer> groupsLeft = new ArrayList<Integer>(groupIDs);

			ArrayList<Group> groups = new ArrayList<Group>();

			int position = 1;

			for (GroupProvider groupProvider : this.groupProviders) {

				try {
					List<? extends Group> providerGroups = groupProvider.getGroups(groupsLeft, attributes);

					if (providerGroups != null) {

						groups.addAll(providerGroups);

						if (position < groupProviders.size()) {

							for (Group group : providerGroups) {

								groupsLeft.remove(group.getGroupID());

								if (groupsLeft.isEmpty()) {

									return groups;
								}
							}
						}
					}

				} catch (Exception e) {

					log.error("Error getting groups with groupID in " + StringUtils.toCommaSeparatedString(groupsLeft) + " from group provider " + groupProvider, e);
				}

				position++;
			}

			if (groups.isEmpty()) {

				return null;

			} else {

				return groups;
			}

		} finally {
			r.unlock();
		}
	}

	public List<Group> getGroupsByAttribute(String attributeName, boolean attributes) {

		r.lock();
		try {

			ArrayList<Group> groups = new ArrayList<Group>();

			for (GroupProvider groupProvider : this.groupProviders) {

				try {

					List<? extends Group> providerGroups = groupProvider.getGroupsByAttribute(attributeName, attributes);

					if (providerGroups != null) {

						groups.addAll(providerGroups);

					}

				} catch (Exception e) {

					log.error("Error getting groups by attribute name " + attributeName + " from group provider " + groupProvider, e);
				}

			}

			if (groups.isEmpty()) {

				return null;

			} else {

				return groups;
			}

		} finally {
			r.unlock();
		}

	}

	public List<Group> getGroupsByAttribute(String attributeName, String attributeValue, boolean attributes) {

		r.lock();
		try {

			ArrayList<Group> groups = new ArrayList<Group>();

			for (GroupProvider groupProvider : this.groupProviders) {

				try {

					List<? extends Group> providerGroups = groupProvider.getGroupsByAttribute(attributeName, attributeValue, attributes);

					if (providerGroups != null) {

						groups.addAll(providerGroups);

					}

				} catch (Exception e) {

					log.error("Error getting groups by attribute name " + attributeName + " with value " + attributeValue + " from group provider " + groupProvider, e);
				}

			}

			if (groups.isEmpty()) {

				return null;

			} else {

				return groups;
			}

		} finally {
			r.unlock();
		}

	}

	public Group getGroupByAttribute(String attributeName, String attributeValue, boolean attributes) {

		r.lock();
		try {
			Group group = null;

			for (GroupProvider groupProvider : this.groupProviders) {

				try {

					group = groupProvider.getGroupByAttribute(attributeName, attributeValue, attributes);

					if (group != null) {

						return group;
					}

				} catch (Exception e) {

					log.error("Error getting group by attribute name " + attributeName + " with value " + attributeValue + " from group provider " + groupProvider, e);
				}
			}

			return null;

		} finally {
			r.unlock();
		}

	}

	public Group getGroupByAttributes(List<Entry<String, String>> attributeEntries, boolean attributes) {

		r.lock();
		try {
			Group group = null;

			for (GroupProvider groupProvider : this.groupProviders) {

				try {

					group = groupProvider.getGroupByAttributes(attributeEntries, attributes);

					if (group != null) {

						return group;
					}

				} catch (Exception e) {

					log.error("Error getting group by " + attributeEntries.size() + " attributes from group provider " + groupProvider, e);
				}
			}

			return null;

		} finally {
			r.unlock();
		}
	}

	public Integer getGroupCount() {

		r.lock();
		try {
			Integer groupCount = 0;

			for (GroupProvider groupProvider : this.groupProviders) {

				try {
					groupCount += groupProvider.getGroupCount();

				} catch (Exception e) {

					log.error("Error getting group count from group provider " + groupProvider, e);
				}
			}

			return groupCount;
		} finally {
			r.unlock();
		}
	}

	public Integer getDisabledGroupCount() {

		r.lock();
		try {
			Integer groupCount = 0;

			for (GroupProvider groupProvider : this.groupProviders) {

				try {
					groupCount += groupProvider.getDisabledGroupCount();

				} catch (Exception e) {

					log.error("Error getting disabled group count from group provider " + groupProvider, e);
				}
			}

			return groupCount;
		} finally {
			r.unlock();
		}
	}

	public List<Group> getGroups(Order order, char startsWith, boolean users, boolean attributes) {

		r.lock();
		try {
			ArrayList<Group> groupList = new ArrayList<Group>();

			for (GroupProvider groupProvider : this.groupProviders) {

				Collection<? extends Group> groups;

				try {
					groups = groupProvider.getGroups(order, startsWith, attributes);

					if (groups != null) {
						groupList.addAll(groups);
					}

				} catch (Exception e) {

					log.error("Error getting groups from group provider " + groupProvider, e);
				}
			}

			if (!groupList.isEmpty()) {

				if (groupProviders.size() > 1) {

					Collections.sort(groupList, getGroupComparator(order));
				}

				return groupList;
			}

			return null;
		} finally {
			r.unlock();
		}
	}

	public Set<Character> getGroupFirstLetterIndex() {

		r.lock();
		try {
			TreeSet<Character> letterIndex = new TreeSet<Character>();

			for (GroupProvider groupProvider : this.groupProviders) {

				try {
					List<Character> providerLetterIndex = groupProvider.getGroupFirstLetterIndex();

					if (providerLetterIndex != null) {

						letterIndex.addAll(providerLetterIndex);
					}

				} catch (Exception e) {

					log.error("Error getting group first letter index from group provider " + groupProvider, e);
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

	public boolean canAddGroupClass(Class<? extends Group> groupClass) {

		return canAddGroupClass(groupClass, null);
	}

	public boolean canAddGroupClass(Class<? extends Group> groupClass, DataSource dataSource) {

		r.lock();
		try {
			for (GroupProvider groupProvider : this.groupProviders) {

				if (groupProvider instanceof MutableGroupProvider) {

					MutableGroupProvider mutableGroupProvider = (MutableGroupProvider) groupProvider;

					if (mutableGroupProvider.canAddGroupClass(groupClass) && (dataSource == null || dataSource.equals(mutableGroupProvider.getDataSource()))) {

						return true;
					}
				}
			}

			return false;
		} finally {
			r.unlock();
		}
	}

	public boolean canUpdate(Group group) {

		r.lock();
		try {
			for (GroupProvider groupProvider : this.groupProviders) {

				if (groupProvider instanceof MutableGroupProvider && groupProvider.isProviderFor(group)) {

					return true;
				}
			}

			return false;
		} finally {
			r.unlock();
		}
	}


	public void addGroup(Group group) throws UnableToAddGroupException {

		addGroup(group, null);
	}

	public void addGroup(Group group, DataSource dataSource) throws UnableToAddGroupException {

		r.lock();
		try {
			for (GroupProvider groupProvider : this.groupProviders) {

				if (groupProvider instanceof MutableGroupProvider) {

					MutableGroupProvider mutableGroupProvider = (MutableGroupProvider) groupProvider;

					if (mutableGroupProvider.canAddGroupClass(group.getClass()) && (dataSource == null || dataSource.equals(mutableGroupProvider.getDataSource()))) {

						try {
							mutableGroupProvider.addGroup(group);

							if (log.isDebugEnabled()) {

								log.debug("Added group " + group + " using group provider " + mutableGroupProvider);
							}

							return;

						} catch (Exception e) {

							log.error("Error adding group " + group + " using group provider " + mutableGroupProvider, e);
						}
					}
				}
			}

			if (dataSource == null) {

				throw new UnableToAddGroupException("No suitable group provider found for group " + group.getClass());

			} else {

				throw new UnableToAddGroupException("No suitable group provider found for group " + group.getClass() + " and datasource " + dataSource);
			}

		} finally {
			r.unlock();
		}
	}

	public void updateGroup(Group group, boolean attributes) throws UnableToUpdateGroupException {

		r.lock();
		try {
			for (GroupProvider groupProvider : this.groupProviders) {

				if (groupProvider instanceof MutableGroupProvider && groupProvider.isProviderFor(group)) {

					try {
						((MutableGroupProvider) groupProvider).updateGroup(group, attributes);

						if (log.isDebugEnabled()) {

							log.debug("Updated group " + group + " using group provider " + groupProvider);
						}

						return;

					} catch (Exception e) {

						log.error("Error updating group " + group + " using group provider " + groupProvider, e);
					}
				}
			}

			throw new UnableToUpdateGroupException("No suitable group provider found for group " + group.getClass());
		} finally {
			r.unlock();
		}
	}

	public void deleteGroup(Group group) throws UnableToDeleteGroupException {

		r.lock();
		try {
			for (GroupProvider groupProvider : this.groupProviders) {

				if (groupProvider instanceof MutableGroupProvider && groupProvider.isProviderFor(group)) {

					try {
						((MutableGroupProvider) groupProvider).deleteGroup(group);

						if (log.isDebugEnabled()) {

							log.debug("Deleted group " + group + " using group provider " + groupProvider);
						}

						return;

					} catch (Exception e) {

						log.error("Deleted group " + group + " using group provider " + groupProvider, e);
					}
				}
			}

			throw new UnableToDeleteGroupException("No suitable group provider found for group " + group.getClass());
		} finally {
			r.unlock();
		}
	}


	/**
	 *  Removes all group providers from the group handler.
	 */
	public void clear(){

		w.lock();
		try {

			Iterator<GroupProvider> iterator = this.groupProviders.iterator();

			while(iterator.hasNext()){

				log.info("Removing group provider " + iterator.next() + " from group handler");

				iterator.remove();
			}

		} finally {
			w.unlock();
		}
	}
}
