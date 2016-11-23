package com.nordicpeak.flowengine.queries.contactdetailquery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToUpdateUserException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.object.ObjectUtils;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.TooLongContentValidationError;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationErrorType;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLGenerator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;
import se.unlogic.webutils.url.URLRewriter;
import se.unlogic.webutils.validation.ValidationUtils;

import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.InstanceMetadata;
import com.nordicpeak.flowengine.interfaces.MutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryCRUDCallback;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryProviderModule;
import com.nordicpeak.flowengine.utils.JTidyUtils;
import com.nordicpeak.flowengine.utils.TextTagReplacer;

public class ContactDetailQueryProviderModule extends BaseQueryProviderModule<ContactDetailQueryInstance> implements BaseQueryCRUDCallback {

	private static final EmailPopulator EMAIL_POPULATOR = new EmailPopulator();

	private AnnotatedDAO<ContactDetailQuery> queryDAO;
	private AnnotatedDAO<ContactDetailQueryInstance> queryInstanceDAO;

	private ContactDetailQueryCRUD queryCRUD;

	private QueryParameterFactory<ContactDetailQuery, Integer> queryIDParamFactory;
	private QueryParameterFactory<ContactDetailQueryInstance, Integer> queryInstanceIDParamFactory;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		// Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, ContactDetailQueryProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		queryDAO = daoFactory.getDAO(ContactDetailQuery.class);
		queryInstanceDAO = daoFactory.getDAO(ContactDetailQueryInstance.class);

		queryCRUD = new ContactDetailQueryCRUD(queryDAO.getWrapper(Integer.class), new AnnotatedRequestPopulator<ContactDetailQuery>(ContactDetailQuery.class), "ContactDetailQuery", "query", null, this);

