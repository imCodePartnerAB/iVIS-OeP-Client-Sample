package com.nordicpeak.flowengine.queries.dropdownquery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.annotations.XSLVariable;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.dao.SimpleAnnotatedDAOFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.datatypes.Matrix;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLGenerator;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;
import se.unlogic.webutils.url.URLRewriter;

import com.nordicpeak.flowengine.enums.QueryState;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.ImmutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.InstanceMetadata;
import com.nordicpeak.flowengine.interfaces.MutableQueryDescriptor;
import com.nordicpeak.flowengine.interfaces.MutableQueryInstanceDescriptor;
import com.nordicpeak.flowengine.interfaces.Query;
import com.nordicpeak.flowengine.interfaces.QueryInstance;
import com.nordicpeak.flowengine.populators.FreeTextAlternativePopulator;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryCRUDCallback;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryProviderModule;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativeQueryUtils;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQueryCallback;
import com.nordicpeak.flowengine.queries.tablequery.SummaryTableQueryCallback;
import com.nordicpeak.flowengine.utils.JTidyUtils;
import com.nordicpeak.flowengine.utils.TextTagReplacer;

public class DropDownQueryProviderModule extends BaseQueryProviderModule<DropDownQueryInstance> implements BaseQueryCRUDCallback, FixedAlternativesQueryCallback<DropDownQuery>, SummaryTableQueryCallback<DropDownQuery> {

	@XSLVariable(prefix="java.")
	private String countText = "Count";

	@XSLVariable(prefix="java.")
	private String alternativesText = "Alternative";

	private AnnotatedDAO<DropDownQuery> queryDAO;
	private AnnotatedDAO<DropDownQueryInstance> queryInstanceDAO;

	private DropDownQueryCRUD queryCRUD;

	private QueryParameterFactory<DropDownQuery, Integer> queryIDParamFactory;
	private QueryParameterFactory<DropDownQueryInstance, Integer> queryInstanceIDParamFactory;
	private QueryParameterFactory<DropDownQueryInstance, DropDownQuery> queryInstanceQueryParamFactory;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		//Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, DropDownQueryProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		queryDAO = daoFactory.getDAO(DropDownQuery.class);
		queryInstanceDAO = daoFactory.getDAO(DropDownQueryInstance.class);

		queryCRUD = new DropDownQueryCRUD(queryDAO.getWrapper(Integer.class), new AnnotatedRequestPopulator<DropDownQuery>(DropDownQuery.class), "DropDownQuery", "query", null, this);

