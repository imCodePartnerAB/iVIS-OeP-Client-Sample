<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="UserAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="java.addUserBreadCrumbText">Add user</xsl:variable>
	<xsl:variable name="java.updateUserBreadCrumbText">Update: </xsl:variable>
	<xsl:variable name="java.listUserTypesBreadCrumbText">Select user type</xsl:variable>
		
	<xsl:variable name="i18n.noUsersFound" select="'No users found'"/>
	<xsl:variable name="i18n.addUser" select="'Add user'"/>
	
	<xsl:variable name="i18n.removeUser" select="'Remove user'"/>
	<xsl:variable name="i18n.username" select="'Username'"/>
	<xsl:variable name="i18n.password" select="'Password'"/>
	<xsl:variable name="i18n.firstname" select="'Firstname'"/>
	<xsl:variable name="i18n.lastname" select="'Lastname'"/>
	<xsl:variable name="i18n.emailAddress" select="'Email address'"/>
	<xsl:variable name="i18n.enabled" select="'Enabled'"/>
	<xsl:variable name="i18n.administrator" select="'Administrator'"/>
	<xsl:variable name="i18n.updateUser" select="'Update user'"/>
	
	<xsl:variable name="i18n.accountCreated" select="'Account created'"/>
	<xsl:variable name="i18n.lastLogin" select="'Last login'"/>
	<xsl:variable name="i18n.saveChanges" select="'Save changes'"/>
	<xsl:variable name="i18n.groups" select="'Groups'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'You must fill in the field'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Incorrect format in field'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'Too short content in field '"/>
	<xsl:variable name="i18n.validation.tooLong" select="'Too long content in field'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Unknown error in field'"/>
	<xsl:variable name="i18n.userToUpdateNotFound" select="'User not found'"/>
	<xsl:variable name="i18n.userToRemoveNotFound" select="'User not found'"/>
	
	<xsl:variable name="i18n.unknownFault" select="'An unknown fault has occurred'"/>
	<xsl:variable name="i18n.statistics">Statistics</xsl:variable>
	<xsl:variable name="i18n.totalUserCount">Users</xsl:variable>
	<xsl:variable name="i18n.disabledUserCount">Disabled users</xsl:variable>
	<xsl:variable name="i18n.totalGroupCount">Groups</xsl:variable>
	<xsl:variable name="i18n.disabledGroupCount">Disabled users</xsl:variable>
	<xsl:variable name="i18n.userProviderCount">User providers</xsl:variable>
	
	<xsl:variable name="i18n.usernameAlreadyTaken">There's already an account associated with this username</xsl:variable>
	<xsl:variable name="i18n.emailAlreadyTaken">There's already an account associated with this email address</xsl:variable>
	<xsl:variable name="i18n.passwordConfirmationMissMatch">The passwords don't match</xsl:variable>
	<xsl:variable name="i18n.unableToAddUsers">It's not possible to add users at the moment</xsl:variable>
	<xsl:variable name="i18n.viewUser">View user</xsl:variable>
	<xsl:variable name="i18n.editUser">Update user</xsl:variable>
	<xsl:variable name="i18n.deleteUser">Delete user</xsl:variable>
	
	
	<xsl:variable name="i18n.requestedUserNotFound">The requsted user could not be found</xsl:variable>
	<xsl:variable name="i18n.userCannotBeUpdated">This user cannot be updated</xsl:variable>
	<xsl:variable name="i18n.userCannotBeDeleted">This user cannot be deleted</xsl:variable>
	<xsl:variable name="i18n.firstnameIndex">Firstname index</xsl:variable>
	<xsl:variable name="i18n.lastnameIndex">Lastname index</xsl:variable>
	<xsl:variable name="i18n.usernameIndex">Username index</xsl:variable>
	<xsl:variable name="i18n.emailIndex">E-mail index</xsl:variable>
	<xsl:variable name="i18n.unknownIndex">Index</xsl:variable>
	
	<xsl:variable name="i18n.userUpdatedLocked">You don't have access to update this user</xsl:variable>
	<xsl:variable name="i18n.userDeletedLocked">You don't have access to delete this user</xsl:variable>
	<xsl:variable name="i18n.downloadEmailList">Download email list</xsl:variable>
	<xsl:variable name="i18n.attributes">Attributes</xsl:variable>
	<xsl:variable name="i18n.name">Name</xsl:variable>
	<xsl:variable name="i18n.value">Value</xsl:variable>
	<xsl:variable name="i18n.SelectUserType">Select user type</xsl:variable>
	<xsl:variable name="i18n.SelectUserType.Description">Select the type of user you want to add.</xsl:variable>
	
	<xsl:variable name="i18n.NoFormAddableUserTypesAvailable">Unable to add users, no user types available.</xsl:variable>
	<xsl:variable name="i18n.RequestedUserTypeNotFound">Requested user type not found</xsl:variable>
	<xsl:variable name="i18n.UpdateFailedUserNotUpdatable">The requested user is not updatable</xsl:variable>
	<xsl:variable name="i18n.DeleteFailedException">An error occured while deleting the requested user</xsl:variable>
	<xsl:variable name="i18n.switchToUser">Login as user</xsl:variable>
</xsl:stylesheet>
