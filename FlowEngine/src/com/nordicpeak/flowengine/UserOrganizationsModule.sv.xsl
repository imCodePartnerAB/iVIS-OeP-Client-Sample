<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="UserOrganizationsModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.validation.requiredField" select="'Du måste fylla i fältet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format på fältet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'För kort innehåll i fältet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'För långt innehåll i fältet'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Okänt fel på fältet'"/>
	<xsl:variable name="i18n.unknownFault" select="'Ett okänt fel har uppstått'"/>
	
	<xsl:variable name="i18n.ListOrganizationDescription.Part1">Du har</xsl:variable>
	<xsl:variable name="i18n.ListOrganizationDescription.Part2">registrerade företag</xsl:variable>
	
	<xsl:variable name="i18n.Organization">Företag</xsl:variable>
	<xsl:variable name="i18n.OrganizationNumber">Organisationsnummer</xsl:variable>
	<xsl:variable name="i18n.AddOrganization">Lägg till företag</xsl:variable>
	<xsl:variable name="i18n.Add">Lägg till</xsl:variable>
	<xsl:variable name="i18n.UpdateOrganization">Ändra företag</xsl:variable>
	<xsl:variable name="i18n.Update">Ändra</xsl:variable>
	<xsl:variable name="i18n.Delete">Ta bort</xsl:variable>
	<xsl:variable name="i18n.DeleteOrganizationConfirm">Är du säker på att du vill ta bort företaget</xsl:variable>
	<xsl:variable name="i18n.Email">E-postadress</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara ändringar</xsl:variable>
	<xsl:variable name="i18n.ContactDetails">Kontaktuppgifter</xsl:variable>
	<xsl:variable name="i18n.Name">Företagets namn</xsl:variable>
	<xsl:variable name="i18n.Address">Adress</xsl:variable>
	<xsl:variable name="i18n.ZipCode">Postnummer</xsl:variable>
	<xsl:variable name="i18n.PostalAddress">Ort</xsl:variable>
	<xsl:variable name="i18n.ContactPerson">Kontaktperson</xsl:variable>
	<xsl:variable name="i18n.Firstname">Förnamn</xsl:variable>
	<xsl:variable name="i18n.Lastname">Efternamn</xsl:variable>
	<xsl:variable name="i18n.Phone">Telefonnummer</xsl:variable>
	<xsl:variable name="i18n.MobilePhone">Mobiltelefon</xsl:variable>
	
	
	
	
	<xsl:variable name="i18n.SMS">Kontakta mig via SMS</xsl:variable>
	<xsl:variable name="i18n.Phone">Telefon</xsl:variable>
	
	<xsl:variable name="i18n.Cancel">Avbryt</xsl:variable>
	<xsl:variable name="i18n.NoOrganizations">Du har inga registrerade företag</xsl:variable>
	
	<xsl:variable name="i18n.CancelConfirm">Är du säker på att du vill avbryta utan att spara</xsl:variable>
	
	<xsl:variable name="i18n.NameExists">Du har redan registrerat ett företag med det angivna namnet</xsl:variable>
	<xsl:variable name="i18n.OrganizationNumberExists">Du har redan registrerat ett företag med det angivna organisationsnumret</xsl:variable>
	<xsl:variable name="i18n.UpdateFailedOrganizationNotFound">Det begärda företaget hittades inte</xsl:variable>
	
</xsl:stylesheet>
