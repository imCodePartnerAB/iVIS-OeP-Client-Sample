<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="InvitationAdminModuleTemplates.xsl"/>
	
	<xsl:variable name="i18n.InvitationTypes">Invitation types</xsl:variable>
	<xsl:variable name="i18n.NoInvitationTypesFound">No invitation types found</xsl:variable>
	<xsl:variable name="i18n.AddInvitationType">Add invitation type</xsl:variable>
	<xsl:variable name="i18n.Invitations">Invitations</xsl:variable>
	<xsl:variable name="i18n.NoInvitationsFound">No invitations found</xsl:variable>
	<xsl:variable name="i18n.AddInvitation">Add invitation</xsl:variable>
	<xsl:variable name="i18n.ShowUpdateInvitationType">Show/update invation type</xsl:variable>
	<xsl:variable name="i18n.DeleteInvitationType">Delete invitation type</xsl:variable>
	<xsl:variable name="i18n.ShowUpdateInvitationFor">Show/update invation for</xsl:variable>
	<xsl:variable name="i18n.DeleteInvitationFor">Delete invitation for</xsl:variable>
	<xsl:variable name="i18n.ThisInvitationHasBeenSent">This invitation has been sent</xsl:variable>
	<xsl:variable name="i18n.times">times</xsl:variable>
	<xsl:variable name="i18n.TheLastOneWasSent">The last one was sent</xsl:variable>
	<xsl:variable name="i18n.ThisInvitationHasNotBeenSentYet">This invitation has not been sent yet</xsl:variable>
	<xsl:variable name="i18n.SendInvitationTo">Send invitation to</xsl:variable>
	<xsl:variable name="i18n.Name">Name</xsl:variable>
	<xsl:variable name="i18n.Subject">Subject</xsl:variable>
	<xsl:variable name="i18n.SenderName">Sender name</xsl:variable>
	<xsl:variable name="i18n.SenderEmailAddress">Sender e-mail address</xsl:variable>
	<xsl:variable name="i18n.Message">E-mail message</xsl:variable>
	<xsl:variable name="i18n.Tags">Tags usable in e-mail message</xsl:variable>
	<xsl:variable name="i18n.RecipientFirstname">Recipient firstname</xsl:variable>
	<xsl:variable name="i18n.RecipientLastname">Recipient lastname</xsl:variable>
	<xsl:variable name="i18n.RecipientEmail">Recipient email</xsl:variable>
	<xsl:variable name="i18n.SendInvitationImmediately">Send invitation immediately</xsl:variable>
	<xsl:variable name="i18n.UpateInvitation">Update invitation</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Save changes</xsl:variable>
	<xsl:variable name="i18n.UpdateInvitationType">Update invitation type</xsl:variable>
	<xsl:variable name="i18n.Groups">Groups</xsl:variable>
	<xsl:variable name="i18n.InvitationLink">Invitation link</xsl:variable>
	<xsl:variable name="i18n.Firstname">Firstname</xsl:variable>
	<xsl:variable name="i18n.Lastname">Lastname</xsl:variable>
	<xsl:variable name="i18n.Email">Email</xsl:variable>
	<xsl:variable name="i18n.InvitationType">Invitation type</xsl:variable>
	<xsl:variable name="i18n.Link">Link</xsl:variable>
	<xsl:variable name="i18n.name">name</xsl:variable>
	<xsl:variable name="i18n.subject">subject</xsl:variable>
	<xsl:variable name="i18n.message">message</xsl:variable>
	<xsl:variable name="i18n.senderName">sender name</xsl:variable>
	<xsl:variable name="i18n.senderEmailAddress">sender email address</xsl:variable>
	<xsl:variable name="i18n.email">email</xsl:variable>
	<xsl:variable name="i18n.firstname">firstname</xsl:variable>
	<xsl:variable name="i18n.lastname">lastname</xsl:variable>
		
	<xsl:variable name="i18n.ValidationError.RequiredField">You need to fill in the field</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InvalidFieldValueFormat">Invalid value in field</xsl:variable>
	<xsl:variable name="i18n.ValidationError.UnknownFieldError">Unknown problem validating field</xsl:variable>
	
	<xsl:variable name="i18n.ValidationError.Message.InvitationNotFound">The invitation was not found</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.InvitationTypeNotFound">The invitation type was not found</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.EmailAlreadyInvited">There already exists a user or invitation with this e-mail address</xsl:variable>
	<xsl:variable name="i18n.ValidationError.Message.UnknownError">An unknown error occurred</xsl:variable>
	
	<xsl:variable name="i18n.registrationText">Registration welcome message</xsl:variable>
	<xsl:variable name="i18n.registeredText">Registration completed message</xsl:variable>
	<xsl:variable name="i18n.SendUnsentInvitations">Send unsent invitations</xsl:variable>
	<xsl:variable name="i18n.ResendSentInvitations">Resend all sent invitations</xsl:variable>
</xsl:stylesheet>
