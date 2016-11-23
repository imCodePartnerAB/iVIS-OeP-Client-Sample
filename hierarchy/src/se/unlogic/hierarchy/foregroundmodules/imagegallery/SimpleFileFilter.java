/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.imagegallery;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.fileupload.FileItem;

public class SimpleFileFilter implements FileFilter {
	@Override
	public boolean accept(File file) {

		return isValidFilename(file.getName());

	}

	public boolean accept(FileItem fileItem) {

		return isValidFilename(fileItem.getName());
	}

	public static boolean isValidFilename(String filename) {

		filename = filename.toLowerCase();

		if (filename.endsWith("jpg") || filename.endsWith("jpeg") || filename.endsWith("gif") || filename.endsWith("png") || filename.endsWith("bmp")) {
			return true;
		} else {
			return false;
		}
	}
};
