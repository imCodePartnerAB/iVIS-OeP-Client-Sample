package se.unlogic.webutils.populators;

import javax.servlet.http.HttpServletRequest;

import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.http.BeanRequestPopulator;

public class SimpleRequestPopulator<ReturnType> implements BeanRequestPopulator<ReturnType> {

	private Class<ReturnType> clazz;

	public SimpleRequestPopulator(Class<ReturnType> clazz) {
		this.clazz = clazz;
	}
	
	public ReturnType populate(HttpServletRequest req) throws ValidationException {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new ValidationException(new ValidationError("errorWhilePopulating"));
		} catch (IllegalAccessException e) {
			throw new ValidationException(new ValidationError("errorWhilePopulating"));
		}
	}

	public ReturnType populate(ReturnType bean, HttpServletRequest req) throws ValidationException {
		return bean;
	}
}
