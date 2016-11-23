package se.unlogic.hierarchy.core.utils.crud;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.hierarchy.core.enums.EventTarget;
import se.unlogic.hierarchy.core.events.CRUDEvent;
import se.unlogic.hierarchy.core.interfaces.EventHandler;
import se.unlogic.webutils.http.URIParser;


public class CRUDEventFilter<T extends Serializable> implements BeanFilter<T> {

	private final Class<?> channel;
	private final EventHandler eventHandler;
	
	public CRUDEventFilter(Class<?> channel, EventHandler eventHandler) {

		super();
		this.channel = channel;
		this.eventHandler = eventHandler;
	}

	@Override
	public void beanLoaded(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void beansLoaded(List<? extends T> beans, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void addBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void beanAdded(T bean, HttpServletRequest req, URIParser uriParser, User user) {

		eventHandler.sendEvent(channel, new CRUDEvent<T>(CRUDAction.ADD, bean), EventTarget.ALL);
	}

	@Override
	public void updateBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void beanUpdated(T bean, HttpServletRequest req, URIParser uriParser, User user) {

		eventHandler.sendEvent(channel, new CRUDEvent<T>(CRUDAction.UPDATE, bean), EventTarget.ALL);
	}

	@Override
	public void deleteBean(T bean, HttpServletRequest req, URIParser uriParser, User user) {}

	@Override
	public void beanDeleted(T bean, HttpServletRequest req, URIParser uriParser, User user) {

		eventHandler.sendEvent(channel, new CRUDEvent<T>(CRUDAction.DELETE, bean), EventTarget.ALL);		
	}
}
