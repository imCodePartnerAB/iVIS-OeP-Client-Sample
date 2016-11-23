/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.fileviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.ModuleConfigurationException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.URIParser;

public class FileViewModule extends AnnotatedForegroundModule {

	private static final ArrayList<SettingDescriptor> SETTINGDESCRIPTORS = new ArrayList<SettingDescriptor>(4);

	static {
		SETTINGDESCRIPTORS.add(SettingDescriptor.createCheckboxSetting("displayFilpathAndLineCount", "Display filepath and linecount", "Displays the path to the file and the number of lines in the file", false));
		SETTINGDESCRIPTORS.add(SettingDescriptor.createTextFieldSetting("filePath", "File path", "Path to the file to be displayed", true, null, null));
	}

	@ModuleSetting
	protected boolean displayFilpathAndLineCount = false;

	@ModuleSetting
	protected String filePath;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {
		super.init(moduleDescriptor, sectionInterface, dataSource);
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {
		super.update(moduleDescriptor, dataSource);
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws ModuleConfigurationException {

		if (filePath == null || StringUtils.isEmpty(filePath)) {
			// File is empty, module needs to be configured
			throw new ModuleConfigurationException("No file path set");
		}

		File file = new File(filePath);

		if (!file.exists()) {
			// File does not exists
			log.warn("The file " + filePath + " does not exist (" + this.moduleDescriptor + ")");

			return createSimpleResponse("FileNotFound", req, uriParser);

		} else if (file.isDirectory()) {
			// File is a direcoty...
			log.warn("The file " + filePath + " is a directory (" + this.moduleDescriptor + ")");

			return createSimpleResponse("FileIsDirectory", req, uriParser);

		} else if (!file.canRead()) {
			// File is not readable
			log.warn("The file " + filePath + " is not readable (" + this.moduleDescriptor + ")");

			return createSimpleResponse("UnableToAccessFile", req, uriParser);

		} else {
			log.info("Displaying file " + filePath + " for user " + user);

			FileReader fileReader = null;
			BufferedReader bufferedReader = null;

			try {
				fileReader = new FileReader(file);
				bufferedReader = new BufferedReader(fileReader);

				Document doc = this.createDocument(req, uriParser);

				Element fileElement = doc.createElement("File");
				doc.getFirstChild().appendChild(fileElement);

				if (displayFilpathAndLineCount) {
					fileElement.appendChild(XMLUtils.createCDATAElement("FilePath", filePath, doc));
				}

				Element linesElement = doc.createElement("Lines");
				fileElement.appendChild(linesElement);

				String line = null;

				while ((line = bufferedReader.readLine()) != null) {
					linesElement.appendChild(XMLUtils.createCDATAElement("Line", line, doc));
				}

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());

			} catch (FileNotFoundException e) {
				// The file doesn't exist
				log.warn("The file " + filePath + " does not exist (" + this.moduleDescriptor + ")");
				return createSimpleResponse("FileNotFound", req, uriParser);

			} catch (IOException e) {
				// Unknown error
				log.error("Error reading file " + filePath + " (" + this.moduleDescriptor + ")");

				return createSimpleResponse("UnableToAccessFile", req, uriParser);
			} finally {

				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
					}
				}

				if (fileReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	private SimpleForegroundModuleResponse createSimpleResponse(String elementName, HttpServletRequest req, URIParser uriParser) {

		Document doc = this.createDocument(req, uriParser);

		doc.getFirstChild().appendChild(doc.createElement(elementName));

		return new SimpleForegroundModuleResponse(doc, this.getDefaultBreadcrumb());
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {
		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}

	@Override
	public List<SettingDescriptor> getSettings() {
		return SETTINGDESCRIPTORS;
	}
}
