package se.unlogic.hierarchy.core.interfaces;

import javax.sql.DataSource;


public interface SectionModule<DescriptorType> extends Module<DescriptorType> {

	void init(DescriptorType descriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception;
}
