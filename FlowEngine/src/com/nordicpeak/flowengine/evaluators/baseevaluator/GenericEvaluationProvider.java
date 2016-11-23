package com.nordicpeak.flowengine.evaluators.baseevaluator;

import com.nordicpeak.flowengine.beans.EvaluationResponse;
import com.nordicpeak.flowengine.interfaces.EvaluationCallback;
import com.nordicpeak.flowengine.interfaces.EvaluationProvider;
import com.nordicpeak.flowengine.interfaces.QueryInstance;



public interface GenericEvaluationProvider<E extends BaseEvaluator> extends EvaluationProvider{

	public EvaluationResponse evaluate(QueryInstance queryInstance, E evaluator, EvaluationCallback callback);

}
