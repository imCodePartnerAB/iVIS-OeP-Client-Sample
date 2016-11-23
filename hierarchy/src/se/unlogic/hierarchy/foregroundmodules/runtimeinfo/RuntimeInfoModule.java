/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.runtimeinfo;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.io.BinarySizes;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.time.TimeUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.image.GaugeGenerator;

public class RuntimeInfoModule extends AnnotatedForegroundModule {

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		log.info("User " + user + " viewing runtime info");

		Document doc = XMLUtils.createDomDocument();

		Element documentElement = doc.createElement("Document");
		doc.appendChild(documentElement);

		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));

		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();

		documentElement.appendChild(XMLUtils.createElement("AvailableProcessors", operatingSystemMXBean.getAvailableProcessors(), doc));
		documentElement.appendChild(XMLUtils.createElement("Arch", operatingSystemMXBean.getArch(), doc));
		documentElement.appendChild(XMLUtils.createElement("Name", operatingSystemMXBean.getName(), doc));
		documentElement.appendChild(XMLUtils.createElement("Version", operatingSystemMXBean.getVersion(), doc));

		//TODO When moving on to Java 1.6 implement the getSystemLoadAverage() method

		ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();

		documentElement.appendChild(XMLUtils.createElement("LoadedClassCount", classLoadingMXBean.getLoadedClassCount(), doc));
		documentElement.appendChild(XMLUtils.createElement("UnloadedClassCount", classLoadingMXBean.getUnloadedClassCount(), doc));
		documentElement.appendChild(XMLUtils.createElement("TotalLoadedClassCount", classLoadingMXBean.getTotalLoadedClassCount(), doc));

		documentElement.appendChild(toXML(doc,ManagementFactory.getMemoryMXBean().getHeapMemoryUsage(),"Heap Memory Usage",null));
		documentElement.appendChild(toXML(doc,ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage(),"Non Heap Memory Usage",null));

		for(MemoryPoolMXBean memoryPoolMXBean : ManagementFactory.getMemoryPoolMXBeans()){

			documentElement.appendChild(toXML(doc,memoryPoolMXBean.getUsage(),memoryPoolMXBean.getName(),memoryPoolMXBean.getType().toString()));
		}

		return new SimpleForegroundModuleResponse(doc,moduleDescriptor.getName(),this.getDefaultBreadcrumb());
	}

	private Node toXML(Document doc, MemoryUsage memoryUsage, String name, String type) {

		Element memoryUsageElement = doc.createElement("MemoryUsage");

		XMLUtils.appendNewCDATAElement(doc, memoryUsageElement, "Name", name);
		XMLUtils.appendNewCDATAElement(doc, memoryUsageElement, "Type", type);

		XMLUtils.appendNewElement(doc, memoryUsageElement, "Init", (memoryUsage.getInit()/BinarySizes.MegaByte));
		XMLUtils.appendNewElement(doc, memoryUsageElement, "Used", (memoryUsage.getUsed()/BinarySizes.MegaByte));
		XMLUtils.appendNewElement(doc, memoryUsageElement, "Committed", (memoryUsage.getCommitted()/BinarySizes.MegaByte));
		XMLUtils.appendNewElement(doc, memoryUsageElement, "Max", (memoryUsage.getMax()/BinarySizes.MegaByte));

		return memoryUsageElement;
	}

	@WebPublic(alias="gauage")
	public ForegroundModuleResponse generateGauge(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, URINotFoundException{

		Float percent;

		if(uriParser.size() == 3 && (percent = NumberUtils.toFloat(uriParser.get(2))) != null){

			try {

				GaugeGenerator.getPercentGauge(percent, res, 150);

			} catch (Exception e) {

				log.info("Error " + e + " sending generated gauage to user " + user + " requesting from " + req.getRemoteAddr());
			}

			return null;
		}

		throw new URINotFoundException(uriParser);
	}

	@WebPublic(alias="gc")
	public ForegroundModuleResponse runGC(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, URINotFoundException{

		log.info("User " + user + " invoking gargabe collector...");

		long startTime = System.currentTimeMillis();

		System.gc();

		log.info("GC executed in " + TimeUtils.millisecondsToString(System.currentTimeMillis() - startTime) + " ms");

		redirectToDefaultMethod(req, res);

		return null;
	}
}
