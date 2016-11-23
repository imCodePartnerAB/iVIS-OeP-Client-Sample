package com.nordicpeak.flowengine.queries.fileuploadquery;

import java.lang.reflect.Field;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.XMLElement;

import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.interfaces.QueryRequestProcessor;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryInstance;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryUtils;

@Table(name = "file_upload_query_instances")
@XMLElement
public class FileUploadQueryInstance extends BaseQueryInstance {

	private static final long serialVersionUID = -7761759005604863873L;

	public static Field QUERY_RELATION = ReflectionUtils.getField(FileUploadQueryInstance.class, "query");
	public static Field FILES_RELATION = ReflectionUtils.getField(FileUploadQueryInstance.class, "files");

	private int temporaryFileCounter = 1;

	private String instanceManagerID;

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryInstanceID;

	@DAOManaged(columnName = "queryID")
	@ManyToOne
	@XMLElement
	private FileUploadQuery query;

	@DAOManaged
	@OneToMany
	@XMLElement(fixCase=true)
	private List<FileDescriptor> files;

	public Integer getQueryInstanceID() {

		return queryInstanceID;
	}

	public void setQueryInstanceID(Integer queryInstanceID) {

		this.queryInstanceID = queryInstanceID;
	}

	public FileUploadQuery getQuery() {

		return query;
	}

	public void setQuery(FileUploadQuery query) {

		this.query = query;
	}

	@Override
	public void reset(MutableAttributeHandler attributeHandler) {

		this.setFiles(null);
		super.reset(attributeHandler);
	}

	public void copyQueryValues() {

	}

	@Override
	public String toString() {

		return "FileUploadQueryInstance (queryInstanceID=" + queryInstanceID + ")";
	}

	public List<FileDescriptor> getFiles() {

		return files;
	}

	public void setFiles(List<FileDescriptor> files) {

		this.files = files;
	}

	public synchronized int getNextTemporaryFileID() {

		return temporaryFileCounter++;
	}

	@Override
	public void close(QueryHandler queryHandler) {

		queryHandler.getQueryProvider(this.getQueryInstanceDescriptor().getQueryDescriptor().getQueryTypeID(), FileUploadQueryProviderModule.class).close(this);
	}

	public String getInstanceManagerID() {

		return instanceManagerID;
	}

	public void setInstanceManagerID(String instanceManagerID) {

		this.instanceManagerID = instanceManagerID;
	}

	@Override
	public QueryRequestProcessor getQueryRequestProcessor(HttpServletRequest req, User user, QueryHandler queryHandler) throws Exception {

		return BaseQueryUtils.getGenericQueryInstanceProvider(this.getClass(), queryHandler, queryInstanceDescriptor.getQueryDescriptor().getQueryTypeID()).getQueryRequestProcessor(this, req, user);
	}

	@Override
	public Element toExportXML(Document doc, QueryHandler queryHandler) throws Exception {

		Element element = getBaseExportXML(doc);

		queryHandler.getQueryProvider(queryInstanceDescriptor.getQueryDescriptor().getQueryTypeID(), FileUploadQueryProviderModule.class).appendFileExportXML(doc, element, this);

		return element;
	}
}
