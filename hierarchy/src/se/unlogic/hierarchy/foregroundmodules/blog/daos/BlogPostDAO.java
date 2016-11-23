/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.daos;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.foregroundmodules.blog.beans.ArchiveEntry;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.BlogPost;
import se.unlogic.hierarchy.foregroundmodules.blog.beans.TagEntry;
import se.unlogic.standardutils.enums.Month;

public interface BlogPostDAO {

	public List<BlogPost> getPosts(int startIndex, int postCount, String blogID) throws SQLException;

	public int getPostCount(String blogID) throws SQLException;

	public BlogPost getPost(int postID, boolean relations, String blogID) throws SQLException;

	public BlogPost getPost(String alias, boolean relations, String blogID) throws SQLException;

	public List<ArchiveEntry> getLatestArchiveEntries(int archiveBundleLimit, String blogID) throws SQLException;

	public List<TagEntry> getTagEntries(int tagBundleLimit, String blogID) throws SQLException;

	public void add(BlogPost blogPost, String blogID) throws SQLException;

	public void update(BlogPost blogPost) throws SQLException;

	public void delete(BlogPost blogPost, String blogID) throws SQLException;

	public List<BlogPost> getPosts(Integer year, String blogID) throws SQLException;

	public List<TagEntry> getTagEntries(String blogID) throws SQLException;

	public List<String> getTags(String blogID) throws SQLException;

	public List<ArchiveEntry> getArchiveEntries(String blogID) throws SQLException;

	public List<BlogPost> getPosts(String tag, String blogID) throws SQLException;

	public List<BlogPost> getPosts(int year, Month month, String blogID) throws SQLException;

	public List<BlogPost> getLatestPosts(int limit, String blogID) throws SQLException;

	public void incrementReadCount(int postID) throws SQLException;

}
