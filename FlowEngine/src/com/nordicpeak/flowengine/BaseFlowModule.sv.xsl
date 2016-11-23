<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:import href="BaseFlowModuleTemplates.xsl"/>
	
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:variable name="i18n.FlowInstanceID">�rendenummer</xsl:variable>
	
	<xsl:variable name="i18n.previousStep">F�reg�ende steg</xsl:variable>
	<xsl:variable name="i18n.nextStep">N�sta steg</xsl:variable>
	
	<xsl:variable name="i18n.SaveBoxDescription">Vill du forts�tta med din ans�kan vid ett senare tillf�lle s� kan du spara den n�r som helst.</xsl:variable>
	<xsl:variable name="i18n.ShortSaveBoxDescription">Du kan spara din ans�kan f�r att forts�tta senare.</xsl:variable>
	<xsl:variable name="i18n.Save">Spara ans�kan</xsl:variable>
	<xsl:variable name="i18n.save">Spara</xsl:variable>

	<xsl:variable name="i18n.PreviewNotEnabledForCurrentFlow">Det �r inte m�jligt att f�rhandsgranska den h�r e-tj�nsten.</xsl:variable>
	<xsl:variable name="i18n.PreviewOnlyAvailableWhenFlowFullyPopulated">F�r att kunna f�rhansdsgranska m�ste du f�rst fylla i samtliga steg.</xsl:variable>
	<xsl:variable name="i18n.SubmitOnlyAvailableWhenFlowFullyPopulated">F�r att kunna spara och skicka in m�ste du f�rst fylla i samtliga steg.</xsl:variable>
	<xsl:variable name="i18n.FileUploadException.part1">Det gick inte att tolka informationen fr�n din webbl�sare. Kontrollera att du inte bifogat mer �n </xsl:variable>
	<xsl:variable name="i18n.FileUploadException.part2"> MB.</xsl:variable>
	<xsl:variable name="i18n.UnknownValidationError">Ett ok�nt valideringsfel har uppst�tt!</xsl:variable>
	<xsl:variable name="i18n.preview">F�rhandsgranska</xsl:variable>
	<xsl:variable name="i18n.submit">Skicka in</xsl:variable>
	<xsl:variable name="i18n.signAndSubmit">Signera och skicka in</xsl:variable>
	<xsl:variable name="i18n.payHeader">Betala</xsl:variable>
	<xsl:variable name="i18n.payAndSubmit">Godk�nn betalning</xsl:variable>
	
	<xsl:variable name="i18n.flowInstance">ans�kan</xsl:variable>
	<xsl:variable name="i18n.noAnsweredQueriesInThisStep">Det finns inga besvarade fr�gor i detta steg.</xsl:variable>
	
	<xsl:variable name="i18n.FlowInstanceSaved">Din ans�kan sparades! Om du vill forts�tta senare hittar du din ans�kan under Mina �renden.</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceManagerSubmitted">Din ans�kan �r nu inskickad.</xsl:variable>
	<xsl:variable name="i18n.Receipt">Kvittens</xsl:variable>
	<xsl:variable name="i18n.PostedBy">Inskickat av</xsl:variable>
	<xsl:variable name="i18n.Print">Skriv ut</xsl:variable>
	<xsl:variable name="i18n.DownloadPDF">H�mta PDF</xsl:variable>
	
	<xsl:variable name="i18n.StepDescription.Part1">Steg</xsl:variable>
	<xsl:variable name="i18n.StepDescription.Part2">av</xsl:variable>
	
	<xsl:variable name="i18n.UnableToPopulateQueryInstance">Ett fel vid populering av ans�kan!</xsl:variable>
	<xsl:variable name="i18n.UnableToSaveQueryInstance">Ett fel uppstod n�r ans�kan skulle sparas!</xsl:variable>
	<xsl:variable name="i18n.UnableToResetQueryInstance">Ett fel uppstod n�r ans�kan skulle rensas!</xsl:variable>
	<xsl:variable name="i18n.NoQueriesInCurrentStep">Det finns inga fr�gor att besvara i detta steg.</xsl:variable>
	
	<xsl:variable name="i18n.AjaxLoading">Var god v�nta</xsl:variable>
	<xsl:variable name="i18n.AjaxCancel">Avbryt</xsl:variable>
	<xsl:variable name="i18n.AjaxRetry">F�rs�k igen</xsl:variable>
	<xsl:variable name="i18n.AjaxReload">Ladda om sidan</xsl:variable>
	<xsl:variable name="i18n.UnExpectedAjaxError">Ett ov�ntat fel har intr�ffat!</xsl:variable>
	<xsl:variable name="i18n.UnExpectedAjaxErrorDescription">Anv�nd knapparna nedan f�r att f�rs�ka igen eller ladda om sidan.</xsl:variable>
	
	<xsl:variable name="i18n.SubmitLoading">Var god v�nta</xsl:variable>
	
	<xsl:variable name="i18n.SigningProviderNotFoundError">Signeringstj�nsten kunde inte hittas.</xsl:variable>
	
	<xsl:variable name="i18n.Previous">F�reg�ende</xsl:variable>
	<xsl:variable name="i18n.Next">N�sta</xsl:variable>
	
	<xsl:variable name="i18n.saveBtnSuffix">ans�kan</xsl:variable>
	
	<xsl:variable name="i18n.FlowInstanceConcurrentlyModified">Denna ans�kan har �ndrats sedan du �ppnade den och kan d�rf�r inte sparas. Klicka h�r f�r att �ppna den senaste versionen, observera att dina eventuella �ndringar kommer att g� f�rlorade.</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceConcurrentlyModifiedConfirm">�r du s�ker p� att du vill �ppna senaste versionen och d�rmed f�rlora eventuella �ndringar?</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceConcurrentlyModifiedLinkTitle">�ppna den senaste versionen</xsl:variable>
	<xsl:variable name="i18n.MultiSignStatus">Flerparts-signering</xsl:variable>
	
	<xsl:variable name="i18n.SubmittedEvent">Inskickad</xsl:variable>
	<xsl:variable name="i18n.SignedEvent">Signerad</xsl:variable>
	<xsl:variable name="i18n.PayedEvent">Betald</xsl:variable>
	<xsl:variable name="i18n.UpdatedEvent">�ndrad</xsl:variable>
	<xsl:variable name="i18n.StatusUpdatedEvent">Status �ndrad</xsl:variable>
	<xsl:variable name="i18n.ManagersUpdatedEvent">Handl�ggare �ndrad</xsl:variable>
	<xsl:variable name="i18n.CustomerNotificationEvent">Notifiering skickad</xsl:variable>
	<xsl:variable name="i18n.CustomerMessageSentEvent">Meddelande skickat till handl�ggare</xsl:variable>
	<xsl:variable name="i18n.ManagerMessageSentEvent">Meddelande skickat till kund</xsl:variable>
	<xsl:variable name="i18n.OtherEvent">Annan h�ndelse</xsl:variable>
	
</xsl:stylesheet>
