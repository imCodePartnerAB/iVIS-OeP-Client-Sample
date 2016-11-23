package se.unlogic.hierarchy.core.beans;

import java.io.Serializable;

import se.unlogic.hierarchy.core.interfaces.MutableAttribute;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

public class SimpleAttribute extends GeneratedElementable implements Serializable, MutableAttribute{

	private static final long serialVersionUID = -8624741311877325627L;

	@DAOManaged
	@Key
	@XMLElement(fixCase=true)
	protected String name;

	@DAOManaged
	@XMLElement(fixCase=true)
	protected String value;

	public SimpleAttribute(){}

	public SimpleAttribute(String name, String value){

		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getValue() {

		return value;
	}

	@Override
	public void setValue(String value) {

		this.value = value;
	}
}
