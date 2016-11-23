package se.unlogic.hierarchy.core.utils.crud;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.standardutils.dao.TransactionHandler;


public class TransactionRequestFilter implements RequestFilter {

	public static final String TRANSACTION_HANDLER_REQUEST_ATTRIBUTE = "transactionHandler";
	
	protected final DataSource dataSource; 
	
	public TransactionRequestFilter(DataSource dataSource) {

		super();
		this.dataSource = dataSource;
	}

	@Override
	public HttpServletRequest parseRequest(HttpServletRequest req, User user) throws SQLException {

		req.setAttribute(TRANSACTION_HANDLER_REQUEST_ATTRIBUTE, new TransactionHandler(dataSource));

		return req;
	}

	@Override
	public void releaseRequest(HttpServletRequest req, User user) {

		TransactionHandler transactionHandler = (TransactionHandler) req.getAttribute(TRANSACTION_HANDLER_REQUEST_ATTRIBUTE);
		
		TransactionHandler.autoClose(transactionHandler);
	}
}
