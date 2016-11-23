package se.unlogic.hierarchy.foregroundmodules.userproviders;

import java.util.List;

import se.unlogic.hierarchy.core.beans.BaseUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.populators.GroupQueryPopulator;
import se.unlogic.hierarchy.core.populators.GroupTypePopulator;
import se.unlogic.hierarchy.core.populators.UserQueryPopulator;
import se.unlogic.hierarchy.core.populators.UserTypePopulator;
import se.unlogic.standardutils.collections.CollectionUtils;
import se.unlogic.standardutils.populators.BeanStringPopulator;
import se.unlogic.standardutils.populators.QueryParameterPopulator;

public abstract class BaseUserProviderModule<UserType extends BaseUser> extends AnnotatedMutableUserProviderModule<UserType> {

	public BaseUserProviderModule(Class<UserType> userClass) {

		super(userClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isProviderFor(User user) {

		if (userClass.equals(user.getClass()) && ((UserType) user).getProviderID() != null && ((UserType) user).getProviderID().equals(this.moduleDescriptor.getModuleID())) {

			return true;
		}

		return false;
	}

	@Override
	protected UserType setupUser(UserType user, boolean setupAttributes) {

		if (user != null) {

			user.setProviderID(this.moduleDescriptor.getModuleID());
		}

		return user;
	}

	@Override
	protected List<UserType> setupUsers(List<UserType> users, boolean setupAttributes) {

		if (users != null) {

			for (UserType user : users) {

				user.setProviderID(this.moduleDescriptor.getModuleID());
			}
		}

		return users;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<BeanStringPopulator<?>> getBeanStringPopulators() {

		return CollectionUtils.getList((BeanStringPopulator<?>) new UserTypePopulator(systemInterface.getUserHandler(), false, false), (BeanStringPopulator<?>) new GroupTypePopulator(systemInterface.getGroupHandler(), true));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<QueryParameterPopulator<?>> getQueryParameterPopulators() {

		return CollectionUtils.getList((QueryParameterPopulator<?>) UserQueryPopulator.POPULATOR, (QueryParameterPopulator<?>) GroupQueryPopulator.POPULATOR);
	}
}
