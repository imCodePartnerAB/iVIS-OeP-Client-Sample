<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryCommon.sv.xsl"/>
	<xsl:include href="ContactDetailQueryTemplates.xsl"/>
	
	<xsl:variable name="i18n.Firstname">F�rnamn</xsl:variable>
	<xsl:variable name="i18n.Lastname">Efternamn</xsl:variable>
	<xsl:variable name="i18n.FirstnameAndLastname">F�r- &amp; Efternamn</xsl:variable>
	<xsl:variable name="i18n.Address">Adress</xsl:variable>
	<xsl:variable name="i18n.ZipCode">Postnummer</xsl:variable>
	<xsl:variable name="i18n.PostalAddress">Ort</xsl:variable>
	<xsl:variable name="i18n.MobilePhone">Mobiltelefon</xsl:variable>
	<xsl:variable name="i18n.Email">E-postadress</xsl:variable>
	<xsl:variable name="i18n.Phone">Telefon</xsl:variable>
	
	<xsl:variable name="i18n.ChooseContactChannels">Kontaktv�gar</xsl:variable>
	
	<xsl:variable name="i18n.AllowContactBySMS">Kontakta mig via SMS</xsl:variable>
	<xsl:variable name="i18n.ContactBySMS">SMS</xsl:variable>
	<xsl:variable name="i18n.ContactByEmail">E-post</xsl:variable>
	
	
	<xsl:variable name="i18n.And">&amp;</xsl:variable>
	
	<xsl:variable name="i18n.UpdateMyUserProfile">Uppdatera kontaktuppgifterna under mina uppgifter</xsl:variable>
	
	<xsl:variable name="i18n.EmailAlreadyTaken">E-postadressen anv�nds redan av en annan anv�ndare</xsl:variable>
	<xsl:variable name="i18n.UnableToUpdateUser">Det gick inte att uppdatera dina kontaktuppgifter, f�rs�k igen</xsl:variable>
		
	
	<xsl:variable name="i18n.RequiredField">Det h�r f�ltet �r obligatoriskt!</xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part1">Inneh�llet i det h�r f�ltet �r </xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part2"> tecken vilket �verskrider maxgr�nsen p� </xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part3"> tecken!</xsl:variable>
	<xsl:variable name="i18n.InvalidFormat">Felaktigt format p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt valideringsfel har uppst�tt!</xsl:variable>
	<xsl:variable name="i18n.NoContactChannelChoosen">Du fylla i din e-post address eller fylla i mobiltelefon och v�lja att bli kontaktad via SMS!</xsl:variable>
	
</xsl:stylesheet>
