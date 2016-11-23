/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.blog.daos;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.handlers.UserHandler;

public abstract class BlogDAOFactory {

	public abstract void init(DataSource dataSource, UserHandler userHandler) throws Exception;

	public abstract BlogPostDAO getBlogPostDAO();

	public abstract CommentDAO getCommentDAO();
}
