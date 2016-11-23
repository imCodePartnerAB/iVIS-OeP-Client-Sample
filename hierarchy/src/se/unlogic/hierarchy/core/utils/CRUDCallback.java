/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.webutils.http.URIParser;

public interface CRUDCallback<UserType extends User> {

	Document createDocument(HttpServletRequest req, URIParser uriParser, UserType user);

	public String getFullAlias();

	public String getTitlePrefix();

	public Breadcrumb getDefaultBreadcrumb();
}
