package se.riges.lm.rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;

import se.riges.lm.rmi.exceptions.LMAccountException;
import se.riges.lm.rmi.exceptions.LMUnavailableException;
import se.riges.lm.rmi.interfaces.IAddress;
import se.riges.lm.rmi.interfaces.IEstate;

/**
 * 
 * @author Per Fahlén - Sweco Position AB
 *  Interface for validating the address
 */
public interface ValidateLM extends Remote{

	public IAddress validateAddress(String address, String municipality) throws RemoteException, LMAccountException, LMUnavailableException;
	
	public IEstate validateEstate(String estateName, String municipality) throws RemoteException, LMAccountException, LMUnavailableException;
	
	public IEstate getEstateByCoordinate(double x, double y, String municipaliy) throws RemoteException, LMAccountException, LMUnavailableException;	
	
}
