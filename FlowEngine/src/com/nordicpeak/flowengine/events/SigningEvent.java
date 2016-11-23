package com.nordicpeak.flowengine.events;

import java.io.Serializable;

import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;

import com.nordicpeak.flowengine.beans.FlowInstanceEvent;
import com.nordicpeak.flowengine.managers.FlowInstanceManager;

public class SigningEvent implements Serializable {

	private static final long serialVersionUID = -383039162159917192L;

	protected final FlowInstanceManager flowInstanceManager;
	protected final FlowInstanceEvent event;
	protected final ForegroundModuleDescriptor moduleDescriptor;
	protected final SiteProfile siteProfile;
	protected final String actionID;

	public SigningEvent(FlowInstanceManager flowInstanceManager, FlowInstanceEvent event, ForegroundModuleDescriptor moduleDescriptor, SiteProfile siteProfile, String actionID) {

		super();
		this.flowInstanceManager = flowInstanceManager;
		this.event = event;
		this.moduleDescriptor = moduleDescriptor;
		this.siteProfile = siteProfile;
		this.actionID = actionID;
	}

	public FlowInstanceManager getFlowInstanceManager() {

		return flowInstanceManager;
	}

	public FlowInstanceEvent getEvent() {

		return event;
	}

	public ForegroundModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	public SiteProfile getSiteProfile() {

		return siteProfile;
	}

	public String getActionID() {

		return actionID;
	}

}
