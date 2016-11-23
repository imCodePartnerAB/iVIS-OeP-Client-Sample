package se.unlogic.hierarchy.foregroundmodules.groupadmin;

import se.unlogic.hierarchy.foregroundmodules.groupproviders.SimpleGroup;
import se.unlogic.standardutils.factory.BeanFactory;


public class SimpleGroupFactory implements BeanFactory<SimpleGroup> {

	@Override
	public SimpleGroup newInstance() {

		return new SimpleGroup();
	}

}
