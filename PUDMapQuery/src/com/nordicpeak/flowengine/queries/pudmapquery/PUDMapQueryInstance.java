package com.nordicpeak.flowengine.queries.pudmapquery;

import java.util.Arrays;
import java.util.List;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.XMLElement;

import com.nordicpeak.flowengine.queries.basemapquery.BaseMapQueryInstance;
import com.vividsolutions.jts.geom.Geometry;

@Table(name = "pud_map_query_instances")
@XMLElement
public class PUDMapQueryInstance extends BaseMapQueryInstance<PUDMapQuery> {

	private static final long serialVersionUID = -6796307158661195070L;

	@DAOManaged
	@XMLElement
	private Double xCoordinate;
	
	@DAOManaged
	@XMLElement
	private Double yCoordinate;
	
	@DAOManaged(columnName = "queryID")
	@ManyToOne
	@XMLElement
	private PUDMapQuery query;
	
	private Geometry geometry;
	
	public Double getXCoordinate() {
		return xCoordinate;
	}

	public void setXCoordinate(Double xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public Double getYCoordinate() {
		return yCoordinate;
	}

	public void setYCoordinate(Double yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	@Override
	public void setQuery(PUDMapQuery query) {
		
		this.query = query;
	}

	@Override
	public PUDMapQuery getQuery() {
		
		return this.query;
	}
	
	public void setPrintableGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public void reset() {

		this.xCoordinate = null;
		this.yCoordinate = null;

		super.reset();
	}

	@Override
	public List<Geometry> getPrintableGeometries() {
		
		return Arrays.asList(geometry);
	}
	
}
