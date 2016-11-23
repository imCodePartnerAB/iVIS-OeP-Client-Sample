package se.unlogic.standardutils.threads;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import se.unlogic.standardutils.reflection.ReflectionUtils;


public class ReflectedRunnable implements Runnable {

	private final Object object;
	private final Method method;
	
	public ReflectedRunnable(Object object, String methodName) {

		super();
		this.object = object;
		
		method = ReflectionUtils.getMethod(object.getClass(), methodName, 0);
		
		if(method == null){
			
			throw new RuntimeException("Method " + methodName + "() not found in class " + object.getClass());
		}
		
		if(!method.isAccessible()){
			
			ReflectionUtils.fixMethodAccess(method);
		}
	}

	public void run() {

		try {
			method.invoke(object);
			
		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);
			
		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);
			
		} catch (InvocationTargetException e) {

			throw new RuntimeException(e);
		}
	}
}
