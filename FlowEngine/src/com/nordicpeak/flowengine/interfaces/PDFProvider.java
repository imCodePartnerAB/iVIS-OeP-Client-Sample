package com.nordicpeak.flowengine.interfaces;

import java.io.File;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;

import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;


public interface PDFProvider {

	File getPDF(Integer flowInstanceID, Integer eventID);
	
	File getTemporaryPDF(Integer flowInstanceID);

	File createTemporaryPDF(FlowInstanceManager instanceManager, boolean signed, SiteProfile profile, User user) throws Exception;
	
	public boolean saveTemporaryPDF(Integer flowInstanceID, FlowInstanceEvent flowInstanceEvent) throws Exception;
	
	public boolean deleteTemporaryPDF(Integer flowInstanceID) throws Exception;
	
	public boolean hasTemporaryPDF(Integer flowInstanceID);
}
