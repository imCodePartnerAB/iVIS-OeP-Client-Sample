package com.nordicpeak.flowengine.signingproviders;

import java.sql.Timestamp;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@Table(name = "dummy_multi_sign_signatures")
@XMLElement
public class Signature extends GeneratedElementable {

	@DAOManaged
	@Key
	@XMLElement
	private Integer flowInstanceID;

	@DAOManaged
	@Key
	@XMLElement
	private String socialSecurityNumber;

	@DAOManaged
	@XMLElement
	private Timestamp added;

	public Integer getFlowInstanceID() {

		return flowInstanceID;
	}

	public void setFlowInstanceID(Integer flowInstanceID) {

		this.flowInstanceID = flowInstanceID;
	}

	public String getSocialSecurityNumber() {

		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {

		this.socialSecurityNumber = socialSecurityNumber;
	}

	public Timestamp getAdded() {

		return added;
	}

	public void setAdded(Timestamp added) {

		this.added = added;
	}

}
