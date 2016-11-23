<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="UserAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="java.addUserBreadCrumbText">L�gg till anv�ndare</xsl:variable>
	<xsl:variable name="java.updateUserBreadCrumbText">Uppdatera: </xsl:variable>
	<xsl:variable name="java.listUserTypesBreadCrumbText">V�lj anv�ndartyp</xsl:variable>
	
	<xsl:variable name="i18n.noUsersFound" select="'Inga anv�ndare hittades'"/>
	<xsl:variable name="i18n.addUser" select="'L�gg till anv�ndare'"/>
	
	<xsl:variable name="i18n.removeUser" select="'Ta bort anv�ndare'"/>
	<xsl:variable name="i18n.username" select="'Anv�ndarnamn'"/>
	<xsl:variable name="i18n.password" select="'L�senord'"/>
	<xsl:variable name="i18n.firstname" select="'F�rnamn'"/>
	<xsl:variable name="i18n.lastname" select="'Efternamn'"/>
	<xsl:variable name="i18n.emailAddress" select="'E-post adress'"/>
	<xsl:variable name="i18n.enabled" select="'Aktiverad'"/>
	<xsl:variable name="i18n.administrator" select="'Administrat�r'"/>
	<xsl:variable name="i18n.updateUser" select="'Uppdatera anv�ndaren'"/>
	
	<xsl:variable name="i18n.accountCreated" select="'Kontot skapat'"/>
	<xsl:variable name="i18n.lastLogin" select="'Senaste inloggning'"/>
	<xsl:variable name="i18n.saveChanges" select="'Spara �ndringar'"/>
	<xsl:variable name="i18n.groups" select="'Grupper'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'F�r kort inneh�ll i f�ltet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="i18n.userToUpdateNotFound" select="'Anv�ndaren du f�rs�ker uppdatera hittades inte'"/>
	<xsl:variable name="i18n.userToRemoveNotFound" select="'Anv�ndaren du f�rs�ker ta bort hittades inte'"/>
	
	<xsl:variable name="i18n.unknownFault" select="'Ett ok�nt fel har uppst�tt'"/>
	<xsl:variable name="i18n.statistics">Statistik</xsl:variable>
	<xsl:variable name="i18n.totalUserCount">Antal anv�ndare</xsl:variable>
	<xsl:variable name="i18n.disabledUserCount">Antal avaktiverade anv�ndare</xsl:variable>
	<xsl:variable name="i18n.totalGroupCount">Antal grupper</xsl:variable>
	<xsl:variable name="i18n.disabledGroupCount">Antal avaktiverade grupper</xsl:variable>
	<xsl:variable name="i18n.userProviderCount">K�llor</xsl:variable>
	
	<xsl:variable name="i18n.usernameAlreadyTaken">Anv�ndarnamnet anv�nds redan av en annan anv�ndare</xsl:variable>
	<xsl:variable name="i18n.emailAlreadyTaken">E-post adressen anv�nds redan av en annan anv�ndare</xsl:variable>
	<xsl:variable name="i18n.passwordConfirmationMissMatch">L�senorden st�mmer inte �verens</xsl:variable>
	<xsl:variable name="i18n.unableToAddUsers">Det g�r f�r n�rvarande inte att l�gga till anv�ndare i systemet</xsl:variable>
	<xsl:variable name="i18n.viewUser">Visa anv�ndaren</xsl:variable>
	<xsl:variable name="i18n.editUser">Uppdatera anv�ndaren</xsl:variable>
	<xsl:variable name="i18n.deleteUser">Ta bort anv�ndaren</xsl:variable>
	
	
	<xsl:variable name="i18n.requestedUserNotFound">Den beg�rda anv�ndaren hittades inte</xsl:variable>
	<xsl:variable name="i18n.userCannotBeUpdated">Den anv�ndaren kan inte uppdateras</xsl:variable>
	<xsl:variable name="i18n.userCannotBeDeleted">Den h�r anv�ndaren kan inte tas bort</xsl:variable>	
	<xsl:variable name="i18n.firstnameIndex">Index f�rnamn</xsl:variable>
	<xsl:variable name="i18n.lastnameIndex">Index efternamn</xsl:variable>
	<xsl:variable name="i18n.usernameIndex">Index anv�ndarnamn</xsl:variable>
	<xsl:variable name="i18n.emailIndex">Index e-post</xsl:variable>
	<xsl:variable name="i18n.unknownIndex">Index</xsl:variable>
	
	<xsl:variable name="i18n.userUpdatedLocked">Du har inte beh�righet att uppdatera den h�r anv�ndaren</xsl:variable>
	<xsl:variable name="i18n.userDeletedLocked">Du har inte beh�righet att ta bort den h�r anv�ndaren</xsl:variable>

	<xsl:variable name="i18n.downloadEmailList">Ladda hem e-post lista</xsl:variable>
	<xsl:variable name="i18n.attributes">Attribut</xsl:variable>
	<xsl:variable name="i18n.name">Namn</xsl:variable>
	<xsl:variable name="i18n.value">V�rde</xsl:variable>
	<xsl:variable name="i18n.SelectUserType">V�lj anv�ndartyp</xsl:variable>
	<xsl:variable name="i18n.SelectUserType.Description">V�lj vilken typ av anv�ndare du vill l�gga till.</xsl:variable>
	
	<xsl:variable name="i18n.NoFormAddableUserTypesAvailable">Det g�r inte att l�gga till anv�ndare, inga anv�ndartyper hittades</xsl:variable>
	<xsl:variable name="i18n.RequestedUserTypeNotFound">Den beg�rda anv�ndartypen gick inte att hitta</xsl:variable>
	<xsl:variable name="i18n.UpdateFailedUserNotUpdatable">Den valda anv�ndaren g�r inte att uppdatera</xsl:variable>
	<xsl:variable name="i18n.DeleteFailedException">Ett fel uppstod n�r anv�ndaren skulle tas bort</xsl:variable>
	<xsl:variable name="i18n.switchToUser">Logga in som anv�ndaren</xsl:variable>
</xsl:stylesheet>
