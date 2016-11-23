/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.dummy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.emailutils.framework.Attachment;
import se.unlogic.emailutils.framework.Email;
import se.unlogic.emailutils.framework.EmailSender;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class DummyEmailSenderModule extends AnnotatedForegroundModule implements EmailSender {

	private Email email;

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>" + this.moduleDescriptor.getName() + "</h1>");

		if (email == null) {
			stringBuilder.append("<p>No emails received</p>");
		} else {
			stringBuilder.append("<p>Latest received e-mail</p>");
			stringBuilder.append("<p><a href=\"" + this.getModuleURI(req) + "/rawMessage\">Click here to view raw message</a></p>");
			stringBuilder.append("<table>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Sender name:</td>");
			stringBuilder.append("<td>" + email.getSenderName() + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Sender address:</td>");
			stringBuilder.append("<td>" + email.getSenderAddress() + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Reply to:</td>");
			stringBuilder.append("<td>" + email.getReplyTo() + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Recipients:</td>");
			stringBuilder.append("<td>" + StringUtils.toCommaSeparatedString(email.getRecipients()) + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>BCCRecipients:</td>");
			stringBuilder.append("<td>" + StringUtils.toCommaSeparatedString(email.getBccRecipients()) + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>CCRecipients:</td>");
			stringBuilder.append("<td>" + StringUtils.toCommaSeparatedString(email.getCcRecipients()) + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Subject:</td>");
			stringBuilder.append("<td>" + email.getSubject() + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td colspan=\"2\">Message:</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td colspan=\"2\"><pre>" + email.getMessage() + "</pre></td>");
			stringBuilder.append("</tr>");

			if(!CollectionUtils.isEmpty(email.getAttachments())){
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td colspan=\"2\">Attachments:</td>");
				stringBuilder.append("</tr>");
				
				for(Attachment attachment : email.getAttachments()){
					
					stringBuilder.append("<tr>");
					stringBuilder.append("<td colspan=\"2\"><pre>" + attachment.toString() + "</pre></td>");
					stringBuilder.append("</tr>");				
				}
			}
			
			stringBuilder.append("</table>");
		}

		stringBuilder.append("</div>");

		return new SimpleForegroundModuleResponse(stringBuilder.toString(), moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor,
			SectionInterface sectionInterface, DataSource dataSource)
					throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.sectionInterface.getSystemInterface().getEmailHandler().addSender(this);
	}

	@Override
	public void unload() throws Exception {

		this.sectionInterface.getSystemInterface().getEmailHandler().removeSender(this);

		super.unload();
	}

	@WebPublic
	public SimpleForegroundModuleResponse rawMessage(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if(email == null){
			this.redirectToDefaultMethod(req, res);
			return null;
		}

		res.setContentType(email.getMessageContentType());
		res.getWriter().write(email.getMessage());
		res.flushBuffer();

		return null;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public boolean send(Email email) {

		log.info("Received email " + email);

		this.email = email;

		return true;
	}

}
