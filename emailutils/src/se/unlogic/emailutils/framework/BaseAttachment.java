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



public abstract class BaseAttachment implements Attachment {

	public static String ATTACHMENT = "attachment";
	public static String INLINE = "inline";
	
	protected String disposition = ATTACHMENT;
		
	public String getDisposition() {
	
		return disposition;
	}

	
	public void setDisposition(String disposition) {
	
		this.disposition = disposition;
	}

	protected void setDisposition(MimeBodyPart mimeBodyPart){
		
		if(this.disposition != null){
		
			try {
				mimeBodyPart.setDisposition(disposition);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
