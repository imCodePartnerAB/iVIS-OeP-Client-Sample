package com.nordicpeak.flowengine.interfaces;

import java.util.List;

import se.unlogic.hierarchy.core.interfaces.AccessInterface;

public interface ImmutableFlowType extends AccessInterface{

	public Integer getFlowTypeID();

	public String getName();

	public List<? extends ImmutableCategory> getCategories();

	public List<? extends ImmutableFlow> getFlows();

	public List<String> getAllowedQueryTypes();

}