/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.invitation.cruds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.handlers.GroupHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.hierarchy.foregroundmodules.invitation.SimpleInvitationAdminModule;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.SimpleInvitationType;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

public class SimpleInvitationTypeCRUD extends IntegerBasedCRUD<SimpleInvitationType,SimpleInvitationAdminModule> {

	public static final AnnotatedRequestPopulator<SimpleInvitationType> POPULATOR = new AnnotatedRequestPopulator<SimpleInvitationType>(SimpleInvitationType.class);

	private final GroupHandler groupHandler;

	public SimpleInvitationTypeCRUD(CRUDDAO<SimpleInvitationType, Integer> crudDAO, SimpleInvitationAdminModule callback, GroupHandler groupHandler) {

		super(crudDAO, POPULATOR, "InvitationType", "invitation type", "", callback);

		this.groupHandler = groupHandler;
	}

	@Override
	protected void appendAddFormData(Document doc, Element addTypeElement, User user, HttpServletRequest req, URIParser uriParser) {

		Element groups = doc.createElement("Groups");
		addTypeElement.appendChild(groups);

		for (Group group : groupHandler.getGroups(false)) {
			groups.appendChild(group.toXML(doc));
		}

	}

	@Override
	protected void appendUpdateFormData(SimpleInvitationType invitationType, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) {

		this.appendAddFormData(doc, updateTypeElement, user, req, uriParser);
	}

	@Override
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		return callback.list(req, res, user, uriParser, validationErrors);
	}

	@Override
	protected void validateAddPopulation(SimpleInvitationType invitationType, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		String[] groupStringIDs = req.getParameterValues("group");

		if (groupStringIDs != null) {

			ArrayList<Integer> groupIDs = NumberUtils.toInt(groupStringIDs);

			if (groupIDs != null) {

				invitationType.setGroupIDs(groupIDs);
			}
		}
	}

	@Override
	protected void validateUpdatePopulation(SimpleInvitationType invitationType, HttpServletRequest req, User user, URIParser uriParser) throws ValidationException, SQLException, Exception {

		validateAddPopulation(invitationType,req, user, uriParser);
	}
}
