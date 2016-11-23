/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.pagemodules;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.validation.ValidationUtils;

public class PagePopulator implements BeanResultSetPopulator<Page>, BeanRequestPopulator<Page> {

	//TODO replace with Annotated dito...
	
	@Override
	public Page populate(ResultSet rs) throws SQLException {

		Page page = new Page();

		page.setPageID(rs.getInt("pageID"));
		page.setAdminAccess(rs.getBoolean("adminAccess"));
		page.setAnonymousAccess(rs.getBoolean("anonymousAccess"));
		page.setDescription(rs.getString("description"));
		page.setEnabled(rs.getBoolean("enabled"));
		page.setName(rs.getString("name"));
		page.setAlias(rs.getString("alias"));
		page.setText(rs.getString("text"));
		page.setUserAccess(rs.getBoolean("userAccess"));
		page.setVisibleInMenu(rs.getBoolean("visibleInMenu"));
		page.setBreadCrumb(rs.getBoolean("breadCrumb"));

		Long sectionID = (Long) rs.getObject("sectionID");
		if (sectionID != null) {
			page.setSectionID(sectionID.intValue());
		}

		return page;
	}

	@Override
	public Page populate(Page page, HttpServletRequest req) throws ValidationException {

		ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

		String alias = ValidationUtils.validateNotEmptyParameter("alias", req, validationErrors);
		String name = ValidationUtils.validateNotEmptyParameter("name", req, validationErrors);
		String description = ValidationUtils.validateNotEmptyParameter("description", req, validationErrors);
		String text = ValidationUtils.validateNotEmptyParameter("text", req, validationErrors);

		if (!validationErrors.isEmpty()) {
			throw new ValidationException(validationErrors);
		} else {
			page.setAlias(alias);
			page.setName(name);
			page.setDescription(description);
			page.setText(text);

			page.setAdminAccess(req.getParameter("adminAccess") != null);
			page.setAnonymousAccess(req.getParameter("anonymousAccess") != null);
			page.setUserAccess(req.getParameter("userAccess") != null);
			page.setVisibleInMenu(req.getParameter("visibleInMenu") != null);
			page.setEnabled(req.getParameter("enabled") != null);
			page.setBreadCrumb(req.getParameter("breadCrumb") != null);

			String[] allowedUserIDs = req.getParameterValues("user");
			ArrayList<Integer> userIDs = null;

			if (allowedUserIDs != null) {
				userIDs = NumberUtils.toInt(allowedUserIDs);
			}

			page.setAllowedUserIDs(userIDs);

			String[] allowedGroupIDs = req.getParameterValues("group");
			ArrayList<Integer> groupIDs = null;

			if (allowedGroupIDs != null) {
				groupIDs = NumberUtils.toInt(allowedGroupIDs);
			}

			page.setAllowedGroupIDs(groupIDs);

			return page;
		}
	}

	@Override
	public Page populate(HttpServletRequest req) throws ValidationException {

		Page page = new Page();

		return this.populate(page, req);
	}
	/*
	public static Page populate(Document doc) throws XPathExpressionException, InvalidPageXMLException{
		Page page = new Page();

		XPathFactory  factory=XPathFactory.newInstance();
		XPath xPath=factory.newXPath();

		page.setName(xPath.evaluate("/page/name", doc));
		page.setDescription(xPath.evaluate("/page/description", doc));
		page.setText(xPath.evaluate("/page/text", doc));
		page.setAlias(xPath.evaluate("/page/alias", doc));
		page.setAdminAccess(Boolean.parseBoolean(xPath.evaluate("/page/adminAccess", doc)));
		page.setUserAccess(Boolean.parseBoolean(xPath.evaluate("/page/userAccess", doc)));
		page.setAnonymousAccess(Boolean.parseBoolean(xPath.evaluate("/page/anonymousAccess", doc)));
		page.setVisibleInMenu(Boolean.parseBoolean(xPath.evaluate("/page/visibleInMenu", doc)));
		page.setEnabled(Boolean.parseBoolean(xPath.evaluate("/page/enabled", doc)));

		if(!page.isValid()){
			throw new InvalidPageXMLException();
		}

		return page;
	}
	*/
}
