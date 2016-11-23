package com.nordicpeak.flowengine.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;

import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;


public interface SigningProvider {

	ViewFragment sign(HttpServletRequest req, HttpServletResponse res, User user, MutableFlowInstanceManager instanceManager, SigningCallback signingCallback, boolean savedDuringCurrentRequest) throws Exception;

}
