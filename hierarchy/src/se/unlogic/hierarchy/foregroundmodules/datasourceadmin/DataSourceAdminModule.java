/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.datasourceadmin;

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
import se.unlogic.hierarchy.core.beans.DataSourceDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleDataSourceDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.cache.DataSourceCache;
import se.unlogic.hierarchy.core.daos.interfaces.DataSourceDAO;
import se.unlogic.hierarchy.core.enums.DataSourceType;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.CRUDCallback;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.context.ContextUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.populators.annotated.AnnotatedRequestPopulator;

public class DataSourceAdminModule extends AnnotatedForegroundModule implements CRUDCallback<User> {

	private static final AnnotatedRequestPopulator<SimpleDataSourceDescriptor> POPULATOR = new AnnotatedRequestPopulator<SimpleDataSourceDescriptor>(SimpleDataSourceDescriptor.class);

	private DataSourceDAO dataSourceDAO;
	private DataSourceCache dataSourceCache;
	private DataSourceCRUD dataSourceCRUD;

	@XSLVariable
	private String addDataSourceBreadcrumbText;

	@XSLVariable
	private String updateDataSourceBreadcrumbText;

	@XSLVariable
	private String copySuffix = " (copy)";

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {
		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.dataSourceCache = sectionInterface.getSystemInterface().getDataSourceCache();
	}

	@Override
	protected void createDAOs(DataSource dataSource) {

		dataSourceDAO = this.sectionInterface.getSystemInterface().getCoreDaoFactory().getDataSourceDAO();
		dataSourceCRUD = new DataSourceCRUD(dataSourceDAO, POPULATOR, "DataSource", "Data source", this,sectionInterface.getSystemInterface().getDataSourceCache());
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return this.list(req, res, user, uriParser, (List<ValidationError>)null);
	}

	@WebPublic
	public ForegroundModuleResponse add(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return dataSourceCRUD.add(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse update(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return dataSourceCRUD.update(req, res, user, uriParser);
	}

	@WebPublic
	public ForegroundModuleResponse delete(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return dataSourceCRUD.delete(req, res, user, uriParser);
	}

	@WebPublic
	public SimpleForegroundModuleResponse start(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		DataSourceDescriptor dataSourceDescriptor;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (dataSourceDescriptor = this.dataSourceCRUD.getRequestedBean(req, res, user, uriParser, null)) != null) {

			if(!this.dataSourceCache.isCached(dataSourceDescriptor.getDataSourceID())){

				log.info("User " + user + " caching datasource " + dataSourceDescriptor);

				if(dataSourceDescriptor.isEnabled() && dataSourceDescriptor.getType().equals(DataSourceType.ContainerManaged) && !ContextUtils.isBound(dataSourceDescriptor.getUrl())) {
					return this.list(req, res, user, uriParser, new ValidationError("nameNotBound"));
				}
				else if(dataSourceDescriptor.isEnabled() && !ReflectionUtils.isAvailable(dataSourceDescriptor.getDriver())) {
					return this.list(req, res, user, uriParser, new ValidationError("driverNotAvailable"));
				}

				try {
					this.dataSourceCache.getDataSource(dataSourceDescriptor);
				} catch (SQLException e) {
					log.info("User " + user + " failed to start datasource " + dataSourceDescriptor, e);
					return this.list(req, res, user, uriParser, new ValidationError("datasourceNotStarted"));
				}
			}
		}

		this.redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse stop(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		DataSourceDescriptor dataSourceDescriptor;

		if (uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (dataSourceDescriptor = this.dataSourceCache.getCachedDataSourceDescriptor(Integer.parseInt(uriParser.get(2)))) != null) {

			log.info("User " + user + " stopping datasource " + dataSourceDescriptor);

			this.dataSourceCache.stop(dataSourceDescriptor.getDataSourceID());
		}

		this.redirectToDefaultMethod(req, res);

		return null;
	}

	@WebPublic
	public SimpleForegroundModuleResponse copy(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		SimpleDataSourceDescriptor dataSourceDescriptor = dataSourceCRUD.getRequestedBean(req, res, user, uriParser, null);

		if(dataSourceDescriptor == null){

			return list(req, res, user, uriParser, new ValidationError("CopyFailedDataSourceNotFound"));
		}

		log.info("User " + user + " copying data source " + dataSourceDescriptor);

		dataSourceDescriptor.setDataSourceID(null);
		dataSourceDescriptor.setName(dataSourceDescriptor.getName() + copySuffix);

		this.dataSourceDAO.add(dataSourceDescriptor);

		redirectToDefaultMethod(req, res);

		return null;
	}

	@Override
	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		return this.createDocument(req, uriParser);
	}

	private Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	@Override
	public String getTitlePrefix() {

		return this.moduleDescriptor.getName();
	}

	public SimpleForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError) throws Exception {
		
		return list(req, res, user, uriParser, CollectionUtils.getGenericSingletonList(validationError));
	}
	
	public SimpleForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, List<ValidationError> validationErrors) throws Exception {

		Document doc = this.createDocument(req, uriParser);

		Element listElement = doc.createElement("List");
		doc.getDocumentElement().appendChild(listElement);

		if(validationErrors != null){

			XMLUtils.append(doc, listElement, validationErrors);
		}

		ArrayList<SimpleDataSourceDescriptor> dataSourceDescriptors = this.dataSourceDAO.getAll();

		ArrayList<DataSourceDescriptor> cachedDataSources = this.dataSourceCache.getCachedDataSourceDescriptors();

		if(dataSourceDescriptors != null){

			for(SimpleDataSourceDescriptor descriptor : dataSourceDescriptors){

				Element descriptorElement = descriptor.toXML(doc);
				listElement.appendChild(descriptorElement);

				descriptorElement.setAttribute("db", "true");

				if(cachedDataSources.contains(descriptor)){

					descriptorElement.setAttribute("cached", "true");
					cachedDataSources.remove(descriptor);

				}else{

					descriptorElement.setAttribute("cached", "false");
				}
			}
		}

		for(DataSourceDescriptor descriptor : cachedDataSources){

			Element descriptorElement = descriptor.toXML(doc);
			listElement.appendChild(descriptorElement);

			descriptorElement.setAttribute("db", "false");
			descriptorElement.setAttribute("cached", "true");
		}

		return new SimpleForegroundModuleResponse(doc,this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}


	public String getAddDataSourceBreadcrumbText() {
		return addDataSourceBreadcrumbText;
	}


	public String getUpdateDataSourceBreadcrumbText() {
		return updateDataSourceBreadcrumbText;
	}
}
