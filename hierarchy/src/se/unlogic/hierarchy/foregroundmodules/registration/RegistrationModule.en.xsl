<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="RegistrationModuleTemplates.xsl"/>

	<xsl:variable name="defaultUserConditions">Behave!</xsl:variable>

	<xsl:variable name="defaultRegistrationMessage">Fill in the form below to create an account.</xsl:variable>
	<xsl:variable name="defaultRegisteredMessage">Your account has now been created. An e-mail has been sent to you with further instructions on how to activate your account.</xsl:variable>
	<xsl:variable name="defaultAccountEnabledMessage">Your account has now been activated, you can now login.</xsl:variable>
	<xsl:variable name="defaultEmailSubject">Account confirmation</xsl:variable>
	<xsl:variable name="defaultEmailText">
Hello $user.firstname!

You have received this message because your email address was used during registration at http://somesite.

Click on the link below to activate your account:

$confirmation-link

If you did not register at http://somesite, please disregard this email. You do not need to unsubscribe or take any further action.

Your account will be automatically deleted in $confirmation-timeout days if left unactivated.

/Someone
	</xsl:variable>

	<xsl:variable name="defaultNewAccountNotificationSubject">New user registered</xsl:variable>
	<xsl:variable name="defaultNewAccountNotificationText">
Hello $subscriber.firstname,

A new user has registered: $user.firstname "$user.username" $user.lastname

/Registration module
	</xsl:variable>

	<xsl:variable name="AddUser.userConditions">Terms &amp; conditions</xsl:variable>
	<xsl:variable name="AddUser.showUserConditions">Show terms &amp; conditions</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part1">I have read and accept the</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part2">terms &amp; conditions</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part3">.</xsl:variable>

	<xsl:variable name="AddUser.firstname">Firstname</xsl:variable>
	<xsl:variable name="AddUser.lastname">Lastname</xsl:variable>
	<xsl:variable name="AddUser.username">Username</xsl:variable>
	<xsl:variable name="AddUser.email">E-mail</xsl:variable>
	<xsl:variable name="AddUser.emailConfirmation">Confirm e-mail</xsl:variable>
	<xsl:variable name="AddUser.password">Password</xsl:variable>
	<xsl:variable name="AddUser.passwordConfirmation">Confirm password</xsl:variable>
	<xsl:variable name="AddUser.createAccount">Create account</xsl:variable>	
	<xsl:variable name="AddUser.captchaConfirmation">Confirmation code</xsl:variable>
	<xsl:variable name="AddUser.regenerateCaptcha">Generate new code</xsl:variable>					

	<xsl:variable name="validationError.requiredField" select="'You need to fill in the field'" />
	<xsl:variable name="validationError.invalidFormat" select="'Invalid value in field'" />
	<xsl:variable name="validationError.tooShort" select="'Too short content in field'" />
	<xsl:variable name="validationError.tooLong" select="'Too long content in field'" />		
	<xsl:variable name="validationError.unknownError" select="'Unknown problem validating field'" />	

	<xsl:variable name="validationError.field.firstname">firstname</xsl:variable>
	<xsl:variable name="validationError.field.lastname">lastname</xsl:variable>
	<xsl:variable name="validationError.field.username">username</xsl:variable>
	<xsl:variable name="validationError.field.email">e-email</xsl:variable>
	<xsl:variable name="validationError.field.password">password</xsl:variable>

	<xsl:variable name="validationError.message.UsernameAlreadyTaken">The username you have entered is already taken</xsl:variable>
	<xsl:variable name="validationError.message.EmailAlreadyTaken">The email address you have entered is already taken</xsl:variable>
	<xsl:variable name="validationError.message.EmailConfirmationMismatch">The e-mail addresses you have entered don't match</xsl:variable>
	<xsl:variable name="validationError.message.PasswordConfirmationMismatch">The passwords you have entered don't match</xsl:variable>
	<xsl:variable name="validationError.message.InvalidCaptchaConfirmation">Invalid confirmation code</xsl:variable>
	<xsl:variable name="validationError.message.UnableToProcessEmail">Unable to register the account. Contact the system administrator</xsl:variable>
	<xsl:variable name="validationError.message.InvalidEmailAddress">Unable to register the account. Check the email address. If the problem remains, contact the system administrator</xsl:variable>
	<xsl:variable name="validationError.message.NoEmailSendersFound">Unable to register the account. Contact the system administrator</xsl:variable>
	<xsl:variable name="validationError.message.unknownFault">An unknown error has occurred!</xsl:variable>		
	<xsl:variable name="validationError.message.NoUserConditionConfirmation">You must confirm the user conditions</xsl:variable>	
				
</xsl:stylesheet>