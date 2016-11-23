package com.nordicpeak.flowengine.accesscontrollers;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.utils.AccessUtils;

import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.interfaces.FlowInstanceAccessController;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowInstance;


public class ManagerFlowInstanceAccessController implements FlowInstanceAccessController {

	private boolean requireMutableState;
	private boolean requireDeletableState;

	public ManagerFlowInstanceAccessController(boolean requireMutableState, boolean requireDeletableState) {

		super();
		this.requireMutableState = requireMutableState;
		this.requireDeletableState = requireDeletableState;
	}


	@Override
	public void checkNewFlowInstanceAccess(Flow flow, User user) throws AccessDeniedException {}

	@Override
	public void checkFlowInstanceAccess(ImmutableFlowInstance flowInstance, User user) throws AccessDeniedException {

		if(!AccessUtils.checkAccess(user, flowInstance.getFlow().getFlowFamily())){

			throw new AccessDeniedException("User is not manager for flow family " + flowInstance.getFlow().getFlowFamily());

		}else if(requireMutableState && !flowInstance.getStatus().isAdminMutable()){

			throw new AccessDeniedException("Access denied to flow instance " + flowInstance + ", the requested instance is not in a manager mutable state.");

		}else if(requireDeletableState && !flowInstance.getStatus().isAdminDeletable()){

			throw new AccessDeniedException("Access denied to flow instance " + flowInstance + ", the requested instance is not in a manager deletable state.");
		}
	}

	@Override
	public boolean isMutable(ImmutableFlowInstance flowInstance, User user) {

		return flowInstance.getStatus().isAdminMutable();
	}
}
