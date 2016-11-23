/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.webutils.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;

public class URIParser {

	protected List<String> uriList;
	protected String formattedURI;
	protected final String currentURI;
	protected final String contextPath;
	protected final String requestURL;

	public URIParser(HttpServletRequest req, String uriPrefix, String uriSuffix) {

		this.contextPath = req.getContextPath();

		// Remove contextpath from URI
		if (req.getContextPath().length() > 0) {
			formattedURI = req.getRequestURI().substring(req.getContextPath().length());
		} else {
			formattedURI = req.getRequestURI();
		}

		// Unescape the uri
		try {
			formattedURI = URLDecoder.decode(formattedURI, "UTF-8");

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException(e);
		}

		// Remove Prefix
		if (!StringUtils.isEmpty(uriPrefix) && formattedURI.startsWith(uriPrefix)) {

			formattedURI = formattedURI.substring(uriPrefix.length());
		}

		// Remove Suffix
		if (!StringUtils.isEmpty(uriSuffix) && formattedURI.endsWith(uriSuffix)) {
			formattedURI = formattedURI.substring(0, formattedURI.length() - uriSuffix.length());
		}

		// Generate currentURI
		if (!StringUtils.isEmpty(uriPrefix) && req.getServletPath().startsWith(uriPrefix)) {

			this.currentURI = req.getServletPath().substring(uriPrefix.length());

		} else {

			this.currentURI = req.getServletPath();
		}

		// Remove leading and trailing slashes
		if (formattedURI.startsWith("/")) {

			formattedURI = formattedURI.substring(1);
		}

		if (formattedURI.endsWith("/")) {

			formattedURI = formattedURI.substring(0, formattedURI.length() - 1);
		}

		if (formattedURI.length() > 0 && formattedURI.trim() != "") {
			// Split the URI string at each forward slash
			uriList = Arrays.asList(formattedURI.split("/"));

			this.formattedURI = "/" + formattedURI;
		}

		requestURL = RequestUtils.getFullContextPathURL(req) + formattedURI;
	}

	private URIParser(List<String> uriList, String currentURI, String formattedURI, String contextPath, String requestURL) {

		if (!CollectionUtils.isEmpty(uriList)) {
			this.uriList = new ArrayList<String>(uriList.size());
			this.uriList.addAll(uriList);
			this.uriList.remove(0);
			this.currentURI = currentURI + "/" + uriList.get(0);
		} else {
			this.currentURI = currentURI;
		}

		this.formattedURI = formattedURI;
		this.contextPath = contextPath;
		this.requestURL = requestURL;
	}

	public int size() {

		if (this.uriList != null) {
			return this.uriList.size();
		} else {
			return 0;
		}
	}

	public String get(int i) {

		if (this.uriList != null && this.uriList.size() > i) {
			return this.uriList.get(i);
		} else {
			return null;
		}
	}

	public Integer getInt(int i) {

		if (this.uriList != null && this.uriList.size() > i) {
			return NumberUtils.toInt(this.uriList.get(i));
		} else {
			return null;
		}
	}	
	
	public Long getLong(int i) {

		if (this.uriList != null && this.uriList.size() > i) {
			return NumberUtils.toLong(this.uriList.get(i));
		} else {
			return null;
		}
	}	
	
	public boolean contains(String uripart) {

		if (this.uriList != null) {
			return this.uriList.contains(uripart);
		} else {
			return false;
		}
	}

	public String getValue(String uripart) {

		if (this.uriList != null && uriList.indexOf(uripart) != -1 && uriList.size() > (uriList.indexOf(uripart)) + 1) {
			return uriList.get(uriList.indexOf(uripart) + 1);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {

		if (this.formattedURI != null) {
			return this.formattedURI;
		} else {
			return "";
		}
	}

	public String[] getAll() {

		if (this.uriList != null) {
			return this.uriList.toArray(new String[this.uriList.size()]);
		} else {
			return null;
		}

	}

	public URIParser getNextLevel() {

		return new URIParser(this.uriList, this.currentURI, this.formattedURI, this.contextPath, requestURL);
	}

	public String getCurrentURI(boolean includeContextPath) {

		if (includeContextPath) {

			return this.contextPath + this.currentURI;

		} else {

			return currentURI;
		}
	}

	public void addToURI(String uriParts) {

		// Remove leading and trailing slashes
		if (uriParts.startsWith("/")) {
			uriParts = uriParts.substring(1);
		}

		if (uriParts.endsWith("/")) {
			uriParts = uriParts.substring(0, uriParts.length() - 1);
		}

		if (uriParts.length() > 0 && uriParts.trim() != "") {
			// Split the URI string at each forward slash
			List<String> uriList = Arrays.asList(uriParts.split("/"));

			if(this.uriList != null){

				this.uriList.addAll(uriList);

			}else{

				this.uriList = uriList;
			}

		}

		this.formattedURI += "/" + uriParts;
	}

	public String getFormattedURI() {

		return formattedURI;
	}

	public String getRemainingURI() {

		if(uriList == null){

			return "";
		}

		StringBuilder builder = new StringBuilder();

		boolean first = true;

		for (String uriPart : uriList) {

			if (first) {

				first = false;

			} else {

				builder.append("/");
			}

			builder.append(uriPart);
		}

		return builder.toString();
	}

	/**
	 * This is a replacement for the {@link HttpServletRequest#getRequestURL()} method which can be quite expensive since it returns a new StringBuffer each time the method is called.
	 * <br><br>
	 * This method returns an ordinary String instead which is cached when the URIParser is created to improve performance.
	 * 
	 * @return
	 */
	public String getRequestURL(){

		return requestURL;
	}

	public String getURI(int toPart, boolean includeContextPath) {

		StringBuilder builder = new StringBuilder();

		builder.append(getCurrentURI(includeContextPath));

		int partIndex = 0;

		while(partIndex <= toPart && partIndex < uriList.size()){

			builder.append("/");
			builder.append(uriList.get(partIndex));

			partIndex++;
		}

		return builder.toString();
	}

	
	public String getContextPath() {
	
		return contextPath;
	}
}
