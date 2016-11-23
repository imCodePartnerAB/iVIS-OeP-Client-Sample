package se.unlogic.hierarchy.foregroundmodules.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.SimpleForegroundModule;
import se.unlogic.standardutils.threads.ThreadUtils;
import se.unlogic.standardutils.time.MillisecondTimeUnits;
import se.unlogic.webutils.http.URIParser;


public class SlowModule extends SimpleForegroundModule {

	@Override
	public ForegroundModuleResponse processRequest(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Exception, Throwable {

		ThreadUtils.sleep(15 * MillisecondTimeUnits.SECOND);

		return new SimpleForegroundModuleResponse("That was slow wasn't it?");
	}

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		ThreadUtils.sleep(15 * MillisecondTimeUnits.SECOND);
	}

	@Override
	public void update(ForegroundModuleDescriptor moduleDescriptor, DataSource dataSource) throws Exception {

		super.update(moduleDescriptor, dataSource);

		ThreadUtils.sleep(15 * MillisecondTimeUnits.SECOND);
	}

	@Override
	public void unload() throws Exception {

		ThreadUtils.sleep(15 * MillisecondTimeUnits.SECOND);

		super.unload();
	}

}
