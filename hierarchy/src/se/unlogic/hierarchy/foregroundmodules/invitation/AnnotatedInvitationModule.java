package se.unlogic.hierarchy.foregroundmodules.invitation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitation;
import se.unlogic.hierarchy.foregroundmodules.invitation.beans.BaseInvitationType;
import se.unlogic.standardutils.dao.AnnotatedDAO;
import se.unlogic.standardutils.dao.HighLevelQuery;
import se.unlogic.standardutils.dao.QueryParameterFactory;
import se.unlogic.standardutils.populators.StringPopulator;
import se.unlogic.standardutils.validation.ValidationError;
import se.unlogic.standardutils.validation.ValidationException;
import se.unlogic.webutils.validation.ValidationUtils;


public abstract class AnnotatedInvitationModule<I extends BaseInvitation, IT extends BaseInvitationType, U extends MutableUser> extends BaseInvitationModule<I, IT, U> {

	protected AnnotatedDAO<I> invitationDAO;
	protected QueryParameterFactory<I, Integer> invitationIDParamFactory;
	protected QueryParameterFactory<I, UUID> invitationLinkIDParamFactory;

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		invitationDAO = createDAO(dataSource);
		invitationIDParamFactory = invitationDAO.getParamFactory("invitationID", Integer.class);
		invitationLinkIDParamFactory = invitationDAO.getParamFactory("linkID", UUID.class);
	}

	protected abstract AnnotatedDAO<I> createDAO(DataSource dataSource);

	@Override
	protected U populateUser(HttpServletRequest req) throws ValidationException {

		ArrayList<ValidationError> validationErrors = new ArrayList<ValidationError>();

		String username = ValidationUtils.validateParameter("username", req, true, 0, 40, StringPopulator.getPopulator(), validationErrors);
		String password = ValidationUtils.validateParameter("password", req, true, 0, 255, StringPopulator.getPopulator(), validationErrors);

		if (!validationErrors.isEmpty()) {

			throw new ValidationException(validationErrors);
		}

		U user = createNewUserInstance();

		user.setUsername(username);
		user.setPassword(password);

		return user;
	}

	protected abstract U createNewUserInstance();

	@Override
	protected I getInvitation(Integer invitationID, UUID invitationLinkID) throws SQLException {

		HighLevelQuery<I> query = new HighLevelQuery<I>();

		query.addParameter(invitationIDParamFactory.getParameter(invitationID));
		query.addParameter(invitationLinkIDParamFactory.getParameter(invitationLinkID));

		return invitationDAO.get(query);
	}

	@Override
	protected void deleteInvitation(I invitation) throws SQLException {

		invitationDAO.delete(invitation);
	}
}
