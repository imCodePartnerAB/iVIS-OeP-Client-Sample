/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.interfaces;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;

import se.unlogic.standardutils.i18n.Language;
import se.unlogic.standardutils.xsl.XSLTransformer;


public interface CachedXSLTDescriptor {

	/**
	 * @return The underlying {@link XSLTransformer} object wrapped by this descriptor
	 * 
	 */
	public XSLTransformer getCachedXSLT();

	/**
	 * @return A new {@link Transformer} instance from the underlying {@link XSLTransformer} object wrapped by this descriptor.
	 * @throws TransformerConfigurationException
	 */
	public Transformer getTransformer() throws TransformerConfigurationException;

	public Language getLanguage();

	public String getName();

	public boolean isDefault();
	
	public boolean usesFullMenu();
}
