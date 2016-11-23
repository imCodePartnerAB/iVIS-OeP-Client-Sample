/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.test;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.emailutils.framework.FileAttachment;
import se.unlogic.emailutils.framework.SimpleEmail;
import se.unlogic.emailutils.framework.StringAttachment;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.servlets.CoreServlet;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.webutils.http.URIParser;

public class EmailMailTestModule extends SimpleForegroundModule {

	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		SimpleEmail email = new SimpleEmail();

		email.setSenderName(user.getFirstname() + " \"" + user.getUsername() + "\" " + user.getLastname());
		email.setSenderAddress(user.getEmail());
		email.addRecipient(user.getEmail());
		email.setSubject(CoreServlet.VERSION + " test message");
		email.setMessage("Hello!\nThis is a test message from " + CoreServlet.VERSION + " sent to you by " + user);

		email.add(new FileAttachment(new File(this.sectionInterface.getSystemInterface().getApplicationFileSystemPath() + "/WEB-INF/web.xml")));

		email.add(new StringAttachment("Text attachment",SimpleEmail.TEXT));

		email.add(new StringAttachment("test.txt","Text attachment",SimpleEmail.TEXT));

		this.sectionInterface.getSystemInterface().getEmailHandler().send(email);

		return new SimpleForegroundModuleResponse("Mail sent to " + user.getEmail(),getDefaultBreadcrumb());
	}

}
