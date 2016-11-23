package se.riges.lm.rmi.beans;

import java.io.Serializable;

import se.riges.lm.rmi.interfaces.IPlacename;

public class Placename implements IPlacename, Serializable {

	static final long serialVersionUID = -1861605790751137787L;

	private String name;
	private String municipality;
	private String geometry;
	
	public Placename(){}
	
	public Placename(String name, String municipality, String geometry){
		this.name = name;
		this.municipality = municipality;
		this.geometry = geometry;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getMunicipality() {
		// TODO Auto-generated method stub
		return this.municipality;
	}

	@Override
	public String getGeometry() {
		return this.geometry;
	}

}
