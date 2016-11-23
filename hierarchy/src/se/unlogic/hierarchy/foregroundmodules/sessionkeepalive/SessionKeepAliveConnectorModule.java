package se.unlogic.hierarchy.foregroundmodules.sessionkeepalive;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.annotations.TextFieldSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.standardutils.string.StringUtils;
import se.unlogic.standardutils.validation.PositiveStringIntegerValidator;
import se.unlogic.webutils.http.URIParser;


public class SessionKeepAliveConnectorModule extends AnnotatedForegroundModule {

	@ModuleSetting
	@TextFieldSettingDescriptor(name="Poll frequency", description="Controls at which interval the clients contact the connector (speicified in seconds)",required=true,formatValidator=PositiveStringIntegerValidator.class)
	protected int keepAlivePollFrequency = 60;

	private String cachedJavaScript;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		this.cachedJavaScript = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("keepalive.js"));
	}

	@Override
	public void update(ForegroundModuleDescriptor descriptor, DataSource dataSource) throws Exception {

		super.update(descriptor, dataSource);
		
		this.cachedJavaScript = StringUtils.readStreamAsString(this.getClass().getResourceAsStream("keepalive.js"));
	}	
	
	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		log.info("Keep alive request from user " + user + " from address " + req.getRemoteAddr());

		res.setContentType("text/html");
		res.setCharacterEncoding("ISO-8859-1");
		res.getWriter().write(user != null ? "1" : "0");
		res.getWriter().flush();

		return null;
	}

	@WebPublic(alias="keepalive.js")
	public ForegroundModuleResponse getScript(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		log.debug("User " + user + " requesting session keep alive script from address " + req.getRemoteAddr());

		String script = cachedJavaScript.replace("fullalias", req.getContextPath() + this.getFullAlias());
		script = script.replace("pollFreq", String.valueOf(keepAlivePollFrequency));

		res.setHeader("Content-Disposition", "inline; filename=\"keepalive.js\"");

		res.setContentType("application/javascript");
		res.getWriter().write(script);
		res.getWriter().flush();

		return null;
	}
}
