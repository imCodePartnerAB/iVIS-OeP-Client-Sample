/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public class URLAttachment extends BaseAttachment implements Attachment {

	private final URL url;

	public URLAttachment(URL url) {
		super();
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

	public MimeBodyPart getMimeBodyPart() {

		MimeBodyPart urlPart = new MimeBodyPart();
	    URLDataSource urlDataSource = new URLDataSource(url);

	    try {
		    urlPart.setDataHandler(new DataHandler(urlDataSource));
			urlPart.setFileName(urlDataSource.getName());
			
			this.setDisposition(urlPart);
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		return urlPart;
	}
}
