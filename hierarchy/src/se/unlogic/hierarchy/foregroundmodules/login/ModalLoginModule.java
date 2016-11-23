package se.unlogic.hierarchy.foregroundmodules.login;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.backgroundmodules.authentication.ModalLoginBackgroundModule;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.EnumDropDownSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextAreaSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.PathType;
import se.unlogic.hierarchy.core.handlers.SimpleSettingHandler;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;

public class ModalLoginModule extends UserProviderLoginModule {

	@ModuleSetting
	@TextAreaSettingDescriptor(name = "Modal background module alias", description = "Aliases for modal background module", required = true)
	protected String modalBgModuleAliases = "*";

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Modal background module name", description = "The name of the modal background module", required = true)
	protected String modalBgModuleName = "Login module (background)";

	@ModuleSetting
	@EnumDropDownSettingDescriptor(name = "Modal background module xslt path type", description = "The pathtype of the xslt", required = true)
	protected PathType modalBgModuleXSLType = PathType.Classpath;

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "Modal background module xsl path", description = "The xsl path of the modal background module", required = true)
	protected String modalBgModuleXSLPath;

	@ModuleSetting
	@TextAreaSettingDescriptor(name = "Modal background module slots", description = "Slots for modal background module", required = true)
	protected String modalBgModuleSlots;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="Use modal registration", description="Control if the registration form should be displayed as a modal form or not")
	protected boolean useModalRegistration = true;
	
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
	public SimpleForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception {

		SimpleForegroundModuleResponse moduleResponse = super.processRequest(req, res, user, uriParser);

		if (res.isCommitted()) {

			return moduleResponse;
		}

		if (Boolean.valueOf(req.getParameter("onlymodulehtml"))) {

			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			if (moduleResponse != null) {
				moduleResponse.excludeSystemTransformation(true);
			}

		}

		return moduleResponse;

	}

	private void createModalBackgroundModule() throws Exception {

		if (!StringUtils.isEmpty(this.modalBgModuleXSLPath) && !StringUtils.isEmpty(this.modalBgModuleSlots)) {

			boolean update = false;
			SimpleBackgroundModuleDescriptor descriptor = null;

			if (modalBgModule != null && (descriptor = modalBgModule.get()) != null) {
				update = true;
			} else {
				descriptor = new SimpleBackgroundModuleDescriptor();
			}

			if (!StringUtils.isEmpty(this.modalBgModuleAliases)) {
				String[] aliases = this.modalBgModuleAliases.split("\\n");
				descriptor.setAliases(Arrays.asList(aliases));
			}

			String[] slots = this.modalBgModuleSlots.split("\\n");
			descriptor.setSlots(Arrays.asList(slots));

			descriptor.setXslPathType(this.modalBgModuleXSLType);
			descriptor.setXslPath(this.modalBgModuleXSLPath);
			descriptor.setName(this.modalBgModuleName);
			descriptor.setClassname(ModalLoginBackgroundModule.class.getName());
			descriptor.setAdminAccess(false);
			descriptor.setUserAccess(false);
			descriptor.setAnonymousAccess(true);

			Map<String, List<String>> settings = this.moduleDescriptor.getMutableSettingHandler().getMap();

			settings.put("loginModuleURI", Collections.singletonList(this.sectionInterface.getSectionDescriptor().getFullAlias() + "/" + moduleDescriptor.getAlias()));
			settings.put("useModalRegistration", Collections.singletonList(Boolean.toString(useModalRegistration)));
			
			descriptor.setMutableSettingHandler(new SimpleSettingHandler(settings));
			descriptor.setEnabled(true);
			descriptor.setSectionID(sectionInterface.getSectionDescriptor().getSectionID());
			descriptor.setStaticContentPackage("staticcontent");
			descriptor.setDataSourceID(this.moduleDescriptor.getDataSourceID());

			if (update) {
				sectionInterface.getBackgroundModuleCache().update(descriptor);
			} else {
				sectionInterface.getBackgroundModuleCache().cache(descriptor);
				modalBgModule = new WeakReference<SimpleBackgroundModuleDescriptor>(descriptor);
			}

		}

	}

	@Override
	public void unload() throws Exception {

		super.unload();
		
		SimpleBackgroundModuleDescriptor bgModuleDescriptor = null;

		if (modalBgModule != null && (bgModuleDescriptor = modalBgModule.get()) != null) {

			try {
				sectionInterface.getBackgroundModuleCache().unload(bgModuleDescriptor);
			} catch (Exception e) {
				log.error("Error unloding background module " + bgModuleDescriptor + " while unloading module " + moduleDescriptor, e);
			}
		}
	}

	@Override
	public SimpleForegroundModuleResponse sendRedirect(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, User loginUser) throws Exception {

		String redirectURI = req.getParameter("requesteduri");

		// Extra check to prevent redirects to other sites
		if (redirectURI != null && redirectURI.startsWith(req.getContextPath())) {

			res.sendRedirect(redirectURI);

			return null;
		}

		return super.sendRedirect(req, res, uriParser, loginUser);
	}

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse res, URIParser uriParser, String redirectURI) throws Throwable {

		if(redirectURI == null){

			super.handleRequest(req, res, uriParser, redirectURI);
			return;
		}

		if (this.modalBgModule != null && this.modalBgModule.get() != null) {

			String referer = req.getHeader("referer");

			if(referer == null || !referer.startsWith(RequestUtils.getFullContextPathURL(req))){

				res.sendRedirect(this.getModuleURI(req) + "?redirect=" + URLEncoder.encode(redirectURI,"ISO-8859-1") + "#clear");

			}else if(req.getParameter("requesteduri") != null){

				String redirect = req.getParameter("requesteduri");

				res.sendRedirect(this.getModuleURI(req) + "?redirect=" + URLEncoder.encode(redirect,"ISO-8859-1") + "#clear");

			} else {

				res.sendRedirect(referer + "?requesteduri=" + URLEncoder.encode(redirectURI,"ISO-8859-1") + "#" + this.moduleDescriptor.getAlias());
			}

		} else {

			super.handleRequest(req, res, uriParser, redirectURI);
		}
	}
}
