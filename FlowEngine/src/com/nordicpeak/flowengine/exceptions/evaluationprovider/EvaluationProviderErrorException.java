package com.nordicpeak.flowengine.exceptions.evaluationprovider;

import com.nordicpeak.flowengine.interfaces.EvaluationProvider;

public class EvaluationProviderErrorException extends EvaluationProviderException {

	private static final long serialVersionUID = 7228972782831540190L;

	public EvaluationProviderErrorException(String message, Throwable cause, EvaluationProvider queryProvider) {

		super(message, cause, queryProvider);

	}
}
