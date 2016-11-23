<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="GalleryModuleTemplates.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="i18n.galleries.addGallery.title" select="'Add a new gallery'"/>
	<xsl:variable name="i18n.galleries.addGallery" select="'Add gallery'"/>
	<xsl:variable name="i18n.galleries.scanForNewImages.title" select="'Scans all galleries for new images and creates thumbs for them'"/>
	<xsl:variable name="i18n.galleries.scanForNewImages" select="'Look for new images'"/>
	<xsl:variable name="i18n.galleries.regenerateThumbs.title" select="'Regenerates all thumbs in the database'"/>
	<xsl:variable name="i18n.galleries.regenerateThumbs" select="'Regenerate thumbs'"/>
	<xsl:variable name="i18n.galleries.noGalleriesFound" select="'No galleries found'"/>
	
	
	<xsl:variable name="i18n.gallery.regenerateThumbs.title" select="'Regenerates all thumbs in the gallery'"/>
	<xsl:variable name="i18n.gallery.updateGallery.title" select="'Update gallery'"/>
	<xsl:variable name="i18n.gallery.deleteGallery.title" select="'Delete gallery'"/>
	<xsl:variable name="i18n.gallery.deleteGallery.popup" select="'Delete gallery'"/>
	<xsl:variable name="i18n.gallery.thumblink.title" select="'Show gallery'"/>
	
	<xsl:variable name="i18n.gallery.pictures" select="'images'"/>
	
	<xsl:variable name="i18n.showGallery.pictures" select="'images'"/>
	<xsl:variable name="i18n.showGallery.page" select="'Page'"/>
	<xsl:variable name="i18n.showGallery.pagecount" select="'of'"/>
	<xsl:variable name="i18n.showGallery.previousLink.title" select="'Previous'"/>
	<xsl:variable name="i18n.showGallery.previousImage.alt" select="'Previous'"/>
	<xsl:variable name="i18n.showGallery.previousLink.text" select="'Previous'"/>
	<xsl:variable name="i18n.showGallery.nextLink.title" select="'Next'"/>
	<xsl:variable name="i18n.showGallery.nextLink.text" select="'Next'"/>	
	<xsl:variable name="i18n.showGallery.nextImage.alt" select="'Next'"/>
	<xsl:variable name="i18n.showGallery.addImagesLink.title" select="'Add images'"/>
	<xsl:variable name="i18n.showGallery.addImagesLink.text" select="'Add images'"/>
	<xsl:variable name="i18n.showGallery.showAllGalleriesLink.title" select="'Show all galleries'"/>
	<xsl:variable name="i18n.showGallery.showAllGalleriesLink.text" select="'Show all galleries'"/>
	<xsl:variable name="i18n.showGallery.noImagesInGallery" select="'There are no images in the gallery'"/>
	<xsl:variable name="i18n.showGallery.select.title" select="'Select'"/>
	<xsl:variable name="i18n.showGallery.selectAll.title" select="'Select All'"/>
	<xsl:variable name="i18n.showGallery.deselectAll.title" select="'Deselect All'"/>
	<xsl:variable name="i18n.showGallery.deleteSelectedPictures.confirm" select="'Delete selected picture(s)?'"/>
	<xsl:variable name="i18n.showGallery.deleteSelectedPictures.title" select="'Delete selected'"/>
	
	<xsl:variable name="i18n.file.link.title" select="'Show larger image'"/>
	
	<xsl:variable name="i18n.showImage.pictures" select="'images'"/>
	<xsl:variable name="i18n.showImage.deleteImageLink.title" select="'Delete image'"/>
	<xsl:variable name="i18n.showImage.deleteImageLink.popup" select="'Delete image'"/>
	<xsl:variable name="i18n.showImage.picture" select="'image'"/>
	<xsl:variable name="i18n.showImage.pictureCount" select="'of'"/>
	<xsl:variable name="i18n.showImage.previousLink.title" select="'Previous'"/>
	<xsl:variable name="i18n.showImage.previousImage.alt" select="'Previous'"/>
	<xsl:variable name="i18n.showImage.previousLink.text" select="'Previous'"/>
	<xsl:variable name="i18n.showImage.nextLink.title" select="'Next'"/>
	<xsl:variable name="i18n.showImage.nextLink.text" select="'Next'"/>	
	<xsl:variable name="i18n.showImage.nextImage.alt" select="'Next'"/>	
	<xsl:variable name="i18n.showImage.showThumbsLink.title" select="'Thumbnails'"/>
	<xsl:variable name="i18n.showImage.showThumbsLink.text" select="'Thumbnails'"/>

	<xsl:variable name="i18n.comment.updateCommentLink.title" select="'Update comment'"/>
	<xsl:variable name="i18n.comment.deleteCommentLink.title" select="'Delete comment'"/>
	<xsl:variable name="i18n.comment.submit" select="'Save changes'"/>
	<xsl:variable name="i18n.comment.anonymousUser" select="'Anonymous user'"/>
	
	<xsl:variable name="i18n.updateGallery.header" select="'Update'"/>
	<xsl:variable name="i18n.updateGallery.name" select="'Name'"/>
	<xsl:variable name="i18n.updateGallery.description" select="'Description'"/>
	<xsl:variable name="i18n.updateGallery.path" select="'Path'"/>
	<xsl:variable name="i18n.updateGallery.permissions" select="'Permissions'"/>
	<xsl:variable name="i18n.updateGallery.anonymousUsers" select="'Non logged in users'"/>
	<xsl:variable name="i18n.updateGallery.loggedInUsers" select="'Logged in users'"/>
	<xsl:variable name="i18n.updateGallery.adminUsers" select="'Administrators'"/>
	<xsl:variable name="i18n.updateGallery.submit" select="'Save changes'"/>
	<xsl:variable name="i18n.updateGallery.readAccess" select="'Read access'"/>
	<xsl:variable name="i18n.updateGallery.uploadAccess" select="'Upload access'"/>
	
	<xsl:variable name="i18n.addGallery.header" select="'Add gallery'"/>
	<xsl:variable name="i18n.addGallery.name" select="'Name'"/>
	<xsl:variable name="i18n.addGallery.description" select="'Description'"/>
	<xsl:variable name="i18n.addGallery.path" select="'Path'"/>
	<xsl:variable name="i18n.addGallery.uploadFiles" select="'Upload images (zip)'"/>
	<xsl:variable name="i18n.addGallery.permissions" select="'Permissions'"/>
	<xsl:variable name="i18n.addGallery.anonymousUsers" select="'Non logged in users'"/>
	<xsl:variable name="i18n.addGallery.loggedInUsers" select="'Logged in users'"/>
	<xsl:variable name="i18n.addGallery.adminUsers" select="'Administrators'"/>
	<xsl:variable name="i18n.addGallery.submit" select="'Create gallery'"/>
	<xsl:variable name="i18n.addGallery.readAccess" select="'Read access'"/>
	<xsl:variable name="i18n.addGallery.uploadAccess" select="'Upload access'"/>	
	
	<xsl:variable name="i18n.groups.header" select="'Groups'"/>
	
	<xsl:variable name="i18n.users.header" select="'Users'"/>
	
	<xsl:variable name="i18n.addImages.header" select="'Add images'"/>
	<xsl:variable name="i18n.addImages.text" select="'Images (zip file or single image)'"/>
	<xsl:variable name="i18n.addImages.diskThreshold" select="'Maximum file size allowed is'"/>
	<xsl:variable name="i18n.addImages.submit" select="'Upload images'"/>
	
	<xsl:variable name="i18n.validationError.RequiredField" select="'You need to fill in the field'"/>
	<xsl:variable name="i18n.validationError.InvalidFormat" select="'Invalid value in field'"/>
	<xsl:variable name="i18n.validationError.Other" select="'The path you have entered is not valid, change field'"/>
	<xsl:variable name="i18n.validationError.unknownValidationErrorType" select="'Unknown problem validating field'"/>
	<xsl:variable name="i18n.validationError.field.name" select="'name'"/>
	<xsl:variable name="i18n.validationError.field.description" select="'description'"/>
	<xsl:variable name="i18n.validationError.field.comment" select="'comment'"/>
	<xsl:variable name="i18n.validationError.field.url" select="'path'"/>
	<xsl:variable name="i18n.validationError.messageKey.BadFileFormat" select="'Invalid file format!'"/>
	<xsl:variable name="i18n.validationError.messageKey.FileSizeLimitExceeded" select="'Maximum file size limit exceeded!'"/>
	<xsl:variable name="i18n.validationError.unknownMessageKey" select="'An unknown error has occured!'"/>			
	
	<xsl:variable name="i18n.gallery.file.showFullImageLink.title" select="'Show original image'"/>
	<xsl:variable name="i18n.gallery.file.comments" select="'Comments'"/>
	<xsl:variable name="i18n.gallery.file.hide.comments" select="'Hide comments'"/>					
	<xsl:variable name="i18n.gallery.file.show.comments" select="'Show comments'"/>
	<xsl:variable name="i18n.gallery.file.noComments" select="'There are no comments for this image'"/>
	<xsl:variable name="i18n.gallery.file.addcomment" select="'Comment'"/>
	<xsl:variable name="i18n.gallery.file.submit" select="'Add comment'"/>

	<xsl:variable name="i18n.showGallery.downloadLink.title">Download the whole gallery as a ZIP file</xsl:variable>
	<xsl:variable name="i18n.showGallery.downloadLink.text">Download</xsl:variable>
	<xsl:variable name="i18n.showGallery.downloadImage.alt">Download the whole gallery as a ZIP file</xsl:variable>

</xsl:stylesheet>
