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
		/js/login.js
	</xsl:variable>
	
	<xsl:variable name="links">
		/css/oh-ui-dialog.css
	</xsl:variable>
	
	<xsl:template match="Document">
				
		<script type="text/javascript">
			contextPath = '<xsl:value-of select="requestinfo/contextpath" />';
			loginModuleURI = '<xsl:value-of select="requestinfo/contextpath" /><xsl:value-of select="loginModuleURI" />';
			useModalRegistration = <xsl:value-of select="useModalRegistration" />
			iframeContent = '<xsl:value-of select="requestinfo/contextpath" />/static/b/<xsl:value-of select="module/sectionID" />/<xsl:value-of select="hashCode" />/staticform.html';
		</script>
		
		<iframe id="login-iframe" name="login-iframe" style="display:none"></iframe>
		
		<div id="login-dialog" class="oh-ui-dialog">
			
		</div>
	 
	</xsl:template>
	
</xsl:stylesheet>