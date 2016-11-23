package com.nordicpeak.flowengine.interfaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.validation.ValidationError;

import com.nordicpeak.flowengine.beans.PDFQueryResponse;
import com.nordicpeak.flowengine.beans.QueryResponse;

public interface ImmutableQueryInstance {

	public QueryResponse getShowHTML(HttpServletRequest req, User user, QueryHandler queryHandler, String updateURL, String queryRequestURL) throws Throwable;

	public QueryResponse getFormHTML(HttpServletRequest req, User user, List<ValidationError> validationErrors, QueryHandler queryHandler, boolean enableAjaxPosting, String queryRequestURL) throws Throwable;

	public PDFQueryResponse getPDFContent(QueryHandler queryHandler) throws Throwable;

	public ImmutableQueryInstanceDescriptor getQueryInstanceDescriptor();

	public QueryRequestProcessor getQueryRequestProcessor(HttpServletRequest req, User user, QueryHandler queryHandler) throws Exception;

	public Element toExportXML(Document doc, QueryHandler queryHandler) throws Exception;
}