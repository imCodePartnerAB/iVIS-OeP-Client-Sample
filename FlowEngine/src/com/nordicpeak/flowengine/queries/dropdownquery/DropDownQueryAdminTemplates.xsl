<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

	<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>

	<xsl:variable name="globalscripts">
		/jquery/jquery.js
		/jquery/jquery-ui.js
		/ckeditor/ckeditor.js
		/ckeditor/adapters/jquery.js
		/ckeditor/init.js
	</xsl:variable>	

	<xsl:variable name="scripts">
		/common/js/queryadmin.js
	</xsl:variable>

	<xsl:template match="Document">	
		
		<div id="DropDownQueryProvider" class="contentitem">
		
			<xsl:apply-templates select="validationError"/>
			<xsl:apply-templates select="UpdateDropDownQuery"/>
		
		</div>
		
	</xsl:template>
		
	<xsl:template match="UpdateDropDownQuery">
	
		<h1><xsl:value-of select="$i18n.UpdateQuery" /><xsl:text>:&#160;</xsl:text><xsl:value-of select="DropDownQuery/QueryDescriptor/name" /></h1>
		
		<xsl:apply-templates select="validationException/validationError" />
		
		<form id="updateDropDownQueryForm" name="queryAdminForm" method="post" action="{/Document/requestinfo/uri}">
					
			<xsl:call-template name="createCommonFieldsForm">
				<xsl:with-param name="element" select="DropDownQuery" />
			</xsl:call-template>
			
			<xsl:call-template name="createAlternativesForm">
				<xsl:with-param name="alternatives" select="DropDownQuery/Alternatives/DropDownAlternative" />
				<xsl:with-param name="freeTextAlternative" select="DropDownQuery/freeTextAlternative" />
			</xsl:call-template>
			
			<div class="floatright margintop clearboth">
				<input type="submit" value="{$i18n.SaveChanges}" />
			</div>
		
		</form>
		
	</xsl:template>

	<xsl:template name="appendCreateCommonFieldsFormConterAfterName">
	
		<div class="floatleft full bigmarginbottom">
			<label for="shortDescription" class="floatleft clearboth"><xsl:value-of select="$i18n.ShortDescription" /></label>
			<div class="floatleft full">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id" select="'shortDescription'"/>
					<xsl:with-param name="name" select="'shortDescription'"/>
					<xsl:with-param name="title" select="$i18n.ShortDescription"/>
					<xsl:with-param name="element" select="DropDownQuery" />
				</xsl:call-template>
		    </div>
		</div>	
	
	</xsl:template>

	<xsl:template match="DropDownAlternative">
		
		<xsl:call-template name="createAlternative">
			<xsl:with-param name="alternativeID" select="alternativeID" />
			<xsl:with-param name="sortOrder" select="sortIndex" />
			<xsl:with-param name="value" select="name" />
		</xsl:call-template>
			
	</xsl:template>

	<xsl:template match="validationError[messageKey = 'UpdateFailedDropDownQueryNotFound']">
		
		<p class="error">
			<xsl:value-of select="$i18n.DropDownQueryNotFound" />
		</p>
		
	</xsl:template>

	<xsl:template match="fieldName">
	
		<xsl:variable name="fieldName" select="." />
	
		<xsl:choose>
			<xsl:when test="$fieldName = 'shortDescription'">
				<xsl:value-of select="$i18n.shortDescription" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$fieldName" />
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>