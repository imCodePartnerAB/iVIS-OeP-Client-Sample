<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="classpath://com/nordicpeak/flowengine/queries/common/xsl/QueryCommon.sv.xsl"/>
	<xsl:include href="OrganizationDetailQueryTemplates.xsl"/>
	
	<xsl:variable name="i18n.Name">F�retagets namn</xsl:variable>
	<xsl:variable name="i18n.OrganizationNumber">Organisationsnummer</xsl:variable>
	<xsl:variable name="i18n.Address">Adress</xsl:variable>
	<xsl:variable name="i18n.ZipCode">Postnummer</xsl:variable>
	<xsl:variable name="i18n.PostalAddress">Ort</xsl:variable>
	<xsl:variable name="i18n.ContactPerson">Kontaktperson</xsl:variable>
	<xsl:variable name="i18n.Firstname">F�rnamn</xsl:variable>
	<xsl:variable name="i18n.Lastname">Efternamn</xsl:variable>
	<xsl:variable name="i18n.FirstnameAndLastname">F�r- &amp; Efternamn</xsl:variable>
	<xsl:variable name="i18n.MobilePhone">Mobiltelefon</xsl:variable>
	<xsl:variable name="i18n.Email">E-postadress</xsl:variable>
	<xsl:variable name="i18n.Phone">Telefon</xsl:variable>
	
	<xsl:variable name="i18n.ChooseContactChannels">Kontaktv�gar</xsl:variable>
	
	<xsl:variable name="i18n.AllowContactBySMS">Kontakta mig via SMS</xsl:variable>
	<xsl:variable name="i18n.ContactBySMS">SMS</xsl:variable>
	<xsl:variable name="i18n.ContactByEmail">E-post</xsl:variable>
	
	<xsl:variable name="i18n.And">&amp;</xsl:variable>
	
	<xsl:variable name="i18n.ChooseOrganization">V�lj f�retag</xsl:variable>
	<xsl:variable name="i18n.OrganizationDescription">V�lj ett av din befintliga f�retag eller registrera ett nytt</xsl:variable>
	<xsl:variable name="i18n.NewOrganization">Nytt f�retag</xsl:variable>
	
	<xsl:variable name="i18n.AddToMyOrganizations">L�gg till det h�r f�retagets uppgifter under mina f�retag</xsl:variable>
	<xsl:variable name="i18n.UpdateToMyOrganizations">Uppdatera f�retagets uppgifter under mina f�retag</xsl:variable>
	
	<xsl:variable name="i18n.UnableToPersistOrganization">Det gick inte att spara f�retagets uppgifter till mina f�retag. F�rs�k igen</xsl:variable>
	
	<xsl:variable name="i18n.NameExists">Du har redan registrerat ett f�retag med det angivna namnet</xsl:variable>
	<xsl:variable name="i18n.OrganizationNumberExists">Du har redan registrerat ett f�retag med det angivna organisationsnumret</xsl:variable>
	
	<xsl:variable name="i18n.RequiredField">Det h�r f�ltet �r obligatoriskt!</xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part1">Inneh�llet i det h�r f�ltet �r </xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part2"> tecken vilket �verskrider maxgr�nsen p� </xsl:variable>
	<xsl:variable name="i18n.TooLongFieldContent.part3"> tecken!</xsl:variable>
	<xsl:variable name="i18n.InvalidFormat">Felaktigt format p� f�ltet</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt valideringsfel har uppst�tt!</xsl:variable>
	<xsl:variable name="i18n.NoContactChannelChoosen">Du m�ste v�lja minst ett kontakts�tt!</xsl:variable>
	
</xsl:stylesheet>
