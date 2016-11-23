package se.riges.lm.rmi.beans;
/**
 * 
 */


import java.io.Serializable;
import java.rmi.RemoteException;


/**
 * @author Per Fahlén - Sweco Position AB
 *
 */
public class Estate implements se.riges.lm.rmi.interfaces.IEstate, Serializable{

	private static final long serialVersionUID = -8680154513611318582L;
	
	public Estate() throws RemoteException {
		
	}
	
	public Estate(String estateName, String geometry, String municipality, Integer estateID){
		this.estateName = estateName;
		this.geometry = geometry;
		this.municipality = municipality;
		this.estateID = estateID;
	}

	private String estateName, geometry, municipality;
	private Integer estateID;
	
	@Override
	public String getEstateName(){
		return this.estateName;
	}


	@Override
	public Integer getEstateID() {
		return this.estateID;
	}


	@Override
	public String getGeometry() {
		return this.geometry;
	}


	@Override
	public String getMunicipality() {
		return municipality;
	}
}
