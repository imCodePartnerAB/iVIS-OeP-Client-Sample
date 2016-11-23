/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.fileuploadutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class MultipartRequest implements HttpServletRequest {

	private final SafeDiskFileItemFactory factory;
	private final ServletFileUpload uploadHandler;

	private final HashMap<String, String[]> parameterMap = new HashMap<String, String[]>();
	private final HashMap<String, List<FileItem>> fileMap = new HashMap<String, List<FileItem>>();
	private final ArrayList<FileItem> fileItemList = new ArrayList<FileItem>();
	private final HttpServletRequest originalRequest;

	public MultipartRequest(int ramThreshold, long maxSize, HttpServletRequest req) throws FileUploadException {

		this(ramThreshold, maxSize, (File)null, req);
	}

	public MultipartRequest(int ramThreshold, long maxSize, long maxFileSize, HttpServletRequest req) throws FileUploadException {

		this(ramThreshold, maxSize, maxFileSize, (File)null, req);
	}

	public MultipartRequest(int ramThreshold, long maxSize, String repository, HttpServletRequest req) throws FileUploadException {

		this(ramThreshold, maxSize, repository != null ? new File(repository) : null, req);
	}

	public MultipartRequest(int ramThreshold, long maxSize, File repository, HttpServletRequest req) throws FileUploadException {

		this(ramThreshold,maxSize,null,repository,req);
	}

	public MultipartRequest(int ramThreshold, long maxSize, Long maxFileSize, File repository, HttpServletRequest req) throws FileUploadException {

		this.originalRequest = req;

		factory = new SafeDiskFileItemFactory(ramThreshold, repository);

		uploadHandler = new ServletFileUpload(factory);
		uploadHandler.setSizeMax(maxSize);

		if(maxFileSize != null){
			uploadHandler.setFileSizeMax(maxFileSize);
		}

		HashMap<String, List<String>> tempParameterMap = new HashMap<String, List<String>>();

		List<FileItem> items;
		try {
			items = uploadHandler.parseRequest(req);

			if (items != null && items.size() > 0) {
				for (FileItem item : items) {
					if (item.isFormField()) {

						Field contentType = item.getClass().getDeclaredField("contentType");
						contentType.setAccessible(true);
						contentType.set(item, "charset=UTF-8;");

						List<String> valueList = tempParameterMap.get(item.getFieldName());

						if (valueList != null) {
							valueList.add(item.getString());
						} else {
							valueList = new ArrayList<String>();
							valueList.add(item.getString());
							tempParameterMap.put(item.getFieldName(), valueList);
						}

					} else {

						List<FileItem> fileItems = fileMap.get(item.getFieldName());

						if (fileItems == null) {

							fileItems = new ArrayList<FileItem>(10);
							fileMap.put(item.getFieldName(), fileItems);
						}

						fileItems.add(item);
						this.fileItemList.add(item);
					}
				}
			}

			if (!tempParameterMap.isEmpty()) {

				for (Entry<String, List<String>> entry : tempParameterMap.entrySet()) {

					this.parameterMap.put(entry.getKey(), entry.getValue().toArray(new String[entry.getValue().size()]));
				}
			}

		} catch (FileUploadException e) {
			factory.deleteFiles();
			throw e;
		} catch (IllegalAccessException e) {
			factory.deleteFiles();
		} catch (NoSuchFieldException e) {
			factory.deleteFiles();
		}
	}

	public int getParameterCount() {

		return this.parameterMap.size();
	}

	public Enumeration<?> getParameterNames() {

		return new IteratorEnumeration<String>(this.parameterMap.keySet().iterator());
	}

	public String[] getParameterValues(String name) {

		return this.parameterMap.get(name);
	}

	public int getFileCount() {

		return this.fileItemList.size();
	}

	public String getParameter(String name) {

		String[] values = this.parameterMap.get(name);

		if (values == null) {

			return null;

		} else {

			return values[0];
		}
	}

	public FileItem getFile(String name) {

		List<FileItem> files = this.fileMap.get(name);

		if (files == null || files.isEmpty()) {

			return null;
		}

		return files.get(0);
	}

	public List<FileItem> getFiles(String name) {

		return this.fileMap.get(name);
	}

	public void deleteFiles() {

		factory.deleteFiles();
	}

	public FileItem getFile(int index) {

		return fileItemList.get(index);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<FileItem> getFiles() {

		return (ArrayList<FileItem>) this.fileItemList.clone();
	}

	public static boolean isMultipartRequest(HttpServletRequest req) {

		return ServletFileUpload.isMultipartContent(req);
	}

	public String getAuthType() {

		return originalRequest.getAuthType();
	}

	public String getContextPath() {

		return originalRequest.getContextPath();
	}

	public Cookie[] getCookies() {

		return originalRequest.getCookies();
	}

	public long getDateHeader(String arg0) {

		return originalRequest.getDateHeader(arg0);
	}

	public String getHeader(String arg0) {

		return originalRequest.getHeader(arg0);
	}

	public Enumeration<?> getHeaderNames() {

		return originalRequest.getHeaderNames();
	}

	public Enumeration<?> getHeaders(String arg0) {

		return originalRequest.getHeaders(arg0);
	}

	public int getIntHeader(String arg0) {

		return originalRequest.getIntHeader(arg0);
	}

	public String getMethod() {

		return originalRequest.getMethod();
	}

	public String getPathInfo() {

		return originalRequest.getPathInfo();
	}

	public String getPathTranslated() {

		return originalRequest.getPathTranslated();
	}

	public String getQueryString() {

		return originalRequest.getQueryString();
	}

	public String getRemoteUser() {

		return originalRequest.getRemoteUser();
	}

	public String getRequestURI() {

		return originalRequest.getRequestURI();
	}

	public StringBuffer getRequestURL() {

		return originalRequest.getRequestURL();
	}

	public String getRequestedSessionId() {

		return originalRequest.getRequestedSessionId();
	}

	public String getServletPath() {

		return originalRequest.getServletPath();
	}

	public HttpSession getSession() {

		return originalRequest.getSession();
	}

	public HttpSession getSession(boolean arg0) {

		return originalRequest.getSession(arg0);
	}

	public Principal getUserPrincipal() {

		return originalRequest.getUserPrincipal();
	}

	public boolean isRequestedSessionIdFromCookie() {

		return originalRequest.isRequestedSessionIdFromCookie();
	}

	public boolean isRequestedSessionIdFromURL() {

		return originalRequest.isRequestedSessionIdFromURL();
	}

	@SuppressWarnings("deprecation")
	public boolean isRequestedSessionIdFromUrl() {

		return originalRequest.isRequestedSessionIdFromUrl();
	}

	public boolean isRequestedSessionIdValid() {

		return originalRequest.isRequestedSessionIdValid();
	}

	public boolean isUserInRole(String arg0) {

		return originalRequest.isUserInRole(arg0);
	}

	public Object getAttribute(String arg0) {

		return originalRequest.getAttribute(arg0);
	}

	public Enumeration<?> getAttributeNames() {

		return originalRequest.getAttributeNames();
	}

	public String getCharacterEncoding() {

		return originalRequest.getCharacterEncoding();
	}

	public int getContentLength() {

		return originalRequest.getContentLength();
	}

	public String getContentType() {

		return originalRequest.getContentType();
	}

	public ServletInputStream getInputStream() throws IOException {

		return originalRequest.getInputStream();
	}

	public String getLocalAddr() {

		return originalRequest.getLocalAddr();
	}

	public String getLocalName() {

		return originalRequest.getLocalName();
	}

	public int getLocalPort() {

		return originalRequest.getLocalPort();
	}

	public Locale getLocale() {

		return originalRequest.getLocale();
	}

	public Enumeration<?> getLocales() {

		return originalRequest.getLocales();
	}

	public Map<?, ?> getParameterMap() {

		return parameterMap;
	}

	public String getProtocol() {

		return originalRequest.getProtocol();
	}

	public BufferedReader getReader() throws IOException {

		return originalRequest.getReader();
	}

	@SuppressWarnings("deprecation")
	public String getRealPath(String arg0) {

		return originalRequest.getRealPath(arg0);
	}

	public String getRemoteAddr() {

		return originalRequest.getRemoteAddr();
	}

	public String getRemoteHost() {

		return originalRequest.getRemoteHost();
	}

	public int getRemotePort() {

		return originalRequest.getRemotePort();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {

		return originalRequest.getRequestDispatcher(arg0);
	}

	public String getScheme() {

		return originalRequest.getScheme();
	}

	public String getServerName() {

		return originalRequest.getServerName();
	}

	public int getServerPort() {

		return originalRequest.getServerPort();
	}

	public boolean isSecure() {

		return originalRequest.isSecure();
	}

	public void removeAttribute(String arg0) {

		originalRequest.removeAttribute(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {

		originalRequest.setAttribute(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {

		originalRequest.setCharacterEncoding(arg0);
	}
}
