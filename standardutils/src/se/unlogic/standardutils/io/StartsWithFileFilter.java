/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.io;

import java.io.File;
import java.io.FileFilter;


public class StartsWithFileFilter implements FileFilter {

	private final boolean directories;
	private final String prefix;

	public StartsWithFileFilter(String prefix, boolean directories) {

		super();
		this.prefix = prefix;
		this.directories = directories;
	}

	public boolean accept(File file) {

		if(file.isDirectory() == directories && file.getName().startsWith(prefix)){

			return true;
		}

		return false;
	}
}
