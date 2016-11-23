/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ViewFragment;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public abstract class GenericFormCRUD<BeanType extends Elementable, UserType extends User, ModuleCallback extends ForegroundModule, FormCallback> {

	protected Logger log = Logger.getLogger(GenericFormCRUD.class);
	protected BeanRequestPopulator<BeanType> populator;
	protected String typeElementName;
	protected String typeElementPluralName;
	protected String typeLogName;
	protected String typeLogPluralName;
	protected ModuleCallback moduleCallback;
	protected ViewFragmentTransformer viewTransformer;

	public GenericFormCRUD(BeanRequestPopulator<BeanType> populator, String typeElementName, String typeLogName, ModuleCallback moduleCallback, ViewFragmentTransformer viewTransformer) {
		this(populator, typeElementName, typeElementName + "s", typeLogName, typeLogName + "s", moduleCallback, viewTransformer);
	}

	public GenericFormCRUD(BeanRequestPopulator<BeanType> populator, String typeElementName, String typeElementPluralName, String typeLogName, String typeLogPluralName, ModuleCallback moduleCallback, ViewFragmentTransformer viewTransformer) {
		super();
		this.populator = populator;
		this.typeElementName = typeElementName;
		this.typeElementPluralName = typeElementPluralName;
		this.typeLogName = typeLogName;
		this.typeLogPluralName = typeLogPluralName;
		this.moduleCallback = moduleCallback;
		this.viewTransformer = viewTransformer;
	}

	public ViewFragment showAddForm(HttpServletRequest req,  UserType user, URIParser uriParser, ValidationException validationException, FormCallback formCallback) throws Exception {

		log.info("User " + user + " requested add " + this.typeLogName + " form");

		Document doc = createDocument(req, uriParser, user);
		Element addTypeElement = doc.createElement("Add" + typeElementName);
		doc.getFirstChild().appendChild(addTypeElement);

		if (validationException != null) {
			addTypeElement.appendChild(validationException.toXML(doc));
			addTypeElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		this.appendAddFormData(doc, addTypeElement, user, req, uriParser, formCallback);

		return createViewFragment(doc);
	}

	public BeanType populateFromAddRequest(HttpServletRequest req, UserType user, URIParser uriParser, FormCallback formCallback) throws Exception {

		BeanType bean = this.getPopulator(req).populate(req);

		validateAddPopulation(bean, req, user, uriParser, formCallback);

		return bean;
	}

	protected void appendAddFormData(Document doc, Element addTypeElement, UserType user, HttpServletRequest req, URIParser uriParser, FormCallback formCallback) throws Exception {}

	protected void validateAddPopulation(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser, FormCallback formCallback) throws ValidationException, SQLException, Exception {}

	public ViewFragment showUpdateForm(BeanType bean, HttpServletRequest req,  UserType user, URIParser uriParser, ValidationException validationException, FormCallback formCallback) throws Exception {

		log.info("User " + user + " requested update " + this.typeLogName + " form for " + bean);

		Document doc = createDocument(req, uriParser, user);
		Element updateTypeElement = doc.createElement("Update" + typeElementName);
		doc.getFirstChild().appendChild(updateTypeElement);

		appendBean(bean, updateTypeElement, doc);

		if (validationException != null) {
			updateTypeElement.appendChild(validationException.toXML(doc));
			updateTypeElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		this.appendUpdateFormData(bean, doc, updateTypeElement, user, req, uriParser, formCallback);

		return createViewFragment(doc);
	}

	protected void appendBean(BeanType bean, Element targetElement, Document doc) {

		targetElement.appendChild(bean.toXML(doc));
	}

	public BeanType populateFromUpdateRequest(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser, FormCallback formCallback) throws Exception {

		bean = getPopulator(req).populate(bean, req);

		validateUpdatePopulation(bean, req, user, uriParser, formCallback);

		return bean;
	}

	protected void appendUpdateFormData(BeanType bean, Document doc, Element updateTypeElement, UserType user, HttpServletRequest req, URIParser uriParser, FormCallback formCallback) throws Exception {}

	protected void validateUpdatePopulation(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser, FormCallback formCallback) throws ValidationException, SQLException, Exception {}

	public BeanRequestPopulator<BeanType> getPopulator(HttpServletRequest req) {
		return populator;
	}

	public void setPopulator(BeanRequestPopulator<BeanType> populator) {

		if (populator == null) {
			throw new NullPointerException("Populator cannot be null!");
		}

		this.populator = populator;
	}

	public ViewFragment showBean(BeanType bean, HttpServletRequest req,  UserType user, URIParser uriParser, ValidationError validationError, FormCallback formCallback) throws Exception {

		log.info("User " + user + " viewing " + this.typeLogName + " " + bean);

		Document doc = createDocument(req, uriParser, user);
		Element showTypeElement = doc.createElement("Show" + typeElementName);
		doc.getFirstChild().appendChild(showTypeElement);

		this.appendBean(bean, showTypeElement, doc);

		this.appendShowFormData(bean, doc, showTypeElement, user, req, uriParser, formCallback);

		if (validationError != null) {
			showTypeElement.appendChild(validationError.toXML(doc));
			showTypeElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		return createViewFragment(doc);
	}

	protected void appendShowFormData(BeanType bean, Document doc, Element showTypeElement, UserType user, HttpServletRequest req,  URIParser uriParser, FormCallback formCallback) throws SQLException, IOException, Exception {}

	protected ViewFragment createViewFragment(Document doc) throws TransformerConfigurationException, TransformerException {

		return viewTransformer.createViewFragment(doc);
	}

	protected Document createDocument(HttpServletRequest req, URIParser uriParser, UserType user) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		doc.appendChild(document);

		return doc;
	}
}
