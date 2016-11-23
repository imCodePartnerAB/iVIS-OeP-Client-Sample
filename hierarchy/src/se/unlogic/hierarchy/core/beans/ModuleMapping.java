package se.unlogic.hierarchy.core.beans;

import java.util.List;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;

public class ModuleMapping<DescriptorType extends ModuleDescriptor> {

	private DescriptorType moduleDescriptor;
	private List<AliasMapping> mappings;

	public ModuleMapping(DescriptorType moduleDescriptor, List<AliasMapping> mappings) {

		super();
		this.moduleDescriptor = moduleDescriptor;
		this.mappings = mappings;
	}

	public DescriptorType getModuleDescriptor() {

		return moduleDescriptor;
	}

	public List<AliasMapping> getMappings() {

		return mappings;
	}

}
