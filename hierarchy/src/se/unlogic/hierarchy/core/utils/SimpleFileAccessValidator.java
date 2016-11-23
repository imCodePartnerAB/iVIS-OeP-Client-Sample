/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;


public class SimpleFileAccessValidator extends BaseFileAccessValidator {

	private String text;

	public SimpleFileAccessValidator(String fileBaseUrl, String text) {
		super(fileBaseUrl);
		this.text = text;
	}

	@Override
	public boolean checkAccess(String filePath) {

		return checkFilePath(text, filePath);
	}

}
