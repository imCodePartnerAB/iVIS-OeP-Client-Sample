package se.unlogic.hierarchy.core.validationerrors;

import se.unlogic.hierarchy.core.enums.ModuleType;
import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name = "validationError")
public class ModuleConflictValidationError extends ValidationError {

	@XMLElement(childName="TargetDescriptor")
	private ModuleDescriptor moduleDescriptor;
	
	@XMLElement(childName="ConflictingDescriptor")
	private ModuleDescriptor conflictingModuleDescriptor;
	
	@XMLElement
	private ModuleType moduleType;

	public ModuleConflictValidationError(String messageKey, ModuleDescriptor moduleDescriptor, ModuleDescriptor conflictingModuleDescriptor, ModuleType moduleType) {

		super(messageKey);
		this.moduleDescriptor = moduleDescriptor;
		this.conflictingModuleDescriptor = conflictingModuleDescriptor;
		this.moduleType = moduleType;
	}

	public ModuleDescriptor getModuleDescriptor() {

		return moduleDescriptor;
	}

	public ModuleDescriptor getConflictingModuleDescriptor() {

		return conflictingModuleDescriptor;
	}

	public ModuleType getModuleType() {

		return moduleType;
	}

}
