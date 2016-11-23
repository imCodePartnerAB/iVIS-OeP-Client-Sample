<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="RegistrationModuleTemplates.xsl"/>

	<xsl:variable name="defaultUserConditions">Behave!</xsl:variable>

	<xsl:variable name="defaultRegistrationMessage">Fyll i formul�ret nedan f�r att skapa ett konto.</xsl:variable>
	<xsl:variable name="defaultRegisteredMessage">Ditt konto har skapats och ett e-post meddelande har skickats till din e-post adress med mer informatiom om hur du aktiverar ditt konto.</xsl:variable>
	<xsl:variable name="defaultAccountEnabledMessage">Ditt konto �r aktiverat, du kan nu logga in.</xsl:variable>
	<xsl:variable name="defaultEmailSubject">Konto bekr�ftelse</xsl:variable>
	<xsl:variable name="defaultEmailText">
Hej $user.firstname!

Du f�r detta meddelande p� grund att denna e-post adress anv�nts vid registrering av konto hos http://somesite.

Klicka p� l�nken nedan f�r att aktivera ditt konto:

$confirmation-link

Om du inte registrerade dig p� http://somesite kan bortse fr�n detta e-post meddelande.

Ditt konto kommer automatiskt att tas bort om $confirmation-timeout dagar om du inte aktiverar det.

/Someone
	</xsl:variable>

	<xsl:variable name="defaultNewAccountNotificationSubject">Ny anv�ndare registrerad</xsl:variable>
	<xsl:variable name="defaultNewAccountNotificationText">
Hej $subscriber.firstname,

En ny anv�ndare har registrerat sig: $user.firstname "$user.username" $user.lastname

/Registreringsmodulen
	</xsl:variable>

	<xsl:variable name="AddUser.userConditions">Anv�ndarvillkor</xsl:variable>
	<xsl:variable name="AddUser.showUserConditions">Visa anv�ndarvillkoren</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part1">Jag har tagit del av</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part2">anv�ndarvillkoren</xsl:variable>
	<xsl:variable name="AddUser.userConditions.part3">och godk�nner dessa</xsl:variable>

	<xsl:variable name="AddUser.firstname">F�rnamn</xsl:variable>
	<xsl:variable name="AddUser.lastname">Efternamn</xsl:variable>
	<xsl:variable name="AddUser.username">Anv�ndarnamn</xsl:variable>
	<xsl:variable name="AddUser.email">E-post</xsl:variable>
	<xsl:variable name="AddUser.emailConfirmation">Bekr�fta e-post</xsl:variable>
	<xsl:variable name="AddUser.password">L�senord</xsl:variable>
	<xsl:variable name="AddUser.passwordConfirmation">Bekr�fta l�senord</xsl:variable>
	<xsl:variable name="AddUser.createAccount">Skapa konto</xsl:variable>
	<xsl:variable name="AddUser.captchaConfirmation">Bildverifiering</xsl:variable>
	<xsl:variable name="AddUser.regenerateCaptcha">Generera ny bild</xsl:variable>			
							

	<xsl:variable name="validationError.requiredField" select="'Du m�ste fylla i f�ltet'" />
	<xsl:variable name="validationError.invalidFormat" select="'Felaktigt format p� f�ltet'" />
	<xsl:variable name="validationError.tooShort" select="'F�r kort inneh�ll i f�ltet'" />
	<xsl:variable name="validationError.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'" />		
	<xsl:variable name="validationError.unknownError" select="'Ok�nt fel p� f�ltet'" />	

	<xsl:variable name="validationError.field.firstname">f�rnamn</xsl:variable>
	<xsl:variable name="validationError.field.lastname">efternamn</xsl:variable>
	<xsl:variable name="validationError.field.username">anv�ndarnamn</xsl:variable>
	<xsl:variable name="validationError.field.email">e-post</xsl:variable>
	<xsl:variable name="validationError.field.password">l�senord</xsl:variable>
	
	<xsl:variable name="validationError.message.UsernameAlreadyTaken">Anv�ndarnamnet du valt �r upptaget</xsl:variable>
	<xsl:variable name="validationError.message.EmailAlreadyTaken">E-postadressen du valt �r upptagen</xsl:variable>
	<xsl:variable name="validationError.message.EmailConfirmationMismatch">E-postadresserna �verensst�mmer inte</xsl:variable>
	<xsl:variable name="validationError.message.PasswordConfirmationMismatch">L�senorden �verensst�mmer inte</xsl:variable>
	<xsl:variable name="validationError.message.InvalidCaptchaConfirmation">Felaktig bildverifiering</xsl:variable>
	<xsl:variable name="validationError.message.UnableToProcessEmail">Det gick inte att skapa kontot, kontakta systemadministrat�ren f�r mer information</xsl:variable>
	<xsl:variable name="validationError.message.InvalidEmailAddress">Det gick inte att skapa kontot, kontakta systemadministrat�ren f�r mer information</xsl:variable>
	<xsl:variable name="validationError.message.NoEmailSendersFound">Det gick inte att skapa kontot, kontakta systemadministrat�ren f�r mer information</xsl:variable>		
	<xsl:variable name="validationError.message.unknownFault">Ett ok�nt fel har uppst�tt!</xsl:variable>
	<xsl:variable name="validationError.message.NoUserConditionConfirmation">Du m�ste godk�nna anv�ndarvillkoren</xsl:variable>					
</xsl:stylesheet>