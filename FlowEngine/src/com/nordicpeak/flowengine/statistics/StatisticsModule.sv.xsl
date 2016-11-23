<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="StatisticsModuleTemplates.xsl"/>
	
	<xsl:variable name="java.flowFamilyCountChartLabel">Publicerade e-tj�nster</xsl:variable>
	<xsl:variable name="java.surveyRatingsChartLabel">Anv�ndarn�jdhet per vecka</xsl:variable>
	
	<xsl:variable name="java.csvWeek">Vecka</xsl:variable>
	<xsl:variable name="java.csvFlowInstanceCount">Antal ans�kningar</xsl:variable>
	<xsl:variable name="java.csvFlowInstanceCountFile">antal ans�kningar.csv</xsl:variable>
	<xsl:variable name="java.csvGlobalFlowCount">Antal e-tj�nster</xsl:variable>
	<xsl:variable name="java.csvGlobalFlowCountFile">antal e-tj�nster.csv</xsl:variable>
	<xsl:variable name="java.csvFamilyRating">Anv�ndarn�jdhet</xsl:variable>
	<xsl:variable name="java.csvFamilyRatingFile">anv�ndarn�jdhet.csv</xsl:variable>
	<xsl:variable name="java.csvStep">Steg</xsl:variable>
	<xsl:variable name="java.csvAbortCount">Avbrutna ans�kningar</xsl:variable>
	<xsl:variable name="java.csvStepAbortCountFile">avbrutna ans�kningar.csv</xsl:variable>
	<xsl:variable name="java.csvUnsubmittedCount">Antal ej inskickade ans�kningar</xsl:variable>
	<xsl:variable name="java.csvStepUnsubmittedCountFile">antal ej inskickade ans�kningar.csv</xsl:variable>
	
	<xsl:variable name="i18n.RatingPerWeek">Anv�ndarn�jdhet per vecka</xsl:variable>
	
	<xsl:variable name="i18n.FlowInstancesPerWeek">Antal ans�kningar per vecka</xsl:variable>
	
	<xsl:variable name="i18n.FlowStepAbortCountChartLabel">Avbrutna ans�kningar per steg</xsl:variable>
	<xsl:variable name="i18n.FlowStepUnsubmittedCountChartLabel">Antal ej inskickade ans�kningar per steg</xsl:variable>
	<xsl:variable name="i18n.NoStatisticsAvailable">Det finns f�r n�rvarande ingen statstik tillg�nglig f�r detta v�rde.</xsl:variable>
	
	<xsl:variable name="i18n.FlowFamilies">V�lj e-tj�nst</xsl:variable>
	<xsl:variable name="i18n.Version">Version </xsl:variable>
	<xsl:variable name="i18n.PublishedFlowFamiliesPerWeek">Antal publicerade e-tj�nster per vecka</xsl:variable>
	<xsl:variable name="i18n.DownloadChartDataInCSVFormat">Ladda ner datat f�r detta diagram i CSV-format</xsl:variable>
	<xsl:variable name="i18n.DownloadChartData">Ladda ner</xsl:variable>
	
</xsl:stylesheet>
