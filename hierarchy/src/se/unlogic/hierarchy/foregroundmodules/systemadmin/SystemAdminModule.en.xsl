<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="SystemAdminModuleTemplates.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.en.xsl"/>
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="addForegroundModuleBreadCrumbText">Add foreground module</xsl:variable>
	<xsl:variable name="updateForegroundModuleBreadCrumbText">Edit foreground module: </xsl:variable>
	<xsl:variable name="copyForegroundModuleBreadCrumbText">Copy foreground module: </xsl:variable>
	<xsl:variable name="moveForegroundModuleBreadCrumbText">Move foreground module: </xsl:variable>
	
	<xsl:variable name="addBackgroundModuleBreadCrumbText">Add background module</xsl:variable>
	<xsl:variable name="updateBackgroundModuleBreadCrumbText">Edit background module: </xsl:variable>
	<xsl:variable name="copyBackgroundModuleBreadCrumbText">Copy background module: </xsl:variable>
	<xsl:variable name="moveBackgroundModuleBreadCrumbText">Move background module: </xsl:variable>	
	
	<xsl:variable name="addFilterModuleBreadCrumbText">Add filter module</xsl:variable>
	<xsl:variable name="updateFilterModuleBreadCrumbText">Update filter module: </xsl:variable>	
	
	<xsl:variable name="addSectionBreadCrumbText">Add section</xsl:variable>
	<xsl:variable name="updateSectionBreadCrumbText">Edit section: </xsl:variable>
	<xsl:variable name="moveSectionBreadCrumbText">Move section: </xsl:variable>
	<xsl:variable name="importModulesBreadCrumbText">Import modules into section: </xsl:variable>
	
	<xsl:variable name="i18n.expandAll" select="'Expand all'"/>
	<xsl:variable name="i18n.collapseAll" select="'Collapse all'"/>
	<xsl:variable name="i18n.startSection" select="'Start section'"/>
	<xsl:variable name="i18n.stopSection" select="'Stop section'"/>
	<xsl:variable name="i18n.addFilterModule" select="'Add filter module'"/>
	<xsl:variable name="i18n.addModuleInSection" select="'Add module in section'"/>
	<xsl:variable name="i18n.addSubSectionInSection" select="'Add subsection in section'"/>
	<xsl:variable name="i18n.addSubSection" select="'Add subsection'"/>
	<xsl:variable name="i18n.moveSection" select="'Move section'"/>
	<xsl:variable name="i18n.editSection" select="'Edit section'"/>
	<xsl:variable name="i18n.removeSection" select="'Remove section'"/>
	<xsl:variable name="i18n.startModule" select="'Start module'"/>
	<xsl:variable name="i18n.stopModule" select="'Stop module'"/>
	<xsl:variable name="i18n.moveModule" select="'Move module'"/>
	<xsl:variable name="i18n.copyModule" select="'Copy module'"/>
	<xsl:variable name="i18n.editModule" select="'Edit module'"/>
	<xsl:variable name="i18n.removeModule" select="'Remove module'"/>
	<xsl:variable name="i18n.name" select="'Name'"/>
	<xsl:variable name="i18n.description" select="'Description'"/>
	<xsl:variable name="i18n.alias" select="'Alias'"/>
	<xsl:variable name="i18n.requiredProtocol" select="'Protocol'"/>
	<xsl:variable name="i18n.slots" select="'Slots'"/>
	<xsl:variable name="i18n.autoStartUponSystemStartAndCacheReload" select="'Start automatically upon system start and cache reload'"/>
	<xsl:variable name="i18n.showSectionInMenu" select="'Show section in menu'"/>
	<xsl:variable name="i18n.showModuleInMenu" select="'Show module in menu'"/>
	<xsl:variable name="i18n.showBreadCrumb" select="'Show breadcrumb'"/>
	<xsl:variable name="i18n.defaultAddresses" select="'Default addresses'"/>
	<xsl:variable name="i18n.administrators" select="'Administrators'"/>
	<xsl:variable name="i18n.loggedInUsers" select="'Logged in users'"/>
	<xsl:variable name="i18n.anonymousUsers" select="'Non logged in users'"/>
	<xsl:variable name="i18n.access" select="'Access'"/>
	<xsl:variable name="i18n.allLoggedInUsers" select="'All logged in users'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'You must fill in the field'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Incorrect format in field'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'Too short content in field '"/>
	<xsl:variable name="i18n.validation.tooLong" select="'Too long content in field'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Unknown error in field'"/>
	<xsl:variable name="i18n.anonymousDefaultURI" select="'Default address for non logged in users'"/>
	<xsl:variable name="i18n.userDefaultURI" select="'Default address for logged in users'"/>
	<xsl:variable name="i18n.adminDefaultURI" select="'Default address for administrators'"/>
	<xsl:variable name="i18n.classname" select="'classname'"/>
	<xsl:variable name="i18n.xslPathType" select="'XSLT path type'"/>
	<xsl:variable name="i18n.xslPath" select="'XSLT path'"/>
	<xsl:variable name="i18n.validationError.dataSourceID" select="'datasource'"/>
	<xsl:variable name="i18n.datasource" select="'Datasource'"/>
	<xsl:variable name="i18n.duplicateModuleAlias" select="'A module with this alias already exists in this section'"/>
	<xsl:variable name="i18n.duplicateSectionAlias" select="'A subsection with this alias already exists in this section'"/>
	
	
	<xsl:variable name="i18n.unknownFault" select="'An unknown fault has occurred'"/>
	<xsl:variable name="i18n.groups" select="'Groups'"/>
	<xsl:variable name="i18n.users" select="'Users'"/>
	<xsl:variable name="i18n.classnameWholePath" select="'Class name (absolute path)'"/>
	<xsl:variable name="i18n.staticPackage" select="'Static content package'"/>
	<xsl:variable name="i18n.xslt" select="'XSLT stylesheet'"/>
	<xsl:variable name="i18n.pathType" select="'Path type'"/>
	<xsl:variable name="i18n.path" select="'Path'"/>
	<xsl:variable name="i18n.addModule" select="'Add module'"/>
	<xsl:variable name="i18n.noStyleSheet" select="'No stylesheet'"/>
	<xsl:variable name="i18n.moveSectionInstruction" select="'Click on the section to which you want to move the section'"/>
	<xsl:variable name="i18n.moveModuleInstruction" select="'Click on the section to which you want to move the module'"/>
	<xsl:variable name="i18n.copyModuleInstruction" select="'Click on the section to which you want to copy the module'"/>
	<xsl:variable name="i18n.editSubSection" select="'Edit section'"/>
	<xsl:variable name="i18n.saveChanges" select="'Save changes'"/>
	<xsl:variable name="i18n.dataSources.defaultDataSource" select="'System default'"/>
	<xsl:variable name="i18n.updateModule.moduleSettings" select="'Module specific settings'"/>
	<xsl:variable name="i18n.updateModule.moduleHasNoModuleSettings" select="'This module has no module specific settings'"/>
	<xsl:variable name="i18n.updateModule.moduleNotStarted" select="'Module specific settings are only available when the module is started'"/>
	<xsl:variable name="i18n.settingDescriptor.notSet" select="'Not set'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart1" select="'The setting'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart2" select="'has an unknown format'"/>
	<xsl:variable name="i18n.settingDescriptor.resetDefualtValue" select="'Reset default value'"/>
	
	<xsl:variable name="i18n.priority" select="'Priority'"/>
	<xsl:variable name="i18n.downloadModuleDescriptor" select="'Download descriptor for module'"/>	
	
	<xsl:variable name="i18n.importModulesInSection">Import modules in section:</xsl:variable>
	<xsl:variable name="i18n.descriptors">Module descriptors</xsl:variable>
	<xsl:variable name="i18n.startMode">Start modules</xsl:variable>
	<xsl:variable name="i18n.startNoModules">None</xsl:variable>
	<xsl:variable name="i18n.startEnabledModules">Marked as autostart</xsl:variable>
	<xsl:variable name="i18n.startAllModules">All modules</xsl:variable>
	<xsl:variable name="i18n.importModules">Import modules</xsl:variable>
	<xsl:variable name="i18n.preserveModuleIDs">Preserve module ID's</xsl:variable>
	<xsl:variable name="i18n.preserveDataSourceIDs">Preserve data source ID's</xsl:variable>
	<xsl:variable name="i18n.FilterModuleImportInSubsection">Filter modules can only be imported in the root section</xsl:variable>
	<xsl:variable name="i18n.NoDescriptorsfound">No descriptors found</xsl:variable>
	<xsl:variable name="i18n.UnableToParseRequest">Unable to parse request</xsl:variable>
	<xsl:variable name="i18n.InvalidFileExtension.part1">The file </xsl:variable>
	<xsl:variable name="i18n.InvalidFileExtension.part2"> has an invalid file extension. Only .bgmodule, .fgmodule and .flmodule files are supported.</xsl:variable>
	
	<xsl:variable name="i18n.foregroundModule">foreground module</xsl:variable>
	<xsl:variable name="i18n.backgroundModule">background module</xsl:variable>
	<xsl:variable name="i18n.filterModule">filter module</xsl:variable>	
	
	<xsl:variable name="i18n.DuplicateModuleID.part1">The ID of module </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part2"/>
	<xsl:variable name="i18n.DuplicateModuleID.part3">conflicts with the ID of module </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.DuplicateModuleAlias.part1">The alias of module </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part2"/>
	<xsl:variable name="i18n.DuplicateModuleAlias.part3"> conflicts with the alias of module </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part1">You attached </xsl:variable>
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part2"> of files which exceeds the allowed limit of </xsl:variable>
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part3">!</xsl:variable>
	
	<xsl:variable name="i18n.FileSizeLimitExceeded.part1">The attached file </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part2"> with size </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part3"> exceeds the allowed file size of </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.UnableToParseFile.part1">Unable to parse </xsl:variable>
	<xsl:variable name="i18n.UnableToParseFile.part2">!</xsl:variable>

</xsl:stylesheet>
