package se.unlogic.hierarchy.foregroundmodules.htmloutput;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.unlogic.hierarchy.backgroundmodules.htmloutput.HTMLOutputModule;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.AccessDeniedException;
import se.unlogic.hierarchy.core.exceptions.URINotFoundException;
import se.unlogic.hierarchy.core.interfaces.BackgroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.MutableSettingHandler;
import se.unlogic.hierarchy.core.utils.AccessUtils;
import se.unlogic.hierarchy.core.utils.FCKConnector;
import se.unlogic.hierarchy.core.utils.ModuleUtils;
import se.unlogic.hierarchy.core.utils.SimpleFileAccessValidator;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.webutils.http.URIParser;


public class HTMLOutputFileStoreModule extends AnnotatedForegroundModule{

	@ModuleSetting
	@TextFieldSettingDescriptor(name = "File store", description = "Directory where uploaded files are stored", required = true)
	protected String fileStore;

	private FCKConnector connector;

	@Override
	protected void parseSettings(MutableSettingHandler mutableSettingHandler) throws Exception {

		super.parseSettings(mutableSettingHandler);

		this.connector = new FCKConnector(fileStore);
	}

	@Override
	protected ForegroundModuleResponse processForegroundRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {


		Integer moduleID;

		if(uriParser.size() < 3 || (moduleID = NumberUtils.toInt(uriParser.get(1))) == null){

			throw new URINotFoundException(uriParser);
		}

		Entry<BackgroundModuleDescriptor, HTMLOutputModule> moduleEntry = ModuleUtils.findBackgroundModule(HTMLOutputModule.class, true, moduleID, true, systemInterface.getRootSection());

		if(moduleEntry == null){

			throw new URINotFoundException(uriParser);

		}else if(!AccessUtils.checkRecursiveModuleAccess(user, moduleEntry.getKey(), systemInterface)){

			throw new AccessDeniedException("Access to background module " + moduleEntry.getKey() + " denied!");
		}

		this.connector.processFileRequest(req, res, user, uriParser, moduleDescriptor, sectionInterface, 2, new SimpleFileAccessValidator(HTMLOutputModule.RELATIVE_PATH_MARKER,moduleEntry.getValue().getUnescapedHTML()));

		return null;
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		return null;
	}
}
