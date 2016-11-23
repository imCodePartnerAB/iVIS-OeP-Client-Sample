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

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.enums.HTTPProtocol;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.enums.EnumUtils;

public class ForegroundModuleDescriptorPopulator implements BeanResultSetPopulator<SimpleForegroundModuleDescriptor> {

	@Override
	public SimpleForegroundModuleDescriptor populate(ResultSet rs) throws SQLException {
		SimpleForegroundModuleDescriptor mb = new SimpleForegroundModuleDescriptor();

		mb.setModuleID(rs.getInt("moduleID"));
		mb.setClassname(rs.getString("classname"));
		mb.setName(rs.getString("name"));
		mb.setAlias(rs.getString("alias"));
		mb.setDescription(rs.getString("description"));
		mb.setXslPath(rs.getString("xslPath"));

		if(rs.getString("xslPathType") != null && EnumUtils.isEnum(PathType.class, rs.getString("xslPathType"))){
			mb.setXslPathType(PathType.valueOf(rs.getString("xslPathType")));
		}

		mb.setAnonymousAccess(rs.getBoolean("anonymousAccess"));
		mb.setAdminAccess(rs.getBoolean("adminAccess"));
		mb.setUserAccess(rs.getBoolean("userAccess"));
		mb.setEnabled(rs.getBoolean("enabled"));
		mb.setVisibleInMenu(rs.getBoolean("visibleInMenu"));

		Long sectionID = (Long)rs.getObject("sectionID");
		if(sectionID != null){
			mb.setSectionID(sectionID.intValue());
		}

		Long dataSourceID = (Long)rs.getObject("dataSourceID");
		if(dataSourceID != null){
			mb.setDataSourceID(dataSourceID.intValue());
		}

		mb.setStaticContentPackage(rs.getString("staticContentPackage"));

		if(rs.getString("requiredProtocol") != null && EnumUtils.isEnum(HTTPProtocol.class, rs.getString("requiredProtocol"))){
			mb.setRequiredProtocol(HTTPProtocol.valueOf(rs.getString("requiredProtocol")));
		}

		return mb;
	}

	//	public SimpleForegroundModuleDescriptor populate(Element element) {
	//
	//		SimpleForegroundModuleDescriptor mb = new SimpleForegroundModuleDescriptor();
	//
	//		XPathFactory  factory=XPathFactory.newInstance();
	//		XPath xPath=factory.newXPath();
	//
	//		try {
	//			mb.setAdminAccess(Boolean.parseBoolean(xPath.evaluate("/module/adminAccess",doc)));
	//			mb.setUserAccess(Boolean.parseBoolean(xPath.evaluate("/module/userAccess",doc)));
	//			mb.setAnonymousAccess(Boolean.parseBoolean(xPath.evaluate("/module/anonymousAccess",doc)));
	//			mb.setEnabled(Boolean.parseBoolean(xPath.evaluate("/module/enabled",doc)));
	//			mb.setVisibleInMenu(Boolean.parseBoolean(xPath.evaluate("/module/visibleInMenu",doc)));
	//
	//			mb.setAlias(xPath.evaluate("/module/alias",doc));
	//			mb.setClassname(xPath.evaluate("/module/classname",doc));
	//			mb.setDescription(xPath.evaluate("/module/description",doc));
	//			mb.setName(xPath.evaluate("/module/name",doc));
	//			mb.setXslPath(xPath.evaluate("/module/xslPath",doc));
	//			mb.setStaticContentPackage(xPath.evaluate("/module/staticContentDirectory", doc));
	//
	//			if(EnumUtils.isEnum(PathType.class, xPath.evaluate("/module/xslPathType",doc))){
	//				mb.setXslPathType(PathType.valueOf(xPath.evaluate("/module/xslPathType",doc)));
	//			}
	//
	//		} catch (XPathExpressionException e) {}
	//
	//		return mb;
	//	}
}
