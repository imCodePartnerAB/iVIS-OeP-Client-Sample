/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.core.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.enums.ResponseType;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;

public final class SimpleForegroundModuleResponse extends BaseModuleResponse implements ForegroundModuleResponse {

	private ForegroundModuleDescriptor moduleDescriptor;
	private String title;
	private final ArrayList<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
	private List<BackgroundModuleResponse> backgroundModuleResponses;
	private boolean userChanged;
	private boolean excludeSystemTransformation = false;
	private SectionMenu sectionMenu;
	private boolean excludeSectionBreadcrumbs;

	//TODO remove most constructors and break up this class into three separate variants

	public SimpleForegroundModuleResponse(Document document, boolean userChanged, Breadcrumb... breadcrumbs) {
		super(document);
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(Document document, boolean userChanged, String title, Breadcrumb... breadcrumbs) {
		super(document);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(Document document, Transformer transformer, boolean userChanged, Breadcrumb... breadcrumbs) {
		super(document, transformer);
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(Document document, Transformer transformer, boolean userChanged, String title, Breadcrumb... breadcrumbs) {
		super(document, transformer);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(Element response, boolean userChanged, Breadcrumb... breadcrumbs) {
		super(response);
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(Element response, boolean userChanged, String title, Breadcrumb... breadcrumbs) {
		super(response);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(String response, boolean userChanged, Breadcrumb... breadcrumbs) {
		super(response);
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(String response, boolean userChanged, String title, Breadcrumb... breadcrumbs) {
		super(response);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
		this.userChanged = userChanged;
	}

	public SimpleForegroundModuleResponse(Element response, Breadcrumb... breadcrumbs) {
		super(response);
		this.addBreadcrumbs(breadcrumbs);
	}

	public SimpleForegroundModuleResponse(Element response, String title, Breadcrumb... breadcrumbs) {
		super(response);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
	}

	public SimpleForegroundModuleResponse(String response, Breadcrumb... breadcrumbs) {
		super(response);
		this.addBreadcrumbs(breadcrumbs);
	}

	public SimpleForegroundModuleResponse(String response, String title, Breadcrumb... breadcrumbs) {
		super(response);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
	}

	public SimpleForegroundModuleResponse(Document response, Breadcrumb... breadcrumbs) {
		super(response);
		this.addBreadcrumbs(breadcrumbs);
	}

	public SimpleForegroundModuleResponse(Document response, String title, Breadcrumb... breadcrumbs) {
		super(response);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
	}

	public SimpleForegroundModuleResponse(Document response, Transformer transformer, Breadcrumb... breadcrumbs) {
		super(response,transformer);
		this.document = response;
		this.transformer = transformer;
		this.addBreadcrumbs(breadcrumbs);
	}

	public SimpleForegroundModuleResponse(Document response, Transformer transformer, String title, Breadcrumb... breadcrumbs) {
		super(response,transformer);
		this.title = title;
		this.addBreadcrumbs(breadcrumbs);
	}

	private void addBreadcrumbs(Breadcrumb[] breadcrumbs) {

		this.breadcrumbs.addAll(Arrays.asList(breadcrumbs));
	}

	@Override
	public void addBreadcrumbFirst(Breadcrumb breadcrumb) {

		this.breadcrumbs.add(0,breadcrumb);
	}

	public void addBreadcrumbLast(Breadcrumb breadcrumb) {

		this.breadcrumbs.add(breadcrumb);
	}	
	
	public void addBreadcrumbsLast(List<Breadcrumb> breadcrumbs) {

		this.breadcrumbs.addAll(breadcrumbs);
	}
	
	@Override
	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	@Override
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public boolean isUserChanged() {
		return userChanged;
	}

	public void setUserChanged(boolean userChanged) {
		this.userChanged = userChanged;
	}

	@Override
	public void excludeSystemTransformation(boolean excludeSystemTransformation) {
		this.excludeSystemTransformation = excludeSystemTransformation;
	}

	@Override
	public boolean isExcludeSystemTransformation() {
		return excludeSystemTransformation;
	}
	
	@Override
	public ResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}

	@Override
	public Transformer getTransformer() {
		return transformer;
	}

	@Override
	public void setMenu(SectionMenu sectionMenu){
		
		this.sectionMenu = sectionMenu;
	}
	
	@Override
	public SectionMenu getMenu(){
		
		return this.sectionMenu;
	}

	@Override
	public void setTransformer(Transformer transformer) {
		this.transformer = transformer;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public ForegroundModuleDescriptor getModuleDescriptor() {
		return moduleDescriptor;
	}

	@Override
	public void setModuleDescriptor(ForegroundModuleDescriptor moduleDescriptor) {
		this.moduleDescriptor = moduleDescriptor;
	}

	@Override
	public final ArrayList<Breadcrumb> getBreadcrumbs() {
		return breadcrumbs;
	}

	@Override
	public final List<BackgroundModuleResponse> getBackgroundModuleResponses() {
		return backgroundModuleResponses;
	}

	@Override
	public void addBackgroundModuleResponses(List<BackgroundModuleResponse> backgroundModuleResponses) {

		if(this.backgroundModuleResponses == null){

			this.backgroundModuleResponses = backgroundModuleResponses;

		}else{

			this.backgroundModuleResponses.addAll(backgroundModuleResponses);
		}
	}

	
	public void setTitle(String title) {
	
		this.title = title;
	}
	
	@Override
	public boolean isExcludeSectionBreadcrumbs() {
		return excludeSectionBreadcrumbs;
	}

	public void setExcludeSectionBreadcrumbs(boolean excludeSectionBreadcrumbs) {
		this.excludeSectionBreadcrumbs = excludeSectionBreadcrumbs;
	}

}
