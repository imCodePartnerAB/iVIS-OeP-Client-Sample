package se.unlogic.hierarchy.core.interfaces;

import se.unlogic.hierarchy.core.enums.PathType;

public interface VisibleModuleDescriptor extends ModuleDescriptor{

	Integer getSectionID();
	
	String getStaticContentPackage();
	
	boolean hasStyleSheet();

	PathType getXslPathType();
	
	String getXslPath();
}
