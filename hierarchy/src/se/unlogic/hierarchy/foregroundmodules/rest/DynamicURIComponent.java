package se.unlogic.hierarchy.foregroundmodules.rest;

import se.unlogic.standardutils.populators.BeanStringPopulator;


public class DynamicURIComponent implements URIComponent {

	private final BeanStringPopulator<?> populator;
	private final int paramIndex;
	
	public DynamicURIComponent(ParamMapping paramMapping) {

		super();
		this.populator = paramMapping.getPopulator();
		this.paramIndex = paramMapping.getIndex();
	}

	@Override
	public boolean matches(String value, Object[] paramArray) {

		Object bean = populator.getValue(value);
		
		if(bean == null){
		
			return false;
		}
		
		paramArray[paramIndex] = bean;
		
		return true;
	}

}
