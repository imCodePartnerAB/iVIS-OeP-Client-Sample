<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="SystemAdminModuleTemplates.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.sv.xsl"/>
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="addForegroundModuleBreadCrumbText">L�gg till f�rgrundsmodul</xsl:variable>
	<xsl:variable name="updateForegroundModuleBreadCrumbText">Uppdatera f�rgrundsmodul: </xsl:variable>
	<xsl:variable name="copyForegroundModuleBreadCrumbText">Kopiera f�rgrundsmodul: </xsl:variable>
	<xsl:variable name="moveForegroundModuleBreadCrumbText">Flytta f�rgrundsmodul: </xsl:variable>
	
	<xsl:variable name="addBackgroundModuleBreadCrumbText">L�gg till bakgrundsmodul</xsl:variable>
	<xsl:variable name="updateBackgroundModuleBreadCrumbText">Uppdatera bakgrundsmodul: </xsl:variable>
	<xsl:variable name="copyBackgroundModuleBreadCrumbText">Kopiera bakgrundsmodul: </xsl:variable>
	<xsl:variable name="moveBackgroundModuleBreadCrumbText">Flytta bakgrundsmodul: </xsl:variable>	
	
	<xsl:variable name="addFilterModuleBreadCrumbText">L�gg till filtermodul</xsl:variable>
	<xsl:variable name="updateFilterModuleBreadCrumbText">Uppdatera filtermodul: </xsl:variable>
	
	<xsl:variable name="addSectionBreadCrumbText">L�gg till sektion</xsl:variable>
	<xsl:variable name="updateSectionBreadCrumbText">Uppdatera sektion: </xsl:variable>
	<xsl:variable name="moveSectionBreadCrumbText">Flytta sektion: </xsl:variable>
	<xsl:variable name="importModulesBreadCrumbText">Importera moduler i sektionen: </xsl:variable>
	
	<xsl:variable name="i18n.expandAll" select="'F�ll ut alla'"/>
	<xsl:variable name="i18n.collapseAll" select="'St�ng alla'"/>
	<xsl:variable name="i18n.startSection" select="'Starta sektion '"/>
	<xsl:variable name="i18n.stopSection" select="'Stoppa sektion '"/>
	<xsl:variable name="i18n.addFilterModule" select="'L�gg till filtermodul'"/>
	<xsl:variable name="i18n.addModuleInSection" select="'L�gg till en modul i sektionen'"/>
	<xsl:variable name="i18n.addSubSectionInSection" select="'L�gg till en undersektion i sektionen'"/>
	<xsl:variable name="i18n.addSubSection" select="'L�gg till undersektion'"/>
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
	<xsl:variable name="i18n.showBreadCrumb" select="'Visa br�dsmula'"/>
	<xsl:variable name="i18n.defaultAddresses" select="'Standardadresser'"/>
	<xsl:variable name="i18n.administrators" select="'Administrat�rer'"/>
	<xsl:variable name="i18n.loggedInUsers" select="'Inloggade anv�ndare'"/>
	<xsl:variable name="i18n.anonymousUsers" select="'Ej inloggade anv�ndare'"/>
	<xsl:variable name="i18n.access" select="'�tkomst'"/>
	<xsl:variable name="i18n.allLoggedInUsers" select="'Samtliga Inloggade anv�ndare'"/>
	<xsl:variable name="i18n.validation.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'F�r kort inneh�ll i f�ltet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="i18n.anonymousDefaultURI" select="'standard adress f�r icke inloggade'"/>
	<xsl:variable name="i18n.userDefaultURI" select="'standard adress f�r inloggade'"/>
	<xsl:variable name="i18n.adminDefaultURI" select="'standard adress f�r administrat�rer'"/>
	<xsl:variable name="i18n.classname" select="'klassnamn'"/>
	<xsl:variable name="i18n.xslPathType" select="'s�kv�gstyp f�r XSLT stilmall'"/>
	<xsl:variable name="i18n.xslPath" select="'s�kv�g f�r XSLT stilmall'"/>
	<xsl:variable name="i18n.validationError.dataSourceID" select="'datak�lla'"/>
	<xsl:variable name="i18n.datasource" select="'Datasource'"/>
	<xsl:variable name="i18n.duplicateModuleAlias" select="'Det finns redan en modul med detta alias i den h�r sektion'"/>
	<xsl:variable name="i18n.duplicateSectionAlias" select="'Det finns redan en under sektion med detta alias i den h�r sektion'"/>
	
	
	<xsl:variable name="i18n.unknownFault" select="'Ett ok�nt fel har uppst�tt'"/>
	<xsl:variable name="i18n.groups" select="'Grupper'"/>
	<xsl:variable name="i18n.users" select="'Anv�ndare'"/>
	<xsl:variable name="i18n.classnameWholePath" select="'Klassnamn (hela s�kv�gen)'"/>
	<xsl:variable name="i18n.staticPackage" select="'Paket f�r statiska filer'"/>
	<xsl:variable name="i18n.xslt" select="'XSLT stilmall'"/>
	<xsl:variable name="i18n.pathType" select="'S�kv�gstyp'"/>
	<xsl:variable name="i18n.path" select="'S�kv�g'"/>
	<xsl:variable name="i18n.addModule" select="'L�gg till modul'"/>
	<xsl:variable name="i18n.noStyleSheet" select="'Ingen stilmall'"/>
	<xsl:variable name="i18n.moveSectionInstruction" select="'Klicka p� den sektion som du vill flytta sektionen till'"/>
	<xsl:variable name="i18n.moveModuleInstruction" select="'Klicka p� den sektion som du vill flytta modulen till'"/>
	<xsl:variable name="i18n.copyModuleInstruction" select="'Klicka p� den sektion som du vill kopiera modulen till'"/>
	<xsl:variable name="i18n.editSubSection" select="'Uppdatera sektionen'"/>
	<xsl:variable name="i18n.saveChanges" select="'Spara �ndringar'"/>
	<xsl:variable name="i18n.dataSources.defaultDataSource" select="'System standard'"/>
	<xsl:variable name="i18n.updateModule.moduleSettings" select="'Modulspecifika inst�llningar'"/>
	<xsl:variable name="i18n.updateModule.moduleHasNoModuleSettings" select="'Den h�r modulen har inga modulespecifika inst�llningar'"/>
	<xsl:variable name="i18n.updateModule.moduleNotStarted" select="'Modulespecifika inst�llningar g�r endast att komma �t n�r modulen �r startad'"/>
	<xsl:variable name="i18n.settingDescriptor.notSet" select="'Ej satt'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart1" select="'Inst�llningen'"/>
	<xsl:variable name="i18n.settingDescriptor.unknownTypePart2" select="'har ett ok�nt format'"/>
	<xsl:variable name="i18n.settingDescriptor.resetDefualtValue" select="'�terst�ll standardv�rde'"/>
	
	<xsl:variable name="i18n.priority" select="'Prioritet'"/>
	<xsl:variable name="i18n.downloadModuleDescriptor" select="'Ladda ner descriptorn f�r modulen'"/>	
	<xsl:variable name="i18n.importModulesInSection">Importera moduler i sektionen:</xsl:variable>
	
	<xsl:variable name="i18n.descriptors">Moduldeskriptorer</xsl:variable>
	<xsl:variable name="i18n.startMode">Start av moduler</xsl:variable>
	<xsl:variable name="i18n.startNoModules">Inga moduler</xsl:variable>
	<xsl:variable name="i18n.startEnabledModules">Markerade som autostart</xsl:variable>
	<xsl:variable name="i18n.startAllModules">Samtliga moduler</xsl:variable>
	<xsl:variable name="i18n.importModules">Importera moduler</xsl:variable>
	<xsl:variable name="i18n.preserveModuleIDs">Beh�ll modul-ID:n</xsl:variable>
	<xsl:variable name="i18n.FilterModuleImportInSubsection">Filter moduler kan endast importeras i root sektionen</xsl:variable>
	<xsl:variable name="i18n.NoDescriptorsfound">Inga deskriptorer hittades</xsl:variable>
	<xsl:variable name="i18n.UnableToParseRequest">Den gick inte att tolka informationen fr�n din webbl�sare</xsl:variable>
	<xsl:variable name="i18n.InvalidFileExtension.part1">Filen </xsl:variable>
	<xsl:variable name="i18n.InvalidFileExtension.part2"> har en ogiltigt filtyp. Endast .bgmodule, .fgmodule och .flmodule filer st�ds!</xsl:variable>
	
	<xsl:variable name="i18n.foregroundModule">f�rgrundsmodul</xsl:variable>
	<xsl:variable name="i18n.backgroundModule">bakgrundsmodul</xsl:variable>
	<xsl:variable name="i18n.filterModule">filtermodul</xsl:variable>
	
	<xsl:variable name="i18n.DuplicateModuleID.part1">ID-nummret p� </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part2">en </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part3"> krockar med ID-nummret p� modulen </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleID.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.DuplicateModuleAlias.part1">Aliaset p� </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part2">en </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part3"> krockar med aliaset p� modulen </xsl:variable>
	<xsl:variable name="i18n.DuplicateModuleAlias.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part1">Du bifogade totalt </xsl:variable>
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part2"> filer och den max till�tna storleken �r </xsl:variable>
	<xsl:variable name="i18n.RequestSizeLimitExceeded.part3">!</xsl:variable>
	
	<xsl:variable name="i18n.FileSizeLimitExceeded.part1">Den bifogade filen </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part2"> med storleken </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part3"> �verskrider den maximalt till�tna filstorleken p� </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part4">!</xsl:variable>
	
	<xsl:variable name="i18n.UnableToParseFile.part1">Den gick inte att tolka filen </xsl:variable>
	<xsl:variable name="i18n.UnableToParseFile.part2">!</xsl:variable>
	<xsl:variable name="i18n.preserveDataSourceIDs">Beh�ll ID:n f�r datak�llor</xsl:variable>
</xsl:stylesheet>
