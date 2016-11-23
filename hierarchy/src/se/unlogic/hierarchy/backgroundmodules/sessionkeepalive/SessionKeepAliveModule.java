package se.unlogic.hierarchy.backgroundmodules.sessionkeepalive;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import se.unlogic.hierarchy.backgroundmodules.AnnotatedBackgroundModule;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.ScriptTag;
import se.unlogic.hierarchy.core.beans.SimpleBackgroundModuleResponse;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.sessionkeepalive.SessionKeepAliveConnectorModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.webutils.http.URIParser;


public class SessionKeepAliveModule extends AnnotatedBackgroundModule {

	protected static final ScriptTag JQUERY_SCRIPT_TAG = new ScriptTag("/static/global/jquery/jquery.js");

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Connector module name", description="Name to be used for the automatically added session keep alive connector module",required=true)
	protected String connectorModuleName = "Session Keep alive connector";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Invitation module alias", description="Alias to be used for the automatically added session keep alive connector module",required=true)
	protected String connectorModuleAlias = "keepaliveconnector";

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Poll frequency", description="Controls at which interval the clients contact the connector (speicified in seconds)",required=true,formatValidator=PositiveStringIntegerValidator.class)
	protected int keepAlivePollFrequency = 60;

	protected SimpleForegroundModuleDescriptor connectorModuleDescriptor;

	@Override
	public void init(BackgroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		checkConnectorModule();
	}

	@Override
	public void update(BackgroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		checkConnectorModule();
	}

	@Override
	public void unload() throws Exception {

		unloadConnectorModule();

		super.unload();
	}

	@Override
	public BackgroundModuleResponse processBackgroundRequest(HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		if(connectorModuleDescriptor != null && scripts != null){

			//Return empty module response containing a script tag pointing to the foregroundmodule and a scripttag for jquery
			SimpleBackgroundModuleResponse moduleResponse = new SimpleBackgroundModuleResponse("");

			return moduleResponse;
		}

		return null;
	}

	protected synchronized void checkConnectorModule() {

		if(StringUtils.isEmpty(connectorModuleAlias) || StringUtils.isEmpty(connectorModuleName)){

			if(connectorModuleDescriptor == null){

				log.warn("Module not properly configured, refusing to create instance of connector module");

			}else{

				log.warn("Module not properly configured, stopping current instance of connector module");

				unloadConnectorModule();
			}

		}else{

			if(connectorModuleDescriptor == null){

				SimpleForegroundModuleDescriptor connectorModuleDescriptor = new SimpleForegroundModuleDescriptor();
				connectorModuleDescriptor.setSectionID(systemInterface.getRootSection().getSectionDescriptor().getSectionID());
				connectorModuleDescriptor.setClassname(SessionKeepAliveConnectorModule.class.getName());
				connectorModuleDescriptor.setAdminAccess(true);
				connectorModuleDescriptor.setUserAccess(true);
				connectorModuleDescriptor.setAnonymousAccess(true);
				connectorModuleDescriptor.setVisibleInMenu(false);
				connectorModuleDescriptor.setDataSourceID(moduleDescriptor.getDataSourceID());
				connectorModuleDescriptor.setAlias(connectorModuleAlias);
				connectorModuleDescriptor.setName(connectorModuleName);
				connectorModuleDescriptor.setDescription(connectorModuleName);
				connectorModuleDescriptor.setMutableSettingHandler(moduleDescriptor.getMutableSettingHandler());

				try{
					systemInterface.getRootSection().getForegroundModuleCache().cache(connectorModuleDescriptor);

					generateScripts();

					this.connectorModuleDescriptor = connectorModuleDescriptor;
				}catch(Exception e){
					log.error("Error caching connector module", e);
				}

			}else{

				log.info("Updating connector module");

				connectorModuleDescriptor.setDataSourceID(moduleDescriptor.getDataSourceID());
				connectorModuleDescriptor.setAlias(connectorModuleAlias);
				connectorModuleDescriptor.setName(connectorModuleName);
				connectorModuleDescriptor.setDescription(connectorModuleName);
				connectorModuleDescriptor.setMutableSettingHandler(moduleDescriptor.getMutableSettingHandler());

				try{
					systemInterface.getRootSection().getForegroundModuleCache().update(connectorModuleDescriptor);

					generateScripts();

				}catch(Exception e){
					log.error("Error updating connector module", e);
				}
			}
		}
	}

	private void generateScripts() {

		ArrayList<ScriptTag> scripts = new ArrayList<ScriptTag>(2);

		scripts.add(JQUERY_SCRIPT_TAG);
		scripts.add(new ScriptTag("/" + connectorModuleAlias + "/keepalive.js"));

		this.scripts = scripts;
	}

	protected synchronized void unloadConnectorModule() {

		try{
			if(systemInterface.getRootSection().getForegroundModuleCache().isCached(connectorModuleDescriptor)){
				systemInterface.getRootSection().getForegroundModuleCache().unload(connectorModuleDescriptor);
			}
			this.connectorModuleDescriptor = null;
		}catch(Exception e){
			log.error("Error unloading connector module", e);
		}
	}
}
