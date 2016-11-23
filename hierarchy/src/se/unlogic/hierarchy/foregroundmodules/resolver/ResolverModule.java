/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.resolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;


public class ResolverModule extends SimpleForegroundModule{

	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");

		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));

		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);

		String value = req.getParameter("host");

		if(!StringUtils.isEmpty(value)){

			value = value.trim();

			log.info("User " + user + " resolving host " + value);

			document.appendChild(XMLUtils.createCDATAElement("Host", value, doc));

			try {
				InetAddress inetAddress = InetAddress.getByName(value);

				String resolvedHost = inetAddress.getHostAddress();

				if(!resolvedHost.equals(value)){

					document.appendChild(XMLUtils.createCDATAElement("ResolvedHost", resolvedHost, doc));

				}else{

					document.appendChild(doc.createElement("UnableToResolveHost"));
				}

			} catch (UnknownHostException e) {

				document.appendChild(doc.createElement("UnableToResolveHost"));
			}

		}else if((value = req.getParameter("ip")) != null){

			value = value.trim();

			log.info("User " + user + " resolving ip " + value);

			document.appendChild(XMLUtils.createCDATAElement("IP", value, doc));

			try {
				InetAddress inetAddress = InetAddress.getByName(value);

				String resolvedIP = inetAddress.getHostName();

				if(!resolvedIP.equals(value)){

					document.appendChild(XMLUtils.createCDATAElement("ResolvedIP", resolvedIP, doc));

				}else{

					document.appendChild(doc.createElement("UnableToResolveIP"));
				}

			} catch (UnknownHostException e) {

				document.appendChild(doc.createElement("UnableToResolveIP"));
			}

		}else{

			log.info("User " + user + " requesting resolver form");
		}

		return new SimpleForegroundModuleResponse(doc, this.getDefaultBreadcrumb());
	}
}
