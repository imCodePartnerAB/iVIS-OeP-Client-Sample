<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	
	<xsl:import href="BaseFlowBrowserModule.sv.xsl"/>
	
	<xsl:include href="FlowInstanceAdminModuleTemplates.xsl"/>

<xsl:variable name="java.noManagersSelected">Ingen handl�ggare vald.</xsl:variable>

	<xsl:variable name="i18n.Help">Hj�lp</xsl:variable>
	
	<xsl:variable name="i18n.Emergency">akut</xsl:variable>
	<xsl:variable name="i18n.Owned">mina</xsl:variable>
	<xsl:variable name="i18n.Flagged">flaggade</xsl:variable>
	<xsl:variable name="i18n.Active">aktiva</xsl:variable>
	<xsl:variable name="i18n.UnAssigned">obehandlade</xsl:variable>
	
	<xsl:variable name="i18n.EmergencyTab">�renden du beh�ver agera p�</xsl:variable>
	<xsl:variable name="i18n.OwnedTab">Tilldelade</xsl:variable>
	<xsl:variable name="i18n.FlaggedTab">Flaggade</xsl:variable>
	<xsl:variable name="i18n.ActiveTab">Aktiva</xsl:variable>
	<xsl:variable name="i18n.UnAssignedTab">Obehandlade</xsl:variable>
	
	<xsl:variable name="i18n.Flow">E-tj�nst</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceID">�rendenummer</xsl:variable>
	<xsl:variable name="i18n.Status">Status</xsl:variable>
	<xsl:variable name="i18n.Date">Datum</xsl:variable>
	<xsl:variable name="i18n.Priority">Prioritet</xsl:variable>
	
	<xsl:variable name="i18n.CurrentStatus">Aktuell status:</xsl:variable>
	<xsl:variable name="i18n.SaveChanges">Spara �ndringar</xsl:variable>
	<xsl:variable name="i18n.SaveAndClose">Spara och st�ng</xsl:variable>
	
	<xsl:variable name="i18n.PrioritizedInstancesDescription.Part1">Du har</xsl:variable>
	<xsl:variable name="i18n.PrioritizedInstancesDescription.Part2">�renden som du beh�ver agera p�, klicka p� knappen "V�lj" f�r att forts�tta.</xsl:variable>
	<xsl:variable name="i18n.PrioritizedInstancesHelp">
<h2 data-icon-before="?" class="h1 full">�renden du beh�ver agera p�</h2>
Hj�lptext h�r...
	</xsl:variable>
	
	<xsl:variable name="i18n.UserAssignedInstancesDescription.Part1">Du har</xsl:variable>
	<xsl:variable name="i18n.UserAssignedInstancesDescription.Part2">tilldelade �renden, klicka p� knappen "V�lj" f�r att forts�tta.</xsl:variable>
	<xsl:variable name="i18n.UserAssignedInstancesHelp">
<h2 data-icon-before="?" class="h1 full">Tilldelade �renden</h2>
Hj�lptext h�r...
	</xsl:variable>
	
	<xsl:variable name="i18n.BookmarkedInstancesDescription.Part1">Det finns</xsl:variable>
	<xsl:variable name="i18n.BookmarkedInstancesDescription.Part2">�renden som du beh�ver agera p�, klicka p� knappen "V�lj" f�r att forts�tta.</xsl:variable>
	<xsl:variable name="i18n.BookmarkedInstancesHelp">
<h2 data-icon-before="?" class="h1 full">Flaggade �renden</h2>
Hj�lptext h�r...
	</xsl:variable>
	
	<xsl:variable name="i18n.ActiveInstancesDescription.Part1">Du har</xsl:variable>
	<xsl:variable name="i18n.ActiveInstancesDescription.Part2">aktiva �renden, klicka p� knappen "V�lj" f�r att forts�tta.</xsl:variable>
	<xsl:variable name="i18n.ActiveInstancesHelp">
<h2 data-icon-before="?" class="h1 full">Aktiva �renden</h2>
Hj�lptext h�r...
	</xsl:variable>
	
	<xsl:variable name="i18n.UnassignedInstancesDescription.Part1">Du har</xsl:variable>
	<xsl:variable name="i18n.UnassignedInstancesDescription.Part2">obehandlade �renden, klicka p� knappen "V�lj" f�r att forts�tta.</xsl:variable>
	<xsl:variable name="i18n.UnassignedInstancesHelp">
