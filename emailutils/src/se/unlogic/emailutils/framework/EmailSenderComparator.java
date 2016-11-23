/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

import java.util.Comparator;

public class EmailSenderComparator implements Comparator<EmailSender> {

	public int compare(EmailSender o1, EmailSender o2) {
		if(o1.getPriority() > o2.getPriority()){
			return -1;
		}else if(o1.getPriority() == o2.getPriority()){
			return 0;
		}else{
			return 1;
		}
	}
}
