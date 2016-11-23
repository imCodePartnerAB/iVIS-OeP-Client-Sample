/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.fileuploadutils;

import java.util.Enumeration;
import java.util.Iterator;


public class IteratorEnumeration<T> implements Enumeration<T> {

	private Iterator<T> iterator;

	public IteratorEnumeration(Iterator<T> iterator) {
		super();
		this.iterator = iterator;
	}

	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	public T nextElement() {
		return iterator.next();
	}
}
