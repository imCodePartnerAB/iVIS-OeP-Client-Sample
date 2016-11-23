package se.unlogic.hierarchy.foregroundmodules.registration;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.backgroundmodules.authentication.ModalRegistrationBackgroundModule;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.handlers.SimpleSettingHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.URIParser;

public abstract class BaseModalRegistrationModule<UserType extends MutableUser> extends UserProviderRegistrationModule<UserType> {

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Modal background module alias", description="Aliases for modal background module", required=true)
	protected String modalBgModuleAliases = "*";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Modal background module name", description = "The name of the modal background module", required = true)
	protected String modalBgModuleName = "Registration module (background)";

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name="Modal background module xslt path type", description="The pathtype of the xslt",required=true)
	protected PathType modalBgModuleXSLType = PathType.Classpath;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Modal background module xsl path", description = "The xsl path of the modal background module", required = true)
	protected String modalBgModuleXSLPath;

	@ModuleSetting
	@TextAreaSettingDescriptor(name="Modal background module slots", description="Slots for modal background module", required=true)
	protected String modalBgModuleSlots;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Auto login", description="Controls wheter to login created user automatically or not")
	protected boolean autoLogin;

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
	public SimpleForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		SimpleForegroundModuleResponse moduleResponse = super.defaultMethod(req, res, user, uriParser);

		if(Boolean.valueOf(req.getParameter("onlymodulehtml")) && moduleResponse != null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			moduleResponse.excludeSystemTransformation(true);
		}

		return moduleResponse;
	}

	@Override
	protected SimpleForegroundModuleResponse accountActivated(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, UserType newUser, boolean userChanged) throws Exception {

		if(this.autoLogin) {
			
			if(systemInterface.getLoginHandler().loginUser(req, uriParser, newUser)){
				
				if(Boolean.valueOf(req.getParameter("onlymodulehtml"))){
				
					res.setContentType("text/html");
					res.setCharacterEncoding("ISO-8859-1");
					res.getWriter().write("1");
					res.getWriter().flush();
				}

				if(res.isCommitted()){

					return null;
				}

				userChanged = true;
				
			}else{
				
				log.warn("Unable to auto login user " + newUser + " via login handler");
			}
		}

		return super.accountActivated(req, res, uriParser, newUser, userChanged);

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

			descriptor.setXslPathType(this.modalBgModuleXSLType);
			descriptor.setXslPath(this.modalBgModuleXSLPath);
			descriptor.setName(this.modalBgModuleName);
			descriptor.setClassname(ModalRegistrationBackgroundModule.class.getName());
			descriptor.setAdminAccess(false);
			descriptor.setUserAccess(false);
			descriptor.setAnonymousAccess(true);

			Map<String, List<String>> settings = this.moduleDescriptor.getMutableSettingHandler().getMap();

			settings.put("registrationModuleURI", Collections.singletonList(this.sectionInterface.getSectionDescriptor().getFullAlias() + "/" + moduleDescriptor.getAlias()));

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
	public void unload() throws Exception {

		SimpleBackgroundModuleDescriptor bgModuleDescriptor = null;

		if(modalBgModule != null && (bgModuleDescriptor = modalBgModule.get()) != null) {

			try {
				sectionInterface.getBackgroundModuleCache().unload(bgModuleDescriptor);
			} catch (Exception e) {
				log.error("Error unloding background module " + bgModuleDescriptor + " while unloading module " + moduleDescriptor, e);
			}

		}

		super.unload();
	}

}
