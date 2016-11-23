/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.populators;

import java.sql.SQLException;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.standardutils.dao.querys.PreparedStatementQuery;
import se.unlogic.standardutils.populators.QueryParameterPopulator;


public class GroupQueryPopulator implements QueryParameterPopulator<Group> {

	public static final GroupQueryPopulator POPULATOR = new GroupQueryPopulator();

	@Override
	public Class<Group> getType() {

		return Group.class;
	}

	@Override
	public void populate(PreparedStatementQuery query, int paramIndex, Object bean) throws SQLException {

		if(bean == null){

			query.setObject(paramIndex, null);

		}else{

			Group group = (Group)bean;

			query.setInt(paramIndex, group.getGroupID());
		}
	}
}
