package com.nordicpeak.flowengine.queries.basequery;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLUtils;

import com.nordicpeak.flowengine.beans.PDFQueryResponse;
import com.nordicpeak.flowengine.beans.QueryResponse;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.interfaces.QueryRequestProcessor;



public abstract class BaseQueryInstance extends GeneratedElementable implements QueryInstance{

	private static final long serialVersionUID = -4177738017399293462L;

	@XMLElement
	protected MutableQueryInstanceDescriptor queryInstanceDescriptor;

	public void set(MutableQueryInstanceDescriptor mutableQueryInstanceDescriptor) {

		this.queryInstanceDescriptor = mutableQueryInstanceDescriptor;
	}

	@Override
	public MutableQueryInstanceDescriptor getQueryInstanceDescriptor() {

		return queryInstanceDescriptor;
	}

	@Override
	public void populate(HttpServletRequest req, User user, boolean allowPartialPopulation, QueryHandler queryHandler, MutableAttributeHandler attributeHandler) throws ValidationException {

		BaseQueryUtils.getGenericQueryInstanceProvider(this.getClass(), queryHandler, queryInstanceDescriptor.getQueryDescriptor().getQueryTypeID()).populate(this, req, user, allowPartialPopulation, attributeHandler);
	}

	@Override
	public void save(TransactionHandler transactionHandler, QueryHandler queryHandler) throws Throwable {

		BaseQueryUtils.getGenericQueryInstanceProvider(this.getClass(), queryHandler, queryInstanceDescriptor.getQueryDescriptor().getQueryTypeID()).save(this, transactionHandler);
	}

	@Override
	public QueryResponse getShowHTML(HttpServletRequest req, User user, QueryHandler queryHandler, String updateURL, String queryRequestURL) throws Throwable {

		return BaseQueryUtils.getGenericQueryInstanceProvider(this.getClass(), queryHandler, queryInstanceDescriptor.getQueryDescriptor().getQueryTypeID()).getShowHTML(this, req, user, updateURL, queryRequestURL);
	}

	@Override
	public QueryResponse getFormHTML(HttpServletRequest req, User user, List<ValidationError> validationErrors, QueryHandler queryHandler, boolean enableAjaxPosting, String queryRequestURL) throws Throwable {

		return BaseQueryUtils.getGenericQueryInstanceProvider(this.getClass(), queryHandler, queryInstanceDescriptor.getQueryDescriptor().getQueryTypeID()).getFormHTML(this, req, user, validationErrors, enableAjaxPosting, queryRequestURL);
	}

	@Override
	public void close(QueryHandler queryHandler){}

	@Override
	public void reset(MutableAttributeHandler attributeHandler) {

		if(this.queryInstanceDescriptor != null){
			this.queryInstanceDescriptor.setPopulated(false);
		}
	}

	@Override
	public QueryRequestProcessor getQueryRequestProcessor(HttpServletRequest req, User user, QueryHandler queryHandler) throws Exception {

		return null;
	}

	@Override
	public PDFQueryResponse getPDFContent(QueryHandler queryHandler) throws Throwable {

		return BaseQueryUtils.getGenericQueryInstanceProvider(this.getClass(), queryHandler, queryInstanceDescriptor.getQueryDescriptor().getQueryTypeID()).getPDFContent(this);
	}

	public Element getBaseExportXML(Document doc) throws Exception{

		Element queryInstanceElement = doc.createElement(queryInstanceDescriptor.getQueryDescriptor().getXSDElementName());

		XMLUtils.appendNewElement(doc, queryInstanceElement, "QueryID", queryInstanceDescriptor.getQueryDescriptor().getQueryID());
		XMLUtils.appendNewCDATAElement(doc, queryInstanceElement, "Name", queryInstanceDescriptor.getQueryDescriptor().getName());

		return queryInstanceElement;
	}
}
