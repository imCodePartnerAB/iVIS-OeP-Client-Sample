package com.imcode.oeplatform.flowengine.queries.radiobuttonquery;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import se.unlogic.hierarchy.core.annotations.FCKContent;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.datatypes.Matrix;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLParser;
import se.unlogic.standardutils.xml.XMLValidationUtils;
import se.unlogic.webutils.annotations.URLRewrite;

import com.nordicpeak.flowengine.annotations.TextTagReplace;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesBaseQuery;
import com.nordicpeak.flowengine.queries.tablequery.SummaryTableQuery;
import com.nordicpeak.flowengine.queries.tablequery.SummaryTableQueryUtils;


@Table(name = "ivis_radio_button_queries")
@XMLElement
public class RadioButtonQuery extends FixedAlternativesBaseQuery implements SummaryTableQuery{

	private static final long serialVersionUID = -842191226937409416L;

	public static final Field ALTERNATIVES_RELATION = ReflectionUtils.getField(RadioButtonQuery.class, "alternatives");

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryID;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement(cdata=true)
	private String description;

	@FCKContent
	@TextTagReplace
	@URLRewrite
	@DAOManaged
	@WebPopulate(maxLength = 65535)
	@XMLElement(cdata=true)
	private String helpText;

	@DAOManaged
	@OneToMany(autoUpdate=true, autoAdd=true)
	@XMLElement(fixCase=true)
	private List<RadioButtonAlternative> alternatives;

	@WebPopulate(maxLength = 255)
	@DAOManaged
	@XMLElement
	private String freeTextAlternative;

	@DAOManaged
	@OneToMany
	@XMLElement
	private List<RadioButtonQueryInstance> instances;

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

	@Override
	public Integer getQueryID() {

		return queryID;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public List<RadioButtonAlternative> getAlternatives() {

		return alternatives;
	}

	public List<RadioButtonQueryInstance> getInstances() {

		return instances;
	}

	public void setInstances(List<RadioButtonQueryInstance> instances) {

		this.instances = instances;
	}

	public void setQueryID(int queryID) {

		this.queryID = queryID;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public void setAlternatives(List<RadioButtonAlternative> alternatives) {

		this.alternatives = alternatives;
	}

	@Override
	public String getFreeTextAlternative() {
		return freeTextAlternative;
	}

	public void setFreeTextAlternative(String freeTextAlternative) {
		this.freeTextAlternative = freeTextAlternative;
	}

	public String getHelpText() {

		return helpText;
	}

	public void setHelpText(String helpText) {

		this.helpText = helpText;
	}

	@Override
	public String toString() {

		if(this.queryDescriptor != null){

			return queryDescriptor.getName() + " (queryID: " + queryID + ")";
		}

		return "RadioButtonQuery (queryID: " + queryID + ")";
	}

	@Override
	public String getXSDTypeName() {

		return "RadioButtonQuery" + queryID;
	}

	@Override
	public void toXSD(Document doc) {

		toXSD(doc, 1);
	}

	@Override
	public Matrix<String> getDataTable(List<Integer> queryInstanceIDs, QueryHandler queryHandler) throws SQLException {

		return SummaryTableQueryUtils.getGenericTableQueryCallback(this.getClass(), queryHandler, getQueryDescriptor().getQueryTypeID()).getSummaryTable(this, queryInstanceIDs);
	}
	
	@Override
	public void populate(XMLParser xmlParser) throws ValidationException {

		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		description = XMLValidationUtils.validateParameter("description", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		helpText = XMLValidationUtils.validateParameter("helpText", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
		freeTextAlternative = XMLValidationUtils.validateParameter("freeTextAlternative", xmlParser, false, 1, 255, StringPopulator.getPopulator(), errors);
		
		alternatives = RadioButtonQueryCRUD.ALTERNATIVES_POPLATOR.populate(xmlParser, errors);
		
		if(!errors.isEmpty()){

			throw new ValidationException(errors);
		}
		
	}
	
}
