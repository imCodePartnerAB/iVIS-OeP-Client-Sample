/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.logout;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.HTMLEditorSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

public class LogoutModule extends AnnotatedForegroundModule {

	@ModuleSetting(allowsNull=true)
	@TextFieldSettingDescriptor(name="Redirect URL",description="If this field is set then users will be redirected on logout. If the url does not begin with http:// or https:// then the contextpath will be appended to the beginning of the given url.")
	protected String redirectURL;
	
	@ModuleSetting(allowsNull=true)
	@HTMLEditorSettingDescriptor(name="Logout message", description="An optional logout message which overrides the default logout message if no redirect URL is set")
	protected String logoutMessage;
	
	protected boolean relativeRedirectURL;
	
	@Override
	protected void moduleConfigured() throws Exception {

		super.moduleConfigured();
		
		checkSettings();
	}
	
	protected void checkSettings() {

		String redirectURL = this.redirectURL;
		
		if(redirectURL != null){
			
			redirectURL = redirectURL.toLowerCase();
			
			if(redirectURL.startsWith("http://") || redirectURL.startsWith("https://")){
				
				relativeRedirectURL = false;
				
			}else{
				
				relativeRedirectURL = true;
			}
		}
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException {

		if(user != null){
		
			HttpSession session = req.getSession();

			if(session != null){
				try{
					//This step is done in order to able to separate manual logins from sessions timeouts
					session.removeAttribute("user");
					
					session.invalidate();

					log.info("User " + user + " logged out!");
				}catch(IllegalStateException e){
					log.info("Unable to logout user " + user + " session already invalidated");
				}
			}
		}else{
			
			log.info("User is not logged in, displaying logged out message");
		}
		
		if(redirectURL != null){
			
			if(relativeRedirectURL){
				
				res.sendRedirect(req.getContextPath() + redirectURL);
				
			}else{
				
				res.sendRedirect(redirectURL);
			}
		}
		
		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		doc.appendChild(document);
		
		Element logoutElement = doc.createElement("LoggedOut");
		document.appendChild(logoutElement);

		if(logoutMessage != null){
			
			XMLUtils.appendNewElement(doc, logoutElement, "Message", URLRewriter.setAbsoluteLinkUrls(logoutMessage, req));
		}
		
		return new SimpleForegroundModuleResponse(doc,true,moduleDescriptor.getName());
	}
}
