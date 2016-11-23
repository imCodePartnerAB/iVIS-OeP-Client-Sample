package se.unlogic.hierarchy.foregroundmodules.userproviders;

import java.lang.reflect.Field;

import javax.sql.DataSource;

import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;

public class SimpleUserProviderModule extends BaseUserFormProviderModule<SimpleUser> {

	public SimpleUserProviderModule() {
 
		super(SimpleUser.class);
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, this.getClass().getName(), new XMLDBScriptProvider(this.getClass().getResourceAsStream("dbscripts/SimpleUserProvider.xml")));

		if(upgradeResult.isUpgrade()){

			log.info(upgradeResult.toString());
		}

		super.createDAOs(dataSource);

	}

	@Override
	protected String getUsergroupTablename() {

		return "simple_user_groups";
	}

	@Override
	protected Field getGroupsRelation() {

		return SimpleUser.GROUPS_RELATION;
	}

	@Override
	protected Field getAttributesRelation() {

		return SimpleUser.ATTRIBUTES_RELATION;
	}

	@Override
	protected String getUserAttributesTableName() {

		return "simple_user_attributes";
	}

	@Override
	protected String getDefaultFormStyleSheet() {
		return "SimpleUserProviderForm.en.xsl";
	}
}
