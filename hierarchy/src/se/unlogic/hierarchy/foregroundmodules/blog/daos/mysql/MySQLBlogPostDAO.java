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
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.daos.BaseDAO;
import se.unlogic.hierarchy.core.handlers.UserHandler;
import se.unlogic.hierarchy.core.populators.UserTypePopulator;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.ArchiveEntry;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.BlogPost;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.TagEntry;
import se.unlogic.hierarchy.foregroundmodules.blog.daos.BlogPostDAO;
import se.unlogic.hierarchy.foregroundmodules.blog.populators.ArchiveEntryPopulator;
import se.unlogic.hierarchy.foregroundmodules.blog.populators.TagEntryPopulator;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ArrayListQuery;
import se.unlogic.standardutils.dao.querys.IntegerKeyCollector;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.enums.Month;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.populators.annotated.AnnotatedResultSetPopulator;

public class MySQLBlogPostDAO extends BaseDAO implements BlogPostDAO {

	private static final ArchiveEntryPopulator ARCHIVE_ENTRY_POPULATOR = new ArchiveEntryPopulator();
	private static final TagEntryPopulator TAG_ENTRY_POPULATOR = new TagEntryPopulator();

	private final AnnotatedResultSetPopulator<BlogPost> blogPostPopulator;
	private final MySQLCommentDAO commentDAO;

	public MySQLBlogPostDAO(DataSource ds, UserHandler userHandler, MySQLCommentDAO mySQLCommentDAO) {
		super(ds);
		this.blogPostPopulator = new AnnotatedResultSetPopulator<BlogPost>(BlogPost.class,new UserTypePopulator(userHandler,false, false));
		this.commentDAO = mySQLCommentDAO;
	}

