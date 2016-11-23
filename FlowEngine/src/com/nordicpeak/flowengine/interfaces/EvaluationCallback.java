package com.nordicpeak.flowengine.interfaces;

import com.nordicpeak.flowengine.exceptions.queryinstance.IllegalQueryInstanceAccessException;


public interface EvaluationCallback {

	public QueryInstance getQueryInstance(Integer queryID) throws IllegalQueryInstanceAccessException;
}
