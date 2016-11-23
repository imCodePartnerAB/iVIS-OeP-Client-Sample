package com.nordicpeak.flowengine.interfaces;

import javax.servlet.http.HttpServletRequest;

import com.nordicpeak.flowengine.managers.ImmutableFlowInstanceManager;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.webutils.http.URIParser;


public interface MultiSigningProvider {

	ViewFragment getSigningStatus(HttpServletRequest req, User user, URIParser uriParser, ImmutableFlowInstanceManager immutableFlowInstanceManager) throws Exception;;
	
}
