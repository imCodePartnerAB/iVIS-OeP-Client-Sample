package se.unlogic.standardutils.rmi;

import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;


public class RMIUtils {

	public static String getClientHost(){
		
		try {
			return RemoteServer.getClientHost();
		} catch (ServerNotActiveException e) {
			throw new RuntimeException(e);
		}
	}
}
