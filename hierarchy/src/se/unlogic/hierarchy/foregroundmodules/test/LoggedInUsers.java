/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.webutils.http.URIParser;

public class LoggedInUsers extends SimpleForegroundModule {

	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<h2>Currently logged in users:</h2><br>");

		for(User loggedInUser : User.getLoggedInUsers()){
			stringBuilder.append(loggedInUser.toString() + "<br>");
		}

		return new SimpleForegroundModuleResponse(stringBuilder.toString(),getDefaultBreadcrumb());
	}
}
