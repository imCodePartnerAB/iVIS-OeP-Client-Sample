package com.nordicpeak.flowengine.interfaces;

import java.sql.SQLException;

import se.unlogic.standardutils.dao.TransactionHandler;

import com.nordicpeak.flowengine.beans.EvaluatorTypeDescriptor;


public interface EvaluationProvider {

	/**
	 * @return The query type that this class provides
	 */
	public EvaluatorTypeDescriptor getEvaluatorType();

	public Evaluator createEvaluator(MutableEvaluatorDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable;
	
	public Evaluator importEvaluator(MutableEvaluatorDescriptor descriptor, TransactionHandler transactionHandler, Query query) throws Throwable;

	public Evaluator getEvaluator(MutableEvaluatorDescriptor descriptor) throws Throwable;

	public boolean deleteEvaluator(ImmutableEvaluatorDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable;

	public boolean supportsQueryType(Class<? extends Query> queryClass);

	public void copyEvaluation(MutableEvaluatorDescriptor sourceEvaluatorDescriptor, MutableEvaluatorDescriptor copyEvaluatorDescriptor, Query sourceQuery, Query copyQuery, TransactionHandler transactionHandler) throws SQLException;
}
