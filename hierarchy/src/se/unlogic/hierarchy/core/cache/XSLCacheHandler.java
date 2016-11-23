/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.transform.TransformerConfigurationException;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.SimpleCachedXSLTDescriptor;
import se.unlogic.hierarchy.core.interfaces.CachedXSLTDescriptor;
import se.unlogic.standardutils.datatypes.SimpleEntry;
import se.unlogic.standardutils.i18n.Language;
import se.unlogic.standardutils.xsl.XSLTransformer;

/**
 * A handler class that handles multiple XSLTCache objects representing different designs and different languages by wrapping them in a CachedXSLTDescriptor.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 * 
 */
public class XSLCacheHandler {

	protected Logger log = Logger.getLogger(this.getClass());

	protected final ConcurrentHashMap<Language, HashSet<SimpleCachedXSLTDescriptor>> languageMap = new ConcurrentHashMap<Language, HashSet<SimpleCachedXSLTDescriptor>>();

	protected SimpleCachedXSLTDescriptor defaultXsltDescriptor;

	//A direct reference to the default design of the default language to speed things up
	protected Language defaultLanguage;

	protected int xslDescriptorCount;

	public XSLCacheHandler(Language defaultLanguage) {

		super();
		this.defaultLanguage = defaultLanguage;
	}

	/**
	 * Adds a new CachedXSLT object to the cache overwriting and previous instance matching the same design name and language.
	 * <br><br>
	 * If the new CachedXSLT is marked as default and the given language already contains a default design the isDefault flag of that design will be set to false.
	 * 
	 * @param cachedXSLT
	 * @param language
	 * @param name
	 * @param isDefault
	 * @return
	 */
	public synchronized CachedXSLTDescriptor add(XSLTransformer cachedXSLT, Language language, String name, boolean isDefault, boolean useFullMenu) {

		SimpleCachedXSLTDescriptor xsltDescriptor = new SimpleCachedXSLTDescriptor(cachedXSLT, language, name, isDefault, useFullMenu);

		HashSet<SimpleCachedXSLTDescriptor> languageDesigns = languageMap.get(language);

		if (languageDesigns == null) {

			languageDesigns = new HashSet<SimpleCachedXSLTDescriptor>();
			languageDesigns.add(xsltDescriptor);
			languageMap.put(language, languageDesigns);

			if(isDefault && language.equals(defaultLanguage)){

				this.defaultXsltDescriptor = xsltDescriptor;
			}

			xslDescriptorCount++;

		} else {

			languageDesigns = new HashSet<SimpleCachedXSLTDescriptor>(languageDesigns);

			if(isDefault){

				for(SimpleCachedXSLTDescriptor cachedXSLTDescriptor : languageDesigns){

					if(cachedXSLTDescriptor.isDefault()){
						cachedXSLTDescriptor.setDefault(false);
					}
				}

				if(language.equals(defaultLanguage)){

					this.defaultXsltDescriptor = xsltDescriptor;
				}
			}

			if (!languageDesigns.add(xsltDescriptor)) {

				languageDesigns.remove(xsltDescriptor);
				languageDesigns.add(xsltDescriptor);
			}else{
				xslDescriptorCount++;
			}

			languageMap.put(language, languageDesigns);
		}

		return xsltDescriptor;
	}

	public synchronized void remove(CachedXSLTDescriptor xsltDescriptor) {

		HashSet<SimpleCachedXSLTDescriptor> languageDesigns = languageMap.get(xsltDescriptor.getLanguage());

		if (languageDesigns != null && languageDesigns.contains(xsltDescriptor)) {

			languageDesigns = new HashSet<SimpleCachedXSLTDescriptor>(languageDesigns);

			languageDesigns.remove(xsltDescriptor);

			if(xsltDescriptor.equals(defaultXsltDescriptor)){

				this.defaultXsltDescriptor = null;
			}

			languageMap.put(xsltDescriptor.getLanguage(), languageDesigns);

			xslDescriptorCount--;
		}
	}

	/**
	 * Returns a {@link CachedXSLTDescriptor} matching the requested name and the system default language or null if no such {@link CachedXSLTDescriptor} is found.
	 * 
	 * @param name Name of the desired CachedXSLTDescriptor
	 * @return
	 */
	public CachedXSLTDescriptor getCachedXSLTDescriptor(String name) {

		return getCachedXSLTDescriptor(defaultLanguage, name);
	}

