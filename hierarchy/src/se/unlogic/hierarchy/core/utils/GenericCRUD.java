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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.Breadcrumb;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.URLType;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public abstract class GenericCRUD<BeanType extends Elementable, IDType, UserType extends User, CallbackType extends CRUDCallback<UserType>> {

	public static final String SHOW = "show";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";

	protected Logger log = Logger.getLogger(GenericCRUD.class);
	protected CRUDDAO<BeanType, IDType> crudDAO;
	protected BeanRequestPopulator<BeanType> populator;
	protected String typeElementName;
	protected String typeElementPluralName;
	protected String typeLogName;
	protected String typeLogPluralName;
	protected CallbackType callback;
	protected String listMethodAlias;

	//Strings used for breadcrumbs and titles
	protected String addTextPrefix = "";
	protected String updateTextPrefix = "";
	protected String showTextPrefix = "";
	protected String listTextPrefix = null;

	public GenericCRUD(CRUDDAO<BeanType, IDType> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeLogName, String listMethodAlias, CallbackType callback) {
		this(crudDAO, populator, typeElementName, typeElementName + "s", typeLogName, typeLogName + "s", listMethodAlias, callback);
	}

	public GenericCRUD(CRUDDAO<BeanType, IDType> crudDAO, BeanRequestPopulator<BeanType> populator, String typeElementName, String typeElementPluralName, String typeLogName, String typeLogPluralName, String listMethodAlias, CallbackType callback) {
		super();
		this.crudDAO = crudDAO;
		this.populator = populator;
		this.typeElementName = typeElementName;
		this.typeElementPluralName = typeElementPluralName;
		this.typeLogName = typeLogName;
		this.typeLogPluralName = typeLogPluralName;
		this.callback = callback;
		this.listMethodAlias = listMethodAlias;
	}

	public ForegroundModuleResponse add(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		try{
			ValidationException validationException = null;

			try {
				req = parseRequest(req, user);
				
				this.checkAddAccess(user,req,uriParser);

				if (req.getMethod().equalsIgnoreCase("POST")) {

					BeanType bean = this.populateFromAddRequest(req, user, uriParser);

					this.validateAddPopulation(bean, req, user, uriParser);

					log.info("User " + user + " adding " + this.typeLogName + " " + bean);

					this.addBean(bean, req, user, uriParser);

					return this.beanAdded(bean, req, res, user, uriParser);
				}

			} catch (ValidationException e) {

				validationException = e;
			}

			return showAddForm(req,res,user,uriParser,validationException);

		}finally{

			releaseRequest(req, user);
		}
	}

	public ForegroundModuleResponse showAddForm(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, ValidationException validationException) throws Exception {

		log.info("User " + user + " requested add " + this.typeLogName + " form");

		Document doc = this.callback.createDocument(req, uriParser, user);
		Element addTypeElement = doc.createElement("Add" + typeElementName);
		doc.getFirstChild().appendChild(addTypeElement);

		if (validationException != null) {
			addTypeElement.appendChild(validationException.toXML(doc));
			addTypeElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		this.appendAddFormData(doc, addTypeElement, user, req, uriParser);

		SimpleForegroundModuleResponse moduleResponse = createAddFormModuleResponse(doc, req, user, uriParser);

		moduleResponse.addBreadcrumbsLast(this.getAddBreadcrumbs(req, user, uriParser));

		return moduleResponse;
	}

	/**
	 * This method is used for parsing special requests such as multipart requests where the request object is replaced by another implementation
	 *
	 * @param req
	 * @param user
	 * @return
	 */
	protected HttpServletRequest parseRequest(HttpServletRequest req, UserType user) throws ValidationException, Exception{

		return req;
	}

	/**
	 * This method is used as trigger to indicate that the CRUD is finished with a request previously parsed by the {@parseRequest} method
	 *
	 * @param req
	 * @param user
	 */
	protected void releaseRequest(HttpServletRequest req, UserType user) {}

	/**
	 * This methods is used for appending default breadscrumbs shared between all CRUD operation (add, update, delete, show). Please note that the bean parameter is null for add operations
	 *
	 * @param breadcrumbs
	 * @param bean
	 * @throws Exception 
	 */
	protected void appendDefaultBreadcrumbs(List<Breadcrumb> breadcrumbs, BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception{

		breadcrumbs.add(callback.getDefaultBreadcrumb());
	}

	protected List<Breadcrumb> getAddBreadcrumbs(HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>(2);

		appendDefaultBreadcrumbs(breadcrumbs, null, req, user, uriParser);

		String prefix = getAddTextPrefix();

		if(prefix != null){

			breadcrumbs.add(new Breadcrumb(prefix, uriParser.getFormattedURI(), URLType.RELATIVE_FROM_CONTEXTPATH));
		}

		return breadcrumbs;
	}

	/**
	 * If this method returns null no breadcrumb apart from the default breadscrumbs will be added during add operations unless the {@link GenericCRUD#getAddBreadcrumbs(HttpServletRequest, HttpServletResponse, User, URIParser)} method is overridden.
	 *
	 * @return
	 */
	protected String getAddTextPrefix() {

		return addTextPrefix;
	}

	protected void checkAddAccess(UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {}

	protected ForegroundModuleResponse beanAdded(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		redirectToListMethod(req, res, bean);

		return null;
	}

	protected BeanType populateFromAddRequest(HttpServletRequest req, UserType user, URIParser uriParser) throws ValidationException, Exception {

		return this.getPopulator(req).populate(req);
	}

	protected void addBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		this.crudDAO.add(bean);
	}

	protected void appendAddFormData(Document doc, Element addTypeElement, UserType user, HttpServletRequest req, URIParser uriParser) throws Exception {}

	protected void validateAddPopulation(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws ValidationException, SQLException, Exception {}

	public ForegroundModuleResponse update(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		try {
			ValidationException validationException = null;

			try {
				req = parseRequest(req, user);
				
			} catch (ValidationException parseException) {

				//Try to get bean anyway so we can show the update form
				BeanType bean;
				
				try {
					bean = this.getRequestedBean(req, res, user, uriParser, UPDATE);
					
					if(bean != null){
						
						return showUpdateForm(bean, req, res, user, uriParser, validationException);
					}					
					
				} catch (Exception getBeanException) {}

				return list(req, res, user, uriParser, parseException.getErrors());
			}

			BeanType bean = this.getRequestedBean(req, res, user, uriParser, UPDATE);

			if (bean != null) {

				try {
					this.checkUpdateAccess(bean, user, req, uriParser);

					if (req.getMethod().equalsIgnoreCase("POST")) {

						bean = this.populateFromUpdateRequest(bean, req, user, uriParser);

						this.validateUpdatePopulation(bean, req, user, uriParser);

						log.info("User " + user + " updating " + typeLogName + " " + bean);

						this.updateBean(bean, req, user, uriParser);

						return this.beanUpdated(bean, req, res, user, uriParser);
					}

				} catch (ValidationException e) {

					validationException = e;
				}

				return showUpdateForm(bean, req, res, user, uriParser, validationException);

			} else {
				return list(req, res, user, uriParser, Collections.singletonList(new ValidationError("UpdateFailed" + typeElementName + "NotFound")));
			}

		} finally {

			releaseRequest(req, user);
		}
	}

	public ForegroundModuleResponse showUpdateForm(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, ValidationException validationException) throws Exception {

		log.info("User " + user + " requested update " + this.typeLogName + " form for " + bean);

		Document doc = this.callback.createDocument(req, uriParser, user);
		Element updateTypeElement = doc.createElement("Update" + typeElementName);
		doc.getFirstChild().appendChild(updateTypeElement);

		appendBean(bean, updateTypeElement, doc, user);

		if (validationException != null) {
			updateTypeElement.appendChild(validationException.toXML(doc));
			updateTypeElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		this.appendUpdateFormData(bean, doc, updateTypeElement, user, req, uriParser);

		SimpleForegroundModuleResponse moduleResponse = createUpdateFormModuleResponse(bean, doc, req, user, uriParser);

		moduleResponse.addBreadcrumbsLast(getUpdateBreadcrumbs(bean,req, user, uriParser));

		return moduleResponse;
	}

	protected void appendBean(BeanType bean, Element targetElement, Document doc, UserType user) {

		targetElement.appendChild(bean.toXML(doc));
	}

	protected String getListTitle(HttpServletRequest req, UserType user, URIParser uriParser) {

		String callbackTitle = callback.getTitlePrefix();

		if(callbackTitle == null){

			return listTextPrefix;

		}else{

			if(listTextPrefix == null){

				return callbackTitle;

			}else{

				return callbackTitle + " " + listTextPrefix;
			}
		}
	}

	protected String getShowTitle(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) {

		String callbackTitle = callback.getTitlePrefix();

		if(callbackTitle == null){

			return showTextPrefix + getBeanName(bean);

		}else{

			return callbackTitle + " " + showTextPrefix + getBeanName(bean);
		}
	}

	protected String getAddTitle(HttpServletRequest req, UserType user, URIParser uriParser) {

		String callbackTitle = callback.getTitlePrefix();

		if(callbackTitle == null){

			return addTextPrefix;

		}else{

			return callbackTitle + " " + addTextPrefix;
		}
	}

	private String getUpdateTitle(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) {

		String callbackTitle = callback.getTitlePrefix();

		if(callbackTitle == null){

			return updateTextPrefix + getBeanName(bean);

		}else{

			return callbackTitle + " " + updateTextPrefix + getBeanName(bean);
		}
	}

	protected List<Breadcrumb> getUpdateBreadcrumbs(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>(3);

		appendDefaultBreadcrumbs(breadcrumbs, bean, req, user, uriParser);

		breadcrumbs.add(new Breadcrumb(getUpdateTextPrefix() + getBeanName(bean), uriParser.getFormattedURI(), URLType.RELATIVE_FROM_CONTEXTPATH));

		return breadcrumbs;
	}

	protected String getUpdateTextPrefix() {

		return updateTextPrefix;
	}

	protected List<Breadcrumb> getShowBreadcrumbs(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>(3);

		appendDefaultBreadcrumbs(breadcrumbs, bean, req, user, uriParser);

		breadcrumbs.add(new Breadcrumb(getShowTextPrefix() + getBeanName(bean), uriParser.getFormattedURI(), URLType.RELATIVE_FROM_CONTEXTPATH));

		return breadcrumbs;
	}

	protected String getShowTextPrefix() {

		return showTextPrefix;
	}

	protected String getBeanName(BeanType bean) {

		return bean.toString();
	}

	protected void checkUpdateAccess(BeanType bean, UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException{

	}

	protected ForegroundModuleResponse beanUpdated(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		redirectToListMethod(req, res, bean);

		return null;
	}

	protected void updateBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {
		this.crudDAO.update(bean);
	}

	protected BeanType populateFromUpdateRequest(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws ValidationException, Exception {

		return getPopulator(req).populate(bean, req);
	}

	protected void appendUpdateFormData(BeanType bean, Document doc, Element updateTypeElement, UserType user, HttpServletRequest req, URIParser uriParser) throws Exception {}

	protected void validateUpdatePopulation(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws ValidationException, SQLException, Exception {}

	/**
	 * @param req
	 * @param res
	 * @param user
	 * @param uriParser
	 * @param getMode used to signal for which purpose the bean is being requested. For example an update or delete operation.
	 * @return
	 * @throws SQLException
	 * @throws AccessDeniedException
	 */
	public abstract BeanType getRequestedBean(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, String getMode) throws SQLException, AccessDeniedException;

	public ForegroundModuleResponse delete(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		try{
			req = parseRequest(req, user);

			BeanType bean = this.getRequestedBean(req, res, user, uriParser, DELETE);

			if (bean != null) {

				this.checkDeleteAccess(bean,user,req,uriParser);

				log.info("User " + user + " deleting " + this.typeLogName + " " + bean);

				this.deleteBean(bean, req, user, uriParser);

				return this.beanDeleted(bean, req, res, user, uriParser);

			} else {

				return list(req, res, user, uriParser, Collections.singletonList(new ValidationError("DeleteFailed" + typeElementName + "NotFound")));
			}

		}finally{

			releaseRequest(req, user);
		}
	}

	protected void checkDeleteAccess(BeanType bean, UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException{
	}

	protected ForegroundModuleResponse beanDeleted(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		redirectToListMethod(req, res, bean);

		return null;
	}

	protected void deleteBean(BeanType bean, HttpServletRequest req, UserType user, URIParser uriParser) throws Exception {

		this.crudDAO.delete(bean);
	}

	public CRUDDAO<BeanType, IDType> getCrudDAO() {
		return crudDAO;
	}

	public void setCrudDAO(CRUDDAO<BeanType, IDType> crudDAO) {

		if (crudDAO == null) {
			throw new NullPointerException("CRUDDAO cannot be null!");
		}

		this.crudDAO = crudDAO;
	}

	public BeanRequestPopulator<BeanType> getPopulator(HttpServletRequest req) {
		return populator;
	}

	public void setPopulator(BeanRequestPopulator<BeanType> populator) {

		if (populator == null) {
			throw new NullPointerException("Populator cannot be null!");
		}

		this.populator = populator;
	}
	
	public ForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		this.checkListAccess(user,req,uriParser);

		log.info("User " + user + " listing " + this.typeLogPluralName);

		Document doc = this.callback.createDocument(req, uriParser, user);
		Element listTypeElement = doc.createElement("List" + this.typeElementPluralName);
		doc.getFirstChild().appendChild(listTypeElement);

		this.appendAllBeans(doc, listTypeElement, user, req, uriParser, validationErrors);

		if (validationErrors != null) {
			XMLUtils.append(doc, listTypeElement, validationErrors);
			listTypeElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		this.appendListFormData(doc, listTypeElement, user, req, uriParser, validationErrors);

		SimpleForegroundModuleResponse moduleResponse = createListModuleResponse(doc, req, user, uriParser);

		moduleResponse.addBreadcrumbsLast(getListBreadcrumbs(req, user, uriParser,validationErrors));

		return moduleResponse;
	}

	public ForegroundModuleResponse show(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser) throws Exception {

		return show(req, res, user, uriParser, null);
	}

	public ForegroundModuleResponse show(HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, ValidationError validationError) throws Exception {

		BeanType bean = this.getRequestedBean(req, res, user, uriParser, SHOW);

		if (bean != null) {

			return showBean(bean, req, res, user, uriParser, validationError);

		} else {

			return list(req, res, user, uriParser, Collections.singletonList(new ValidationError("ShowFailed" + typeElementName + "NotFound")));
		}
	}

	public ForegroundModuleResponse showBean(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, ValidationError validationError) throws Exception {

		if(validationError != null){

			return showBean(bean, req, res, user, uriParser, Collections.singletonList(validationError));
		}

		return showBean(bean, req, res, user, uriParser, (List<ValidationError>)null);
	}

	public ForegroundModuleResponse showBean(BeanType bean, HttpServletRequest req, HttpServletResponse res, UserType user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		this.checkShowAccess(bean,user,req,uriParser);

		log.info("User " + user + " viewing " + this.typeLogName + " " + bean);

		Document doc = this.callback.createDocument(req, uriParser, user);
		Element showTypeElement = doc.createElement("Show" + typeElementName);
		doc.getFirstChild().appendChild(showTypeElement);

		this.appendBean(bean, showTypeElement, doc, user);

		this.appendShowFormData(bean, doc, showTypeElement, user, req, res, uriParser);

		if(res.isCommitted()){

			return null;
		}

		if (validationErrors != null) {
			XMLUtils.append(doc, showTypeElement, validationErrors);
			showTypeElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		SimpleForegroundModuleResponse moduleResponse = createShowBeanModuleResponse(bean, doc, req, user, uriParser);

		List<Breadcrumb> breadcrumbs = getShowBreadcrumbs(bean,req, user, uriParser);

		if(breadcrumbs != null){

			moduleResponse.addBreadcrumbsLast(breadcrumbs);
		}

		return moduleResponse;
	}

	protected void appendShowFormData(BeanType bean, Document doc, Element showTypeElement, UserType user, HttpServletRequest req, HttpServletResponse res, URIParser uriParser) throws SQLException, IOException, Exception {}

	protected void checkShowAccess(BeanType bean, UserType user, HttpServletRequest req, URIParser uriParser)  throws AccessDeniedException, URINotFoundException, SQLException {}

	protected void checkListAccess(UserType user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {}

	protected List<BeanType> getAllBeans(UserType user) throws SQLException {

		return this.crudDAO.getAll();
	}

	protected List<BeanType> getAllBeans(UserType user, HttpServletRequest req, URIParser uriParser) throws SQLException {

		return getAllBeans(user);
	}

	protected void appendAllBeans(Document doc, Element listTypeElement, UserType user, HttpServletRequest req, URIParser uriParser, List<ValidationError> validationError) throws SQLException {

		XMLUtils.append(doc, listTypeElement, this.typeElementPluralName, getAllBeans(user, req, uriParser));

	}

	protected List<Breadcrumb> getListBreadcrumbs(HttpServletRequest req, UserType user, URIParser uriParser, List<ValidationError> validationError) throws Exception {

		List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>(2);

		appendDefaultBreadcrumbs(breadcrumbs, null, req, user, uriParser);

		String prefix = getListTextPrefix();

		if(prefix != null){

			breadcrumbs.add(new Breadcrumb(prefix, uriParser.getFormattedURI(), URLType.RELATIVE_FROM_CONTEXTPATH));
		}

		return breadcrumbs;
	}

	protected void appendListFormData(Document doc, Element listTypeElement, UserType user, HttpServletRequest req, URIParser uriParser, List<ValidationError> validationError) throws SQLException {}

	protected void redirectToListMethod(HttpServletRequest req, HttpServletResponse res, BeanType bean) throws Exception {

		res.sendRedirect(req.getContextPath() + callback.getFullAlias() + listMethodAlias);
	}
	
	protected SimpleForegroundModuleResponse createListModuleResponse(Document doc, HttpServletRequest req, UserType user, URIParser uriParser) {
		
		return new SimpleForegroundModuleResponse(doc, getListTitle(req, user, uriParser));
	}

	protected SimpleForegroundModuleResponse createShowBeanModuleResponse(BeanType bean, Document doc, HttpServletRequest req, UserType user, URIParser uriParser) {
		
		return new SimpleForegroundModuleResponse(doc, getShowTitle(bean, req, user, uriParser));
	}
	
	protected SimpleForegroundModuleResponse createAddFormModuleResponse(Document doc, HttpServletRequest req, UserType user, URIParser uriParser) {
		
		return new SimpleForegroundModuleResponse(doc, this.getAddTitle(req, user, uriParser));
	}
	
	protected SimpleForegroundModuleResponse createUpdateFormModuleResponse(BeanType bean, Document doc, HttpServletRequest req, UserType user, URIParser uriParser) {
		
		return new SimpleForegroundModuleResponse(doc, getUpdateTitle(bean, req, user, uriParser));
	}
	
	public void setShowTextPrefix(String showBreadcrumbPrefix) {

		this.showTextPrefix = showBreadcrumbPrefix;
	}


	public void setAddTextPrefix(String addBreadcrumbPrefix) {

		this.addTextPrefix = addBreadcrumbPrefix;
	}


	public void setUpdateTextPrefix(String updateBreadcrumbPrefix) {

		this.updateTextPrefix = updateBreadcrumbPrefix;
	}


	public String getListTextPrefix() {

		return listTextPrefix;
	}


	public void setListTextPrefix(String listTextPrefix) {

		this.listTextPrefix = listTextPrefix;
	}
}
