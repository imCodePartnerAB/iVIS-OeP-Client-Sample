/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery.daos;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Comment;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.beans.Gallery;
import se.unlogic.hierarchy.foregroundmodules.imagegallery.populators.CommentPopulator;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.IntegerKeyCollector;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;

public class CommentDao extends BaseDAO {

	private static CommentPopulator Populator = new CommentPopulator(null);
	private final PictureDao pictureDao;

	public CommentDao(DataSource ds, UserHandler userHandler) {
		super(ds);

		pictureDao = new PictureDao(ds);
		Populator = new CommentPopulator(userHandler);
	}

	public ArrayList<Comment> findByPictureID(Integer pictureID) throws SQLException {

		ArrayListQuery<Comment> query = new ArrayListQuery<Comment>(this.dataSource.getConnection(), true, "SELECT * FROM picturecomments WHERE pictureID = ? ORDER BY date ASC, commentID ASC", Populator);

		query.setInt(1, pictureID);

		return query.executeQuery();
	}

	public Comment get(Integer commentID) throws SQLException {

		String queryString = "SELECT * FROM picturecomments WHERE commentID = ?";

		ObjectQuery<Comment> query = new ObjectQuery<Comment>(this.dataSource.getConnection(), true, queryString, Populator);

		query.setInt(1, commentID);

		Comment comment = query.executeQuery();

		return comment;
	}

	public ArrayList<Comment> getByFilenameAndGallery(String filename, Gallery gallery) throws SQLException {

		Integer pictureID = pictureDao.getPictureIDByFilenameAndGallery(filename, gallery);
		if (pictureID != null) {

			ArrayListQuery<Comment> query = new ArrayListQuery<Comment>(this.dataSource.getConnection(), true, "SELECT * FROM picturecomments WHERE pictureID = ? ORDER BY date ASC, commentID ASC", Populator);

			query.setInt(1, pictureID);

			return query.executeQuery();
		}

		return null;
	}

	public Integer add(Comment comment) throws SQLException {

		UpdateQuery query = new UpdateQuery(this.dataSource.getConnection(), true, "INSERT INTO picturecomments VALUES (null,?,?,?,?)");

		query.setInt(1, comment.getPictureID());
		query.setString(2, comment.getComment());
		query.setTimestamp(3, comment.getDate());
		if (comment.getUser() != null) {
			query.setInt(4, comment.getUser().getUserID());
		} else {
			query.setObject(4, null);
		}

		IntegerKeyCollector keyCollector = new IntegerKeyCollector();
		
		query.executeUpdate(keyCollector);
		
		return keyCollector.getKeyValue();
	}

	public void delete(Comment comment) throws SQLException {

		UpdateQuery query = new UpdateQuery(this.dataSource.getConnection(), true, "DELETE FROM picturecomments WHERE commentID = ?");

		query.setInt(1, comment.getCommentID());

		query.executeUpdate();
	}

	public void update(Comment comment) throws SQLException {
		UpdateQuery query = new UpdateQuery(this.dataSource.getConnection(), true, "UPDATE picturecomments SET comment = ? WHERE commentID = ?");

		query.setString(1, comment.getComment());
		query.setInt(2, comment.getCommentID());

		query.executeUpdate();
	}
}
