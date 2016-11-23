package se.unlogic.hierarchy.core.interfaces;


public interface GlobalInstanceListener {

	public <KeyClass, InstanceType extends KeyClass> void instanceAdded(Class<KeyClass> key, InstanceType instance);
	
	public <KeyClass, InstanceType extends KeyClass> void instanceRemoved(Class<KeyClass> key, InstanceType instance);
}
