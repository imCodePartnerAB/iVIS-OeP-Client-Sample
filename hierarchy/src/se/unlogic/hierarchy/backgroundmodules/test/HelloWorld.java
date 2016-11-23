/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.backgroundmodules.test;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.backgroundmodules.SimpleBackgroundModule;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.webutils.http.URIParser;


public class HelloWorld extends SimpleBackgroundModule{
	
	@Override
	public BackgroundModuleResponse processRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {
		
		return new SimpleBackgroundModuleResponse("<div class=\"contentitem\"><h1>Hello world!</h1><p>This background module is called " + this.moduleDescriptor.getName() + "</p><p>Current URI: "  + uriParser.getCurrentURI(false) + "</p><p>Remaining URI: " + uriParser.getRemainingURI() + "</p></div>");
	}
}
