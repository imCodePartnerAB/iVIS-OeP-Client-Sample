<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="UserAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="java.addUserBreadCrumbText">Lägg till användare</xsl:variable>
	<xsl:variable name="java.updateUserBreadCrumbText">Uppdatera: </xsl:variable>
	<xsl:variable name="java.listUserTypesBreadCrumbText">Välj användartyp</xsl:variable>
	
	<xsl:variable name="i18n.noUsersFound" select="'Inga användare hittades'"/>
	<xsl:variable name="i18n.addUser" select="'Lägg till användare'"/>
	
	<xsl:variable name="i18n.removeUser" select="'Ta bort användare'"/>
	<xsl:variable name="i18n.username" select="'Användarnamn'"/>
	<xsl:variable name="i18n.password" select="'Lösenord'"/>
	<xsl:variable name="i18n.firstname" select="'Förnamn'"/>
	<xsl:variable name="i18n.lastname" select="'Efternamn'"/>
	<xsl:variable name="i18n.emailAddress" select="'E-post adress'"/>
	<xsl:variable name="i18n.enabled" select="'Aktiverad'"/>
	<xsl:variable name="i18n.administrator" select="'Administratör'"/>
	<xsl:variable name="i18n.updateUser" select="'Uppdatera användaren'"/>
	
	<xsl:variable name="i18n.accountCreated" select="'Kontot skapat'"/>
	<xsl:variable name="i18n.lastLogin" select="'Senaste inloggning'"/>
	<xsl:variable name="i18n.saveChanges" select="'Spara ändringar'"/>
	<xsl:variable name="i18n.groups" select="'Grupper'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'Du måste fylla i fältet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format på fältet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'För kort innehåll i fältet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'För långt innehåll i fältet'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Okänt fel på fältet'"/>
	<xsl:variable name="i18n.userToUpdateNotFound" select="'Användaren du försöker uppdatera hittades inte'"/>
	<xsl:variable name="i18n.userToRemoveNotFound" select="'Användaren du försöker ta bort hittades inte'"/>
	
	<xsl:variable name="i18n.unknownFault" select="'Ett okänt fel har uppstått'"/>
	<xsl:variable name="i18n.statistics">Statistik</xsl:variable>
	<xsl:variable name="i18n.totalUserCount">Antal användare</xsl:variable>
	<xsl:variable name="i18n.disabledUserCount">Antal avaktiverade användare</xsl:variable>
	<xsl:variable name="i18n.totalGroupCount">Antal grupper</xsl:variable>
	<xsl:variable name="i18n.disabledGroupCount">Antal avaktiverade grupper</xsl:variable>
	<xsl:variable name="i18n.userProviderCount">Källor</xsl:variable>
	
	<xsl:variable name="i18n.usernameAlreadyTaken">Användarnamnet används redan av en annan användare</xsl:variable>
	<xsl:variable name="i18n.emailAlreadyTaken">E-post adressen används redan av en annan användare</xsl:variable>
	<xsl:variable name="i18n.passwordConfirmationMissMatch">Lösenorden stämmer inte överens</xsl:variable>
	<xsl:variable name="i18n.unableToAddUsers">Det går för närvarande inte att lägga till användare i systemet</xsl:variable>
	<xsl:variable name="i18n.viewUser">Visa användaren</xsl:variable>
	<xsl:variable name="i18n.editUser">Uppdatera användaren</xsl:variable>
	<xsl:variable name="i18n.deleteUser">Ta bort användaren</xsl:variable>
	
	
	<xsl:variable name="i18n.requestedUserNotFound">Den begärda användaren hittades inte</xsl:variable>
	<xsl:variable name="i18n.userCannotBeUpdated">Den användaren kan inte uppdateras</xsl:variable>
	<xsl:variable name="i18n.userCannotBeDeleted">Den här användaren kan inte tas bort</xsl:variable>	
	<xsl:variable name="i18n.firstnameIndex">Index förnamn</xsl:variable>
	<xsl:variable name="i18n.lastnameIndex">Index efternamn</xsl:variable>
	<xsl:variable name="i18n.usernameIndex">Index användarnamn</xsl:variable>
	<xsl:variable name="i18n.emailIndex">Index e-post</xsl:variable>
	<xsl:variable name="i18n.unknownIndex">Index</xsl:variable>
	
	<xsl:variable name="i18n.userUpdatedLocked">Du har inte behörighet att uppdatera den här användaren</xsl:variable>
	<xsl:variable name="i18n.userDeletedLocked">Du har inte behörighet att ta bort den här användaren</xsl:variable>

	<xsl:variable name="i18n.downloadEmailList">Ladda hem e-post lista</xsl:variable>
	<xsl:variable name="i18n.attributes">Attribut</xsl:variable>
	<xsl:variable name="i18n.name">Namn</xsl:variable>
	<xsl:variable name="i18n.value">Värde</xsl:variable>
	<xsl:variable name="i18n.SelectUserType">Välj användartyp</xsl:variable>
	<xsl:variable name="i18n.SelectUserType.Description">Välj vilken typ av användare du vill lägga till.</xsl:variable>
	
	<xsl:variable name="i18n.NoFormAddableUserTypesAvailable">Det går inte att lägga till användare, inga användartyper hittades</xsl:variable>
	<xsl:variable name="i18n.RequestedUserTypeNotFound">Den begärda användartypen gick inte att hitta</xsl:variable>
	<xsl:variable name="i18n.UpdateFailedUserNotUpdatable">Den valda användaren går inte att uppdatera</xsl:variable>
	<xsl:variable name="i18n.DeleteFailedException">Ett fel uppstod när användaren skulle tas bort</xsl:variable>
	<xsl:variable name="i18n.switchToUser">Logga in som användaren</xsl:variable>
</xsl:stylesheet>
