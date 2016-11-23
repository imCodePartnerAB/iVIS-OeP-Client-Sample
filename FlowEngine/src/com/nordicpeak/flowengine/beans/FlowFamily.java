package com.nordicpeak.flowengine.beans;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import se.unlogic.emailutils.populators.EmailPopulator;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.OneToMany;
import se.unlogic.standardutils.dao.annotations.SimplifiedRelation;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

import com.nordicpeak.flowengine.enums.StatisticsMode;
import com.nordicpeak.flowengine.interfaces.ImmutableFlowFamily;
import com.nordicpeak.flowengine.interfaces.ImmutableUserFavourite;

@Table(name = "flowengine_flow_families")
@XMLElement
public class FlowFamily extends GeneratedElementable implements Serializable, ImmutableFlowFamily, AccessInterface {

	private static final long serialVersionUID = 6716050201654571775L;

	public static final Field FLOWS_RELATION = ReflectionUtils.getField(FlowFamily.class, "flows");
	public static final Field MANAGER_GROUPS_RELATION = ReflectionUtils.getField(FlowFamily.class, "managerGroupIDs");
	public static final Field MANAGER_USERS_RELATION = ReflectionUtils.getField(FlowFamily.class, "managerUserIDs");

	@DAOManaged(autoGenerated = true)
	@Key
	@XMLElement
	private Integer flowFamilyID;

	@DAOManaged
	@XMLElement
	private Integer versionCount;

	@DAOManaged
	@WebPopulate(maxLength = 255)
	@XMLElement
	private String contactName;

	@DAOManaged
	@WebPopulate(maxLength = 255, populator = EmailPopulator.class)
	@XMLElement
	private String contactEmail;

	@DAOManaged
	@WebPopulate(maxLength = 255)
	@XMLElement
	private String contactPhone;

	@DAOManaged
	@WebPopulate(maxLength = 255)
	@XMLElement
	private String ownerName;

	@DAOManaged
	@WebPopulate(maxLength = 255, populator = EmailPopulator.class)
	@XMLElement
	private String ownerEmail;

	@DAOManaged
	@WebPopulate
	@XMLElement
	private StatisticsMode statisticsMode;

	@DAOManaged
	@OneToMany(autoUpdate = true)
	@SimplifiedRelation(table = "flowengine_flow_family_manager_groups", remoteValueColumnName = "groupID")
	@WebPopulate(paramName = "group")
	@XMLElement(childName = "groupID")
	private List<Integer> managerGroupIDs;

	@DAOManaged
	@OneToMany(autoUpdate = true)
	@SimplifiedRelation(table = "flowengine_flow_family_manager_users", remoteValueColumnName = "userID")
	@WebPopulate(paramName = "user")
	@XMLElement(childName = "userID")
	private List<Integer> managerUserIDs;

	@DAOManaged
	@OneToMany
	@XMLElement
	private List<Flow> flows;

	@DAOManaged
	@OneToMany
	@XMLElement(fixCase = true)
	private List<UserFavourite> userFavourites;

	private Integer flowInstanceCount;

	@Override
	public Integer getFlowFamilyID() {

		return flowFamilyID;
	}

	public void setFlowFamilyID(Integer flowFamilyID) {

		this.flowFamilyID = flowFamilyID;
	}

	@Override
	public Integer getVersionCount() {

		return versionCount;
	}

	public void setVersionCount(Integer currentIncrement) {

		this.versionCount = currentIncrement;
	}

	public String getContactName() {

		return contactName;
	}

	public void setContactName(String contactName) {

		this.contactName = contactName;
	}

	public String getContactEmail() {

		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {

		this.contactEmail = contactEmail;
	}

	public String getContactPhone() {

		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {

		this.contactPhone = contactPhone;
	}

	public String getOwnerName() {

		return ownerName;
	}

	public void setOwnerName(String ownerName) {

		this.ownerName = ownerName;
	}

	public String getOwnerEmail() {

		return ownerEmail;
	}

	public void setOwnerEmail(String ownerEmail) {

		this.ownerEmail = ownerEmail;
	}

	public void setManagerGroupIDs(List<Integer> managerGroupIDs) {

		this.managerGroupIDs = managerGroupIDs;
	}

	public void setManagerUserIDs(List<Integer> managerUserIDs) {

		this.managerUserIDs = managerUserIDs;
	}

	@Override
	public List<Flow> getFlows() {

		return flows;
	}

	public void setFlows(List<Flow> flows) {

		this.flows = flows;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((flowFamilyID == null) ? 0 : flowFamilyID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FlowFamily other = (FlowFamily) obj;
		if (flowFamilyID == null) {
			if (other.flowFamilyID != null) {
				return false;
			}
		} else if (!flowFamilyID.equals(other.flowFamilyID)) {
			return false;
		}
		return true;
	}

	public Integer getFlowInstanceCount() {

		return flowInstanceCount;
	}

	public void setFlowInstanceCount(Integer flowInstanceCount) {

		this.flowInstanceCount = flowInstanceCount;
	}

	public static long getSerialversionuid() {

		return serialVersionUID;
	}

	public List<Integer> getManagerGroupIDs() {

		return managerGroupIDs;
	}

	public List<Integer> getManagerUserIDs() {

		return managerUserIDs;
	}

	@Override
	public boolean allowsAdminAccess() {

		return false;
	}

	@Override
	public boolean allowsUserAccess() {

		return false;
	}

	@Override
	public boolean allowsAnonymousAccess() {

		return false;
	}

	@Override
	public List<Integer> getAllowedGroupIDs() {

		return managerGroupIDs;
	}

	@Override
	public List<Integer> getAllowedUserIDs() {

		return managerUserIDs;
	}

	@Override
	public List<? extends ImmutableUserFavourite> getUserFavourites() {

		return userFavourites;
	}

	public void setUserFavourites(List<UserFavourite> userFavourites) {

		this.userFavourites = userFavourites;
	}

	@Override
	public String toString() {

		return "ID: " + flowFamilyID;
	}


	public StatisticsMode getStatisticsMode() {

		return statisticsMode;
	}


	public void setStatisticsMode(StatisticsMode statisticsMode) {

		this.statisticsMode = statisticsMode;
	}

}
