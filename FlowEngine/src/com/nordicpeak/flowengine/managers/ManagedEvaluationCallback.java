package com.nordicpeak.flowengine.managers;

import java.util.List;

import com.nordicpeak.flowengine.exceptions.queryinstance.IllegalQueryInstanceAccessException;
import com.nordicpeak.flowengine.interfaces.EvaluationCallback;
import com.nordicpeak.flowengine.interfaces.QueryInstance;

public class ManagedEvaluationCallback implements EvaluationCallback {

	private final List<ManagedStep> managedSteps;
	private int minStepIndex;
	private int minQueryIndex;

	public ManagedEvaluationCallback(List<ManagedStep> managedSteps, int minStepIndex, int minQueryIndex) {

		super();
		this.managedSteps = managedSteps;
		this.minStepIndex = minStepIndex;
		this.minQueryIndex = minQueryIndex;
	}

	@Override
	public QueryInstance getQueryInstance(Integer queryID) throws IllegalQueryInstanceAccessException {

		int stepIndex = 0;

		for(ManagedStep managedStep : managedSteps){

			int queryIndex = 0;

			for(ManagedQueryInstance managedQueryInstance : managedStep.getManagedQueryInstances()){

				if(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor().getQueryDescriptor().getQueryID().equals(queryID)){

					if(stepIndex < minStepIndex || (stepIndex == minStepIndex && queryIndex <= minQueryIndex)){

						throw new IllegalQueryInstanceAccessException(managedQueryInstance.getQueryInstance().getQueryInstanceDescriptor(),"Evaluators can only access query instances positioned after the current query in the flow");
					}

					return managedQueryInstance.getQueryInstance();
				}

				queryIndex++;
			}

			stepIndex++;
		}

		return null;
	}

	protected void incrementQueryIndex(){

		minQueryIndex++;
	}
}
