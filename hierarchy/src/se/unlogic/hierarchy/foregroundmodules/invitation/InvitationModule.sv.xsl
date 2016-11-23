<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="InvitationModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.Firstname">Förnamn</xsl:variable>
	<xsl:variable name="i18n.Lastname">Efternamn</xsl:variable>
	<xsl:variable name="i18n.Email">E-post</xsl:variable>
	<xsl:variable name="i18n.Username">Användarnamn</xsl:variable>
	<xsl:variable name="i18n.Password">Lösenord</xsl:variable>
	<xsl:variable name="i18n.CreateAccount">Skapa konto</xsl:variable>
	
	<xsl:variable name="i18n.username">användarnamn</xsl:variable>
	<xsl:variable name="i18n.password">lösenord</xsl:variable>
	
			
	<xsl:variable name="i18n.ValidationError.RequiredField">Du måste fylla i fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFieldValueFormat">Felaktigt format på värdet i fältet</xsl:variable>
	

	<xsl:variable name="i18n.ValidationError.TooLong">För långt värde på fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.TooShort">För kort värde på fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UsernameAlreadyTaken">Användarnamnet är upptaget</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UnknownError">Ett okänt fel har uppstått</xsl:variable>

</xsl:stylesheet>
