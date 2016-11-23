package com.imcode.oeplatform.flowengine.queries.textareaquery;

import com.imcode.oeplatform.flowengine.queries.DependentField;
import com.imcode.oeplatform.flowengine.queries.DependentFieldValue;
import com.imcode.oeplatform.flowengine.queries.DependentQuery;
import com.imcode.oeplatform.flowengine.queries.DependentQueryInstance;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Table(name = "ivis_text_area_query_instances")
@XMLElement
public class TextAreaQueryInstance extends BaseQueryInstance {

	private static final long serialVersionUID = -7761759005604863873L;

	public static Field QUERY_RELATION = ReflectionUtils.getField(TextAreaQueryInstance.class, "query");

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryInstanceID;

	@DAOManaged(columnName = "queryID")
	@ManyToOne
	@XMLElement
	private TextAreaQuery query;

	@DAOManaged
	@XMLElement
	private String value;

	public Integer getQueryInstanceID() {

		return queryInstanceID;
	}

	public void setQueryInstanceID(Integer queryInstanceID) {

		this.queryInstanceID = queryInstanceID;
	}

	public TextAreaQuery getQuery() {

		return query;
	}

	public void setQuery(TextAreaQuery query) {

		this.query = query;
	}

	@Override
	public void reset(MutableAttributeHandler attributeHandler) {

		this.value = null;
		super.reset(attributeHandler);
	}

	public void copyQueryValues() {

	}

	@Override
	public String toString() {

		return "TextAreaQueryInstance (queryInstanceID=" + queryInstanceID + ")";
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	@Override
	public Element toExportXML(Document doc, QueryHandler queryHandler) throws Exception {

		Element element = getBaseExportXML(doc);

		XMLUtils.appendNewCDATAElement(doc, element, "Value", value);

		return element;
	}

//	@Override
//	public DependentField getQueryField() {
//		return null;
//	}

//	@Override
//	@SuppressWarnings("unchecked")
//	public List<? extends DependentQuery> getValues() {
//		return (List<? extends DependentQuery>) Collections.singletonList(this);
//	}
//
//	@Override
//	public <T extends DependentQuery> T getQuery() {
//		return getQuery();
//	}

}
