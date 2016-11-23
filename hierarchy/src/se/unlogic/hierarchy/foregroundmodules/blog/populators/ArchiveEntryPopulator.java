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

import se.unlogic.hierarchy.foregroundmodules.blog.beans.ArchiveEntry;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.enums.Month;

public class ArchiveEntryPopulator implements BeanResultSetPopulator<ArchiveEntry> {

	@Override
	public ArchiveEntry populate(ResultSet rs) throws SQLException {

		ArchiveEntry archiveEntry = new ArchiveEntry();

		archiveEntry.setMonth(EnumUtils.toEnum(Month.values(), rs.getInt("monthNr")-1));
		archiveEntry.setYear(rs.getInt("yearNr"));
		archiveEntry.setPostCount(rs.getInt("postCount"));

		return archiveEntry;
	}
}
