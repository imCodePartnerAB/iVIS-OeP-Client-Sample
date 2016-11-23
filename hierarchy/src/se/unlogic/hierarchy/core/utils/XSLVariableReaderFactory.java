/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.VisibleModuleDescriptor;
import se.unlogic.standardutils.xsl.XSLVariableReader;

public class XSLVariableReaderFactory {

	public static XSLVariableReader getVariableReader(VisibleModuleDescriptor moduleDescriptor, SystemInterface systemInterface) throws SAXException, IOException, ParserConfigurationException, ClassNotFoundException, URISyntaxException, XPathExpressionException{

		if(moduleDescriptor.getXslPath() != null && moduleDescriptor.getXslPathType() != null){

			if(moduleDescriptor.getXslPathType() == PathType.Filesystem){

				return new XSLVariableReader(moduleDescriptor.getXslPath());

			}else if(moduleDescriptor.getXslPathType() == PathType.RealtiveFilesystem){

				return new XSLVariableReader(systemInterface.getApplicationFileSystemPath() + moduleDescriptor.getXslPath());

			}else if(moduleDescriptor.getXslPathType() == PathType.Classpath){

				URL styleSheetURL = Class.forName(moduleDescriptor.getClassname()).getResource(moduleDescriptor.getXslPath());

				if(styleSheetURL != null){

					return new XSLVariableReader(styleSheetURL.toURI());

				}
			}
		}

		return null;
	}
}
