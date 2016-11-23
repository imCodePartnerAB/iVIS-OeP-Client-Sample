/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery.populators;

import java.sql.ResultSet;
import java.sql.SQLException;

import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Comment;
import se.unlogic.standardutils.dao.BeanResultSetPopulator;

public class CommentPopulator implements BeanResultSetPopulator<Comment> {

	private final UserHandler userHandler;

    public CommentPopulator(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	@Override
	public Comment populate(ResultSet rs) throws SQLException {

		Comment comment = new Comment();

		comment.setCommentID(rs.getInt("commentID"));
		comment.setPictureID(rs.getInt("pictureID"));
		comment.setComment(rs.getString("comment"));
		comment.setDate(rs.getTimestamp("date"));

		Integer userID = rs.getInt("userID");

		if(userID != null){
			comment.setUser(userHandler.getUser(userID, false, false));
		}

		return comment;
	}
}
