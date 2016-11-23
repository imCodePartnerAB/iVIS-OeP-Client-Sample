package se.unlogic.standardutils.db.tableversionhandler;


public interface DBScriptProvider {

	DBScript getScript(int version);

}
