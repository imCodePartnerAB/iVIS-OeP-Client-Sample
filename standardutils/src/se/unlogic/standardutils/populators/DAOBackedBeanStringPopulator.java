package se.unlogic.standardutils.populators;

import java.sql.SQLException;

import se.unlogic.standardutils.dao.CRUDDAO;
import se.unlogic.standardutils.numbers.NumberUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;



public class DAOBackedBeanStringPopulator<T> implements BeanStringPopulator<T> {

	private final CRUDDAO<T, Integer> crudDAO;
	private final Class<T> clazz;

	public DAOBackedBeanStringPopulator(Class<T> clazz, CRUDDAO<T, Integer> crudDAO) {

		this.clazz = clazz;
		this.crudDAO = crudDAO;
	}

	public boolean validateFormat(String value) {

		return NumberUtils.isInt(value);
	}

	public T getValue(String value) {

		try {
			return crudDAO.get(Integer.parseInt(value));

		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Class<? extends T> getType() {

		return clazz;
	}

	public String getPopulatorID() {

		return null;
	}
}
