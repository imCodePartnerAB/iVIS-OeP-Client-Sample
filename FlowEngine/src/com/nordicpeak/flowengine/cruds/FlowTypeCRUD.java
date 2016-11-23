package com.nordicpeak.flowengine.cruds;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.serialization.SerializationUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

import com.nordicpeak.flowengine.FlowAdminModule;
import com.nordicpeak.flowengine.beans.FlowType;


public class FlowTypeCRUD extends IntegerBasedCRUD<FlowType, FlowAdminModule> {

	public FlowTypeCRUD(CRUDDAO<FlowType, Integer> crudDAO, FlowAdminModule callback) {

		super(crudDAO, new AnnotatedRequestPopulator<FlowType>(FlowType.class), "FlowType", "flow type", "/flowtypes", callback);
	}

	@Override
	public FlowType getBean(Integer beanID, String getMode) throws SQLException, AccessDeniedException {

		if(getMode != null && (getMode == FlowCRUD.SHOW || getMode == FlowCRUD.DELETE)){

			return callback.getCachedFlowType(beanID);

		}else{

			FlowType flowType = callback.getCachedFlowType(beanID);

			if(flowType == null){

				return null;
			}

			flowType = SerializationUtils.cloneSerializable(flowType);

			return flowType;
		}
	}

	@Override
	protected List<FlowType> getAllBeans(User user) throws SQLException {

		ArrayList<FlowType> filteredFlowtypes = new ArrayList<FlowType>(callback.getCachedFlowTypes());

		//Filter flow types if user not admin for this module
		if(!AccessUtils.checkAccess(user, callback)){

			Iterator<FlowType> iterator = filteredFlowtypes.iterator();

			FlowType flowType;

			while(iterator.hasNext()){

				flowType = iterator.next();

				if(!AccessUtils.checkAccess(user, flowType)){

					iterator.remove();
				}
			}
		}

		return filteredFlowtypes;
	}

	@Override
	protected String getBeanName(FlowType bean) {

		return bean.getName();
	}

	@Override
	protected void checkAddAccess(User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		checkModificationAccess(user);
	}

	@Override
	protected void checkUpdateAccess(FlowType bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		checkModificationAccess(user);
	}

	@Override
	protected void checkDeleteAccess(FlowType bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		checkModificationAccess(user);
	}

	public void checkModificationAccess(User user) throws AccessDeniedException{

		if(!AccessUtils.checkAccess(user, callback)){

			throw new AccessDeniedException("User does not have access to administrate flow types.");
		}
	}

	@Override
	protected void checkShowAccess(FlowType bean, User user, HttpServletRequest req, URIParser uriParser) throws AccessDeniedException, URINotFoundException, SQLException {

		if(!AccessUtils.checkAccess(user, bean)){

			throw new AccessDeniedException("User does not have access to requested flow type.");
		}
	}

	@Override
	protected ForegroundModuleResponse beanAdded(FlowType bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		callback.getEventHandler().sendEvent(FlowType.class, new CRUDEvent<FlowType>(CRUDAction.ADD, bean), EventTarget.ALL);

		res.sendRedirect(req.getContextPath() + callback.getFullAlias() + "/flowtype/" + bean.getFlowTypeID());

		return null;
	}

	@Override
	protected ForegroundModuleResponse beanUpdated(FlowType bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		callback.getEventHandler().sendEvent(FlowType.class, new CRUDEvent<FlowType>(CRUDAction.UPDATE, bean), EventTarget.ALL);

		res.sendRedirect(req.getContextPath() + callback.getFullAlias() + "/flowtype/" + bean.getFlowTypeID());

		return null;
	}

	@Override
	protected ForegroundModuleResponse beanDeleted(FlowType bean, HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		callback.getEventHandler().sendEvent(FlowType.class, new CRUDEvent<FlowType>(CRUDAction.DELETE, bean), EventTarget.ALL);

		return super.beanDeleted(bean, req, res, user, uriParser);
	}

	@Override
	protected void appendShowFormData(FlowType bean, Document doc, Element showTypeElement, User user, HttpServletRequest req, HttpServletResponse res, URIParser uriParser) throws SQLException, IOException, Exception {

		appendAdminAccess(user, doc, showTypeElement);

		if(bean.getAllowedGroupIDs() != null){

			XMLUtils.append(doc, showTypeElement, "AllowedGroups", callback.getGroupHandler().getGroups(bean.getAllowedGroupIDs(), false));
		}

		if(bean.getAllowedUserIDs() != null){

			XMLUtils.append(doc, showTypeElement, "AllowedUsers", callback.getUserHandler().getUsers(bean.getAllowedUserIDs(), false, true));
		}

		if(bean.getAllowedQueryTypes() != null){

			XMLUtils.append(doc, showTypeElement, "QueryTypeDescriptors", callback.getQueryHandler().getQueryTypes(bean.getAllowedQueryTypes()));
		}

		XMLUtils.appendNewElement(doc, showTypeElement, "flowFamilyCount", callback.getFlowFamilies(bean.getFlowTypeID()).size());
	}

	@Override
	protected void appendListFormData(Document doc, Element listTypeElement, User user, HttpServletRequest req, URIParser uriParser, List<ValidationError> validationErrors) throws SQLException {

		appendAdminAccess(user, doc, listTypeElement);
	}

	private void appendAdminAccess(User user, Document doc, Element element){

		if(AccessUtils.checkAccess(user, callback)){

			XMLUtils.appendNewElement(doc, element, "AdminAccess");
		}
	}

	@Override
	protected void appendAllBeans(Document doc, Element listTypeElement, User user, HttpServletRequest req, URIParser uriParser, List<ValidationError> validationErrors) throws SQLException {

		List<FlowType> flowTypes = getAllBeans(user, req, uriParser);

		if(CollectionUtils.isEmpty(flowTypes)){

			return;
		}

		Element flowTypesElement = doc.createElement(this.typeElementPluralName);
		listTypeElement.appendChild(flowTypesElement);

		for(FlowType flowType : flowTypes){

			Element flowTypeElement = flowType.toXML(doc);
			flowTypesElement.appendChild(flowTypeElement);

			XMLUtils.appendNewElement(doc, flowTypeElement, "flowFamilyCount", callback.getFlowFamilies(flowType.getFlowTypeID()).size());
		}
	}

	@Override
	protected void appendAddFormData(Document doc, Element addTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws Exception {

		appendFormData(doc, addTypeElement, user, req, uriParser);
	}

	@Override
	protected void appendUpdateFormData(FlowType bean, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws Exception {

		appendFormData(doc, updateTypeElement, user, req, uriParser);

		if(bean.getAllowedGroupIDs() != null){

			XMLUtils.append(doc, updateTypeElement, "AllowedGroups", callback.getGroupHandler().getGroups(bean.getAllowedGroupIDs(), false));
		}

		if(bean.getAllowedUserIDs() != null){

			XMLUtils.append(doc, updateTypeElement, "AllowedUsers", callback.getUserHandler().getUsers(bean.getAllowedUserIDs(), false, true));
		}
	}

	private void appendFormData(Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) {

		XMLUtils.append(doc, updateTypeElement, "QueryTypeDescriptors", callback.getQueryHandler().getAvailableQueryTypes());


	}
}
