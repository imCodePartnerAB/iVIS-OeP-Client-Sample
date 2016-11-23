<?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes" encoding="ISO-8859-1" omit-xml-declaration="yes"/>

	<!--<xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>-->

	<xsl:template name="createIvisAlternative">

		<xsl:param name="alternativesContainerID" select="'alternativeContainer'"/>
		<xsl:param name="alternativeID"/>
		<xsl:param name="sortOrder"/>
		<!--<xsl:param name="value"/>-->
		<!--<xsl:param name="exported"/>-->

		<div id="alternativeHTML_{$alternativeID}" class="full floatleft margintop marginbottom">
			<img class="vertical-align-middle marginright cursor-move" src="{$commonImagePath}/move.png"
				 title="{$i18n.MoveAlternative}"/>

			<xsl:call-template name="createHiddenField">
				<xsl:with-param name="id" select="concat('alternativeID_', $alternativeID)"/>
				<xsl:with-param name="name" select="'alternativeID'"/>
				<xsl:with-param name="value" select="$alternativeID"/>
			</xsl:call-template>
			<xsl:call-template name="createHiddenField">
				<xsl:with-param name="id" select="concat('sortorder_', $alternativeID)"/>
				<xsl:with-param name="name" select="concat('sortorder_', $alternativeID)"/>
				<xsl:with-param name="value" select="$sortOrder"/>
				<xsl:with-param name="class" select="'sortorder'"/>
				<xsl:with-param name="requestparameters" select="//requestparameters"/>
			</xsl:call-template>
			<xsl:text>&#160;</xsl:text>
			<xsl:call-template name="createCheckbox">
				<xsl:with-param name="id" select="concat('exported_', $alternativeID)" />
				<xsl:with-param name="name" select="concat('exported_', $alternativeID)" />
				<xsl:with-param name="class" select="'vertical-align-center'" />
				<xsl:with-param name="value" select="$alternativeID" />
				<!--<xsl:with-param name="value" select="$exported" />-->
				<!--<xsl:with-param name="checked">-->
					<!--<xsl:if test="$exported">true</xsl:if>-->
				<!--</xsl:with-param>-->
				<xsl:with-param name="checked" select="exported"/>
				<!--<xsl:with-param name="elementName" select="'exported'" />-->
				<!--<xsl:with-param name="element" select="//Alternatives/CheckboxAlternative[alternativeID = $alternativeID]" />-->
				<!--<xsl:with-param name="requestparameters" select="//requestparameters"/>-->
			</xsl:call-template>
			<xsl:text>&#160;</xsl:text>
			<xsl:call-template name="createTextField">
				<xsl:with-param name="id" select="concat('xsdElementName_', $alternativeID)"/>
				<xsl:with-param name="name" select="concat('xsdElementName_', $alternativeID)"/>
				<xsl:with-param name="title" select="xsdElementName"/>
				<xsl:with-param name="value" select="xsdElementName"/>
				<xsl:with-param name="width" select="'20%'"/>
				<xsl:with-param name="requestparameters" select="//requestparameters"/>
			</xsl:call-template>
			<xsl:call-template name="createTextField">
				<xsl:with-param name="id" select="concat('alternative_', $alternativeID)"/>
				<xsl:with-param name="name" select="concat('alternative_', $alternativeID)"/>
				<xsl:with-param name="title" select="name"/>
				<xsl:with-param name="value" select="name"/>
				<xsl:with-param name="width" select="'69%'"/>
				<xsl:with-param name="requestparameters" select="//requestparameters"/>
			</xsl:call-template>

			<a href="javascript:void(0);"
			   onclick="deleteAlternative('#{$alternativesContainerID}','{$alternativeID}','{$i18n.DeleteAlternative}')"
			   title="{$i18n.DeleteAlternative}">
				<img class="vertical-align-middle marginleft" src="{$commonImagePath}/delete.png"/>
			</a>

		</div>

	</xsl:template>

	<xsl:template name="createIvisAlternativesForm">

		<xsl:param name="alternatives" />
		<xsl:param name="freeTextAlternative" />
		<xsl:param name="requestparameters" select="requestparameters" />

		<div class="full floatleft bigmarginbottom">
			<label for="alternative" class="floatleft clearboth"><xsl:value-of select="$i18n.Alternative" /></label>

			<div id="alternativeContainer" class="full floatleft marginleft sortable">
				<xsl:choose>
					<xsl:when test="$requestparameters">
						<xsl:apply-templates select="$requestparameters/parameter[starts-with(name,'alternative_')]" mode="ivisalternative" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="$alternatives" />
					</xsl:otherwise>
				</xsl:choose>

			</div>

			<div class="floatright marginright margintop">
				<a href="javascript:void(0);" onclick="addIvisAlternative('#alternativeContainer','{$commonImagePath}',$('#alternativeContainer').children().size(),'{$i18n.MoveAlternative}','{$i18n.DeleteAlternative}')" title="{$i18n.AddAlternative}">
					<xsl:value-of select="$i18n.AddAlternative"/>
				</a>
				<a href="javascript:void(0);" onclick="addOrganization('#alternativeContainer','{$commonImagePath}',$('#alternativeContainer').children().size(),'{$i18n.MoveAlternative}','{$i18n.DeleteAlternative}')" title="{$i18n.AddAlternative}">
					<img class="vertical-align-bottom marginleft" src="{$commonImagePath}/add.png"/>
				</a>
			</div>

			<label for="useFreeTextAlternative" class="floatleft clearboth"><xsl:value-of select="$i18n.FreeTextAlternative" /></label>
			<div class="clearboth floatleft marginbottom"><xsl:value-of select="$i18n.FreeTextAlternativeDescription" /></div>
			<div class="floatleft full marginbottom">
				<xsl:text>&#160;</xsl:text>
				<xsl:call-template name="createCheckbox">
					<xsl:with-param name="id" select="'useFreeTextAlternative'" />
					<xsl:with-param name="name" select="'useFreeTextAlternative'" />
					<xsl:with-param name="title" select="$i18n.FreeTextAlternative" />
					<xsl:with-param name="checked">
						<xsl:if test="$freeTextAlternative">true</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:text>&#160;</xsl:text>
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id" select="'freeTextAlternative'"/>
					<xsl:with-param name="name" select="'freeTextAlternative'"/>
					<xsl:with-param name="title" select="$i18n.FreeTextAlternative"/>
					<xsl:with-param name="value" select="name"/>
					<xsl:with-param name="width" select="'89%'"/>
					<xsl:with-param name="value" select="$freeTextAlternative"/>
					<xsl:with-param name="disabled" select="'disabled'"/>
				</xsl:call-template>
			</div>

		</div>

	</xsl:template>

	<xsl:template name="createIvisCommonFieldsForm">

		<xsl:param name="element" />

		<div class="floatleft full bigmarginbottom">
			<label for="name" class="floatleft clearboth"><xsl:value-of select="$i18n.Name" /></label>
			<div class="floatleft full">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id" select="'name'"/>
					<xsl:with-param name="name" select="'name'"/>
					<xsl:with-param name="title" select="$i18n.Name"/>
					<xsl:with-param name="element" select="$element/QueryDescriptor" />
				</xsl:call-template>
			</div>
		</div>

		<xsl:call-template name="appendCreateCommonFieldsFormConterAfterName"/>

		<div class="floatleft full bigmarginbottom">
			<label for="description" class="floatleft clearboth"><xsl:value-of select="$i18n.Description" /></label>
			<div class="floatleft full">
				<xsl:choose>
					<xsl:when test="/Document/useCKEditorForDescription = 'true'">
						<xsl:call-template name="createTextArea">
							<xsl:with-param name="id" select="'description'" />
							<xsl:with-param name="name" select="'description'" />
							<xsl:with-param name="title" select="$i18n.Description"/>
							<xsl:with-param name="class" select="'ckeditor'" />
							<xsl:with-param name="element" select="$element" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="id" select="'description'"/>
							<xsl:with-param name="name" select="'description'"/>
							<xsl:with-param name="title" select="$i18n.Description"/>
							<xsl:with-param name="element" select="$element" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>

			</div>
		</div>

		<div class="floatleft full bigmarginbottom">
			<label for="helpText" class="floatleft clearboth"><xsl:value-of select="$i18n.HelpText" /></label>
			<div class="floatleft full">
				<xsl:choose>
					<xsl:when test="/Document/useCKEditorForDescription = 'true'">
				<xsl:call-template name="createTextArea">
					<xsl:with-param name="id" select="'helpText'" />
					<xsl:with-param name="name" select="'helpText'" />
					<xsl:with-param name="class" select="'ckeditor'" />
					<xsl:with-param name="element" select="$element" />
				</xsl:call-template>
						<xsl:call-template name="initializeIvisCommonCKEditor">
							<xsl:with-param name="element" select="$element" />
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="createTextField">
							<xsl:with-param name="id" select="'helpText'"/>
							<xsl:with-param name="name" select="'helpText'"/>
							<xsl:with-param name="title" select="$i18n.HelpText"/>
							<xsl:with-param name="element" select="$element" />
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</div>

		<label class="floatleft"><xsl:value-of select="$i18n.DefaultQueryState.title"/></label>

		<div class="clearboth floatleft marginbottom"><xsl:value-of select="$i18n.DefaultQueryState.description"/></div>

		<div class="floatleft full marginbottom">

			<div class="floatleft full">
				<xsl:call-template name="createRadio">
					<xsl:with-param name="id" select="'visible'"/>
					<xsl:with-param name="name" select="'defaultQueryState'"/>
					<xsl:with-param name="value" select="'VISIBLE'"/>
					<xsl:with-param name="element" select="$element/QueryDescriptor" />
				</xsl:call-template>

				<label for="visible">
					<xsl:value-of select="$i18n.QueryState.VISIBLE" />
				</label>
			</div>
		</div>

		<div class="floatleft full marginbottom">

			<div class="floatleft full">
				<xsl:call-template name="createRadio">
					<xsl:with-param name="id" select="'visible_required'"/>
					<xsl:with-param name="name" select="'defaultQueryState'"/>
					<xsl:with-param name="value" select="'VISIBLE_REQUIRED'"/>
					<xsl:with-param name="element" select="$element/QueryDescriptor" />
				</xsl:call-template>

				<label for="visible_required">
					<xsl:value-of select="$i18n.QueryState.VISIBLE_REQUIRED" />
				</label>
			</div>
		</div>

		<div class="floatleft full marginbottom">

			<div class="floatleft full">
				<xsl:call-template name="createRadio">
					<xsl:with-param name="id" select="'hidden'"/>
					<xsl:with-param name="name" select="'defaultQueryState'"/>
					<xsl:with-param name="value" select="'HIDDEN'"/>
					<xsl:with-param name="element" select="$element/QueryDescriptor" />
				</xsl:call-template>

				<label for="hidden">
					<xsl:value-of select="$i18n.QueryState.HIDDEN" />
				</label>
			</div>
		</div>

		<label class="floatleft"><xsl:value-of select="$i18n.exportQuery.title"/></label>

		<div class="clearboth floatleft marginbottom"><xsl:value-of select="$i18n.exportQuery.description"/></div>

		<div class="floatleft full marginbottom">

			<div class="floatleft full">
				<xsl:call-template name="createCheckbox">
					<xsl:with-param name="id" select="'export'"/>
					<xsl:with-param name="name" select="'exported'"/>
					<xsl:with-param name="value" select="'true'"/>
					<xsl:with-param name="element" select="$element/QueryDescriptor" />
				</xsl:call-template>

				<label for="export">
					<xsl:value-of select="$i18n.exportQuery" />
				</label>
			</div>
		</div>

		<div class="floatleft full bigmarginbottom">
			<label for="xsdElementName" class="floatleft clearboth"><xsl:value-of select="$i18n.xsdElementName" /></label>
			<div class="floatleft full">
				<xsl:call-template name="createTextField">
					<xsl:with-param name="id" select="'xsdElementName'"/>
					<xsl:with-param name="name" select="'xsdElementName'"/>
					<xsl:with-param name="title" select="$i18n.xsdElementName"/>
					<xsl:with-param name="element" select="$element/QueryDescriptor" />
				</xsl:call-template>
			</div>
		</div>

	</xsl:template>

	<xsl:template match="parameter" mode="ivisalternative">

		<xsl:variable name="id" select="substring(name,13,47)" />

		<xsl:call-template name="createIvisAlternative">
			<xsl:with-param name="alternativeID" select="$id" />
			<xsl:with-param name="sortOrder" select="../parameter[name = concat('sortorder_',$id)]/value" />
			<!--<xsl:with-param name="value" select="value" />-->
		</xsl:call-template>

	</xsl:template>

	<xsl:template name="initializeIvisCommonCKEditor">

		<xsl:param name="element" />
		<xsl:param name="flowTypeID" select="$element/../flowTypeID" />

		<script type="text/javascript">

			var fckSettings = new Object();
			var basePath = '<xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/ckeditor/';
			fckSettings.editorContainerClass = 'ckeditor';
			fckSettings.editorHeight = 150;

			<xsl:if test="/Document/ckConnectorModuleAlias">
				fckSettings.filebrowserBrowseUri = basePath + 'filemanager/index.html?Connector=<xsl:value-of select="/Document/requestinfo/contextpath"/><xsl:value-of select="/Document/ckConnectorModuleAlias" />/connector/<xsl:value-of select="$flowTypeID" />';
				fckSettings.filebrowserImageBrowseUri = basePath + 'filemanager/index.html?Connector=<xsl:value-of select="/Document/requestinfo/contextpath"/><xsl:value-of select="/Document/ckConnectorModuleAlias" />/connector/<xsl:value-of select="$flowTypeID" />';
			</xsl:if>

			<xsl:if test="/Document/cssPath">
				fckSettings.contentsCss = '<xsl:value-of select="/Document/requestinfo/contextpath"/><xsl:value-of select="/Document/cssPath"/>';
			</xsl:if>

			fckSettings.customConfig = '<xsl:value-of select="/Document/requestinfo/contextpath"/>/static/f/<xsl:value-of select="/Document/module/sectionID"/>/<xsl:value-of select="/Document/module/moduleID"/>/common/ckeditor/config.js';

		</script>

	</xsl:template>


</xsl:stylesheet>
