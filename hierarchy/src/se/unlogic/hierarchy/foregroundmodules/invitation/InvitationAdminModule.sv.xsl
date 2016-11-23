<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:import href="InvitationAdminModuleTemplates.xsl"/>

	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:variable name="i18n.InvitationTypes">Inbjudningstyper</xsl:variable>
	<xsl:variable name="i18n.NoInvitationTypesFound">Inga inbjudningstyper funna</xsl:variable>
	<xsl:variable name="i18n.AddInvitationType">Lägg till inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.Invitations">Inbjudningar</xsl:variable>
	<xsl:variable name="i18n.NoInvitationsFound">Inga inbjudningar hittades.</xsl:variable>
	<xsl:variable name="i18n.AddInvitation">Lägg till inbjudan</xsl:variable>
	<xsl:variable name="i18n.ShowUpdateInvitationType">Visa/uppdatera inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.DeleteInvitationType">Ta bort inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.ShowUpdateInvitationFor">Visa/uppdatera inbjudan för</xsl:variable>
	<xsl:variable name="i18n.DeleteInvitationFor">Ta bort inbjudan för</xsl:variable>
	<xsl:variable name="i18n.ThisInvitationHasBeenSent">Denna inbjudan har skickats</xsl:variable>
	<xsl:variable name="i18n.times">gånger</xsl:variable>
	<xsl:variable name="i18n.TheLastOneWasSent">Den senaste sändes</xsl:variable>
	<xsl:variable name="i18n.ThisInvitationHasNotBeenSentYet">Denna inbjudan har inte skickats ännu</xsl:variable>
	<xsl:variable name="i18n.SendInvitationTo">Skicka inbjudan till</xsl:variable>
	<xsl:variable name="i18n.Name">Namn</xsl:variable>
	<xsl:variable name="i18n.Subject">Ämne</xsl:variable>
	<xsl:variable name="i18n.SenderName">Avsändarnamn</xsl:variable>
	<xsl:variable name="i18n.SenderEmailAddress">Avsändarens epost-adress</xsl:variable>
	<xsl:variable name="i18n.Message">E-post meddelande</xsl:variable>
	<xsl:variable name="i18n.Tags">Taggar som kan användas i e-post meddelandet</xsl:variable>
	<xsl:variable name="i18n.RecipientFirstname">Mottagarens förnamn</xsl:variable>
	<xsl:variable name="i18n.RecipientLastname">Mottagarens efternamn</xsl:variable>
	<xsl:variable name="i18n.RecipientEmail">Mottagarens epost-adress</xsl:variable>
	<xsl:variable name="i18n.SendInvitationImmediately">Skicka inbjudan direkt</xsl:variable>
	<xsl:variable name="i18n.UpateInvitation">Ändra inbjudan</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara ändringar</xsl:variable>
	<xsl:variable name="i18n.UpdateInvitationType">Ändra inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.Groups">Grupper</xsl:variable>
	<xsl:variable name="i18n.InvitationLink">Inbjudningslänk</xsl:variable>
	<xsl:variable name="i18n.Firstname">Förnamn</xsl:variable>
	<xsl:variable name="i18n.Lastname">Efternamn</xsl:variable>
	<xsl:variable name="i18n.Email">E-post</xsl:variable>
	<xsl:variable name="i18n.InvitationType">Inbjudningstyp</xsl:variable>
	<xsl:variable name="i18n.Link">Länk</xsl:variable>
	<xsl:variable name="i18n.name">namn</xsl:variable>
	<xsl:variable name="i18n.subject">ämne</xsl:variable>
	<xsl:variable name="i18n.message">meddelande</xsl:variable>
	<xsl:variable name="i18n.senderName">Avsändarnamn</xsl:variable>
	<xsl:variable name="i18n.senderEmailAddress">Avsändarens epost-adress</xsl:variable>
	<xsl:variable name="i18n.email">e-post</xsl:variable>
	<xsl:variable name="i18n.firstname">förnamn</xsl:variable>
	<xsl:variable name="i18n.lastname">efternamn</xsl:variable>
		
	<xsl:variable name="i18n.ValidationError.RequiredField">Du måste fylla i fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFieldValueFormat">Felaktigt format på värdet i fältet</xsl:variable>
	<xsl:variable name="i18n.ValidationError.UnknownFieldError">Okänt fel på fältet</xsl:variable>
	
	<xsl:variable name="i18n.ValidationError.Message.InvitationNotFound">Inbjudan kunde inte hittas</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.InvitationTypeNotFound">Inbjudningstypen kunde inte hittas</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.EmailAlreadyInvited">Det finns redan en användare eller inbjudan med denna epost-adress</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UnknownError">Ett okänt fel har uppstått</xsl:variable>
	
	<xsl:variable name="i18n.registrationText">Meddelande vid registrering</xsl:variable>
	<xsl:variable name="i18n.registeredText">Meddelande vid slutförd registrering</xsl:variable>
	<xsl:variable name="i18n.SendUnsentInvitations">Skicka ej skickade inbjudningar</xsl:variable>
	<xsl:variable name="i18n.ResendSentInvitations">Skicka om alla skickade inbjudningar</xsl:variable>
</xsl:stylesheet>