	/**
	 * Returns the default {@link CachedXSLTDescriptor} matching the requested language or null if no such {@link CachedXSLTDescriptor} is found.
	 * 
	 * @param language Language of the desired CachedXSLTDescriptor
	 * @return
	 */
	public CachedXSLTDescriptor getCachedXSLTDescriptor(Language language) {

		if(language.equals(defaultLanguage)){

			return getCachedXSLTDescriptor();
		}

		HashSet<SimpleCachedXSLTDescriptor> languageDesigns = languageMap.get(defaultLanguage);

		for (CachedXSLTDescriptor xsltDescriptor : languageDesigns) {

			if (xsltDescriptor.isDefault()) {

				return xsltDescriptor;
			}
		}

		return null;
	}

	/**
	 * Returns the default {@link CachedXSLTDescriptor} matching the system default language or null if no such {@link CachedXSLTDescriptor} is found.
	 * 
	 * @param language Language of the desired CachedXSLTDescriptor
	 * @return
	 */
	public CachedXSLTDescriptor getCachedXSLTDescriptor() {

		return defaultXsltDescriptor;
	}

	/**
	 * Returns a {@link CachedXSLTDescriptor} matching the requested name and language or null if no such CachedXSLT is found.
	 * 
	 * @param language Language of the desired CachedXSLT
	 * @param name Name of the desired CachedXSLT
	 * @return
	 */
	public CachedXSLTDescriptor getCachedXSLTDescriptor(Language language, String name) {

		HashSet<SimpleCachedXSLTDescriptor> languageDesigns = languageMap.get(language);

		for (CachedXSLTDescriptor xsltDescriptor : languageDesigns) {

			if (xsltDescriptor.getName().equals(name)) {

				return xsltDescriptor;
			}
		}

		return null;
	}
	
	public Collection<CachedXSLTDescriptor> getCachedXSLTDescriptors(Language language) {

		List<CachedXSLTDescriptor> cachedXSLTDescriptors = new ArrayList<CachedXSLTDescriptor>(languageMap.size());
		
		HashSet<SimpleCachedXSLTDescriptor> languageDesigns = languageMap.get(language);
		
		for (CachedXSLTDescriptor xsltDescriptor : languageDesigns) {

			cachedXSLTDescriptors.add(xsltDescriptor);
		}
		
		return cachedXSLTDescriptors;
		
	}

	/**
	 * This method attempts to find the best matching {@link CachedXSLTDescriptor} based on the optional language and design name parameters.
	 * 
	 * @param language
	 * @param name
	 * @return
	 */
	public CachedXSLTDescriptor getBestMatchingXSLTDescriptor(Language language, String name){

		if(defaultXsltDescriptor != null && xslDescriptorCount == 1){

			return defaultXsltDescriptor;
		}

		CachedXSLTDescriptor xsltDescriptor;

		if(language != null){

			if(name != null){

				xsltDescriptor = getCachedXSLTDescriptor(language, name);

				if(xsltDescriptor != null){

					return xsltDescriptor;
				}
			}

			xsltDescriptor = getCachedXSLTDescriptor(language);

			if(xsltDescriptor != null){

				return xsltDescriptor;
			}
		}

		if(name != null){

			xsltDescriptor = getCachedXSLTDescriptor(name);

			if(xsltDescriptor != null){

				return xsltDescriptor;
			}
		}

		return getCachedXSLTDescriptor();
	}


	public int getXslDescriptorCount() {

		return xslDescriptorCount;
	}

	public List<? extends Entry<CachedXSLTDescriptor, ? extends Exception>> reloadStylesheets(){

		ArrayList<SimpleEntry<CachedXSLTDescriptor, ? extends Exception>> errorList = new ArrayList<SimpleEntry<CachedXSLTDescriptor,? extends Exception>>();

		for(Set<SimpleCachedXSLTDescriptor> descriptorSet : languageMap.values()){

			for(SimpleCachedXSLTDescriptor descriptor : descriptorSet){

				try {
					descriptor.getCachedXSLT().reloadStyleSheet();

				} catch (TransformerConfigurationException e) {

					handleReloadingException(descriptor,e);

					errorList.add(new SimpleEntry<CachedXSLTDescriptor, TransformerConfigurationException>(descriptor, e));

				} catch (RuntimeException e){

					handleReloadingException(descriptor,e);

					errorList.add(new SimpleEntry<CachedXSLTDescriptor, RuntimeException>(descriptor, e));
				}
			}
		}

		if(!errorList.isEmpty()){

			return errorList;
		}

		return null;
	}

	private void handleReloadingException(SimpleCachedXSLTDescriptor descriptor, Exception e) {

		log.error("Error reloading stylesheet " + descriptor);
	}


	/**
	 * @return The default CachedXSLTDescriptor for the default language
	 */
	public CachedXSLTDescriptor getDefaultXsltDescriptor() {

		return defaultXsltDescriptor;
	}
}
