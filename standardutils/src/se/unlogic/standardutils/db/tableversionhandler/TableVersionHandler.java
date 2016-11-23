package se.unlogic.standardutils.db.tableversionhandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.dao.querys.ObjectQuery;
import se.unlogic.standardutils.dao.querys.UpdateQuery;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.populators.IntegerPopulator;
import se.unlogic.standardutils.string.StringUtils;

public class TableVersionHandler {

	public static UpgradeResult upgradeDBTables(DataSource dataSource, String tableGroupName, DBScriptProvider scriptProvider) throws TableUpgradeException, SQLException {

		return upgradeDBTables(dataSource, tableGroupName, scriptProvider, null, null);
	}

	public static UpgradeResult upgradeDBTables(DataSource dataSource, String tableGroupName, DBScriptProvider scriptProvider, Integer initialVersion, Integer maxVersion) throws TableUpgradeException, SQLException {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			if(initialVersion == null){
				
				initialVersion = getTableGroupVersion(transactionHandler, tableGroupName);
				
				if (initialVersion == null) {

					initialVersion = 0;
				}
			}
			
			int currentVersion = initialVersion;

			try {
				while (true) {

					if(maxVersion != null && currentVersion >= maxVersion){

						break;
					}

					DBScript script = scriptProvider.getScript(++currentVersion);

					if (script == null) {
						currentVersion--;
						break;
					}

					script.execute(transactionHandler);
				}

				if (currentVersion != initialVersion) {

					setTableGroupVersion(transactionHandler, tableGroupName, currentVersion);
				}

				transactionHandler.commit();

			} catch (Exception e) {

				throw new TableUpgradeException("Error upgrading table group " + tableGroupName + ". Initial version " + initialVersion + ", upgrade failed at version " + currentVersion, e);
			}

			return new UpgradeResult(tableGroupName, initialVersion, currentVersion);

		} finally {
			TransactionHandler.autoClose(transactionHandler);
		}
	}

	public static void setTableGroupVersion(TransactionHandler transactionHandler, String tableGroupName, int version) throws SQLException {

		checkTableGroupName(tableGroupName);

		if(!DBUtils.tableExists(transactionHandler.getMetaData(), "table_versions")){

			try {
				String sql = StringUtils.readStreamAsString(TableVersionHandler.class.getResourceAsStream("TableVersionHandler.sql"));

				UpdateQuery createTableQuery = transactionHandler.getUpdateQuery(sql);

				createTableQuery.executeUpdate();

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		UpdateQuery deleteQuery = transactionHandler.getUpdateQuery("DELETE FROM table_versions WHERE tableGroupName = ?");

		deleteQuery.setString(1,tableGroupName);

		deleteQuery.executeUpdate();

		UpdateQuery insertQuery = transactionHandler.getUpdateQuery("INSERT INTO table_versions VALUES (?,?)");

		insertQuery.setString(1, tableGroupName);
		insertQuery.setInt(2, version);

		insertQuery.executeUpdate();
	}

	public static Integer getTableGroupVersion(TransactionHandler transactionHandler, String tableGroupName) throws SQLException {

		if(!DBUtils.tableExists(transactionHandler.getMetaData(), "table_versions")){

			return null;
		}

		ObjectQuery<Integer> query = transactionHandler.getObjectQuery("SELECT version FROM table_versions WHERE tableGroupName = ?", IntegerPopulator.getPopulator());

		query.setString(1, tableGroupName);

		return query.executeQuery();
	}

	public static Integer getTableGroupVersion(DataSource dataSource, String tableGroupName) throws SQLException {

		Connection connection = null;

		try{
			connection = dataSource.getConnection();

			return getTableGroupVersion(connection, tableGroupName);

		}finally{
			DBUtils.closeConnection(connection);
		}
	}

	public static Integer getTableGroupVersion(Connection connection, String tableGroupName) throws SQLException {

		if(!DBUtils.tableExists(connection.getMetaData(), "table_versions")){

			return null;
		}

		ObjectQuery<Integer> query = new ObjectQuery<Integer>(connection, false, "SELECT version FROM table_versions WHERE tableGroupName = ?", IntegerPopulator.getPopulator());

		query.setString(1, tableGroupName);

		return query.executeQuery();
	}

	private static void checkTableGroupName(String tableGroupName){

		if(tableGroupName.length() > 255){

			throw new RuntimeException("Table group name cannot be longer than 255 characters");
		}
	}

	public static void setTableGroupVersion(DataSource dataSource, String tableGroupName, int version) throws SQLException {

		TransactionHandler transactionHandler = null;
		
		try{
			transactionHandler = new TransactionHandler(dataSource);
			
			setTableGroupVersion(transactionHandler, tableGroupName, version);
			
			transactionHandler.commit();
			
		}finally{
			
			TransactionHandler.autoClose(transactionHandler);
		}
	}
}
