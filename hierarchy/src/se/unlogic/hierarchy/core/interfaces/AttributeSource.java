package se.unlogic.hierarchy.core.interfaces;

import java.util.List;


public interface AttributeSource {

	public List<? extends MutableAttribute> getAttributes();
	
	public void addAttribute(String name, String value);
}
