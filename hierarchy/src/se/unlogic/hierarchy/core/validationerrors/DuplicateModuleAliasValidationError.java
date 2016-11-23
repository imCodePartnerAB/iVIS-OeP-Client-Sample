package se.unlogic.hierarchy.core.validationerrors;

import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name = "validationError")
public class DuplicateModuleAliasValidationError extends ModuleConflictValidationError {

	public DuplicateModuleAliasValidationError(ModuleDescriptor moduleDescriptor, ModuleDescriptor conflictingModuleDescriptor, ModuleType moduleType) {

		super("DuplicateModuleAlias", moduleDescriptor, conflictingModuleDescriptor, moduleType);
	}
}
