/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.SimpleAccessInterface;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.VisibleModuleDescriptor;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class AccessUtils {

	public static boolean checkAccess(User user, AccessInterface access) {

		if (user == null) {
			return access.allowsAnonymousAccess();
		} else if (access.allowsUserAccess() || (user.isAdmin() && access.allowsAdminAccess())) {
			return true;
		} else if (access.getAllowedUserIDs() != null && !access.getAllowedUserIDs().isEmpty() && access.getAllowedUserIDs().contains(user.getUserID())) {
			return true;
		} else if (access.getAllowedGroupIDs() != null && !access.getAllowedGroupIDs().isEmpty() && user.getGroups() != null && !user.getGroups().isEmpty()) {

			for (Group group : user.getGroups()) {

				if (group.isEnabled() && access.getAllowedGroupIDs().contains(group.getGroupID())) {
					return true;
				}
			}
		}

		return false;
	}

	@Deprecated
	public static void appendGroupsAndUsers(Document doc, Element targetElement, UserHandler userHandler, GroupHandler groupHandler) {

		Element usersElement = doc.createElement("users");
		targetElement.appendChild(usersElement);

		List<User> users = userHandler.getUsers(false, false);

		if (users != null) {

			for (User user : users) {

				usersElement.appendChild(user.toXML(doc));
			}
		}

		Element groupsElement = doc.createElement("groups");
		targetElement.appendChild(groupsElement);

		List<Group> groups = groupHandler.getGroups(false);

		if (groups != null) {

			for (Group group : groups) {
				groupsElement.appendChild(group.toXML(doc));
			}
		}
	}

	public static void appendAllowedGroupsAndUsers(Document doc, Element targetElement, AccessInterface accessInterface, UserHandler userHandler, GroupHandler groupHandler) {

		Collection<Integer> userIDs = accessInterface.getAllowedUserIDs();

		if (userIDs != null) {

			XMLUtils.append(doc, targetElement, "users", userHandler.getUsers(userIDs, false, true));
		}

		Collection<Integer> groupIDs = accessInterface.getAllowedGroupIDs();

		if (groupIDs != null) {

			XMLUtils.append(doc, targetElement, "groups", groupHandler.getGroups(groupIDs, false));
		}
	}

	public static void appendAllowedGroupAndUserIDs(Document doc, Element targetElement, AccessInterface accessInterface) {

		if (accessInterface.getAllowedGroupIDs() != null && !accessInterface.getAllowedGroupIDs().isEmpty()) {

			Element allowedGroupsElement = doc.createElement("allowedGroupIDs");

			for (Integer groupID : accessInterface.getAllowedGroupIDs()) {
				allowedGroupsElement.appendChild(XMLUtils.createElement("groupID", groupID.toString(), doc));
			}

			targetElement.appendChild(allowedGroupsElement);
		}

		if (accessInterface.getAllowedUserIDs() != null && !accessInterface.getAllowedUserIDs().isEmpty()) {

			Element allowedUsersElement = doc.createElement("allowedUserIDs");

			for (Integer userID : accessInterface.getAllowedUserIDs()) {
				allowedUsersElement.appendChild(XMLUtils.createElement("userID", userID.toString(), doc));
			}

			targetElement.appendChild(allowedUsersElement);
		}
	}

	public static boolean checkRecursiveModuleAccess(User user, VisibleModuleDescriptor moduleDescriptor, SystemInterface systemInterface) {

		if (!AccessUtils.checkAccess(user, moduleDescriptor)) {

			return false;
		}

		SectionInterface sectionInterface = systemInterface.getSectionInterface(moduleDescriptor.getSectionID());

		if (sectionInterface == null) {

			return false;

		} else {

			while(sectionInterface != null) {

				if (!AccessUtils.checkAccess(user, sectionInterface.getSectionDescriptor())) {

					return false;
				}

				sectionInterface = sectionInterface.getParentSectionInterface();
			}
		}

		return true;
	}

	public static boolean checkRecursiveModuleAccess(User user, SectionInterface sectionInterface) {

		if (sectionInterface == null) {

			return false;

		} else {

			while(sectionInterface != null) {

				if (!AccessUtils.checkAccess(user, sectionInterface.getSectionDescriptor())) {

					return false;
				}

				sectionInterface = sectionInterface.getParentSectionInterface();
			}
		}

		return true;
	}

	public static boolean checkRecursiveModuleItemAccess(User user, VisibleModuleDescriptor moduleDescriptor, AccessInterface accessInterface, SystemInterface systemInterface) {

		if (!AccessUtils.checkAccess(user, accessInterface)) {

			return false;
		}

		return checkRecursiveModuleAccess(user, moduleDescriptor, systemInterface);
	}

	public static boolean checkAccess(User user, boolean permissive, AccessInterface... access) {

		if (permissive) {
			for (AccessInterface accessInterface : access) {
				if (AccessUtils.checkAccess(user, accessInterface)) {
					return true;
				}
			}
			return false;
		} else {
			for (AccessInterface accessInterface : access) {
				if (!AccessUtils.checkAccess(user, accessInterface)) {
					return false;
				}
			}
			return true;
		}
	}

	public static AccessInterface combine(boolean permissive, AccessInterface... accessInterfaces) {

		return combine(permissive, Arrays.asList(accessInterfaces));
	}

	public static AccessInterface combine(boolean permissive, Collection<AccessInterface> accessInterfaces) {

		if (permissive) {
			Set<Integer> allowedGroupIDs = new HashSet<Integer>();
			Set<Integer> allowedUserIDs = new HashSet<Integer>();
			boolean adminAccess = false, anonymousAccess = false, userAccess = false;

			for (AccessInterface accessInterface : accessInterfaces) {

				if (accessInterface.allowsAdminAccess()) {
					adminAccess = true;
				}

				if (accessInterface.allowsAnonymousAccess()) {
					anonymousAccess = true;
				}

				if (accessInterface.allowsUserAccess()) {
					userAccess = true;
				}

				if (accessInterface.getAllowedGroupIDs() != null) {
					allowedGroupIDs.addAll(accessInterface.getAllowedGroupIDs());
				}

				if (accessInterface.getAllowedUserIDs() != null) {
					allowedUserIDs.addAll(accessInterface.getAllowedUserIDs());
				}
			}

			return new SimpleAccessInterface(adminAccess, anonymousAccess, userAccess, allowedGroupIDs, allowedUserIDs);

		} else {

			List<Integer> allowedGroupIDs = new ArrayList<Integer>();
			List<Integer> allowedUserIDs = new ArrayList<Integer>();

			boolean adminAccess = true, anonymousAccess = true, userAccess = true;

			for (AccessInterface accessInterface : accessInterfaces) {

				if (!accessInterface.allowsAdminAccess()) {
					adminAccess = false;
				}

				if (!accessInterface.allowsAnonymousAccess()) {
					anonymousAccess = false;
				}

				if (!accessInterface.allowsUserAccess()) {
					userAccess = false;
				}

				allowedGroupIDs = CollectionUtils.conjunction(allowedGroupIDs, accessInterface.getAllowedGroupIDs());
				allowedUserIDs = CollectionUtils.conjunction(allowedUserIDs, accessInterface.getAllowedUserIDs());
			}

			return new SimpleAccessInterface(adminAccess, anonymousAccess, userAccess, allowedGroupIDs, allowedUserIDs);
		}
	}

	public static boolean isEmpty(AccessInterface accessInterface) {

		return !accessInterface.allowsAnonymousAccess() && !accessInterface.allowsUserAccess() && !accessInterface.allowsAdminAccess() && CollectionUtils.isEmpty(accessInterface.getAllowedGroupIDs()) && CollectionUtils.isEmpty(accessInterface.getAllowedUserIDs());
	}
}