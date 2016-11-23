/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.invitation.cruds;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.hierarchy.foregroundmodules.invitation.BaseInvitationAdminModule;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitation;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitationType;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;

public abstract class BaseInvitationCRUD<I extends BaseInvitation, IT extends BaseInvitationType, C extends BaseInvitationAdminModule<I, IT>> extends IntegerBasedCRUD<I,C> {

	public BaseInvitationCRUD(CRUDDAO<I, Integer> crudDAO, BeanRequestPopulator<I> populator, String typeElementName, String typeLogName, String listMethodAlias, C callback) {

		super(crudDAO, populator, typeElementName, typeLogName, listMethodAlias, callback);
	}

	@Override
	protected void appendAddFormData(Document doc, Element addTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws SQLException {
		XMLUtils.append(doc, addTypeElement, "InvitationTypes", callback.getInvitationTypes());
	}

	@Override
	protected void appendUpdateFormData(I invitation, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws SQLException {
		XMLUtils.append(doc, updateTypeElement, "InvitationTypes", callback.getInvitationTypes());
		XMLUtils.appendNewElement(doc, updateTypeElement, "invitationURL", callback.getInvitationURL(invitation, req));
	}

	@Override
	protected void validateAddPopulation(I invitation, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, AccessDeniedException {

		List<ValidationError> validationErrors = null;
		IT invitationType = null;

		if (!NumberUtils.isInt(req.getParameter("invitationType")) || (invitationType = callback.getInvitationType(NumberUtils.toInt(req.getParameter("invitationType")))) == null) {

			validationErrors = CollectionUtils.addAndInstantiateIfNeeded(validationErrors, new ValidationError("invitationType", ValidationErrorType.RequiredField));
		}


		if (callback.checkIfEmailInUse(invitation)) {
			validationErrors = CollectionUtils.addAndInstantiateIfNeeded(validationErrors, new ValidationError("EmailAlreadyTaken"));
		}

		if(validationErrors != null){

			throw new ValidationException(validationErrors);
		}

		if(invitation.getLinkID() == null){

			invitation.setLinkID(UUID.randomUUID());
		}

		setInvitationType(invitation, invitationType);
	}

	protected abstract void setInvitationType(I invitation, IT invitationType);

	@Override
	protected void validateUpdatePopulation(I invitation, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, AccessDeniedException {

		this.validateAddPopulation(invitation, req, user, uriParser);
	}

	@Override
	protected ForegroundModuleResponse beanAdded(I invitation, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (req.getParameter("send") != null) {
			try {
				this.callback.sendInvitation(invitation, user, req);
			} catch (SQLException e) {

				throw new RuntimeException();
			}
		}

		return super.beanAdded(invitation, req, res, user, uriParser);
	}

	@Override
	protected ForegroundModuleResponse beanUpdated(I invitation, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if (req.getParameter("send") != null) {
			this.callback.sendInvitation(invitation, user, req);
		}

		return super.beanUpdated(invitation, req, res, user, uriParser);
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		return callback.list(req, res, user, uriParser, validationErrors);
	}
}
