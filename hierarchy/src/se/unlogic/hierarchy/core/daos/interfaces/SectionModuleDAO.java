package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;


public interface SectionModuleDAO<T extends ModuleDescriptor> extends ModuleDAO<T> {

	public abstract List<T> getEnabledModules(Integer sectionID) throws SQLException;

	public abstract List<T> getModules(Integer sectionID) throws SQLException;
}
