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

import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.enums.EnumUtils;

public class SectionDescriptorPopulator implements BeanResultSetPopulator<SimpleSectionDescriptor> {

	@Override
	public SimpleSectionDescriptor populate(ResultSet rs) throws SQLException {

		SimpleSectionDescriptor simpleSectionDescriptor = new SimpleSectionDescriptor();

		simpleSectionDescriptor.setAdminAccess(rs.getBoolean("adminAccess"));
		simpleSectionDescriptor.setAlias(rs.getString("alias"));
		simpleSectionDescriptor.setAnonymousAccess(rs.getBoolean("anonymousAccess"));
		simpleSectionDescriptor.setAnonymousDefaultURI(rs.getString("anonymousDefaultURI"));
		simpleSectionDescriptor.setUserDefaultURI(rs.getString("userDefaultURI"));
		simpleSectionDescriptor.setDescription(rs.getString("description"));
		simpleSectionDescriptor.setEnabled(rs.getBoolean("enabled"));

		Long partentSectionID = (Long)rs.getObject("parentSectionID");
		if(partentSectionID != null){
			simpleSectionDescriptor.setParentSectionID(partentSectionID.intValue());
		}

		simpleSectionDescriptor.setSectionID(rs.getInt("sectionID"));
		simpleSectionDescriptor.setUserAccess(rs.getBoolean("userAccess"));
		simpleSectionDescriptor.setVisibleInMenu(rs.getBoolean("visibleInMenu"));
		simpleSectionDescriptor.setBreadCrumb(rs.getBoolean("breadcrumb"));
		simpleSectionDescriptor.setName(rs.getString("name"));
		
		if(rs.getString("requiredProtocol") != null && EnumUtils.isEnum(HTTPProtocol.class, rs.getString("requiredProtocol"))){
			simpleSectionDescriptor.setRequiredProtocol(HTTPProtocol.valueOf(rs.getString("requiredProtocol")));
		}

		return simpleSectionDescriptor;
	}
}
