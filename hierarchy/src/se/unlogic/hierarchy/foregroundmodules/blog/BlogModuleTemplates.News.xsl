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
	
		<div id="BlogModule-News">
	
			<xsl:apply-templates select="ShowPage" />
			<xsl:apply-templates select="ShowPost" />
			<xsl:apply-templates select="AddBlogPost" />
			<xsl:apply-templates select="UpdateBlogPost" />
			<xsl:apply-templates select="UpdateComment" />
			<xsl:apply-templates select="ShowTags" />
			<xsl:apply-templates select="ShowTagPosts" />
			<xsl:apply-templates select="ShowArchive" />
			<xsl:apply-templates select="ShowArchiveMonth" />
			
		</div>	
			
	</xsl:template>
	
	
	<xsl:template match="BlogPost">
		
		<xsl:param name="part2" select="'true'" />
				
		<div class="contentitem">
			<div class="floatleft full margintop marginbottom">
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
			
				<a name="{alias}"></a>
				<h1 class="nomargin">
					<xsl:value-of select="title" />
				</h1>
				
				<h6><xsl:value-of select="substring(added,1,10)" /></h6>
				
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
				
				<hr class="full clearboth"/>
			
				<xsl:if test="position() = last() and ../../pageNumber">
										
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
			
			</div>
			
		</div>		
	
	</xsl:template>
	
</xsl:stylesheet>

