<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="../../core/utils/xsl/Common.xsl" />
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/CKEditor.xsl" />

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js
	</xsl:variable>	

	<xsl:template match="Document">		
	
		<xsl:apply-templates select="ShowPage" />
		<xsl:apply-templates select="ShowPost" />
		<xsl:apply-templates select="AddBlogPost" />
		<xsl:apply-templates select="UpdateBlogPost" />
		<xsl:apply-templates select="UpdateComment" />
		<xsl:apply-templates select="ShowTags" />
		<xsl:apply-templates select="ShowTagPosts" />
		<xsl:apply-templates select="ShowArchive" />
		<xsl:apply-templates select="ShowArchiveMonth" />
			
	</xsl:template>
	
	<xsl:template match="ShowArchive">
		
		<div class="contentitem">		
		
			<h1>
				<xsl:value-of select="$ShowArchive.title" />
			</h1>
			
			<xsl:choose>
				<xsl:when test="ArchiveEntry">
					
					<xsl:apply-templates select="ArchiveEntry" />
					
				</xsl:when>
				<xsl:otherwise>
				
					<p><xsl:value-of select="$ArchiveEntry.noPostsFound" /></p>
				
				</xsl:otherwise>
			</xsl:choose>
			
		</div>
	
	</xsl:template>
	
	<xsl:template match="ArchiveEntry">
		
		<p>
			<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/archive/{year}/{month}">
				
				<xsl:value-of select="year" />
				
				<xsl:text> </xsl:text>
				
				<xsl:call-template name="getMonthName">
					<xsl:with-param name="monthName" select="month" />
				</xsl:call-template>
				
				<xsl:text> (</xsl:text>
				
				<xsl:value-of select="postCount" />
				
				<xsl:text> </xsl:text>
				
				<xsl:choose>
					<xsl:when test="postCount &gt; 1">
						<xsl:value-of select="$posts" />	
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$post" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>)</xsl:text>
			</a>
		</p>
	
	</xsl:template>	
	
	<xsl:template name="getMonthName">
		
		<xsl:param name="monthName" />
		
		<xsl:choose>
			<xsl:when test="$monthName='january'">
				<xsl:value-of select="$january" />
			</xsl:when>
			<xsl:when test="$monthName='february'">
				<xsl:value-of select="$february" />
			</xsl:when>
			<xsl:when test="$monthName='march'">
				<xsl:value-of select="$march" />
			</xsl:when>
			<xsl:when test="$monthName='april'">
				<xsl:value-of select="$april" />
			</xsl:when>
			<xsl:when test="$monthName='may'">
				<xsl:value-of select="$may" />
			</xsl:when>
			<xsl:when test="$monthName='june'">
				<xsl:value-of select="$june" />
			</xsl:when>
			<xsl:when test="$monthName='july'">
				<xsl:value-of select="$july" />
			</xsl:when>
			<xsl:when test="$monthName='august'">
				<xsl:value-of select="$august" />
			</xsl:when>
			<xsl:when test="$monthName='september'">
				<xsl:value-of select="$september" />
			</xsl:when>
			<xsl:when test="$monthName='october'">
				<xsl:value-of select="$october" />
			</xsl:when>
			<xsl:when test="$monthName='november'">
				<xsl:value-of select="$november" />
			</xsl:when>
			<xsl:when test="$monthName='december'">
				<xsl:value-of select="$december" />
			</xsl:when>
		</xsl:choose>
	
	</xsl:template>
	
	<xsl:template match="ShowTagPosts">
	
		<xsl:choose>
			<xsl:when test="count(BlogPost) = 1">
			
				<xsl:apply-templates select="BlogPost">
					<xsl:with-param name="part2" select="'true'" />
				</xsl:apply-templates>
				
			</xsl:when>
			<xsl:otherwise>
			
				<xsl:apply-templates select="BlogPost" />
				
			</xsl:otherwise>
		</xsl:choose>
		
	
	</xsl:template>
	
	<xsl:template match="ShowArchiveMonth">
	
			<xsl:choose>
				<xsl:when test="blogPosts">
								
					<xsl:choose>
						<xsl:when test="count(blogPosts/BlogPost) = 1">
						
							<xsl:apply-templates select="blogPosts/BlogPost">
								<xsl:with-param name="part2" select="'true'" />
							</xsl:apply-templates>
							
						</xsl:when>
						<xsl:otherwise>
						
							<xsl:apply-templates select="blogPosts/BlogPost" />
							
						</xsl:otherwise>
					</xsl:choose>
					
				</xsl:when>
				<xsl:otherwise>
				
					<div class="contentitem">		
					
						<h1>
							<xsl:value-of select="$ShowArchive.title" />
							
							<xsl:text> </xsl:text>
							
							<xsl:call-template name="getMonthName">
								<xsl:with-param name="monthName" select="month" />
							</xsl:call-template>
							
							<xsl:text> </xsl:text>
							
							<xsl:value-of select="year" />														
						</h1>				
				
						<p><xsl:value-of select="$ArchiveEntry.noPostsFound" /></p>
				
					</div>
				
				</xsl:otherwise>
			</xsl:choose>	
	
	</xsl:template>	
	
	<xsl:template match="ShowTags">
		
		<div class="contentitem">		
		
			<h1>
				<xsl:value-of select="$ShowTags.title" />
			</h1>
			
			<xsl:choose>
				<xsl:when test="tags">
					
					<xsl:apply-templates select="tags/TagEntry" mode="list" />
					
				</xsl:when>
				<xsl:otherwise>
				
					<p><xsl:value-of select="$ShowTags.noTagsFound" /></p>
				
				</xsl:otherwise>
			</xsl:choose>
			
		</div>
		
	</xsl:template>
	
	<xsl:template match="TagEntry" mode="list">
		
		<p>
			<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/tag/{tagName}">
				<xsl:value-of select="tagName" />
				
				<xsl:text> (</xsl:text>
				
				<xsl:value-of select="postCount" />
				
				<xsl:text> </xsl:text>
				
				<xsl:choose>
					<xsl:when test="postCount &gt; 1">
						<xsl:value-of select="$posts" />	
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$post" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>)</xsl:text>
			</a>
		</p>
	
	</xsl:template>
	
	<xsl:template match="UpdateComment">
	
		<div class="contentitem">		
		
			<h1>
				<xsl:value-of select="$UpdateComment.title" />
			</h1>
			
			<xsl:apply-templates select="validationException/validationError" />
			
			<form method="POST" action="{/Document/requestinfo/uri}">
				<table class="full">				
					<tr>
						<td><xsl:value-of select="$message" />:</td>
					</tr>
					<tr>
						<td>
							<xsl:call-template name="createTextArea">
								<xsl:with-param name="name" select="'message'" />
								<xsl:with-param name="element" select="Comment" />
								<xsl:with-param name="rows" select="10" />							
							</xsl:call-template>										
						</td>
					</tr>												
				</table>

				<div class="floatright">
					<input type="submit" value="{$saveChanges}" />
				</div>							
			</form>
		</div>
	
	</xsl:template>
	
	<xsl:template match="UpdateBlogPost">
	
		<div class="contentitem">
				
			<h1>
				<xsl:value-of select="$UpdateBlogPost.title" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="BlogPost/title" />
			</h1>
			
			<xsl:apply-templates select="validationException/validationError" />
			
			<form method="POST" action="{/Document/requestinfo/uri}">
				<table class="full">
					<tr>
						<td width="10%"><xsl:value-of select="$title" />:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'title'" />
								<xsl:with-param name="element" select="BlogPost" />															
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td width="10%"><xsl:value-of select="$alias" />:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'alias'" />
								<xsl:with-param name="element" select="BlogPost" />							
							</xsl:call-template>
						</td>
					</tr>					
					<tr>
						<td colspan="2"><xsl:value-of select="$message" />:</td>
					</tr>
					<tr>
						<td colspan="2">
							<textarea class="fckeditor" name="message" rows="10">								
								<xsl:choose>
									<xsl:when test="requestparameters/parameter[name='message']">
										<xsl:value-of select="requestparameters/parameter[name='message']/value" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="BlogPost/message" />
									</xsl:otherwise>
								</xsl:choose>								
							</textarea>												
						</td>
					</tr>												
				</table>
		
				<xsl:call-template name="initFCKEditor" />
		
				<p>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" />
					<xsl:value-of select="$splitTagInfo" />
				</p>
				
				<h2><xsl:value-of select="$tags" /></h2>
		
				<fieldset>
					<legend><xsl:value-of select="$existingTags" /></legend>
					<div class="scrolllist">			
						<xsl:apply-templates select="tags/TagEntry" />
					</div>
				</fieldset>
		
				<fieldset>
					<legend><xsl:value-of select="$newTags" /></legend>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'newtags'" />
							<xsl:with-param name="rows" select="5" />							
						</xsl:call-template>						
				</fieldset>		
		
				<div class="floatright">
					<input type="submit" value="{$saveChanges}" />
				</div>
							 			
			</form>			
		</div>
	
	</xsl:template>	
	
	<xsl:template match="AddBlogPost">
	
		<div class="contentitem">	
		
			<h1><xsl:value-of select="$AddBlogPost.title" /></h1>
			
			<xsl:apply-templates select="validationException/validationError" />
			
			<form method="POST" action="{/Document/requestinfo/uri}">
				<table class="full">
					<tr>
						<td width="10%"><xsl:value-of select="$title" />:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'title'" />							
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td width="10%"><xsl:value-of select="$alias" />:</td>
						<td>
							<xsl:call-template name="createTextField">
								<xsl:with-param name="name" select="'alias'" />							
							</xsl:call-template>
						</td>
					</tr>					
					<tr>
						<td colspan="2"><xsl:value-of select="$message" />:</td>
					</tr>
					<tr>
						<td colspan="2">
							<textarea class="fckeditor" name="message" rows="10">
								<xsl:value-of select="requestparameters/parameter[name='message']/value" />
							</textarea>												
						</td>
					</tr>												
				</table>
		
				<xsl:call-template name="initFCKEditor" />
		
				<p>
					<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/info.png" />
					<xsl:value-of select="$splitTagInfo" />
				</p>
		
				<h2><xsl:value-of select="$tags" /></h2>
		
				<fieldset>
					<legend><xsl:value-of select="$existingTags" /></legend>
					<div class="scrolllist">			
						<xsl:apply-templates select="tags/TagEntry" />
					</div>
				</fieldset>
		
				<fieldset>
					<legend><xsl:value-of select="$newTags" /></legend>
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="name" select="'newtags'" />
							<xsl:with-param name="rows" select="5" />							
						</xsl:call-template>						
				</fieldset>		
		
				<div class="floatright">
					<input type="submit" value="{$add}" />
				</div>
								 			
			</form>			
		</div>
	
	</xsl:template>
	
	<xsl:template match="TagEntry">
		<div class="floatleft full border marginbottom">
			<div class="floatleft">

				<xsl:value-of select="tagName" />
				
				<xsl:text>&#x20;(</xsl:text>
							
				<xsl:value-of select="postCount" />							
				
				<xsl:text>)</xsl:text>
							
			</div>
			<div class="floatright marginright">				
				<xsl:call-template name="createCheckbox">
					<xsl:with-param name="name" select="'tag'" />
					<xsl:with-param name="value" select="tagName" />
					<xsl:with-param name="element" select="../../BlogPost/tags" />
					<xsl:with-param name="requestparameters" select="../../requestparameters" />
				</xsl:call-template>
			</div>				
		</div>	
	</xsl:template>
		
	<xsl:template match="ShowPage">
	
		<xsl:choose>
			<xsl:when test="Posts">
				<xsl:apply-templates select="Posts/BlogPost" />
			</xsl:when>
			<xsl:otherwise>
				<div class="contentitem">
					<h1><xsl:value-of select="$ShowPage.noPostsTitle" /></h1>
					<p>
						<xsl:value-of select="$ShowPage.noPostsText" />
					</p>
					
					<xsl:if test="/Document/isAdmin">
						<div class="floatright marginright">
							<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addpost" title="{$addPost}">
								<xsl:value-of select="$addPost" />
								<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/page_text_add.png" />
							</a>
						</div>						
					</xsl:if>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>
	
	<xsl:template match="ShowPost">
		
		<xsl:apply-templates select="BlogPost">
			<xsl:with-param name="part2" select="'true'" />
		</xsl:apply-templates>
			
	</xsl:template>
	
	<xsl:template match="Comment">
	
		<hr />
		
		<div class="full">
	
			<xsl:if test="/Document/isAdmin">
				<div class="floatright marginright">
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updatecomment/{commentID}" title="{$editComment}">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/comment_edit.png" />
					</a>
					<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/deletecomment/{commentID}" title="{$deleteComment}?" onclick="return confirm('{$deleteComment}?');">
						<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/comment_delete.png" />
					</a>												
				</div>						
			</xsl:if>	
		
			<a name="comment{commentID}" />
						
			<p class="tiny">
			
				<xsl:value-of select="$postedBy" />
				
				<xsl:choose>
					<xsl:when test="posterName">
						
						<xsl:choose>
							<xsl:when test="posterWebsite">
								<a href="{posterWebsite}">
									<xsl:value-of select="posterName" />
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="posterName" />
							</xsl:otherwise>
						</xsl:choose>
						
						<xsl:if test="/Document/isAdmin">
							<xsl:text> </xsl:text>
							<a href="mailto:{posterEmail}">
								<xsl:text>@</xsl:text>
							</a>
						</xsl:if>
						
					</xsl:when>
					<xsl:otherwise>
					
						<xsl:call-template name="user">
							<xsl:with-param name="user" select="poster/user" />
						</xsl:call-template>
								
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text> </xsl:text>
				
				<xsl:value-of select="added" />				
				
				<xsl:if test="updated">
				
					<xsl:text> | </xsl:text>
				
					<xsl:value-of select="$updatedBy" />
				
					<xsl:call-template name="user">
						<xsl:with-param name="user" select="editor/user" />
					</xsl:call-template>
					
					<xsl:text> </xsl:text>
					
					<xsl:value-of select="updated" />					
				</xsl:if>				
				
			</p>

			<p>
				<xsl:call-template name="replaceLineBreak">
					<xsl:with-param name="string" select="message" />
				</xsl:call-template> 
			</p>			
		</div>
			
	</xsl:template>
	
	<xsl:template match="BlogPost">
		
		<xsl:param name="part2" select="'false'" />
				
		<div class="contentitem">
			
				<xsl:if test="/Document/isAdmin">
					<div class="floatright marginright">
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addpost" title="{$addPost}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/page_text_add.png" />
						</a>
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/updatepost/{postID}" title="{$editPost} {title}">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/page_text_edit.png" />
						</a>
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/deletepost/{postID}" title="{$deletePost} {title}?" onclick="return confirm('{$deletePost} {title}?');">
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/page_text_delete.png" />
						</a>												
					</div>						
				</xsl:if>			
			
			<h1>
				<a name="{alias}" href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/post/{alias}" title="{$BlogPost.half.readMore}">			
					<xsl:value-of select="title" />
				</a>
			</h1>
			
			<div>
				<span class="tiny">
					<xsl:value-of select="$postedBy" />
					
					<xsl:call-template name="user">
						<xsl:with-param name="user" select="poster/user" />
					</xsl:call-template>
					
					<xsl:text> </xsl:text>
					
					<xsl:value-of select="added" />
					
					<xsl:if test="updated">
					
						<xsl:text> | </xsl:text>
					
						<xsl:value-of select="$updatedBy" />
					
						<xsl:call-template name="user">
							<xsl:with-param name="user" select="editor/user" />
						</xsl:call-template>
						
						<xsl:text> </xsl:text>
						
						<xsl:value-of select="updated" />					
					</xsl:if>
	
					<xsl:if test="/Document/allowComments">
						<xsl:text> | </xsl:text>
						
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/post/{alias}" title="{$BlogPost.half.readMore}">
							<xsl:value-of select="count(comments/Comment)" /> 
							<xsl:text> </xsl:text>
							<xsl:value-of select="$comments" />
						</a>
					</xsl:if>
					
					<xsl:if test="tags">
					
						<xsl:text> | </xsl:text>
						
						<xsl:value-of select="$tags" />
						
						<xsl:text>: </xsl:text>
						
						<xsl:apply-templates select="tags/tag" />
					
					</xsl:if>
				</span>			
			</div>
			
			<xsl:value-of select="textPart1" disable-output-escaping="yes" />
			
			<xsl:choose>
				<xsl:when test="textPart2 and $part2 = 'false'">
					<div class="floatright">
						<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/post/{alias}" title="{$BlogPost.half.readMore}">
							<xsl:value-of select="$BlogPost.half.readMore" />
							<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/arrow_right.png" />
						</a>					
					</div>					
				</xsl:when>
				<xsl:when test="textPart2 and $part2 = 'true'">
					<xsl:value-of select="textPart2" disable-output-escaping="yes" />				
				</xsl:when>
			</xsl:choose>
			
			<xsl:choose>
				<xsl:when test="$part2='true'">
					<xsl:if test="/Document/allowComments">
						
						<xsl:if test="comments">

							<a name="comments"/>
							<h2><xsl:value-of select="$BlogPost.comments" /></h2>				
							<xsl:apply-templates select="comments/Comment" />
						
						</xsl:if>
					
						<hr />
						
						<xsl:choose>
							<xsl:when test="../isLoggedIn">
							
								<a name="addcomment" />
							
								<h2>
									<xsl:value-of select="$BlogPost.addCommentTitle" />
								</h2>
							
								<xsl:apply-templates select="../validationException/validationError" />					
							
								<form method="POST" action="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addcomment/{alias}#addcomment">
									
									<div class="marginbottom">
										<xsl:call-template name="createTextArea">
											<xsl:with-param name="name" select="'message'" />
											<xsl:with-param name="requestparameters" select="../requestparameters" />
											<xsl:with-param name="rows" select="5" />							
										</xsl:call-template>
									</div>
									
									<div class="floatright">
										<input type="submit" value="{$add}" />
									</div>						
								</form>
							</xsl:when>
							<xsl:when test="../allowAnonymousComments or ../../allowAnonymousComments">
								
								<a name="addcomment" />
								
								<h2>
									<xsl:value-of select="$BlogPost.addCommentTitle" />
								</h2>
								
								<xsl:apply-templates select="../validationException/validationError" />					
							
								<form method="POST" action="{/Document/requestinfo/currentURI}/{/Document/module/alias}/addcomment/{alias}#addcomment">						
									<table class="full">
										<tr>
											<td width="180px">
												<xsl:value-of select="$BlogPost.name" />
												<span class="required">*</span>
											</td>
											<td>
												<xsl:call-template name="createTextField">
													<xsl:with-param name="name" select="'posterName'" />
													<xsl:with-param name="requestparameters" select="../requestparameters" />
												</xsl:call-template>
											</td>
										</tr>
										<tr>
											<td>
												<xsl:value-of select="$BlogPost.mail" />
												<span class="required">*</span>
											</td>
											<td>
												<xsl:call-template name="createTextField">
													<xsl:with-param name="name" select="'posterEmail'" />
													<xsl:with-param name="requestparameters" select="../requestparameters" />
												</xsl:call-template>
											</td>
										</tr>
										<tr>
											<td>
												<xsl:value-of select="$BlogPost.website" />
											</td>
											<td>
												<xsl:call-template name="createTextField">
													<xsl:with-param name="name" select="'posterWebsite'" />
													<xsl:with-param name="requestparameters" select="../requestparameters" />
												</xsl:call-template>
											</td>
										</tr>
										<tr>
											<td colspan="2" class="text-align-center">
												<img id="captchaimg" src="{/Document/requestinfo/currentURI}/{/Document/module/alias}/captcha" />
												<br />
												<a href="javascript:document.getElementById('captchaimg').setAttribute('src','{/Document/requestinfo/currentURI}/{/Document/module/alias}/captcha?' + (new Date()).getTime());">
													<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/reload.png" />
													<xsl:value-of select="$BlogPost.regenerateCaptcha" />
												</a>										
											</td>
										</tr>
										<tr>
											<td>
												<xsl:value-of select="$BlogPost.captcha" />
												<span class="required">*</span>
											</td>
											<td>
												<xsl:call-template name="createTextField">
													<xsl:with-param name="name" select="'captchaConfirmation'" />
													<xsl:with-param name="requestparameters" select="../requestparameters" />
												</xsl:call-template>
											</td>
										</tr>								
									</table>
									
									<xsl:call-template name="createTextArea">
										<xsl:with-param name="name" select="'message'" />
										<xsl:with-param name="requestparameters" select="../requestparameters" />
										<xsl:with-param name="rows" select="8" />							
									</xsl:call-template>
									
									<div class="floatright">
										<input type="submit" value="{$add}" />
									</div>							
								</form>
								
							</xsl:when>
							<xsl:otherwise>
								<p>
									<xsl:value-of select="$BlogPost.anonymousCommentsDisabled" />
								</p>
							</xsl:otherwise>
						</xsl:choose>			
				
					</xsl:if>
				
				</xsl:when>
				<xsl:otherwise>
					
						<xsl:if test="position() = last() and ../../pageNumber">
							
							<hr class="full clearboth" />
							
							<xsl:if test="../../pageNumber &gt; 1">
								<div class="floatright">
									<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/page/{../../pageNumber - 1}" title="{$BlogPost.newerPosts}">
										<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/arrow_right.png" />
										<xsl:value-of select="$BlogPost.newerPosts" />
									</a>					
								</div>								
							</xsl:if>
							
							<xsl:if test="not(../../lastPage)">
								<div class="floatleft">
									<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/page/{../../pageNumber + 1}" title="{$BlogPost.olderPosts}">
										<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/arrow_left.png" />
										<xsl:value-of select="$BlogPost.olderPosts" />
									</a>					
								</div>								
							</xsl:if>

						</xsl:if>

				</xsl:otherwise>
			</xsl:choose>			
		
		</div>		
	
	</xsl:template>
	
	<xsl:template match="tag">
	
		<a href="{/Document/requestinfo/currentURI}/{/Document/module/alias}/tag/{.}" title="{$tag.linkTitle} {.}">
			<xsl:value-of select="." />
		</a>
	
		<xsl:if test="position() != last()">
			<xsl:text>, </xsl:text>
		</xsl:if>
	
	</xsl:template>
	
	<xsl:template name="user">
	
		<xsl:param name="user" />
	
		<xsl:choose>
			<xsl:when test="$user">
				<xsl:choose>
					<xsl:when test="/Document/displayFullName='true'">
						<xsl:value-of select="$user/firstname" />
			
						<xsl:text>&#x20;</xsl:text>
			
						<xsl:value-of select="$user/lastname" />					
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$user/username" />	
					</xsl:otherwise>
				</xsl:choose>			
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$deletedUser" />			
			</xsl:otherwise>
		</xsl:choose>
	
	</xsl:template>
	
	<xsl:template match="validationError">
		<xsl:if test="fieldName and validationErrorType and not(messageKey)">
			<p class="error">
				<xsl:choose>
					<xsl:when test="validationErrorType='RequiredField'">
						<xsl:value-of select="$validationError.requiredField" />
					</xsl:when>
					<xsl:when test="validationErrorType='InvalidFormat'">
						<xsl:value-of select="$validationError.invalidFormat" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooShort'">
						<xsl:value-of select="$validationError.tooShort" />
					</xsl:when>
					<xsl:when test="validationErrorType='TooLong'">
						<xsl:value-of select="$validationError.tooLong" />
					</xsl:when>		
					<xsl:otherwise>
						<xsl:value-of select="$validationError.unknownError" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>&#x20;</xsl:text>
				
				<xsl:choose>
					<xsl:when test="fieldName = 'alias'">
						<xsl:value-of select="$validationError.field.alias" />
					</xsl:when>
					<xsl:when test="fieldName = 'title'">
						<xsl:value-of select="$validationError.field.title" />
					</xsl:when>
					<xsl:when test="fieldName = 'message'">
						<xsl:value-of select="$validationError.field.message" />
					</xsl:when>
					<xsl:when test="fieldName = 'posterName'">
						<xsl:value-of select="$validationError.field.posterName" />
					</xsl:when>
					<xsl:when test="fieldName = 'posterEmail'">
						<xsl:value-of select="$validationError.field.posterEmail" />
					</xsl:when>
					<xsl:when test="fieldName = 'posterWebsite'">
						<xsl:value-of select="$validationError.field.posterWebsite" />
					</xsl:when>																			
					<xsl:otherwise>
						<xsl:value-of select="fieldName" />
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:text>!</xsl:text>		
			</p>
		</xsl:if>
		
		<xsl:if test="messageKey">
			<p class="error">
				<xsl:choose>
					<xsl:when test="messageKey='InvalidCaptchaConfirmation'">
						<xsl:value-of select="$validationError.message.InvalidCaptchaConfirmation" />
					</xsl:when>
					<xsl:when test="messageKey='AliasAlreadyTaken'">
						<xsl:value-of select="$validationError.message.AliasAlreadyTaken" />
					</xsl:when>									
					<xsl:otherwise>
						<xsl:text><xsl:value-of select="$validationError.message.unknownFault" /></xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</p>
		</xsl:if>
		
	</xsl:template>
	
	<xsl:template name="initFCKEditor">
		
		<!-- Call global CKEditor init template -->
		<xsl:call-template name="initializeFCKEditor">
			<xsl:with-param name="basePath"><xsl:value-of select="/Document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/Document/module/sectionID" />/<xsl:value-of select="/Document/module/moduleID" />/ckeditor/</xsl:with-param>
			<xsl:with-param name="customConfig">config.js</xsl:with-param>
			<xsl:with-param name="editorContainerClass">fckeditor</xsl:with-param>
			<xsl:with-param name="editorHeight">400</xsl:with-param>
			<xsl:with-param name="filebrowserBrowseUri">filemanager/index.html?Connector=<xsl:value-of select="/Document/requestinfo/currentURI" />/<xsl:value-of select="/Document/module/alias" />/connector</xsl:with-param>
			<xsl:with-param name="filebrowserImageBrowseUri">filemanager/index.html?Connector=<xsl:value-of select="/Document/requestinfo/currentURI" />/<xsl:value-of select="/Document/module/alias" />/connector</xsl:with-param>
			<xsl:with-param name="contentsCss">
				<xsl:if test="cssPath">
					<xsl:value-of select="/document/requestinfo/contextpath" /><xsl:value-of select="cssPath"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
		
	</xsl:template>				
</xsl:stylesheet>

