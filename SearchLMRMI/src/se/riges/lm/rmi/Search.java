package se.riges.lm.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

import se.riges.lm.rmi.exceptions.LMAccountException;
import se.riges.lm.rmi.exceptions.LMUnavailableException;
import se.riges.lm.rmi.interfaces.IAddress;
import se.riges.lm.rmi.interfaces.IEstate;
import se.riges.lm.rmi.interfaces.IPlacename;

public interface Search extends Remote {
	
	public LinkedList<IPlacename> getPlaceName(int municaplityCode, String placename, String municaplity) throws RemoteException, LMAccountException, LMUnavailableException;
	
	public LinkedList<IEstate> getEstate(String estate, String municaplity) throws RemoteException, LMAccountException, LMUnavailableException;
	
	public LinkedList<IAddress> getAddress(String address, String municaplity) throws RemoteException, LMAccountException, LMUnavailableException;
}
