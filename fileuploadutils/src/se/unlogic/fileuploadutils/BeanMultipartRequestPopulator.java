/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.fileuploadutils;

import se.unlogic.standardutils.validation.ValidationException;

public interface BeanMultipartRequestPopulator<ReturnType>{
	public ReturnType populate(MultipartRequest req) throws ValidationException;
	public ReturnType populate(ReturnType bean, MultipartRequest req) throws ValidationException;
}
