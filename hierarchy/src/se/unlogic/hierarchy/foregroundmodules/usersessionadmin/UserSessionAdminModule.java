/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.usersessionadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class UserSessionAdminModule extends AnnotatedForegroundModule {

	private static final UserNameComparator USERCOMPARATOR = new UserNameComparator();

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {

		log.info("User " + user + " listing logged in users");

		Document doc = this.createDocument(req, uriParser);

		Element loggedInUsers = doc.createElement("LoggedInUsers");
		doc.getFirstChild().appendChild(loggedInUsers);

		ArrayList<User> users = User.getLoggedInUsers();

		Collections.sort(users, USERCOMPARATOR);

		for (User loggedInUser : users) {
			loggedInUsers.appendChild(loggedInUser.toXML(doc));
		}

		return new SimpleForegroundModuleResponse(doc,this.moduleDescriptor.getName(),getDefaultBreadcrumb());
	}

	@WebPublic
	public SimpleForegroundModuleResponse logoutUser(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, URINotFoundException {

		if (uriParser.size() == 4) {

			Integer userID = NumberUtils.toInt(uriParser.get(2));

			if (userID != null) {

				ArrayList<User> users = User.getLoggedInUsers();

				User requestedUser = null;

				for (User loggedInUser : users) {

					if (loggedInUser.getUserID() != null && loggedInUser.getUserID().equals(userID)) {

						try {
							if (loggedInUser.getSession() != null && loggedInUser.getSession().getId().equals(uriParser.get(3))) {
								requestedUser = loggedInUser;
								break;
							}
						} catch (IllegalStateException e) {
							log.debug("Unable to access session for user " + loggedInUser);
						}
					}
				}

				if (requestedUser != null) {

					HttpSession session = requestedUser.getSession();

					if (session != null) {

						try {
							//This step is done in order to able to separate manual logins from sessions timeouts
							session.removeAttribute("user");
							session.invalidate();
							log.info("User " + requestedUser + " logged out by user " + user);
						} catch (IllegalStateException e) {
							log.debug("User " + requestedUser + " has already been logged out, " + e);
						}

					} else {
						log.debug("User " + requestedUser + " has already been logged out");
					}
				}

				this.redirectToDefaultMethod(req, res);
				return null;
			}
		}

		throw new URINotFoundException(uriParser);
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {
		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}
}
