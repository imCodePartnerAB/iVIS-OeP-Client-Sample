/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SimpleEmail implements Email {

	@Override
	public String toString() {

		if(recipients.size() == 1 && bccRecipients.size() == 0 && ccRecipients.size() == 0){

			return this.subject + " (to: " + recipients.get(0) + ")";

		}else{
			return this.subject + " (to: " + (this.bccRecipients.size() + this.ccRecipients.size() + this.recipients.size()) + " recipient(s))";
		}
	}

	public static final String TEXT = "text/plain";
	public static final String HTML = "text/html";
	public static final String DEFAULT_CHARSET = "Cp1252";

	private final ArrayList<Attachment> attachments = new ArrayList<Attachment>();
	private final ArrayList<String> bccRecipients = new ArrayList<String>();
	private final ArrayList<String> ccRecipients = new ArrayList<String>();
	private final ArrayList<String> recipients = new ArrayList<String>();
	private final ArrayList<String> replyTo = new ArrayList<String>();

	private String senderName;
	private String senderAddress;
	private String charset = DEFAULT_CHARSET;
	private String messageContentType = TEXT;
	private String subject;
	private String message;
	
	private Date sentDate;

	public List<Attachment> getAttachments() {
		return this.attachments;
	}

	public String getCharset() {
		return this.charset;
	}

	public String getMessage() {
		return this.message;
	}

	public ArrayList<String> getReplyTo() {
		return replyTo;
	}

	public String getMessageContentType() {
		return this.messageContentType;
	}

	public List<String> getRecipients() {
		return this.recipients;
	}

	public void addRecipient(String address) throws InvalidEmailAddressException {

		if (!EmailUtils.isValidEmailAddress(address)) {
			throw new InvalidEmailAddressException(address);
		}

		recipients.add(address);
	}

	public boolean addRecipients(Collection<String> addresses) throws InvalidEmailAddressException {

		this.validateAddresses(addresses);

		return recipients.addAll(addresses);
	}

	public String getSenderName() {
		return this.senderName;
	}

	public String getSenderAddress() {
		return this.senderAddress;
	}

	public String getSubject() {
		return subject;
	}

	public boolean add(Attachment o) {
		return attachments.add(o);
	}

	public boolean addAll(Collection<? extends Attachment> c) {
		return attachments.addAll(c);
	}

	public List<String> getBccRecipients() {
		return this.bccRecipients;
	}

	public void addBccRecipient(String address) throws InvalidEmailAddressException {

		if (!EmailUtils.isValidEmailAddress(address)) {
			throw new InvalidEmailAddressException(address);
		}

		bccRecipients.add(address);
	}

	public boolean addBccRecipients(Collection<String> addresses) throws InvalidEmailAddressException {

		this.validateAddresses(addresses);

		return bccRecipients.addAll(addresses);
	}

	public List<String> getCcRecipients() {
		return this.ccRecipients;
	}

	public void addCcRecipient(String address) throws InvalidEmailAddressException {

		if (!EmailUtils.isValidEmailAddress(address)) {
			throw new InvalidEmailAddressException(address);
		}

		ccRecipients.add(address);
	}

	public boolean addCcRecipients(Collection<String> addresses) throws InvalidEmailAddressException {

		this.validateAddresses(addresses);

		return ccRecipients.addAll(addresses);
	}

	public void setSenderAddress(String senderAddress) throws InvalidEmailAddressException {

		if (!EmailUtils.isValidEmailAddress(senderAddress)) {
			throw new InvalidEmailAddressException(senderAddress);
		}

		this.senderAddress = senderAddress;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setMessageContentType(String messageContentType) {
		this.messageContentType = messageContentType;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private void validateAddresses(Collection<String> addresses) throws InvalidEmailAddressException {

		if (addresses == null) {
			throw new InvalidEmailAddressException(null);
		} else {
			for (String address : addresses) {
				if (!EmailUtils.isValidEmailAddress(address)) {
					throw new InvalidEmailAddressException(address);
				}
			}
		}
	}

	public Date getSentDate() {

		return sentDate;
	}

	public void setSentDate(Date sentDate) {

		this.sentDate = sentDate;
	}
}
