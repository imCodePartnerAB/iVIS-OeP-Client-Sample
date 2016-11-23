/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.validation;


public class TCPPortStringFormatValidator extends StringIntegerValidator {

	private static final long serialVersionUID = -235693977712812656L;

	public TCPPortStringFormatValidator(){
		super(1,65536);
	}
}
