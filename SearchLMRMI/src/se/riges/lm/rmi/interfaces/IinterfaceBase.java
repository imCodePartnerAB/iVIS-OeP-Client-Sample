package se.riges.lm.rmi.interfaces;

import java.rmi.Remote;

/**
 * 
 * @author Per Fahlén - Sweco Position AB
 *
 *Interface for data parameter and return value for Estate over RMI
 *
 */
public interface IinterfaceBase extends Remote {

	public String getEstateName();
	
	public Integer getEstateID();
	
	public String getMunicipality();
	
}
