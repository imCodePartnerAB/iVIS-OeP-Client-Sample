<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="GroupAdminModuleTemplates.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.en.xsl"/>
	
	<xsl:variable name="noGroupsFound" select="'No groups found'"/>
	<xsl:variable name="addGroup" select="'Add group'"/>
	
	<xsl:variable name="name" select="'Name'"/>
	<xsl:variable name="value" select="'Value'"/>
	<xsl:variable name="description" select="'Description'"/>
	<xsl:variable name="enabled" select="'Enabled'"/>
	<xsl:variable name="updateGroup" select="'Edit group'"/>
	<xsl:variable name="removeGroup" select="'Remove group'"/>
	<xsl:variable name="saveChanges" select="'Save changes'"/>
	<xsl:variable name="members" select="'Members'"/>
	<xsl:variable name="validation.requiredField" select="'You must fill in the field'"/>
	<xsl:variable name="validation.invalidFormat" select="'Incorrect format in field'"/>
	<xsl:variable name="validation.unknownError" select="'Unknown error in field'"/>
	<xsl:variable name="validation.tooShort" select="'Too short content in field '"/>
	<xsl:variable name="validation.tooLong" select="'Too long content in field'"/>		
	<xsl:variable name="groupToUpdateNotFound" select="'Group not found'"/>
	<xsl:variable name="groupToRemoveNotFound" select="'Group not found'"/>
	<xsl:variable name="unknownFault" select="'An unknown fault has occurred'"/>

	<xsl:variable name="editGroup">Update group</xsl:variable>
	<xsl:variable name="deleteGroup">Delete group</xsl:variable>
	<xsl:variable name="limitedEditGroup">Select members of this group</xsl:variable>
	<xsl:variable name="groupCannotBeDeleted">This group cannot be deleted</xsl:variable>
	<xsl:variable name="userID">dummy</xsl:variable>
	<xsl:variable name="viewGroup">View group</xsl:variable>
	<xsl:variable name="groupToShowNotFound">The requested group was not found</xsl:variable>
	<xsl:variable name="groupCannotBeEditedInfo">This group cannot be updated but you can select which users who should be members of it.</xsl:variable>
	
	<xsl:variable name="attributes">Attributes</xsl:variable>
</xsl:stylesheet>
