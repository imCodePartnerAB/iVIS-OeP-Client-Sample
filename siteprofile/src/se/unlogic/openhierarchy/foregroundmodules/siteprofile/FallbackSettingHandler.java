package se.unlogic.openhierarchy.foregroundmodules.siteprofile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.SettingHandler;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.xml.XMLUtils;


public class FallbackSettingHandler implements SettingHandler{

	private final SettingHandler primary;
	private final SettingHandler fallback;

	public FallbackSettingHandler(SettingHandler primary, SettingHandler fallback) {

		super();
		this.primary = primary;
		this.fallback = fallback;
	}

	@Override
	public boolean isSet(String id) {

		if(primary.isSet(id)){

			return true;
		}

		return fallback.isSet(id);
	}

	@Override
	public String getString(String id) {
		
		if(!primary.isSet(id)){

			return fallback.getString(id);
		}

		return primary.getString(id);
	}

	@Override
	public Integer getInt(String id) {

		if(!primary.isSet(id)){

			return fallback.getInt(id);
		}

		return primary.getInt(id);
	}

	@Override
	public Long getLong(String id) {

		if(!primary.isSet(id)){

			return fallback.getLong(id);
		}

		return primary.getLong(id);
	}

	@Override
	public Double getDouble(String id) {

		if(!primary.isSet(id)){

			return fallback.getDouble(id);
		}

		return primary.getDouble(id);
	}

	@Override
	public Boolean getBoolean(String id) {

		if(!primary.isSet(id)){

			return fallback.getBoolean(id);
		}

		return primary.getBoolean(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> getIDs() {

		return CollectionUtils.combineAsSet(primary.getIDs(), fallback.getIDs());
	}

	@Override
	public List<Double> getDoubles(String id) {

		if(!primary.isSet(id)){

			return fallback.getDoubles(id);
		}

		return primary.getDoubles(id);
	}

	@Override
	public List<Integer> getInts(String id) {

		if(!primary.isSet(id)){

			return fallback.getInts(id);
		}

		return primary.getInts(id);
	}

	@Override
	public List<Long> getLongs(String id) {

		if(!primary.isSet(id)){

			return fallback.getLongs(id);
		}

		return primary.getLongs(id);
	}

	@Override
	public List<String> getStrings(String id) {

		if(!primary.isSet(id)){

			return fallback.getStrings(id);
		}

		return primary.getStrings(id);
	}

	@Override
	public boolean getPrimitiveBoolean(String id) {

		if(!primary.isSet(id)){

			return fallback.getPrimitiveBoolean(id);
		}

		return primary.getPrimitiveBoolean(id);
	}

	@Override
	public boolean isEmpty() {

		return primary.isEmpty() && fallback.isEmpty();
	}

	@Override
	public int size() {

		return getIDs().size();
	}

	@Override
	public Map<String, List<String>> getMap() {

		HashMap<String, List<String>> combinedMap = new HashMap<String, List<String>>(fallback.size() + primary.size());

		for(Entry<String,List<String>> entry : fallback.getMap().entrySet()){

			if(!primary.isSet(entry.getKey())){

				combinedMap.put(entry.getKey(), entry.getValue());
			}
		}

		combinedMap.putAll(primary.getMap());

		return combinedMap;
	}

	@Override
	public Element toXML(Document doc) {

		Element settingsElement = doc.createElement("settings");

		for (Entry<String, List<String>> entry : this.getMap().entrySet()) {

			Element settingElement = doc.createElement("setting");
			settingsElement.appendChild(settingElement);

			settingElement.appendChild(XMLUtils.createCDATAElement("id", entry.getKey(), doc));

			if (entry.getValue() != null) {

				for (String value : entry.getValue()) {
					settingElement.appendChild(XMLUtils.createCDATAElement("value", value, doc));
				}
			}
		}

		return settingsElement;
	}

	@Override
	public Short getShort(String id) {

		if(!primary.isSet(id)){

			return fallback.getShort(id);
		}

		return primary.getShort(id);
	}

	@Override
	public Float getFloat(String id) {

		if(!primary.isSet(id)){

			return fallback.getFloat(id);
		}

		return primary.getFloat(id);
	}

	@Override
	public List<Float> getFloats(String id) {

		if(!primary.isSet(id)){

			return fallback.getFloats(id);
		}

		return primary.getFloats(id);
	}
}
