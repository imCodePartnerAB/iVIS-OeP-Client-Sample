package se.unlogic.hierarchy.basemodules;

import java.lang.reflect.Field;
import java.util.concurrent.locks.Lock;

import se.unlogic.standardutils.reflection.ReflectionUtils;


public class FieldInstanceListener<KeyClass> extends ReflectionInstanceListener<KeyClass>{

	private final Field field;

	public FieldInstanceListener(Object target, Field field, boolean required, Lock writeLock) {

		super(required, target, writeLock);
		this.field = field;
		
		ReflectionUtils.fixFieldAccess(field);
	}

	@Override
	protected <InstanceType extends KeyClass> void setInstance(InstanceType instance){
		
		try {
			field.set(target, instance);
			
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
		
		return field.getType();
	}
}
