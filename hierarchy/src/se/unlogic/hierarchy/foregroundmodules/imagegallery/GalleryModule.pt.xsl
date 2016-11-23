<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output encoding="ISO-8859-1" method="html" version="4.0"/>

	<xsl:include href="GalleryModuleTemplates.xsl"/>
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="i18n.galleries.addGallery.title" select="'Adicionar uma nova galeria'"/>
	<xsl:variable name="i18n.galleries.addGallery" select="'Adicionar galeria'"/>
	<xsl:variable name="i18n.galleries.scanForNewImages.title" select="'Rastreia todos galerias de imagens e cria novos polegares para eles'"/>
	<xsl:variable name="i18n.galleries.scanForNewImages" select="'Olhe para as novas imagens'"/>
	<xsl:variable name="i18n.galleries.regenerateThumbs.title" select="'Regenera todos os polegares na base de dados'"/>
	<xsl:variable name="i18n.galleries.regenerateThumbs" select="'Regenerar polegares'"/>
	<xsl:variable name="i18n.galleries.noGalleriesFound" select="'N�o encontrou galerias'"/>
	
	
	<xsl:variable name="i18n.gallery.regenerateThumbs.title" select="'Regenera todos os polegares na galeria'"/>
	<xsl:variable name="i18n.gallery.updateGallery.title" select="'Actualiza��o galeria'"/>
	<xsl:variable name="i18n.gallery.deleteGallery.title" select="'Apagar galeria'"/>
	<xsl:variable name="i18n.gallery.deleteGallery.popup" select="'Apagar galeria'"/>
	<xsl:variable name="i18n.gallery.thumblink.title" select="'Visualizar galeria'"/>
	
	<xsl:variable name="i18n.gallery.pictures" select="'imagens'"/>
	
	<xsl:variable name="i18n.showGallery.pictures" select="'imagens'"/>
	<xsl:variable name="i18n.showGallery.page" select="'P�gina'"/>
	<xsl:variable name="i18n.showGallery.pagecount" select="'de'"/>
	<xsl:variable name="i18n.showGallery.previousLink.title" select="'Anterior'"/>
	<xsl:variable name="i18n.showGallery.previousImage.alt" select="'Anterior'"/>
	<xsl:variable name="i18n.showGallery.previousLink.text" select="'Anterior'"/>
	<xsl:variable name="i18n.showGallery.nextLink.title" select="'Pr�ximo'"/>
	<xsl:variable name="i18n.showGallery.nextLink.text" select="'Pr�ximo'"/>	
	<xsl:variable name="i18n.showGallery.nextImage.alt" select="'Pr�ximo'"/>
	<xsl:variable name="i18n.showGallery.addImagesLink.title" select="'Adicionar imagens'"/>
	<xsl:variable name="i18n.showGallery.addImagesLink.text" select="'Adicionar imagens'"/>
	<xsl:variable name="i18n.showGallery.showAllGalleriesLink.title" select="'Mostrar todas as galerias'"/>
	<xsl:variable name="i18n.showGallery.showAllGalleriesLink.text" select="'Mostrar todas as galerias'"/>
	<xsl:variable name="i18n.showGallery.noImagesInGallery" select="'N�o existem imagens na galeria'"/>
	<xsl:variable name="i18n.showGallery.select.title" select="'Select'"/>
	<xsl:variable name="i18n.showGallery.selectAll.title" select="'Select All'"/>
	<xsl:variable name="i18n.showGallery.deselectAll.title" select="'Deselect All'"/>
	<xsl:variable name="i18n.showGallery.deleteSelectedPictures.confirm" select="'Delete selected picture(s)?'"/>
	<xsl:variable name="i18n.showGallery.deleteSelectedPictures.title" select="'Delete selected'"/>
	
	<xsl:variable name="i18n.file.link.title" select="'Visualizar imagem ampliada'"/>
	
	<xsl:variable name="i18n.showImage.pictures" select="'images'"/>
	<xsl:variable name="i18n.showImage.deleteImageLink.title" select="'Excluir imagen'"/>
	<xsl:variable name="i18n.showImage.deleteImageLink.popup" select="'Excluir imagen'"/>
	<xsl:variable name="i18n.showImage.picture" select="'imagens'"/>
	<xsl:variable name="i18n.showImage.pictureCount" select="'de'"/>
	<xsl:variable name="i18n.showImage.previousLink.title" select="'Anterior'"/>
	<xsl:variable name="i18n.showImage.previousImage.alt" select="'Anterior'"/>
	<xsl:variable name="i18n.showImage.previousLink.text" select="'Anterior'"/>
	<xsl:variable name="i18n.showImage.nextLink.title" select="'Pr�ximo'"/>
	<xsl:variable name="i18n.showImage.nextLink.text" select="'Pr�ximo'"/>	
	<xsl:variable name="i18n.showImage.nextImage.alt" select="'Pr�ximo'"/>	
	<xsl:variable name="i18n.showImage.showThumbsLink.title" select="'Thumbnails'"/>
	<xsl:variable name="i18n.showImage.showThumbsLink.text" select="'Thumbnails'"/>

	<xsl:variable name="i18n.comment.updateCommentLink.title" select="'Atualiza��o coment�rio'"/>
	<xsl:variable name="i18n.comment.deleteCommentLink.title" select="'Excluir coment�rio'"/>
	<xsl:variable name="i18n.comment.submit" select="'Salvar altera��es'"/>
	<xsl:variable name="i18n.comment.anonymousUser" select="'Usu�rio an�nimo'"/>
	
	<xsl:variable name="i18n.updateGallery.header" select="'Atualizar'"/>
	<xsl:variable name="i18n.updateGallery.name" select="'Nome'"/>
	<xsl:variable name="i18n.updateGallery.description" select="'Descri��o'"/>
	<xsl:variable name="i18n.updateGallery.path" select="'Caminho'"/>
	<xsl:variable name="i18n.updateGallery.permissions" select="'Permiss�es'"/>
	<xsl:variable name="i18n.updateGallery.anonymousUsers" select="'N�o est� autenticado usu�rios'"/>
	<xsl:variable name="i18n.updateGallery.loggedInUsers" select="'Identificados os usu�rios'"/>
	<xsl:variable name="i18n.updateGallery.adminUsers" select="'Administradores'"/>
	<xsl:variable name="i18n.updateGallery.submit" select="'Salvar altera��es'"/>
	<xsl:variable name="i18n.updateGallery.readAccess" select="'Ler acesso'"/>
	<xsl:variable name="i18n.updateGallery.uploadAccess" select="'Enviar acesso'"/>	
	
	<xsl:variable name="i18n.addGallery.header" select="'Adicionar galeria'"/>
	<xsl:variable name="i18n.addGallery.name" select="'Nome'"/>
	<xsl:variable name="i18n.addGallery.description" select="'Descri��o'"/>
	<xsl:variable name="i18n.addGallery.path" select="'Caminho'"/>
	<xsl:variable name="i18n.addGallery.uploadFiles" select="'Enviar imagens (zip)'"/>
	<xsl:variable name="i18n.addGallery.permissions" select="'Permiss�es'"/>
	<xsl:variable name="i18n.addGallery.anonymousUsers" select="'N�o est� autenticado usu�rios'"/>
	<xsl:variable name="i18n.addGallery.loggedInUsers" select="'Identificados os usu�rios'"/>
	<xsl:variable name="i18n.addGallery.adminUsers" select="'Administradores'"/>
	<xsl:variable name="i18n.addGallery.submit" select="'Criar uma galeria'"/>
	<xsl:variable name="i18n.addGallery.readAccess" select="'Ler acesso'"/>
	<xsl:variable name="i18n.addGallery.uploadAccess" select="'Enviar acesso'"/>	
	
	<xsl:variable name="i18n.groups.header" select="'Grupos'"/>
	
	<xsl:variable name="i18n.users.header" select="'Usu�rios'"/>
	
	<xsl:variable name="i18n.addImages.header" select="'Adicionar imagens'"/>
	<xsl:variable name="i18n.addImages.text" select="'Imagens (zip)'"/>
	<xsl:variable name="i18n.addImages.diskThreshold" select="'Maximum file size allowed is'"/>
	<xsl:variable name="i18n.addImages.submit" select="'Enviar imagens'"/>
	
	<xsl:variable name="i18n.validationError.RequiredField" select="'Voc� precisa preencher o campo'"/>
	<xsl:variable name="i18n.validationError.InvalidFormat" select="'Valor inv�lido no campo'"/>
	<xsl:variable name="i18n.validationError.Other" select="'O caminho que voc� digitou n�o � v�lido, a mudan�a campo'"/>
	<xsl:variable name="i18n.validationError.unknownValidationErrorType" select="'Desconhecido problema validar campo'"/>
	<xsl:variable name="i18n.validationError.field.name" select="'nome'"/>
	<xsl:variable name="i18n.validationError.field.description" select="'descri��o'"/>
	<xsl:variable name="i18n.validationError.field.comment" select="'descri��o'"/>
	<xsl:variable name="i18n.validationError.field.url" select="'caminho'"/>
	<xsl:variable name="i18n.validationError.messageKey.BadFileFormat" select="'Formato de arquivo inv�lido!'"/>
	<xsl:variable name="i18n.validationError.messageKey.FileSizeLimitExceeded" select="'Maximum file size limit exceeded!'"/>
	<xsl:variable name="i18n.validationError.unknownMessageKey" select="'Ocorreu um erro desconhecido!'"/>			
	
	<xsl:variable name="i18n.gallery.file.showFullImageLink.title" select="'Mostrar imagem original'"/>
	<xsl:variable name="i18n.gallery.file.comments" select="'Coment�rios'"/>
	<xsl:variable name="i18n.gallery.file.hide.comments" select="'Ocultar coment�rios'"/>					
	<xsl:variable name="i18n.gallery.file.show.comments" select="'Mostrar coment�rios'"/>
	<xsl:variable name="i18n.gallery.file.noComments" select="'N�o h� coment�rios para esta imagem'"/>
	<xsl:variable name="i18n.gallery.file.addcomment" select="'Coment�rio'"/>
	<xsl:variable name="i18n.gallery.file.submit" select="'Adicionar coment�rio'"/>

	<xsl:variable name="i18n.showGallery.downloadLink.title">Download the whole gallery as a ZIP file</xsl:variable>
	<xsl:variable name="i18n.showGallery.downloadLink.text">Download</xsl:variable>
	<xsl:variable name="i18n.showGallery.downloadImage.alt">Download the whole gallery as a ZIP file</xsl:variable>
</xsl:stylesheet>
