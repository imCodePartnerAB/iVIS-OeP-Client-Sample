package com.imcode.oeplatform.flowengine.queries.checkboxquery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.imcode.oeplatform.flowengine.queries.checkboxquery.validationerrors.TooFewAlternativesSelectedValidationError;
import com.imcode.oeplatform.flowengine.queries.checkboxquery.validationerrors.TooManyAlternativesSelectedValidationError;
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

public class CheckboxQueryProviderModule extends BaseQueryProviderModule<CheckboxQueryInstance> implements BaseQueryCRUDCallback, FixedAlternativesQueryCallback<CheckboxQuery>, SummaryTableQueryCallback<CheckboxQuery> {

	private static final RelationQuery SAVE_QUERY_INSTANCE_RELATION_QUERY = new RelationQuery(CheckboxQueryInstance.ALTERNATIVES_RELATION);

	@XSLVariable(prefix="java.")
	private String countText = "Count";

	@XSLVariable(prefix="java.")
	private String alternativesText = "Alternative";

	private AnnotatedDAO<CheckboxQuery> queryDAO;
	private AnnotatedDAO<CheckboxQueryInstance> queryInstanceDAO;

	private CheckboxQueryCRUD queryCRUD;

	private QueryParameterFactory<CheckboxQuery, Integer> queryIDParamFactory;
	private QueryParameterFactory<CheckboxQueryInstance, Integer> queryInstanceIDParamFactory;
	private QueryParameterFactory<CheckboxQueryInstance, CheckboxQuery> queryInstanceQueryParamFactory;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		//Automatic table version handling
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, CheckboxQueryProviderModule.class.getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("DB script.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		SimpleAnnotatedDAOFactory daoFactory = new SimpleAnnotatedDAOFactory(dataSource);

		queryDAO = daoFactory.getDAO(CheckboxQuery.class);
		queryInstanceDAO = daoFactory.getDAO(CheckboxQueryInstance.class);

		queryCRUD = new CheckboxQueryCRUD(queryDAO.getWrapper(Integer.class), new AnnotatedRequestPopulator<CheckboxQuery>(CheckboxQuery.class), "CheckboxQuery", "query", null, this);

		queryIDParamFactory = queryDAO.getParamFactory("queryID", Integer.class);
		queryInstanceIDParamFactory = queryInstanceDAO.getParamFactory("queryInstanceID", Integer.class);
		queryInstanceQueryParamFactory = queryInstanceDAO.getParamFactory("query", CheckboxQuery.class);
	}

