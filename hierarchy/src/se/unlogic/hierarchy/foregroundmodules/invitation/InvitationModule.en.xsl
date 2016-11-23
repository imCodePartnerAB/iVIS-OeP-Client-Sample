<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="InvitationModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.Firstname">Firstname</xsl:variable>
	<xsl:variable name="i18n.Lastname">Lastname</xsl:variable>
	<xsl:variable name="i18n.Email">Email</xsl:variable>
	<xsl:variable name="i18n.Username">Username</xsl:variable>
	<xsl:variable name="i18n.Password">Password</xsl:variable>
	<xsl:variable name="i18n.CreateAccount">Create Account</xsl:variable>
	
	<xsl:variable name="i18n.username">username</xsl:variable>
	<xsl:variable name="i18n.password">password</xsl:variable>
	
			
	<xsl:variable name="i18n.ValidationError.RequiredField">You need to fill in the field</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFieldValueFormat">Invalid value in field</xsl:variable>

	<xsl:variable name="i18n.ValidationError.Message.UsernameAlreadyTaken">The username you have selected has already been taken by another user</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UnknownError">An unknown error occurred</xsl:variable>

	<xsl:variable name="i18n.ValidationError.TooShort">Too short content in field</xsl:variable>
	<xsl:variable name="i18n.ValidationError.TooLong">Too long content in field</xsl:variable>	
</xsl:stylesheet>
