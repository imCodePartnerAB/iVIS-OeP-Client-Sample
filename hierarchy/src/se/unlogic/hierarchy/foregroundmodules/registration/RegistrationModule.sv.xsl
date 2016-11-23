<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="RegistrationModuleTemplates.xsl"/>

	<xsl:variable name="defaultUserConditions">Behave!</xsl:variable>

	<xsl:variable name="defaultRegistrationMessage">Fyll i formuläret nedan för att skapa ett konto.</xsl:variable>
	<xsl:variable name="defaultRegisteredMessage">Ditt konto har skapats och ett e-post meddelande har skickats till din e-post adress med mer informatiom om hur du aktiverar ditt konto.</xsl:variable>
	<xsl:variable name="defaultAccountEnabledMessage">Ditt konto är aktiverat, du kan nu logga in.</xsl:variable>
	<xsl:variable name="defaultEmailSubject">Konto bekräftelse</xsl:variable>
	<xsl:variable name="defaultEmailText">
Hej $user.firstname!

Du får detta meddelande på grund att denna e-post adress använts vid registrering av konto hos http://somesite.

Klicka på länken nedan för att aktivera ditt konto:

$confirmation-link

Om du inte registrerade dig på http://somesite kan bortse från detta e-post meddelande.

Ditt konto kommer automatiskt att tas bort om $confirmation-timeout dagar om du inte aktiverar det.

/Someone
	</xsl:variable>

	<xsl:variable name="defaultNewAccountNotificationSubject">Ny användare registrerad</xsl:variable>
	<xsl:variable name="defaultNewAccountNotificationText">
Hej $subscriber.firstname,

En ny användare har registrerat sig: $user.firstname "$user.username" $user.lastname

/Registreringsmodulen
	</xsl:variable>

	<xsl:variable name="AddUser.userConditions">Användarvillkor</xsl:variable>
	<xsl:variable name="AddUser.showUserConditions">Visa användarvillkoren</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part1">Jag har tagit del av</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part2">användarvillkoren</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part3">och godkänner dessa</xsl:variable>

	<xsl:variable name="AddUser.firstname">Förnamn</xsl:variable>
	<xsl:variable name="AddUser.lastname">Efternamn</xsl:variable>
	<xsl:variable name="AddUser.username">Användarnamn</xsl:variable>
	<xsl:variable name="AddUser.email">E-post</xsl:variable>
	<xsl:variable name="AddUser.emailConfirmation">Bekräfta e-post</xsl:variable>
	<xsl:variable name="AddUser.password">Lösenord</xsl:variable>
	<xsl:variable name="AddUser.passwordConfirmation">Bekräfta lösenord</xsl:variable>
	<xsl:variable name="AddUser.createAccount">Skapa konto</xsl:variable>
	<xsl:variable name="AddUser.captchaConfirmation">Bildverifiering</xsl:variable>
	<xsl:variable name="AddUser.regenerateCaptcha">Generera ny bild</xsl:variable>			
							

	<xsl:variable name="validationError.requiredField" select="'Du måste fylla i fältet'" />
	<xsl:variable name="validationError.invalidFormat" select="'Felaktigt format på fältet'" />
	<xsl:variable name="validationError.tooShort" select="'För kort innehåll i fältet'" />
	<xsl:variable name="validationError.tooLong" select="'För långt innehåll i fältet'" />		
	<xsl:variable name="validationError.unknownError" select="'Okänt fel på fältet'" />	

	<xsl:variable name="validationError.field.firstname">förnamn</xsl:variable>
	<xsl:variable name="validationError.field.lastname">efternamn</xsl:variable>
	<xsl:variable name="validationError.field.username">användarnamn</xsl:variable>
	<xsl:variable name="validationError.field.email">e-post</xsl:variable>
	<xsl:variable name="validationError.field.password">lösenord</xsl:variable>
	
	<xsl:variable name="validationError.message.UsernameAlreadyTaken">Användarnamnet du valt är upptaget</xsl:variable>
	<xsl:variable name="validationError.message.EmailAlreadyTaken">E-postadressen du valt är upptagen</xsl:variable>
	<xsl:variable name="validationError.message.EmailConfirmationMismatch">E-postadresserna överensstämmer inte</xsl:variable>
	<xsl:variable name="validationError.message.PasswordConfirmationMismatch">Lösenorden överensstämmer inte</xsl:variable>
	<xsl:variable name="validationError.message.InvalidCaptchaConfirmation">Felaktig bildverifiering</xsl:variable>
	<xsl:variable name="validationError.message.UnableToProcessEmail">Det gick inte att skapa kontot, kontakta systemadministratören för mer information</xsl:variable>
	<xsl:variable name="validationError.message.InvalidEmailAddress">Det gick inte att skapa kontot, kontakta systemadministratören för mer information</xsl:variable>
	<xsl:variable name="validationError.message.NoEmailSendersFound">Det gick inte att skapa kontot, kontakta systemadministratören för mer information</xsl:variable>		
	<xsl:variable name="validationError.message.unknownFault">Ett okänt fel har uppstått!</xsl:variable>
	<xsl:variable name="validationError.message.NoUserConditionConfirmation">Du måste godkänna användarvillkoren</xsl:variable>					
</xsl:stylesheet>