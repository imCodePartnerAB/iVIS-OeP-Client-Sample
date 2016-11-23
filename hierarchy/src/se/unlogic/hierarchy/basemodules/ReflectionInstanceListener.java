package se.unlogic.hierarchy.basemodules;

import java.util.concurrent.locks.Lock;

import se.unlogic.hierarchy.core.interfaces.InstanceListener;


public abstract class ReflectionInstanceListener<KeyClass> implements InstanceListener<KeyClass> {

	protected final Lock writeLock;
	protected final boolean required;
	protected final Object target;
	protected boolean hasInstance;
	
	public ReflectionInstanceListener(boolean required, Object target, Lock writeLock) {

		super();
		this.required = required;
		this.target = target;
		this.writeLock = writeLock;
	}

	@Override
	public <InstanceType extends KeyClass> void instanceAdded(Class<KeyClass> key, InstanceType instance) {

		writeLock.lock();
		
		try{
			setInstance(instance);
			hasInstance = true;
			
		}finally{
			
			writeLock.unlock();
		}

	}

	@Override
	public <InstanceType extends KeyClass> void instanceRemoved(Class<KeyClass> key, InstanceType instance) {

		writeLock.lock();
		
		try{
			hasInstance = false;
			setInstance(null);
			
		}finally{
			
			writeLock.unlock();
		}		
	}

	protected abstract <InstanceType extends KeyClass> void setInstance(InstanceType instance);	
	
	@SuppressWarnings("rawtypes")
	public abstract Class getRawKey();
	
	public boolean isRequired() {
	
		return required;
	}
	
	public boolean hasInstance(){
		
		return hasInstance;
	}
}
