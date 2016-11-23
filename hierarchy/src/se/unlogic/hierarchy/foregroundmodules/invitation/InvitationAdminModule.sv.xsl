<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:import href="InvitationAdminModuleTemplates.xsl"/>

	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:variable name="i18n.InvitationTypes">Inbjudningstyper</xsl:variable>
	<xsl:variable name="i18n.NoInvitationTypesFound">Inga inbjudningstyper funna</xsl:variable>
	<xsl:variable name="i18n.AddInvitationType">L�gg till inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.Invitations">Inbjudningar</xsl:variable>
	<xsl:variable name="i18n.NoInvitationsFound">Inga inbjudningar hittades.</xsl:variable>
	<xsl:variable name="i18n.AddInvitation">L�gg till inbjudan</xsl:variable>
	<xsl:variable name="i18n.ShowUpdateInvitationType">Visa/uppdatera inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.DeleteInvitationType">Ta bort inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.ShowUpdateInvitationFor">Visa/uppdatera inbjudan f�r</xsl:variable>
	<xsl:variable name="i18n.DeleteInvitationFor">Ta bort inbjudan f�r</xsl:variable>
	<xsl:variable name="i18n.ThisInvitationHasBeenSent">Denna inbjudan har skickats</xsl:variable>
	<xsl:variable name="i18n.times">g�nger</xsl:variable>
	<xsl:variable name="i18n.TheLastOneWasSent">Den senaste s�ndes</xsl:variable>
	<xsl:variable name="i18n.ThisInvitationHasNotBeenSentYet">Denna inbjudan har inte skickats �nnu</xsl:variable>
	<xsl:variable name="i18n.SendInvitationTo">Skicka inbjudan till</xsl:variable>
	<xsl:variable name="i18n.Name">Namn</xsl:variable>
	<xsl:variable name="i18n.Subject">�mne</xsl:variable>
	<xsl:variable name="i18n.SenderName">Avs�ndarnamn</xsl:variable>
	<xsl:variable name="i18n.SenderEmailAddress">Avs�ndarens epost-adress</xsl:variable>
	<xsl:variable name="i18n.Message">E-post meddelande</xsl:variable>
	<xsl:variable name="i18n.Tags">Taggar som kan anv�ndas i e-post meddelandet</xsl:variable>
	<xsl:variable name="i18n.RecipientFirstname">Mottagarens f�rnamn</xsl:variable>
	<xsl:variable name="i18n.RecipientLastname">Mottagarens efternamn</xsl:variable>
	<xsl:variable name="i18n.RecipientEmail">Mottagarens epost-adress</xsl:variable>
	<xsl:variable name="i18n.SendInvitationImmediately">Skicka inbjudan direkt</xsl:variable>
	<xsl:variable name="i18n.UpateInvitation">�ndra inbjudan</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara �ndringar</xsl:variable>
	<xsl:variable name="i18n.UpdateInvitationType">�ndra inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.Groups">Grupper</xsl:variable>
	<xsl:variable name="i18n.InvitationLink">Inbjudningsl�nk</xsl:variable>
	<xsl:variable name="i18n.Firstname">F�rnamn</xsl:variable>
	<xsl:variable name="i18n.Lastname">Efternamn</xsl:variable>
	<xsl:variable name="i18n.Email">E-post</xsl:variable>
	<xsl:variable name="i18n.InvitationType">Inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.Link">L�nk</xsl:variable>
	<xsl:variable name="i18n.name">namn</xsl:variable>
	<xsl:variable name="i18n.subject">�mne</xsl:variable>
	<xsl:variable name="i18n.message">meddelande</xsl:variable>
	<xsl:variable name="i18n.senderName">Avs�ndarnamn</xsl:variable>
	<xsl:variable name="i18n.senderEmailAddress">Avs�ndarens epost-adress</xsl:variable>
	<xsl:variable name="i18n.email">e-post</xsl:variable>
	<xsl:variable name="i18n.firstname">f�rnamn</xsl:variable>
	<xsl:variable name="i18n.lastname">efternamn</xsl:variable>
		
	<xsl:variable name="i18n.ValidationError.RequiredField">Du m�ste fylla i f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFieldValueFormat">Felaktigt format p� v�rdet i f�ltet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.UnknownFieldError">Ok�nt fel p� f�ltet</xsl:variable>
	
	<xsl:variable name="i18n.ValidationError.Message.InvitationNotFound">Inbjudan kunde inte hittas</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.InvitationTypeNotFound">Inbjudningstypen kunde inte hittas</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.EmailAlreadyInvited">Det finns redan en anv�ndare eller inbjudan med denna epost-adress</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UnknownError">Ett ok�nt fel har uppst�tt</xsl:variable>
	
	<xsl:variable name="i18n.registrationText">Meddelande vid registrering</xsl:variable>
	<xsl:variable name="i18n.registeredText">Meddelande vid slutf�rd registrering</xsl:variable>
	<xsl:variable name="i18n.SendUnsentInvitations">Skicka ej skickade inbjudningar</xsl:variable>
	<xsl:variable name="i18n.ResendSentInvitations">Skicka om alla skickade inbjudningar</xsl:variable>
</xsl:stylesheet>
