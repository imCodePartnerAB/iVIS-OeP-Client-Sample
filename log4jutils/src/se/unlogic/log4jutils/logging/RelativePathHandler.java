/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.log4jutils.logging;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RelativePathHandler {
	
    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock r = rwl.readLock();
    private static final Lock w = rwl.writeLock();	
	
	private static final HashMap<String,String> pathMap = new HashMap<String,String>();
	
	public static void setPath(String name, String path){
		w.lock();
		try{
			pathMap.put(name, path);
		}finally{
			w.unlock();
		}
	}
	
	public static String getPath(String name){
		r.lock();
		try{
			return pathMap.get(name);
		}finally{
			r.unlock();
		}		
	}
}
