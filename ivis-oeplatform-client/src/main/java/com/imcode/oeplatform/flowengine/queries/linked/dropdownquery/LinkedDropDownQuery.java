package com.imcode.oeplatform.flowengine.queries.linked.dropdownquery;

import com.imcode.entities.interfaces.JpaEntity;
import com.imcode.oeplatform.flowengine.populators.JsonStringfier;
import com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery.LinkedAlternativesBaseQuery;
import com.imcode.services.GenericService;
import com.nordicpeak.flowengine.annotations.TextTagReplace;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.tablequery.SummaryTableQuery;
import com.nordicpeak.flowengine.queries.tablequery.SummaryTableQueryUtils;
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

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Table(name = "linked_drop_down_queries")
@XMLElement
public class LinkedDropDownQuery extends LinkedAlternativesBaseQuery implements SummaryTableQuery {

    public static final Field ALTERNATIVES_RELATION = ReflectionUtils.getField(LinkedDropDownQuery.class, "alternatives");

    @DAOManaged
    @Key
    @XMLElement
    private Integer queryID;

    @TextTagReplace
    @DAOManaged
    @WebPopulate(maxLength = 255)
    @XMLElement
    private String shortDescription;

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
    @XMLElement
    private String helpText;

//    @ModuleSetting
//    @TextFieldSettingDescriptor(name = "Client id", description = "iVIS client id", required = true)
//    protected String clientId;
//    @TextTagReplace
//    @DAOManaged
//    @WebPopulate(maxLength = 255)
//    @XMLElement
//    private String entityClassName;

    //    @DAOManaged
//    @OneToMany(autoUpdate = true, autoAdd = true)
    @XMLElement(fixCase = true)
    private List<LinkedDropDownAlternative> alternatives;

    @XMLElement(valueFormatter = JsonStringfier.class)
    private Object entities;

    @DAOManaged
    @OneToMany
    @XMLElement
    private List<LinkedDropDownQueryInstance> instances;


    private GenericService entityService;

    @Override
    public Integer getQueryID() {

        return queryID;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public List<LinkedDropDownAlternative> getAlternatives() {

        return alternatives;
    }

    public void setAlternatives(List<LinkedDropDownAlternative> alternatives) {

        this.alternatives = alternatives;
    }

//	@Override
//	public String getFreeTextAlternative() {
//		return freeTextAlternative;
//	}
//
//	public void setFreeTextAlternative(String freeTextAlternative) {
//		this.freeTextAlternative = freeTextAlternative;
//	}

    public List<LinkedDropDownQueryInstance> getInstances() {

        return instances;
    }

    public void setInstances(List<LinkedDropDownQueryInstance> instances) {

        this.instances = instances;
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

    public String getShortDescription() {

        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {

        this.shortDescription = shortDescription;
    }

    @Override
    public String toString() {

        if (this.queryDescriptor != null) {

            return queryDescriptor.getName() + " (queryID: " + queryID + ")";
        }

        return "LinkedDropDownQuery (queryID: " + queryID + ")";
    }

    @Override
    public String getXSDTypeName() {

        return "LinkedDropDownQuery" + queryID;
    }

    @Override
    public void toXSD(Document doc) {

        toXSD(doc, 1);
    }

    @Override
    public Matrix<String> getDataTable(List<Integer> queryInstanceIDs, QueryHandler queryHandler) throws SQLException {

        return SummaryTableQueryUtils.getGenericTableQueryCallback(this.getClass(), queryHandler, getQueryDescriptor().getQueryTypeID()).getSummaryTable(this, queryInstanceIDs);
    }

//    public String getEntityClassName() {
//        return entityClassName;
//    }
//
//    public void setEntityClassName(String entityClassName) {
//        this.entityClassName = entityClassName;
//    }

    @Override
    public GenericService getEntityService() {
        return entityService;
    }

    @Override
    public void setEntityService(GenericService entityService) {
        this.entityService = entityService;
    }

    @Override
    public void populate(XMLParser xmlParser) throws ValidationException {

        List<ValidationError> errors = new ArrayList<ValidationError>();

        shortDescription = XMLValidationUtils.validateParameter("shortDescription", xmlParser, false, 1, 255, StringPopulator.getPopulator(), errors);
        description = XMLValidationUtils.validateParameter("description", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
        helpText = XMLValidationUtils.validateParameter("helpText", xmlParser, false, 1, 65535, StringPopulator.getPopulator(), errors);
//		freeTextAlternative = XMLValidationUtils.validateParameter("freeTextAlternative", xmlParser, false, 1, 255, StringPopulator.getPopulator(), errors);
//        setEntityClassname(XMLValidationUtils.validateParameter("entityClassname", xmlParser, false, 1, 255, StringPopulator.getPopulator(), errors));
        setEntityClassname(XMLValidationUtils.validateParameter("entityClassname", xmlParser, false, 1, 255, StringPopulator.getPopulator(), errors));

        alternatives = LinkedDropDownQueryCRUD.ALTERNATIVES_POPLATOR.populate(xmlParser, errors);

        if (!errors.isEmpty()) {

            throw new ValidationException(errors);
        }

    }

    @SuppressWarnings("unchecked")
    public List<JpaEntity> getEntities() {
        return (List<JpaEntity>) entities;
    }

    public void setEntities(List<JpaEntity> entities) {
        this.entities = entities;
    }
}
