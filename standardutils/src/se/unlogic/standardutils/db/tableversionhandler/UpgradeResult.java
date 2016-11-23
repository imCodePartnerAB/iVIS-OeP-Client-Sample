package se.unlogic.standardutils.db.tableversionhandler;

public class UpgradeResult {

	private String tableGroupName;
	private Integer initialVersion;
	private Integer currentVersion;

	public UpgradeResult(String tableGroupName, Integer initialVersion, Integer currentVersion) {

		super();
		this.tableGroupName = tableGroupName;
		this.initialVersion = initialVersion;
		this.currentVersion = currentVersion;
	}

	public Integer getInitialVersion() {

		return initialVersion;
	}

	public Integer getCurrentVersion() {

		return currentVersion;
	}

	@Override
	public String toString() {

		if(!isUpgrade()){
			
			return "No table upgrade performed for table group " + tableGroupName;
		}
		
		return "Table group " + tableGroupName + " upgraded from version " + initialVersion + " to version " + currentVersion;
	}
	
	public boolean isUpgrade(){
		
		return initialVersion < currentVersion;
	}

	public String getTableGroupName() {
	
		return tableGroupName;
	}
}
