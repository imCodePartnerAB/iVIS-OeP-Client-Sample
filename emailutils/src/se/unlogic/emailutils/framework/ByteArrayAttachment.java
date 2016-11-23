/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public class ByteArrayAttachment extends BaseAttachment implements Attachment{

	private final byte[] byteArray;
	private final String contentType;
	private final String filename;

	public ByteArrayAttachment(byte[] byteArray, String contentType, String filename) {

		super();
		this.byteArray = byteArray;
		this.contentType = contentType;
		this.filename = filename;
	}

	public MimeBodyPart getMimeBodyPart() {

		MimeBodyPart bodyPart = new MimeBodyPart();
		ByteArrayDatasource datasource = new ByteArrayDatasource(byteArray, contentType, filename);

		try {
			bodyPart.setDataHandler(new DataHandler(datasource));
			bodyPart.setFileName(datasource.getName());
			this.setDisposition(bodyPart);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		return bodyPart;
	}
}
