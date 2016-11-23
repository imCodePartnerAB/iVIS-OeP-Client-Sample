<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	
	<xsl:variable name="globalscripts">
		/jquery/jquery.js
	</xsl:variable>	
	
	<xsl:variable name="scripts">
		/js/nivoslider/jquery.nivo.slider.js
	</xsl:variable>
	
	<xsl:variable name="links">
		/js/nivoslider/nivo-slider.css
		/js/nivoslider/custom-nivo-slider.css
	</xsl:variable>
	
	<xsl:template match="Document">		
		
		<xsl:apply-templates select="ImageSlider" />
		
	</xsl:template>
	
	<xsl:template match="ImageSlider">
		
		<xsl:value-of select="imageSliderHTML" disable-output-escaping="yes" />
		
		<script type="text/javascript">
			$(window).load(function() {
				$('#slider').nivoSlider({
					effect:'<xsl:value-of select="animationEffect" />',
					animSpeed: <xsl:value-of select="animationSpeed" />,
					pauseTime: <xsl:value-of select="pauseTime" />,
					directionNav: <xsl:value-of select="directionNavigation" />
				});
			}); 
		</script>
		
	</xsl:template>
	
</xsl:stylesheet>