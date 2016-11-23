package com.nordicpeak.flowengine.queries.manualmultisignquery;

import java.util.ArrayList;
import java.util.List;

import se.unlogic.hierarchy.core.annotations.FCKContent;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.populators.NonNegativeStringIntegerPopulator;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLValidationUtils;
import se.unlogic.webutils.annotations.URLRewrite;

import com.nordicpeak.flowengine.annotations.TextTagReplace;
import com.nordicpeak.flowengine.queries.basequery.BaseQuery;

@Table(name = "manual_multi_sign_queries")
@XMLElement
public class ManualMultiSignQuery extends BaseQuery {

	private static final long serialVersionUID = 3734201104262524858L;

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryID;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement(cdata = true)
	private String description;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement(cdata = true)
	private String helpText;

	@DAOManaged
	@WebPopulate(populator=NonNegativeStringIntegerPopulator.class)
	@XMLElement
	private Integer maxParties;	
	
	@DAOManaged
	@OneToMany
	@XMLElement
	private List<ManualMultiSignQueryInstance> instances;

	@Override
	public Integer getQueryID() {

		return queryID;
	}

	@Override
	public String getDescription() {

		return description;
	}

	public void setQueryID(int queryID) {

		this.queryID = queryID;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getHelpText() {

		return helpText;
	}

	public void setHelpText(String helpText) {

		this.helpText = helpText;
	}

	@Override
	public String toString() {

		if(this.queryDescriptor != null) {

			return queryDescriptor.getName() + " (queryID: " + queryID + ")";
		}

		return "ManualMultiSignQuery (queryID: " + queryID + ")";
	}

	@Override
	public String getXSDTypeName() {

		return "ManualMultiSignQuery" + queryID;
	}

	@Override
	public void populate(XMLParser xmlParser) throws ValidationException {

		List<ValidationError> errors = new ArrayList<ValidationError>();

		description = XMLValidationUtils.validateParameter("description", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		helpText = XMLValidationUtils.validateParameter("helpText", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		maxParties = xmlParser.getInteger("maxParties");

		if(!errors.isEmpty()) {

			throw new ValidationException(errors);
		}
	}

	public List<ManualMultiSignQueryInstance> getInstances() {

		return instances;
	}

	public void setInstances(List<ManualMultiSignQueryInstance> instances) {

		this.instances = instances;
	}

	public void setQueryID(Integer queryID) {

		this.queryID = queryID;
	}
}
