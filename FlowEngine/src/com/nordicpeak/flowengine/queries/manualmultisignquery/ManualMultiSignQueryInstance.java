package com.nordicpeak.flowengine.queries.manualmultisignquery;

import java.lang.reflect.Field;
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

import com.nordicpeak.flowengine.interfaces.MultiSigningQuery;
import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryInstance;
import com.nordicpeak.flowengine.queries.checkboxquery.CheckboxQueryInstance;

@Table(name = "manual_multi_sign_query_instances")
@XMLElement
public class ManualMultiSignQueryInstance extends BaseQueryInstance implements MultiSigningQuery{

	private static final long serialVersionUID = 2847121037559137804L;

	public static final Field SIGNING_PARTIES_RELATION = ReflectionUtils.getField(ManualMultiSignQueryInstance.class, "signingParties");
	public static final Field QUERY_RELATION = ReflectionUtils.getField(CheckboxQueryInstance.class, "query");

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryInstanceID;

	@DAOManaged(columnName = "queryID")
	@ManyToOne
	@XMLElement
	private ManualMultiSignQuery query;

	@DAOManaged
	@OneToMany
	@XMLElement(fixCase=true)
	private List<ManualSigningParty> signingParties;

	public Integer getQueryInstanceID() {

		return queryInstanceID;
	}

	public void setQueryInstanceID(Integer queryInstanceID) {

		this.queryInstanceID = queryInstanceID;
	}

	@Override
	public void reset(MutableAttributeHandler attributeHandler) {

		this.signingParties = null;
		super.reset(attributeHandler);
	}

	public void copyQueryValues() {

	}

	@Override
	public String toString() {

		return "ManualMultiSignQueryInstance (queryInstanceID=" + queryInstanceID + ")";
	}

	public ManualMultiSignQuery getQuery() {

		return query;
	}

	public void setQuery(ManualMultiSignQuery query) {

		this.query = query;
	}

	public List<ManualSigningParty> getSigningParties() {

		return signingParties;
	}

	public void setSigningParties(List<ManualSigningParty> signingParties) {

		this.signingParties = signingParties;
	}

	@Override
	public Element toExportXML(Document doc, QueryHandler queryHandler) {

		// TODO implement XML export support

		return null;
	}

}
