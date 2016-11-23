/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public class StringAttachment extends BaseAttachment implements Attachment{

	private final String filename;
	private final String message;
	private final String contentType;

	public StringAttachment(String message, String contentType){
		this.filename = null;
		this.message = message;
		this.contentType = contentType;
	}

	public StringAttachment(String filename, String message, String contentType){
		this.filename = filename;
		this.message = message;
		this.contentType = contentType;
	}

	public String getMessage() {
		return message;
	}

	public String getContentType() {
		return contentType;
	}

	public MimeBodyPart getMimeBodyPart() {

		MimeBodyPart textPart = new MimeBodyPart();
		try {
			textPart.setContent(message, contentType);

			if(filename != null){
				textPart.setFileName(filename);
			}

			this.setDisposition(textPart);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		return textPart;
	}
}
