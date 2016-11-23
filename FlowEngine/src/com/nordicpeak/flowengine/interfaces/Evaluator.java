package com.nordicpeak.flowengine.interfaces;

import java.io.Serializable;

import se.unlogic.standardutils.xml.Elementable;

import com.nordicpeak.flowengine.beans.EvaluationResponse;



public interface Evaluator extends Serializable, Elementable{

	public ImmutableEvaluatorDescriptor getEvaluatorDescriptor();

	public String getConfigAlias();

	public EvaluationResponse evaluate(QueryInstance queryInstance, EvaluationCallback callback, EvaluationHandler evaluationHandler);
}
