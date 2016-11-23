<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="NewPasswordModuleTemplates.xsl"/>

	<xsl:variable name="i18n.NewPasswordForm.regenerateCaptcha">Generera ny bild</xsl:variable>
	<xsl:variable name="i18n.NewPasswordForm.captchaConfirmation">Bildverifiering</xsl:variable>
	<xsl:variable name="i18n.NewPasswordForm.submit">Beg�r nytt l�senord</xsl:variable>
	<xsl:variable name="i18n.email">E-post</xsl:variable>
	<xsl:variable name="i18n.username">Anv�ndarnamn</xsl:variable>
	<xsl:variable name="i18n.UserNotFound">Ingen anv�ndare hittades med de angivna uppgifterna</xsl:variable>
	<xsl:variable name="i18n.UserNotMutable">Den g�r inte att �ndra l�senord p� den h�r anv�ndaren</xsl:variable>
	<xsl:variable name="i18n.UnableToUpdateUser">Det gick inte att �ndra l�senordet p� den h�r anv�ndaren</xsl:variable>
	<xsl:variable name="i18n.ErrorSendingMail">Ett fel intr�ffade n�r det nya l�senordet skulle skickas till dig</xsl:variable>
	
	<xsl:variable name="newPasswordFormMessage">Fyll i formul�ret nedan f�r att f� ett nytt l�senord skickat till dig</xsl:variable>
	<xsl:variable name="newPasswordSentMessage">Ett nytt l�senord har skickats till dig</xsl:variable>
	
	<xsl:variable name="subject">Nytt l�senord</xsl:variable>
	<xsl:variable name="message">Hej $user.firstname,
	
H�r ditt nya l�senord till Min site: $password

/Min site</xsl:variable>		
	<xsl:variable name="i18n.validation.requiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'F�r kort inneh�ll i f�ltet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="i18n.unknownFault" select="'Ett ok�nt fel har uppst�tt'"/>
	<xsl:variable name="i18n.InvalidCaptchaConfirmation">Felaktig bildverifiering</xsl:variable>
	<xsl:variable name="i18n.UserNotEnabled">Det h�r anv�ndarkontot �r inte aktiverat</xsl:variable>
</xsl:stylesheet>
