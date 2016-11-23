<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="SimpleUserProviderFormTemplates.xsl"/>
	
	<xsl:variable name="i18n.username" select="'Username'"/>
	<xsl:variable name="i18n.password" select="'Password'"/>
	<xsl:variable name="i18n.firstname" select="'Firstname'"/>
	<xsl:variable name="i18n.lastname" select="'Lastname'"/>
	<xsl:variable name="i18n.emailAddress" select="'Email address'"/>
	<xsl:variable name="i18n.enabled" select="'Enabled'"/>
	<xsl:variable name="i18n.administrator" select="'Administrator'"/>
	
	<xsl:variable name="i18n.accountCreated" select="'Account created'"/>
	<xsl:variable name="i18n.lastLogin" select="'Last login'"/>
	
	<xsl:variable name="i18n.groups" select="'Groups'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'You must fill in the field'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Incorrect format in field'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'Too short content in field '"/>
	<xsl:variable name="i18n.validation.tooLong" select="'Too long content in field'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Unknown error in field'"/>
	<xsl:variable name="i18n.userToUpdateNotFound" select="'User not found'"/>
	<xsl:variable name="i18n.userToRemoveNotFound" select="'User not found'"/>
	
	<xsl:variable name="i18n.unknownFault" select="'An unknown fault has occurred'"/>
	
	<xsl:variable name="i18n.usernameAlreadyTaken">There's already an account associated with this username</xsl:variable>
	<xsl:variable name="i18n.emailAlreadyTaken">There's already an account associated with this email address</xsl:variable>
	<xsl:variable name="i18n.passwordConfirmationMissMatch">The passwords don't match</xsl:variable>
	<xsl:variable name="i18n.unableToAddUsers">It's not possible to add users at the moment</xsl:variable>
	
	<xsl:variable name="i18n.passwordConfirmation">Confirm password</xsl:variable>
	
	<xsl:variable name="i18n.requestedUserNotFound">The requsted user could not be found</xsl:variable>
	
	<xsl:variable name="i18n.attributes">Attributes</xsl:variable>
	<xsl:variable name="i18n.name">Name</xsl:variable>
	<xsl:variable name="i18n.value">Value</xsl:variable>
	<xsl:variable name="i18n.changePassword">Change password</xsl:variable>
</xsl:stylesheet>
