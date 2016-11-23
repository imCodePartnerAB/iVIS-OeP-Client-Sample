package se.unlogic.hierarchy.basemodules;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.interfaces.SectionModule;
import se.unlogic.hierarchy.core.interfaces.SystemInterface;
import se.unlogic.hierarchy.core.interfaces.VisibleModuleDescriptor;


public abstract class BaseSectionModule<DescriptorType extends VisibleModuleDescriptor> extends BaseModule<DescriptorType> implements SectionModule<DescriptorType> {

	protected SectionInterface sectionInterface;
	protected SystemInterface systemInterface;
	protected DataSource dataSource;
	
	@Override
	public void init(DescriptorType descriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		this.moduleDescriptor = descriptor;
		this.sectionInterface = sectionInterface;
		this.systemInterface = sectionInterface.getSystemInterface();
		this.dataSource = dataSource;

		this.createDAOs(dataSource);	
	}	
}
