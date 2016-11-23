/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLUtils;

public class SimpleSettingHandler implements MutableSettingHandler {

	private static final long serialVersionUID = -7497930260527274476L;
	private ConcurrentHashMap<String, List<String>> settingsMap;

	public SimpleSettingHandler(Map<String, List<String>> settingsMap) {
		super();

		this.setSettingsMap(settingsMap);
	}

	public SimpleSettingHandler(XMLParser xmlParser) {

		this.settingsMap = new ConcurrentHashMap<String, List<String>>();

		List<XMLParser> settingNodes = xmlParser.getNodes("setting");

		for(XMLParser setting : settingNodes){

			String id = setting.getString("id");

			if(StringUtils.isEmpty(id)){

				continue;
			}

			List<String> values = setting.getStrings("value");

			if(values != null){

				this.settingsMap.put(id, values);
			}
		}
	}

	public SimpleSettingHandler() {

		this.settingsMap = new ConcurrentHashMap<String, List<String>>(0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.handlers.SettingHandler#isSet(java.lang.String)
	 */
	@Override
	public boolean isSet(String id) {
		return settingsMap.containsKey(id);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.handlers.SettingHandler#getString(java.lang.String)
	 */
	@Override
	public String getString(String id) {

		List<String> values = this.settingsMap.get(id);

		if (values == null || values.isEmpty()) {
			return null;
		}

		return values.get(0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.handlers.SettingHandler#getInt(java.lang.String)
	 */
	@Override
	public Integer getInt(String id) {
		return NumberUtils.toInt(this.getString(id));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.handlers.SettingHandler#getLong(java.lang.String)
	 */
	@Override
	public Long getLong(String id) {
		return NumberUtils.toLong(this.getString(id));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.handlers.SettingHandler#getDouble(java.lang.String)
	 */
	@Override
	public Double getDouble(String id) {
		return NumberUtils.toDouble(this.getString(id));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see se.unlogic.hierarchy.core.handlers.SettingHandler#getBoolean(java.lang.String)
	 */
	@Override
	public Boolean getBoolean(String id) {

		String value = this.getString(id);

		if (value == null) {
			return null;
		}

		return Boolean.parseBoolean(value);
	}

	/**
	 * This metod is used to the values of the MutableSettingHandler. The input map values will be copied into a new {@link ConcurrentHashMap}.
	 * 
	 * @param settingsMap
	 */
	public void setSettingsMap(Map<String, List<String>> settingsMap) {

		if (settingsMap != null) {
			this.settingsMap = new ConcurrentHashMap<String, List<String>>(settingsMap);
		} else {
			this.settingsMap = new ConcurrentHashMap<String, List<String>>();
		}
	}

	@Override
	public boolean isEmpty() {
		return settingsMap.isEmpty();
	}

	@Override
	public Set<String> getIDs() {
		return new TreeSet<String>(settingsMap.keySet());
	}

	@Override
	public int size() {
		return settingsMap.size();
	}

	public Set<Entry<String, List<String>>> entrySet() {
		return settingsMap.entrySet();
	}

	@Override
	public List<Double> getDoubles(String id) {

		return NumberUtils.toDouble(this.settingsMap.get(id));
	}

	@Override
	public List<Integer> getInts(String id) {
		return NumberUtils.toInt(this.settingsMap.get(id));
	}

	@Override
	public List<Long> getLongs(String id) {
		return NumberUtils.toLong(this.settingsMap.get(id));
	}

	@Override
	public List<String> getStrings(String id) {
		return this.settingsMap.get(id);
	}

	@Override
	public boolean getPrimitiveBoolean(String id) {

		String value = this.getString(id);

		if (value == null) {
			return false;
		}

		return Boolean.parseBoolean(value);
	}

	@Override
	public Map<String, List<String>> getMap() {

		return new HashMap<String, List<String>>(this.settingsMap);
	}

	@Override
	public void removeSetting(String id) {

		this.settingsMap.remove(id);
	}

	@Override
	public void setSetting(String id, Object value) {

		if(value != null){

			this.settingsMap.put(id, Collections.singletonList(value.toString()));
		}else{

			this.settingsMap.remove(id);
		}
	}

	@Override
	public void setSetting(String id, List<?> values) {

		if(values != null && !values.isEmpty()) {

			ArrayList<String> stringList = new ArrayList<String>(values.size());

			for(Object object : values){

				if(object != null){

					stringList.add(object.toString());
				}
			}

			this.settingsMap.put(id, stringList);

		} else {

			this.settingsMap.remove(id);
		}
	}

	@Override
	public void clear() {

		this.settingsMap.clear();
	}

	@Override
	public Element toXML(Document doc) {

		Element settingsElement = doc.createElement("settings");

		for (Entry<String, List<String>> entry : this.settingsMap.entrySet()) {

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
	public void setSettings(Map<String, List<String>> settings) {

		this.settingsMap = new ConcurrentHashMap<String, List<String>>(settings);
	}

	@Override
	public void replaceSettings(HashMap<String, List<String>> settingValues) {

		this.settingsMap.putAll(settingValues);
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((settingsMap == null) ? 0 : settingsMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj){
			return true;
		}
		if(obj == null){
			return false;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		SimpleSettingHandler other = (SimpleSettingHandler)obj;
		if(settingsMap == null){
			if(other.settingsMap != null){
				return false;
			}
		}else if(!settingsMap.equals(other.settingsMap)){
			return false;
		}
		return true;
	}

	@Override
	public Short getShort(String id) {

		return NumberUtils.toShort(this.getString(id));
	}

	@Override
	public Float getFloat(String id) {

		return NumberUtils.toFloat(this.getString(id));
	}

	@Override
	public List<Float> getFloats(String id) {

		return NumberUtils.toFloat(this.settingsMap.get(id));
	}
}
