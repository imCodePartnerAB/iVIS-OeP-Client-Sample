<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="addPost">Lägg till blogginlägg</xsl:variable>
	<xsl:variable name="editPost">Redigera blogginlägg</xsl:variable>
	<xsl:variable name="deletePost">Ta bort blogginlägg</xsl:variable>
	
	<xsl:variable name="splitTagInfo">Om du inte vill visa hela ditt blogginlägg på första sidan kan du skriva texten split-post på en egen rad där du vill dela blogginlägget.</xsl:variable>
	
	<xsl:variable name="editComment">Redigera kommentar</xsl:variable>
	<xsl:variable name="deleteComment">Ta bort kommentar</xsl:variable>
	
	<xsl:variable name="postedBy">Inlagt av </xsl:variable>
	<xsl:variable name="updatedBy">Uppdaterat av </xsl:variable>
	<xsl:variable name="deletedUser">Borttagen användare</xsl:variable>
	<xsl:variable name="comments">Kommentarer</xsl:variable>
	<xsl:variable name="title">Rubrik</xsl:variable>
	<xsl:variable name="alias">Alias</xsl:variable>
	<xsl:variable name="message">Meddelande</xsl:variable>
	<xsl:variable name="tags">Taggar</xsl:variable>
	<xsl:variable name="existingTags">Befintliga taggar</xsl:variable>
	<xsl:variable name="newTags">Nya taggar (en per rad)</xsl:variable>
	<xsl:variable name="add">Lägg till</xsl:variable>
	<xsl:variable name="saveChanges">Spara ändringar</xsl:variable>
	<xsl:variable name="posts">inlägg</xsl:variable>
	<xsl:variable name="post">inlägg</xsl:variable>
	
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
	<xsl:variable name="tagBundleDescription">Populära taggar</xsl:variable>
	
	<xsl:variable name="archiveBundleName">Arkiv</xsl:variable>
	<xsl:variable name="pageBreadcrumbText">Sida </xsl:variable>
	<xsl:variable name="archiveBreadcrumbText">Arkiv </xsl:variable>
	<xsl:variable name="tagsBreadcrumbText">Taggar </xsl:variable>
	<xsl:variable name="tagBreadcrumbText">Tagg </xsl:variable>
	<xsl:variable name="updateCommentBreadcrumbText">Redigera kommentar</xsl:variable>
	<xsl:variable name="ShowPage.noPostsTitle">Bloggen är tom</xsl:variable>
	<xsl:variable name="ShowPage.noPostsText">Ingen har gjort några inlägg än.</xsl:variable>	
	
	<xsl:variable name="BlogPost.addCommentTitle">Lägg till kommentar</xsl:variable>
	<xsl:variable name="BlogPost.half.readMore">Klicka här för att läsa hela inlägget </xsl:variable>
	<xsl:variable name="BlogPost.anonymousCommentsDisabled">Anonyma kommentarer tillåts för närvarande inte</xsl:variable>	
	<xsl:variable name="BlogPost.comments">Kommentarer</xsl:variable>
	<xsl:variable name="BlogPost.name">Namn</xsl:variable>
	<xsl:variable name="BlogPost.mail">E-post (kommer ej visas)</xsl:variable>
	<xsl:variable name="BlogPost.website">Hemsida</xsl:variable>
	<xsl:variable name="BlogPost.captcha">Bildverifering</xsl:variable>
	<xsl:variable name="BlogPost.regenerateCaptcha">Generera ny bild</xsl:variable>
	<xsl:variable name="BlogPost.olderPosts">Äldre inlägg</xsl:variable>
	<xsl:variable name="BlogPost.newerPosts">Nyare inlägg</xsl:variable>
	
	<xsl:variable name="UpdateComment.title">redigera kommentar</xsl:variable>
	
	<xsl:variable name="AddBlogPost.title">Lägg till blogginlägg</xsl:variable>
	
	<xsl:variable name="UpdateBlogPost.title">Uppdatera blogginlägg</xsl:variable>
	
	<xsl:variable name="tag.linkTitle">Visa alla inlägg märkta med taggen</xsl:variable>
	
	<xsl:variable name="ShowTags.title">Taggar</xsl:variable>
	<xsl:variable name="ShowTags.noTagsFound">Inga taggar hittades</xsl:variable>
	
	<xsl:variable name="ShowArchive.title">Arkiv</xsl:variable>
	<xsl:variable name="ArchiveEntry.noPostsFound">Inga inlägg hittades</xsl:variable>
		
	<xsl:variable name="validationError.requiredField" select="'Du måste fylla i fältet'" />
	<xsl:variable name="validationError.invalidFormat" select="'Felaktigt format på fältet'" />
	<xsl:variable name="validationError.tooShort" select="'För kort innehåll i fältet'" />
	<xsl:variable name="validationError.tooLong" select="'För långt innehåll i fältet'" />	
	<xsl:variable name="validationError.unknownError" select="'Okänt fel på fältet'" />					
				
	<xsl:variable name="validationError.field.alias" select="'alias'" />
	<xsl:variable name="validationError.field.message" select="'meddelande'" />
	<xsl:variable name="validationError.field.title" select="'rubrik'" />
	<xsl:variable name="validationError.field.posterName" select="'namn'"/>
	<xsl:variable name="validationError.field.posterEmail" select="'e-post'"/>
	<xsl:variable name="validationError.field.posterWebsite" select="'hemsida'"/>
	
	<xsl:variable name="validationError.message.InvalidCaptchaConfirmation">Felaktig captcha kod</xsl:variable>
	<xsl:variable name="validationError.message.AliasAlreadyTaken">Aliaset du valt används redan av ett annat blogg inägg</xsl:variable>
	<xsl:variable name="validationError.message.unknownFault" select="'Ett okänt fel har uppstått'" />
	
</xsl:stylesheet>