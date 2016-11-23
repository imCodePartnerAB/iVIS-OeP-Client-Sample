/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.daos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.foregroundmodules.blog.beans.Comment;

public interface CommentDAO {

	public void add(Comment comment) throws SQLException;

	public Comment get(Integer commentID) throws SQLException;

	public void update(Comment comment) throws SQLException;

	public void delete(Comment comment) throws SQLException;

	public List<Comment> getBlogPostComments(Connection connection, Integer postID) throws SQLException;

}
