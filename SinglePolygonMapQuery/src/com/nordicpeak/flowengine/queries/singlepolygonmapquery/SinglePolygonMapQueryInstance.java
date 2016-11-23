package com.nordicpeak.flowengine.queries.singlepolygonmapquery;

import java.util.Arrays;
import java.util.List;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.XMLElement;

import com.nordicpeak.flowengine.queries.basemapquery.BaseMapQueryInstance;
import com.vividsolutions.jts.geom.Geometry;

@Table(name = "single_polygon_map_query_instances")
@XMLElement
public class SinglePolygonMapQueryInstance extends BaseMapQueryInstance<SinglePolygonMapQuery> {

	private static final long serialVersionUID = -6796307158661195070L;

	@DAOManaged
	@XMLElement
	private String polygon;
	
	@DAOManaged
	@XMLElement
	private String polygonConfig;
	
	@DAOManaged(columnName = "queryID")
	@ManyToOne
	@XMLElement
	private SinglePolygonMapQuery query;

	private Geometry geometry;
	
	public String getPolygon() {
		return polygon;
	}

	public void setPolygon(String polygon) {
		this.polygon = polygon;
	}

	public String getPolygonConfig() {
		return polygonConfig;
	}

	public void setPolygonConfig(String polygonConfig) {
		this.polygonConfig = polygonConfig;
	}

	public void setPrintableGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@Override
	public void setQuery(SinglePolygonMapQuery query) {
		
		this.query = query;
	}

	@Override
	public SinglePolygonMapQuery getQuery() {
		
		return this.query;
	}
	
	@Override
	public List<Geometry> getPrintableGeometries() {
		
		return Arrays.asList(geometry);
	}
	
	@Override
	public void reset() {

		this.polygon = null;

		super.reset();
	}

	@Override
	public String toString() {
		return "SinglePolygonMapQueryInstance [queryInstanceID=" + getQueryInstanceID() + ", propertyUnitNumber=" + getPropertyUnitDesignation() + "]";
	}
	
}
