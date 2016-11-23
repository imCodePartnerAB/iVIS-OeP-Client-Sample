package se.unlogic.hierarchy.foregroundmodules.login;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;


public class XMLLoginModule extends AnnotatedForegroundModule {

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Update last login",description="Controls if the last login field of the user should be updated")
	protected boolean updateLastLogin;
	
	@ModuleSetting
	@CheckboxSettingDescriptor(name="Include attributes",description="Controls if the user attributes are included in the XML")
	protected boolean includeAttributes;		
	
	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		String username = getUsername(req);
		String password = getPassword(req);

		if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)){

			User requestedUser = systemInterface.getUserHandler().getUserByUsernamePassword(username, password, true, true);

			if(requestedUser != null){

				if(updateLastLogin){

					setLastLogin(requestedUser);
				}

				req.getSession(true).setAttribute("user", requestedUser);

				log.info("User " + requestedUser + " logged in from address " + req.getRemoteHost());
				
				systemInterface.getEventHandler().sendEvent(User.class, new LoginEvent(requestedUser, req.getSession()), EventTarget.ALL);

				user = requestedUser;

				Document doc = XMLUtils.createDomDocument();
				
				doc.appendChild(user.toXML(doc));
				
				if(user.getAttributeHandler() != null && !user.getAttributeHandler().isEmpty()){
				
					doc.getDocumentElement().appendChild(user.getAttributeHandler().toXML(doc));
				}

				sendResponse(doc, res);
				
				return null;
				
			}
		}
		
		log.warn("Failed login attempt using username " + username + " from address " + req.getRemoteHost());
		
		res.setStatus(400);
		
		Document doc = XMLUtils.createDomDocument();
		
		doc.appendChild(doc.createElement("LoginFailed"));
		
		sendResponse(doc, res);
		
		return null;		
	}

	protected void sendResponse(Document doc, HttpServletResponse res) {

		res.setCharacterEncoding(systemInterface.getEncoding());
		res.setContentType("text/xml");

		try {
			XMLUtils.writeXML(doc, res.getOutputStream(), false, systemInterface.getEncoding());
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		} catch (IOException e) {}
	}	
	
	protected String getUsername(HttpServletRequest req) {

		return req.getParameter("username");
	}

	protected String getPassword(HttpServletRequest req) {

		return req.getParameter("password");
	}

	protected void setLastLogin(User user) throws SQLException {

		user.setCurrentLogin(new Timestamp(System.currentTimeMillis()));

		if(user instanceof MutableUser){

			MutableUser mutableUser = (MutableUser) user;

			Timestamp lastLogin = user.getLastLogin();

			mutableUser.setLastLogin(user.getCurrentLogin());

			try {
				systemInterface.getUserHandler().updateUser(mutableUser, false, false, false);

			} catch (Exception e) {

				log.error("Unable to update last login for user " + user,e);
			}

			mutableUser.setLastLogin(lastLogin);
		}
	}	
}
