package com.nordicpeak.flowengine.integration.callback;

import java.util.Date;

public class IntegrationMessage {

	protected String message;

	protected String userID;

	protected Date added;

	protected IntegrationAttachment[] attachments;

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public String getUserID() {

		return userID;
	}

	public void setUserID(String userID) {

		this.userID = userID;
	}

	public Date getAdded() {

		return added;
	}

	public void setAdded(Date added) {

		this.added = added;
	}

	public IntegrationAttachment[] getAttachments() {

		return attachments;
	}

	public void setAttachments(IntegrationAttachment[] attachments) {

		this.attachments = attachments;
	}

}
