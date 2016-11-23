package com.nordicpeak.flowengine.search;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import se.unlogic.hierarchy.core.beans.SimpleAccessInterface;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.json.JsonArray;
import se.unlogic.standardutils.json.JsonObject;
import se.unlogic.standardutils.json.JsonUtils;
import se.unlogic.standardutils.serialization.SerializationUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.HTTPUtils;

import com.nordicpeak.flowengine.beans.Flow;
import com.nordicpeak.flowengine.beans.FlowFamily;
import com.nordicpeak.flowengine.beans.FlowInstance;
import com.nordicpeak.flowengine.dao.FlowEngineDAOFactory;
import com.nordicpeak.flowengine.search.events.AddFlowEvent;
import com.nordicpeak.flowengine.search.events.AddFlowFamilyEvent;
import com.nordicpeak.flowengine.search.events.AddUpdateFlowInstanceEvent;
import com.nordicpeak.flowengine.search.events.DeleteFlowEvent;
import com.nordicpeak.flowengine.search.events.DeleteFlowFamilyEvent;
import com.nordicpeak.flowengine.search.events.DeleteFlowInstanceEvent;
import com.nordicpeak.flowengine.search.events.QueuedIndexEvent;

public class FlowInstanceIndexer {

	private static final String ID_FIELD = "id";
	private static final String FLOW_ID_FIELD = "flowID";
	private static final String FLOW_NAME_FIELD = "name";
	private static final String FLOW_FAMILY_ID_FIELD = "familyID";
	private static final String ADDED_FIELD = "added";
	private static final String STATUS_NAME_FIELD = "status";
	private static final String POSTER_FIELD = "poster";
	private static final String MANAGER_FIELD = "manager";
	private static final String ACCESS_INTERFACE_FIELD = "accessInterface";

	private static final String[] SEARCH_FIELDS = new String[] { ID_FIELD, POSTER_FIELD, MANAGER_FIELD, FLOW_NAME_FIELD, STATUS_NAME_FIELD, ADDED_FIELD};

	protected Logger log = Logger.getLogger(this.getClass());

	private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
	private Directory index = new RAMDirectory();
	private IndexWriter indexWriter;
	private IndexReader indexReader;
	private IndexSearcher searcher;

	protected final SystemInterface systemInterface;
	protected final FlowEngineDAOFactory daoFactory;
	protected final QueryParameterFactory<FlowFamily, Integer> flowFamilyIDParamFactory;
	protected final QueryParameterFactory<Flow, Boolean> flowEnabledParamFactory;
	protected final QueryParameterFactory<Flow, Integer> flowIDParamFactory;
	protected final QueryParameterFactory<FlowInstance, Integer> flowInstanceIDParamFactory;

	protected int maxHitCount;
	protected int maxFilteredHitCount;

	private boolean logIndexing = true;
	
	private LinkedBlockingQueue<QueuedIndexEvent> eventQueue = new LinkedBlockingQueue<QueuedIndexEvent>();

	private CallbackThreadPoolExecutor threadPoolExecutor;

