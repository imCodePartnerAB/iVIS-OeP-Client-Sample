package com.nordicpeak.flowengine.queries.pudquery;

import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.annotations.FCKContent;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLValidationUtils;
import se.unlogic.webutils.annotations.URLRewrite;

import com.nordicpeak.flowengine.annotations.TextTagReplace;
import com.nordicpeak.flowengine.queries.basequery.BaseQuery;

@Table(name = "pud_query_queries")
@XMLElement
public class PUDQuery extends BaseQuery {

	private static final long serialVersionUID = -1356231798937567819L;

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryID;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement
	private String description;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement
	private String helpText;

	@DAOManaged
	@OneToMany(autoAdd=true, autoUpdate=true, autoGet=true)
	@SimplifiedRelation(table = "pud_query_allowed_search_services", remoteValueColumnName = "service")
	@WebPopulate(required=true, maxLength=15, paramName="allowedSearchService")
	@XMLElement(fixCase=true,childName="allowedSearchService")
	private List<SearchService> allowedSearchServices;
	
	@DAOManaged
	@OneToMany
	@XMLElement
	private List<PUDQueryInstance> instances;
	
	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public List<SearchService> getAllowedSearchServices() {
		return allowedSearchServices;
	}

	public void setAllowedSearchServices(List<SearchService> allowedSearchServices) {
		this.allowedSearchServices = allowedSearchServices;
	}

	public List<PUDQueryInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<PUDQueryInstance> instances) {
		this.instances = instances;
	}

	public void setQueryID(Integer queryID) {
		this.queryID = queryID;
	}

	@Override
	public Integer getQueryID() {
		return this.queryID;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		
		return this.description;
	}
	
	@Override
	public String toString() {

		if (this.queryDescriptor != null) {

			return queryDescriptor.getName() + " (queryID: " + queryID + ")";
		}

		return "PUDQuery (queryID: " + queryID + ")";
	}
	
	@Override
	public String getXSDTypeName() {
		
		return null;
	}

	@Override
	public void populate(XMLParser xmlParser) throws ValidationException {

		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		description = XMLValidationUtils.validateParameter("description", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		helpText = XMLValidationUtils.validateParameter("helpText", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		
		XMLParser serviceParser = xmlParser.getNode("AllowedSearchServices");
		
		if(serviceParser != null) {
			
			allowedSearchServices = XMLValidationUtils.validateParameters("allowedSearchService", serviceParser, false, PUDQueryCRUD.SEARCH_SERVICE_POPULATOR, errors);
			
		}
		
		if(!errors.isEmpty()){

			throw new ValidationException(errors);
		}
		
	}
	
}
