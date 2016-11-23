<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="GalleryModuleTemplates.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="i18n.galleries.addGallery.title" select="'L�gg till ett nytt galleri'"/>
	<xsl:variable name="i18n.galleries.addGallery" select="'L�gg till galleri'"/>
	<xsl:variable name="i18n.galleries.scanForNewImages.title" select="'Letar igenom alla gallerier efter nya bilder och skapar thumbnails f�r dem'"/>
	<xsl:variable name="i18n.galleries.scanForNewImages" select="'Leta efter nya bilder'"/>
	<xsl:variable name="i18n.galleries.regenerateThumbs.title" select="'Genererar om samtliga thumbnails i databasen'"/>
	<xsl:variable name="i18n.galleries.regenerateThumbs" select="'Uppdatera thumbnails'"/>
	<xsl:variable name="i18n.galleries.noGalleriesFound" select="'Inga gallerier hittades'"/>
	
	
	<xsl:variable name="i18n.gallery.regenerateThumbs.title" select="'Generera om samtliga thumbnails i galleriet'"/>
	<xsl:variable name="i18n.gallery.updateGallery.title" select="'Uppdatera galleriet'"/>
	<xsl:variable name="i18n.gallery.deleteGallery.title" select="'Ta bort galleriet'"/>
	<xsl:variable name="i18n.gallery.deleteGallery.popup" select="'�r du s�ker p� att du vill ta bort galleriet'"/>
	<xsl:variable name="i18n.gallery.thumblink.title" select="'Visa galleriet'"/>
	
	<xsl:variable name="i18n.gallery.pictures" select="'bilder'"/>
	
	<xsl:variable name="i18n.showGallery.pictures" select="'bilder'"/>
	<xsl:variable name="i18n.showGallery.page" select="'Sida'"/>
	<xsl:variable name="i18n.showGallery.pagecount" select="'av'"/>
	<xsl:variable name="i18n.showGallery.previousLink.title" select="'F�reg�ende'"/>
	<xsl:variable name="i18n.showGallery.previousImage.alt" select="'F�reg�ende'"/>
	<xsl:variable name="i18n.showGallery.previousLink.text" select="'F�reg�ende'"/>
	<xsl:variable name="i18n.showGallery.nextLink.title" select="'N�sta'"/>
	<xsl:variable name="i18n.showGallery.nextLink.text" select="'N�sta'"/>	
	<xsl:variable name="i18n.showGallery.nextImage.alt" select="'N�sta'"/>
	<xsl:variable name="i18n.showGallery.addImagesLink.title" select="'L�gg till bilder'"/>
	<xsl:variable name="i18n.showGallery.addImagesLink.text" select="'L�gg till bilder'"/>
	<xsl:variable name="i18n.showGallery.showAllGalleriesLink.title" select="'Visa alla gallerier'"/>
	<xsl:variable name="i18n.showGallery.showAllGalleriesLink.text" select="'Visa alla gallerier'"/>
	<xsl:variable name="i18n.showGallery.noImagesInGallery" select="'Det finns inga bilder i galleriet'"/>
	<xsl:variable name="i18n.showGallery.select.title" select="'Markera'"/>
	<xsl:variable name="i18n.showGallery.selectAll.title" select="'Markera alla'"/>
	<xsl:variable name="i18n.showGallery.deselectAll.title" select="'Avmarkera alla'"/>
	<xsl:variable name="i18n.showGallery.deleteSelectedPictures.confirm" select="'Ta bort markerade bild(er)?'"/>
	<xsl:variable name="i18n.showGallery.deleteSelectedPictures.title" select="'Ta bort markerade'"/>
	
	<xsl:variable name="i18n.file.link.title" select="'Visa st�rre bild'"/>
	
	<xsl:variable name="i18n.showImage.pictures" select="'bilder'"/>
	<xsl:variable name="i18n.showImage.deleteImageLink.title" select="'Ta bort bilden'"/>
	<xsl:variable name="i18n.showImage.deleteImageLink.popup" select="'�r du s�ker p� att du vill ta bort bilden'"/>
	<xsl:variable name="i18n.showImage.picture" select="'bild'"/>
	<xsl:variable name="i18n.showImage.pictureCount" select="'av'"/>
	<xsl:variable name="i18n.showImage.previousLink.title" select="'F�reg�ende'"/>
	<xsl:variable name="i18n.showImage.previousImage.alt" select="'F�reg�ende'"/>
	<xsl:variable name="i18n.showImage.previousLink.text" select="'F�reg�ende'"/>
	<xsl:variable name="i18n.showImage.nextLink.title" select="'N�sta'"/>
	<xsl:variable name="i18n.showImage.nextLink.text" select="'N�sta'"/>	
	<xsl:variable name="i18n.showImage.nextImage.alt" select="'N�sta'"/>	
	<xsl:variable name="i18n.showImage.showThumbsLink.title" select="'Sm� bilder'"/>
	<xsl:variable name="i18n.showImage.showThumbsLink.text" select="'Sm� bilder'"/>

	<xsl:variable name="i18n.comment.updateCommentLink.title" select="'Uppdatera kommentaren'"/>
	<xsl:variable name="i18n.comment.deleteCommentLink.title" select="'Ta bort kommentaren'"/>
	<xsl:variable name="i18n.comment.submit" select="'Spara �ndringar'"/>
	<xsl:variable name="i18n.comment.anonymousUser" select="'Anonym anv�ndare'"/>
	
	<xsl:variable name="i18n.updateGallery.header" select="'Redigera'"/>
	<xsl:variable name="i18n.updateGallery.name" select="'Namn'"/>
	<xsl:variable name="i18n.updateGallery.description" select="'Beskrivning'"/>
	<xsl:variable name="i18n.updateGallery.path" select="'S�kv�g'"/>
	<xsl:variable name="i18n.updateGallery.permissions" select="'Beh�righeter'"/>
	<xsl:variable name="i18n.updateGallery.anonymousUsers" select="'Ej inloggade anv�ndare'"/>
	<xsl:variable name="i18n.updateGallery.loggedInUsers" select="'Inloggade anv�ndare'"/>
	<xsl:variable name="i18n.updateGallery.adminUsers" select="'Administrat�rer'"/>
	<xsl:variable name="i18n.updateGallery.submit" select="'Spara �ndringar'"/>
	<xsl:variable name="i18n.updateGallery.readAccess" select="'L�sa'"/>
	<xsl:variable name="i18n.updateGallery.uploadAccess" select="'Ladda upp bilder'"/>	
	
	<xsl:variable name="i18n.addGallery.header" select="'L�gg till galleri'"/>
	<xsl:variable name="i18n.addGallery.name" select="'Namn'"/>
	<xsl:variable name="i18n.addGallery.description" select="'Beskrivning'"/>
	<xsl:variable name="i18n.addGallery.path" select="'S�kv�g'"/>
	<xsl:variable name="i18n.addGallery.uploadFiles" select="'Ladda upp bilder (zip)'"/>
	<xsl:variable name="i18n.addGallery.permissions" select="'Beh�righeter'"/>
	<xsl:variable name="i18n.addGallery.anonymousUsers" select="'Ej inloggade anv�ndare'"/>
	<xsl:variable name="i18n.addGallery.loggedInUsers" select="'Inloggade anv�ndare'"/>
	<xsl:variable name="i18n.addGallery.adminUsers" select="'Administrat�rer'"/>
	<xsl:variable name="i18n.addGallery.submit" select="'Skapa galleri'"/>
	<xsl:variable name="i18n.addGallery.readAccess" select="'L�sa'"/>
	<xsl:variable name="i18n.addGallery.uploadAccess" select="'Ladda upp bilder'"/>	
	
	<xsl:variable name="i18n.groups.header" select="'Grupper'"/>
	
	<xsl:variable name="i18n.users.header" select="'Anv�ndare'"/>
	
	<xsl:variable name="i18n.addImages.header" select="'L�gg till bilder'"/>
	<xsl:variable name="i18n.addImages.text" select="'Ladda upp bild/bilder (zip fil eller enskild bild)'"/>
	<xsl:variable name="i18n.addImages.diskThreshold" select="'Max till�tna filstorlek �r'"/>
	<xsl:variable name="i18n.addImages.submit" select="'L�gg till bild(er)'"/>
	
	<xsl:variable name="i18n.validationError.RequiredField" select="'Du m�ste fylla i f�ltet'"/>
	<xsl:variable name="i18n.validationError.InvalidFormat" select="'Felaktigt format p� f�ltet'"/>
	<xsl:variable name="i18n.validationError.Other" select="'S�kv�gen �r inte giltig, �ndra f�ltet'"/>
	<xsl:variable name="i18n.validationError.unknownValidationErrorType" select="'Ok�nt fel p� f�ltet'"/>
	<xsl:variable name="i18n.validationError.field.name" select="'namn'"/>
	<xsl:variable name="i18n.validationError.field.description" select="'beskrivning'"/>
	<xsl:variable name="i18n.validationError.field.comment" select="'kommentar'"/>
	<xsl:variable name="i18n.validationError.field.url" select="'s�kv�g'"/>
	<xsl:variable name="i18n.validationError.messageKey.BadFileFormat" select="'Felaktigt filformat!'"/>
	<xsl:variable name="i18n.validationError.messageKey.FileSizeLimitExceeded" select="'Max till�ten filstorlek �verskriden!'"/>
	<xsl:variable name="i18n.validationError.unknownMessageKey" select="'Ett ok�nt fel har uppst�tt!'"/>			
	
	<xsl:variable name="i18n.gallery.file.showFullImageLink.title" select="'Visa st�rre bild'"/>
	<xsl:variable name="i18n.gallery.file.comments" select="'Kommentarer'"/>
	<xsl:variable name="i18n.gallery.file.hide.comments" select="'D�lj kommentarer'"/>					
	<xsl:variable name="i18n.gallery.file.show.comments" select="'Visa kommentarer'"/>
	<xsl:variable name="i18n.gallery.file.noComments" select="'Det finns inga kommentarer f�r denna bild'"/>
	<xsl:variable name="i18n.gallery.file.addcomment" select="'Kommentar'"/>
	<xsl:variable name="i18n.gallery.file.submit" select="'L�gg till kommentar'"/>

	<xsl:variable name="i18n.showGallery.downloadLink.title">Ladda ner hela galleriet som en ZIP fil</xsl:variable>
	<xsl:variable name="i18n.showGallery.downloadLink.text">Ladda ner</xsl:variable>
	<xsl:variable name="i18n.showGallery.downloadImage.alt">Ladda ner hela galleriet som en ZIP fil</xsl:variable>

</xsl:stylesheet>
