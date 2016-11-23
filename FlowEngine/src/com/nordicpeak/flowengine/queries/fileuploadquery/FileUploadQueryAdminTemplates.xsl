<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js
	</xsl:variable>	

	<xsl:template match="Document">	
		
		<div id="FileUploadQueryProvider" class="contentitem">
		
			<xsl:apply-templates select="validationError"/>
			<xsl:apply-templates select="UpdateFileUploadQuery "/>
		
		</div>
		
	</xsl:template>
		
	<xsl:template match="UpdateFileUploadQuery">
	
		<h1><xsl:value-of select="$i18n.UpdateQuery" /><xsl:text>:&#160;</xsl:text><xsl:value-of select="FileUploadQuery/QueryDescriptor/name" /></h1>
		
		<xsl:apply-templates select="validationException/validationError" />
		
		<form id="updateFileUploadQueryForm" name="queryAdminForm" method="post" action="{/Document/requestinfo/uri}">
			
			<xsl:call-template name="createCommonFieldsForm">
				<xsl:with-param name="element" select="FileUploadQuery" />
			</xsl:call-template>
			
			<div class="floatleft full bigmarginbottom">
				<label for="allowedFileExtensions" class="floatleft clearboth"><xsl:value-of select="$i18n.AllowedFileExtensions" /></label>
				<div class="floatleft full">
					<xsl:call-template name="createTextArea">
						<xsl:with-param name="id" select="'allowedFileExtensions'"/>
						<xsl:with-param name="name" select="'allowedFileExtensions'"/>
						<xsl:with-param name="title" select="$i18n.AllowedFileExtensions"/>
						<xsl:with-param name="rows" select="'5'"/>
						<xsl:with-param name="value">
							<xsl:apply-templates select="FileUploadQuery/allowedFileExtensions/value" />
						</xsl:with-param>
					</xsl:call-template>
			    </div>
			</div>
			
			<div class="floatleft full bigmarginbottom">
				<label for="maxFileCount" class="floatleft clearboth"><xsl:value-of select="$i18n.MaxFileCount" /></label>
				<div class="floatleft full">
					<xsl:call-template name="createTextField">
						<xsl:with-param name="id" select="'maxFileCount'"/>
						<xsl:with-param name="name" select="'maxFileCount'"/>
						<xsl:with-param name="title" select="$i18n.MaxFileCount"/>
						<xsl:with-param name="size" select="'30'"/>
						<xsl:with-param name="element" select="FileUploadQuery" />
					</xsl:call-template>
			    </div>
			</div>
			
			<div class="floatleft full bigmarginbottom">
				<label for="maxFileSize" class="floatleft clearboth">
					<xsl:value-of select="$i18n.MaxFileSize" />
					<xsl:text>&#160;(</xsl:text>
					<xsl:value-of select="$i18n.MaxAllowedFileSize.Part1" />
					<xsl:text>&#160;</xsl:text>
					<xsl:value-of select="MaxAllowedFileSize"  />
					<xsl:text>&#160;</xsl:text>
					<xsl:value-of select="$i18n.MaxAllowedFileSize.Part2" />
					<xsl:text>)</xsl:text>
				</label>
				<div class="floatleft full">
					<xsl:call-template name="createTextField">
						<xsl:with-param name="id" select="'maxFileSize'"/>
						<xsl:with-param name="name" select="'maxFileSize'"/>
						<xsl:with-param name="title" select="$i18n.MaxFileSize"/>
						<xsl:with-param name="size" select="'30'"/>
						<xsl:with-param name="value" select="MaxFileSizeInMB" />
					</xsl:call-template>
			    </div>
			</div>
			
			<div class="floatright margintop clearboth">
				<input type="submit" value="{$i18n.SaveChanges}" />
			</div>
		
		</form>
		
	</xsl:template>

	<xsl:template match="value">
		
		<xsl:value-of select="." />
		
		<xsl:if test="position() != last()">
			<xsl:text>&#10;</xsl:text>
		</xsl:if>
		
	</xsl:template>

	<xsl:template match="validationError[messageKey = 'UpdateFailedFileUploadQueryNotFound']">
		
		<p class="error">
			<xsl:value-of select="$i18n.FileUploadQueryNotFound" />
		</p>
		
	</xsl:template>

	<xsl:template match="fieldName">
	
		<xsl:variable name="fieldName" select="." />
	
		<xsl:choose>
			<xsl:when test="$fieldName = 'maxFileCount'">
				<xsl:value-of select="$i18n.maxFileCount" />
			</xsl:when>
			<xsl:when test="$fieldName = 'maxFileSize'">
				<xsl:value-of select="$i18n.maxFileSize" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$fieldName" />
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>