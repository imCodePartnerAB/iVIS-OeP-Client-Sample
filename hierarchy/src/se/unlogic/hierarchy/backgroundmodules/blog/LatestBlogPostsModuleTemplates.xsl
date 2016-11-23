<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	
	<xsl:template match="Document">		
		
		<xsl:apply-templates select="BlogPosts"/>
		
	</xsl:template>
	
	<xsl:template match="BlogPosts">
		
		<div id="LatestBlogPostsModule" class="contentitem">
			
			<div class="border content-box">
	            <h1 class="background">
					<span><xsl:value-of select="/Document/module/name"/></span>
	            </h1>
	           	<div class="content">
		           	<xsl:choose>
		           		<xsl:when test="BlogPost">
		           			<xsl:apply-templates select="BlogPost" />
							
							<div class="text-align-right">
								<a href="{blogURL}">
									<xsl:value-of select="$i18n.MorePosts" />
								</a>
							</div>		           			
		           		</xsl:when>
		           		<xsl:otherwise>
		           			<p><xsl:value-of select="$i18n.noPosts" /></p>
		           			<br/>
		           		</xsl:otherwise>
		           	</xsl:choose>
	           	</div>
	            <div class="footer" />
			</div>			
					
		</div>
		
	</xsl:template>
		
	<xsl:template match="BlogPost">
		
		<p>
			<img class="alignbottom" src="{/Document/contextpath}/static/b/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/page_text.png"/>
		
			<a title="{title}" href="{URL}#{alias}">
				<xsl:value-of select="title"/>
			</a>
		</p>
		<p class="tiny">
			<xsl:if test="position() != last()">
				<xsl:attribute name="class">tiny borderbottom</xsl:attribute>
			</xsl:if>
			
			<xsl:value-of select="added"/>
		</p>		
			
	</xsl:template>
	
</xsl:stylesheet>