	@Override
	public Query createQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws SQLException {

		CheckboxQuery query = new CheckboxQuery();

		query.setQueryID(descriptor.getQueryID());

		this.queryDAO.add(query, transactionHandler, null);

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query importQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		CheckboxQuery query = new CheckboxQuery();

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

		CheckboxQuery query = this.getQuery(descriptor.getQueryID());

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public Query getQuery(MutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		CheckboxQuery query = this.getQuery(descriptor.getQueryID(), transactionHandler);

		if (query == null) {

			return null;
		}

		query.init(descriptor, getFullAlias() + "/config/" + descriptor.getQueryID());

		return query;
	}

	@Override
	public QueryInstance getQueryInstance(MutableQueryInstanceDescriptor descriptor, String instanceManagerID, HttpServletRequest req, User user, InstanceMetadata instanceMetadata) throws SQLException {

		CheckboxQueryInstance queryInstance = null;

		//Check if we should create a new instance or get an existing one
		if (descriptor.getQueryInstanceID() == null) {

			queryInstance = new CheckboxQueryInstance();

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

	private CheckboxQuery getQuery(Integer queryID) throws SQLException {

		HighLevelQuery<CheckboxQuery> query = new HighLevelQuery<CheckboxQuery>(CheckboxQuery.ALTERNATIVES_RELATION);

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query);
	}

	private CheckboxQuery getQuery(Integer queryID, TransactionHandler transactionHandler) throws SQLException {

		HighLevelQuery<CheckboxQuery> query = new HighLevelQuery<CheckboxQuery>(CheckboxQuery.ALTERNATIVES_RELATION);

		query.addParameter(queryIDParamFactory.getParameter(queryID));

		return queryDAO.get(query, transactionHandler);
	}

	private CheckboxQueryInstance getQueryInstance(Integer queryInstanceID) throws SQLException {

		HighLevelQuery<CheckboxQueryInstance> query = new HighLevelQuery<CheckboxQueryInstance>(CheckboxQueryInstance.ALTERNATIVES_RELATION, CheckboxQueryInstance.QUERY_RELATION);

		query.addParameter(queryInstanceIDParamFactory.getParameter(queryInstanceID));

		return queryInstanceDAO.get(query);
	}

	@Override
	public void save(CheckboxQueryInstance queryInstance, TransactionHandler transactionHandler) throws Throwable {

		//Check if the query instance has an ID set and if the ID of the descriptor has changed
		if(queryInstance.getQueryInstanceID() == null || !queryInstance.getQueryInstanceID().equals(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID())){

			queryInstance.setQueryInstanceID(queryInstance.getQueryInstanceDescriptor().getQueryInstanceID());

			this.queryInstanceDAO.add(queryInstance, transactionHandler, SAVE_QUERY_INSTANCE_RELATION_QUERY);

		}else{

			this.queryInstanceDAO.update(queryInstance, transactionHandler, SAVE_QUERY_INSTANCE_RELATION_QUERY);
		}
	}

	@Override
	public void populate(CheckboxQueryInstance queryInstance, HttpServletRequest req, User user, boolean allowPartialPopulation, MutableAttributeHandler attributeHandler) throws ValidationException {

		List<CheckboxAlternative> availableAlternatives = queryInstance.getQuery().getAlternatives();

		if (CollectionUtils.isEmpty(availableAlternatives)) {

			//If the parent query doesn't have any alternatives then there is no population to do
			queryInstance.reset(attributeHandler);
			return;
		}

		List<CheckboxAlternative> selectedAlternatives = new ArrayList<CheckboxAlternative>(queryInstance.getQuery().getAlternatives().size());

		for (CheckboxAlternative alternative : availableAlternatives) {

			if (req.getParameter("q" + queryInstance.getQuery().getQueryID() + "_alternative" + alternative.getAlternativeID()) != null) {

				selectedAlternatives.add(alternative);
			}
		}

		int alternativesSelected = selectedAlternatives.size();

		List<ValidationError> validationErrors = new ArrayList<ValidationError>();

		String freeTextAlternativeValue = FreeTextAlternativePopulator.populate(queryInstance.getQuery().getQueryID(), "_freeTextAlternative", req, validationErrors);

		if(freeTextAlternativeValue != null) {
			alternativesSelected++;
		}

		//If partial population is allowed and the user has not select any alternatives, skip validation
		if (allowPartialPopulation) {

			if(alternativesSelected == 0) {

				queryInstance.setAlternatives(null);
				queryInstance.setFreeTextAlternativeValue(null);
				queryInstance.getQueryInstanceDescriptor().setPopulated(false);

			} else {

				queryInstance.setAlternatives(selectedAlternatives);
				queryInstance.setFreeTextAlternativeValue(freeTextAlternativeValue);
				queryInstance.getQueryInstanceDescriptor().setPopulated(true);

			}

			return;
		}

		//Check if this query is required or if the user has selected any alternatives anyway
		if (queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED || alternativesSelected != 0) {

			if (queryInstance.getMinChecked() != null && alternativesSelected < queryInstance.getMinChecked()) {

				validationErrors.add(new TooFewAlternativesSelectedValidationError(alternativesSelected, queryInstance.getMinChecked()));

			} else if (queryInstance.getMaxChecked() != null && alternativesSelected > queryInstance.getMaxChecked()) {

				validationErrors.add(new TooManyAlternativesSelectedValidationError(alternativesSelected, queryInstance.getMaxChecked()));

			} else if (queryInstance.getQueryInstanceDescriptor().getQueryState() == QueryState.VISIBLE_REQUIRED && alternativesSelected == 0) {

				validationErrors.add(new ValidationError("RequiredQuery"));
			}
		}

		if(!validationErrors.isEmpty()) {
			throw new ValidationException(validationErrors);
		}

		queryInstance.setFreeTextAlternativeValue(freeTextAlternativeValue);
		queryInstance.setAlternatives(selectedAlternatives);
		queryInstance.getQueryInstanceDescriptor().setPopulated(!selectedAlternatives.isEmpty());
	}

	@WebPublic(alias = "config")
	public ForegroundModuleResponse configureQuery(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.queryCRUD.update(req, res, user, uriParser);
	}

	@Override
	public boolean deleteQuery(ImmutableQueryDescriptor descriptor, TransactionHandler transactionHandler) throws SQLException {

		CheckboxQuery query = getQuery(descriptor.getQueryID());

		if (query == null) {

			return false;
		}

		this.queryDAO.delete(query, transactionHandler);

		return true;
	}

	@Override
	public boolean deleteQueryInstance(ImmutableQueryInstanceDescriptor descriptor, TransactionHandler transactionHandler) throws Throwable {

		CheckboxQueryInstance queryInstance = this.getQueryInstance(descriptor.getQueryInstanceID());

		if (queryInstance == null) {

			return false;
		}

		this.queryInstanceDAO.delete(queryInstance, transactionHandler);

		return true;
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	@Override
	public void copyQuery(MutableQueryDescriptor sourceQueryDescriptor, MutableQueryDescriptor copyQueryDescriptor, TransactionHandler transactionHandler) throws SQLException {

		CheckboxQuery query = getQuery(sourceQueryDescriptor.getQueryID(), transactionHandler);

		query.setQueryID(copyQueryDescriptor.getQueryID());

		if(query.getAlternatives() != null){

			for(CheckboxAlternative alternative : query.getAlternatives()){

				alternative.setAlternativeID(null);
			}
		}

		this.queryDAO.add(query, transactionHandler, new RelationQuery(CheckboxQuery.ALTERNATIVES_RELATION));
	}

	@Override
	public List<CheckboxQueryInstance> getQueryInstances(CheckboxQuery checkboxQuery, List<Integer> queryInstanceIDs) throws SQLException {

		HighLevelQuery<CheckboxQueryInstance> query = new HighLevelQuery<CheckboxQueryInstance>();

		query.addRelation(CheckboxQueryInstance.ALTERNATIVES_RELATION);

		query.addParameter(queryInstanceQueryParamFactory.getParameter(checkboxQuery));
		query.addParameter(queryInstanceIDParamFactory.getWhereInParameter(queryInstanceIDs));

		return this.queryInstanceDAO.getAll(query);
	}

	@Override
	public Matrix<String> getSummaryTable(CheckboxQuery query, List<Integer> queryInstanceIDs) throws SQLException {

		if(query.getAlternatives() == null){

			return null;
		}

		List<CheckboxQueryInstance> instances;

		if(queryInstanceIDs != null){

			instances = getQueryInstances(query, queryInstanceIDs);

		}else{

			instances = null;
		}

		Matrix<String> table = new Matrix<String>(query.getFreeTextAlternative() != null ? query.getAlternatives().size() + 2 : query.getAlternatives().size() + 1, 2);

		table.setCell(0, 0, alternativesText);
		table.setCell(0, 1, countText);

		int currentRow = 1;

		for(CheckboxAlternative alternative : query.getAlternatives()){

			table.setCell(currentRow, 0, alternative.getName());

			int selectionCount = 0;

			if(instances != null){

				for(CheckboxQueryInstance instance : instances){

					if(instance.getAlternatives() != null && instance.getAlternatives().contains(alternative)){

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

				for(CheckboxQueryInstance instance : instances){

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
	protected void appendPDFData(Document doc, Element showQueryValuesElement, CheckboxQueryInstance queryInstance) {

		super.appendPDFData(doc, showQueryValuesElement, queryInstance);

		if(queryInstance.getQuery().getDescription() != null){

			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "Description", JTidyUtils.getXHTML(queryInstance.getQuery().getDescription()));
			XMLUtils.appendNewCDATAElement(doc, showQueryValuesElement, "isHTMLDescription", queryInstance.getQuery().getDescription().contains("<") && queryInstance.getQuery().getDescription().contains(">"));
		}
	}

}
