package se.unlogic.hierarchy.core.daos.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import se.unlogic.standardutils.dao.QueryOperators;
import se.unlogic.standardutils.dao.TransactionHandler;


public interface AttributeDAO<T> {

	public void set(T descriptor) throws SQLException;

	public void set(T descriptor, TransactionHandler transactionHandler) throws SQLException;

	public void getAttributeHandler(T descriptor, Connection connection) throws SQLException;

	public List<Integer> getIDsByAttribute(String name) throws SQLException;

	public List<Integer> getIDsByAttribute(String name, String value, QueryOperators operator) throws SQLException;
}
