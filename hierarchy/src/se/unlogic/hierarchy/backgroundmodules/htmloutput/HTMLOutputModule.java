package se.unlogic.hierarchy.backgroundmodules.htmloutput;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.GroupMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.HTMLEditorSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.UserMultiListSettingDescriptor;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.SystemStatus;
import se.unlogic.hierarchy.core.interfaces.AccessInterface;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.ForegroundModule;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleCacheListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SystemStartupListener;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.FCKUtils;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.hierarchy.foregroundmodules.htmloutput.HTMLOutputAdminModule;
import se.unlogic.standardutils.collections.KeyAlreadyCachedException;
import se.unlogic.standardutils.collections.KeyNotCachedException;
import se.unlogic.standardutils.references.WeakReferenceUtils;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.RequestUtils;
import se.unlogic.webutils.http.URIParser;
import se.unlogic.webutils.url.URLRewriter;

public class HTMLOutputModule extends AnnotatedBackgroundModule implements AccessInterface, SystemStartupListener, ForegroundModuleCacheListener {

	private static final Pattern PERCENT_PATTERN = Pattern.compile("%(?![0-9a-fA-F]{2})");
	public static final String RELATIVE_PATH_MARKER = "/@";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="CSS class",description="The CSS class for this background module",required=false)
	protected String cssClass = "htmloutputmodule";

	@ModuleSetting(allowsNull=true)
	@TextFieldSettingDescriptor(name="Admin CSS class",description="The body CSS class used for html output admin module",required=false)
	protected String adminCssClass;
	
	@ModuleSetting(allowsNull=true)
	@HTMLEditorSettingDescriptor(name="HTML",description="The HTML output for this background module",required=false)
	protected String html;

	@ModuleSetting
	@CheckboxSettingDescriptor(name="HTML is required", description="Controls whether HTML is required or not")
	protected boolean htmlRequired = true;

	@ModuleSetting
	@TextFieldSettingDescriptor(name="File store module alias",description="Full alias to the file store module",required=false)
	protected String fileStoreModuleAlias = null;

	@ModuleSetting(allowsNull = true)
	@UserMultiListSettingDescriptor(name = "Admin users", description = "Users allowed to administrate the content in this module", required = false)
	protected List<Integer> adminUsers;

	@ModuleSetting(allowsNull = true)
	@GroupMultiListSettingDescriptor(name = "Admin groups", description = "Groups allowed to administrate the content in this module", required = false)
	protected List<Integer> adminGroups;

	protected String unescapedHTML;

	private WeakReference<HTMLOutputAdminModule> htmlOutputAdminModuleRef;

	@Override
	public void init(BackgroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if(sectionInterface.getSystemInterface().getSystemStatus() == SystemStatus.STARTING){

			this.sectionInterface.getSystemInterface().addStartupListener(this);

		}else{

			systemStarted();
		}
	}

	@Override
	public void unload() throws Exception {

		this.sectionInterface.getSystemInterface().removeForegroundModuleCacheListener(this);
	}

	@Override
	public void systemStarted() {

		Entry<ForegroundModuleDescriptor, HTMLOutputAdminModule> moduleEntry = ModuleUtils.findForegroundModule(HTMLOutputAdminModule.class, true, null, true, this.systemInterface.getRootSection());

		if(moduleEntry != null){

			this.htmlOutputAdminModuleRef = new WeakReference<HTMLOutputAdminModule>(moduleEntry.getValue());
		}

		this.sectionInterface.getSystemInterface().addForegroundModuleCacheListener(this);
	}

	@Override
	public void moduleCached(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) throws KeyAlreadyCachedException {

		if(moduleInstance instanceof HTMLOutputAdminModule){
			this.htmlOutputAdminModuleRef = new WeakReference<HTMLOutputAdminModule>((HTMLOutputAdminModule)moduleInstance);
		}
	}

	@Override
	public void moduleUpdated(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) throws KeyNotCachedException {}

	@Override
	public void moduleUnloaded(ForegroundModuleDescriptor moduleDescriptor, ForegroundModule moduleInstance) throws KeyNotCachedException {

		if(moduleInstance instanceof HTMLOutputAdminModule){
			this.htmlOutputAdminModuleRef = null;
		}
	}

	@Override
	public BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		if(!htmlRequired && StringUtils.isEmpty(html)) {
			return null;
		}

		Document doc = XMLUtils.createDomDocument();
		Element document = doc.createElement("Document");
		doc.appendChild(document);

		document.appendChild(RequestUtils.getRequestInfoAsXML(doc, req, uriParser));
		document.appendChild(this.moduleDescriptor.toXML(doc));

		XMLUtils.appendNewElement(doc, document, "cssClass", cssClass);

		BackgroundModuleResponse moduleResponse = null;

		if(!StringUtils.isEmpty(html)) {

			String patchedHTML = html;

			if(this.fileStoreModuleAlias != null){

				String absoluteFileURL = req.getContextPath() + fileStoreModuleAlias + "/" + this.moduleDescriptor.getModuleID();

				patchedHTML = FCKUtils.setAbsoluteFileUrls(patchedHTML, absoluteFileURL);
			}

			patchedHTML = URLRewriter.setAbsoluteLinkUrls(patchedHTML, req);

			XMLUtils.appendNewElement(doc, document, "HTML", patchedHTML);
			moduleResponse = new SimpleBackgroundModuleResponse(doc);
		}

		HTMLOutputAdminModule systemAdminModule = WeakReferenceUtils.getReferenceValue(this.htmlOutputAdminModuleRef);

		if(systemAdminModule != null && AccessUtils.checkAccess(user,this) && AccessUtils.checkAccess(user,systemAdminModule.getModuleDescriptor())) {

			this.htmlOutputAdminModuleRef = new WeakReference<HTMLOutputAdminModule>(systemAdminModule);

			XMLUtils.appendNewElement(doc, document, "settingsURL", systemAdminModule.getFullAlias() + "/settings/" + this.moduleDescriptor.getModuleID());

			if(moduleResponse == null) {
				moduleResponse = new SimpleBackgroundModuleResponse(doc);
			}

		}

		return moduleResponse;
	}

	public void setHtml(String html) {
		this.html = html;

		this.unescapedHTML = null;
	}

	public boolean htmlIsRequired() {
		return htmlRequired;
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
		return this.adminGroups;
	}

	@Override
	public Collection<Integer> getAllowedUserIDs() {
		return this.adminUsers;
	}

	public String getUnescapedHTML() {

		if(this.unescapedHTML == null && this.html != null){

			unescapedHTML = PERCENT_PATTERN.matcher(html).replaceAll("%25");
			try {
				unescapedHTML = URLDecoder.decode(unescapedHTML, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		return unescapedHTML;
	}


	public String getHtml() {

		return html;
	}
	
	public String getCssClass() {
		
		return cssClass;
	}
	
	public String getAdminCssClass() {
		
		return adminCssClass;
	}
}
