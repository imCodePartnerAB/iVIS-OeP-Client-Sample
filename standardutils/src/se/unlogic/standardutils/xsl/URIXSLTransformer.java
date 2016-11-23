/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xsl;

import java.net.URI;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class URIXSLTransformer extends BaseXSLTransformer {

	private final URI uri;
	private final URIResolver uriResolver;
	private final boolean useCache;

	public URIXSLTransformer(URI uri, boolean useCache) throws TransformerConfigurationException {
		super();
		this.uri = uri;
		this.uriResolver = null;
		this.useCache = useCache;
		this.reloadStyleSheet();
	}

	public URIXSLTransformer(URI uri, URIResolver uriResolver, boolean useCache) throws TransformerConfigurationException {

		this.uri = uri;
		this.uriResolver = uriResolver;
		this.useCache = useCache;
		this.reloadStyleSheet();
	}

	public void reloadStyleSheet() throws TransformerConfigurationException {

		if(useCache){
			
			this.templates = TemplateCache.getTemplates(new TemplateDescriptor(uri, uriResolver));
			
		}else{
		
			TransformerFactory transFact = TransformerFactory.newInstance();

			if(uriResolver != null){
				transFact.setURIResolver(uriResolver);
			}

			this.templates = transFact.newTemplates(new StreamSource(uri.toString()));			
		}
	}

	@Override
	public String toString() {

		return "CachedXSLTURI: " + uri;
	}
}
