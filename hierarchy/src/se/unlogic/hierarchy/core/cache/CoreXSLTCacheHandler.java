/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.cache;

import java.io.File;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;

import se.unlogic.hierarchy.core.exceptions.NoXSLStylesheetsFoundException;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.i18n.Language;
import se.unlogic.standardutils.settings.SettingNode;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.ClassPathURIResolver;
import se.unlogic.standardutils.xsl.FileXSLTransformer;


public class CoreXSLTCacheHandler extends XSLCacheHandler {

	public CoreXSLTCacheHandler(SettingNode config, Language defaultLanguage, String applicationFileSystemPath){

		super(defaultLanguage);

		List<? extends SettingNode> stylesheets = config.getNodes("/Config/StyleSheets/StyleSheet");

		for(SettingNode settingNode : stylesheets){

			String path = settingNode.getString(".");
			String name = settingNode.getString("@name");
			Language language = EnumUtils.toEnum(Language.class, settingNode.getString("@language"));
			boolean isDefault = settingNode.getBoolean("@default");
			boolean fullPath = settingNode.getBoolean("@pathtype = 'Full'");
			boolean useFullMenu = settingNode.getBoolean("@fullMenu");

			if(StringUtils.isEmpty(path)){

				log.warn("Stylesheet " + name + " with no path set found in config, ignoring stylesheet.");

			}else if(StringUtils.isEmpty(name)){

				log.warn("Stylesheet with no name set found in config (path " + path + "), ignoring stylesheet.");

				continue;

			}else if(language == null){

				log.warn("Stylesheet " + name + " with no or invalid language set found in config (path " + path + "), ignoring stylesheet.");

				continue;
			}

			try {
				FileXSLTransformer cachedXSLTFile;

				if(fullPath){

					cachedXSLTFile = new FileXSLTransformer(new File(path), ClassPathURIResolver.getInstance());

				}else{

					cachedXSLTFile = new FileXSLTransformer(new File(applicationFileSystemPath + "WEB-INF/" + path), ClassPathURIResolver.getInstance());
				}

				this.add(cachedXSLTFile, language, name, isDefault, useFullMenu);

			} catch (TransformerConfigurationException e) {

				log.error("Error caching stylesheet " + name + " (path: " + path + ")",e);
			}
		}

		if(this.getXslDescriptorCount() == 0){

			throw new NoXSLStylesheetsFoundException();

		}else if(this.defaultXsltDescriptor == null){

			throw new NoDefaultCoreStylesheetFoundException("No default stylesheet found for default language " + defaultLanguage);
		}
	}
}