		queryIDParamFactory = queryDAO.getParamFactory("queryID", Integer.class);
		queryInstanceIDParamFactory = queryInstanceDAO.getParamFactory("queryInstanceID", Integer.class);
		queryInstanceQueryParamFactory = queryInstanceDAO.getParamFactory("query", DropDownQuery.class);
	}

	@Override
	public Query createQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws SQLException {

		DropDownQuery query = new DropDownQuery();

		query.setQueryID(descriptor.getQueryID());

		this.queryDAO.add(query,transactionHandler,null);

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query importQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		DropDownQuery query = new DropDownQuery();

		query.setQueryID(descriptor.getQueryID());

		query.populate(descriptor.getImportParser().getNode(XMLGenerator.getElementName(query.getClass())));

		List<Integer> oldAlternativeIDs = FixedAlternativeQueryUtils.getAlternativeIDs(query);

		FixedAlternativeQueryUtils.clearAlternativeIDs(query.getAlternatives());

		this.queryDAO.add(query, transactionHandler, null);

		query.setAlternativeConversionMap(FixedAlternativeQueryUtils.getAlternativeConversionMap(query.getAlternatives(), oldAlternativeIDs));

		return query;
	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor) throws SQLException {

		DropDownQuery query = this.getQuery(descriptor.getQueryID());

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		DropDownQuery query = this.getQuery(descriptor.getQueryID(), transactionHandler);

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public QueryInstance getQueryInstance(MutableQueryInstanceDescriptor descriptor, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws SQLException {

		DropDownQueryInstance queryInstance;

		//Check if we should create a new instance or get an existing one
		if (descriptor.getQueryInstanceID() == null) {

			queryInstance = new DropDownQueryInstance();

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

	public DropDownQuery getQuery(Integer queryID) throws SQLException {

		HighLevelQuery<DropDownQuery> query = new HighLevelQuery<DropDownQuery>(DropDownQuery.ALTERNATIVES_RELATION);

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query);
	}

	public DropDownQuery getQuery(Integer queryID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<DropDownQuery> query = new HighLevelQuery<DropDownQuery>(DropDownQuery.ALTERNATIVES_RELATION);

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query, transactionHandler);
	}

	private DropDownQueryInstance getQueryInstance(Integer queryInstanceID) throws SQLException {

		HighLevelQuery<DropDownQueryInstance> query = new HighLevelQuery<DropDownQueryInstance>(DropDownQueryInstance.ALTERNATIVE_RELATION, DropDownQueryInstance.QUERY_RELATION);

		query.addParameter(queryInstanceIDParamFactory.getParameter(queryInstanceID));

		return queryInstanceDAO.get(query);
	}

	@Override
	public void save(DropDownQueryInstance queryInstance, TransactionHandler transactionHandler) throws Throwable {

		if(queryInstance.getQueryInstanceID() == null || !queryInstance.getQueryInstanceID().equals(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID())){

			queryInstance.setQueryInstanceID(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID());

			this.queryInstanceDAO.add(queryInstance, transactionHandler, null);

		}else{

			this.queryInstanceDAO.update(queryInstance, transactionHandler, null);
		}
	}

	@Override
	public void populate(DropDownQueryInstance queryInstance, HttpServletRequest req, User user, boolean allowPartialPopulation, MutableAttributeHandler attributeHandler) throws ValidationException {

		List<DropDownAlternative> availableAlternatives = queryInstance.getQuery().getAlternatives();

		if (CollectionUtils.isEmpty(availableAlternatives)) {

			//If the parent query doesn't have any alternatives then there is no population to do
			queryInstance.reset(attributeHandler);
			return;
		}

		Integer alternativeID = NumberUtils.toInt(req.getParameter("q" + queryInstance.getQuery().getQueryID() + "_alternative"));

		boolean alternativeSelected = false;

		DropDownAlternative selectedAlternative = null;

		if (alternativeID != null) {

			for (DropDownAlternative alternative : availableAlternatives) {

				if (alternative.getAlternativeID().equals(alternativeID)) {

					selectedAlternative = alternative;
					alternativeSelected = true;
					break;
				}
			}
		}

		List<ValidationError> validationErrors = new ArrayList<ValidationError>();

		String freeTextAlternativeValue = null;

		if(!alternativeSelected) {

			freeTextAlternativeValue = FreeTextAlternativePopulator.populate(queryInstance.getQuery().getQueryID(), "_alternative", req, validationErrors);

			if(freeTextAlternativeValue != null) {
				alternativeSelected = true;
			}

		}

		//If partial population is allowed, skip validation
		if (allowPartialPopulation && freeTextAlternativeValue == null) {

			queryInstance.setAlternative(selectedAlternative);
			queryInstance.setFreeTextAlternativeValue(null);
			queryInstance.getQueryInstanceDescriptor().setPopulated(selectedAlternative != null);
			return;
		}

		//Check if this query is required and if the user has selected any alternative
		if (queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED && !alternativeSelected) {

			validationErrors.add(new ValidationError("RequiredQuery"));
		}

		if(!validationErrors.isEmpty()) {
			throw new ValidationException(validationErrors);
		}

		queryInstance.setFreeTextAlternativeValue(freeTextAlternativeValue);
		queryInstance.setAlternative(selectedAlternative);
		queryInstance.getQueryInstanceDescriptor().setPopulated(selectedAlternative != null);
	}

	@WebPublic(alias = "config")
	public ForegroundModuleResponse configureQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.queryCRUD.update(req, res, user, uriParser);
	}

	@Override
	public boolean deleteQuery(ImmutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws SQLException {

		DropDownQuery query = getQuery(descriptor.getQueryID());

		if (query == null) {

			return false;
		}

		this.queryDAO.delete(query, transactionHandler);

		return true;
	}

	@Override
	public boolean deleteQueryInstance(ImmutableQueryInstanceDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		DropDownQueryInstance queryInstance = this.getQueryInstance(descriptor.getQueryInstanceID());

		if (queryInstance == null) {

			return false;
		}

		this.queryInstanceDAO.delete(queryInstance, transactionHandler);

		return true;
	}

	@WebPublic
	public ForegroundModuleResponse addDummyAlternatives(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		//Add random alternatives to queries which don't have any alternatives

		HighLevelQuery<DropDownQuery> query = new HighLevelQuery<DropDownQuery>(DropDownQuery.ALTERNATIVES_RELATION);

		List<DropDownQuery> queries = this.queryDAO.getAll(query);

		if (queries == null) {

			return null;
		}

		for (DropDownQuery dropDownQuery : queries) {

			if (dropDownQuery.getAlternatives() != null) {

				continue;
			}

			log.info("Adding dummy alternatives to query " + dropDownQuery);

			dropDownQuery.setShortDescription("Select an alternative");

			dropDownQuery.setAlternatives(new ArrayList<DropDownAlternative>(3));

			dropDownQuery.getAlternatives().add(new DropDownAlternative("MutableAlternative 1", 0));
			dropDownQuery.getAlternatives().add(new DropDownAlternative("MutableAlternative 2", 1));
			dropDownQuery.getAlternatives().add(new DropDownAlternative("MutableAlternative 3", 2));

			this.queryDAO.update(dropDownQuery, query);
		}

		return null;
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	@Override
	public void copyQuery(MutableQueryDescriptor sourceQueryDescriptor, MutableQueryDescriptor copyQueryDescriptor, TransactionHandler transactionHandler) throws SQLException {

		DropDownQuery query = getQuery(sourceQueryDescriptor.getQueryID(), transactionHandler);

		query.setQueryID(copyQueryDescriptor.getQueryID());

		if(query.getAlternatives() != null){

			for(DropDownAlternative alternative : query.getAlternatives()){

				alternative.setAlternativeID(null);
			}
		}

		this.queryDAO.add(query, transactionHandler, new RelationQuery(DropDownQuery.ALTERNATIVES_RELATION));
	}

	@Override
	public List<DropDownQueryInstance> getQueryInstances(DropDownQuery dropDownQuery, List<Integer> queryInstanceIDs) throws SQLException {

		HighLevelQuery<DropDownQueryInstance> query = new HighLevelQuery<DropDownQueryInstance>();

		query.addRelation(DropDownQueryInstance.ALTERNATIVE_RELATION);

		query.addParameter(queryInstanceQueryParamFactory.getParameter(dropDownQuery));
		query.addParameter(queryInstanceIDParamFactory.getWhereInParameter(queryInstanceIDs));

		return this.queryInstanceDAO.getAll(query);
	}

	@Override
	public Matrix<String> getSummaryTable(DropDownQuery query, List<Integer> queryInstanceIDs) throws SQLException {

		if(query.getAlternatives() == null){

			return null;
		}

		List<DropDownQueryInstance> instances;

		if(queryInstanceIDs != null){

			instances = getQueryInstances(query, queryInstanceIDs);

		}else{

			instances = null;
		}

		Matrix<String> table = new Matrix<String>(query.getFreeTextAlternative() != null ? query.getAlternatives().size() + 2 : query.getAlternatives().size() + 1, 2);

		table.setCell(0, 0, alternativesText);
		table.setCell(0, 1, countText);

		int currentRow = 1;

		for(DropDownAlternative alternative : query.getAlternatives()){

			table.setCell(currentRow, 0, alternative.getName());

			int selectionCount = 0;

			if(instances != null){

				for(DropDownQueryInstance instance : instances){

					if(instance.getAlternative() != null && instance.getAlternative().equals(alternative)){

						selectionCount++;
					}
				}
			}

			table.setCell(currentRow, 1, String.valueOf(selectionCount));

			currentRow++;
		}

		if(query.getFreeTextAlternative() != null){

			table.setCell(currentRow, 0, query.getFreeTextAlternative());

			int selectionCount = 0;

			if(instances != null){

				for(DropDownQueryInstance instance : instances){

					if(instance.getFreeTextAlternativeValue() != null){

						selectionCount++;
					}
				}
			}

			table.setCell(currentRow, 1, String.valueOf(selectionCount));
		}

		return table;
	}

	@Override
	protected void appendPDFData(Document doc, Element showQueryValuesElement, DropDownQueryInstance queryInstance) {

		super.appendPDFData(doc, showQueryValuesElement, queryInstance);

		if(queryInstance.getQuery().getDescription() != null){

			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "Description", JTidyUtils.getXHTML(queryInstance.getQuery().getDescription()));
			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "isHTMLDescription", queryInstance.getQuery().getDescription().contains("<") && queryInstance.getQuery().getDescription().contains(">"));
		}
	}

}
