package se.unlogic.hierarchy.core.beans;

import se.unlogic.hierarchy.core.interfaces.ModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.ProviderDescriptor;
import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

@XMLElement(name = "ProviderDescriptor")
public class SimpleProviderDescriptor extends GeneratedElementable implements ProviderDescriptor {

	@XMLElement
	private final String id;

	@XMLElement
	private final String name;

	public SimpleProviderDescriptor(String id, String name) {

		super();
		this.id = id;
		this.name = name;
	}

	public SimpleProviderDescriptor(ModuleDescriptor moduleDescriptor) {

		if(moduleDescriptor.getModuleID() == null){
			
			throw new NullPointerException("moduleID cannot be null");
		}
		
		this.id = moduleDescriptor.getType().getShortName() + "-" + moduleDescriptor.getModuleID();
		this.name = moduleDescriptor.getName();
	}
	
	public String getID() {

		return id;
	}

	public String getName() {

		return name;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SimpleProviderDescriptor other = (SimpleProviderDescriptor) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
