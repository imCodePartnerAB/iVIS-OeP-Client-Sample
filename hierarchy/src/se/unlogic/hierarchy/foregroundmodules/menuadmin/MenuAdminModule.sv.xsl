<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="MenuAdminModuleTemplates.xsl" />
	
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/usergrouplist/UserGroupList.sv.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	 <xsl:variable name="expandAll" select="'F�ll ut alla'" />
	 <xsl:variable name="collapseAll" select="'St�ng alla'" />
	 <xsl:variable name="addMenuInSection" select="'L�gg till menyalternativ i sektionen'" />
	 <xsl:variable name="moveMenu" select="'Flytta menyalternativ'"/>
	 <xsl:variable name="moveMenuInstruction" select="'Klicka p� den sektion som du vill flytta menyalternativet till'"/>
	 <xsl:variable name="editMenu" select="'Redigera menyalternativ'"/>
	 <xsl:variable name="removeMenu" select="'Ta bort menyalternativ'"/>
	 <xsl:variable name="name" select="'Namn'"/>
	 <xsl:variable name="description" select="'Beskrivning'"/>
	 <xsl:variable name="type" select="'Typ'"/>
	 <xsl:variable name="whitespace" select="'Mellanrum'"/>
	 <xsl:variable name="virtualMenu" select="'Virtuellt menyalternativ'"/>
	 <xsl:variable name="regularMenu" select="'Vanligt menyalternativ'"/>
	 <xsl:variable name="heading" select="'Rubrik'"/>
	 <xsl:variable name="section" select="'Sektion'"/>
	 <xsl:variable name="parameters" select="'Parametrar'"/>
	 <xsl:variable name="address" select="'Adress'"/>
	 <xsl:variable name="sortMenuInSection" select="'Sortering av meny i sektionen'"/>
	 <xsl:variable name="noMenuesFound" select="'Inga menyalternativ hittades'"/>
	 <xsl:variable name="visibleTo" select="'Synlig f�r'"/>
	 <xsl:variable name="link" select="'L�nk'"/>
	 <xsl:variable name="thisSectionIsNotStarted" select="'Den h�r sektionen �r inte startad!'"/>
	 <xsl:variable name="toAnotherSection" select="'till en annan sektion'"/>
	 <xsl:variable name="theModule" select="'Modulen'"/>
	 <xsl:variable name="theSection" select="'Sektionen'"/>
	 <xsl:variable name="theBundle" select="'Bundeln'"/>
	 <xsl:variable name="fromModule" select="'fr�n modulen'"/>
	 <xsl:variable name="unknown" select="'Ok�nt'"/>
	 <xsl:variable name="source" select="'K�lla'"/>
	 <xsl:variable name="phrase1" select="'Beskriver vart menyalternativet h�rstammar ifr�n'"/>
	 <xsl:variable name="phrase2" select="'Adressen som menyalternativet pekar till'"/>
	 <xsl:variable name="menu" select="'Menyalternativ'"/>
	 <xsl:variable name="phrase3" select="'Menyalternativ i bundlen'"/>
	 <xsl:variable name="access" select="'�tkomst'"/>
	 <xsl:variable name="admins" select="'Administrat�rer'"/>
	 <xsl:variable name="loggedInUsers" select="'Inloggade anv�ndare'"/>
	 <xsl:variable name="nonLoggedInUsers" select="'Ej inloggade anv�ndare'"/>
	
	 <xsl:variable name="validationError.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	 <xsl:variable name="validationError.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	 <xsl:variable name="validationError.unknown" select="'Ok�nt fel p� f�ltet'"/>
	 <xsl:variable name="validationError.tooLong">F�r l�ngt v�rde i f�ltet</xsl:variable>
	 
	 <xsl:variable name="validationError.duplicateAlias" select="'Det finns redan ett menyalternativ med detta alias i den h�r sektion!'"/>
	 <xsl:variable name="validationError.unknownErrorOccurred" select="'Ett ok�nt fel har intr�ffat'"/>
	
	 <xsl:variable name="add" select="'L�gg till'"/>
	 <xsl:variable name="users" select="'Anv�ndare'"/>
	 <xsl:variable name="groups" select="'Grupper'"/>
	 <xsl:variable name="inSection" select="'in section'"/>
	 <xsl:variable name="saveChanges" select="'Spara �ndringar'"/>
	
</xsl:stylesheet>