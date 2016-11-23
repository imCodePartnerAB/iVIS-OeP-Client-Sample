/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.populators;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.hierarchy.foregroundmodules.blog.beans.TagEntry;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;

public class TagEntryPopulator implements BeanResultSetPopulator<TagEntry> {

	@Override
	public TagEntry populate(ResultSet rs) throws SQLException {

		TagEntry entry = new TagEntry();

		entry.setPostCount(rs.getInt("postCount"));
		entry.setTagName(rs.getString("tag"));

		return entry;
	}
}
