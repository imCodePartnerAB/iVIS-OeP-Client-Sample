/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.smssenders.dummy;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SMS;
import se.unlogic.hierarchy.core.interfaces.SMSSender;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class DummySMSSenderModule extends AnnotatedForegroundModule implements SMSSender {

	private SMS sms;

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>" + this.moduleDescriptor.getName() + "</h1>");

		if (sms == null) {
			stringBuilder.append("<p>No SMS received</p>");
		} else {
			stringBuilder.append("<p>Latest received SMS</p>");
			stringBuilder.append("<table>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Sender name:</td>");
			stringBuilder.append("<td>" + sms.getSenderName() + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Recipients:</td>");
			stringBuilder.append("<td>" + StringUtils.toCommaSeparatedString(sms.getRecipients()) + "</td>");
			stringBuilder.append("</tr>");

			stringBuilder.append("<tr>");
			stringBuilder.append("<td>Message:</td>");
			stringBuilder.append("<td>" + sms.getMessage().replaceAll("[\\n]+", "<br>") + "</td>");
			stringBuilder.append("</tr>");

			if (sms.getAttributeHandler() != null) {

				stringBuilder.append("<tr>");
				stringBuilder.append("<td colspan=\"2\">Attributes:</td>");
				stringBuilder.append("</tr>");

				for (Entry<String, String> entry : sms.getAttributeHandler().getAttributeMap().entrySet()) {

					stringBuilder.append("<tr>");
					stringBuilder.append("<td>" + entry.getKey().toString() + ":" + "</td>");
					stringBuilder.append("<td>" + entry.getValue().toString() + "</td>");
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

		if (!systemInterface.getInstanceHandler().addInstance(SMSSender.class, this)) {

			log.warn("Another instance has already been registered in instance handler for class " + SMSSender.class.getName());
		}
	}

	@Override
	public void unload() throws Exception {

		if (this.equals(systemInterface.getInstanceHandler().getInstance(SMSSender.class))) {

			systemInterface.getInstanceHandler().removeInstance(SMSSender.class);
		}

		super.unload();
	}

	@Override
	public boolean send(SMS sms) {

		log.info("Received SMS " + sms);

		this.sms = sms;

		return true;
	}

}
