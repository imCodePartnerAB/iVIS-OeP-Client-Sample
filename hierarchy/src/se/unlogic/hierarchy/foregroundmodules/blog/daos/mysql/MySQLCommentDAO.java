/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.daos.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.populators.UserTypePopulator;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.Comment;
import se.unlogic.hierarchy.foregroundmodules.blog.daos.CommentDAO;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.IntegerKeyCollector;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.populators.annotated.AnnotatedResultSetPopulator;

public class MySQLCommentDAO extends BaseDAO implements CommentDAO {

	private final AnnotatedResultSetPopulator<Comment> commentPopulator;

	public MySQLCommentDAO(DataSource ds, UserHandler userHandler) {
		super(ds);
		commentPopulator = new AnnotatedResultSetPopulator<Comment>(Comment.class,new UserTypePopulator(userHandler, false, false));
	}

	@Override
	public void add(Comment comment) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "INSERT INTO blog_comments VALUES (null,?,?,?,?,?,?,?,?,?)");

		query.setTimestamp(1, comment.getAdded());
		query.setTimestamp(2, comment.getUpdated());
		query.setString(3, comment.getMessage());

		if (comment.getPoster() != null) {
			query.setInt(4, comment.getPoster().getUserID());
		} else {
			query.setObject(4, null);
		}

		if (comment.getEditor() != null) {
			query.setInt(5, comment.getEditor().getUserID());
		} else {
			query.setObject(5, null);
		}

		query.setString(6, comment.getPosterName());
		query.setString(7, comment.getPosterEmail());
		query.setString(8, comment.getPosterWebsite());
		query.setInt(9, comment.getPostID());

		IntegerKeyCollector keyCollector = new IntegerKeyCollector();

		query.executeUpdate(keyCollector);

		comment.setCommentID(keyCollector.getKeyValue());
	}

	@Override
	public Comment get(Integer commentID) throws SQLException {

		ObjectQuery<Comment> query = new ObjectQuery<Comment>(dataSource, "SELECT * FROM blog_comments WHERE commentID = ?", commentPopulator);

		query.setInt(1, commentID);

		return query.executeQuery();
	}

	@Override
	public void update(Comment comment) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "UPDATE blog_comments SET added = ?, updated = ?, message = ?, posterID = ?, editorID = ?, posterName = ?, posterEmail = ?, posterWebsite = ?, postID = ? WHERE commentID = ?");

		query.setTimestamp(1, comment.getAdded());
		query.setTimestamp(2, comment.getUpdated());
		query.setString(3, comment.getMessage());

		if (comment.getPoster() != null) {
			query.setInt(4, comment.getPoster().getUserID());
		} else {
			query.setObject(4, null);
		}

		if (comment.getEditor() != null) {
			query.setInt(5, comment.getEditor().getUserID());
		} else {
			query.setObject(5, null);
		}

		query.setString(6, comment.getPosterName());
		query.setString(7, comment.getPosterEmail());
		query.setString(8, comment.getPosterWebsite());
		query.setInt(9, comment.getPostID());

		query.setInt(10, comment.getCommentID());

		query.executeUpdate();

	}

	@Override
	public void delete(Comment comment) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "DELETE FROM blog_comments WHERE commentID = ?");

		query.setInt(1, comment.getCommentID());

		query.executeUpdate();
	}

	@Override
	public List<Comment> getBlogPostComments(Connection connection, Integer postID) throws SQLException {

		ArrayListQuery<Comment> query = new ArrayListQuery<Comment>(connection, false, "SELECT * FROM blog_comments WHERE postID = ? ORDER BY ADDED ASC", commentPopulator);

		query.setInt(1, postID);

		return query.executeQuery();
	}
}
