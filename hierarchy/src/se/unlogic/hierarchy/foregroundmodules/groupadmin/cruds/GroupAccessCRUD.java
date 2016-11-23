package se.unlogic.hierarchy.foregroundmodules.groupadmin.cruds;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.hierarchy.core.beans.MutableGroup;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.exceptions.UnableToAddGroupException;
import se.unlogic.hierarchy.core.exceptions.UnableToDeleteGroupException;
import se.unlogic.hierarchy.foregroundmodules.groupadmin.GroupAccessAdminModule;
import se.unlogic.standardutils.xml.XMLUtils;
import se.unlogic.webutils.http.BeanRequestPopulator;
import se.unlogic.webutils.http.URIParser;


public class GroupAccessCRUD extends GroupCRUD<GroupAccessAdminModule> {

	public GroupAccessCRUD(BeanRequestPopulator<MutableGroup> populator, String typeElementName, String typeLogName, GroupAccessAdminModule groupAdminModule) {

		super(populator, typeElementName, typeLogName, groupAdminModule);
	}

	@Override
	protected void appendUpdateFormData(MutableGroup bean, Document doc, Element updateTypeElement, User user, HttpServletRequest req, URIParser uriParser) throws SQLException {

		super.appendUpdateFormData(bean, doc, updateTypeElement, user, req, uriParser);

		XMLUtils.append(doc, updateTypeElement, "AdminUsers", callback.getGroupAdminUsers(bean));
	}

	@Override
	protected void addBean(MutableGroup bean, HttpServletRequest req, User user, URIParser uriParser) throws SQLException, UnableToAddGroupException {

		super.addBean(bean, req, user, uriParser);

		callback.setGroupAccessMappings(bean, req);
	}

	@Override
	protected void updateBean(MutableGroup bean, HttpServletRequest req, User user, URIParser uriParser) throws Exception {

		super.updateBean(bean, req, user, uriParser);

		callback.setGroupAccessMappings(bean, req);
	}

	@Override
	protected void deleteBean(MutableGroup bean, HttpServletRequest req, User user, URIParser uriParser) throws SQLException, UnableToDeleteGroupException {

		super.deleteBean(bean, req, user, uriParser);

		callback.deleteGroupAccessMappings(bean, null);
	}
}
