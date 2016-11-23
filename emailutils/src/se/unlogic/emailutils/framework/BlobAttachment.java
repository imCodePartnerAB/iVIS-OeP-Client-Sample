/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.sql.Blob;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public class BlobAttachment extends BaseAttachment implements Attachment{

	private final MimeBodyPart mimeBodyPart;

	public BlobAttachment(Blob blob) throws MessagingException, SQLException {
		super();
		this.mimeBodyPart = new MimeBodyPart(blob.getBinaryStream());
	}

	public MimeBodyPart getMimeBodyPart() {

		this.setDisposition(mimeBodyPart);
		
		return mimeBodyPart;
	}
}
