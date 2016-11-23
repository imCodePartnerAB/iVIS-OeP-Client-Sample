/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.populators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.VirtualMenuItem;
import se.unlogic.hierarchy.core.enums.MenuItemType;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.validation.ValidationUtils;

public class VirtualMenuItemPopulator implements BeanRequestPopulator<VirtualMenuItem>,BeanResultSetPopulator<VirtualMenuItem> {

	@Override
	public VirtualMenuItem populate(HttpServletRequest req) throws ValidationException {

		return populate(new VirtualMenuItem(),req);
	}

	@Override
	public VirtualMenuItem populate(VirtualMenuItem virtualMenuItem, HttpServletRequest req) throws ValidationException {

		ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

		String itemtype = req.getParameter("itemtype");

		if(StringUtils.isEmpty(itemtype)){
			
			validationErrors.add(new ValidationError("itemtype",ValidationErrorType.RequiredField));
			
		}else{

			MenuItemType menuItemType = EnumUtils.toEnum(MenuItemType.class, itemtype);

			if(menuItemType == null){
				
				validationErrors.add(new ValidationError("itemtype",ValidationErrorType.InvalidFormat));
				
			}else{
				
				virtualMenuItem.setItemType(menuItemType);
			}
		}

		if(virtualMenuItem.getItemType() != null && !virtualMenuItem.getItemType().equals(MenuItemType.BLANK)){

			virtualMenuItem.setName(ValidationUtils.validateParameter("name", req, true, 1, 45, validationErrors));
			virtualMenuItem.setDescription(ValidationUtils.validateParameter("description", req, true, 1, 65536, validationErrors));
			virtualMenuItem.setUrl(ValidationUtils.validateParameter("url", req, false, 1, 65536, validationErrors));

		}else{

			virtualMenuItem.setName(null);
			virtualMenuItem.setDescription(null);
			virtualMenuItem.setUrl(null);
		}

		virtualMenuItem.setAnonymousAccess(req.getParameter("anonymousAccess") != null);
		virtualMenuItem.setUserAccess(req.getParameter("userAccess") != null);
		virtualMenuItem.setAdminAccess(req.getParameter("adminAccess") != null);

		if(validationErrors.isEmpty()){

			virtualMenuItem.setAllowedUserIDs(NumberUtils.toInt(req.getParameterValues("user")));
			virtualMenuItem.setAllowedGroupIDs(NumberUtils.toInt(req.getParameterValues("group")));

			return virtualMenuItem;
			
		}else{
			
			throw new ValidationException(validationErrors);
		}
	}

	@Override
	public VirtualMenuItem populate(ResultSet rs) throws SQLException {

		VirtualMenuItem virtualMenuItem = new VirtualMenuItem();

		virtualMenuItem.setMenuItemID(rs.getInt("menuItemID"));
		virtualMenuItem.setItemType(MenuItemType.valueOf(rs.getString("itemtype")));
		virtualMenuItem.setName(rs.getString("name"));
		virtualMenuItem.setDescription(rs.getString("description"));
		virtualMenuItem.setUrl(rs.getString("url"));
		virtualMenuItem.setAnonymousAccess(rs.getBoolean("anonymousAccess"));
		virtualMenuItem.setUserAccess(rs.getBoolean("userAccess"));
		virtualMenuItem.setAdminAccess(rs.getBoolean("adminAccess"));
		virtualMenuItem.setSectionID(rs.getInt("sectionID"));

		return virtualMenuItem;
	}
}
