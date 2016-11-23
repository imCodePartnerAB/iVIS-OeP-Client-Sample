package se.unlogic.hierarchy.foregroundmodules.newpassword;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.backgroundmodules.authentication.ModalNewPasswordBackgroundModule;
import se.unlogic.hierarchy.core.annotations.DropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.handlers.SimpleSettingHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public class ModalNewPasswordModule extends NewPasswordModule {

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Modal background module alias", description="Aliases for modal background modle", required=true)
	protected String modalBgModuleAliases = "*";
	
	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Modal background module name", description = "The name of the modal background module", required = true)
	protected String modalBgModuleName = "New password module (background)";
	
	@ModuleSetting
	@DropDownSettingDescriptor(name="Modal background module xslt path type",description="The pathtype of the xslt",required=true,values={"Classpath","Filesystem","RealtiveFilesystem"},valueDescriptions={"Classpath","Filesystem","RealtiveFilesystem"})
	protected String modalBgModuleXSLType = "Classpath";
	
	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Modal background module xsl path", description = "The xsl path of the modal background module", required = true)
	protected String modalBgModuleXSLPath;
	
	@ModuleSetting
	@TextAreaSettingDescriptor(name="Modal background module slots", description="Slots for modal background module", required=true)
	protected String modalBgModuleSlots;
	
	protected WeakReference<SimpleBackgroundModuleDescriptor> modalBgModule = null;
	
	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {
		
		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.createModalBackgroundModule();
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {
		
		super.update(moduleDescriptor, dataSource);
		
		this.createModalBackgroundModule();
	}
	
	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) {
		
		ForegroundModuleResponse moduleResponse = super.defaultMethod(req, res, user, uriParser);
		
		if(Boolean.valueOf(req.getParameter("onlymodulehtml")) && moduleResponse != null) {
			moduleResponse.excludeSystemTransformation(true);
		}
			
		return moduleResponse;
	}

	private void createModalBackgroundModule() throws Exception {
		
		if(!StringUtils.isEmpty(this.modalBgModuleXSLPath) && !StringUtils.isEmpty(this.modalBgModuleSlots)) {
		
			boolean update = false;
			SimpleBackgroundModuleDescriptor descriptor = null;
			
			if(modalBgModule != null && (descriptor = modalBgModule.get()) != null) { 
				update = true;
			} else {
				descriptor = new SimpleBackgroundModuleDescriptor();
			}
	
			if(!StringUtils.isEmpty(this.modalBgModuleAliases)) {
				String[] aliases = this.modalBgModuleAliases.split("\\n");
				descriptor.setAliases(Arrays.asList(aliases));
			}
			
			String[] slots = this.modalBgModuleSlots.split("\\n");
			descriptor.setSlots(Arrays.asList(slots));
	
			descriptor.setXslPathType(PathType.valueOf(this.modalBgModuleXSLType));
			descriptor.setXslPath(this.modalBgModuleXSLPath);
			descriptor.setName(this.modalBgModuleName);
			descriptor.setClassname(ModalNewPasswordBackgroundModule.class.getName());
			descriptor.setAdminAccess(false);
			descriptor.setUserAccess(false);
			descriptor.setAnonymousAccess(true);

			Map<String, List<String>> settings = this.moduleDescriptor.getMutableSettingHandler().getMap();
	
			settings.put("newPasswordModuleURI", Collections.singletonList(this.sectionInterface.getSectionDescriptor().getFullAlias() + "/" + moduleDescriptor.getAlias()));
			
			descriptor.setMutableSettingHandler(new SimpleSettingHandler(settings));
			descriptor.setEnabled(true);
			descriptor.setSectionID(sectionInterface.getSectionDescriptor().getSectionID());
			descriptor.setStaticContentPackage("staticcontent");
			descriptor.setDataSourceID(this.moduleDescriptor.getDataSourceID());
	
			if(update) {
				sectionInterface.getBackgroundModuleCache().update(descriptor);
			} else {
				sectionInterface.getBackgroundModuleCache().cache(descriptor);
				modalBgModule = new WeakReference<SimpleBackgroundModuleDescriptor>(descriptor);
			}
		
		}
		
	}
	
	@Override
	public void unload() {
		
		SimpleBackgroundModuleDescriptor bgModuleDescriptor = null;
		
		if(modalBgModule != null && (bgModuleDescriptor = modalBgModule.get()) != null) {
			
			try {
				sectionInterface.getBackgroundModuleCache().unload(bgModuleDescriptor);
			} catch (Exception e) {
				log.error("Error unloding background module " + bgModuleDescriptor + " while unloading module " + moduleDescriptor, e);
			}
			
		}
	}
	
}
