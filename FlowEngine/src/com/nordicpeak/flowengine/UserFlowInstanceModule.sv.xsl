<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:import href="BaseFlowBrowserModule.sv.xsl"/>
	
	<xsl:include href="UserFlowInstanceModuleTemplates.xsl"/>
	
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>
	
	<xsl:variable name="i18n.MyFlowInstances">Mina �renden</xsl:variable>
	
	<xsl:variable name="i18n.FlowName">E-tj�nst</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceID">�rendenummer</xsl:variable>
	<xsl:variable name="i18n.Status">Status</xsl:variable>
	<xsl:variable name="i18n.Updated">Senast sparat</xsl:variable>
	<xsl:variable name="i18n.LastEvent">Senaste h�ndelse</xsl:variable>
	<xsl:variable name="i18n.Date">Datum</xsl:variable>
	
	<xsl:variable name="i18n.FirstSubmitted">Inskickat</xsl:variable>
	<xsl:variable name="i18n.LastSubmitted">Senast kompletterat</xsl:variable>
	<xsl:variable name="i18n.LastChanged">Senast �ndrat</xsl:variable>
	<xsl:variable name="i18n.Managers">Handl�ggare</xsl:variable>
	<xsl:variable name="i18n.Description">Beskrivning</xsl:variable>
	<xsl:variable name="i18n.by">av</xsl:variable>
	<xsl:variable name="i18n.NoManager">Ingen handl�ggare tilldelad</xsl:variable>
	<xsl:variable name="i18n.Choose">Till �rende</xsl:variable>
	<xsl:variable name="i18n.NotEnabled">Ej tillg�nglig</xsl:variable>
	
	<xsl:variable name="i18n.Continue">Forts�tt</xsl:variable>
	<xsl:variable name="i18n.Delete">Ta bort</xsl:variable>
	
	<xsl:variable name="i18n.Details">Detaljer</xsl:variable>
	<xsl:variable name="i18n.ExternalMessages">Meddelanden</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceEvents">�rendehistorik</xsl:variable>
	<xsl:variable name="i18n.NoExternalMessages">Inga meddelanden</xsl:variable>
	<xsl:variable name="i18n.NewMessage">Nytt meddelande</xsl:variable>
	<xsl:variable name="i18n.Action">H�ndelse</xsl:variable>
	<xsl:variable name="i18n.Person">Person</xsl:variable>
	<xsl:variable name="i18n.NoEvents">Ingen �rendehistorik</xsl:variable>
	
	<xsl:variable name="i18n.Message">Meddelande</xsl:variable>
	<xsl:variable name="i18n.AttachFiles">Bifoga filer</xsl:variable>
	<xsl:variable name="i18n.Close">St�ng</xsl:variable>
	<xsl:variable name="i18n.Cancel">Avbryt</xsl:variable>
	<xsl:variable name="i18n.ChooseFiles">V�lj filer</xsl:variable>
	<xsl:variable name="i18n.MaximumFileSize">Maximal filstorlek vid uppladdning</xsl:variable>
	<xsl:variable name="i18n.SubmitMessage">Skicka meddelande</xsl:variable>
	
	<xsl:variable name="i18n.DeleteFile">Ta bort fil</xsl:variable>

	<xsl:variable name="i18n.ShowFlowInstance">Visa �rende</xsl:variable>
	<xsl:variable name="i18n.Or">Eller</xsl:variable>
	<xsl:variable name="i18n.UpdateFlowInstance">�ndra �rende</xsl:variable>
	
	<xsl:variable name="i18n.Help">Hj�lp</xsl:variable>
	
	<xsl:variable name="i18n.Poster">S�kande</xsl:variable>
	
	<xsl:variable name="i18n.MyMessages">Mina meddelanden</xsl:variable>
	<xsl:variable name="i18n.ToFlowInstance">Till �rende</xsl:variable>
	
	<xsl:variable name="i18n.SavedFlowInstancesTitle">Ej inskickade �renden</xsl:variable>
	<xsl:variable name="i18n.SavedFlowInstances.Part1">Du har</xsl:variable>
	<xsl:variable name="i18n.SavedFlowInstances.Part2">�rende</xsl:variable>
	<xsl:variable name="i18n.SavedFlowInstances.Part2.Plural">�renden</xsl:variable>
	<xsl:variable name="i18n.SavedFlowInstances.Part3">som inte har skickats in</xsl:variable>
	<xsl:variable name="i18n.SavedFlowInstancesHelp">
<h2 class="h1 full" data-icon-before="?">Sparade �renden</h2>
De �renden som du har p�b�rjat kan du forts�tta arbeta med genom att klicka p� "Forts�tt". Om du vill avbryta �rendet klicka p� den r�da knappen i anslutning till �rendet.
	</xsl:variable>
	
	<xsl:variable name="i18n.SubmittedFlowInstances">P�g�ende �renden</xsl:variable>
	<xsl:variable name="i18n.NoSubmittedFlowInstances">Du har inga p�g�ende �renden</xsl:variable>
	<xsl:variable name="i18n.SubmittedFlowInstancesHelp">
<h2 class="h1 full" data-icon-before="?">P�g�ende �renden</h2>
P�g�ende �renden �r de �renden som du har skickat in. Klicka p� "Till �rende" f�r att komma till ditt �rende.
	</xsl:variable>
	
	<xsl:variable name="i18n.ArchivedFlowInstances">Avslutade �renden</xsl:variable>
	<xsl:variable name="i18n.NoArchivedFlowInstances">Du har inga avslutade �renden</xsl:variable>
	<xsl:variable name="i18n.ArchivedFlowInstancesHelp">
<h2 class="h1 full" data-icon-before="?">Avslutade �renden</h2>
S� l�nge e-tj�nsten f�r dina avslutade �renden finns tillg�ngliga kan du alltid titta p� ditt �rende genom att klicka p� "Till �rende"
</xsl:variable>
	
	<xsl:variable name="i18n.DeleteFlowInstanceConfirm">�r du s�ker p� att du vill avbryta �rendet</xsl:variable>
	
	<xsl:variable name="i18n.FlowInstancePreviewError">Ett fel uppstod vid visning av �rendet.</xsl:variable>
	
	<xsl:variable name="i18n.FileSizeLimitExceeded.part1">Filen </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part2"> har en storlek p� </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part3"> vilket �verskrider den maximalt till�tna filstorleken p�</xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part4">.</xsl:variable>
	
	<xsl:variable name="i18n.validationError.UnableToParseRequest">Ett ok�nt fel uppstod vid filuppladdningen</xsl:variable>
	<xsl:variable name="i18n.validationError.MessageRequired">Du m�ste skriva ett meddelande</xsl:variable>
	<xsl:variable name="i18n.validationError.MessageToLong">Du har skrivit ett f�r l�ngt meddelande</xsl:variable>
	<xsl:variable name="i18n.validationError.MessageToShort">Du har skrivit ett f�r kort meddelande</xsl:variable>
	
	<xsl:variable name="i18n.DownloadFlowInstancePDF">H�mta kvittens i PDF-format</xsl:variable>
	<xsl:variable name="i18n.WaitingForMultisign">V�ntar p� signering</xsl:variable>
	<xsl:variable name="i18n.WaitingForPayment">V�ntar p� betalning</xsl:variable>
	<xsl:variable name="i18n.DownloadFlowInstanceXML">H�mta ans�kan i XML-format</xsl:variable>
</xsl:stylesheet>
