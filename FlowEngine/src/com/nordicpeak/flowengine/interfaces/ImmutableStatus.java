package com.nordicpeak.flowengine.interfaces;

import java.util.List;

import com.nordicpeak.flowengine.enums.ContentType;

public interface ImmutableStatus {

	public Integer getStatusID();

	public String getName();

	public String getDescription();
	
	public Integer getManagingTime();
	
	public ImmutableFlow getFlow();

	public List<? extends ImmutableFlowInstance> getFlowInstances();

	public boolean isUserMutable();
	
	public boolean isUserDeletable();

	public boolean isAdminMutable();

	public boolean isAdminDeletable();
	
	public ContentType getContentType();

	public List<? extends ImmutableDefaultStatusMapping> getDefaulStatusMappings();

}