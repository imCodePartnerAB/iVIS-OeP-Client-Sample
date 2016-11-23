<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	
	<xsl:template match="Document">		
		
		<div id="SocialMediaModule" class="clearboth floatright">
			<xsl:if test="showTwitter">
				<div class="floatright">
					<xsl:choose>
						<xsl:when test="protocol = 'https'">
							<a target="_blank" rel="nofollow" title="{$shareTwitter}" href="{protocol}://twitter.com/home?status={$reading}+{url}"><img src="{staticcontentPath}/pics/tweet.png" /></a>
						</xsl:when>
						<xsl:otherwise>
							<a href="http://twitter.com/share" class="twitter-share-button" data-url="{url}" data-count="horizontal">Tweet</a><script type="text/javascript" src="http://platform.twitter.com/widgets.js"></script>
						</xsl:otherwise>
					</xsl:choose>
					
	 				
				</div>
			</xsl:if>
			<xsl:if test="showFacebook">
				<div class="floatright">
					<iframe src="{protocol}://www.facebook.com/plugins/like.php?locale={$locale}&amp;href={url}&amp;layout=button_count&amp;show_faces=false&amp;width=80&amp;action=like&amp;colorscheme=light&amp;height=21" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:80px; height:21px;" allowTransparency="true"></iframe>
				</div>
			</xsl:if>
		</div>
		
	</xsl:template>
	
</xsl:stylesheet>