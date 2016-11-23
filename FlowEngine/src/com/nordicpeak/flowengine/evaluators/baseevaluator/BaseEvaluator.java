package com.nordicpeak.flowengine.evaluators.baseevaluator;

import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLParserPopulateable;

import com.nordicpeak.flowengine.beans.EvaluationResponse;
import com.nordicpeak.flowengine.interfaces.EvaluationCallback;
import com.nordicpeak.flowengine.interfaces.EvaluationHandler;
import com.nordicpeak.flowengine.interfaces.Evaluator;
import com.nordicpeak.flowengine.interfaces.MutableEvaluatorDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryInstance;


public abstract class BaseEvaluator extends GeneratedElementable implements Evaluator, XMLParserPopulateable {

	private static final long serialVersionUID = -8708777700485841600L;

	@XMLElement
	protected String configURL;

	@XMLElement
	protected MutableEvaluatorDescriptor evaluatorDescriptor;

	public void init(MutableEvaluatorDescriptor evaluatorDescriptor, String configURL) {

		this.configURL = configURL;
		this.evaluatorDescriptor = evaluatorDescriptor;
	}

	@Override
	public MutableEvaluatorDescriptor getEvaluatorDescriptor() {

		return evaluatorDescriptor;
	}

	@Override
	public String getConfigAlias() {

		return configURL;
	}

	@Override
	public EvaluationResponse evaluate(QueryInstance queryInstance, EvaluationCallback callback, EvaluationHandler evaluationHandler) {

		return BaseEvaluatorUtils.getGenericEvaluationProvider(this.getClass(), evaluationHandler, evaluatorDescriptor.getEvaluatorTypeID()).evaluate(queryInstance, this, callback);
	}

	public abstract Integer getEvaluatorID();
}
