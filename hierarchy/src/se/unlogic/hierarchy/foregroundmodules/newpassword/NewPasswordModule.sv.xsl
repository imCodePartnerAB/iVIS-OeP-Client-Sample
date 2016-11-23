<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="NewPasswordModuleTemplates.xsl"/>

	<xsl:variable name="i18n.NewPasswordForm.regenerateCaptcha">Generera ny bild</xsl:variable>
	<xsl:variable name="i18n.NewPasswordForm.captchaConfirmation">Bildverifiering</xsl:variable>
	<xsl:variable name="i18n.NewPasswordForm.submit">Begär nytt lösenord</xsl:variable>
	<xsl:variable name="i18n.email">E-post</xsl:variable>
	<xsl:variable name="i18n.username">Användarnamn</xsl:variable>
	<xsl:variable name="i18n.UserNotFound">Ingen användare hittades med de angivna uppgifterna</xsl:variable>
	<xsl:variable name="i18n.UserNotMutable">Den går inte att ändra lösenord på den här användaren</xsl:variable>
	<xsl:variable name="i18n.UnableToUpdateUser">Det gick inte att ändra lösenordet på den här användaren</xsl:variable>
	<xsl:variable name="i18n.ErrorSendingMail">Ett fel inträffade när det nya lösenordet skulle skickas till dig</xsl:variable>
	
	<xsl:variable name="newPasswordFormMessage">Fyll i formuläret nedan för att få ett nytt lösenord skickat till dig</xsl:variable>
	<xsl:variable name="newPasswordSentMessage">Ett nytt lösenord har skickats till dig</xsl:variable>
	
	<xsl:variable name="subject">Nytt lösenord</xsl:variable>
	<xsl:variable name="message">Hej $user.firstname,
	
Här ditt nya lösenord till Min site: $password

/Min site</xsl:variable>		
	<xsl:variable name="i18n.validation.requiredField" select="'Du måste fylla i fältet'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Felaktigt format på fältet'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'För kort innehåll i fältet'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'För långt innehåll i fältet'"/>	
	<xsl:variable name="i18n.validation.unknownError" select="'Okänt fel på fältet'"/>
	<xsl:variable name="i18n.unknownFault" select="'Ett okänt fel har uppstått'"/>
	<xsl:variable name="i18n.InvalidCaptchaConfirmation">Felaktig bildverifiering</xsl:variable>
	<xsl:variable name="i18n.UserNotEnabled">Det här användarkontot är inte aktiverat</xsl:variable>
</xsl:stylesheet>