<h2 data-icon-before="?" class="h1 full">Obehandlade �renden</h2>
Hj�lptext h�r...
	</xsl:variable>
	
	<xsl:variable name="i18n.NoFlowInstances">Inga �renden</xsl:variable>
	<xsl:variable name="i18n.Choose">V�lj</xsl:variable>
	
	<xsl:variable name="i18n.High">H�g</xsl:variable>
	<xsl:variable name="i18n.Medium">Medium</xsl:variable>
	
	<xsl:variable name="i18n.SearchFlowInstance">S�k �rende</xsl:variable>
	<xsl:variable name="i18n.SearchFlowInstanceDescription">S�k bland �renden i systemet. Endast �renden du har beh�righet till visas.</xsl:variable>
	<xsl:variable name="i18n.SearchFormTitle">S�k p� �rendenummer, fastighetsnamn, personnummer</xsl:variable>
		
	
	<xsl:variable name="i18n.FirstSubmitted">Inskickat</xsl:variable>
	<xsl:variable name="i18n.LastSubmitted">Senast kompletterat</xsl:variable>
	<xsl:variable name="i18n.LastChanged">Senast �ndrat</xsl:variable>
	<xsl:variable name="i18n.Managers">Handl�ggare</xsl:variable>
	<xsl:variable name="i18n.LastSubmittedBy">Senast kompletterat av</xsl:variable>
	
	<xsl:variable name="i18n.by">av</xsl:variable>
	<xsl:variable name="i18n.NoManager">Ingen handl�ggare tilldelad</xsl:variable>
	
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
	<xsl:variable name="i18n.close">st�ng</xsl:variable>
	<xsl:variable name="i18n.Cancel">Avbryt</xsl:variable>
	<xsl:variable name="i18n.ChooseFiles">V�lj filer</xsl:variable>
	<xsl:variable name="i18n.MaximumFileSize">Maximal filstorlek vid uppladdning</xsl:variable>
	<xsl:variable name="i18n.SubmitMessage">Skicka meddelande</xsl:variable>
	
	<xsl:variable name="i18n.DeleteFile">Ta bort fil</xsl:variable>

	<xsl:variable name="i18n.ShowFlowInstance">Visa �rende</xsl:variable>
	<xsl:variable name="i18n.Or">Eller</xsl:variable>
	<xsl:variable name="i18n.UpdateFlowInstance">�ndra �rende</xsl:variable>
	
	<xsl:variable name="i18n.FlowInstancePreviewNotificationTitle.Part1">Du granskar nu</xsl:variable>
	<xsl:variable name="i18n.FlowInstancePreviewNotificationTitle.Part2">�rende</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceFormNotificationTitle.Part1">Du �ndrar nu</xsl:variable>
	<xsl:variable name="i18n.FlowInstanceFormNotificationTitle.Part2">�rende</xsl:variable>
	
	<xsl:variable name="i18n.FlowInstanceSavedByManager">�ndringarna i �rendet sparades!</xsl:variable>
	
	<xsl:variable name="i18n.Hits.Part1">gav</xsl:variable>
	<xsl:variable name="i18n.Hits.Part2">tr�ffar</xsl:variable>
	<xsl:variable name="i18n.SearchDone">S�kningen �r klar</xsl:variable>
	
	<xsl:variable name="i18n.InternalMessages">Interna noteringar</xsl:variable>
	<xsl:variable name="i18n.SubmitInternalMessage">Spara notering</xsl:variable>
	<xsl:variable name="i18n.InternalMessage">Notering</xsl:variable>
	<xsl:variable name="i18n.NewInternalMessage">Ny notering</xsl:variable>
	<xsl:variable name="i18n.InternalMessagesTitle">Noteringar</xsl:variable>
	<xsl:variable name="i18n.NoInternalMessages">Inga noteringar</xsl:variable>
	
	<xsl:variable name="i18n.ChooseManager">V�lj handl�ggare</xsl:variable>
	<xsl:variable name="i18n.SearchManager">S�k i listan</xsl:variable>
	<xsl:variable name="i18n.AddManager">L�gg till</xsl:variable>
	<xsl:variable name="i18n.DeleteManager">Ta bort</xsl:variable>
	
	<xsl:variable name="i18n.FlowInstancePreviewError">Ett fel uppstod vid visning av �rendet.</xsl:variable>
		
	<xsl:variable name="i18n.FlowInstanceManagerClosedError">Den kopia av ans�kan som du hade �ppen har st�ngts.</xsl:variable>
	<xsl:variable name="i18n.StatusNotFoundValidationError">Den valda statusen hittades inte.</xsl:variable>
	<xsl:variable name="i18n.InvalidStatusValidationError">Du har valt en ogiltig status.</xsl:variable>
	<xsl:variable name="i18n.OneOrMoreSelectedManagerUsersNotFoundError">En eller flera av de valda anv�ndarna hittade inte.</xsl:variable>
	<xsl:variable name="i18n.UnauthorizedManagerUserError.part1">Anv�ndaren</xsl:variable>
	<xsl:variable name="i18n.UnauthorizedManagerUserError.part2">har inte beh�righet att handl�gga detta �rende.</xsl:variable>
	
	<xsl:variable name="i18n.FileSizeLimitExceeded.part1">Filen </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part2"> har en storlek p� </xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part3"> vilket �verskrider den maximalt till�tna filstorleken p�</xsl:variable>
	<xsl:variable name="i18n.FileSizeLimitExceeded.part4">.</xsl:variable>
	
	<xsl:variable name="i18n.ValidationError.UnableToParseRequest">Ett ok�nt fel uppstod vid filuppladdningen</xsl:variable>
	<xsl:variable name="i18n.ValidationError.ExternalMessageRequired">Du m�ste skriva ett meddelande</xsl:variable>
	<xsl:variable name="i18n.ValidationError.ExternalMessageToLong">Du har skrivit ett f�r l�ngt meddelande</xsl:variable>
	<xsl:variable name="i18n.ValidationError.ExternalMessageToShort">Du har skrivit ett f�r kort meddelande</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InternalMessageRequired">Du m�ste skriva en notering</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InternalMessageToLong">Du har skrivit en f�r l�ng notering</xsl:variable>
	<xsl:variable name="i18n.ValidationError.InternalMessageToShort">Du har skrivit en f�r kort notering</xsl:variable>
	
	<xsl:variable name="i18n.DownloadFlowInstancePDF">H�mta kvittens i PDF-format</xsl:variable>	
	 
	 <xsl:variable name="i18n.saveBtnSuffix">och st�ng</xsl:variable>
	 
	<xsl:variable name="i18n.SiteProfile">Kommun</xsl:variable>
	<xsl:variable name="i18n.Summary">Sammanfattning av �ppna �renden</xsl:variable>
	<xsl:variable name="i18n.DownloadFlowInstanceXML">H�mta ans�kan i XML-format</xsl:variable>

</xsl:stylesheet>
