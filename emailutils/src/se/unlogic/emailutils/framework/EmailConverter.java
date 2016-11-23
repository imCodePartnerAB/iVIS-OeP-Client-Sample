/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

public class EmailConverter {

	public static MimeMessage convert(Email email, Session session) throws MessagingException {

		MimeMessage message = new MimeMessage(session);
		
		message.setSubject(email.getSubject());
		message.setFrom(EmailUtils.getAddresses(email.getSenderAddress(), email.getSenderName()));
		message.setReplyTo(EmailUtils.getAddresses(email.getReplyTo()));
		message.setRecipients(RecipientType.TO, EmailUtils.getAddresses(email.getRecipients()));
		message.setRecipients(RecipientType.CC, EmailUtils.getAddresses(email.getCcRecipients()));
		message.setRecipients(RecipientType.BCC, EmailUtils.getAddresses(email.getBccRecipients()));

		if(email.getSentDate() != null){
			
			message.setSentDate(email.getSentDate());
			
		}else{
			
			message.setSentDate(new Date());
		}
		
		if (email.getAttachments() != null && !email.getAttachments().isEmpty()) {

			MimeMultipart mimeMultipart = new MimeMultipart();

				if(email.getMessage() != null){
				      MimeBodyPart textPart = new MimeBodyPart();
				      textPart.setContent(email.getMessage(), email.getMessageContentType() + "; charset=" + email.getCharset());
				      mimeMultipart.addBodyPart(textPart);
				}

			for (Attachment attachment : email.getAttachments()) {
				mimeMultipart.addBodyPart(attachment.getMimeBodyPart());
			}

			message.setContent(mimeMultipart);

		} else if (email.getMessage() != null) {

			message.setContent(email.getMessage(), email.getMessageContentType() + "; charset=" + email.getCharset());
		}

		return message;
	}
}
