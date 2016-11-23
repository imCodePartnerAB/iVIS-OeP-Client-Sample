package se.unlogic.hierarchy.foregroundmodules.invitation;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.annotations.CheckboxSettingDescriptor;
import se.unlogic.hierarchy.core.annotations.ModuleSetting;
import se.unlogic.hierarchy.core.interfaces.ForegroundModuleDescriptor;
import se.unlogic.hierarchy.core.interfaces.SectionInterface;
import se.unlogic.hierarchy.core.utils.HierarchyAnnotatedDAOFactory;
import se.unlogic.hierarchy.core.utils.IntegerBasedCRUD;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.SimpleInvitation;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.SimpleInvitationType;
import se.unlogic.hierarchy.foregroundmodules.invitation.cruds.SimpleInvitationCRUD;
import se.unlogic.hierarchy.foregroundmodules.invitation.cruds.SimpleInvitationTypeCRUD;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.db.DBUtils;
import se.unlogic.standardutils.db.tableversionhandler.TableVersionHandler;
import se.unlogic.standardutils.db.tableversionhandler.UpgradeResult;
import se.unlogic.standardutils.db.tableversionhandler.XMLDBScriptProvider;
import se.unlogic.standardutils.string.StringUtils;

public class SimpleInvitationAdminModule extends BaseInvitationAdminModule<SimpleInvitation, SimpleInvitationType> {

	@ModuleSetting
	@CheckboxSettingDescriptor(name = "Register in instance handler", description = "Controls if this module should register itself in the global instance handler.")
	boolean registerInInstanceHandler = true;

	protected AnnotatedDAO<SimpleInvitation> invitationDAO;
	protected AnnotatedDAO<SimpleInvitationType> invitationTypeDAO;

	protected QueryParameterFactory<SimpleInvitation, String> invitationEmailParamFactory;

	protected SimpleInvitationCRUD invitationCRUD;
	protected SimpleInvitationTypeCRUD invitationTypeCRUD;

	@Override
	public void init(ForegroundModuleDescriptor moduleDescriptor, SectionInterface sectionInterface, DataSource dataSource) throws Exception {

		super.init(moduleDescriptor, sectionInterface, dataSource);

		if (registerInInstanceHandler) {

			if (!systemInterface.getInstanceHandler().addInstance(SimpleInvitationAdminModule.class, this)) {

				log.warn("Another instance has already been registered in instance handler for class " + SimpleInvitationAdminModule.class.getName());
			}
		}

	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		TransactionHandler transactionHandler = null;

		try {
			transactionHandler = new TransactionHandler(dataSource);

			//Check if new table version style has taken over
			if (TableVersionHandler.getTableGroupVersion(transactionHandler, SimpleInvitationAdminModule.class.getName()) == null) {

				//Check DB tables legacy style
				if (!DBUtils.tableExists(dataSource, "invitationtypes")) {

					log.info("Creating invitationtypes table in datasource " + dataSource);

					String sql = StringUtils.readStreamAsString(SimpleInvitationAdminModule.class.getResourceAsStream("dbscripts/InvitationTypesTable.sql"));

					transactionHandler.getUpdateQuery(sql).executeUpdate();
				}

				if (!DBUtils.tableExists(dataSource, "invitationtypegroups")) {

					log.info("Creating invitationtypegroups table in datasource " + dataSource);

					String sql = StringUtils.readStreamAsString(SimpleInvitationAdminModule.class.getResourceAsStream("dbscripts/InvitationTypeGroupsTable.sql"));

					transactionHandler.getUpdateQuery(sql).executeUpdate();
				}

				if (!DBUtils.tableExists(dataSource, "invitations")) {

					log.info("Creating invitations table in datasource " + dataSource);

					String sql = StringUtils.readStreamAsString(SimpleInvitationAdminModule.class.getResourceAsStream("dbscripts/InvitationsTable.sql"));

					transactionHandler.getUpdateQuery(sql).executeUpdate();
				}
			}

		} finally {

			TransactionHandler.autoClose(transactionHandler);
		}

		//Check DB tables modern style
		UpgradeResult upgradeResult = TableVersionHandler.upgradeDBTables(dataSource, SimpleInvitationAdminModule.class.getName(), new XMLDBScriptProvider(SimpleInvitationAdminModule.class.getResourceAsStream("dbscripts/DB script.xml")));

		if (upgradeResult.isUpgrade()) {

			log.info(upgradeResult.toString());
		}

		HierarchyAnnotatedDAOFactory daoFactory = new HierarchyAnnotatedDAOFactory(dataSource, systemInterface.getUserHandler(), systemInterface.getGroupHandler());

		invitationDAO = daoFactory.getDAO(SimpleInvitation.class);
		invitationTypeDAO = daoFactory.getDAO(SimpleInvitationType.class);

		invitationEmailParamFactory = invitationDAO.getParamFactory("email", String.class);

		invitationCRUD = new SimpleInvitationCRUD(invitationDAO.getWrapper(Integer.class), this);
		invitationTypeCRUD = new SimpleInvitationTypeCRUD(invitationTypeDAO.getWrapper(Integer.class), this, systemInterface.getGroupHandler());
	}

	@Override
	public List<SimpleInvitationType> getInvitationTypes() throws SQLException {

		return invitationTypeDAO.getAll();
	}

	@Override
	public List<SimpleInvitation> getInvitations() throws SQLException {

		return invitationDAO.getAll();
	}

	@Override
	protected IntegerBasedCRUD<SimpleInvitationType, ?> getInvitationTypeCRUD() {

		return invitationTypeCRUD;
	}

	@Override
	protected IntegerBasedCRUD<SimpleInvitation, ?> getInvitationCRUD() {

		return invitationCRUD;
	}

	@Override
	protected void updateInvitation(SimpleInvitation invitation) throws SQLException {

		invitationDAO.update(invitation);
	}

	@Override
	public boolean checkIfEmailInUse(SimpleInvitation invitation) throws SQLException {

		if (systemInterface.getUserHandler().getUserByEmail(invitation.getEmail(), false, false) != null) {

			return true;
		}

		HighLevelQuery<SimpleInvitation> query = new HighLevelQuery<SimpleInvitation>(invitationEmailParamFactory.getParameter(invitation.getEmail()));

		SimpleInvitation match = invitationDAO.get(query);

		if (match != null && !match.equals(invitation)) {

			return true;
		}

		return false;
	}

	@Override
	protected String getInvitationModuleClass() {

		return SimpleInvitationModule.class.getName();
	}

	@Override
	public void unload() throws Exception {

		if (registerInInstanceHandler) {

			if (systemInterface.getInstanceHandler().getInstance(SimpleInvitationAdminModule.class) == this) {

				systemInterface.getInstanceHandler().removeInstance(SimpleInvitationAdminModule.class);
			}
		}

		super.unload();
	}
}
