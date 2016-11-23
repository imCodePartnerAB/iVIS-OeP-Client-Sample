package se.unlogic.hierarchy.basemodules;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

import se.unlogic.standardutils.reflection.ReflectionUtils;


public class MethodInstanceListener<KeyClass> extends ReflectionInstanceListener<KeyClass>{

	private final Method method;

	public MethodInstanceListener(Object target, Method method, boolean required, Lock writeLock) {

		super(required, target, writeLock);
		this.method = method;
		
		ReflectionUtils.fixMethodAccess(method);
	}

	@Override
	protected <InstanceType extends KeyClass> void setInstance(InstanceType instance){
		
		try {
			method.invoke(target, instance);
			
		} catch (Exception e){
		
			if(e instanceof RuntimeException){
				
				throw (RuntimeException)e;
			}
			
			throw new RuntimeException(e);
		}			
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Class getRawKey(){
		
		return method.getParameterTypes()[0];
	}
}
