package se.unlogic.hierarchy.foregroundmodules.togglemodule;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.UserMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class ToggleModule extends AnnotatedForegroundModule {

	@ModuleSetting(allowsNull = true)
	@GroupMultiListSettingDescriptor(name = "Admin groups", description = "Groups allowed to administrate this module")
	protected List<Integer> adminGroupIDs;

	@ModuleSetting(allowsNull = true)
	@UserMultiListSettingDescriptor(name = "Admin users", description = "Users allowed to administrate this module")
	protected List<Integer> adminUserIDs;

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(description = "List of background module IDs, one per row", name = "Background Module IDs")
	private List<Integer> backgroundModuleIDs;

	@ModuleSetting(allowsNull = true)
	@TextAreaSettingDescriptor(description = "List of foreground module IDs, one per row", name = "Foreground Module IDs")
	private List<Integer> foregroundModuleIDs;

	@ModuleSetting
	@CheckboxSettingDescriptor(description = "Should changes to module be persistent after server restart", name = "Persist changes")
	private boolean persistChanges = true;

	@Override
	public synchronized ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		log.info("User " + user + " opened toggle module view");

		if(backgroundModuleIDs == null || foregroundModuleIDs == null){
			Document doc = createDocument(req, uriParser, user);
			Element listSettingsElement = doc.createElement("ListModules");
			doc.getFirstChild().appendChild(listSettingsElement);

			listSettingsElement.appendChild(doc.createElement("ModuleNotConfigured"));
			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}

		List<ModuleDescriptor> backgroundModuleDescriptors = new ArrayList<ModuleDescriptor>();

		for(Integer backgroundModuleId : backgroundModuleIDs){
			ModuleDescriptor moduleDescriptor = getModule(backgroundModuleId, ModuleType.BACKGROUND);

			if(moduleDescriptor != null){
				backgroundModuleDescriptors.add(moduleDescriptor);
			}else{
				log.warn("Background module with id " + backgroundModuleId + " not found.");
			}
		}

		List<ModuleDescriptor> foregroundModuleDescriptors = new ArrayList<ModuleDescriptor>();

		for(Integer foregroundModuleId : foregroundModuleIDs){
			ModuleDescriptor moduleDescriptor = getModule(foregroundModuleId, ModuleType.FOREGROUND);

			if(moduleDescriptor != null){
				foregroundModuleDescriptors.add(moduleDescriptor);
			}else{
				log.warn("Foreground module with id " + foregroundModuleId + " not found.");
			}
		}

		Document doc = createDocument(req, uriParser, user);
		Element listModulesElement = doc.createElement("ListModules");
		doc.getFirstChild().appendChild(listModulesElement);

		Element backgroundModulesElement = doc.createElement("BackgroundModules");
		for(ModuleDescriptor moduleDescriptor : backgroundModuleDescriptors){
			Element moduleDescriptorElement = moduleDescriptor.toXML(doc);

			SectionInterface sectionInterface = systemInterface.getSectionInterface(((BackgroundModuleDescriptor)moduleDescriptor).getSectionID());
			if(sectionInterface == null){
				moduleDescriptorElement.appendChild(XMLUtils.createElement("SectionNotStarted", "", doc));

			}else if(!sectionInterface.getBackgroundModuleCache().getCachedModuleDescriptors().contains(moduleDescriptor)){
				moduleDescriptorElement.appendChild(XMLUtils.createElement("ModuleNotStarted", "", doc));
			}

			backgroundModulesElement.appendChild(moduleDescriptorElement);
		}
		listModulesElement.appendChild(backgroundModulesElement);

		Element foregroundModulesElement = doc.createElement("ForegroundModules");
		for(ModuleDescriptor moduleDescriptor : foregroundModuleDescriptors){
			Element moduleDescriptorElement = moduleDescriptor.toXML(doc);

			SectionInterface sectionInterface = systemInterface.getSectionInterface(((ForegroundModuleDescriptor)moduleDescriptor).getSectionID());
			if(sectionInterface == null){
				moduleDescriptorElement.appendChild(XMLUtils.createElement("SectionNotStarted", "", doc));

			}else if(!sectionInterface.getForegroundModuleCache().getCachedModuleDescriptors().contains(moduleDescriptor)){
				moduleDescriptorElement.appendChild(XMLUtils.createElement("ModuleNotStarted", "", doc));
			}

			foregroundModulesElement.appendChild(moduleDescriptorElement);
		}
		listModulesElement.appendChild(foregroundModulesElement);

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	private ModuleDescriptor getModule(Integer moduleID, ModuleType moduleType) throws SQLException {

		if(moduleType.equals(ModuleType.BACKGROUND)){
			return systemInterface.getCoreDaoFactory().getBackgroundModuleDAO().getModule(moduleID);
		}else if(moduleType.equals(ModuleType.FOREGROUND)){
			return systemInterface.getCoreDaoFactory().getForegroundModuleDAO().getModule(moduleID);
		}else{
			throw new RuntimeException("Unknown module type " + moduleType);
		}
	}

	@WebPublic(alias = "startbackgroundmodule")
	public SimpleForegroundModuleResponse startBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException {

		SimpleBackgroundModuleDescriptor moduleDescriptor = null;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && ((moduleDescriptor = (SimpleBackgroundModuleDescriptor)getModule(Integer.parseInt(uriParser.get(2)), ModuleType.BACKGROUND)) != null)){
			SectionInterface sectionInterface = systemInterface.getSectionInterface(moduleDescriptor.getSectionID());

			if(sectionInterface == null){

				this.log.info("User " + user + " tried to cache background module " + moduleDescriptor + " in non-cached section " + systemInterface.getCoreDaoFactory().getSectionDAO().getSection(moduleDescriptor.getSectionID(), false));

			}else if(sectionInterface.getBackgroundModuleCache().isCached(moduleDescriptor)){

				this.log.info("User " + user + " tried to cache background module " + moduleDescriptor + " which is already cached in section " + sectionInterface.getSectionDescriptor());

			}else{
				try{
					this.log.info("User " + user + " caching background module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getBackgroundModuleCache().cache(moduleDescriptor);

					if(persistChanges){
						moduleDescriptor.setEnabled(true);
						systemInterface.getCoreDaoFactory().getBackgroundModuleDAO().update(moduleDescriptor);
					}
				}catch(Exception e){
					this.log.error("Error caching background module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user, e);
				}
			}
		}

		res.sendRedirect(this.getModuleURI(req));

		return null;
	}

	@WebPublic(alias = "stopbackgroundmodule")
	public SimpleForegroundModuleResponse stopBackgroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleBackgroundModuleDescriptor moduleDescriptor = null;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (moduleDescriptor = (SimpleBackgroundModuleDescriptor)getModule(Integer.parseInt(uriParser.get(2)), ModuleType.BACKGROUND)) != null){

			SectionInterface sectionInterface = systemInterface.getSectionInterface(moduleDescriptor.getSectionID());

			if(sectionInterface == null){

				this.log.info("User " + user + " tried to unload background module " + moduleDescriptor + " in non-cached section " + systemInterface.getCoreDaoFactory().getSectionDAO().getSection(moduleDescriptor.getSectionID(), false));

			}else if(!sectionInterface.getBackgroundModuleCache().isCached(moduleDescriptor)){

				this.log.info("User " + user + " tried to unload uncached background module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor());

			}else{
				try{
					this.log.info("User " + user + " unloading background module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getBackgroundModuleCache().unload(moduleDescriptor);

					if(persistChanges){
						moduleDescriptor.setEnabled(false);
						
						systemInterface.getCoreDaoFactory().getBackgroundModuleDAO().update(moduleDescriptor);
					}
				}catch(Exception e){
					this.log.error("Error unloading background module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user);
				}
			}
		}

		res.sendRedirect(this.getModuleURI(req));

		return null;
	}

	@WebPublic(alias = "startforegroundmodule")
	public SimpleForegroundModuleResponse startForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws IOException, SQLException, URINotFoundException {

		SimpleForegroundModuleDescriptor moduleDescriptor = null;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && ((moduleDescriptor = (SimpleForegroundModuleDescriptor)getModule(Integer.parseInt(uriParser.get(2)), ModuleType.FOREGROUND)) != null)){
			SectionInterface sectionInterface = systemInterface.getSectionInterface(moduleDescriptor.getSectionID());

			if(sectionInterface == null){

				this.log.info("User " + user + " tried to cache foreground module " + moduleDescriptor + " in non-cached section " + systemInterface.getCoreDaoFactory().getSectionDAO().getSection(moduleDescriptor.getSectionID(), false));

			}else if(sectionInterface.getForegroundModuleCache().isCached(moduleDescriptor)){

				this.log.info("User " + user + " tried to cache foreground module " + moduleDescriptor + " which is already cached in section " + sectionInterface.getSectionDescriptor());

			}else{
				try{
					this.log.info("User " + user + " caching foreground module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getForegroundModuleCache().cache(moduleDescriptor);

					if(persistChanges){
						moduleDescriptor.setEnabled(true);
						systemInterface.getCoreDaoFactory().getForegroundModuleDAO().update(moduleDescriptor);
					}
				}catch(Exception e){
					this.log.error("Error caching foreground module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user, e);
				}
			}
		}

		res.sendRedirect(this.getModuleURI(req));

		return null;
	}

	@WebPublic(alias = "stopforegroundmodule")
	public SimpleForegroundModuleResponse stopForegroundModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws URINotFoundException, SQLException, IOException {

		SimpleForegroundModuleDescriptor moduleDescriptor = null;

		if(uriParser.size() == 3 && NumberUtils.isInt(uriParser.get(2)) && (moduleDescriptor = (SimpleForegroundModuleDescriptor)getModule(Integer.parseInt(uriParser.get(2)), ModuleType.FOREGROUND)) != null){

			SectionInterface sectionInterface = systemInterface.getSectionInterface(moduleDescriptor.getSectionID());

			if(sectionInterface == null){

				this.log.info("User " + user + " tried to unload foreground module " + moduleDescriptor + " in non-cached section " + systemInterface.getCoreDaoFactory().getSectionDAO().getSection(moduleDescriptor.getSectionID(), false));

			}else if(!sectionInterface.getForegroundModuleCache().isCached(moduleDescriptor)){

				this.log.info("User " + user + " tried to unload uncached foreground module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor());

			}else{
				try{
					this.log.info("User " + user + " unloading foreground module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor());
					sectionInterface.getForegroundModuleCache().unload(moduleDescriptor);

					if(persistChanges){
						moduleDescriptor.setEnabled(false);
						systemInterface.getCoreDaoFactory().getForegroundModuleDAO().update(moduleDescriptor);
					}
				}catch(Exception e){
					this.log.error("Error unloading foreground module " + moduleDescriptor + " in section " + sectionInterface.getSectionDescriptor() + " requested by user " + user);
				}
			}
		}

		res.sendRedirect(this.getModuleURI(req));

		return null;
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element documentElement = doc.createElement("Document");
		doc.appendChild(documentElement);
		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		if(user != null){
			documentElement.appendChild(user.toXML(doc));
		}

		return doc;
	}
}
