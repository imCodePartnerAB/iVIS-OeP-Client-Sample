<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" version="4.0" encoding="ISO-8859-1"/>

    <xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl"/>
    <xsl:include href="classpath://com/imcode/oeplatform/flowengine/queries/resources/xsl/IvisTemplates.xsl"/>

    <xsl:variable name="globalscripts">
/jquery/jquery.js
/jquery/jquery-ui.js
/ckeditor/ckeditor.js
/ckeditor/adapters/jquery.js
/ckeditor/init.js
    </xsl:variable>

    <xsl:variable name="scripts">
/resources/js/queryadmin.js
/common/js/queryadmin.js
    </xsl:variable>

    <xsl:template match="Document">

        <div id="CheckboxQueryProvider" class="contentitem">

            <xsl:apply-templates select="validationError"/>
            <xsl:apply-templates select="UpdateCheckboxQuery"/>

        </div>

    </xsl:template>

    <xsl:template match="UpdateCheckboxQuery">

        <h1>
            <xsl:value-of select="$i18n.UpdateQuery"/><xsl:text>:&#160;</xsl:text><xsl:value-of
                select="CheckboxQuery/QueryDescriptor/name"/>
        </h1>

        <xsl:apply-templates select="validationException/validationError"/>

        <form id="updateCheckboxQueryForm" name="queryAdminForm" method="post" action="{/Document/requestinfo/uri}">

            <!--<xsl:call-template name="createCommonFieldsForm">-->
            <xsl:call-template name="createIvisCommonFieldsForm">
                <xsl:with-param name="element" select="CheckboxQuery"/>
            </xsl:call-template>

            <xsl:call-template name="createIvisAlternativesForm">
                <xsl:with-param name="alternatives" select="CheckboxQuery/Alternatives/CheckboxAlternative"/>
                <xsl:with-param name="freeTextAlternative" select="CheckboxQuery/freeTextAlternative"/>
            </xsl:call-template>

            <div class="floatleft full bigmarginbottom">
                <label for="minChecked" class="floatleft">
                    <xsl:value-of select="$i18n.MinChecked"/>
                </label>
                <div class="floatleft full">
                    <xsl:call-template name="createTextField">
                        <xsl:with-param name="id" select="'minChecked'"/>
                        <xsl:with-param name="name" select="'minChecked'"/>
                        <xsl:with-param name="title" select="$i18n.MinChecked"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="element" select="CheckboxQuery"/>
                    </xsl:call-template>
                </div>
            </div>

            <div class="floatleft full bigmarginbottom">
                <label for="maxChecked" class="floatleft">
                    <xsl:value-of select="$i18n.MaxChecked"/>
                </label>
                <div class="floatleft full">
                    <xsl:call-template name="createTextField">
                        <xsl:with-param name="id" select="'maxChecked'"/>
                        <xsl:with-param name="name" select="'maxChecked'"/>
                        <xsl:with-param name="title" select="$i18n.MaxChecked"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="element" select="CheckboxQuery"/>
                    </xsl:call-template>
                </div>
            </div>

            <div class="floatright margintop clearboth">
                <input type="submit" value="{$i18n.SaveChanges}"/>
            </div>

        </form>

    </xsl:template>

    <xsl:template match="CheckboxAlternative">

        <!--<xsl:call-template name="createAlternative">-->
        <xsl:call-template name="createIvisAlternative">
            <xsl:with-param name="alternativeID" select="alternativeID"/>
            <xsl:with-param name="sortOrder" select="sortIndex"/>
            <!--<xsl:with-param name="value" select="name"/>-->
            <!--<xsl:with-param name="exported" select="exported"/>-->
        </xsl:call-template>

    </xsl:template>

    <xsl:template match="validationError[messageKey = 'MinCheckedBiggerThanMaxChecked']">

        <p class="error">
            <xsl:value-of select="$i18n.MinCheckedBiggerThanMaxChecked"/>
        </p>

    </xsl:template>

    <xsl:template match="validationError[messageKey = 'MaxCheckedToBig']">

        <p class="error">
            <xsl:value-of select="$i18n.MaxCheckedToBig"/>
        </p>

    </xsl:template>

    <xsl:template match="validationError[messageKey = 'MinCheckedToBig']">

        <p class="error">
            <xsl:value-of select="$i18n.MinCheckedToBig"/>
        </p>

    </xsl:template>

    <xsl:template match="validationError[messageKey = 'UpdateFailedCheckboxQueryNotFound']">

        <p class="error">
            <xsl:value-of select="$i18n.CheckboxQueryNotFound"/>
        </p>

    </xsl:template>

    <xsl:template match="validationError[messageKey = 'ToFewAlternatives1Min']">

        <p class="error">
            <xsl:value-of select="$i18n.ToFewAlternatives1Min"/>
        </p>

    </xsl:template>

    <xsl:template match="fieldName">

        <xsl:variable name="fieldName" select="."/>

        <xsl:choose>
            <xsl:when test="$fieldName = 'minChecked'">
                <xsl:value-of select="$i18n.minChecked"/>
            </xsl:when>
            <xsl:when test="$fieldName = 'maxChecked'">
                <xsl:value-of select="$i18n.maxChecked"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$fieldName"/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

</xsl:stylesheet>