package se.unlogic.hierarchy.core.utils;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Group;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.AttributeHandler;


public class UserUtils {

	public static Group getGroupByAttribute(User user, String attributeName, Object attributeValue){

		return getGroupByAttribute(user, attributeName, attributeValue.toString());
	}

	public static Group getGroupByAttribute(User user, String attributeName, String attributeValue){

		if(user == null || user.getGroups() == null){

			return null;
		}

		for(Group group : user.getGroups()){

			AttributeHandler attributeHandler = group.getAttributeHandler();

			if(attributeHandler != null){

				String value = attributeHandler.getString(attributeName);

				if(value != null && value.equals(attributeValue)){

					return group;
				}
			}
		}

		return null;
	}

	public static void appendUsers(Document doc, Element element, Collection<User> users, boolean appendAttributes) {

		if(users != null) {

			for(User user : users) {

				Element userElement = user.toXML(doc);
				element.appendChild(userElement);

				if(appendAttributes && user.getAttributeHandler() != null) {

					userElement.appendChild(user.getAttributeHandler().toXML(doc));

				}

			}

		}

	}

	public static String getAttribute(String attributeName, User user) {

		AttributeHandler attributeHandler = user.getAttributeHandler();

		if(attributeHandler != null){

			return attributeHandler.getString(attributeName);
		}

		return null;
	}
}
