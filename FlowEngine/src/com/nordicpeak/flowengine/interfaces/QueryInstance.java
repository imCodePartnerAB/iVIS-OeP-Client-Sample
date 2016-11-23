package com.nordicpeak.flowengine.interfaces;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.validation.ValidationException;

public interface QueryInstance extends ImmutableQueryInstance, Serializable {

	public void populate(HttpServletRequest req, User user, boolean allowPartialPopulation, QueryHandler queryHandler, MutableAttributeHandler attributeHandler) throws ValidationException;

	public void save(TransactionHandler transactionHandler, QueryHandler queryHandler) throws Throwable;

	public void reset(MutableAttributeHandler attributeHandler);

	@Override
	public MutableQueryInstanceDescriptor getQueryInstanceDescriptor();

	public void close(QueryHandler queryHandler);
}
