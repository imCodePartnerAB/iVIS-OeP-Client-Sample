/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.http;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.xml.XMLUtils;

public class RequestUtils {

	@SuppressWarnings("unchecked")
	public static Element getRequestParameters(HttpServletRequest req, Document doc) {

		Element requestparameters = doc.createElement("requestparameters");

		if (req.getParameterMap() != null) {
			HashMap<String, String[]> parammap = new HashMap<String, String[]>(req.getParameterMap());

			for (String paramname : parammap.keySet()) {
				Element param = doc.createElement("parameter");
				requestparameters.appendChild(param);

				param.appendChild(XMLUtils.createCDATAElement("name", paramname, doc));

				String[] values = parammap.get(paramname);

				if (values != null) {
					for (String value2 : values) {
						Element value = doc.createElement("value");
						value.appendChild(doc.createCDATASection(value2));
						param.appendChild(value);
					}
				}
			}
		}
		return requestparameters;
	}

	@SuppressWarnings("unchecked")
	public static Element getRequestParameters(HttpServletRequest req, Document doc, String... parameterNames) {

		Element requestparameters = doc.createElement("requestparameters");

		if (req.getParameterMap() != null) {
			HashMap<String, String[]> parammap = new HashMap<String, String[]>(req.getParameterMap());

			for (String paramname : parammap.keySet()) {
				Element param = doc.createElement("parameter");
				requestparameters.appendChild(param);

				param.appendChild(XMLUtils.createCDATAElement("name", paramname, doc));

				String[] values = parammap.get(paramname);

				if (values != null) {
					for (String value2 : values) {
						Element value = doc.createElement("value");
						value.appendChild(doc.createCDATASection(value2));
						param.appendChild(value);
					}
				}
			}
		}
		return requestparameters;
	}

	public static Element getRequestInfoAsXML(Document doc, HttpServletRequest req) {

		return getRequestInfoAsXML(doc, req, null, false, false);
	}
	
	public static Element getRequestInfoAsXML(Document doc, HttpServletRequest req, URIParser uriParser) {

		return getRequestInfoAsXML(doc, req, uriParser, false, false);
	}

	public static Element getRequestInfoAsXML(Document doc, HttpServletRequest req, URIParser uriParser, boolean queryString, boolean uriparts) {

		Element requestinfo = doc.createElement("requestinfo");

		requestinfo.appendChild(XMLUtils.createCDATAElement("contextpath", req.getContextPath(), doc));
		requestinfo.appendChild(XMLUtils.createCDATAElement("servletpath", req.getServletPath(), doc));
		requestinfo.appendChild(XMLUtils.createCDATAElement("servername", req.getServerName(), doc));

		//Only add this parameter when it is absolutely necessary since it can be very long (200k+ chars) and is seldom used
		if (queryString && req.getQueryString() != null) {
			requestinfo.appendChild(XMLUtils.createCDATAElement("querystring", req.getQueryString(), doc));
		}

		if(uriParser != null){
			
			requestinfo.appendChild(XMLUtils.createCDATAElement("currentURI", uriParser.getCurrentURI(true), doc));
			requestinfo.appendChild(XMLUtils.createCDATAElement("url", uriParser.getRequestURL(), doc));
			requestinfo.appendChild(XMLUtils.createCDATAElement("uri", req.getContextPath() + uriParser.getFormattedURI(), doc));
			
			if(uriparts){

				if (uriParser.size() > 0) {
					//Add uri parts
					Element uripartsElement = doc.createElement("uriparts");
					requestinfo.appendChild(uripartsElement);

					String[] uriParts = uriParser.getAll();
					for (int i = 0; i < uriParts.length; i++) {
						Element uripart = doc.createElement("uripart");
						uripartsElement.appendChild(uripart);
						uripart.appendChild(XMLUtils.createElement("value", uriParts[i], doc));
						uripart.appendChild(XMLUtils.createElement("position", Integer.toString(i), doc));
					}
				}			
			}			
		}		
		
		return requestinfo;
	}

	public static String getServerURL(HttpServletRequest req) {

		int serverPort = req.getServerPort();
		String protocol = req.getScheme();

		String port = "";

		if ((protocol.equals("http") && (serverPort != 80)) || (protocol.equals("https") && (serverPort != 443))) {

			port = ":" + serverPort;
		}

		return protocol + "://" + req.getServerName() + port;
	}

	public static String getFullContextPathURL(HttpServletRequest req) {

		return getServerURL(req) + req.getContextPath();
	}

	public static String rewriteAsRelativeURL(String url, HttpServletRequest req) {

		String absoluteContextPath = RequestUtils.getFullContextPathURL(req);

		if (url.startsWith(absoluteContextPath)) {
			return url.substring(absoluteContextPath.length());
		}

		return url;
	}

	public static String rewriteAsAbsoluteURL(String url, HttpServletRequest req) {

		String absoluteContextPath = RequestUtils.getFullContextPathURL(req);

		if (url.startsWith("/")) {
			return absoluteContextPath + url;
		}

		return url;
	}

	public static Cookie getCookie(HttpServletRequest req, String name) {

		if(req.getCookies() == null || req.getCookies().length == 0){

			return null;
		}

		for(Cookie cookie : req.getCookies()){

			if(name.equals(cookie.getName())){

				return cookie;
			}
		}

		return null;
	}
}
