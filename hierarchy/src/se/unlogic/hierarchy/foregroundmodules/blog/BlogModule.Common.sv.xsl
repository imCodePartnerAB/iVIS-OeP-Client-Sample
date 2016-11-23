<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="addPost">L�gg till blogginl�gg</xsl:variable>
	<xsl:variable name="editPost">Redigera blogginl�gg</xsl:variable>
	<xsl:variable name="deletePost">Ta bort blogginl�gg</xsl:variable>
	
	<xsl:variable name="splitTagInfo">Om du inte vill visa hela ditt blogginl�gg p� f�rsta sidan kan du skriva texten split-post p� en egen rad d�r du vill dela blogginl�gget.</xsl:variable>
	
	<xsl:variable name="editComment">Redigera kommentar</xsl:variable>
	<xsl:variable name="deleteComment">Ta bort kommentar</xsl:variable>
	
	<xsl:variable name="postedBy">Inlagt av </xsl:variable>
	<xsl:variable name="updatedBy">Uppdaterat av </xsl:variable>
	<xsl:variable name="deletedUser">Borttagen anv�ndare</xsl:variable>
	<xsl:variable name="comments">Kommentarer</xsl:variable>
	<xsl:variable name="title">Rubrik</xsl:variable>
	<xsl:variable name="alias">Alias</xsl:variable>
	<xsl:variable name="message">Meddelande</xsl:variable>
	<xsl:variable name="tags">Taggar</xsl:variable>
	<xsl:variable name="existingTags">Befintliga taggar</xsl:variable>
	<xsl:variable name="newTags">Nya taggar (en per rad)</xsl:variable>
	<xsl:variable name="add">L�gg till</xsl:variable>
	<xsl:variable name="saveChanges">Spara �ndringar</xsl:variable>
	<xsl:variable name="posts">inl�gg</xsl:variable>
	<xsl:variable name="post">inl�gg</xsl:variable>
	
	<xsl:variable name="january">januari</xsl:variable>
	<xsl:variable name="february">februari</xsl:variable>
	<xsl:variable name="march">mars</xsl:variable>
	<xsl:variable name="april">april</xsl:variable>
	<xsl:variable name="may">maj</xsl:variable>
	<xsl:variable name="june">juni</xsl:variable>
	<xsl:variable name="july">juli</xsl:variable>
	<xsl:variable name="august">augusti</xsl:variable>
	<xsl:variable name="september">september</xsl:variable>
	<xsl:variable name="october">oktober</xsl:variable>
	<xsl:variable name="november">november</xsl:variable>
	<xsl:variable name="december">december</xsl:variable>
	
	<xsl:variable name="tagBundleName">Taggar</xsl:variable>
	<xsl:variable name="tagBundleDescription">Popul�ra taggar</xsl:variable>
	
	<xsl:variable name="archiveBundleName">Arkiv</xsl:variable>
	<xsl:variable name="pageBreadcrumbText">Sida </xsl:variable>
	<xsl:variable name="archiveBreadcrumbText">Arkiv </xsl:variable>
	<xsl:variable name="tagsBreadcrumbText">Taggar </xsl:variable>
	<xsl:variable name="tagBreadcrumbText">Tagg </xsl:variable>
	<xsl:variable name="updateCommentBreadcrumbText">Redigera kommentar</xsl:variable>
	<xsl:variable name="ShowPage.noPostsTitle">Bloggen �r tom</xsl:variable>
	<xsl:variable name="ShowPage.noPostsText">Ingen har gjort n�gra inl�gg �n.</xsl:variable>	
	
	<xsl:variable name="BlogPost.addCommentTitle">L�gg till kommentar</xsl:variable>
	<xsl:variable name="BlogPost.half.readMore">Klicka h�r f�r att l�sa hela inl�gget </xsl:variable>
	<xsl:variable name="BlogPost.anonymousCommentsDisabled">Anonyma kommentarer till�ts f�r n�rvarande inte</xsl:variable>	
	<xsl:variable name="BlogPost.comments">Kommentarer</xsl:variable>
	<xsl:variable name="BlogPost.name">Namn</xsl:variable>
	<xsl:variable name="BlogPost.mail">E-post (kommer ej visas)</xsl:variable>
	<xsl:variable name="BlogPost.website">Hemsida</xsl:variable>
	<xsl:variable name="BlogPost.captcha">Bildverifering</xsl:variable>
	<xsl:variable name="BlogPost.regenerateCaptcha">Generera ny bild</xsl:variable>
	<xsl:variable name="BlogPost.olderPosts">�ldre inl�gg</xsl:variable>
	<xsl:variable name="BlogPost.newerPosts">Nyare inl�gg</xsl:variable>
	
	<xsl:variable name="UpdateComment.title">redigera kommentar</xsl:variable>
	
	<xsl:variable name="AddBlogPost.title">L�gg till blogginl�gg</xsl:variable>
	
	<xsl:variable name="UpdateBlogPost.title">Uppdatera blogginl�gg</xsl:variable>
	
	<xsl:variable name="tag.linkTitle">Visa alla inl�gg m�rkta med taggen</xsl:variable>
	
	<xsl:variable name="ShowTags.title">Taggar</xsl:variable>
	<xsl:variable name="ShowTags.noTagsFound">Inga taggar hittades</xsl:variable>
	
	<xsl:variable name="ShowArchive.title">Arkiv</xsl:variable>
	<xsl:variable name="ArchiveEntry.noPostsFound">Inga inl�gg hittades</xsl:variable>
		
	<xsl:variable name="validationError.requiredField" select="'Du m�ste fylla i f�ltet'" />
	<xsl:variable name="validationError.invalidFormat" select="'Felaktigt format p� f�ltet'" />
	<xsl:variable name="validationError.tooShort" select="'F�r kort inneh�ll i f�ltet'" />
	<xsl:variable name="validationError.tooLong" select="'F�r l�ngt inneh�ll i f�ltet'" />	
	<xsl:variable name="validationError.unknownError" select="'Ok�nt fel p� f�ltet'" />					
				
	<xsl:variable name="validationError.field.alias" select="'alias'" />
	<xsl:variable name="validationError.field.message" select="'meddelande'" />
	<xsl:variable name="validationError.field.title" select="'rubrik'" />
	<xsl:variable name="validationError.field.posterName" select="'namn'"/>
	<xsl:variable name="validationError.field.posterEmail" select="'e-post'"/>
	<xsl:variable name="validationError.field.posterWebsite" select="'hemsida'"/>
	
	<xsl:variable name="validationError.message.InvalidCaptchaConfirmation">Felaktig captcha kod</xsl:variable>
	<xsl:variable name="validationError.message.AliasAlreadyTaken">Aliaset du valt anv�nds redan av ett annat blogg in�gg</xsl:variable>
	<xsl:variable name="validationError.message.unknownFault" select="'Ett ok�nt fel har uppst�tt'" />
	
</xsl:stylesheet>