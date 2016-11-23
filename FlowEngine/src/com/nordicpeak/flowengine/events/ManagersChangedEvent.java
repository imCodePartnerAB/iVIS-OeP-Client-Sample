package com.nordicpeak.flowengine.events;

import java.io.Serializable;
import java.util.List;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.openhierarchy.foregroundmodules.siteprofile.interfaces.SiteProfile;

import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.beans.FlowInstanceEvent;

public class ManagersChangedEvent implements Serializable {

	private static final long serialVersionUID = -3105656362605519708L;

	private final FlowInstance flowInstance;
	private final FlowInstanceEvent event;
	private final ForegroundModuleDescriptor moduleDescriptor;
	private final SiteProfile siteProfile;
	private final List<User> previousManagers;
	private final User user;

	public ManagersChangedEvent(FlowInstance flowInstance, FlowInstanceEvent event, ForegroundModuleDescriptor moduleDescriptor, SiteProfile siteProfile, List<User> previousManagers, User user) {

		super();
		this.flowInstance = flowInstance;
		this.event = event;
		this.moduleDescriptor = moduleDescriptor;
		this.siteProfile = siteProfile;
		this.previousManagers = previousManagers;
		this.user = user;
	}

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

	public FlowInstance getFlowInstance() {

		return flowInstance;
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

	public List<User> getPreviousManagers() {

		return previousManagers;
	}

	public User getUser() {

		return user;
	}
}