	public FlowInstanceIndexer(FlowEngineDAOFactory daoFactory, int maxHitCount, int maxFilteredHitCount, SystemInterface systemInterface) throws IOException {

		super();
		this.daoFactory = daoFactory;
		this.maxHitCount = maxHitCount;
		this.systemInterface = systemInterface;

		flowFamilyIDParamFactory = daoFactory.getFlowFamilyDAO().getParamFactory("flowFamilyID", Integer.class);

		flowIDParamFactory = daoFactory.getFlowDAO().getParamFactory("flowID", Integer.class);
		flowEnabledParamFactory = daoFactory.getFlowDAO().getParamFactory("enabled", boolean.class);

		flowInstanceIDParamFactory = daoFactory.getFlowInstanceDAO().getParamFactory("flowInstanceID", Integer.class);

		indexWriter = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_44, analyzer));

		int availableProcessors = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();

		threadPoolExecutor = new CallbackThreadPoolExecutor(availableProcessors, availableProcessors, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), this);
	}

	public void cacheFlowInstances() {

		List<FlowFamily> families;

		try{
			families = getFlowFamilies();

		}catch(SQLException e){

			log.error("Error gettings enabled flow families from DB", e);

			return;
		}

		if(families != null){

			for(FlowFamily flowFamily : families){

				eventQueue.add(new AddFlowFamilyEvent(flowFamily));
				checkQueueState(false);
			}
		}
	}

	public void close() {

		threadPoolExecutor.shutdownNow();
		try {
			threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}

		this.eventQueue.clear();
		threadPoolExecutor.purge();

		try{
			indexWriter.close();
		}catch(IOException e){
			log.warn("Error closing index writer", e);
		}

		try{
			index.close();
		}catch(IOException e){
			log.warn("Error closing index", e);
		}
	}

	public void search(HttpServletRequest req, HttpServletResponse res, User user) throws IOException {

		//Check if the index contains any documents
		if (indexReader == null || indexReader.numDocs() == 0) {

			sendEmptyResponse(res);
		}

		String queryString = req.getParameter("q");

		log.info("User " + user + " searching for: " + StringUtils.toLogFormat(queryString, 50));

		if(StringUtils.isEmpty(queryString)){

			sendEmptyResponse(res);
			return;
		}

		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_44, SEARCH_FIELDS, analyzer);

		Query query;

		try{
			query = parser.parse(SearchUtils.rewriteQueryString(queryString));

		}catch(ParseException e){

			log.warn("Unable to parse query string " + StringUtils.toLogFormat(queryString, 50) + " requsted by user " + user + " accessing from " + req.getRemoteAddr());

			sendEmptyResponse(res);
			return;
		}

		TopDocs results = searcher.search(query, maxHitCount);

		if(results.scoreDocs.length == 0){

			sendEmptyResponse(res);
			return;
		}

		JsonArray jsonArray = new JsonArray();

		//Create JSON from hits
		for(ScoreDoc scoreDoc : results.scoreDocs){

			Document doc = searcher.doc(scoreDoc.doc);

			//Access check
			BytesRef bytesRef = doc.getBinaryValue(ACCESS_INTERFACE_FIELD);

			AccessInterface accessInterface = SerializationUtils.deserializeFromArray(AccessInterface.class, bytesRef.bytes);

			if (!AccessUtils.checkAccess(user, accessInterface)) {

				continue;
			}

			JsonObject instance = new JsonObject(2);
			instance.putField(ID_FIELD, doc.get(ID_FIELD));
			instance.putField(FLOW_NAME_FIELD, doc.get(FLOW_NAME_FIELD));
			instance.putField(STATUS_NAME_FIELD, doc.get(STATUS_NAME_FIELD));
			instance.putField(ADDED_FIELD, doc.get(ADDED_FIELD));

			jsonArray.addNode(instance);

			if(jsonArray.size() == maxFilteredHitCount){

				break;
			}
		}

		JsonObject jsonObject = new JsonObject(2);
		jsonObject.putField("hitCount", Integer.toString(jsonArray.size()));
		jsonObject.putField("hits", jsonArray);
		HTTPUtils.sendReponse(jsonObject.toJson(), JsonUtils.getContentType(), res);
		return;
	}

	public static void sendEmptyResponse(HttpServletResponse res) throws IOException {

		JsonObject jsonObject = new JsonObject(1);
		jsonObject.putField("hitCount", "0");
		HTTPUtils.sendReponse(jsonObject.toJson(), JsonUtils.getContentType(), res);
	}

	public int getMaxHitCount() {

		return maxHitCount;
	}

	public void setMaxHitCount(int maxHitCount) {

		this.maxHitCount = maxHitCount;
	}

	public void addFlowFamilies(List<FlowFamily> beans) {

		for(FlowFamily flowFamily : beans){

			eventQueue.add(new AddFlowFamilyEvent(flowFamily));
			checkQueueState(false);
		}
	}

	public void updateFlowFamilies(List<FlowFamily> beans) {

		for(FlowFamily flowFamily : beans){

			eventQueue.add(new DeleteFlowFamilyEvent(flowFamily));
			eventQueue.add(new AddFlowFamilyEvent(flowFamily));
			checkQueueState(false);
		}
	}

	public void deleteFlowFamilies(List<FlowFamily> beans) {

		for(FlowFamily flowFamily : beans){

			eventQueue.add(new DeleteFlowFamilyEvent(flowFamily));
			checkQueueState(false);
		}
	}

	public void addFlows(List<Flow> beans) {

		for(Flow flow : beans){

			eventQueue.add(new AddFlowEvent(flow));
			checkQueueState(false);
		}
	}

	public void updateFlows(List<Flow> beans) {

		for(Flow flow : beans){

			eventQueue.add(new DeleteFlowEvent(flow));
			eventQueue.add(new AddFlowEvent(flow));
			checkQueueState(false);
		}
	}

	public void deleteFlows(List<Flow> beans) {

		for(Flow flow : beans){

			eventQueue.add(new DeleteFlowEvent(flow));
			checkQueueState(false);
		}
	}

	public void addFlowInstances(List<FlowInstance> beans) {

		for(FlowInstance flowInstance : beans){

			eventQueue.add(new AddUpdateFlowInstanceEvent(flowInstance));
			checkQueueState(false);
		}
	}

	public void updateFlowInstances(List<FlowInstance> beans) {

		for(FlowInstance flowInstance : beans){

			eventQueue.add(new AddUpdateFlowInstanceEvent(flowInstance));
			checkQueueState(false);
		}
	}

	public void deleteFlowInstances(List<FlowInstance> beans) {

		for(FlowInstance flowInstance : beans){

			eventQueue.add(new DeleteFlowInstanceEvent(flowInstance));
			checkQueueState(false);
		}
	}

	public void checkQueueState(boolean commit) {

		if (systemInterface.getSystemStatus() != SystemStatus.STARTED) {

			return;
		}

		if (threadPoolExecutor.getExecutingThreadCount() == 0 && threadPoolExecutor.getQueue().isEmpty()) {

			try {
				if (commit) {

					if(logIndexing){
						log.info("Committing index changes from last event.");
					}
					
					this.indexWriter.commit();
					this.indexReader = DirectoryReader.open(index);
					this.searcher = new IndexSearcher(indexReader);
				}

			} catch (IOException e) {
				log.error("Unable to commit index", e);
			}

			while(true){

				QueuedIndexEvent nextEvent = eventQueue.poll();

				if (nextEvent == null) {

					log.debug("No queued search events found, thread pool idle.");
					return;
				}
				if(logIndexing){
					log.info("Processing " + nextEvent);
				}

				int tasks = nextEvent.queueTasks(threadPoolExecutor, this);

				if(tasks > 0){

					return;
				}
			}
		}
	}

	public boolean isValidState() {

		return systemInterface.getSystemStatus() == SystemStatus.STARTED;
	}

	public void indexFlowInstance(FlowInstance flowInstance, Flow flow, FlowFamily flowFamily){

		log.debug("Indexing flow instance " + flowInstance);

		try{
			Document doc = new Document();

			doc.add(new StringField(ID_FIELD, flowInstance.getFlowInstanceID().toString(), Field.Store.YES));
			doc.add(new StringField(FLOW_ID_FIELD, flow.getFlowID().toString(), Field.Store.YES));
			doc.add(new StringField(FLOW_FAMILY_ID_FIELD, flowFamily.getFlowFamilyID().toString(), Field.Store.YES));
			doc.add(new TextField(FLOW_NAME_FIELD, flow.getName(), Field.Store.YES));
			doc.add(new TextField(ADDED_FIELD, DateUtils.DATE_TIME_FORMATTER.format(flowInstance.getAdded()), Field.Store.YES));
			doc.add(new TextField(STATUS_NAME_FIELD, flowInstance.getStatus().getName(), Field.Store.YES));

			if(flowInstance.getPoster() != null){

				doc.add(new TextField(POSTER_FIELD, flowInstance.getPoster().getFirstname() + " " + flowInstance.getPoster().getLastname(), Field.Store.NO));
			}


			if(flowInstance.getManagers() != null){

				for(User manager : flowInstance.getManagers()){

					doc.add(new TextField(MANAGER_FIELD, manager.getFirstname() + " " + manager.getLastname(), Field.Store.NO));
				}
			}

			doc.add(new StoredField(ACCESS_INTERFACE_FIELD, SerializationUtils.serializeToArray(new SimpleAccessInterface(flowFamily))));

			indexWriter.addDocument(doc);

		}catch(Exception e){

			log.error("Error indexing flow instance " + flowInstance, e);
		}
	}

	public void deleteDocument(FlowInstance flowInstance) {

		log.debug("Removing flow instance " + flowInstance + " from index.");

		BooleanQuery query = new BooleanQuery();

		query.add(new TermQuery(new Term(ID_FIELD, flowInstance.getFlowInstanceID().toString())), Occur.MUST);

		try{
			indexWriter.deleteDocuments(query);

		}catch(Exception e){

			log.error("Error removing flow instance " + flowInstance + " from index", e);
		}
	}

	public void deleteDocuments(Flow flow) {

		log.debug("Removing flow instances belonging to flow " + flow + " from index.");

		BooleanQuery query = new BooleanQuery();

		query.add(new TermQuery(new Term(FLOW_ID_FIELD, flow.getFlowID().toString())), Occur.MUST);

		try{
			indexWriter.deleteDocuments(query);

		}catch(Exception e){

			log.error("Error removing flow instances belonging to flow " + flow + " from index", e);
		}
	}

	public void deleteDocuments(FlowFamily flowFamily) {

		log.debug("Removing flow instances belonging to flow family " + flowFamily + " from index.");

		BooleanQuery query = new BooleanQuery();

		query.add(new TermQuery(new Term(FLOW_FAMILY_ID_FIELD, flowFamily.getFlowFamilyID().toString())), Occur.MUST);

		try{
			indexWriter.deleteDocuments(query);

		}catch(Exception e){

			log.error("Error removing flow instances belonging to flow family " + flowFamily + " from index", e);
		}
	}

	public FlowInstance getFlowInstance(Integer flowInstanceID) throws SQLException {

		HighLevelQuery<FlowInstance> query = new HighLevelQuery<FlowInstance>(FlowInstance.MANAGERS_RELATION, FlowInstance.FLOW_RELATION,  FlowInstance.FLOW_STATE_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION);

		query.addParameter(flowInstanceIDParamFactory.getParameter(flowInstanceID));

		return daoFactory.getFlowInstanceDAO().get(query);
	}

	public Flow getFlow(Integer flowID) throws SQLException {

		HighLevelQuery<Flow> query = new HighLevelQuery<Flow>(Flow.FLOW_INSTANCES_RELATION, FlowInstance.MANAGERS_RELATION, FlowInstance.FLOW_STATE_RELATION, Flow.FLOW_FAMILY_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION);

		query.addParameter(flowEnabledParamFactory.getParameter(true));
		query.addParameter(flowIDParamFactory.getParameter(flowID));

		return daoFactory.getFlowDAO().get(query);
	}

	public FlowFamily getFlowFamily(Integer flowFamilyID) throws SQLException {

		HighLevelQuery<FlowFamily> query = new HighLevelQuery<FlowFamily>(FlowFamily.FLOWS_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION, Flow.FLOW_INSTANCES_RELATION, FlowInstance.FLOW_STATE_RELATION, FlowInstance.MANAGERS_RELATION);

		query.addParameter(flowFamilyIDParamFactory.getParameter(flowFamilyID));
		query.addRelationParameter(Flow.class, flowEnabledParamFactory.getParameter(true));

		return daoFactory.getFlowFamilyDAO().get(query);
	}

	private List<FlowFamily> getFlowFamilies() throws SQLException {

		HighLevelQuery<FlowFamily> query = new HighLevelQuery<FlowFamily>(FlowFamily.FLOWS_RELATION, FlowFamily.MANAGER_GROUPS_RELATION, FlowFamily.MANAGER_USERS_RELATION, Flow.FLOW_INSTANCES_RELATION, FlowInstance.FLOW_STATE_RELATION, FlowInstance.MANAGERS_RELATION);

		query.addRelationParameter(Flow.class, flowEnabledParamFactory.getParameter(true));

		return daoFactory.getFlowFamilyDAO().getAll(query);
	}

	
	public boolean isLogIndexing() {
	
		return logIndexing;
	}

	
	public void setLogIndexing(boolean logIndexing) {
	
		this.logIndexing = logIndexing;
	}
}
