package com.nordicpeak.flowengine.queries.manualmultisignquery;

import java.io.Serializable;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

import com.nordicpeak.flowengine.interfaces.SigningParty;

@Table(name = "manual_multi_sign_parties")
@XMLElement(name="SigningParty")
public class ManualSigningParty extends GeneratedElementable implements SigningParty, Serializable {

	private static final long serialVersionUID = -3437086714088347301L;

	@DAOManaged(columnName="queryInstanceID")
	@Key
	@ManyToOne
	private ManualMultiSignQueryInstance queryInstance;

	@DAOManaged
	@Key
	@XMLElement
	private String socialSecurityNumber;

	@DAOManaged
	@XMLElement
	private String name;

	@DAOManaged
	@XMLElement
	private String email;

	public ManualSigningParty(){}
	
	public ManualSigningParty(String socialSecurityNumber, String name, String email) {

		super();
		this.socialSecurityNumber = socialSecurityNumber;
		this.name = name;
		this.email = email;
	}

	public ManualMultiSignQueryInstance getQueryInstance() {

		return queryInstance;
	}

	public void setQueryInstance(ManualMultiSignQueryInstance queryInstance) {

		this.queryInstance = queryInstance;
	}

	public String getSocialSecurityNumber() {

		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {

		this.socialSecurityNumber = socialSecurityNumber;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(String email) {

		this.email = email;
	}

}
