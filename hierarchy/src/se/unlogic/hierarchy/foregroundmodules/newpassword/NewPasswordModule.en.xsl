<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="NewPasswordModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.NewPasswordForm.regenerateCaptcha">Generate new code</xsl:variable>
	<xsl:variable name="i18n.NewPasswordForm.captchaConfirmation">Confirmation code</xsl:variable>
	<xsl:variable name="i18n.NewPasswordForm.submit">Request new password</xsl:variable>
	<xsl:variable name="i18n.email">E-mail</xsl:variable>
	<xsl:variable name="i18n.username">Username</xsl:variable>
	<xsl:variable name="i18n.UserNotFound">No user found with the specified details</xsl:variable>
	<xsl:variable name="i18n.UserNotMutable">The password cannot be changed for the requested user</xsl:variable>
	<xsl:variable name="i18n.UnableToUpdateUser">Unable to change password for the requested user</xsl:variable>
	<xsl:variable name="i18n.ErrorSendingMail">An error occurred while sending the new password</xsl:variable>
	
	<xsl:variable name="newPasswordFormMessage">Fill in the form below to get a new password e-mailed to you</xsl:variable>
	<xsl:variable name="newPasswordSentMessage">A new password has been e-mailed to you</xsl:variable>
	
	<xsl:variable name="subject">New password</xsl:variable>
	<xsl:variable name="message">Hello $user.firstname,
	
Here is your new password for mysite: $password

/Mysite</xsl:variable>
	
	<xsl:variable name="i18n.validation.requiredField" select="'You need to fill in the field'"/>
	<xsl:variable name="i18n.validation.invalidFormat" select="'Invalid value in field'"/>
	<xsl:variable name="i18n.validation.tooShort" select="'Too short content in field'"/>
	<xsl:variable name="i18n.validation.tooLong" select="'Too long content in field'"/>		
	<xsl:variable name="i18n.validation.unknownError" select="'Unknown problem validating field'"/>
	<xsl:variable name="i18n.unknownFault" select="'An unknown error has occured!'"/>
	<xsl:variable name="i18n.InvalidCaptchaConfirmation">Invalid confirmation code</xsl:variable>
	<xsl:variable name="i18n.UserNotEnabled">The requested user account is not enabled</xsl:variable>
</xsl:stylesheet>
