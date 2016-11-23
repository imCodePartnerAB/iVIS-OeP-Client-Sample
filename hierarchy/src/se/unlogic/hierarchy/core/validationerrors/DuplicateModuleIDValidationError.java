package se.unlogic.hierarchy.core.validationerrors;

import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name = "validationError")
public class DuplicateModuleIDValidationError extends ModuleConflictValidationError {

	public DuplicateModuleIDValidationError(ModuleDescriptor moduleDescriptor, ModuleDescriptor conflictingModuleDescriptor, ModuleType moduleType) {

		super("DuplicateModuleID", moduleDescriptor, conflictingModuleDescriptor, moduleType);
	}
}
