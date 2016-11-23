package se.unlogic.hierarchy.core.events;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import se.unlogic.hierarchy.core.enums.CRUDAction;
import se.unlogic.standardutils.collections.CollectionUtils;


public class CRUDEvent<T extends Serializable> implements Serializable{

	private static final long serialVersionUID = 1222007411425011867L;
	
	private final Class<T> beanClass;
	private final CRUDAction action;
	private final List<T> beans;

	@SuppressWarnings("unchecked")
	public CRUDEvent(CRUDAction action, T bean) {

		this((Class<T>)bean.getClass(), action, Collections.singletonList(bean));
	}

	public CRUDEvent(Class<T> beanClass, CRUDAction action, T... beans) {

		this(beanClass, action, Arrays.asList(beans));
	}

	public CRUDEvent(Class<T> beanClass, CRUDAction action, List<T> beans) {

		if(action == null){

			throw new NullPointerException("Action cannot be null");
		}

		if(CollectionUtils.isEmpty(beans)){

			throw new NullPointerException("Beans cannot be null or empty");
		}

		this.action = action;
		this.beans = beans;
		this.beanClass = beanClass;
	}

	public CRUDAction getAction() {

		return action;
	}

	public List<T> getBeans() {

		return beans;
	}

	public Class<T> getBeanClass(){

		return beanClass;
	}

	@Override
	public String toString() {

		return "CRUDEvent [beanClass=" + beanClass.getSimpleName() + ", action=" + action + ", beans=" + beans + "]";
	}
}
