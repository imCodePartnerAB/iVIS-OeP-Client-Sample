package se.unlogic.hierarchy.basemodules;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.beans.SettingDescriptor;
import se.unlogic.hierarchy.core.interfaces.Module;


public abstract class BaseModule<DescriptorType> implements Module<DescriptorType> {

	protected Logger log = Logger.getLogger(this.getClass());
	protected DescriptorType moduleDescriptor;
	protected DataSource dataSource;
	
	@Override
	public void update(DescriptorType descriptor, DataSource dataSource) throws Exception {

		this.moduleDescriptor = descriptor;

		if (dataSource != this.dataSource) {
			this.dataSource = dataSource;
			this.createDAOs(dataSource);
		}	
	}

	@Override
	public void unload() throws Exception {}
	
	@Override
	public String toString(){

		return String.valueOf(moduleDescriptor);
	}	
	
	protected void createDAOs(DataSource dataSource) throws Exception {}	
	
	@Override
	public List<SettingDescriptor> getSettings() {

		return null;
	}
}
