/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.invitation;

import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.exceptions.UnableToAddUserException;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitation;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitationType;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public abstract class BaseInvitationModule<I extends BaseInvitation, IT extends BaseInvitationType, U extends MutableUser> extends AnnotatedForegroundModule {

	protected UserHandler userHandler;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);
		this.userHandler = systemInterface.getUserHandler();
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, UnableToAddUserException, ModuleConfigurationException {

		if (uriParser.size() != 3) {
			throw new URINotFoundException(uriParser);
		}

		Integer invitationID = NumberUtils.toInt(uriParser.get(1));

		UUID invitationLinkID = null;

		try {
			invitationLinkID = UUID.fromString(uriParser.get(2));
		} catch (IllegalArgumentException e) {}

		if (invitationID == null || invitationLinkID == null) {
			throw new URINotFoundException(uriParser);
		}

		I invitation = getInvitation(invitationID, invitationLinkID);

		if (invitation == null) {
			throw new URINotFoundException(uriParser);
		}

		IT invitationType = invitation.getInvitationType();

		ValidationException validationException = null;

		if (req.getMethod().equalsIgnoreCase("POST")) {
			try {
				U invitedUser = populateUser(req);

				if (userHandler.getUserByUsername(invitedUser.getUsername(), false, false) != null) {
					throw new ValidationException(Collections.singletonList(new ValidationError("UsernameAlreadyTaken")));
				}

				invitedUser.setFirstname(invitation.getFirstname());
				invitedUser.setLastname(invitation.getLastname());
				invitedUser.setEmail(invitation.getEmail());
				invitedUser.setEnabled(true);

				setUserGroups(invitedUser, invitation);

				log.info("User " + invitedUser + " registered using invitation " + invitation);

				this.userHandler.addUser(invitedUser);

				systemInterface.getEventHandler().sendEvent(invitation.getClass(), new RegisteredEvent<I, U>(invitation, invitedUser), EventTarget.ALL);
				
				this.userAdded(invitation, invitedUser);

				deleteInvitation(invitation);

				Document doc = this.createDocument(req, uriParser);
				Element registerElement = doc.createElement("Registered");
				doc.getFirstChild().appendChild(registerElement);

				XMLUtils.appendNewCDATAElement(doc, registerElement, "RegisteredText", invitationType.getRegisteredText());

				registerElement.appendChild(invitedUser.toXML(doc));

				return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());

			} catch (ValidationException e) {
				validationException = e;
			}
		}

		log.info("User " + user + " accessing invitation " + invitation + " from " + req.getRemoteAddr());

		Document doc = this.createDocument(req, uriParser);
		Element registerElement = doc.createElement("Register");
		doc.getFirstChild().appendChild(registerElement);

		registerElement.appendChild(invitation.toXML(doc));

		XMLUtils.appendNewCDATAElement(doc, registerElement, "RegistrationText", invitationType.getRegistrationText());

		if (validationException != null) {
			registerElement.appendChild(validationException.toXML(doc));
			registerElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	protected abstract void deleteInvitation(I invitation) throws SQLException;

	protected void setUserGroups(U invitedUser, I invitation){};

	protected abstract U populateUser(HttpServletRequest req) throws ValidationException;

	protected abstract I getInvitation(Integer invitationID, UUID invitationLinkID) throws SQLException;

	protected void userAdded(I invitation, U invitedUser) throws SQLException {}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	@Override
	public SimpleForegroundModuleResponse methodNotFound(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {
		return defaultMethod(req, res, user, uriParser);
	}
}
