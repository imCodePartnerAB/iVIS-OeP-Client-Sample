package se.unlogic.hierarchy.foregroundmodules.test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateUserException;
import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.random.RandomUtils;
import se.unlogic.webutils.http.URIParser;


public class UserAttributeTestModule extends AnnotatedForegroundModule {

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {

		log.info("User " + user + " listing attributes");
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>Module settings test</h1>");

		if(user == null){
			
			stringBuilder.append("<p>You are not logged in, user attribute support cannot be tested.</p>");
		
		}else{
		
			AttributeHandler attributeHandler = user.getAttributeHandler();
			
			if(attributeHandler == null){
				
				stringBuilder.append("<p>The current user type (" + user.getClass() + ") does <b>NOT</b> seem to support user attributes.</p>");
				
			}else{
								
				if(attributeHandler instanceof MutableAttributeHandler){
					
					stringBuilder.append("<p>The current user type (" + user.getClass() + ") supports mutable user attributes.</p>");
					
				}else{
					
					stringBuilder.append("<p>The current user type (" + user.getClass() + ") supports immutable user attributes.</p>");
				}
				
				Map<String,String> attributeMap = attributeHandler.getAttributeMap();
				
				if(CollectionUtils.isEmpty(attributeMap)){
					
					stringBuilder.append("<p>Attribute count: 0</p>");
					
				}else{
					
					stringBuilder.append("<p>Attribute count: " + attributeMap.size() + "</p>");
					
					stringBuilder.append("<p>Attributes:</p>");
					
					stringBuilder.append("<table>");
					stringBuilder.append("<tr><th>Name</th><th>Value</th></tr>");
					
					for(Entry<String, String> attribute : attributeMap.entrySet()){
						
						stringBuilder.append("<tr><td>" + attribute.getKey() + "</td><td>" + attribute.getValue() + "</td></tr>");
					}
					
					stringBuilder.append("</table>");
				}
				
				if(attributeHandler instanceof MutableAttributeHandler){
					
					stringBuilder.append("<p><a href=\"" + req.getContextPath() + this.getFullAlias() + "/clear" + "\">Clear attributes</a></p>");
					stringBuilder.append("<p><a href=\"" + req.getContextPath() + this.getFullAlias() + "/add" + "\">Add random attribute</a></p>");
					stringBuilder.append("<p><a href=\"" + req.getContextPath() + this.getFullAlias() + "/save" + "\">Save user and attributes</a></p>");
				}
			}
		}

		stringBuilder.append("</div>");
		
		return new SimpleForegroundModuleResponse(stringBuilder.toString(),getDefaultBreadcrumb());
	}
	
	@WebPublic(requireLogin=true)
	public ForegroundModuleResponse clear(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException {
		
		log.info("User " + user + " clearing attributes");
		
		AttributeHandler attributeHandler = user.getAttributeHandler();
		
		if(attributeHandler instanceof MutableAttributeHandler){
			
			((MutableAttributeHandler)attributeHandler).clear();
		}
		
		redirectToDefaultMethod(req, res);
		return null;
	}
	
	@WebPublic(requireLogin=true, toLowerCase=true)
	public ForegroundModuleResponse add(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException {
		
		log.info("User " + user + " adding test attribute");
		
		AttributeHandler attributeHandler = user.getAttributeHandler();
		
		if(attributeHandler instanceof MutableAttributeHandler){
			
			((MutableAttributeHandler)attributeHandler).setAttribute("Test attribute " + RandomUtils.getRandomInt(1, 1024), System.currentTimeMillis());
			((MutableAttributeHandler)attributeHandler).setAttribute("Static Test attribute ", "Static value");
		}
		
		redirectToDefaultMethod(req, res);
		return null;
	}
	
	@WebPublic(requireLogin=true)
	public ForegroundModuleResponse save(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, UnableToUpdateUserException {
		
		log.info("User " + user + " saving user and attributes");
		
		systemInterface.getUserHandler().updateUser(user, false, false, true);
		
		redirectToDefaultMethod(req, res);
		return null;
	}
	
	@WebPublic(toLowerCase=true)
	public ForegroundModuleResponse getUsersByAttribute(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, UnableToUpdateUserException {
		
		log.info("User " + user + " listing attributes");
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>Module settings test</h1>");

		String attributeName = req.getParameter("name");
		String attributeValue = req.getParameter("value");
		
		if(attributeName == null || attributeValue == null){
			
			stringBuilder.append("<p>Both <b>name</b> and <b>value</b> parameters need to specified</p>");
		
		}else{
		
			List<User> users = systemInterface.getUserHandler().getUsersByAttribute(attributeName, attributeValue, false, false);
			
			stringBuilder.append("<p>Matching users: " + CollectionUtils.getSize(users) + "</p>");
			
			if(users != null){
				
				stringBuilder.append("<p>Users:</p>");
				
				stringBuilder.append("<table>");
				stringBuilder.append("<tr><th>toString</th><th>Class</th></tr>");
				
				for(User matchingUser : users){
					
					stringBuilder.append("<tr><td>" + matchingUser + "</td><td>" + matchingUser.getClass() + "</td></tr>");
				}
				
				stringBuilder.append("</table>");
			}
		}

		stringBuilder.append("</div>");
		
		return new SimpleForegroundModuleResponse(stringBuilder.toString(),getDefaultBreadcrumb());
	}
}
