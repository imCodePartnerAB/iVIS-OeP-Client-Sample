<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="PageAdminModuleTemplates.xsl" />
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.sv.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="expandAll" select="'F�ll ut alla'" />
	<xsl:variable name="collapseAll" select="'St�ng alla'" />
	<xsl:variable name="addPageInSection" select="'L�gg till en ny sida i sektionen'" />
	<xsl:variable name="varning.noPageviewModuleInSection" select="'Varning det finns ingen modul f�r visning av sidor i den h�r sektionen!'" />
	<xsl:variable name="movePage" select="'Flytta sidan'"/>
	<xsl:variable name="movePageInstruction" select="'Klicka p� den sektion som du vill flytta sidan till'"/>
	<xsl:variable name="copyPage" select="'Kopiera sidan'"/>
	<xsl:variable name="copyPageInstruction" select="'Klicka p� den sektion som du vill kopiera sidan till'"/>
	<xsl:variable name="editPage" select="'Redigera sidan'"/>
	<xsl:variable name="deletePage" select="'Ta bort sidan'"/>
	<xsl:variable name="editingOfPage" select="'Redigering av sidan'"/>
	<xsl:variable name="name" select="'Namn'"/>
	<xsl:variable name="description" select="'Beskrivning'"/>
	<xsl:variable name="alias" select="'Alias'"/>
	<xsl:variable name="content" select="'Inneh�ll'"/>
	<xsl:variable name="additionalSettings" select="'�vriga inst�llningar'"/>
	<xsl:variable name="activatePage" select="'Aktivera sidan'"/>
	<xsl:variable name="showInMenu" select="'Visa sidan i menyn'"/>
	<xsl:variable name="showBreadCrumb" select="'Visa br�dsmula'"/>
	<xsl:variable name="access" select="'�tkomst'"/>
	<xsl:variable name="admins" select="'Administrat�rer'"/>
	<xsl:variable name="loggedInUsers" select="'Samtliga Inloggade anv�ndare'"/>
	<xsl:variable name="nonLoggedInUsers" select="'Ej inloggade anv�ndare'"/>
	<xsl:variable name="validationError.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="validationError.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="validationError.unknown" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="validationError.duplicateAlias" select="'Det finns redan en sida med detta alias i den h�r sektion'"/>
	<xsl:variable name="validationError.unknownErrorOccurred" select="'Ett ok�nt fel har uppst�tt'"/>
	<xsl:variable name="addPage" select="'L�gg till sida'"/>
	<xsl:variable name="pagePreview" select="'F�rhandsvisning av sidan'"/>
	<xsl:variable name="showPageOutsideAdminView" select="'Visa sidan utanf�r administrationsgr�nssnittet'"/>
	<xsl:variable name="users" select="'Anv�ndare'"/>
	<xsl:variable name="groups" select="'Grupper'"/>
	<xsl:variable name="inSection" select="'i sektionen'"/>
	<xsl:variable name="saveChanges" select="'Spara �ndringar'"/>
	
</xsl:stylesheet>