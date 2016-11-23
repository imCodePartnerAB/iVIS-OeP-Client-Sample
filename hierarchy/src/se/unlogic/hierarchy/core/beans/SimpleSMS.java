package se.unlogic.hierarchy.core.beans;

import java.io.Serializable;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.MutableAttributeHandler;
import se.unlogic.hierarchy.core.interfaces.SMS;
import se.unlogic.standardutils.string.StringUtils;

public class SimpleSMS implements SMS, Serializable{

	private static final long serialVersionUID = 7532385181545705535L;

	private String senderName;

	private String message;

	private List<String> recipients;

	private MutableAttributeHandler attributeHandler;

	public String getSenderName() {

		return senderName;
	}

	public void setSenderName(String senderName) {

		this.senderName = senderName;
	}

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public List<String> getRecipients() {

		return recipients;
	}

	public void setRecipients(List<String> recipients) {

		this.recipients = recipients;
	}

	public MutableAttributeHandler getAttributeHandler() {

		return attributeHandler;
	}

	public void setAttributeHandler(MutableAttributeHandler attributeHandler) {

		this.attributeHandler = attributeHandler;
	}

	@Override
	public String toString() {

		String m = StringUtils.toLogFormat(message, 30);

		if (recipients.size() == 1) {

			return m + " (to: " + recipients.get(0) + ")";

		} else {
			return m + " (to: " + this.recipients.size() + " recipient(s))";
		}
	}
}
