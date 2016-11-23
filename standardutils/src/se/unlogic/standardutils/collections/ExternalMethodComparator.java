package se.unlogic.standardutils.collections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

import se.unlogic.standardutils.enums.Order;
import se.unlogic.standardutils.reflection.ReflectionUtils;


public class ExternalMethodComparator<BeanType,MethodReturnType> implements Comparator<BeanType> {

	protected final Method method;
	protected final Order order;
	protected final Comparator<MethodReturnType> comparator;

	public ExternalMethodComparator(Class<? extends BeanType> beanClass, Class<?> methodClass, String methodName, Order order, Comparator<MethodReturnType> comparator){

		method = ReflectionUtils.getMethod(beanClass, methodName, methodClass);

		if(method == null){

			throw new RuntimeException("No method named " + methodName + " returning class " + methodClass.getName() + " and taking no paramaters found in " + beanClass);
		}

		this.order = order;
		this.comparator = comparator;
	}

	@SuppressWarnings({ "unchecked" })
	public int compare(BeanType o1, BeanType o2) {

		try {

			MethodReturnType value1 = (MethodReturnType)method.invoke(o1);
			MethodReturnType value2 = (MethodReturnType)method.invoke(o2);

			if(value1 == null && value2 == null){

				return 0;
			}

			if(order == Order.ASC){

				if(value1 == null){

					return -1;

				}else if(value2 == null){

					return 1;
				}

				return comparator.compare(value1, value2);

			}else{

				if(value1 == null){

					return 1;

				}else if(value2 == null){

					return -1;
				}

				return comparator.compare(value2, value1);
			}


		} catch (IllegalArgumentException e) {

			throw new RuntimeException(e);

		} catch (IllegalAccessException e) {

			throw new RuntimeException(e);

		} catch (InvocationTargetException e) {

			throw new RuntimeException(e);
		}
	}
}
