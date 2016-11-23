package se.unlogic.hierarchy.core.interfaces;

import java.util.List;

import se.unlogic.hierarchy.core.interfaces.AttributeHandler;

public interface SMS {

	public String getSenderName();

	public String getMessage();

	public List<String> getRecipients();

	public AttributeHandler getAttributeHandler();

}