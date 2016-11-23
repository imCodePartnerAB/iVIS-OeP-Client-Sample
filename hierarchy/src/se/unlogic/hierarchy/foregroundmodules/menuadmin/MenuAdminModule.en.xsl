<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="MenuAdminModuleTemplates.xsl" />
	
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.en.xsl"/>

	<!-- Naming template.mode.field.type -->

	<xsl:variable name="expandAll" select="'Expand all'" />
	<xsl:variable name="collapseAll" select="'Collapse all'" />
	<xsl:variable name="addMenuInSection" select="'Add new menu in section'" />
	<xsl:variable name="moveMenu" select="'Move menu'" />
	<xsl:variable name="moveMenuInstruction" select="'Click target section'" />
	<xsl:variable name="editMenu" select="'Edit menu'" />
	<xsl:variable name="removeMenu" select="'Remove menu'" />
	<xsl:variable name="name" select="'Name'" />
	<xsl:variable name="description" select="'Description'" />
	<xsl:variable name="type" select="'Type'" />
	<xsl:variable name="whitespace" select="'Whitespace'" />
	<xsl:variable name="virtualMenu" select="'Virtual menu'" />
	<xsl:variable name="regularMenu" select="'Regular menu'" />
	<xsl:variable name="heading" select="'Heading'" />
	<xsl:variable name="section" select="'Section'" />
	<xsl:variable name="parameters" select="'Parameter'" />
	<xsl:variable name="address" select="'Address'" />
	<xsl:variable name="sortMenuInSection" select="'Sorting of menues in section'" />
	<xsl:variable name="noMenuesFound" select="'No menues found'" />
	<xsl:variable name="visibleTo" select="'Visible to'" />
	<xsl:variable name="link" select="'Link'" />
	<xsl:variable name="thisSectionIsNotStarted" select="'This section is not started!'" />
	<xsl:variable name="toAnotherSection" select="'to another section'" />
	<xsl:variable name="theModule" select="'The module'" />
	<xsl:variable name="theSection" select="'The section'" />
	<xsl:variable name="theBundle" select="'The bundle'" />
	<xsl:variable name="fromModule" select="'from the module'" />
	<xsl:variable name="unknown" select="'Unknown'" />
	<xsl:variable name="source" select="'Source'" />
	<xsl:variable name="phrase1" select="'Describes the menu origin'" />
	<xsl:variable name="phrase2" select="'The address to which the menu points'" />
	<xsl:variable name="menu" select="'Menu'" />
	<xsl:variable name="phrase3" select="'Menu in bundle'" />
	<xsl:variable name="access" select="'Access'" />
	<xsl:variable name="admins" select="'Administrators'" />
	<xsl:variable name="loggedInUsers" select="'Logged in users'" />
	<xsl:variable name="nonLoggedInUsers" select="'Anonymous users'" />

	<xsl:variable name="validationError.requiredField" select="'You must fill in field'" />
	<xsl:variable name="validationError.invalidFormat" select="'Invalid format in field'" />
	<xsl:variable name="validationError.unknown" select="'Unknown error in field'" />
	<xsl:variable name="validationError.tooLong">Too long content in field</xsl:variable>

	<xsl:variable name="validationError.duplicateAlias" select="'A menu with this alias already exists in this section'" />
	<xsl:variable name="validationError.unknownErrorOccurred" select="'An unknown error has occurred'" />

	<xsl:variable name="add" select="'Add'" />
	<xsl:variable name="users" select="'Users'" />
	<xsl:variable name="groups" select="'Groups'" />
	<xsl:variable name="inSection" select="'in section'" />
	<xsl:variable name="saveChanges" select="'Save changes'" />

</xsl:stylesheet>