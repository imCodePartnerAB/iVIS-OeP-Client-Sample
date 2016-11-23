<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="addPost">Add blog post</xsl:variable>
	<xsl:variable name="editPost">Edit blog post</xsl:variable>
	<xsl:variable name="deletePost">Delete blog post</xsl:variable>
	
	<xsl:variable name="splitTagInfo">If you don't wan't to show the full post on the blog firstpage type the split-post on an otherwise empty row where you the post to be split.</xsl:variable>
	
	<xsl:variable name="editComment">Edit comment</xsl:variable>
	<xsl:variable name="deleteComment">Delete comment</xsl:variable>
	
	<xsl:variable name="postedBy">Posted by </xsl:variable>
	<xsl:variable name="updatedBy">Updated by </xsl:variable>
	<xsl:variable name="deletedUser">deleted user</xsl:variable>
	<xsl:variable name="comments">Comments</xsl:variable>
	<xsl:variable name="title">Title</xsl:variable>
	<xsl:variable name="alias">Alias</xsl:variable>
	<xsl:variable name="message">Message</xsl:variable>
	<xsl:variable name="tags">Tags</xsl:variable>
	<xsl:variable name="existingTags">Existing tags</xsl:variable>
	<xsl:variable name="newTags">New tags (one per line)</xsl:variable>
	<xsl:variable name="add">Add</xsl:variable>
	<xsl:variable name="saveChanges">Save changes</xsl:variable>
	<xsl:variable name="posts">posts</xsl:variable>
	<xsl:variable name="post">post</xsl:variable>
	
	<xsl:variable name="january">january</xsl:variable>
	<xsl:variable name="february">february</xsl:variable>
	<xsl:variable name="march">march</xsl:variable>
	<xsl:variable name="april">april</xsl:variable>
	<xsl:variable name="may">may</xsl:variable>
	<xsl:variable name="june">june</xsl:variable>
	<xsl:variable name="july">july</xsl:variable>
	<xsl:variable name="august">august</xsl:variable>
	<xsl:variable name="september">september</xsl:variable>
	<xsl:variable name="october">october</xsl:variable>
	<xsl:variable name="november">november</xsl:variable>
	<xsl:variable name="december">december</xsl:variable>
	
	<xsl:variable name="tagBundleName">Tags</xsl:variable>
	<xsl:variable name="tagBundleDescription">Popular tags</xsl:variable>
	<xsl:variable name="tagBundleMenuitemDescription">Show all blog posts tagged with tag </xsl:variable>
	<xsl:variable name="archiveBundleName">Archive</xsl:variable>
	<xsl:variable name="archiveBundleDescription">Blog post archive</xsl:variable>
	<xsl:variable name="archiveBundleMenuitemDescription">Show all blog posts from </xsl:variable>
	<xsl:variable name="pageBreadcrumbText">Page </xsl:variable>
	<xsl:variable name="archiveBreadcrumbText">Archive </xsl:variable>
	<xsl:variable name="tagsBreadcrumbText">Tags </xsl:variable>
	<xsl:variable name="tagBreadcrumbText">Tag </xsl:variable>
	<xsl:variable name="addBlogPostBreadcrumbText">Add blog post</xsl:variable>
	<xsl:variable name="updateBlogPostBreadcrumbText">"Edit blog post </xsl:variable>
	<xsl:variable name="updateCommentBreadcrumbText">Edit comment</xsl:variable>
	
	<xsl:variable name="ShowPage.noPostsTitle">The blog is empty</xsl:variable>
	<xsl:variable name="ShowPage.noPostsText">Nobody has posted anything in this blog yet.</xsl:variable>	
	
	<xsl:variable name="BlogPost.addCommentTitle">Add comment</xsl:variable>
	<xsl:variable name="BlogPost.half.readMore">Click here to read the full post </xsl:variable>
	<xsl:variable name="BlogPost.anonymousCommentsDisabled">Anonymous comments are currently disabled</xsl:variable>	
	<xsl:variable name="BlogPost.comments">Comments</xsl:variable>
	<xsl:variable name="BlogPost.name">Name</xsl:variable>
	<xsl:variable name="BlogPost.mail">E-mail (will not be published)</xsl:variable>
	<xsl:variable name="BlogPost.website">Website</xsl:variable>
	<xsl:variable name="BlogPost.captcha">Captcha code</xsl:variable>
	<xsl:variable name="BlogPost.regenerateCaptcha">Generate new code</xsl:variable>
	<xsl:variable name="BlogPost.olderPosts">Older posts</xsl:variable>
	<xsl:variable name="BlogPost.newerPosts">Newer posts</xsl:variable>
	
	<xsl:variable name="UpdateComment.title">Update comment</xsl:variable>
	
	<xsl:variable name="AddBlogPost.title">Add blog post</xsl:variable>
	
	<xsl:variable name="UpdateBlogPost.title">Update blog post</xsl:variable>
	
	<xsl:variable name="tag.linkTitle">Show all blog posts tagged with</xsl:variable>
	
	<xsl:variable name="ShowTags.title">Tags</xsl:variable>
	<xsl:variable name="ShowTags.noTagsFound">No tags found</xsl:variable>
	
	<xsl:variable name="ShowArchive.title">Archive</xsl:variable>
	<xsl:variable name="ArchiveEntry.noPostsFound">No posts found</xsl:variable>
	
	<xsl:variable name="validationError.requiredField" select="'You need to fill in the field'" />
	<xsl:variable name="validationError.invalidFormat" select="'Invalid value in field'" />
	<xsl:variable name="validationError.tooShort" select="'Too short content in field'" />
	<xsl:variable name="validationError.tooLong" select="'Too long content in field'" />		
	<xsl:variable name="validationError.unknownError" select="'Unknown problem validating field'" />				
				
	<xsl:variable name="validationError.field.alias" select="'alias'" />
	<xsl:variable name="validationError.field.message" select="'message'" />
	<xsl:variable name="validationError.field.title" select="'title'" />
	<xsl:variable name="validationError.field.posterName" select="'name'"/>
	<xsl:variable name="validationError.field.posterEmail" select="'e-mail'"/>
	<xsl:variable name="validationError.field.posterWebsite" select="'website'"/>
	
	<xsl:variable name="validationError.message.InvalidCaptchaConfirmation">Invalid captcha code</xsl:variable>
	<xsl:variable name="validationError.message.AliasAlreadyTaken">The alias you have entered is already taken by another blog post</xsl:variable>
	<xsl:variable name="validationError.message.unknownFault" select="'An unknown error has occured!'" />
	
</xsl:stylesheet>