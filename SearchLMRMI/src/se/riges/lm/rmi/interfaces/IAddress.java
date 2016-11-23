package se.riges.lm.rmi.interfaces;


/**
 * 
 * @author Per Fahlén - Sweco Position AB
 * Interface for data parameter and return value validating addresses
 */
public interface IAddress extends IinterfaceBase {
	
	public String getAdress();
	public String getPostAdress();
	public String getZipCode();
	public String getSub_municipality();
	public String getAdress_area();
	
	
	
	
	
}
