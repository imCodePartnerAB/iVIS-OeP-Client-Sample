/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.staticcontent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.BundleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MenuItemDescriptor;
import se.unlogic.hierarchy.core.interfaces.Module;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.VisibleModuleDescriptor;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.standardutils.date.PooledSimpleDateFormat;
import se.unlogic.standardutils.io.FileUtils;
import se.unlogic.standardutils.mime.MimeUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

//This module serves static content from classpath

public class StaticContentModule implements ForegroundModule {

	private static final PooledSimpleDateFormat RFC1123_DATE_FORMATTER = new PooledSimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US, TimeZone.getTimeZone("GMT"));

	protected Logger log = Logger.getLogger(this.getClass());

	private SystemInterface systemInterface;
	private SectionInterface sectionInterface;
	private ForegroundModuleDescriptor moduleDescriptor;

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Enable global links", description = "Controls whether or not global links are enabled")
	protected boolean enableGlobalContentLinks = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Global content links file", description = "The path to the file containing the definitions of global links (the file must be in classpath)")
	protected String globalContentLinksFile = "defaultGlobalContentLinks.properties";

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Register in instance handler", description = "Controls if this module should register itself in the global instance handler.")
	boolean registerInInstanceHandler = true;

	protected Properties globalContentLinks;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		this.sectionInterface = sectionInterface;
		this.systemInterface = sectionInterface.getSystemInterface();
		this.moduleDescriptor = moduleDescriptor;

		ModuleUtils.setModuleSettings(this, StaticContentModule.class, moduleDescriptor.getMutableSettingHandler(), sectionInterface.getSystemInterface());

		loadGlobalContentLinks();

		checkInstanceHandlerRegistration(registerInInstanceHandler);
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		this.moduleDescriptor = moduleDescriptor;

		ModuleUtils.setModuleSettings(this, StaticContentModule.class, moduleDescriptor.getMutableSettingHandler(), sectionInterface.getSystemInterface());

		loadGlobalContentLinks();

		checkInstanceHandlerRegistration(registerInInstanceHandler);
	}

	@Override
	public void unload() throws Exception {

		checkInstanceHandlerRegistration(false);
	}

	protected void checkInstanceHandlerRegistration(boolean register){

		if(register){

			if (!systemInterface.getInstanceHandler().addInstance(StaticContentModule.class, this)) {

				log.warn("Another instance has already been registered in instance handler for class " + StaticContentModule.class.getName());
			}

		}else{

			if (systemInterface.getInstanceHandler().getInstance(StaticContentModule.class) == this) {

				systemInterface.getInstanceHandler().removeInstance(StaticContentModule.class);
			}
		}
	}

	private void loadGlobalContentLinks() {

		if(enableGlobalContentLinks && globalContentLinksFile != null){

			InputStream fileStream = this.getClass().getResourceAsStream(globalContentLinksFile);

			if(fileStream != null){

				Properties links = new Properties();

				try{
					links.load(fileStream);

					if(links.size() > 0){

						log.debug("Succesfully parsed " + links.size() + " global content links");
						globalContentLinks = links;
						return;
					}

				}catch(IOException e){
					log.error("Error parsing global content links from definition file: " + globalContentLinksFile, e);
				}

				StreamUtils.closeStream(fileStream);
			}
		}

		globalContentLinks = null;
	}

	@Override
	public List<SettingDescriptor> getSettings() {

		try{
			return ModuleUtils.getAnnotatedSettingDescriptors(this, Object.class, sectionInterface.getSystemInterface());
		}catch(IllegalArgumentException e){

			throw new RuntimeException(e);

		}catch(IllegalAccessException e){

			throw new RuntimeException(e);

		}catch(InstantiationException e){

			throw new RuntimeException(e);

		}catch(SQLException e){

			throw new RuntimeException(e);
		}
	}

	@Override
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws AccessDeniedException, URINotFoundException {

		//TODO add support to separate numeric foreground module aliases from moduleID's (fv prefix?)
		//TODO add support to separate background module hashcodes from moduleID's (bv prefix?)

		Integer sectionID;

		if(uriParser.size() >= 3 && uriParser.get(1).equals("global")){

			if(!enableGlobalContentLinks){

				throw new AccessDeniedException("Global content links are disabled");
			}

			Properties globalContentLinks = this.globalContentLinks;

			if(globalContentLinks == null){

				throw new URINotFoundException(uriParser);
			}

			if(!globalContentLinks.isEmpty()){

				String filePath = getFilePath(uriParser, 1);

				for(Entry<Object, Object> linkEntry : globalContentLinks.entrySet()){

					if(filePath.startsWith(linkEntry.getKey().toString())){

						URL linkedURL = this.getClass().getResource(linkEntry.getValue() + filePath);

						if(linkedURL != null){

							try{
								InputStream fileStream = linkedURL.openStream();

								if(fileStream != null){

									this.sendFile(req, res, uriParser, linkedURL, fileStream, user, linkEntry.getValue().toString(), filePath, null, null);

									return null;
								}
							}catch(IOException e){
								log.error("Unable to load file from url " + linkedURL + " belonging to global content links", e);
							}
						}
					}
				}
			}

		}else if(uriParser.size() >= 5 && (uriParser.get(1).equals("f") || uriParser.get(1).equals("b")) && !uriParser.getFormattedURI().contains("..") && (sectionID = NumberUtils.toInt(uriParser.get(2))) != null){

			//Get the requested section
			SectionInterface sectionInterface = systemInterface.getSectionInterface(sectionID);

			if(sectionInterface == null){

				//The requested section is not started or does not exist
				throw new AccessDeniedException("The requested section ID was not found in cache (URI: " + uriParser.getFormattedURI() + ")");

			}else if(!AccessUtils.checkAccess(user, sectionInterface.getSectionDescriptor())){

				//The user does not have access to the requested section
				throw new AccessDeniedException("User does not have access to section " + sectionInterface.getSectionDescriptor() + " (URI: " + uriParser.getFormattedURI() + ")");
			}

			//Check that the user has access to all parent section
			SectionInterface parentSection = sectionInterface.getParentSectionInterface();

			while(parentSection != null){

				if(!AccessUtils.checkAccess(user, parentSection.getSectionDescriptor())){

					//User does not have access to a parent section
					throw new AccessDeniedException("User does not have access to section " + sectionInterface.getSectionDescriptor() + " (URI: " + uriParser.getFormattedURI() + ")");
				}

				parentSection = parentSection.getParentSectionInterface();
			}

			boolean foreground = uriParser.get(1).equals("f");

			String moduletype;

			if(foreground){

				moduletype = "foreground module";

			}else{

				moduletype = "background module";
			}

			Integer moduleID = NumberUtils.toInt(uriParser.get(3));

			// Get the requested module
			Entry<? extends VisibleModuleDescriptor, ? extends Module<?>> moduleEntry = null;

			if(moduleID != null){

				if(foreground){

					moduleEntry = sectionInterface.getForegroundModuleCache().getEntry(moduleID);

				}else{

					moduleEntry = sectionInterface.getBackgroundModuleCache().getEntry(moduleID);
				}
			}

			if(moduleEntry == null){

				if(foreground){

					String alias = uriParser.get(3);

					moduleEntry = sectionInterface.getForegroundModuleCache().getEntry(alias);

				}else if(moduleID != null){

					moduleEntry = sectionInterface.getBackgroundModuleCache().getEntryByHashCode(moduleID);
				}

			}

			if(moduleEntry != null){

				VisibleModuleDescriptor moduleDescriptor = moduleEntry.getKey();

				// Check if the user has access to this module
				if(AccessUtils.checkAccess(user, moduleDescriptor)){

					// Check that the module has a static content directory set
					if(!StringUtils.isEmpty(moduleDescriptor.getStaticContentPackage())){

						// Check that the requested file exists in the specified classpath directory

						String filePath = getFilePath(uriParser, 3);

						URL url = moduleEntry.getValue().getClass().getResource(moduleDescriptor.getStaticContentPackage() + filePath);

						InputStream fileStream = null;

						if(url != null){

							try{
								fileStream = url.openStream();
							}catch(IOException e){
							}
						}

						if(fileStream != null){

							log.debug("Sending file " + moduleDescriptor.getStaticContentPackage() + filePath + " from " + moduletype + " " + moduleDescriptor + " reqested using URI " + uriParser.getFormattedURI() + " to user " + user);

							this.sendFile(req, res, uriParser, url, fileStream, user, moduleDescriptor.getStaticContentPackage(), filePath, moduleDescriptor, moduletype);

							return null;

						}else if((fileStream = moduleEntry.getValue().getClass().getResourceAsStream(moduleDescriptor.getStaticContentPackage() + "/StaticContentLinks.properties")) != null){

							Properties links = new Properties();

							try{
								links.load(fileStream);

							}catch(IOException e){
								log.error("Unable to load static content links belonging to " + moduletype + " " + moduleDescriptor, e);
							}finally{
								StreamUtils.closeStream(fileStream);
							}

							if(!links.isEmpty()){

								for(Entry<Object, Object> linkEntry : links.entrySet()){

									if(filePath.startsWith(linkEntry.getKey().toString())){

										URL linkedURL = moduleEntry.getValue().getClass().getResource(linkEntry.getValue() + filePath);

										if(linkedURL != null){

											try{
												fileStream = linkedURL.openStream();

												if(fileStream != null){

													this.sendFile(req, res, uriParser, linkedURL, fileStream, user, linkEntry.getValue().toString(), filePath, moduleDescriptor, moduletype);

													return null;
												}

											}catch(IOException e){
												log.error("Unable to load file from url " + linkedURL + " belonging to " + moduletype + " " + moduleDescriptor, e);
											}
										}
									}
								}
							}

						}else{
							log.info("File " + uriParser.getFormattedURI() + " requested from " + moduletype + " " + moduleDescriptor + " by user " + user + " not found");
						}

					}else{
						log.info("User " + user + " requested static content from " + moduletype + " " + moduleDescriptor + " which has no static content package set, using URI " + uriParser.getFormattedURI());
					}
				}else{
					throw new AccessDeniedException("User does not have access to " + moduletype + " " + moduleEntry.getKey() + " (URI: " + uriParser.getFormattedURI() + ")");
				}
			}else{
				log.info("Invalid sectionID or moduleID in URI " + uriParser.getFormattedURI() + " requested by user " + user);
			}
		}else{
			log.info("Invalid URI " + uriParser.getFormattedURI() + " requested by user " + user);
		}

		throw new URINotFoundException(uriParser);
	}

	private String getFilePath(URIParser uriParser, int startIndex) {

		// Parse & build requested file path
		if(uriParser.size() > (startIndex + 1)){

			StringBuilder stringBuilder = new StringBuilder();

			for(int i = startIndex + 1; i < uriParser.size(); i++){
				stringBuilder.append("/" + uriParser.get(i));
			}

			return stringBuilder.toString();

		}else{

			return "/" + uriParser.get(startIndex);
		}
	}

	private void sendFile(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, URL url, InputStream fileStream, User user, String staticContentPackage, String filePath, ModuleDescriptor moduleDescriptor, String moduletype) {

		OutputStream outstream = null;

		try{
			Date lastModified = this.getLastModified(url, uriParser);

			if(lastModified != null){

				String lastModifiedString = RFC1123_DATE_FORMATTER.format(lastModified);

				String modifiedSinceString = req.getHeader("If-Modified-Since");

				if(modifiedSinceString != null && lastModifiedString.equalsIgnoreCase(modifiedSinceString)){

					res.setStatus(304);
					res.flushBuffer();

					return;
				}

				res.setHeader("Last-Modified", lastModifiedString);
			}

			// Set content type, filename
			String filename = FileUtils.toValidHttpFilename(uriParser.get(uriParser.size() - 1));

			res.setStatus(200);
			res.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

			String contentType = MimeUtils.getMimeType(filename);

			if(contentType != null){
				res.setContentType(contentType);
			}else{
				res.setContentType("application/x-unknown-mime-type");
			}

			outstream = res.getOutputStream();

			StreamUtils.transfer(fileStream, outstream);

			if(log.isDebugEnabled()){

				if(moduleDescriptor != null){

					log.debug("Sent file " + staticContentPackage + filePath + " from " + moduletype + " " + moduleDescriptor + " to user " + user);

				}else{

					log.debug("Sent file " + staticContentPackage + filePath + " from global content links to user " + user);
				}
			}

		}catch(IOException e){

			if(log.isDebugEnabled()){

				if(moduleDescriptor != null){

					log.info("Error sending file " + staticContentPackage + filePath + " from " + moduletype + " " + moduleDescriptor + " to user " + user + ", " + e);

				}else{

					log.info("Error sending file " + staticContentPackage + filePath + " from global content links to user " + user + ", " + e);
				}
			}

		}finally{
			try {
				StreamUtils.closeStream(fileStream);
			} catch (NullPointerException e) {
				//Workaround for JVM bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=5041014 (Windows only)
			}
			StreamUtils.closeStream(outstream);
		}
	}

	protected Date getLastModified(URL url, URIParser uriParser) {

		try{

			return new Date(url.openConnection().getLastModified());

		}catch(IOException e){

			log.warn("Unable to get last modified date for url " + url + "," + e);

		}catch(RuntimeException e){

			log.warn("Unable to get last modified date for url " + url + "," + e);
		}

		return null;
	}

	@Override
	public List<BundleDescriptor> getVisibleBundles() {

		return null;
	}

	@Override
	public List<MenuItemDescriptor> getAllMenuItems() {

		return null;
	}

	@Override
	public List<MenuItemDescriptor> getVisibleMenuItems() {

		return null;
	}

	@Override
	public List<BundleDescriptor> getAllBundles() {

		return null;
	}

	public String getModuleContentURL(ForegroundModuleDescriptor moduleDescriptor, HttpServletRequest req) {

		String moduleIdentifier;

		if(moduleDescriptor.getModuleID() != null){

			moduleIdentifier = moduleDescriptor.getModuleID().toString();

		}else{

			moduleIdentifier = moduleDescriptor.getAlias();
		}

		return req.getContextPath() + this.sectionInterface.getSectionDescriptor().getFullAlias() + "/" + this.moduleDescriptor.getAlias() + "/f/" + moduleDescriptor.getSectionID() + "/" + moduleIdentifier;
	}
}
