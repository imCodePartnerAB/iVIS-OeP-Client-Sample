package se.unlogic.hierarchy.foregroundmodules.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.WebPublic;
import se.unlogic.hierarchy.core.beans.SimpleForegroundModuleResponse;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.EventSource;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.interfaces.EventListener;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleResponse;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.foregroundmodules.AnnotatedForegroundModule;
import se.unlogic.webutils.http.URIParser;


public class EventTestModule extends AnnotatedForegroundModule implements EventListener<TestEvent>{

	private TestEvent lastReceivedEvent;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		systemInterface.getEventHandler().addEventListener(EventTestModule.class, TestEvent.class, this);
	}

	@Override
	public void unload() throws Exception {

		systemInterface.getEventHandler().removeEventListener(EventTestModule.class, TestEvent.class, this);

		super.unload();
	}

	@Override
	public ForegroundModuleResponse defaultMethod(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>Last received event</h1>");

		stringBuilder.append("<p>" + lastReceivedEvent + "</p>");

		stringBuilder.append("</div>");

		return new SimpleForegroundModuleResponse(stringBuilder.toString(),getDefaultBreadcrumb());
	}

	@WebPublic(alias="send")
	public ForegroundModuleResponse sendEvent(HttpServletRequest req, HttpServletResponse res, User user, URIParser uriParser) throws Throwable {

		TestEvent event = new TestEvent();

		event.setSent(System.currentTimeMillis());
		event.setValue(req.getParameter("value"));

		systemInterface.getEventHandler().sendEvent(EventTestModule.class, event, this, EventTarget.ALL, EventSource.LOCAL);

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("<div class=\"contentitem\">");
		stringBuilder.append("<h1>Event successfully sent</h1>");
		stringBuilder.append("</div>");

		return new SimpleForegroundModuleResponse(stringBuilder.toString(),getDefaultBreadcrumb());
	}

	@Override
	public void processEvent(TestEvent event, EventSource source) {

		log.info("Event received:" + event);

		this.lastReceivedEvent = event;
	}

	@Override
	public int getPriority() {

		return 0;
	}
}