	@Override
	public List<BlogPost> getPosts(int startIndex, int postCount, String blogID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ArrayListQuery<BlogPost> query = new ArrayListQuery<BlogPost>(connection, false, "SELECT * FROM blog_posts WHERE blogID = ? ORDER BY added DESC LIMIT " + startIndex + "," + postCount, blogPostPopulator);

			query.setString(1, blogID);

			ArrayList<BlogPost> posts = query.executeQuery();

			this.getRelations(connection, posts);

			return posts;
		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	private void getRelations(Connection connection, ArrayList<BlogPost> posts) throws SQLException {

		if (posts != null) {

			for (BlogPost post : posts) {

				this.getRelations(connection, post);
			}
		}
	}

	private void getRelations(Connection connection, BlogPost post) throws SQLException {

		if (post != null) {

			post.setComments(this.commentDAO.getBlogPostComments(connection, post.getPostID()));

			ArrayListQuery<String> tagQuery = new ArrayListQuery<String>(connection, false, "SELECT tag FROM blog_tags WHERE postID = ? ORDER BY tag", StringPopulator.getPopulator());

			tagQuery.setInt(1, post.getPostID());

			post.setTags(tagQuery.executeQuery());
		}
	}

	@Override
	public int getPostCount(String blogID) throws SQLException {

		ObjectQuery<Integer> query = new ObjectQuery<Integer>(dataSource, "SELECT COUNT(postID) FROM blog_posts WHERE blogID = ?", IntegerPopulator.getPopulator());

		query.setString(1, blogID);

		return query.executeQuery();
	}

	@Override
	public BlogPost getPost(int postID, boolean relations, String blogID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ObjectQuery<BlogPost> query = new ObjectQuery<BlogPost>(connection, false, "SELECT * FROM blog_posts WHERE postID = ? AND blogID = ?", blogPostPopulator);

			query.setInt(1, postID);
			query.setString(2, blogID);

			BlogPost post = query.executeQuery();

			if (relations) {
				getRelations(connection, post);
			}

			return post;

		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	@Override
	public BlogPost getPost(String alias, boolean relations, String blogID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ObjectQuery<BlogPost> query = new ObjectQuery<BlogPost>(connection, false, "SELECT * FROM blog_posts WHERE alias = ? AND blogID = ?", blogPostPopulator);

			query.setString(1, alias);
			query.setString(2, blogID);

			BlogPost post = query.executeQuery();

			if (relations) {
				getRelations(connection, post);
			}

			return post;

		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	@Override
	public List<ArchiveEntry> getLatestArchiveEntries(int archiveBundleLimit, String blogID) throws SQLException {

		ArrayListQuery<ArchiveEntry> query = new ArrayListQuery<ArchiveEntry>(dataSource, "SELECT YEAR(added) as yearNr, MONTH(added) as monthNr, count(postID) as postCount FROM blog_posts WHERE blogID = ? GROUP BY yearNr, monthNr ORDER BY yearNr DESC, monthNr DESC LIMIT " + archiveBundleLimit, ARCHIVE_ENTRY_POPULATOR);

		query.setString(1, blogID);

		return query.executeQuery();
	}

	@Override
	public List<TagEntry> getTagEntries(int tagBundleLimit, String blogID) throws SQLException {

		ArrayListQuery<TagEntry> query = new ArrayListQuery<TagEntry>(dataSource, "SELECT blog_tags.tag, COUNT(blog_posts.postID) AS postCount FROM blog_tags INNER JOIN blog_posts ON (blog_tags.postID=blog_posts.postID) WHERE blogID = ?  GROUP BY blog_tags.tag ORDER BY postCount DESC, tag LIMIT " + tagBundleLimit, TAG_ENTRY_POPULATOR);

		query.setString(1, blogID);

		return query.executeQuery();
	}

	@Override
	public void add(BlogPost blogPost, String blogID) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO blog_posts VALUES (null,?,?,?,?,?,?,?,?,?,?)");

			query.setString(1, blogPost.getAlias());
			query.setTimestamp(2, blogPost.getAdded());
			query.setTimestamp(3, blogPost.getUpdated());
			query.setString(4, blogPost.getTitle());
			query.setString(5, blogPost.getMessage());
			query.setInt(6, blogPost.getPoster().getUserID());

			if (blogPost.getEditor() != null) {
				query.setInt(7, blogPost.getEditor().getUserID());
			} else {
				query.setObject(7, null);
			}

			query.setInt(8, blogPost.getReadCount());

			query.setBoolean(9, blogPost.isSplit());

			query.setString(10, blogID);


			IntegerKeyCollector keyCollector = new IntegerKeyCollector();

			query.executeUpdate(keyCollector);

			blogPost.setPostID(keyCollector.getKeyValue());

			if (blogPost.getTags() != null) {
				this.setTags(blogPost, transactionHandler);
			}

			transactionHandler.commit();

		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}

	}

	private void setTags(BlogPost blogPost, TransactionHandler transactionHandler) throws SQLException {

		for (String tag : blogPost.getTags()) {

			UpdateQuery query = transactionHandler.getUpdateQuery("INSERT INTO blog_tags VALUES (?,?)");

			query.setInt(1, blogPost.getPostID());
			query.setString(2, tag);

			query.executeUpdate();
		}
	}

	@Override
	public void update(BlogPost blogPost) throws SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			UpdateQuery query = transactionHandler.getUpdateQuery("UPDATE blog_posts SET alias = ?, added = ?, updated = ?, title = ?, message = ?, posterID = ?, editorID = ?, readCount = ?, split = ? WHERE postID = ?");

			query.setString(1, blogPost.getAlias());
			query.setTimestamp(2, blogPost.getAdded());
			query.setTimestamp(3, blogPost.getUpdated());
			query.setString(4, blogPost.getTitle());
			query.setString(5, blogPost.getMessage());
			query.setInt(6, blogPost.getPoster().getUserID());

			if (blogPost.getEditor() != null) {
				query.setInt(7, blogPost.getEditor().getUserID());
			} else {
				query.setObject(7, null);
			}

			query.setInt(8, blogPost.getReadCount());
			query.setBoolean(9, blogPost.isSplit());
			query.setInt(10, blogPost.getPostID());

			query.executeUpdate();

			this.clearTags(blogPost, transactionHandler);

			if (blogPost.getTags() != null) {
				this.setTags(blogPost, transactionHandler);
			}

			transactionHandler.commit();

		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	private void clearTags(BlogPost blogPost, TransactionHandler transactionHandler) throws SQLException {

		UpdateQuery query = transactionHandler.getUpdateQuery("DELETE FROM blog_tags WHERE postID = ?");

		query.setInt(1, blogPost.getPostID());

		query.executeUpdate();
	}

	@Override
	public void delete(BlogPost blogPost, String blogID) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "DELETE FROM blog_posts WHERE postID = ? and blogID = ?");

