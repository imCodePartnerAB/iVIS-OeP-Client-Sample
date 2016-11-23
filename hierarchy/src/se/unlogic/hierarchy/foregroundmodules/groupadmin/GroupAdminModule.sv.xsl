<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="GroupAdminModuleTemplates.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.sv.xsl"/>

	<xsl:variable name="noGroupsFound" select="'Inga grupper hittades'"/>
	<xsl:variable name="addGroup" select="'L�gg till grupp'"/>
	
	<xsl:variable name="name" select="'Namn'"/>
	<xsl:variable name="value" select="'V�rde'"/>
	<xsl:variable name="description" select="'Beskrivning'"/>
	<xsl:variable name="enabled" select="'Aktiverad'"/>
	<xsl:variable name="updateGroup" select="'Redigera grupp'"/>
	<xsl:variable name="removeGroup" select="'Ta bort grupp'"/>
	<xsl:variable name="saveChanges" select="'Spara �ndringar'"/>
	<xsl:variable name="members" select="'Medlemmar'"/>
	<xsl:variable name="validation.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="validation.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="validation.unknownError" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="validation.tooShort" select="'F�r kort inneh�ll i f�ltet'"/>
	<xsl:variable name="validation.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'"/>		
	<xsl:variable name="groupToUpdateNotFound" select="'Gruppen du f�rs�ker uppdatera hittades inte'"/>
	<xsl:variable name="groupToRemoveNotFound" select="'Gruppen du f�rs�ker ta bort hittades inte'"/>
	<xsl:variable name="unknownFault" select="'Ett ok�nt fel har uppst�tt'"/>
	<xsl:variable name="editGroup">Uppdatera gruppen</xsl:variable>
	<xsl:variable name="deleteGroup">Ta bort gruppen</xsl:variable>
	<xsl:variable name="limitedEditGroup">V�lj medlemmar i gruppen</xsl:variable>
	<xsl:variable name="groupCannotBeDeleted">Den h�r gruppen kan inte tas bort</xsl:variable>
	<xsl:variable name="userID">dummy</xsl:variable>
	<xsl:variable name="viewGroup">Visa gruppen</xsl:variable>
	<xsl:variable name="groupToShowNotFound">Den beg�rda gruppen hittades inte</xsl:variable>
	<xsl:variable name="groupCannotBeEditedInfo">Den h�r gruppen kan inte uppdateras, men du kan v�lja vilka anv�ndare som ska vara medlem i den.</xsl:variable>
	
	<xsl:variable name="attributes">Attribut</xsl:variable>
</xsl:stylesheet>
