package com.nordicpeak.flowengine.queries.textfieldquery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLUtils;

import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryInstance;

@Table(name = "text_field_query_instances")
@XMLElement
public class TextFieldQueryInstance extends BaseQueryInstance {

	private static final long serialVersionUID = -7761759005604863873L;

	public static Field VALUES_RELATION = ReflectionUtils.getField(TextFieldQueryInstance.class, "values");
	public static Field QUERY_RELATION = ReflectionUtils.getField(TextFieldQueryInstance.class, "query");

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryInstanceID;

	@DAOManaged(columnName="queryID")
	@ManyToOne
	@XMLElement
	private TextFieldQuery query;

	@DAOManaged
	@OneToMany
	@XMLElement(fixCase=true)
	private List<TextFieldValue> values;

	public Integer getQueryInstanceID() {

		return queryInstanceID;
	}


	public void setQueryInstanceID(Integer queryInstanceID) {

		this.queryInstanceID = queryInstanceID;
	}


	public TextFieldQuery getQuery() {

		return query;
	}


	public void setQuery(TextFieldQuery query) {

		this.query = query;
	}


	@Override
	public String toString() {

		return "TextFieldQueryQueryInstance (queryInstanceID=" + queryInstanceID + ")";
	}

	@Override
	public void reset(MutableAttributeHandler attributeHandler) {

		if(this.values != null){

			for(TextFieldValue textFieldValue : values){

				if(textFieldValue.getTextField().isSetAsAttribute()){

					attributeHandler.removeAttribute(textFieldValue.getTextField().getAttributeName());
				}
			}
		}

		this.values = null;

		super.reset(attributeHandler);
	}


	public List<TextFieldValue> getValues() {

		return values;
	}


	public void setValues(List<TextFieldValue> values) {

		this.values = values;
	}

	public String getFieldValue(String label) {

		if(this.values != null){

			for(TextFieldValue textFieldValue : this.values){

				if(textFieldValue.getTextField().getLabel().equals(label)){

					return textFieldValue.getValue();
				}
			}
		}

		return null;
	}

	@Override
	public Element toExportXML(Document doc, QueryHandler queryHandler) throws Exception {

		Element element = getBaseExportXML(doc);

		if(this.values != null){

			//TODO this code needs to be sorted later on as it may lead to different element names than the generated XSD since not all labels are iterated over, preferably the element names of the fields should be set when configuring the query

			ArrayList<String> fieldElementNames = new ArrayList<String>(this.values.size());

			for(TextFieldValue textFieldValue : this.values){

				XMLUtils.appendNewCDATAElement(doc, element, TextFieldQuery.generateElementName(textFieldValue.getTextField().getLabel(), fieldElementNames), textFieldValue.getValue());
			}
		}

		return element;
	}
}
