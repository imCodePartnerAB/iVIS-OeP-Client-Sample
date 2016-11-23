package com.nordicpeak.flowengine.queries.pudquery;

import java.lang.reflect.Field;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.XMLElement;

import com.nordicpeak.flowengine.queries.basequery.BaseQueryInstance;

@Table(name = "pud_query_instances")
@XMLElement
public class PUDQueryInstance extends BaseQueryInstance {

	private static final long serialVersionUID = -405719839906745735L;

	public static Field QUERY_RELATION = ReflectionUtils.getField(PUDQueryInstance.class, "query");
	
	@DAOManaged
	@Key
	@XMLElement
	private Integer queryInstanceID;

	@DAOManaged(columnName = "queryID")
	@ManyToOne
	@XMLElement
	private PUDQuery query;

	@DAOManaged
	@XMLElement
	private String propertyUnitDesignation;

	@DAOManaged
	@XMLElement
	private Integer propertyUnitNumber;
	
	public Integer getQueryInstanceID() {
		return queryInstanceID;
	}

	public void setQueryInstanceID(Integer queryInstanceID) {
		this.queryInstanceID = queryInstanceID;
	}

	public PUDQuery getQuery() {
		return query;
	}

	public void setQuery(PUDQuery query) {
		this.query = query;
	}

	public String getPropertyUnitDesignation() {
		return propertyUnitDesignation;
	}

	public void setPropertyUnitDesignation(String propertyUnitDesignation) {
		this.propertyUnitDesignation = propertyUnitDesignation;
	}

	public Integer getPropertyUnitNumber() {
		return propertyUnitNumber;
	}

	public void setPropertyUnitNumber(Integer propertyUnitNumber) {
		this.propertyUnitNumber = propertyUnitNumber;
	}

	@Override
	public void reset() {

		this.propertyUnitDesignation = null;
		this.setPropertyUnitNumber(null);
		
		super.reset();
	}

	@Override
	public String toString() {

		return "PUDQueryInstance (queryInstanceID=" + queryInstanceID + ")";
	}
	
	@Override
	public String getStringValue() {
		
		return propertyUnitDesignation;
	}

	public void copyQueryValues() {}
	
}
