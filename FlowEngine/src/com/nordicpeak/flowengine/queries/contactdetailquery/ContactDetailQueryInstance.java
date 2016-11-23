package com.nordicpeak.flowengine.queries.contactdetailquery;

import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.AttributeHandler;
import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLUtils;

import com.nordicpeak.flowengine.interfaces.QueryHandler;
import com.nordicpeak.flowengine.queries.basequery.BaseQueryInstance;

@Table(name = "contact_detail_query_instances")
@XMLElement
public class ContactDetailQueryInstance extends BaseQueryInstance {

	private static final long serialVersionUID = -7761759005604863873L;

	public static Field QUERY_RELATION = ReflectionUtils.getField(ContactDetailQueryInstance.class, "query");

	@DAOManaged
	@Key
	@XMLElement
	private Integer queryInstanceID;

	@DAOManaged(columnName = "queryID")
	@ManyToOne
	@XMLElement
	private ContactDetailQuery query;

	@DAOManaged
	@XMLElement
	private String firstname;

	@DAOManaged
	@XMLElement
	private String lastname;

	@DAOManaged
	@XMLElement
	private String address;

	@DAOManaged
	@XMLElement
	private String zipCode;

	@DAOManaged
	@XMLElement
	private String postalAddress;

	@DAOManaged
	@XMLElement
	private String phone;

	@DAOManaged
	@XMLElement
	private String email;

	@DAOManaged
	@XMLElement
	private String mobilePhone;

	@DAOManaged
	@XMLElement
	private boolean contactBySMS;

	@DAOManaged
	@XMLElement
	private boolean persistUserProfile;

	@XMLElement
	private boolean isMutableUser;

	public String getAddress() {

		return address;
	}

	public String getFirstname() {

		return firstname;
	}

	public void setFirstname(String firstname) {

		this.firstname = firstname;
	}

	public String getLastname() {

		return lastname;
	}

	public void setLastname(String lastname) {

		this.lastname = lastname;
	}

	public void setAddress(String address) {

		this.address = address;
	}

	public String getZipCode() {

		return zipCode;
	}

	public void setZipCode(String zipCode) {

		this.zipCode = zipCode;
	}

	public String getPostalAddress() {

		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {

		this.postalAddress = postalAddress;
	}

	public String getPhone() {

		return phone;
	}

	public void setPhone(String phone) {

		this.phone = phone;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(String email) {

		this.email = email;
	}

	public String getMobilePhone() {

		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {

		this.mobilePhone = mobilePhone;
	}

	public boolean isContactBySMS() {

		return contactBySMS;
	}

	public void setContactBySMS(boolean contactBySMS) {

		this.contactBySMS = contactBySMS;
	}

	public boolean isPersistUserProfile() {

		return persistUserProfile;
	}

	public void setPersistUserProfile(boolean persistUserProfile) {

		this.persistUserProfile = persistUserProfile;
	}

	public Integer getQueryInstanceID() {

		return queryInstanceID;
	}

	public void setQueryInstanceID(Integer queryInstanceID) {

		this.queryInstanceID = queryInstanceID;
	}

	public ContactDetailQuery getQuery() {

		return query;
	}

	public void setQuery(ContactDetailQuery query) {

		this.query = query;
	}

	public boolean isMutableUser() {

		return isMutableUser;
	}

	public boolean isPopulated() {

		if (StringUtils.isEmpty(firstname) && StringUtils.isEmpty(lastname) && StringUtils.isEmpty(address) && StringUtils.isEmpty(zipCode) && StringUtils.isEmpty(postalAddress) && StringUtils.isEmpty(phone) && StringUtils.isEmpty(email) && StringUtils.isEmpty(mobilePhone)) {

			return false;
		}

		return true;
	}

	@Override
	public void reset(MutableAttributeHandler attributeHandler) {

		this.firstname = null;
		this.lastname = null;
		this.address = null;
		this.zipCode = null;
		this.postalAddress = null;
		this.phone = null;
		this.email = null;
		this.mobilePhone = null;
		this.contactBySMS = false;
		super.reset(attributeHandler);
	}

	@Override
	public String toString() {

		return "ContactDetailQueryInstance (queryInstanceID=" + queryInstanceID + ")";
	}

	public void initialize(User user) {

		this.firstname = user.getFirstname();
		this.lastname = user.getLastname();
		this.email = user.getEmail();

		AttributeHandler attributeHandler = user.getAttributeHandler();

		if (attributeHandler != null) {

			this.address = attributeHandler.getString("address");
			this.zipCode = attributeHandler.getString("zipCode");
			this.postalAddress = attributeHandler.getString("postalAddress");
			this.mobilePhone = attributeHandler.getString("mobilePhone");
			this.phone = attributeHandler.getString("phone");

			this.contactBySMS = attributeHandler.getPrimitiveBoolean("contactBySMS");
		}

		this.isMutableUser = user instanceof MutableUser;
	}

	@Override
	public Element toExportXML(Document doc, QueryHandler queryHandler) throws Exception {

		Element element = getBaseExportXML(doc);

		XMLUtils.appendNewCDATAElement(doc, element, "Firstname", firstname);
		XMLUtils.appendNewCDATAElement(doc, element, "Lastname", lastname);
		XMLUtils.appendNewCDATAElement(doc, element, "Address", address);
		XMLUtils.appendNewCDATAElement(doc, element, "ZipCode", zipCode);
		XMLUtils.appendNewCDATAElement(doc, element, "PostalAddress", postalAddress);
		XMLUtils.appendNewCDATAElement(doc, element, "Phone", phone);
		XMLUtils.appendNewCDATAElement(doc, element, "Email", email);
		XMLUtils.appendNewCDATAElement(doc, element, "MobilePhone", mobilePhone);
		XMLUtils.appendNewCDATAElement(doc, element, "ContactBySMS", contactBySMS);

		return element;
	}
}
