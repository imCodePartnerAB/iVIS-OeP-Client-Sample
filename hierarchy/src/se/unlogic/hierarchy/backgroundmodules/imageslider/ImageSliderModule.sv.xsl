<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="ImageSliderModuleTemplates.xsl" />
	
	<xsl:variable name="imageSliderHTML">
<![CDATA[
<div id="sliderWrapper" style="display: block;">
	<div id="slider">
		<a href="#">
			<img alt="" src="/kvalifikator/images/image-slider/slide1.gif" title="#htmlcaption" />
		</a>
		<a href="#">
			<img alt="" src="/kvalifikator/images/image-slider/slide2.gif" title="#htmlcaption2" />
		</a>
	</div>
</div>
<div class="nivo-html-caption" id="htmlcaption">
	<h3>Aktiviteter</h3>
	<p>Kartlägger och kvalificerar individers kunskap, färdigheter och kompetenser i anslutning till aktiviteter ute på arbetsplatser</p>
</div>
<div class="nivo-html-caption" id="htmlcaption2">
	<h3>Läranderesultat</h3>
	<p>Utgår inte bara från individens formella betyg utan beaktar hela det samlade läranderesultatet</p>
</div>
]]>
	</xsl:variable>

</xsl:stylesheet>