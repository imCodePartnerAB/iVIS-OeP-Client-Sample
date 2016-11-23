<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="LoginModuleTemplates.xsl" />
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="Login.header" select="'Inloggning'" />
	
	<xsl:variable name="AccountDisabled.header" select="'Inloggning'" />
	<xsl:variable name="AccountDisabled.text" select="'Ditt konto är avstängt, kontakta systemadministratören för mer information.'" />
	
	<xsl:variable name="LoginFailed.header" select="'Inloggning'" />
	<xsl:variable name="LoginFailed.text" select="'Felaktigt användarnamn eller lösenord!'" />
	
	<xsl:variable name="AccountLocked.header" select="'Inloggning'" />
	<xsl:variable name="AccountLocked.text.part1" select="'Kontot är låst i '" />
	<xsl:variable name="AccountLocked.text.part2" select="' minuter p.g.a för många felaktiga inloggningsförsök!'" />
	
	<xsl:variable name="LoginForm.username" select="'Användarnamn:'" />
	<xsl:variable name="LoginForm.password" select="'Lösenord:'" />
	<xsl:variable name="LoginForm.submit" select="'Logga in'" />
	
	<xsl:variable name="requestNewPassword" select="'Begär nytt lösenord'" />
	<xsl:variable name="createNewAccount" select="'Skapa ett användarkonto'" />
	
	
</xsl:stylesheet>