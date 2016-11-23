/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.emailutils.framework;

public class InvalidEmailAddressException extends Exception {

	private static final long serialVersionUID = 7616651490874818666L;
	private String address;

	public InvalidEmailAddressException(String address) {
		super(address + " is an invalid e-mail address");
	}

	public String getAddress() {
		return address;
	}
}