		query.setInt(1, blogPost.getPostID());
		query.setString(2, blogID);

		query.executeUpdate();
	}

	@Override
	public List<BlogPost> getPosts(Integer year, String blogID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ArrayListQuery<BlogPost> query = new ArrayListQuery<BlogPost>(connection, false, "SELECT * FROM blog_posts WHERE YEAR(added) = ? AND blogID = ? ORDER BY added DESC", blogPostPopulator);

			query.setInt(1, year);
			query.setString(2, blogID);

			ArrayList<BlogPost> posts = query.executeQuery();

			this.getRelations(connection, posts);

			return posts;
		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	@Override
	public List<TagEntry> getTagEntries(String blogID) throws SQLException {

		ArrayListQuery<TagEntry> query = new ArrayListQuery<TagEntry>(dataSource, "SELECT DISTINCT(tag), COUNT(blog_tags.postID) as postCount FROM blog_tags INNER JOIN blog_posts ON (blog_tags.postID=blog_posts.postID) WHERE blogID = ? GROUP BY tag ORDER BY tag", TAG_ENTRY_POPULATOR);

		query.setString(1, blogID);

		return query.executeQuery();
	}

	@Override
	public List<String> getTags(String blogID) throws SQLException {

		ArrayListQuery<String> query = new ArrayListQuery<String>(dataSource, "SELECT DISTINCT(tag) FROM blog_tags INNER JOIN blog_posts ON (blog_tags.postID=blog_posts.postID) WHERE blogID = ? GROUP BY tag ORDER BY tag", StringPopulator.getPopulator());

		query.setString(1, blogID);

		return query.executeQuery();
	}

	@Override
	public List<ArchiveEntry> getArchiveEntries(String blogID) throws SQLException {

		ArrayListQuery<ArchiveEntry> query = new ArrayListQuery<ArchiveEntry>(dataSource, "SELECT YEAR(added) as yearNr, MONTH(added) as monthNr, count(postID) as postCount FROM blog_posts WHERE blogID = ? GROUP BY yearNr, monthNr ORDER BY yearNr DESC, monthNr DESC", ARCHIVE_ENTRY_POPULATOR);

		query.setString(1, blogID);

		return query.executeQuery();
	}

	@Override
	public List<BlogPost> getPosts(String tag, String blogID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ArrayListQuery<BlogPost> query = new ArrayListQuery<BlogPost>(connection, false, "SELECT blog_posts.* FROM blog_tags INNER JOIN blog_posts ON (blog_tags.postID=blog_posts.postID) WHERE blog_tags.tag = ? AND blogID = ? ORDER BY added DESC", blogPostPopulator);

			query.setString(1, tag);
			query.setString(2, blogID);

			ArrayList<BlogPost> posts = query.executeQuery();

			this.getRelations(connection, posts);

			return posts;
		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	@Override
	public List<BlogPost> getPosts(int year, Month month, String blogID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ArrayListQuery<BlogPost> query = new ArrayListQuery<BlogPost>(connection, false, "SELECT * FROM blog_posts WHERE YEAR(added) = ? AND MONTH(added) = ? AND blogID = ? ORDER BY added DESC", blogPostPopulator);

			query.setInt(1, year);
			query.setInt(2, month.ordinal() + 1);
			query.setString(3, blogID);

			ArrayList<BlogPost> posts = query.executeQuery();

			this.getRelations(connection, posts);

			return posts;
		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	@Override
	public List<BlogPost> getLatestPosts(int limit, String blogID) throws SQLException {

		Connection connection = null;

		try {
			connection = this.dataSource.getConnection();

			ArrayListQuery<BlogPost> query = new ArrayListQuery<BlogPost>(connection, false, "SELECT * FROM blog_posts WHERE blogID = ? ORDER BY added DESC LIMIT " + 0 + "," + limit, blogPostPopulator);

			query.setString(1, blogID);

			ArrayList<BlogPost> posts = query.executeQuery();

			this.getRelations(connection, posts);

			return posts;
		} finally {
			DBUtils.closeConnection(connection);
		}
	}

	@Override
	public void incrementReadCount(int postID) throws SQLException {

		UpdateQuery query = new UpdateQuery(dataSource, "UPDATE blog_posts SET readCount=readCount + 1 WHERE postID = ?");

		query.setInt(1, postID);

		query.executeUpdate();
	}

}
