package se.unlogic.hierarchy.foregroundmodules.groupproviders;

import java.lang.reflect.Field;

import javax.sql.DataSource;

import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;

public class SimpleGroupProviderModule extends BaseGroupProviderModule<SimpleGroup> {

	public SimpleGroupProviderModule() {

		super(SimpleGroup.class);
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		Integer tableVersion = TableVersionHandler.getTableGroupVersion(dataSource, this.getClass().getName());

		if (tableVersion == null) {

			if(DBUtils.tableExists(dataSource, "groups")){
				
				//Run version 1 and then set version to 2
				UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, this.getClass().getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("dbscripts/SimpleGroupProvider.xml")), null, 1);

				if (upgradeResult.isUpgrade()) {

					log.info(upgradeResult.toString());
				}
				
				//Set version to 2
				TableVersionHandler.setTableGroupVersion(dataSource, this.getClass().getName(), 2);
				
			}else if(DBUtils.tableExists(dataSource, "simple_groups")){
				
				//Set version to 2
				TableVersionHandler.setTableGroupVersion(dataSource, this.getClass().getName(), 2);		
				
			}else{
				
				//Set version to 1 so new tagbles will be create using script version 2
				TableVersionHandler.setTableGroupVersion(dataSource, this.getClass().getName(), 1);		
			}	
		}

		//Normal upgrade
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, this.getClass().getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("dbscripts/SimpleGroupProvider.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}
		
		super.createDAOs(dataSource);
	}

	@Override
	protected Field getAttributesRelation() {

		return SimpleGroup.ATTRIBUTES_RELATION;
	}

	@Override
	protected String getGroupAttributesTableName() {

		return "simple_group_attributes";
	}
	
}
