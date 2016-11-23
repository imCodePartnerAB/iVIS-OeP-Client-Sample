/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;

public class IntegerBasedCRUD<BeanType extends Elementable, CallbackType extends CRUDCallback<User>> extends GenericCRUD<BeanType, Integer, User, CallbackType> {

	public IntegerBasedCRUD(CRUDDAO<BeanType, Integer> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeLogName, String listMethodAlias, CallbackType callback) {

		super(crudDAO, populator, typeElementName, typeLogName, listMethodAlias, callback);
	}

	public IntegerBasedCRUD(CRUDDAO<BeanType, Integer> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeElementPluralName, String typeLogName, String typeLogPluralName, String listMethodAlias, CallbackType callback) {

		super(crudDAO, populator, typeElementName, typeElementPluralName, typeLogName, typeLogPluralName, listMethodAlias, callback);
	}

	@Override
	public BeanType getRequestedBean(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, String getMode) throws SQLException, AccessDeniedException {

		Integer beanID = uriParser.getInt(2);
		
		if (uriParser.size() > 2 && beanID != null) {

			return getBean(beanID, getMode);
		}

		return null;
	}

	public BeanType getBean(Integer beanID, String getMode) throws SQLException, AccessDeniedException {

		return getBean(beanID);
	}

	public BeanType getBean(Integer beanID) throws SQLException, AccessDeniedException {

		return crudDAO.get(beanID);
	}
}
