package se.unlogic.hierarchy.core.interfaces;

import java.util.List;


public interface MultipleAliasModuleDescriptor extends ModuleDescriptor, Prioritized{

	List<String> getAliases();
}
