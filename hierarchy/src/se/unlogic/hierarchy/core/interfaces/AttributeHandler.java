package se.unlogic.hierarchy.core.interfaces;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import se.unlogic.standardutils.xml.Elementable;

public interface AttributeHandler extends Serializable, Elementable{

	public boolean isSet(String name);

	public String getString(String name);

	public Integer getInt(String name);

	public Long getLong(String name);

	public Double getDouble(String name);

	public Boolean getBoolean(String name);

	public boolean isEmpty();

	public Set<String> getNames();

	public int size();

	public boolean getPrimitiveBoolean(String name);

	/**
	 * @return A map containing all attributes. It should be noted that changes to this map will not affect the underlying map in the AttributeHandler.
	 */
	public Map<String, String> getAttributeMap();

}