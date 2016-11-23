package se.unlogic.standardutils.db.tableversionhandler;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.xml.XMLParser;


public class XMLDBScript implements DBScript {

	private XMLParser dbScriptNode;
	
	public XMLDBScript(XMLParser dbScriptNode) {

		this.dbScriptNode = dbScriptNode;
	}

	public void execute(TransactionHandler transactionHandler) throws SQLException {

		List<XMLParser> xmlQueries = dbScriptNode.getNodes("Query");
		
		if(xmlQueries.isEmpty()){
			return;
		}
				
		for(XMLParser query : xmlQueries){
			
			transactionHandler.getUpdateQuery(query.getString(".")).executeUpdate();
			
			if(query.getBoolean("@forceCommit")){
				
				transactionHandler.intermediateCommit();
			}
		}	
	}
}
