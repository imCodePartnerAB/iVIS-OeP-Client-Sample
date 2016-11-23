package se.unlogic.hierarchy.core.interfaces;


public interface InstanceListener<KeyClass> {

	public <InstanceType extends KeyClass> void instanceAdded(Class<KeyClass> key, InstanceType instance);
	
	public <InstanceType extends KeyClass> void instanceRemoved(Class<KeyClass> key, InstanceType instance);
}
