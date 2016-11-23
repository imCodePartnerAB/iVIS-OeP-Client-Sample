<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	
	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/jquery/jquery-ui.js
		/jquery/plugins/jquery.hashchange.js
	</xsl:variable>	
	
	<xsl:variable name="scripts">
		/js/flash-wmode-fix.js
		/js/newpassword.js
	</xsl:variable>
	
	<xsl:variable name="links">
		/css/oh-ui-dialog.css
	</xsl:variable>
	
	<xsl:template match="Document">
		
		<script type="text/javascript">
			newPasswordModuleURI = '<xsl:value-of select="requestinfo/contextpath" /><xsl:value-of select="newPasswordModuleURI" />';
		</script>
		
		<div id="newpassword-dialog" class="oh-ui-dialog"></div>
	 
	</xsl:template>
</xsl:stylesheet>