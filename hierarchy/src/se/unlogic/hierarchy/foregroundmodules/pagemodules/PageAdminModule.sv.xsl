<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="PageAdminModuleTemplates.xsl" />
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.sv.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="expandAll" select="'Fäll ut alla'" />
	<xsl:variable name="collapseAll" select="'Stäng alla'" />
	<xsl:variable name="addPageInSection" select="'Lägg till en ny sida i sektionen'" />
	<xsl:variable name="varning.noPageviewModuleInSection" select="'Varning det finns ingen modul för visning av sidor i den här sektionen!'" />
	<xsl:variable name="movePage" select="'Flytta sidan'"/>
	<xsl:variable name="movePageInstruction" select="'Klicka på den sektion som du vill flytta sidan till'"/>
	<xsl:variable name="copyPage" select="'Kopiera sidan'"/>
	<xsl:variable name="copyPageInstruction" select="'Klicka på den sektion som du vill kopiera sidan till'"/>
	<xsl:variable name="editPage" select="'Redigera sidan'"/>
	<xsl:variable name="deletePage" select="'Ta bort sidan'"/>
	<xsl:variable name="editingOfPage" select="'Redigering av sidan'"/>
	<xsl:variable name="name" select="'Namn'"/>
	<xsl:variable name="description" select="'Beskrivning'"/>
	<xsl:variable name="alias" select="'Alias'"/>
	<xsl:variable name="content" select="'Innehåll'"/>
	<xsl:variable name="additionalSettings" select="'Övriga inställningar'"/>
	<xsl:variable name="activatePage" select="'Aktivera sidan'"/>
	<xsl:variable name="showInMenu" select="'Visa sidan i menyn'"/>
	<xsl:variable name="showBreadCrumb" select="'Visa brödsmula'"/>
	<xsl:variable name="access" select="'Åtkomst'"/>
	<xsl:variable name="admins" select="'Administratörer'"/>
	<xsl:variable name="loggedInUsers" select="'Samtliga Inloggade användare'"/>
	<xsl:variable name="nonLoggedInUsers" select="'Ej inloggade användare'"/>
	<xsl:variable name="validationError.requiredField" select="'Du måste fylla i fältet'"/>
	<xsl:variable name="validationError.invalidFormat" select="'Felaktigt format på fältet'"/>
	<xsl:variable name="validationError.unknown" select="'Okänt fel på fältet'"/>
	<xsl:variable name="validationError.duplicateAlias" select="'Det finns redan en sida med detta alias i den här sektion'"/>
	<xsl:variable name="validationError.unknownErrorOccurred" select="'Ett okänt fel har uppstått'"/>
	<xsl:variable name="addPage" select="'Lägg till sida'"/>
	<xsl:variable name="pagePreview" select="'Förhandsvisning av sidan'"/>
	<xsl:variable name="showPageOutsideAdminView" select="'Visa sidan utanför administrationsgränssnittet'"/>
	<xsl:variable name="users" select="'Användare'"/>
	<xsl:variable name="groups" select="'Grupper'"/>
	<xsl:variable name="inSection" select="'i sektionen'"/>
	<xsl:variable name="saveChanges" select="'Spara ändringar'"/>
	
</xsl:stylesheet>