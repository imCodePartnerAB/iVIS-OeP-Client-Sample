<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="InvitationModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.Firstname">F�rnamn</xsl:variable>
	<xsl:variable name="i18n.Lastname">Efternamn</xsl:variable>
	<xsl:variable name="i18n.Email">E-post</xsl:variable>
	<xsl:variable name="i18n.Username">Anv�ndarnamn</xsl:variable>
	<xsl:variable name="i18n.Password">L�senord</xsl:variable>
	<xsl:variable name="i18n.CreateAccount">Skapa konto</xsl:variable>
	
	<xsl:variable name="i18n.username">anv�ndarnamn</xsl:variable>
	<xsl:variable name="i18n.password">l�senord</xsl:variable>
	
			
	<xsl:variable name="i18n.ValidationError.RequiredField">Du m�ste fylla i f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFieldValueFormat">Felaktigt format p� v�rdet i f�ltet</xsl:variable>
	

	<xsl:variable name="i18n.ValidationError.TooLong">F�r l�ngt v�rde p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.TooShort">F�r kort v�rde p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UsernameAlreadyTaken">Anv�ndarnamnet �r upptaget</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UnknownError">Ett ok�nt fel har uppst�tt</xsl:variable>

</xsl:stylesheet>
