/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.threadinfo;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.threads.ThreadUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class ThreadInfoModule extends AnnotatedForegroundModule {

	protected static final String INTERRUPT_OPERATION = "interrupted";
	protected static final String STOP_OPERATION = "stopped";

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		return list(req, res, user, uriParser, null, null, null);
	}

	public SimpleForegroundModuleResponse list(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser, ValidationError validationError, String operation, String threadName) throws Exception {

		log.info("User " + user + " listing threads");

		Document doc = XMLUtils.createDomDocument();

		Element documentElement = doc.createElement("document");
		doc.appendChild(documentElement);
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser, false, false));

		if (validationError != null) {

			documentElement.appendChild(validationError.toXML(doc));

		} else if (operation != null) {

			XMLUtils.appendNewCDATAElement(doc, documentElement, operation, threadName);
		}

		documentElement.appendChild(XMLUtils.createCDATAElement("activeThreads", Thread.activeCount() + "", doc));

		Element threadsElement = doc.createElement("threads");
		documentElement.appendChild(threadsElement);

		Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();

		for (Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet()) {

			Element thread = doc.createElement("thread");
			threadsElement.appendChild(thread);

			thread.appendChild(XMLUtils.createCDATAElement("name", entry.getKey().getName(), doc));
			thread.appendChild(XMLUtils.createCDATAElement("priority", entry.getKey().getPriority() + "", doc));
			thread.appendChild(XMLUtils.createCDATAElement("alive", entry.getKey().isAlive() + "", doc));
			thread.appendChild(XMLUtils.createCDATAElement("daemon", entry.getKey().isDaemon() + "", doc));
			thread.appendChild(XMLUtils.createCDATAElement("interrupted", entry.getKey().isInterrupted() + "", doc));

			Element stacktrace = doc.createElement("stacktrace");
			thread.appendChild(stacktrace);

			if (entry.getValue() != null && entry.getValue().length > 0) {

				for (StackTraceElement stackTraceElement : entry.getValue()) {

					Element traceElement = doc.createElement("stackTraceElement");
					stacktrace.appendChild(traceElement);

					traceElement.appendChild(XMLUtils.createCDATAElement("className", stackTraceElement.getClassName(), doc));
					traceElement.appendChild(XMLUtils.createCDATAElement("fileName", stackTraceElement.getFileName() + "", doc));
					traceElement.appendChild(XMLUtils.createCDATAElement("methodName", stackTraceElement.getMethodName() + "", doc));
					traceElement.appendChild(XMLUtils.createCDATAElement("lineNumber", stackTraceElement.getLineNumber() + "", doc));
					traceElement.appendChild(XMLUtils.createCDATAElement("native", stackTraceElement.isNativeMethod() + "", doc));
				}
			}
		}

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), getDefaultBreadcrumb());
	}

	@WebPublic(alias = "stop")
	public SimpleForegroundModuleResponse stopThread(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		String name = req.getParameter("name");

		if (StringUtils.isEmpty(name)) {

			return list(req, res, user, uriParser, new ValidationError("UnableToStopThreadNoNameSpecified"), null, null);

		} else if (ThreadUtils.killThread(name)) {

			log.info("User " + user + " killed thread " + name);
			
			return list(req, res, user, uriParser, null, STOP_OPERATION, name);

		} else {

			log.info("User " + user + " tried to kill non existing thread " + name);
			
			return list(req, res, user, uriParser, new ValidationError("UnableToStopThreadNotFound"), null, null);
		}
	}

	@WebPublic(alias = "interrupt")
	public SimpleForegroundModuleResponse interruptThread(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		String name = req.getParameter("name");

		if (StringUtils.isEmpty(name)) {

			return list(req, res, user, uriParser, new ValidationError("UnableToInterruptThreadNoNameSpecified"), null, null);

		} else if (ThreadUtils.interruptThread(name)) {

			log.info("User " + user + " intrerrupted thread " + name);
			
			return list(req, res, user, uriParser, null, INTERRUPT_OPERATION, name);

		} else {

			log.info("User " + user + " tried to interrupt non existing thread " + name);
			
			return list(req, res, user, uriParser, new ValidationError("UnableToInterruptThreadNotFound"), null, null);
		}
	}
}
