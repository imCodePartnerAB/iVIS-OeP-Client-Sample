/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.log4jutils.logging;

public class RelativePathNotSetException extends RuntimeException {

	private static final long serialVersionUID = -8945075466022109848L;

	public RelativePathNotSetException(String pathName) {
		super(pathName);
	}
}
