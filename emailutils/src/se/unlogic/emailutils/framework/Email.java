/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.util.Date;
import java.util.List;

public interface Email {

	public String getSenderName();

	public String getSenderAddress();

	public List<String> getRecipients();

	public List<String> getCcRecipients();

	public List<String> getBccRecipients();

	public List<String> getReplyTo();

	public String getSubject();

	public String getMessage();

	public String getMessageContentType();

	public List<? extends Attachment> getAttachments();

	public String getCharset();
	
	public Date getSentDate();
}
