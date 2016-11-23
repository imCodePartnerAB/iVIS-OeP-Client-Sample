package se.unlogic.hierarchy.core.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

import se.unlogic.hierarchy.core.interfaces.GlobalInstanceListener;
import se.unlogic.hierarchy.core.interfaces.InstanceHandler;
import se.unlogic.hierarchy.core.interfaces.InstanceListener;


/**
 * @author Robert "Unlogic" Olofsson
 *
 * This handler is used to simplify sharing of object instances on a global level. It removes the needs to recursively traverse
 * the section and module hierarchy when looking up an instance of a specific class instance shared by all modules on a global level.
 *
 */
public class SystemInstanceHandler implements InstanceHandler {

	protected Logger log = Logger.getLogger(this.getClass());

	private ConcurrentHashMap<Class<?>, Object> instanceMap = new ConcurrentHashMap<Class<?>, Object>();

	private CopyOnWriteArraySet<GlobalInstanceListener> globalInstanceListeners = new CopyOnWriteArraySet<GlobalInstanceListener>();

	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class<?>, Set<InstanceListener>> instanceListenerMap = new ConcurrentHashMap<Class<?>, Set<InstanceListener>>();

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <KeyClass,InstanceType extends KeyClass> boolean addInstance(Class<KeyClass> key, InstanceType instance){

		if(key == null || instance == null){

			throw new NullPointerException("Key and instance cannot be null");
		}

		Object registeredInstance = instanceMap.putIfAbsent(key, instance);

		if(registeredInstance == null){

			log.info("Instance " + instance + " added for key " + key);

			for(GlobalInstanceListener globalInstanceListener : globalInstanceListeners){

				try {
					globalInstanceListener.instanceAdded(key, instance);

				} catch (RuntimeException e) {

					log.error("Error notifying global instance listener " + globalInstanceListener + " regarding instace of class " + key, e);
				}
			}

			Set<InstanceListener> instanceListeners = instanceListenerMap.get(key);

			if(instanceListeners != null){

				for(InstanceListener instanceListener : instanceListeners){

					try {
						instanceListener.instanceAdded(key, instance);

					} catch (RuntimeException e) {

						log.error("Error notifying instance listener " + instanceListeners + " regarding instace of class " + key, e);
					}
				}
			}

			return true;

		}else if(registeredInstance == instance){

			return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <KeyClass, InstanceType extends KeyClass> boolean removeInstance(Class<KeyClass> key){

		InstanceType instance = (InstanceType) instanceMap.remove(key);

		log.info("Instance " + instance + " removed for key " + key);

		if(instance != null){

			Set<InstanceListener> instanceListeners = instanceListenerMap.get(key);

			if(instanceListeners != null){

				for(InstanceListener instanceListener : instanceListeners){

					instanceListener.instanceRemoved(key, instance);
				}
			}

			return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <KeyClass,InstanceType extends KeyClass> InstanceType getInstance(Class<KeyClass> key){

		return (InstanceType) instanceMap.get(key);
	}

	@Override
	public <KeyClass> boolean containsInstance(Class<KeyClass> key){

		return instanceMap.contains(key);
	}

	@Override
	public List<Class<?>> getKeys(){

		return new ArrayList<Class<?>>(instanceMap.keySet());
	}

	@Override
	public void addGlobalInstanceListener(GlobalInstanceListener listener) {

		globalInstanceListeners.add(listener);
	}

	@Override
	public void removeGlobalInstanceListener(GlobalInstanceListener listener) {

		globalInstanceListeners.remove(listener);
	}

	/**
	 * Add a listener that will be triggered when a specific class is added to the handler.
	 * If the specific class is already present in the handler, then the listener will be triggered immediately.
	 *
	 * @param key the class to listen for
	 * @param listener the listener to be triggered
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <KeyClass,InstanceType extends KeyClass> void addInstanceListener(Class<KeyClass> key, InstanceListener<KeyClass> listener){

		synchronized(this){

			Set<InstanceListener> listeners = instanceListenerMap.get(key);

			if(listeners == null){

				listeners = new CopyOnWriteArraySet<InstanceListener>();
				instanceListenerMap.put(key, listeners);
			}

			listeners.add(listener);
		}

		InstanceType instance = (InstanceType) instanceMap.get(key);

		if(instance != null){

			listener.instanceAdded(key, instance);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public synchronized <KeyClass> void removeInstanceListener(Class<KeyClass> key, InstanceListener<KeyClass> listener){

		Set<InstanceListener> listeners = instanceListenerMap.get(key);

		if(listeners != null){

			listeners.remove(listener);

			if(listeners.isEmpty()){

				instanceListenerMap.remove(key);
			}
		}
	}

	/**
	 * Removes all keys, instances and listeners
	 */
	@SuppressWarnings("rawtypes")
	public synchronized void clear(){

		for(Entry<Class<?>, Object> entry : instanceMap.entrySet()){

			log.warn("Instance for type " + entry.getKey().getName() + " implemented by " + entry.getValue().getClass() + " not removed from instance handler on shutdown.");
		}

		instanceMap.clear();

		for(Entry<Class<?>, Set<InstanceListener>> entry : instanceListenerMap.entrySet()){

			for(InstanceListener listener : entry.getValue()){

				log.warn("Instance listener for type " + entry.getKey().getName() + " implemented by " + listener.getClass() + " not removed from instance handler on shutdown.");
			}
		}

		instanceListenerMap.clear();

		for(GlobalInstanceListener listener : globalInstanceListeners){

			log.warn("Global instance listener implemented by " + listener.getClass() + " not removed from instance handler on shutdown.");
		}

		globalInstanceListeners.clear();
	}
}
