package com.nordicpeak.flowengine.queries.manualmultisignquery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.populators.SocialSecurityPopulator;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;
import se.unlogic.webutils.validation.ValidationUtils;

import com.nordicpeak.flowengine.beans.QueryDescriptor;
import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.InstanceMetadata;
import com.nordicpeak.flowengine.interfaces.MutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryProviderModule;
import com.nordicpeak.flowengine.utils.TextTagReplacer;


public class ManualMultiSignQueryProviderModule extends BaseQueryProviderModule<ManualMultiSignQueryInstance> {

	private static final EmailPopulator EMAIL_POPULATOR = new EmailPopulator();
	private static final SocialSecurityPopulator SOCIAL_SECURITY_NUMBER_POPULATOR = new SocialSecurityPopulator();

	private static final RelationQuery SAVE_QUERY_INSTANCE_RELATION_QUERY = new RelationQuery(ManualMultiSignQueryInstance.SIGNING_PARTIES_RELATION);

	private AnnotatedDAO<ManualMultiSignQuery> queryDAO;
	private AnnotatedDAO<ManualMultiSignQueryInstance> queryInstanceDAO;

	private QueryParameterFactory<ManualMultiSignQuery, Integer> queryIDParamFactory;
	private QueryParameterFactory<ManualMultiSignQueryInstance, Integer> queryInstanceIDParamFactory;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		//Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, ManualMultiSignQueryProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		queryDAO = daoFactory.getDAO(ManualMultiSignQuery.class);
		queryInstanceDAO = daoFactory.getDAO(ManualMultiSignQueryInstance.class);

