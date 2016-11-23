/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.log4jutils.logging;

import org.apache.log4j.FileAppender;

public class RelativeFileAppender extends FileAppender {

	private String pathName;
	
	@Override
	public void setFile(String file) {
		super.setFile(this.getPath() + file);
	}
	 
	public void setPathName(String pathName) {
		
		if(pathName == null){
			throw new NullPointerException("Pathname cannot be null!");
		}else{
			this.pathName = pathName;
		}
	}
	
	public String getPathName() {
		return pathName;
	}
	
	private String getPath(){
		
		if(this.pathName == null){
			throw new RelativePathNameNotSetException();
		}else{
			String path = RelativePathHandler.getPath(pathName);
			
			if(path == null){
				throw new RelativePathNotSetException(pathName);
			}else{
				return path;
			}			
		}
	}
}


