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
		
		<div id="TextAreaQueryProvider" class="contentitem">
		
			<xsl:apply-templates select="validationError"/>
			<xsl:apply-templates select="UpdateTextAreaQuery"/>
		
		</div>
		
	</xsl:template>
		
	<xsl:template match="UpdateTextAreaQuery">
	
		<h1><xsl:value-of select="$i18n.UpdateQuery" /><xsl:text>:&#160;</xsl:text><xsl:value-of select="TextAreaQuery/QueryDescriptor/name" /></h1>
		
		<xsl:apply-templates select="validationException/validationError" />
		
		<form id="updateTextAreaQueryForm" name="queryAdminForm" method="post" action="{/Document/requestinfo/uri}">
		
			<xsl:call-template name="createCommonFieldsForm">
				<xsl:with-param name="element" select="TextAreaQuery" />
			</xsl:call-template>
			
			<div class="floatleft full bigmarginbottom">
				<label for="maxLength" class="floatleft clearboth"><xsl:value-of select="$i18n.MaxLength" /></label>
				<div class="floatleft full">
					<xsl:call-template name="createTextField">
						<xsl:with-param name="id" select="'maxLength'"/>
						<xsl:with-param name="name" select="'maxLength'"/>
						<xsl:with-param name="title" select="$i18n.MaxLength"/>
						<xsl:with-param name="size" select="'30'"/>
						<xsl:with-param name="element" select="TextAreaQuery" />
					</xsl:call-template>
			    </div>
			</div>

			<div class="floatleft full marginbottom">

				<div class="floatleft full">
					<xsl:call-template name="createCheckbox">
						<xsl:with-param name="id" select="'dependsOn'"/>
						<xsl:with-param name="name" select="'dependsOn'"/>
						<xsl:with-param name="value" select="'true'"/>
						<xsl:with-param name="element" select="TextAreaQuery" />
					</xsl:call-template>

					<label for="dependsOn">
						<xsl:value-of select="$i18n.DependsOn" />
					</label>
				</div>
			</div>

			<div class="floatleft full bigmarginbottom">
				<label for="dependencySourceName" class="floatleft clearboth"><xsl:value-of select="$i18n.DependencySourceName" /></label>
				<div class="floatleft full">
					<xsl:call-template name="createTextField">
						<xsl:with-param name="id" select="'dependencySourceName'"/>
						<xsl:with-param name="name" select="'dependencySourceName'"/>
						<xsl:with-param name="title" select="$i18n.DependencySourceName"/>
						<xsl:with-param name="element" select="TextAreaQuery" />
					</xsl:call-template>
				</div>
			</div>

			<div class="floatleft full bigmarginbottom">
				<label for="dependencyFieldName" class="floatleft clearboth"><xsl:value-of select="$i18n.DependencyFieldName" /></label>
				<div class="floatleft full">
					<xsl:call-template name="createTextField">
						<xsl:with-param name="id" select="'dependencyFieldName'"/>
						<xsl:with-param name="name" select="'dependencyFieldName'"/>
						<xsl:with-param name="title" select="$i18n.DependencyFieldName"/>
						<xsl:with-param name="element" select="TextAreaQuery" />
					</xsl:call-template>
				</div>
			</div>

			<div class="floatright margintop clearboth">
				<input type="submit" value="{$i18n.SaveChanges}" />
			</div>
		
		</form>
		
	</xsl:template>



	<xsl:template match="validationError[messageKey = 'MaxLengthToBig']">
		
		<p class="error">
			<xsl:value-of select="$i18n.MaxLengthToBig" />
		</p>
		
	</xsl:template>

	<xsl:template match="validationError[messageKey = 'UpdateFailedTextAreaQueryNotFound']">
		
		<p class="error">
			<xsl:value-of select="$i18n.TextAreaQueryNotFound" />
		</p>
		
	</xsl:template>

	<xsl:template match="fieldName">

		<xsl:variable name="fieldName" select="." />
	
		<xsl:choose>
			<xsl:when test="$fieldName = 'maxLength'">
				<xsl:value-of select="$i18n.maxLength" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$fieldName" />
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>