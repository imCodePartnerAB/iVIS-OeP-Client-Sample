/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.hddtemp;

import java.lang.reflect.Field;
import java.sql.SQLException;

import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.AnnotatedDAOWrapper;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.reflection.ReflectionUtils;


public class ServerDriveDAOWrapper extends AnnotatedDAOWrapper<ServerDrive,Integer> {

	private final Field SERVER_RELATION = ReflectionUtils.getField(ServerDrive.class, "server");
	
	public ServerDriveDAOWrapper(AnnotatedDAO<ServerDrive> annotatedDAO, String keyField) {

		super(annotatedDAO, keyField, Integer.class);
	}

	@Override
	public ServerDrive get(Integer beanID) throws SQLException {

		HighLevelQuery<ServerDrive> query = new HighLevelQuery<ServerDrive>();
		
		query.addParameter(parameterFactory.getParameter(beanID));
		
		query.addRelation(SERVER_RELATION);

		return this.annotatedDAO.get(query);
	}
}