		queryIDParamFactory = queryDAO.getParamFactory("queryID", Integer.class);
		queryInstanceIDParamFactory = queryInstanceDAO.getParamFactory("queryInstanceID", Integer.class);
	}

	@Override
	public Query createQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws SQLException {

		ContactDetailQuery query = new ContactDetailQuery();

		query.setQueryID(descriptor.getQueryID());

		this.queryDAO.add(query, transactionHandler, null);

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query importQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		ContactDetailQuery query = new ContactDetailQuery();

		query.setQueryID(descriptor.getQueryID());

		query.populate(descriptor.getImportParser().getNode(XMLGenerator.getElementName(query.getClass())));

		this.queryDAO.add(query, transactionHandler, null);

		return query;
	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor) throws SQLException {

		ContactDetailQuery query = this.getQuery(descriptor.getQueryID());

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		ContactDetailQuery query = this.getQuery(descriptor.getQueryID(), transactionHandler);

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public QueryInstance getQueryInstance(MutableQueryInstanceDescriptor descriptor, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws SQLException {

		ContactDetailQueryInstance queryInstance;

		// Check if we should create a new instance or get an existing one
		if (descriptor.getQueryInstanceID() == null) {

			queryInstance = new ContactDetailQueryInstance();

			if (user != null) {

				queryInstance.initialize(user);
			}

		} else {

			queryInstance = getQueryInstance(descriptor.getQueryInstanceID());

			if (queryInstance == null) {

				return null;
			}
		}

		queryInstance.setQuery(getQuery(descriptor.getQueryDescriptor().getQueryID()));

		if (queryInstance.getQuery() == null) {

			return null;
		}

		if(req != null){

			FCKUtils.setAbsoluteFileUrls(queryInstance.getQuery(), RequestUtils.getFullContextPathURL(req) + ckConnectorModuleAlias);

			URLRewriter.setAbsoluteLinkUrls(queryInstance.getQuery(), req, true);
		}

		TextTagReplacer.replaceTextTags(queryInstance.getQuery(), instanceMetadata.getSiteProfile());

		queryInstance.set(descriptor);

		return queryInstance;
	}

	private ContactDetailQuery getQuery(Integer queryID) throws SQLException {

		HighLevelQuery<ContactDetailQuery> query = new HighLevelQuery<ContactDetailQuery>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query);
	}

	private ContactDetailQuery getQuery(Integer queryID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<ContactDetailQuery> query = new HighLevelQuery<ContactDetailQuery>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query, transactionHandler);
	}

	private ContactDetailQueryInstance getQueryInstance(Integer queryInstanceID) throws SQLException {

		HighLevelQuery<ContactDetailQueryInstance> query = new HighLevelQuery<ContactDetailQueryInstance>(ContactDetailQueryInstance.QUERY_RELATION);

		query.addParameter(queryInstanceIDParamFactory.getParameter(queryInstanceID));

		return queryInstanceDAO.get(query);
	}

	@Override
	public void save(ContactDetailQueryInstance queryInstance, TransactionHandler transactionHandler) throws Throwable {

		if (queryInstance.getQueryInstanceID() == null || !queryInstance.getQueryInstanceID().equals(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID())) {

			queryInstance.setQueryInstanceID(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID());

			this.queryInstanceDAO.add(queryInstance, transactionHandler, null);

		} else {

			this.queryInstanceDAO.update(queryInstance, transactionHandler, null);
		}
	}

	@Override
	public void populate(ContactDetailQueryInstance queryInstance, HttpServletRequest req, User user, boolean allowPartialPopulation, MutableAttributeHandler attributeHandler) throws ValidationException {

		StringPopulator stringPopulator = StringPopulator.getPopulator();

		List<ValidationError> errors = new ArrayList<ValidationError>();

		Integer queryID = queryInstance.getQuery().getQueryID();

		boolean contactBySMS = req.getParameter("q" + queryID + "_contactBySMS") != null;
		boolean persistUserProfile = req.getParameter("q" + queryID + "_persistUserProfile") != null;

		boolean requireAddressFields = !allowPartialPopulation && queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED && queryInstance.getQuery().requiresAddress();

		String address = ValidationUtils.validateParameter("q" + queryID + "_address", req, requireAddressFields, stringPopulator, errors);
		String zipCode = ValidationUtils.validateParameter("q" + queryID + "_zipcode", req, requireAddressFields, stringPopulator, errors);
		String postalAddress = ValidationUtils.validateParameter("q" + queryID + "_postaladdress", req, requireAddressFields, stringPopulator, errors);
		String mobilePhone = ValidationUtils.validateParameter("q" + queryID + "_mobilephone", req, contactBySMS, stringPopulator, errors);
		String email = ValidationUtils.validateParameter("q" + queryID + "_email", req, !queryInstance.getQuery().isAllowSMS() && !allowPartialPopulation, EMAIL_POPULATOR, errors);
		String phone = ValidationUtils.validateParameter("q" + queryID + "_phone", req, false, stringPopulator, errors);

		String firstname;
		String lastname;

		if (user != null) {

			if(queryInstance.getQueryInstanceDescriptor().getQueryState() != QueryState.VISIBLE_REQUIRED && ObjectUtils.isNull(address, zipCode, postalAddress, mobilePhone, phone, req.getParameter("q" + queryID + "_email"))){

				queryInstance.reset(attributeHandler);
				return;
			}

			firstname = user.getFirstname();
			lastname = user.getLastname();
		} else {
			firstname = ValidationUtils.validateParameter("q" + queryID + "_firstname", req, true, stringPopulator, errors);
			lastname = ValidationUtils.validateParameter("q" + queryID + "_lastname", req, true, stringPopulator, errors);

			if(queryInstance.getQueryInstanceDescriptor().getQueryState() != QueryState.VISIBLE_REQUIRED && ObjectUtils.isNull(address, zipCode, postalAddress, firstname, lastname, mobilePhone, phone, req.getParameter("q" + queryID + "_email"))){

				queryInstance.reset(attributeHandler);
				return;
			}
		}

		if(!requireAddressFields){

			if (StringUtils.isEmpty(address) && (queryInstance.getQuery().requiresAddress() || (!StringUtils.isEmpty(zipCode) || !StringUtils.isEmpty(postalAddress)))) {
				errors.add(new ValidationError("q" + queryID + "_address", ValidationErrorType.RequiredField));
			}

			if (StringUtils.isEmpty(zipCode) && (queryInstance.getQuery().requiresAddress() || (!StringUtils.isEmpty(address) || !StringUtils.isEmpty(postalAddress)))) {
				errors.add(new ValidationError("q" + queryID + "_zipcode", ValidationErrorType.RequiredField));
			}

			if (StringUtils.isEmpty(postalAddress) && (queryInstance.getQuery().requiresAddress() || (!StringUtils.isEmpty(address) || !StringUtils.isEmpty(zipCode)))) {
				errors.add(new ValidationError("q" + queryID + "_postaladdress", ValidationErrorType.RequiredField));
			}
		}

		this.validateFieldLength("q" + queryID + "_firstname", firstname, 255, errors);
		this.validateFieldLength("q" + queryID + "_lastname", lastname, 255, errors);
		this.validateFieldLength("q" + queryID + "_address", address, 255, errors);
		this.validateFieldLength("q" + queryID + "_zipcode", zipCode, 10, errors);
		this.validateFieldLength("q" + queryID + "_postaladdress", postalAddress, 255, errors);
		this.validateFieldLength("q" + queryID + "_mobilephone", mobilePhone, 255, errors);
		this.validateFieldLength("q" + queryID + "_email", email, 255, errors);
		this.validateFieldLength("q" + queryID + "_phone", phone, 255, errors);

		if (queryInstance.getQuery().isAllowSMS() && !allowPartialPopulation && queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED && !contactBySMS && email == null) {
			errors.add(new ValidationError("NoContactChannelChoosen"));
		}

		if (!errors.isEmpty()) {

			throw new ValidationException(errors);
		}

		queryInstance.setContactBySMS(contactBySMS);

		queryInstance.setFirstname(firstname);
		queryInstance.setLastname(lastname);
		queryInstance.setAddress(address);
		queryInstance.setZipCode(zipCode);
		queryInstance.setPostalAddress(postalAddress);
		queryInstance.setMobilePhone(mobilePhone);
		queryInstance.setEmail(email);
		queryInstance.setPhone(phone);
		queryInstance.setPersistUserProfile(persistUserProfile);
		queryInstance.getQueryInstanceDescriptor().setPopulated(queryInstance.isPopulated());

		if (user != null && user instanceof MutableUser && persistUserProfile) {

			MutableUser mutableUser = (MutableUser) user;

			if (email != null) {

				User emailMatch = systemInterface.getUserHandler().getUserByEmail(email, false, false);

				if (emailMatch != null && !emailMatch.equals(mutableUser)) {

					errors.add(new ValidationError("EmailAlreadyTaken"));

				} else {

					mutableUser.setEmail(email);

				}
			}

			MutableAttributeHandler userAttributeHandler = mutableUser.getAttributeHandler();

			if (userAttributeHandler != null) {

				setAttributeValue("address", address, userAttributeHandler);
				setAttributeValue("zipCode", zipCode, userAttributeHandler);
				setAttributeValue("postalAddress", postalAddress, userAttributeHandler);
				setAttributeValue("mobilePhone", mobilePhone, userAttributeHandler);
				setAttributeValue("phone", phone, userAttributeHandler);
				setAttributeValue("contactBySMS", contactBySMS, userAttributeHandler);
			}

			if (!errors.isEmpty()) {

				throw new ValidationException(errors);

			} else {

				try {

					log.info("User " + user + " updating user profile");

					req.getSession(true).setAttribute("user", user);

					this.systemInterface.getUserHandler().updateUser(mutableUser, false, false, userAttributeHandler != null);

				} catch (UnableToUpdateUserException e) {

					throw new ValidationException(new ValidationError("UnableToUpdateUser"));

				}

			}

		}

	}

	private void setAttributeValue(String name, Object value, MutableAttributeHandler attributeHandler) {

		if (value != null) {

			attributeHandler.setAttribute(name, value);

		} else {

			attributeHandler.removeAttribute(name);

		}

	}

	private void validateFieldLength(String fieldName, String field, Integer maxLength, List<ValidationError> errors) {

		if (field != null && field.length() > maxLength) {

			errors.add(new TooLongContentValidationError(fieldName, field.length(), maxLength));
		}

	}

	@WebPublic(alias = "config")
	public ForegroundModuleResponse configureQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.queryCRUD.update(req, res, user, uriParser);
	}

	@Override
	public boolean deleteQuery(ImmutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws SQLException {

		ContactDetailQuery query = getQuery(descriptor.getQueryID());

		if (query == null) {

			return false;
		}

		this.queryDAO.delete(query, transactionHandler);

		return true;
	}

	@Override
	public boolean deleteQueryInstance(ImmutableQueryInstanceDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		ContactDetailQueryInstance queryInstance = this.getQueryInstance(descriptor.getQueryInstanceID());

		if (queryInstance == null) {

			return false;
		}

		this.queryInstanceDAO.delete(queryInstance, transactionHandler);

		return true;
	}

	@Override
	public Document createDocument(HttpServletRequest req, User user) {

		Document doc = super.createDocument(req, user);

		if (user != null) {
			doc.getDocumentElement().appendChild(user.toXML(doc));
		}

		return doc;
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	@Override
	public void copyQuery(MutableQueryDescriptor sourceQueryDescriptor, MutableQueryDescriptor copyQueryDescriptor, TransactionHandler transactionHandler) throws SQLException {

		ContactDetailQuery query = getQuery(sourceQueryDescriptor.getQueryID(), transactionHandler);

		query.setQueryID(copyQueryDescriptor.getQueryID());

		queryDAO.add(query, transactionHandler, null);
	}

	@Override
	protected void appendPDFData(Document doc, Element showQueryValuesElement, ContactDetailQueryInstance queryInstance) {

		super.appendPDFData(doc, showQueryValuesElement, queryInstance);

		if (queryInstance.getQuery().getDescription() != null) {

			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "Description", JTidyUtils.getXHTML(queryInstance.getQuery().getDescription()));
			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "isHTMLDescription", queryInstance.getQuery().getDescription().contains("<") && queryInstance.getQuery().getDescription().contains(">"));
		}
	}
}
