/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.http;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.xml.XMLUtils;

public class SessionUtils {

	public static Element getSessionInfoAsXML(HttpSession session, Document doc){

		try {
			Element sessionInfo = doc.createElement("SessionInfo");

			sessionInfo.appendChild(XMLUtils.createElement("SessionID", session.getId(), doc));
			sessionInfo.appendChild(XMLUtils.createElement("CreationTime", DateUtils.DATE_TIME_FORMATTER.format(new Date(session.getCreationTime())), doc));
			sessionInfo.appendChild(XMLUtils.createElement("LastAccessedTime", DateUtils.DATE_TIME_FORMATTER.format(new Date(session.getLastAccessedTime())), doc));
			sessionInfo.appendChild(XMLUtils.createElement("MaxInactiveInterval", TimeUtils.secondsToString(session.getMaxInactiveInterval()), doc));

			return sessionInfo;
		} catch (IllegalStateException e) {
			//Protection against session invalidation
			return null;
		}
	}
	
	public static boolean setAttribute(String name, Object value, HttpServletRequest req){
		
		return setAttribute(name, value, req.getSession());
	}
	
	public static boolean setAttribute(String name, Object value, HttpSession session){
		
		if(session != null){
			
			try{
				session.setAttribute(name, value);
				
				return true;
				
			} catch (IllegalStateException e) {}
		}
		
		return false;
	}
	
	public static Object getAttribute(String name, HttpServletRequest req){
		
		return getAttribute(name, req.getSession());
	}
	
	public static Object getAttribute(String name, HttpSession session){
		
		if(session != null){
			
			try{
				return session.getAttribute(name);
				
			} catch (IllegalStateException e) {}
		}
		
		return null;
	}

	public static void removeAttribute(String name, HttpServletRequest req) {

		removeAttribute(name, req.getSession());
	}
	
	public static void removeAttribute(String name, HttpSession session) {

		if(session != null){
			
			try{
				session.removeAttribute(name);
				
			} catch (IllegalStateException e) {}
		}
	}	
}
