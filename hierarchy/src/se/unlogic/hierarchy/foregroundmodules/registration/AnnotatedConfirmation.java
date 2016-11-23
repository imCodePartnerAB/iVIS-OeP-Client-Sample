/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.registration;

import java.sql.Timestamp;

import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.time.TimeUtils;

@Table(name="registration_cofirmations")
public class AnnotatedConfirmation implements Confirmation {

	@DAOManaged
	@Key
	private Integer userID;

	@DAOManaged
	private String linkID;

	@DAOManaged
	private String host;

	@DAOManaged
	private Timestamp added;

	public AnnotatedConfirmation() {
		super();
	}

	public AnnotatedConfirmation(Integer userID, String linkID, String host) {
		super();
		this.userID = userID;
		this.linkID = linkID;
		this.host = host;
		this.added = TimeUtils.getCurrentTimestamp();
	}

	/* (non-Javadoc)
	 * @see se.unlogic.hierarchy.foregroundmodules.registration.Confirmation#getUserID()
	 */
	@Override
	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.hierarchy.foregroundmodules.registration.Confirmation#getLinkID()
	 */
	@Override
	public String getLinkID() {
		return linkID;
	}

	public void setLinkID(String linkID) {
		this.linkID = linkID;
	}

	/* (non-Javadoc)
	 * @see se.unlogic.hierarchy.foregroundmodules.registration.Confirmation#getIp()
	 */
	@Override
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}


	public Timestamp getAdded() {

		return added;
	}


	public void setAdded(Timestamp added) {

		this.added = added;
	}

	@Override
	public String toString(){

		return "user ID: " + this.userID + ", added: " + DateUtils.DATE_TIME_FORMATTER.format(this.added) + ", host: " + this.host;
	}
}
