package com.nordicpeak.flowengine.integration.callback;

import java.util.Date;

public class IntegrationCallbackService {

	public void confirmDelivery(Integer flowInstanceID, String externalID, boolean delivered, String logMessage) throws FlowInstanceNotFoundException, AccessDeniedException{}
	
	public int addMessage(Integer flowInstanceID, String externalID, IntegrationMessage message, Principal principal) throws FlowInstanceNotFoundException, AccessDeniedException{
		
		return 1;
	}
	
	public int setStatus(Integer flowInstanceID, String externalID, int statusID, String statusAlias, Principal principal) throws FlowInstanceNotFoundException, StatusNotFoundException, AccessDeniedException{
		
		return 1;
	}
	
	public int addEvent(Integer flowInstanceID, String externalID, Date date, String message, Principal principal) throws FlowInstanceNotFoundException, AccessDeniedException{
		
		return 1;
	}
	
	public int setManagers(Integer flowInstanceID, String externalID, Principal[] managers) throws FlowInstanceNotFoundException, AccessDeniedException{
		
		return 1;
	}	
	
	public void deleteInstance(Integer flowInstanceID, String externalID, String logMessage) throws FlowInstanceNotFoundException, AccessDeniedException {};
}