		queryIDParamFactory = queryDAO.getParamFactory("queryID", Integer.class);
		queryInstanceIDParamFactory = queryInstanceDAO.getParamFactory("queryInstanceID", Integer.class);
	}

	@Override
	public Query createQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		ManualMultiSignQuery query = new ManualMultiSignQuery();

		query.setQueryID(descriptor.getQueryID());

		this.queryDAO.add(query, transactionHandler, null);

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor) throws Throwable {

		ManualMultiSignQuery query = this.getQuery(descriptor.getQueryID());

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		ManualMultiSignQuery query = this.getQuery(descriptor.getQueryID());

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public QueryInstance getQueryInstance(MutableQueryInstanceDescriptor descriptor, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws Throwable {

		ManualMultiSignQueryInstance queryInstance = null;

		//Check if we should create a new instance or get an existing one
		if (descriptor.getQueryInstanceID() == null) {

			queryInstance = new ManualMultiSignQueryInstance();

		} else {

			queryInstance = getQueryInstance(descriptor.getQueryInstanceID());

			if (queryInstance == null) {

				return null;
			}
		}

		queryInstance.setQuery(getQuery(descriptor.getQueryDescriptor().getQueryID()));

		if(queryInstance.getQuery() == null){

			return null;
		}

		if(req != null){

			FCKUtils.setAbsoluteFileUrls(queryInstance.getQuery(), RequestUtils.getFullContextPathURL(req) + ckConnectorModuleAlias);

			URLRewriter.setAbsoluteLinkUrls(queryInstance.getQuery(), req, true);
		}

		TextTagReplacer.replaceTextTags(queryInstance.getQuery(), instanceMetadata.getSiteProfile());

		queryInstance.set(descriptor);

		//If this is a new query instance copy the default values
		if(descriptor.getQueryInstanceID() == null){

			queryInstance.copyQueryValues();
		}

		return queryInstance;
	}

	private ManualMultiSignQuery getQuery(Integer queryID) throws SQLException {

		HighLevelQuery<ManualMultiSignQuery> query = new HighLevelQuery<ManualMultiSignQuery>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query);
	}

	private ManualMultiSignQuery getQuery(Integer queryID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<ManualMultiSignQuery> query = new HighLevelQuery<ManualMultiSignQuery>();

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query, transactionHandler);
	}

	private ManualMultiSignQueryInstance getQueryInstance(Integer queryInstanceID) throws SQLException {

		HighLevelQuery<ManualMultiSignQueryInstance> query = new HighLevelQuery<ManualMultiSignQueryInstance>(ManualMultiSignQueryInstance.SIGNING_PARTIES_RELATION, ManualMultiSignQueryInstance.QUERY_RELATION);

		query.addParameter(queryInstanceIDParamFactory.getParameter(queryInstanceID));

		return queryInstanceDAO.get(query);
	}

	@Override
	public boolean deleteQuery(ImmutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		ManualMultiSignQuery query = getQuery(descriptor.getQueryID());

		if (query == null) {

			return false;
		}

		this.queryDAO.delete(query, transactionHandler);

		return true;
	}

	@Override
	public boolean deleteQueryInstance(ImmutableQueryInstanceDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		ManualMultiSignQueryInstance queryInstance = this.getQueryInstance(descriptor.getQueryInstanceID());

		if (queryInstance == null) {

			return false;
		}

		this.queryInstanceDAO.delete(queryInstance, transactionHandler);

		return true;
	}

	@Override
	public void copyQuery(MutableQueryDescriptor sourceQueryDescriptor, MutableQueryDescriptor copyQueryDescriptor, TransactionHandler transactionHandler) throws SQLException {

		ManualMultiSignQuery query = getQuery(sourceQueryDescriptor.getQueryID(), transactionHandler);

		query.setQueryID(copyQueryDescriptor.getQueryID());


		this.queryDAO.add(query, transactionHandler, null);
	}

	@Override
	public void save(ManualMultiSignQueryInstance queryInstance, TransactionHandler transactionHandler) throws Throwable {

		//Check if the query instance has an ID set and if the ID of the descriptor has changed
		if(queryInstance.getQueryInstanceID() == null || !queryInstance.getQueryInstanceID().equals(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID())){

			queryInstance.setQueryInstanceID(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID());

			this.queryInstanceDAO.add(queryInstance, transactionHandler, SAVE_QUERY_INSTANCE_RELATION_QUERY);

		}else{

			this.queryInstanceDAO.update(queryInstance, transactionHandler, SAVE_QUERY_INSTANCE_RELATION_QUERY);
		}
	}

	@Override
	public void populate(ManualMultiSignQueryInstance queryInstance, HttpServletRequest req, User user, boolean allowPartialPopulation, MutableAttributeHandler attributeHandler) throws ValidationException {

		Integer queryID = queryInstance.getQuery().getQueryID();

		if(queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED || !StringUtils.isEmpty(req.getParameter("q" + queryID +"_socialSecurityNumber")) || !StringUtils.isEmpty(req.getParameter("q" + queryID +"_name")) || !StringUtils.isEmpty(req.getParameter("q" + queryID + "_email"))){

			List<ValidationError> errors = new ArrayList<ValidationError>();

			String socialSecurityNumber = ValidationUtils.validateParameter("q" + queryID +"_socialSecurityNumber", req, true, SOCIAL_SECURITY_NUMBER_POPULATOR, errors);
			String name = ValidationUtils.validateParameter("q" + queryID +"_name", req, true, 3, 255, errors);
			String email = ValidationUtils.validateParameter("q" + queryID + "_email", req, true, 1, 255, EMAIL_POPULATOR, errors);

			if(!errors.isEmpty()){

				throw new ValidationException(errors);
			}

			queryInstance.setSigningParties(Collections.singletonList(new ManualSigningParty(socialSecurityNumber, name, email)));

			queryInstance.getQueryInstanceDescriptor().setPopulated(true);

		}else{

			queryInstance.getQueryInstanceDescriptor().setPopulated(false);
		}
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	@WebPublic(alias = "config")
	public ForegroundModuleResponse configureQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		Integer queryID = uriParser.getInt(2);

		if(queryID != null){

			QueryDescriptor queryDescriptor = flowAdminModule.getQueryDescriptor(queryID);

			if(queryDescriptor != null){

				res.sendRedirect(flowAdminModule.getFlowQueryRedirectURL(req, queryDescriptor.getStep().getFlow().getFlowID()));

				return null;
			}
		}

		throw new URINotFoundException(uriParser);
	}
}
