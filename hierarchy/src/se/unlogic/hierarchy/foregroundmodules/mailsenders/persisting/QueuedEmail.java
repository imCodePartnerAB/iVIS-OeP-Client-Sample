/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.mailsenders.persisting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import se.unlogic.emailutils.framework.Attachment;
import se.unlogic.emailutils.framework.Email;
import se.unlogic.emailutils.framework.EmailUtils;
import se.unlogic.emailutils.framework.InvalidEmailAddressException;
import se.unlogic.standardutils.dao.annotations.DAOManaged;

public class QueuedEmail implements Email{

	@DAOManaged
	private UUID emailID;

	@DAOManaged
	private int resendCount;

	@DAOManaged
	private String senderName;

	@DAOManaged
	private String senderAddress;

	@DAOManaged
	private String charset;

	@DAOManaged
	private String messageContentType;

	@DAOManaged
	private String subject;

	@DAOManaged
	private String message;

	private ArrayList<Attachment> attachments;
	private ArrayList<String> bccRecipients;
	private ArrayList<String> ccRecipients;
	private ArrayList<String> recipients;
	private ArrayList<String> replyTo;

	@Override
	public List<Attachment> getAttachments() {
		return this.attachments;
	}

	@Override
	public String getCharset() {
		return this.charset;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public ArrayList<String> getReplyTo() {
		return replyTo;
	}

	@Override
	public String getMessageContentType() {
		return this.messageContentType;
	}

	@Override
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

		return recipients.addAll(addresses);
	}

	@Override
	public String getSenderName() {
		return this.senderName;
	}

	@Override
	public String getSenderAddress() {
		return this.senderAddress;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	public boolean add(Attachment o) {
		return attachments.add(o);
	}

	public boolean addAll(Collection<? extends Attachment> c) {
		return attachments.addAll(c);
	}

	@Override
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

		return bccRecipients.addAll(addresses);
	}

	@Override
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

		return ccRecipients.addAll(addresses);
	}

	public void setSenderAddress(String senderAdress) throws InvalidEmailAddressException {

		if (!EmailUtils.isValidEmailAddress(senderAdress)) {
			throw new InvalidEmailAddressException(senderAdress);
		}

		this.senderAddress = senderAdress;
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

	public UUID getEmailID() {
		return emailID;
	}
	public void setEmailID(UUID emailID) {
		this.emailID = emailID;
	}
	public int getResendCount() {
		return resendCount;
	}
	public void setResendCount(int retryCount) {
		this.resendCount = retryCount;
	}

	@Override
	public String toString() {

		if(recipients != null && recipients.size() == 1 && bccRecipients == null && ccRecipients == null){

			return this.subject + " (to: " + recipients.get(0) + ")";

		}else{

			int recipientCount = 0;

			if(this.recipients != null){
				recipientCount += recipients.size();
			}

			if(this.ccRecipients != null){
				recipientCount += ccRecipients.size();
			}

			if(this.bccRecipients != null){
				recipientCount += bccRecipients.size();
			}

			return this.subject + " (to: " + recipientCount + " recipient(s))";
		}
	}


	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}


	public void setBccRecipients(ArrayList<String> bccRecipients) {
		this.bccRecipients = bccRecipients;
	}


	public void setCcRecipients(ArrayList<String> ccRecipients) {
		this.ccRecipients = ccRecipients;
	}


	public void setRecipients(ArrayList<String> recipients) {
		this.recipients = recipients;
	}


	public void setReplyTo(ArrayList<String> replyTo) {
		this.replyTo = replyTo;
	}

	@Override
	public Date getSentDate() {

		return null;
	}
}
