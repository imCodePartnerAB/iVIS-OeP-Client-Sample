package se.unlogic.standardutils.db.tableversionhandler;

import java.sql.SQLException;

import se.unlogic.standardutils.dao.TransactionHandler;


public interface DBScript {

	void execute(TransactionHandler transactionHandler) throws SQLException;

}
