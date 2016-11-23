/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;


public abstract class BaseFileAccessValidator implements FileAccessValidator {

	private static final Pattern PERCENT_PATTERN = Pattern.compile("%(?![0-9a-fA-F]{2})");

	public static final String[] TAG_ATTRIBUTES = { "href", "src", "link", "value" };

	protected final String fileBasePath;

	public BaseFileAccessValidator(String fileBasePath) {

		this.fileBasePath = fileBasePath;
	}

	@Override
	public abstract boolean checkAccess(String filePath);

	public boolean checkFilePath(String text, String filePath) {

		String absoluteFilePath = fileBasePath + filePath;

		try {
			//Escape all % characters that don't represent a valid escaped character

			text = PERCENT_PATTERN.matcher(text).replaceAll("%25");

			// Handle '&'-character
			text = text.replace("&amp;", "&");

			//TODO ugly fix, the encoding/decoding problems here need a proper solution. Maybe a decoded copy of each page should be kept in cache?
			text = URLDecoder.decode(text, "UTF-8");

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException(e);
		}

		for (String attribute : TAG_ATTRIBUTES) {

			if (text.contains(attribute + "='" + absoluteFilePath + "'")) {

				return true;

			} else if (text.contains(attribute + "=\"" + absoluteFilePath + "\"")) {

				return true;
			}
		}

		return false;
	}
}
