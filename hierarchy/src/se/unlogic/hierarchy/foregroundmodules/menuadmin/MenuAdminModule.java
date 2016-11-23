/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.hierarchy.foregroundmodules.menuadmin;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.Bundle;
import se.unlogic.hierarchy.core.beans.MenuItem;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleSectionDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.beans.VirtualMenuItem;
import se.unlogic.hierarchy.core.daos.factories.CoreDaoFactory;
import se.unlogic.hierarchy.core.daos.interfaces.SectionDAO;
import se.unlogic.hierarchy.core.daos.interfaces.VirtualMenuItemDAO;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.populators.VirtualMenuItemPopulator;
import se.unlogic.hierarchy.core.utils.usergrouplist.UserGroupListConnector;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class MenuAdminModule extends AnnotatedForegroundModule {

	private static final VirtualMenuItemPopulator POPULATOR = new VirtualMenuItemPopulator();

	private SectionDAO sectionDAO;
	private VirtualMenuItemDAO virtualMenuItemDAO;

	protected UserGroupListConnector userGroupListConnector;
	
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptorBean, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptorBean, sectionInterface, dataSource);

		CoreDaoFactory coreDaoFactory = systemInterface.getCoreDaoFactory();

		this.sectionDAO = coreDaoFactory.getSectionDAO();
		this.virtualMenuItemDAO = coreDaoFactory.getVirtualMenuItemDAO();
		
		this.userGroupListConnector = new UserGroupListConnector(systemInterface);
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);
	}

	@Override
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		log.info("User " + user + " listing menu tree");

		Document doc = this.createDocument(req, uriParser);

		SimpleSectionDescriptor rootSection = this.sectionDAO.getRootSection(true);

		Element sectionsElement = doc.createElement("sections");
		doc.getFirstChild().appendChild(sectionsElement);

		this.appendSection(sectionsElement, doc, rootSection, true);

		return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@WebPublic
	public SimpleForegroundModuleResponse addMenuItem(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, IOException {

		Integer sectionID = null;

		if (uriParser.size() == 3 && (sectionID = NumberUtils.toInt(uriParser.get(2))) != null) {

			SimpleSectionDescriptor simpleSectionDescriptor = this.sectionDAO.getSection(sectionID, false);

			if (simpleSectionDescriptor != null) {

				ValidationException validationException = null;

				if (req.getMethod().equalsIgnoreCase("POST")) {

					try {
						VirtualMenuItem virtualMenuItem = POPULATOR.populate(req);

						virtualMenuItem.setSectionID(sectionID);

						log.info("User " + user + " adding virtual menuitem " + virtualMenuItem);

						this.virtualMenuItemDAO.add(virtualMenuItem);

						this.reloadVirtualMenuItemCache(sectionID);

						this.redirectToDefaultMethod(req, res);

						return null;

					} catch (ValidationException e) {
						validationException = e;
					}
				}

				Document doc = this.createDocument(req, uriParser);

				Element addMenuItem = doc.createElement("addMenuItem");
				doc.getFirstChild().appendChild(addMenuItem);

				addMenuItem.appendChild(simpleSectionDescriptor.toXML(doc));

				if (validationException != null) {
					addMenuItem.appendChild(validationException.toXML(doc));
					addMenuItem.appendChild(RequestUtils.getRequestParameters(req, doc));
				}

				return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), getDefaultBreadcrumb());

			} else {
				log.info("Section with sectionID \"" + uriParser.get(1) + "\" not found, requested by user " + user);

				this.redirectToDefaultMethod(req, res);

				return null;
			}

		} else {
			log.warn("Bad sectionID \"" + uriParser.get(1) + "\" provided by user " + user);

			this.redirectToDefaultMethod(req, res);

			return null;
		}
	}

	private void reloadVirtualMenuItemCache(Integer sectionID) throws SQLException {

		SectionInterface sectionInterface = systemInterface.getSectionInterface(sectionID);

		if (sectionInterface != null) {
			sectionInterface.getMenuCache().cacheVirtualMenuItems();
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse updateMenuItem(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, IOException {

		Integer menuItemID = null;

		if (uriParser.size() == 3 && (menuItemID = NumberUtils.toInt(uriParser.get(2))) != null) {

			VirtualMenuItem virtualMenuItem = this.virtualMenuItemDAO.getMenuItem(menuItemID);

			if (virtualMenuItem != null) {

				ValidationException validationException = null;

				if (req.getMethod().equalsIgnoreCase("POST")) {

					try {
						virtualMenuItem = POPULATOR.populate(virtualMenuItem, req);

						log.info("User " + user + " updating virtual menuitem " + virtualMenuItem);

						this.virtualMenuItemDAO.update(virtualMenuItem);

						this.reloadVirtualMenuItemCache(virtualMenuItem.getSectionID());

						this.redirectToDefaultMethod(req, res);

						return null;

					} catch (ValidationException e) {
						validationException = e;
					}
				}

				Document doc = this.createDocument(req, uriParser);

				Element updateMenuItem = doc.createElement("updateMenuItem");
				doc.getFirstChild().appendChild(updateMenuItem);

				updateMenuItem.appendChild(virtualMenuItem.getFullXML(doc));

				if (validationException != null) {
					updateMenuItem.appendChild(validationException.toXML(doc));
					updateMenuItem.appendChild(RequestUtils.getRequestParameters(req, doc));
				}

				if(virtualMenuItem.getAllowedUserIDs() != null){
					
					XMLUtils.append(doc, updateMenuItem, "Users", systemInterface.getUserHandler().getUsers(virtualMenuItem.getAllowedUserIDs(), false, true));
				}

				if(virtualMenuItem.getAllowedGroupIDs() != null){
					
					XMLUtils.append(doc, updateMenuItem, "Groups", systemInterface.getGroupHandler().getGroups(virtualMenuItem.getAllowedGroupIDs(), false));
				}
				
				return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), getDefaultBreadcrumb());

			} else {
				log.info("Virtual menuitem with menuItemID \"" + uriParser.get(2) + "\" not found, requested by user " + user);

				this.redirectToDefaultMethod(req, res);

				return null;
			}

		} else {
			log.warn("Bad menuItemID \"" + uriParser.get(2) + "\" requested by user " + user);

			this.redirectToDefaultMethod(req, res);

			return null;
		}

	}

	@WebPublic
	public SimpleForegroundModuleResponse deleteMenuItem(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, IOException {

		Integer menuItemID = null;

		if (uriParser.size() == 3 && (menuItemID = NumberUtils.toInt(uriParser.get(2))) != null) {

			VirtualMenuItem virtualMenuItem = this.virtualMenuItemDAO.getMenuItem(menuItemID);

			if (virtualMenuItem != null) {

				log.info("User " + user + " deleting virtual menuitem " + virtualMenuItem);

				this.virtualMenuItemDAO.delete(virtualMenuItem);

				this.reloadVirtualMenuItemCache(virtualMenuItem.getSectionID());

				this.redirectToDefaultMethod(req, res);

				return null;

			} else {
				log.info("Virtual menuitem with menuItemID \"" + uriParser.get(2) + "\" not found, requested by user " + user);

				this.redirectToDefaultMethod(req, res);

				return null;
			}

		} else {
			log.warn("Bad menuItemID \"" + uriParser.get(2) + "\" requested by user " + user);

			this.redirectToDefaultMethod(req, res);

			return null;
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse moveMenuItem(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException {

		Integer menuItemID = null;

		if ((uriParser.size() == 3 || uriParser.size() == 4) && (menuItemID = NumberUtils.toInt(uriParser.get(2))) != null) {

			VirtualMenuItem virtualMenuItem = this.virtualMenuItemDAO.getMenuItem(menuItemID);

			if (virtualMenuItem != null) {

				if (uriParser.size() == 4) {

					Integer sectionID = NumberUtils.toInt(uriParser.get(3));

					if (sectionID != null) {

						if (virtualMenuItem.getSectionID().equals(sectionID)) {
							log.debug("User " + user + " trying to move virtual menuitem " + virtualMenuItem + " to the section it already belongs to, ignoring move");
						} else {

							SimpleSectionDescriptor simpleSectionDescriptor = this.sectionDAO.getSection(sectionID, false);

							if (simpleSectionDescriptor != null) {

								log.info("User " + user + " moving virtual menuitem " + virtualMenuItem + " to section " + simpleSectionDescriptor);

								Integer oldSectionID = virtualMenuItem.getSectionID();

								virtualMenuItem.setSectionID(sectionID);

								this.virtualMenuItemDAO.update(virtualMenuItem);

								this.reloadVirtualMenuItemCache(oldSectionID);

								this.reloadVirtualMenuItemCache(sectionID);

								this.redirectToDefaultMethod(req, res);

								return null;

							} else {
								log.warn("User " + user + " trying to move virtual menuitem " + virtualMenuItem + " to non existing section with sectionID " + sectionID);
							}
						}
					} else {
						log.warn("Bad sectionID \"" + uriParser.get(3) + "\" provided for virtual menuitem " + virtualMenuItem + " by user " + user);
					}
				}

				Document doc = this.createDocument(req, uriParser);

				SimpleSectionDescriptor rootSection = this.sectionDAO.getRootSection(true);

				Element moveMenuItemElement = doc.createElement("moveMenuItem");
				doc.getFirstChild().appendChild(moveMenuItemElement);

				moveMenuItemElement.appendChild(virtualMenuItem.getFullXML(doc));

				this.appendSection(moveMenuItemElement, doc, rootSection, false);

				return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), getDefaultBreadcrumb());

			} else {
				log.info("Virtual menuitem with menuItemID \"" + uriParser.get(2) + "\" not found, requested by user " + user);

				this.redirectToDefaultMethod(req, res);

				return null;
			}

		} else {
			log.warn("Bad menuItemID \"" + uriParser.get(2) + "\" requested by user " + user);

			this.redirectToDefaultMethod(req, res);

			return null;
		}
	}

	@WebPublic
	public SimpleForegroundModuleResponse sortMenu(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws SQLException, IOException {

		// Get sectionID
		Integer sectionID = null;

		if (uriParser.size() == 3 && (sectionID = NumberUtils.toInt(uriParser.get(2))) != null) {

			// Get sectioninterface
			SectionInterface sectionInterface = systemInterface.getSectionInterface(sectionID);

			if (sectionInterface != null) {

				if (req.getMethod().equalsIgnoreCase("POST") && req.getParameter("itempositions") != null) {

					log.info("User " + user + " updating menuinex for section " + sectionInterface.getSectionDescriptor());

					// Parse and save new menuindex'es
					String[] itemPositions = req.getParameter("itempositions").replaceAll("menusorter=", "").replaceAll("-", "").replaceAll("[+]", "").split(",");

					log.debug("Itempositions: " + StringUtils.toCommaSeparatedString(itemPositions));

					MenuItem[] menuItemArray = sectionInterface.getMenuCache().getMenuItemSet().toArray(new MenuItem[sectionInterface.getMenuCache().getMenuItemSet().size()]);

					// Save current menuIndex values in a temporary array
					int[] menuIndexArray = new int[menuItemArray.length];

					for (int i = 0; i < menuItemArray.length; i++) {
						menuIndexArray[i] = menuItemArray[i].getMenuIndex();
					}

					// Update index of menuitems using temporary array
					for (int i = 0; i < itemPositions.length && i < menuItemArray.length; i++) {
						log.debug("TextFieldSettingDescriptor " + menuItemArray[Integer.parseInt(itemPositions[i])].toString() + " menuindex to " + (menuIndexArray[i]) + " (" + menuItemArray[Integer.parseInt(itemPositions[i])].getMenuIndex() + ")");
						menuItemArray[Integer.parseInt(itemPositions[i])].setMenuIndex(menuIndexArray[i]);
					}

					sectionInterface.getMenuCache().rebuildIndex();
					sectionInterface.getMenuCache().saveIndex();

					this.redirectToDefaultMethod(req, res);

					return null;

				} else {
					Document doc = this.createDocument(req, uriParser);

					// List menu items
					Element sortMenuElement = doc.createElement("sortMenu");
					doc.getFirstChild().appendChild(sortMenuElement);

					// Append section descriptor
					sortMenuElement.appendChild(sectionInterface.getSectionDescriptor().toXML(doc));

					Element menuitemsElement = doc.createElement("menuitems");
					sortMenuElement.appendChild(menuitemsElement);

					for (MenuItem menuItem : sectionInterface.getMenuCache().getMenuItemSet()) {

						if (menuItem instanceof Bundle) {
							menuitemsElement.appendChild(((Bundle) menuItem).toFullXML(doc));
						} else {
							menuitemsElement.appendChild(menuItem.toXML(doc));
						}
					}

					return new SimpleForegroundModuleResponse(doc, this.moduleDescriptor.getName(), getDefaultBreadcrumb());
				}

			} else {
				log.info("Non existing- or not cache section requested using sectionID \"" + uriParser.get(2) + "\" by user " + user);

				this.redirectToDefaultMethod(req, res);

				return null;
			}
		} else {
			log.warn("Bad sectionID \"" + uriParser.get(2) + "\" requested by user " + user);

			this.redirectToDefaultMethod(req, res);

			return null;
		}
	}

	private void appendSection(Element parentSection, Document doc, SimpleSectionDescriptor simpleSectionDescriptor, boolean menu) throws SQLException {

		Element sectionElement = simpleSectionDescriptor.toXML(doc);
		parentSection.appendChild(sectionElement);

		SectionInterface sectionInterface = systemInterface.getSectionInterface(simpleSectionDescriptor.getSectionID());

		if (sectionInterface == null) {
			// Section not cached
			sectionElement.setAttribute("cached", "false");

		} else {
			// Section cached, get menuitems
			sectionElement.setAttribute("cached", "true");

			if (menu) {
				Element menuElement = doc.createElement("menuitems");
				sectionElement.appendChild(menuElement);

				for (MenuItem menuItem : sectionInterface.getMenuCache().getMenuItemSet()) {

					//TODO this can be made more efficent using different toXML methods
					if (menuItem instanceof Bundle) {
						menuElement.appendChild(((Bundle) menuItem).toFullXML(doc));
					} else {
						menuElement.appendChild(menuItem.toXML(doc));
					}
				}

				Element modules = doc.createElement("modules");
				sectionElement.appendChild(modules);

				for (ForegroundModuleDescriptor moduleDescriptor : sectionInterface.getForegroundModuleCache().getCachedModuleDescriptors()) {
					modules.appendChild(moduleDescriptor.toXML(doc));
				}
			}
		}

		if (simpleSectionDescriptor.getSubSectionsList() != null) {
			Element subSectionsElement = doc.createElement("subsections");
			sectionElement.appendChild(subSectionsElement);

			for (SimpleSectionDescriptor subSectionBean : simpleSectionDescriptor.getSubSectionsList()) {
				this.appendSection(subSectionsElement, doc, subSectionBean, menu);
			}
		}
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser) {

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("document");
		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.sectionInterface.getSectionDescriptor().toXML(doc));
		document.appendChild(this.moduleDescriptor.toXML(doc));
		doc.appendChild(document);
		return doc;
	}
	
	@WebPublic(alias = "users")
	public ForegroundModuleResponse getUsers(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getUsers(req, res, user, uriParser);
	}

	@WebPublic(alias = "groups")
	public ForegroundModuleResponse getGroups(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		return userGroupListConnector.getGroups(req, res, user, uriParser);
	}
}
