package com.nordicpeak.flowengine.beans;

import java.util.List;

import se.unlogic.standardutils.validation.ValidationError;

public class EvaluationResponse {

	private final List<QueryModification> modifications;
	private final List<ValidationError> validationErrors;

	public EvaluationResponse(List<QueryModification> modifications, List<ValidationError> validationErrors) {

		super();
		this.modifications = modifications;
		this.validationErrors = validationErrors;
	}

	public List<QueryModification> getModifications() {

		return modifications;
	}

	public List<ValidationError> getValidationErrors() {

		return validationErrors;
	}
}
