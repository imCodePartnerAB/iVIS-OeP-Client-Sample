package se.unlogic.hierarchy.foregroundmodules.userproviders;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import se.unlogic.hierarchy.core.beans.MutableUser;
import se.unlogic.hierarchy.core.beans.User;
import se.unlogic.hierarchy.core.interfaces.MutableUserProvider;
import se.unlogic.standardutils.dao.RelationQuery;
import se.unlogic.standardutils.hash.HashAlgorithms;
import se.unlogic.standardutils.string.StringUtils;

public abstract class AnnotatedMutableUserProviderModule<UserType extends MutableUser> extends AnnotatedUserProviderModule<UserType> implements MutableUserProvider {

	public AnnotatedMutableUserProviderModule(Class<UserType> userClass) {

		super(userClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addUser(User user) throws SQLException {

		UserType castedUser = (UserType) user;

		this.preAdd(castedUser);
		
		userDAO.add(castedUser, getAddRelations());

		this.postAdd(castedUser);
	}

	protected RelationQuery getAddRelations() {

		RelationQuery relationQuery = new RelationQuery();
		
		if(getGroupsRelation() != null){
			
			relationQuery.addRelation(getGroupsRelation());
		}
		
		if(getAttributesRelation() != null){
			
			relationQuery.addRelation(getAttributesRelation());
		}
		
		return relationQuery;
	}

	protected void preAdd(UserType user) {
		
		if(user.getPassword() != null){
			
			user.setPassword(getHashedPassword(user.getPassword()));	
		}		
	}

	protected void postAdd(UserType user) {

		setupUser(user, true);
	}	
	
	@Override
	@SuppressWarnings("unchecked")
	public void updateUser(User user, boolean encryptPassword, boolean updateGroups, boolean updateAttributes) throws SQLException {

		UserType castedUser = (UserType) user;

		this.preUpdate(castedUser, encryptPassword, updateGroups, updateAttributes);
		
		userDAO.update(castedUser, updateGroups, updateAttributes);
		
		this.postUpdate(castedUser, encryptPassword, updateGroups, updateAttributes);
	}
	
	protected void preUpdate(UserType user, boolean encryptPassword, boolean updateGroups, boolean updateAttributes) {
		
		if (encryptPassword) {
			user.setPassword(getHashedPassword(user.getPassword()));
		}
	}
	
	protected void postUpdate(UserType user, boolean encryptPassword, boolean updateGroups, boolean updateAttributes) {}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteUser(User user) throws SQLException {

		userDAO.delete((UserType) user);
	}

	@Override
	public boolean canAddUserClass(Class<? extends User> userClass) {

		return this.userClass.equals(userClass);
	}

	@Override
	protected void createDAOs(DataSource dataSource) throws Exception {

		super.createDAOs(dataSource);

		fixPasswordHashLengths();
	}

	protected void fixPasswordHashLengths() throws SQLException {

		int length;

		//Check and fix password hash lengths to avoid problems due to a bug in old versions of StandardUtils
		if(passwordAlgorithm.equals(HashAlgorithms.SHA1)){

			length = 40;

		}else if(passwordAlgorithm.equals(HashAlgorithms.MD5)){

			length = 32;

		}else{

			return;
		}

		List<UserType> users = this.userDAO.getUsersByPasswordHashLength(length);

		if(users != null){

			for(UserType user : users){

				String password = user.getPassword();

				user.setPassword(StringUtils.repeatString("0", length - password.length()) + password);
			}

			log.info("Correcting password hash length for " + users.size() + " users...");

			this.userDAO.update(users, null);
		}
	}
}
