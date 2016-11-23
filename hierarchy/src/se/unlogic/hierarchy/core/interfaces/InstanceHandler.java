package se.unlogic.hierarchy.core.interfaces;

import java.util.List;


public interface InstanceHandler {

	public <KeyClass, InstanceType extends KeyClass> boolean addInstance(Class<KeyClass> key, InstanceType instance);

	public <KeyClass, InstanceType extends KeyClass> boolean removeInstance(Class<KeyClass> key);

	public <KeyClass, InstanceType extends KeyClass> InstanceType getInstance(Class<KeyClass> key);

	public <KeyClass> boolean containsInstance(Class<KeyClass> key);

	public List<Class<?>> getKeys();

	public void addGlobalInstanceListener(GlobalInstanceListener listener);

	public void removeGlobalInstanceListener(GlobalInstanceListener listener);

	/**
	 * Add a listener that will be triggered when a specific class is added to the handler.
	 * If the specific class is already present in the handler, then the listener will be triggered immediately.
	 * 
	 * @param key the class to listen for
	 * @param listener the listener to be triggered
	 */
	public <KeyClass, InstanceType extends KeyClass> void addInstanceListener(Class<KeyClass> key, InstanceListener<KeyClass> listener);

	public <KeyClass> void removeInstanceListener(Class<KeyClass> key, InstanceListener<KeyClass> listener);

}