<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="UserProfileModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.username" select="'Anv�ndarnamn'"/>
	<xsl:variable name="i18n.password" select="'L�senord'"/>
	<xsl:variable name="i18n.firstname" select="'F�rnamn'"/>
	<xsl:variable name="i18n.lastname" select="'Efternamn'"/>
	
	
	<xsl:variable name="i18n.validation.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'F�r kort inneh�ll i f�ltet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Ok�nt fel p� f�ltet'"/>
	
	<xsl:variable name="i18n.unknownFault" select="'Ett ok�nt fel har uppst�tt'"/>
	
	<xsl:variable name="i18n.usernameAlreadyTaken">Anv�ndarnamnet anv�nds redan av en annan anv�ndare</xsl:variable>
	<xsl:variable name="i18n.emailAlreadyTaken">E-post adressen anv�nds redan av en annan anv�ndare</xsl:variable>
	<xsl:variable name="i18n.passwordConfirmationMissMatch">L�senorden st�mmer inte �verens</xsl:variable>
	
	<xsl:variable name="i18n.CancelConfirm">�r du s�ker p� att du vill avbryta utan att spara</xsl:variable>
	<xsl:variable name="i18n.UserNotFound">Din anv�ndare hittades inte.</xsl:variable>
	<xsl:variable name="i18n.UserUpdatedMessage">�ndringar sparade.</xsl:variable>
	
	<xsl:variable name="i18n.email">E-postadress</xsl:variable>
	<xsl:variable name="i18n.saveChanges">Spara �ndringar</xsl:variable>
	<xsl:variable name="i18n.ContactDetails">Kontaktuppgifter</xsl:variable>
	
	<xsl:variable name="i18n.citizenIdentifier">Personnummer</xsl:variable>
	<xsl:variable name="i18n.address">Adress</xsl:variable>
	<xsl:variable name="i18n.zipCode">Postnummer</xsl:variable>
	<xsl:variable name="i18n.postalAddress">Ort</xsl:variable>
	<xsl:variable name="i18n.phone">Telefonnummer</xsl:variable>
	<xsl:variable name="i18n.mobilePhone">Mobiltelefon</xsl:variable>
	<xsl:variable name="i18n.SMS">Kontakta mig via SMS</xsl:variable>
</xsl:stylesheet>
