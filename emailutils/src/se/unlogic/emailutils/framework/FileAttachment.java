/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import se.unlogic.standardutils.mime.MimeUtils;

public class FileAttachment extends BaseAttachment implements Attachment{

	private final File file;
	private final String filename;
	private String contentType;
	
	public FileAttachment(File file) {
		super();
		this.file = file;
		this.filename = file.getName();
	}

	public FileAttachment(File file, String filename) {
		super();
		this.file = file;
		this.filename = filename;
	}
	
	public File getFile() {
		return file;
	}

	public MimeBodyPart getMimeBodyPart() {

		MimeBodyPart filePart = new MimeBodyPart();
	    FileDataSource fds = new FileDataSource(file);

	    try {
		    filePart.setDataHandler(new DataHandler(fds));
		   
		    if(contentType == null) {
		    	contentType = MimeUtils.getMimeType(filename);
		    }
		    
		    filePart.setHeader("Content-Type", contentType);
		    
		    if(!contentType.startsWith("text/")) {
		    	filePart.setHeader("Content-Transfer-Encoding", "base64");
		    }
		    
	    	filePart.setFileName(filename);
		    
			this.setDisposition(filePart);
			
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

		return filePart;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
