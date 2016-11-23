package se.unlogic.hierarchy.foregroundmodules.modulesettings;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.UserMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.BackgroundModule;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.FilterModule;
import se.unlogic.hierarchy.core.interfaces.FilterModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.Module;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.interfaces.SectionDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.sections.Section;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.hierarchy.foregroundmodules.systemadmin.ModuleInfoBean;
import se.unlogic.hierarchy.foregroundmodules.systemadmin.ModuleInfoBeanComparator;
import se.unlogic.hierarchy.foregroundmodules.systemadmin.SystemAdminModule;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.enums.EnumUtils;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.references.WeakReferenceUtils;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;


public class ModuleSettingUpdateModule extends AnnotatedForegroundModule implements AccessInterface {

	private static final ModuleInfoBeanComparator MODULE_COMPARATOR = new ModuleInfoBeanComparator();

	@ModuleSetting(allowsNull = true)
	@GroupMultiListSettingDescriptor(name="Admin groups",description="Groups allowed to administrate this module")
	protected List<Integer> adminGroupIDs;

	@ModuleSetting(allowsNull = true)
	@UserMultiListSettingDescriptor(name="Admin users",description="Users allowed to administrate this module")
	protected List<Integer> adminUserIDs;

	@ModuleSetting(allowsNull = true)
	@TextFieldSettingDescriptor(name = "Editor CSS", description = "Path to the desired CSS stylesheet for FCKEditor (relative from the contextpath)", required = false)
	protected String cssPath;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Module ID", description="The ID of the module to configure")
	protected Integer moduleID;

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name="Module type", description="The type of module to configure")
	protected ModuleType moduleType;

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Allowed settings", description="The settings to be configurable")
	protected List<String> allowedSettings;

	protected WeakReference<Entry<? extends ModuleDescriptor,? extends Module<?>>> moduleEntryReference;

	@Override
	protected void moduleConfigured() {

		this.moduleEntryReference = null;
	}

	@Override
	public synchronized ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {
		
		log.info("User " + user + " listing settings");

		if(moduleID == null || moduleType == null || CollectionUtils.isEmpty(allowedSettings)){

			Document doc = createDocument(req, uriParser, user);
			Element listSettingsElement = doc.createElement("ListSettings");
			doc.getFirstChild().appendChild(listSettingsElement);

			listSettingsElement.appendChild(doc.createElement("ModuleNotConfigured"));
			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}

		Entry<? extends ModuleDescriptor,? extends Module<?>> moduleEntry = WeakReferenceUtils.getReferenceValue(moduleEntryReference);

		if(moduleEntry == null){

			moduleEntry = getModule(moduleID, moduleType);

			if(moduleEntry != null){

				this.moduleEntryReference = new WeakReference<Entry<? extends ModuleDescriptor,? extends Module<?>>>(moduleEntry);

			}else{

				Document doc = createDocument(req, uriParser, user);
				Element listSettingsElement = doc.createElement("ListSettings");
				doc.getFirstChild().appendChild(listSettingsElement);

				listSettingsElement.appendChild(doc.createElement("ModuleNotFound"));
				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
			}
		}

		List<? extends SettingDescriptor> settings = moduleEntry.getValue().getSettings();

		if(CollectionUtils.isEmpty(settings)){

			Document doc = createDocument(req, uriParser, user);
			Element listSettingsElement = doc.createElement("ListSettings");
			doc.getFirstChild().appendChild(listSettingsElement);

			listSettingsElement.appendChild(doc.createElement("NoSettingsNotFound"));
			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}

		ArrayList<SettingDescriptor> filteredSettings = new ArrayList<SettingDescriptor>(settings.size());

		for(SettingDescriptor settingDescriptor : settings){

			if(allowedSettings.contains(settingDescriptor.getId())){

				filteredSettings.add(settingDescriptor);
			}
		}

		if(filteredSettings.isEmpty()){

			Document doc = createDocument(req, uriParser, user);
			Element listSettingsElement = doc.createElement("ListSettings");
			doc.getFirstChild().appendChild(listSettingsElement);

			listSettingsElement.appendChild(doc.createElement("ConfiguredSettingsNotFound"));
			return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
		}

		ArrayList<ValidationError> validationErrors = null;

		if(req.getMethod().equalsIgnoreCase("POST")){

			validationErrors = new ArrayList<ValidationError>();

			HashMap<String, List<String>> settingValues = SystemAdminModule.parseModuleSettings(filteredSettings, req, validationErrors);

			if(validationErrors.isEmpty()){

				log.info("User " + user + " updating settings for  " + moduleType.toString().toLowerCase() + " module " + moduleEntry.getKey());

				ModuleDescriptor moduleDescriptor = moduleEntry.getKey();

				moduleDescriptor.getMutableSettingHandler().replaceSettings(settingValues);

				for(SettingDescriptor settingDescriptor : filteredSettings){

					if(!settingValues.containsKey(settingDescriptor.getId())){

						moduleDescriptor.getMutableSettingHandler().removeSetting(settingDescriptor.getId());
					}
				}

				moduleDescriptor.saveSettings(systemInterface);

				Document doc = createDocument(req, uriParser, user);
				Element listSettingsElement = doc.createElement("SettingsSaved");
				doc.getFirstChild().appendChild(listSettingsElement);

				Module<?> module = moduleEntry.getValue();

				try {
					if(module instanceof BackgroundModule){

						SectionInterface sectionInterface = systemInterface.getSectionInterface(((BackgroundModuleDescriptor)moduleDescriptor).getSectionID());

						if(sectionInterface != null){

							sectionInterface.getBackgroundModuleCache().update((BackgroundModuleDescriptor) moduleDescriptor);
						}

					}else if(module instanceof FilterModule){

						sectionInterface.getSystemInterface().getFilterModuleCache().update((FilterModuleDescriptor) moduleDescriptor);

					}else if(module instanceof ForegroundModule){

						SectionInterface sectionInterface = systemInterface.getSectionInterface(((ForegroundModuleDescriptor)moduleDescriptor).getSectionID());

						if(sectionInterface != null){

							sectionInterface.getForegroundModuleCache().update((ForegroundModuleDescriptor) moduleDescriptor);
						}

					}else{

						throw new RuntimeException("Unknown module type " + module.getClass());
					}

				} catch (KeyNotCachedException e) {

					listSettingsElement.appendChild(doc.createElement("ModuleNotUpdated"));
				}

				return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
			}
		}

		Document doc = createDocument(req, uriParser, user);
		Element listSettingsElement = doc.createElement("ListSettings");
		doc.getFirstChild().appendChild(listSettingsElement);

		if(!CollectionUtils.isEmpty(validationErrors)){
			XMLUtils.append(doc, listSettingsElement, "ValidationErrors", validationErrors);
			listSettingsElement.appendChild(RequestUtils.getRequestParameters(req, doc));
		}

		for (SettingDescriptor settingDescriptor : settings) {

			SystemAdminModule.rewriteURLs(moduleEntry.getKey(), settingDescriptor, req);
		}

		XMLUtils.append(doc, listSettingsElement, "SettingDescriptors", filteredSettings);

		Element moduleDescriptorElement = moduleEntry.getKey().toXML(doc);
		listSettingsElement.appendChild(moduleDescriptorElement);

		moduleDescriptorElement.appendChild(moduleEntry.getKey().getMutableSettingHandler().toXML(doc));

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@WebPublic(alias="selectmodule")
	public synchronized ForegroundModuleResponse selectModule(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if(!AccessUtils.checkAccess(user, this)){
			throw new AccessDeniedException("Module administration denied");
		}

		log.info("User " + user + " listing system tree");

		Document doc = this.createDocument(req, uriParser, user);

		Element sectionsElement = doc.createElement("SectionTree");
		doc.getFirstChild().appendChild(sectionsElement);

		if(req.getParameter("modulenotfound") != null){

			XMLUtils.appendNewElement(doc, sectionsElement, "ModuleNotFound");

		}else if(req.getParameter("nosettingsfound") != null){

			XMLUtils.appendNewElement(doc, sectionsElement, "NoSettingsFound");
		}

		this.appendSection(sectionsElement, doc, systemInterface.getRootSection());

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	@WebPublic(alias="selectsettings")
	public synchronized ForegroundModuleResponse selectAllowedSettings(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		if(!AccessUtils.checkAccess(user, this)){
			throw new AccessDeniedException("Module administration denied");
		}

		log.info("User " + user + " selecting settings");

		Integer moduleID = NumberUtils.toInt(req.getParameter("moduleID"));
		ModuleType moduleType = EnumUtils.toEnum(ModuleType.class, req.getParameter("moduletype"));

		Entry<? extends ModuleDescriptor,? extends Module<?>> moduleEntry;

		if(moduleID == null || moduleType == null || (moduleEntry = getModule(moduleID,moduleType)) == null){

			redirectToMethod(req, res, "/selectmodule?modulenotfound=true");
			return null;
		}

		List<? extends SettingDescriptor> settings = moduleEntry.getValue().getSettings();

		if(CollectionUtils.isEmpty(settings)){

			redirectToMethod(req, res, "/selectmodule?nosettingsfound=true");
			return null;
		}

		if(req.getMethod().equalsIgnoreCase("POST")){

			String[] ids = req.getParameterValues("id");

			if(ids != null){

				this.moduleID = moduleID;
				this.moduleType = moduleType;
				this.allowedSettings = Arrays.asList(ids);
				this.moduleEntryReference = null;
				
				MutableSettingHandler mutableSettingHandler = moduleDescriptor.getMutableSettingHandler();

				mutableSettingHandler.setSetting("moduleID", moduleID);
				mutableSettingHandler.setSetting("moduleType", moduleType);
				mutableSettingHandler.setSetting("allowedSettings", allowedSettings);

				moduleDescriptor.saveSettings(systemInterface);
				
				redirectToDefaultMethod(req, res);
				return null;
			}
		}

		Document doc = this.createDocument(req, uriParser, user);

		Element selectSettingDescriptorsElement = doc.createElement("SelectSettingDescriptors");
		doc.getDocumentElement().appendChild(selectSettingDescriptorsElement);

		XMLUtils.append(doc, selectSettingDescriptorsElement, "SelectedSettings", "ID", allowedSettings);
		
		selectSettingDescriptorsElement.appendChild(moduleEntry.getKey().toXML(doc));
		XMLUtils.appendNewElement(doc, selectSettingDescriptorsElement, "ModuleType", moduleType);
		XMLUtils.append(doc, selectSettingDescriptorsElement, "SettingDescriptors", settings);

		return new SimpleForegroundModuleResponse(doc, moduleDescriptor.getName(), this.getDefaultBreadcrumb());
	}

	private Entry<? extends ModuleDescriptor,? extends Module<?>> getModule(Integer moduleID, ModuleType moduleType) {

		if(moduleType.equals(ModuleType.BACKGROUND)){

			return ModuleUtils.findAssignableBackgroundModule(BackgroundModule.class, moduleID, true, systemInterface.getRootSection());

		}else if(moduleType.equals(ModuleType.FILTER)){

			Entry<FilterModuleDescriptor, FilterModule> moduleEntry = sectionInterface.getSystemInterface().getFilterModuleCache().getEntry(moduleID);

			if(moduleEntry != null){

				return moduleEntry;
			}

			return null;

		}else if(moduleType.equals(ModuleType.FOREGROUND)){

			return ModuleUtils.findForegroundModule(ForegroundModule.class, true, moduleID, true, systemInterface.getRootSection());

		}else{

			throw new RuntimeException("Unknown module type " + moduleType);
		}
	}

	private void appendSection(Element parentSection, Document doc, SectionInterface sectionInterface) throws SQLException {

		SectionDescriptor sectionDescriptor = sectionInterface.getSectionDescriptor();

		Element sectionElement = sectionDescriptor.toXML(doc);
		parentSection.appendChild(sectionElement);

		ArrayList<ModuleInfoBean> moduleInfoList = new ArrayList<ModuleInfoBean>();

		// Loop thru all foreground modules in the cache
		ArrayList<ForegroundModuleDescriptor> cachedForegroundModuleList = sectionInterface.getForegroundModuleCache().getCachedModuleDescriptors();

		if (cachedForegroundModuleList != null) {

			for (ForegroundModuleDescriptor mb : cachedForegroundModuleList) {

				if (mb.getModuleID() != null) {

					ModuleInfoBean moduleInfoBean = new ModuleInfoBean();
					moduleInfoBean.setModuleBean(mb);
					moduleInfoBean.setCached(true);
					moduleInfoBean.setModuleType(ModuleType.FOREGROUND);
					moduleInfoList.add(moduleInfoBean);
				}
			}
		}

		// Loop thru all background modules in the cache
		ArrayList<BackgroundModuleDescriptor> cachedBackgroundModuleList = sectionInterface.getBackgroundModuleCache().getCachedModuleDescriptors();

		if (cachedBackgroundModuleList != null) {

			for (BackgroundModuleDescriptor mb : cachedBackgroundModuleList) {

				if (mb.getModuleID() != null) {

					ModuleInfoBean moduleInfoBean = new ModuleInfoBean();
					moduleInfoBean.setModuleBean(mb);
					moduleInfoBean.setCached(true);
					moduleInfoBean.setModuleType(ModuleType.BACKGROUND);
					moduleInfoList.add(moduleInfoBean);
				}
			}
		}

		//Check if this is the root section
		if (sectionDescriptor.getParentSectionID() == null) {

			// Loop thru all filter modules in the cache
			List<FilterModuleDescriptor> cachedFilterModuleList = systemInterface.getFilterModuleCache().getCachedModuleDescriptors();

			if (cachedFilterModuleList != null) {

				for (FilterModuleDescriptor mb : cachedFilterModuleList) {

					if (mb.getModuleID() != null) {

						ModuleInfoBean moduleInfoBean = new ModuleInfoBean();

						moduleInfoBean.setModuleBean(mb);
						moduleInfoBean.setCached(true);
						moduleInfoBean.setModuleType(ModuleType.FILTER);
						moduleInfoList.add(moduleInfoBean);
					}
				}
			}
		}

		Collections.sort(moduleInfoList, MODULE_COMPARATOR);

		XMLUtils.append(doc, sectionElement, "Modules", moduleInfoList);

		Collection<Section> subSections = sectionInterface.getSectionCache().getSectionMap().values();

		if (!subSections.isEmpty()) {

			Element subSectionsElement = doc.createElement("Subsections");
			sectionElement.appendChild(subSectionsElement);

			for (Section subSection : subSections) {
				this.appendSection(subSectionsElement, doc, subSection);
			}
		}
	}

	public Document createDocument(HttpServletRequest req, URIParser uriParser, User user) {

		Document doc = XMLUtils.createDomDocument();
		Element documentElement = doc.createElement("Document");
		doc.appendChild(documentElement);
		documentElement.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		documentElement.appendChild(this.moduleDescriptor.toXML(doc));

		XMLUtils.appendNewElement(doc, documentElement, "cssPath", cssPath);

		if(AccessUtils.checkAccess(user, this)){
			documentElement.appendChild(doc.createElement("IsAdmin"));
		}

		XMLUtils.appendNewElement(doc, documentElement, "moduleID", this.moduleID);
		XMLUtils.appendNewElement(doc, documentElement, "ModuleType", this.moduleType);

		if(user != null){
			documentElement.appendChild(user.toXML(doc));
		}

		return doc;
	}

	@Override
	public boolean allowsAdminAccess() {

		return false;
	}

	@Override
	public boolean allowsUserAccess() {

		return false;
	}

	@Override
	public boolean allowsAnonymousAccess() {

		return false;
	}

	@Override
	public Collection<Integer> getAllowedGroupIDs() {

		return adminGroupIDs;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {

		return adminUserIDs;
	}
}
