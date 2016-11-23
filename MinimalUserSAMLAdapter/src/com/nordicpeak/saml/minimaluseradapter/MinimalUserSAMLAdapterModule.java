package com.nordicpeak.saml.minimaluseradapter;

import java.util.HashMap;
import java.util.List;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.schema.impl.XSStringImpl;

import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToAddUserException;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.minimaluser.MinimalUser;
import se.unlogic.standardutils.time.TimeUtils;

import com.nordicpeak.saml.SAMLUserAdapter;


public class MinimalUserSAMLAdapterModule extends AnnotatedForegroundModule implements SAMLUserAdapter {

	public static final String CITIZEN_IDENTIFIER = "citizenIdentifier";
	public static final String FIRST_NAME = "givenName";
	public static final String LAST_NAME = "surname";
	
	@ModuleSetting(allowsNull=true)
	@GroupMultiListSettingDescriptor(name="Default groups", description="Groups added to new users")
	private List<Integer> defaultGroups;
	
	public User getUser(Assertion assertion) {

		HashMap<String,String> attributeMap = new HashMap<String, String>(assertion.getAttributeStatements().size());
		
		for (Attribute attribute : assertion.getAttributeStatements().get(0).getAttributes()) {

			attributeMap.put(attribute.getName(), ((XSStringImpl) attribute.getAttributeValues().get(0)).getValue());
		}
		
		String citizenIdentifier = attributeMap.get(CITIZEN_IDENTIFIER);
		
		if(citizenIdentifier == null){
			
			log.warn("No " + CITIZEN_IDENTIFIER + " attribute found in assertion.");
			return null;
		}
		
		User user = systemInterface.getUserHandler().getUserByAttribute(CITIZEN_IDENTIFIER, citizenIdentifier, true, true);
		
		if(user != null){
			
			return user;
		}
		
		String firstName = attributeMap.get(FIRST_NAME);
		
		if(firstName == null){
			
			log.warn("No " + FIRST_NAME + " attribute found in assertion.");
			return null;
		}
		
		String lastName = attributeMap.get(LAST_NAME);
		
		if(lastName == null){
			
			log.warn("No " + LAST_NAME + " attribute found in assertion.");
			return null;
		}
		
		MinimalUser minimalUser = new MinimalUser();
		minimalUser.setFirstname(firstName);
		minimalUser.setLastname(lastName);
		minimalUser.setAdded(TimeUtils.getCurrentTimestamp());
		minimalUser.setEnabled(true);
		minimalUser.getAttributeHandler().setAttribute(CITIZEN_IDENTIFIER, citizenIdentifier);
		
		if(defaultGroups != null){
			
			minimalUser.setGroups(systemInterface.getGroupHandler().getGroups(defaultGroups, true));
		}
		
		try {
			this.systemInterface.getUserHandler().addUser(minimalUser);
			log.info("Added account for user " + minimalUser);
			
		} catch (UnableToAddUserException e) {

			log.error("Unable to add account for user " + minimalUser,e);
			
			return null;
		}
		
		return minimalUser;
	}

}
