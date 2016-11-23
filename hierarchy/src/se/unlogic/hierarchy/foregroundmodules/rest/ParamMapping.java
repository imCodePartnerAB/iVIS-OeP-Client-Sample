package se.unlogic.hierarchy.foregroundmodules.rest;

import se.unlogic.standardutils.populators.BeanStringPopulator;

public class ParamMapping {

	private final URIParam annotation;
	private final BeanStringPopulator<?> populator;
	private final int index;

	public ParamMapping(URIParam annotation, BeanStringPopulator<?> populator, int index) {

		super();
		this.annotation = annotation;
		this.populator = populator;
		this.index = index;
	}

	public URIParam getAnnotation() {

		return annotation;
	}

	public BeanStringPopulator<?> getPopulator() {

		return populator;
	}

	public int getIndex() {

		return index;
	}
}
