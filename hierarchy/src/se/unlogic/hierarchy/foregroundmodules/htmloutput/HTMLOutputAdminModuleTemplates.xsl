<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>
	
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>
	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/CKEditor.xsl" />

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js
	</xsl:variable>	
	
	<xsl:template match="Document">
		<xsl:apply-templates select="Settings"/>
	</xsl:template>
	
	<xsl:template match="Settings">
		<div class="contentitem">
			<form method="post" action="{/Document/requestinfo/uri}">
				
				<h1><xsl:value-of select="$i18n.header" /></h1>
				
				<xsl:apply-templates select="validationError"/>
				
				<xsl:call-template name="createTextArea">
					<xsl:with-param name="name" select="'html'"/>
					<xsl:with-param name="class" select="'fckeditor'"/>
					<xsl:with-param name="element" select="."/>
				</xsl:call-template>
				
				<xsl:if test="redirect">
					
					<input type="hidden" name="redirect" value="{redirect}"/>
					
				</xsl:if>
				
				<br/>
				
				<div class="floatright">
					<input type="submit" value="{$i18n.save}" class="marginright" />
				</div>
				
				<xsl:call-template name="initializeFCKEditor">
					<xsl:with-param name="basePath"><xsl:value-of select="/Document/requestinfo/contextpath" />/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/ckeditor/</xsl:with-param>
					<xsl:with-param name="customConfig">config.js</xsl:with-param>
					<xsl:with-param name="editorContainerClass" select="'fckeditor'" />
					<xsl:with-param name="filebrowserBrowseUri">filemanager/index.html?Connector=<xsl:value-of select="/Document/requestinfo/currentURI" />/<xsl:value-of select="/Document/module/alias" />/connector</xsl:with-param>
					<xsl:with-param name="filebrowserImageBrowseUri">filemanager/index.html?Connector=<xsl:value-of select="/Document/requestinfo/currentURI" />/<xsl:value-of select="/Document/module/alias" />/connector</xsl:with-param>					
					<xsl:with-param name="editorHeight">
						<xsl:choose>
							<xsl:when test="/Document/editorHeight">
								<xsl:value-of select="/Document/editorHeight" />
							</xsl:when>
							<xsl:otherwise>200</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="contentsCss">
						<xsl:if test="/Document/cssPath">
							<xsl:value-of select="/Document/cssPath"/>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="bodyClass">
						<xsl:if test="/Document/adminCssClass">
							<xsl:value-of select="/Document/adminCssClass"/>
						</xsl:if>
					</xsl:with-param>		
				</xsl:call-template>
	
			</form>
		</div>
	</xsl:template>
	
	<xsl:template match="validationError">
		<div class="full floatleft">
			<xsl:if test="fieldName and validationErrorType">
				<p class="error">
					<xsl:choose>
						<xsl:when test="validationErrorType='RequiredField'">
							<xsl:value-of select="$validationError.RequiredField" />
						</xsl:when>
						<xsl:when test="validationErrorType='InvalidFormat'">
							<xsl:value-of select="$validationError.InvalidFormat" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$validationError.unknownValidationErrorType" />
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text>&#x20;</xsl:text>
					<xsl:choose>
						<xsl:when test="fieldName = 'html'">
							<xsl:value-of select="$i18n.content" />!
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="fieldName" />
						</xsl:otherwise>
					</xsl:choose>
				</p>
			</xsl:if>
			<xsl:if test="messageKey">
				<p class="error"></p>
			</xsl:if>
		</div>
	</xsl:template>
	
</xsl:stylesheet>