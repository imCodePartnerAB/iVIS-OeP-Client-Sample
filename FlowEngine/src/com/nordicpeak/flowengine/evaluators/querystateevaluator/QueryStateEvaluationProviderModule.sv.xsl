<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
		
	<xsl:include href="QueryStateEvaluationProviderModuleTemplates.xsl"/>
	
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:variable name="java.evaluatorTypeName">�ndra standardl�ge</xsl:variable>
	
	<xsl:variable name="i18n.name">Namn</xsl:variable>
	<xsl:variable name="i18n.UpdateEvaluatorDescriptor.title">Uppdatera regel</xsl:variable>
	<xsl:variable name="i18n.UpdateQueryStateEvaluator.submit">Spara �ndringar</xsl:variable>
	<xsl:variable name="i18n.selectionMode.title">Aktiveringsl�ge</xsl:variable>
	<xsl:variable name="i18n.selectionMode.description">V�lj n�r denna regel ska aktiveras.</xsl:variable>
	<xsl:variable name="i18n.SelectionMode.ANY">N�r anv�ndaren v�ljer n�got av nedanst�ende markerade alternativ.</xsl:variable>
	<xsl:variable name="i18n.SelectionMode.ALL">N�r anv�ndaren v�ljer samtliga av nedanst�ende markerade alternativ.</xsl:variable>
	<xsl:variable name="i18n.alternatives.title">Alternativ</xsl:variable>
	<xsl:variable name="i18n.alternatives.description">V�lj vilka alternativ som denna regel skall kopplas mot.</xsl:variable>
	<xsl:variable name="i18n.QueryHasNoAlternatives">Fr�gan som denna regel �r kopplad till har inga alternativ.</xsl:variable>
	<xsl:variable name="i18n.TargetQueryState.title">Fr�gel�ge</xsl:variable>
	<xsl:variable name="i18n.TargetQueryState.description">V�lj vilket fr�gel�ge nedanst�ende markerade fr�gor ska f� n�r denna regel aktiveras.</xsl:variable>
	<xsl:variable name="i18n.QueryState.VISIBLE">Valfri</xsl:variable>
	<xsl:variable name="i18n.QueryState.VISIBLE_REQUIRED">Obligatorisk</xsl:variable>
	<xsl:variable name="i18n.QueryState.HIDDEN">Dold</xsl:variable>
	<xsl:variable name="i18n.selectionMode">Aktiveringsl�ge</xsl:variable>
	<xsl:variable name="i18n.queryState">Fr�gel�ge</xsl:variable>
	<xsl:variable name="i18n.TargetQueries.title">Fr�gor</xsl:variable>
	<xsl:variable name="i18n.TargetQueries.description">V�lj vilka fr�gor som ska p�verkas av denna regel.</xsl:variable>
		
	<xsl:variable name="i18n.validation.requiredField" select="'Du m�ste fylla i f�ltet:'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format p� f�ltet:'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'F�r kort inneh�ll i f�ltet:'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'F�r l�ngt inneh�ll i f�ltet:'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Ok�nt fel p� f�ltet:'"/>
	<xsl:variable name="i18n.validation.unknownFault" select="'Ett ok�nt valideringsfel har uppst�tt.'"/>

	<xsl:variable name="i18n.enabled">Aktivera regeln</xsl:variable>
	<xsl:variable name="i18n.doNotResetQueryState">�terst�ll INTE fr�gornas standardl�ge automatiskt (anv�ndbart n�r flera regler p�verkar samma fr�gor).</xsl:variable>
</xsl:stylesheet>
