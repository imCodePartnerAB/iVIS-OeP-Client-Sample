<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">
  <xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

  <xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/Common.xsl" />
  <xsl:include href="classpath://se/unlogic/hierarchy/core/utils/xsl/CKEditor.xsl" />

  <xsl:variable name="globalscripts">
	/jquery/jquery.js
	/tablesorter/js/jquery.tablesorter.min.js
	/tablesorter/js/init.tablesorter.js
  </xsl:variable>
  
	<xsl:variable name="globallinks">
		/tablesorter/css/tablesorter.css
	</xsl:variable>

  <xsl:template match="Document">
    <div class="contentitem">
      <xsl:apply-templates select="ListModules" />
      <xsl:apply-templates select="SettingsSaved" />
    </div>
  </xsl:template>

  <xsl:template match="SettingsSaved">

    <h1>
      <xsl:value-of select="$i18n.settingsSaved.title" />
    </h1>

    <xsl:choose>
      <xsl:when test="ModuleNotUpdated">
        <p>
          <xsl:value-of select="$i18n.settingsSaved.nutNotReloadedMessage" />
        </p>
      </xsl:when>
      <xsl:otherwise>
        <p>
          <xsl:value-of select="$i18n.settingsSaved.message" />
        </p>
      </xsl:otherwise>
    </xsl:choose>

    <p>
      <a href="{/document/requestinfo/uri}">
        <xsl:value-of select="$i18n.showSettings" />
      </a>
    </p>

  </xsl:template>

  <xsl:template match="ListModules">

    <h1>
      <xsl:value-of select="/Document/module/name" />
    </h1>

    <xsl:choose>
      <xsl:when test="ModuleNotConfigured">
        <p>
          <xsl:value-of select="$i18n.ModuleNotConfigured" />
        </p>
      </xsl:when>
      <xsl:when test="ModuleNotFound">
        <p>
          <xsl:value-of select="$i18n.ModuleNotFound" />
        </p>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if
          test="BackgroundModules/*">
          <h2>Bakgrundsmoduler</h2>
          <table class="border full">
          	<thead class="sortable">
	            <tr>
	              <th class="default-sort">
	                <xsl:value-of select="$i18n.name" />
	              </th>
	              <th width="16px" class="no-sort">
	                <xsl:value-of select="$i18n.status" />
	              </th>
	              <th width="16px" class="no-sort"></th>
	            </tr>
            </thead>
            <tbody>
	            <xsl:apply-templates select="BackgroundModules" />
            </tbody>
          </table>
        </xsl:if>
        <xsl:if
          test="ForegroundModules/*">
          <h2>Förgrundsmoduler</h2>
          <table class="border full">
          	<thead class="sortable">
	            <tr>
	              <th class="default-sort">
	                <xsl:value-of select="$i18n.name" />
	              </th>
	              <th width="16px" class="no-sort">
	                <xsl:value-of select="$i18n.status" />
	              </th>
	              <th width="16px" class="no-sort"></th>
	            </tr>
            </thead>
            <tbody>
	            <xsl:apply-templates select="ForegroundModules" />
            </tbody>
          </table>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="BackgroundModules/*">
    <tr>
      <td>
        <xsl:value-of select="name" />
      </td>
      <xsl:choose>
      	<xsl:when test="ModuleNotStarted or SectionNotStarted">
	      <td>
	      	<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/stop.png" alt="{$i18n.disabled}" title="{$i18n.disabled}" />
	  	  </td>
	      <td>
	      	<form action="{/Document/requestinfo/currentURI}/{/Document/module/alias}/startbackgroundmodule/{moduleID}" method="post">
	        	<input type="submit" value="{$i18n.enable}">
	        		<xsl:if test="SectionNotStarted"><xsl:attribute name="disabled">disabled</xsl:attribute></xsl:if>
	        	</input>
	        </form>
	      </td>
      	</xsl:when>
      	<xsl:otherwise>
	      <td>
	      	<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/check.png" alt="{$i18n.enabled}" title="{$i18n.enabled}" />
	  	  </td>
	      <td>
	      	<form action="{/Document/requestinfo/currentURI}/{/Document/module/alias}/stopbackgroundmodule/{moduleID}" method="post">
		        <input type="submit" value="{$i18n.disable}" />
	        </form>
	      </td>
      	</xsl:otherwise>
      </xsl:choose>
    </tr>
  </xsl:template>

  <xsl:template match="ForegroundModules/*">
    <tr>
      <td>
        <xsl:value-of select="name" />
      </td>
      <xsl:choose>
      	<xsl:when test="ModuleNotStarted or SectionNotStarted">
	      <td>
	      	<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/stop.png" alt="{$i18n.disabled}" title="{$i18n.disabled}" />
	  	  </td>
	      <td>
	        <form action="{/Document/requestinfo/currentURI}/{/Document/module/alias}/startforegroundmodule/{moduleID}" method="post">
	        	<input type="submit" value="{$i18n.enable}">
	        		<xsl:if test="SectionNotStarted"><xsl:attribute name="disabled">disabled</xsl:attribute></xsl:if>
	        	</input>
	        </form>
	      </td>
      	</xsl:when>
      	<xsl:otherwise>
	      <td>
	      	<img class="alignbottom" src="{/Document/requestinfo/contextpath}/static/f/{/Document/module/sectionID}/{/Document/module/moduleID}/pics/check.png" alt="{$i18n.enabled}" title="{$i18n.enabled}" />
	  	  </td>
	      <td>
	      	<form action="{/Document/requestinfo/currentURI}/{/Document/module/alias}/stopforegroundmodule/{moduleID}" method="post">
		        <input type="submit" value="{$i18n.disable}" />
	        </form>
	      </td>
      	</xsl:otherwise>
      </xsl:choose>
    </tr>
  </xsl:template>

</xsl:stylesheet>