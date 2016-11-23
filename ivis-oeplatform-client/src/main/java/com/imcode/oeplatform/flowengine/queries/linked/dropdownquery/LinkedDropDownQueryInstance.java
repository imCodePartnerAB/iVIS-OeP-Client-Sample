package com.imcode.oeplatform.flowengine.queries.linked.dropdownquery;

import com.imcode.oeplatform.flowengine.interfaces.LinkedMutableElement;
import com.imcode.oeplatform.flowengine.queries.linked.linkedalternativesquery.LinkedAlternativesQueryInstance;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryInstance;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativeQueryUtils;
import com.nordicpeak.flowengine.queries.fixedalternativesquery.FixedAlternativesQueryInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.XMLElement;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;


@Table(name = "linked_drop_down_query_instances")
@XMLElement
public class LinkedDropDownQueryInstance extends BaseQueryInstance implements LinkedAlternativesQueryInstance, FixedAlternativesQueryInstance{

	private static final long serialVersionUID = -7761759005604863873L;

//	public static Field ALTERNATIVE_RELATION = ReflectionUtils.getField(LinkedDropDownQueryInstance.class, "alternative");
	public static Field QUERY_RELATION = ReflectionUtils.getField(LinkedDropDownQueryInstance.class, "query");

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryInstanceID;

	@DAOManaged(columnName="queryID")
	@ManyToOne
	@XMLElement
	private LinkedDropDownQuery query;

//	@DAOManaged(columnName="alternativeID", populatorID = "alrernativePopulator")
//	@DAOManaged(columnName="alternativeRepresentation", populatorID = "alrernativePopulator")
	@DAOManaged(columnName="alternative")
//	@ManyToOne
	@XMLElement
	private LinkedDropDownAlternative alternative;

//	@DAOManaged
//	@XMLElement
//	private String entityClassname;

	public Integer getQueryInstanceID() {

		return queryInstanceID;
	}


	public void setQueryInstanceID(Integer queryInstanceID) {

		this.queryInstanceID = queryInstanceID;
	}


	public LinkedDropDownQuery getQuery() {

		return query;
	}


	public void setQuery(LinkedDropDownQuery query) {

		this.query = query;
	}

	@Override
	public void reset(MutableAttributeHandler attributeHandler) {

		this.alternative = null;
		super.reset(attributeHandler);
	}

	public void copyQueryValues() {}

	@Override
	public String toString() {

		return "DropDownQueryInstance (queryInstanceID=" + queryInstanceID + ")";
	}

	public LinkedDropDownAlternative getAlternative() {

		return alternative;
	}


	public void setAlternative(LinkedDropDownAlternative alternative) {

		this.alternative = alternative;
	}

	/**
	 * @return the entityClassname
	 */
//	@Override
//	public String getEntityClassname() {
//		return entityClassname;
//	}
//
//
//	/**
//	 * @param entityClassname the entityClassname to set
//	 */
//	public void setEntityClassname(String entityClassname) {
//		this.entityClassname = entityClassname;
//	}


	@Override
	public List<? extends LinkedDropDownAlternative> getAlternatives() {

		if(alternative == null){

			return null;
		}

		return Collections.singletonList(alternative);
	}

	@Override
	public String getFreeTextAlternativeValue() {
		return null;
	}

	@Override
	public Element toExportXML(Document doc, QueryHandler queryHandler) throws Exception {

		Element element = getBaseExportXML(doc);
//todo пофиксить этот вызов метода
		FixedAlternativeQueryUtils.appendExportXMLAlternatives(doc, element, this);

		return element;
	}
}
