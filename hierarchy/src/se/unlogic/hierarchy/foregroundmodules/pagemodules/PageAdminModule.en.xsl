<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="PageAdminModuleTemplates.xsl" />
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.en.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="expandAll" select="'Expand all'" />
	<xsl:variable name="collapseAll" select="'Collapse all'" />
	<xsl:variable name="addPageInSection" select="'Add new page in section'" />
	<xsl:variable name="varning.noPageviewModuleInSection" select="'Warning! There is no page view module in this section'" />
	<xsl:variable name="movePage" select="'Move page'"/>
	<xsl:variable name="movePageInstruction" select="'Click target section'"/>
	<xsl:variable name="copyPage" select="'Copy page'"/>
	<xsl:variable name="copyPageInstruction" select="'Click target section'"/>
	<xsl:variable name="editPage" select="'Edit page'"/>
	<xsl:variable name="deletePage" select="'Remove page'"/>
	<xsl:variable name="editingOfPage" select="'Editing of page'"/>
	<xsl:variable name="name" select="'Name'"/>
	<xsl:variable name="description" select="'Description'"/>
	<xsl:variable name="alias" select="'Alias'"/>
	<xsl:variable name="content" select="'Content'"/>
	<xsl:variable name="additionalSettings" select="'Additional settings'"/>
	<xsl:variable name="activatePage" select="'Activate page'"/>
	<xsl:variable name="showInMenu" select="'Show page in menu'"/>
	<xsl:variable name="showBreadCrumb" select="'Show breadcrumb'"/>
	<xsl:variable name="access" select="'Access'"/>
	<xsl:variable name="admins" select="'Administrators'"/>
	<xsl:variable name="loggedInUsers" select="'Logged in users'"/>
	<xsl:variable name="nonLoggedInUsers" select="'Anonymous users'"/>
	<xsl:variable name="validationError.requiredField" select="'You must fill in field'"/>
	<xsl:variable name="validationError.invalidFormat" select="'Invalid format in field'"/>
	<xsl:variable name="validationError.unknown" select="'Unknown error in field'"/>
	<xsl:variable name="validationError.duplicateAlias" select="'A page with this alias already exists in this section'"/>
	<xsl:variable name="validationError.unknownErrorOccurred" select="'An unknown error has occurred'"/>
	<xsl:variable name="addPage" select="'Add page'"/>
	<xsl:variable name="pagePreview" select="'Preview of page'"/>
	<xsl:variable name="showPageOutsideAdminView" select="'Show page outside the admin interface'"/>
	<xsl:variable name="users" select="'Users'"/>
	<xsl:variable name="groups" select="'Groups'"/>
	<xsl:variable name="inSection" select="'in section'"/>
	<xsl:variable name="saveChanges" select="'Save changes'"/>
	
	
</xsl:stylesheet>