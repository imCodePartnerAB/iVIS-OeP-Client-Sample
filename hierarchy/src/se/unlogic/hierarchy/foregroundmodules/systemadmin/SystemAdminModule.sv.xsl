<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="SystemAdminModuleTemplates.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.sv.xsl"/>
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="addForegroundModuleBreadCrumbText">Lägg till förgrundsmodul</xsl:variable>
	<xsl:variable name="updateForegroundModuleBreadCrumbText">Uppdatera förgrundsmodul: </xsl:variable>
	<xsl:variable name="copyForegroundModuleBreadCrumbText">Kopiera förgrundsmodul: </xsl:variable>
	<xsl:variable name="moveForegroundModuleBreadCrumbText">Flytta förgrundsmodul: </xsl:variable>
	
	<xsl:variable name="addBackgroundModuleBreadCrumbText">Lägg till bakgrundsmodul</xsl:variable>
	<xsl:variable name="updateBackgroundModuleBreadCrumbText">Uppdatera bakgrundsmodul: </xsl:variable>
	<xsl:variable name="copyBackgroundModuleBreadCrumbText">Kopiera bakgrundsmodul: </xsl:variable>
	<xsl:variable name="moveBackgroundModuleBreadCrumbText">Flytta bakgrundsmodul: </xsl:variable>	
	
	<xsl:variable name="addFilterModuleBreadCrumbText">Lägg till filtermodul</xsl:variable>
	<xsl:variable name="updateFilterModuleBreadCrumbText">Uppdatera filtermodul: </xsl:variable>
	
	<xsl:variable name="addSectionBreadCrumbText">Lägg till sektion</xsl:variable>
	<xsl:variable name="updateSectionBreadCrumbText">Uppdatera sektion: </xsl:variable>
	<xsl:variable name="moveSectionBreadCrumbText">Flytta sektion: </xsl:variable>
	<xsl:variable name="importModulesBreadCrumbText">Importera moduler i sektionen: </xsl:variable>
	
	<xsl:variable name="i18n.expandAll" select="'Fäll ut alla'"/>
	<xsl:variable name="i18n.collapseAll" select="'Stäng alla'"/>
	<xsl:variable name="i18n.startSection" select="'Starta sektion '"/>
	<xsl:variable name="i18n.stopSection" select="'Stoppa sektion '"/>
	<xsl:variable name="i18n.addFilterModule" select="'Lägg till filtermodul'"/>
	<xsl:variable name="i18n.addModuleInSection" select="'Lägg till en modul i sektionen'"/>
	<xsl:variable name="i18n.addSubSectionInSection" select="'Lägg till en undersektion i sektionen'"/>
	<xsl:variable name="i18n.addSubSection" select="'Lägg till undersektion'"/>
	<xsl:variable name="i18n.moveSection" select="'Flytta sektionen'"/>
	<xsl:variable name="i18n.editSection" select="'Uppdatera sektionen'"/>
	<xsl:variable name="i18n.removeSection" select="'Ta bort sektionen'"/>
	<xsl:variable name="i18n.startModule" select="'Starta modulen'"/>
	<xsl:variable name="i18n.stopModule" select="'Stoppa modulen'"/>
	<xsl:variable name="i18n.moveModule" select="'Flytta modulen'"/>
	<xsl:variable name="i18n.copyModule" select="'Kopiera modulen'"/>
	<xsl:variable name="i18n.editModule" select="'Uppdatera modulen'"/>
	<xsl:variable name="i18n.removeModule" select="'Ta bort modulen'"/>
	<xsl:variable name="i18n.name" select="'Namn'"/>
	<xsl:variable name="i18n.description" select="'Beskrivning'"/>
	<xsl:variable name="i18n.alias" select="'Alias'"/>
	<xsl:variable name="i18n.requiredProtocol" select="'Protocol'"/>
	<xsl:variable name="i18n.slots" select="'Slots'"/>
	<xsl:variable name="i18n.autoStartUponSystemStartAndCacheReload" select="'Starta automatiskt vid systemstart och cache omladdning'"/>
	<xsl:variable name="i18n.showSectionInMenu" select="'Visa sektionen i menyn'"/>
	<xsl:variable name="i18n.showModuleInMenu" select="'Visa modulen i menyn'"/>
	<xsl:variable name="i18n.showBreadCrumb" select="'Visa brödsmula'"/>
	<xsl:variable name="i18n.defaultAddresses" select="'Standardadresser'"/>
	<xsl:variable name="i18n.administrators" select="'Administratörer'"/>
	<xsl:variable name="i18n.loggedInUsers" select="'Inloggade användare'"/>
	<xsl:variable name="i18n.anonymousUsers" select="'Ej inloggade användare'"/>
	<xsl:variable name="i18n.access" select="'Åtkomst'"/>
	<xsl:variable name="i18n.allLoggedInUsers" select="'Samtliga Inloggade användare'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'Du måste fylla i fältet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format på fältet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'För kort innehåll i fältet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'För långt innehåll i fältet'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Okänt fel på fältet'"/>
	<xsl:variable name="i18n.anonymousDefaultURI" select="'standard adress för icke inloggade'"/>
	<xsl:variable name="i18n.userDefaultURI" select="'standard adress för inloggade'"/>
	<xsl:variable name="i18n.adminDefaultURI" select="'standard adress för administratörer'"/>
	<xsl:variable name="i18n.classname" select="'klassnamn'"/>
	<xsl:variable name="i18n.xslPathType" select="'sökvägstyp för XSLT stilmall'"/>
	<xsl:variable name="i18n.xslPath" select="'sökväg för XSLT stilmall'"/>
	<xsl:variable name="i18n.validationError.dataSourceID" select="'datakälla'"/>
	<xsl:variable name="i18n.datasource" select="'Datasource'"/>
	<xsl:variable name="i18n.duplicateModuleAlias" select="'Det finns redan en modul med detta alias i den här sektion'"/>
	<xsl:variable name="i18n.duplicateSectionAlias" select="'Det finns redan en under sektion med detta alias i den här sektion'"/>
	
	
	<xsl:variable name="i18n.unknownFault" select="'Ett okänt fel har uppstått'"/>
	<xsl:variable name="i18n.groups" select="'Grupper'"/>
	<xsl:variable name="i18n.users" select="'Användare'"/>
	<xsl:variable name="i18n.classnameWholePath" select="'Klassnamn (hela sökvägen)'"/>
	<xsl:variable name="i18n.staticPackage" select="'Paket för statiska filer'"/>
	<xsl:variable name="i18n.xslt" select="'XSLT stilmall'"/>
	<xsl:variable name="i18n.pathType" select="'Sökvägstyp'"/>
	<xsl:variable name="i18n.path" select="'Sökväg'"/>
	<xsl:variable name="i18n.addModule" select="'Lägg till modul'"/>
	<xsl:variable name="i18n.noStyleSheet" select="'Ingen stilmall'"/>
	<xsl:variable name="i18n.moveSectionInstruction" select="'Klicka på den sektion som du vill flytta sektionen till'"/>
	<xsl:variable name="i18n.moveModuleInstruction" select="'Klicka på den sektion som du vill flytta modulen till'"/>
	<xsl:variable name="i18n.copyModuleInstruction" select="'Klicka på den sektion som du vill kopiera modulen till'"/>
	<xsl:variable name="i18n.editSubSection" select="'Uppdatera sektionen'"/>
	<xsl:variable name="i18n.saveChanges" select="'Spara ändringar'"/>
	<xsl:variable name="i18n.dataSources.defaultDataSource" select="'System standard'"/>
	<xsl:variable name="i18n.updateModule.moduleSettings" select="'Modulspecifika inställningar'"/>
	<xsl:variable name="i18n.updateModule.moduleHasNoModuleSettings" select="'Den här modulen har inga modulespecifika inställningar'"/>
	<xsl:variable name="i18n.updateModule.moduleNotStarted" select="'Modulespecifika inställningar går endast att komma åt när modulen är startad'"/>
	<xsl:variable name="i18n.settingDescriptor.notSet" select="'Ej satt'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart1" select="'Inställningen'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart2" select="'har ett okänt format'"/>
	<xsl:variable name="i18n.settingDescriptor.resetDefualtValue" select="'Återställ standardvärde'"/>
	
	<xsl:variable name="i18n.priority" select="'Prioritet'"/>
	<xsl:variable name="i18n.downloadModuleDescriptor" select="'Ladda ner descriptorn för modulen'"/>	
	<xsl:variable name="i18n.importModulesInSection">Importera moduler i sektionen:</xsl:variable>
	
	<xsl:variable name="i18n.descriptors">Moduldeskriptorer</xsl:variable>
	<xsl:variable name="i18n.startMode">Start av moduler</xsl:variable>
	<xsl:variable name="i18n.startNoModules">Inga moduler</xsl:variable>
	<xsl:variable name="i18n.startEnabledModules">Markerade som autostart</xsl:variable>
	<xsl:variable name="i18n.startAllModules">Samtliga moduler</xsl:variable>
	<xsl:variable name="i18n.importModules">Importera moduler</xsl:variable>
	<xsl:variable name="i18n.preserveModuleIDs">Behåll modul-ID:n</xsl:variable>
	<xsl:variable name="i18n.FilterModuleImportInSubsection">Filter moduler kan endast importeras i root sektionen</xsl:variable>
	<xsl:variable name="i18n.NoDescriptorsfound">Inga deskriptorer hittades</xsl:variable>
	<xsl:variable name="i18n.UnableToParseRequest">Den gick inte att tolka informationen från din webbläsare</xsl:variable>
	<xsl:variable name="i18n.InvalidFileExtension.part1">Filen </xsl:variable>
	<xsl:variable name="i18n.InvalidFileExtension.part2"> har en ogiltigt filtyp. Endast .bgmodule, .fgmodule och .flmodule filer stöds!</xsl:variable>
	
	<xsl:variable name="i18n.foregroundModule">förgrundsmodul</xsl:variable>
	<xsl:variable name="i18n.backgroundModule">bakgrundsmodul</xsl:variable>
	<xsl:variable name="i18n.filterModule">filtermodul</xsl:variable>
	
	<xsl:variable name="i18n.DuplicateModuleID.part1">ID-nummret på </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part2">en </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part3"> krockar med ID-nummret på modulen </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.DuplicateModuleAlias.part1">Aliaset på </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part2">en </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part3"> krockar med aliaset på modulen </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part1">Du bifogade totalt </xsl:variable>
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part2"> filer och den max tillåtna storleken är </xsl:variable>
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part3">!</xsl:variable>
	
	<xsl:variable name="i18n.FileSizeLimitExceeded.part1">Den bifogade filen </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part2"> med storleken </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part3"> överskrider den maximalt tillåtna filstorleken på </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.UnableToParseFile.part1">Den gick inte att tolka filen </xsl:variable>
	<xsl:variable name="i18n.UnableToParseFile.part2">!</xsl:variable>
	<xsl:variable name="i18n.preserveDataSourceIDs">Behåll ID:n för datakällor</xsl:variable>
</xsl:stylesheet>
