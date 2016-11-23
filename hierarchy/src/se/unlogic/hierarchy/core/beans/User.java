/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.hierarchy.core.servlets.CoreServlet;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.i18n.Language;
import se.unlogic.standardutils.string.StringTag;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.SessionUtils;

public abstract class User implements Serializable, HttpSessionBindingListener, Elementable {

	// Nested class to keep track of logged in users in a protected fashion
	private final static class UserSingleton implements Serializable {

		private static final long serialVersionUID = 6039421779820047064L;

		private static UserSingleton userSingleton;

		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private final Lock r = readWriteLock.readLock();
		private final Lock w = readWriteLock.writeLock();

		private final ArrayList<User> loggedInUsers = new ArrayList<User>();

		private UserSingleton() {

		};

		public static synchronized UserSingleton getInstance() {

			if (userSingleton == null) {
				userSingleton = new UserSingleton();
			}

			return userSingleton;
		}

		private Object readResolve() {

			if (userSingleton == null) {
				userSingleton = this;
			}

			return userSingleton;
		}

		private void addUser(User user) {

			try {
				w.lock();
				this.loggedInUsers.add(user);
			} finally {
				w.unlock();
			}
		}

		private void removeUser(User user) {

			try {
				w.lock();
				this.loggedInUsers.remove(user);
			} finally {
				w.unlock();
			}
		}

		public ArrayList<User> getUsers() {

			try {
				r.lock();
				return new ArrayList<User>(this.loggedInUsers);
			} finally {
				r.unlock();
			}
		}
	}

	private static final long serialVersionUID = 3517714501615304468L;

	private final UserSingleton userSingleton = UserSingleton.getInstance();
	private HttpSession session;

	public abstract boolean isAdmin();

	@StringTag(name = "email")
	public abstract String getEmail();

	@StringTag(name = "firstname")
	public abstract String getFirstname();

	public abstract Timestamp getCurrentLogin();

	public abstract Timestamp getLastLogin();

	@StringTag(name = "lastname")
	public abstract String getLastname();

	public abstract String getPassword();

	@StringTag(name = "username")
	public abstract String getUsername();

	@StringTag(name = "userID")
	public abstract Integer getUserID();

	public abstract boolean isEnabled();

	public abstract Timestamp getAdded();

	public abstract Collection<Group> getGroups();

	public abstract Language getLanguage();

	public abstract String getPreferedDesign();

	public abstract boolean hasFormProvider();

	@Override
	public String toString() {

		if(this.getUserID() != null){
			
			return this.getFirstname() + " " + this.getLastname() + " (ID: " + this.getUserID() + ")";
			
		}else{
			
			return this.getFirstname() + " " + this.getLastname() + " (" + this.getUsername() + ")";			
		}
	}

	@Override
	public final Element toXML(Document doc) {

		// Insert the root element node
		Element userElement = doc.createElement("user");

		if (this.getUserID() != null) {
			userElement.appendChild(XMLUtils.createElement("userID", this.getUserID().toString(), doc));
		}

		// Add username
		if (this.getUsername() != null) {
			userElement.appendChild(XMLUtils.createElement("username", getUsername(), doc));
		}

		// Add firstname
		if (this.getFirstname() != null) {
			userElement.appendChild(XMLUtils.createElement("firstname", getFirstname(), doc));
		}

		// Add lastname
		if (this.getLastname() != null) {
			userElement.appendChild(XMLUtils.createElement("lastname", getLastname(), doc));
		}

		// Add email
		if (this.getEmail() != null) {
			userElement.appendChild(XMLUtils.createElement("email", getEmail(), doc));
		}

		// Add language
		if (this.getLanguage() != null) {
			userElement.appendChild(this.getLanguage().toXML(doc));
		}

		userElement.appendChild(XMLUtils.createElement("admin", Boolean.toString(this.isAdmin()), doc));
		userElement.appendChild(XMLUtils.createElement("enabled", Boolean.toString(this.isEnabled()), doc));

		// Add lastlogin
		if (this.getLastLogin() != null) {
			userElement.appendChild(XMLUtils.createElement("lastLogin", DateUtils.DATE_TIME_FORMATTER.format(this.getLastLogin()), doc));
			userElement.appendChild(XMLUtils.createElement("lastLoginInMilliseconds", this.getLastLogin().getTime() + "", doc));
		}

		// Add currentlogin
		if (this.getCurrentLogin() != null) {
			userElement.appendChild(XMLUtils.createElement("currentLogin", DateUtils.DATE_TIME_FORMATTER.format(this.getCurrentLogin()), doc));
			userElement.appendChild(XMLUtils.createElement("currentLoginInMilliseconds", this.getCurrentLogin().getTime() + "", doc));
		}

		if (this.getAdded() != null) {
			userElement.appendChild(XMLUtils.createElement("added", DateUtils.DATE_TIME_FORMATTER.format(this.getAdded()), doc));
		}

		if (this.session != null) {

			Element sessionInfo = SessionUtils.getSessionInfoAsXML(session, doc);

			// Protection against session invalidation
			if (sessionInfo != null) {
				userElement.appendChild(sessionInfo);
			}
		}

		XMLUtils.appendNewElement(doc, userElement, "isMutable", this instanceof MutableUser);
		XMLUtils.appendNewElement(doc, userElement, "hasFormProvider", hasFormProvider());

		XMLUtils.append(doc, userElement, "groups", this.getGroups());

		List<Element> additionalXML = this.getAdditionalXML(doc);

		if (!CollectionUtils.isEmpty(additionalXML)) {

			for (Element element : additionalXML) {

				userElement.appendChild(element);
			}
		}

		return userElement;
	}

	protected List<Element> getAdditionalXML(Document doc) {

		return null;
	}

	public HttpSession getSession() {

		return session;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent sessionBindingEvent) {

		if (sessionBindingEvent.getName().equals("user")) {
			this.session = sessionBindingEvent.getSession();
			this.userSingleton.addUser(this);
		}
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent sessionBindingEvent) {

		if (LogManager.exists(CoreServlet.class.getName()) != null) {

			try {
				sessionBindingEvent.getSession().isNew();

			} catch (IllegalStateException e) {

				Logger.getLogger(this.getClass()).info("User " + this + " logged out by session timeout");
			}
		}

		if (sessionBindingEvent.getName().equals("user")) {
			this.userSingleton.removeUser(this);
			this.session = null;
		}
	}

	public static ArrayList<User> getLoggedInUsers() {

		return UserSingleton.getInstance().getUsers();
	}

	protected final Object writeReplace() {

		if (!(this.session instanceof Serializable)) {
			this.session = null;
		}

		return this;
	}

	@Override
	public int hashCode() {

		Integer userID = this.getUserID();

		final int prime = 31;
		int result = 1;
		result = prime * result + ((userID == null) ? super.hashCode() : userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		Integer userID = this.getUserID();

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;

		Integer otherUserID = other.getUserID();

		if (userID == null) {
			if (otherUserID != null) {
				return false;
			}
		} else if (!userID.equals(otherUserID)) {
			return false;
		}
		return true;
	}

	public abstract void setCurrentLogin(Timestamp currentLogin);


	/**
	 * @return A {@link AttributeHandler} or null if the current implementation does not support this feature.
	 */
	public AttributeHandler getAttributeHandler(){

		return null;
	}
}
