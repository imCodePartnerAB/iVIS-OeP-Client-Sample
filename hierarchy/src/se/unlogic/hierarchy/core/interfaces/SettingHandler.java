package se.unlogic.hierarchy.core.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

import se.unlogic.standardutils.xml.Elementable;

public interface SettingHandler extends Elementable{

	public boolean isSet(String id);

	public String getString(String id);

	public Integer getInt(String id);

	public Long getLong(String id);

	public Float getFloat(String id);

	public Double getDouble(String id);
	
	public Boolean getBoolean(String id);
	
	public Short getShort(String id);

	public List<String> getStrings(String id);

	public List<Integer> getInts(String id);

	public List<Long> getLongs(String id);

	public List<Float> getFloats(String id);
	
	public List<Double> getDoubles(String id);

	public boolean isEmpty();

	public Set<String> getIDs();

	public int size();

	public boolean getPrimitiveBoolean(String id);

	/**
	 * @return A map containing all settings for this module. It should be noted that changes to this map will not affect the MutableSettingHandler.
	 */
	public Map<String, List<String>> getMap();

}