package com.nordicpeak.flowengine.interfaces;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;

import com.nordicpeak.flowengine.managers.ImmutableFlowInstanceManager;
import com.nordicpeak.flowengine.managers.MutableFlowInstanceManager;


public interface PaymentProvider {

	ViewFragment pay(HttpServletRequest req, HttpServletResponse res, User user, MutableFlowInstanceManager instanceManager, InlinePaymentCallback callback) throws Exception;
	
	ViewFragment pay(HttpServletRequest req, HttpServletResponse res, User user, ImmutableFlowInstanceManager instanceManager, StandalonePaymentCallback callback) throws Exception;
	
}

