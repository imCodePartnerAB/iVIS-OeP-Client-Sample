<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="SimpleUserProviderFormTemplates.xsl"/>
		
	<xsl:variable name="i18n.username" select="'Användarnamn'"/>
	<xsl:variable name="i18n.password" select="'Lösenord'"/>
	<xsl:variable name="i18n.firstname" select="'Förnamn'"/>
	<xsl:variable name="i18n.lastname" select="'Efternamn'"/>
	<xsl:variable name="i18n.emailAddress" select="'E-post adress'"/>
	<xsl:variable name="i18n.enabled" select="'Aktiverad'"/>
	<xsl:variable name="i18n.administrator" select="'Administratör'"/>
	
	<xsl:variable name="i18n.accountCreated" select="'Kontot skapat'"/>
	<xsl:variable name="i18n.lastLogin" select="'Senaste inloggning'"/>
	
	<xsl:variable name="i18n.groups" select="'Grupper'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'Du måste fylla i fältet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format på fältet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'För kort innehåll i fältet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'För långt innehåll i fältet'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Okänt fel på fältet'"/>
	<xsl:variable name="i18n.userToUpdateNotFound" select="'Användaren du försöker uppdatera hittades inte'"/>
	<xsl:variable name="i18n.userToRemoveNotFound" select="'Användaren du försöker ta bort hittades inte'"/>
	
	<xsl:variable name="i18n.unknownFault" select="'Ett okänt fel har uppstått'"/>
	
	<xsl:variable name="i18n.usernameAlreadyTaken">Användarnamnet används redan av en annan användare</xsl:variable>
	<xsl:variable name="i18n.emailAlreadyTaken">E-post adressen används redan av en annan användare</xsl:variable>
	<xsl:variable name="i18n.passwordConfirmationMissMatch">Lösenorden stämmer inte överens</xsl:variable>
	<xsl:variable name="i18n.unableToAddUsers">Det går för närvarande inte att lägga till användare i systemet</xsl:variable>
	
	<xsl:variable name="i18n.passwordConfirmation">Bekräfta lösenord</xsl:variable>
	
	<xsl:variable name="i18n.requestedUserNotFound">Den begärda användaren hittades inte</xsl:variable>
	
	<xsl:variable name="i18n.attributes">Attribut</xsl:variable>
	<xsl:variable name="i18n.name">Namn</xsl:variable>
	<xsl:variable name="i18n.value">Värde</xsl:variable>
	<xsl:variable name="i18n.changePassword">Ändra lösenord</xsl:variable>
</xsl:stylesheet>
