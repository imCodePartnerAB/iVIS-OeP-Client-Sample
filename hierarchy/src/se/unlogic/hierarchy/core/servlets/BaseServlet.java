/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.standardutils.streams.StreamUtils;
import se.unlogic.webutils.http.HTTPUtils;

public abstract class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 3757354033072015112L;

	private static final String FAIL_SAFE_MESSAGE = "failsafe.html";
	private static final String CONFIG_NOT_FOUND_MESSAGE = "confignotfound.html";
	private static final String SERVLET_ERROR_MESSAGE = "servleterror.html";

	protected Logger log;
	private SystemStatus systemStatus;

	public SystemStatus getSystemStatus() {

		return systemStatus;
	}

	public void setSystemStatus(SystemStatus systemStatus) {

		this.systemStatus = systemStatus;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {

		this.preProcessRequest(req, res);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) {

		this.preProcessRequest(req, res);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse res) {

		this.preProcessRequest(req, res);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse res) {

		this.preProcessRequest(req, res);
	}

	protected void preProcessRequest(HttpServletRequest req, HttpServletResponse res) {

		try{
			if(log != null && log.isDebugEnabled()){
				log.debug("Processing " + req.getMethod() + " request for " + req.getRequestURI() + " " + req.getQueryString() + " from " + req.getRemoteAddr());
			}

			//Check if the system is started
			if(systemStatus.equals(SystemStatus.STARTED) || systemStatus.equals(SystemStatus.STARTING)){

				//System started, process request
				this.processRequest(req, res);

			}else if(systemStatus.equals(SystemStatus.FAIL_SAFE)){

				//The system is not started display error page
				sendMessage(FAIL_SAFE_MESSAGE, res);

			}else if(systemStatus.equals(SystemStatus.CONFIG_NOT_FOUND)){

				//The system is not started due to no config being found
				sendMessage(CONFIG_NOT_FOUND_MESSAGE, res);
			}

			if(log != null){
				log.debug("Request processed.");
			}

		}catch(Throwable e){

			if(log != null){
				log.error("Error in servlet", e);
			}else{
				System.out.println("Error in BaseServlet, printing stacktrace...");
				e.printStackTrace();
			}

			try{
				sendMessage(SERVLET_ERROR_MESSAGE, res);
			}catch(Exception e1){}
		}
	}

	protected abstract void processRequest(HttpServletRequest req, HttpServletResponse res) throws Exception;

	public abstract void init(boolean throwExceptions) throws ServletException;

	public void sendMessage(String filename, HttpServletResponse res){

		InputStream inputStream = null;

		try{
			File customMessageFile = new File(this.getServletContext().getRealPath("/") + "WEB-INF/errormessages/" + filename);

			if(customMessageFile.canRead()){

				inputStream = new FileInputStream(customMessageFile);

			}else{

				inputStream = this.getClass().getResourceAsStream("/se/unlogic/hierarchy/core/errormessages/" + filename);
			}

			HTTPUtils.sendReponse(inputStream, "text/html", res);

		}catch(IOException e){
			//Not much we can do here
		}finally{
			StreamUtils.closeStream(inputStream);
		}
	}
}
