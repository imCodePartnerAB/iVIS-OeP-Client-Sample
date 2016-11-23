<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="xml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes" encoding="ISO-8859-1"/>
		
	<xsl:template match="Document">
		
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
				<title>Ärendenummer #<xsl:value-of select="FlowInstance/flowInstanceID"/></title>
				<link rel="stylesheet" type="text/css" href="classpath://com/nordicpeak/flowengine/pdf/staticcontent/css/flowinstance.css" />
				
				<bookmarks>
					<xsl:apply-templates select="ManagerResponses/PDFManagerResponse" mode="bookmark"/>
				</bookmarks>
			</head>
			<body>
			
				<div id="footer">
					<xsl:text>Ärendenummer: #</xsl:text>
					<xsl:value-of select="FlowInstance/flowInstanceID"/>
					
					<xsl:if test="FlowInstance/poster/user">
						<xsl:text> | Inskickat av: </xsl:text>
						<xsl:value-of select="FlowInstance/poster/user/firstname"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="FlowInstance/poster/user/lastname"/>
					</xsl:if>
					
					<xsl:if test="Signed = 'true'">
						<xsl:text> (signerad)</xsl:text>
					</xsl:if>
					
					<xsl:text> | Datum: </xsl:text>
					<xsl:value-of select="SubmitDate"/>
				</div>			    
			    
				<div id="pagenumber-container">
				    Sida <span id="pagenumber"></span> av <span id="pagecount"></span>
				</div>
							
				<div class="header">
					
					<div class="logo">
					
						<xsl:choose>
							<xsl:when test="Logotype">
								<img src="{Logotype}"/>
							</xsl:when>
							<xsl:otherwise>
								<img src="classpath://com/nordicpeak/flowengine/pdf/staticcontent/pics/logo.png"/>							
							</xsl:otherwise>
						</xsl:choose>
					</div>
					
					<div class="header-text">
						<h1>
							<xsl:value-of select="FlowInstance/Flow/name"/>
						</h1>
						<p>
							<xsl:text>Ärendenummer: #</xsl:text>
							<xsl:value-of select="FlowInstance/flowInstanceID"/>
							
							<xsl:if test="FlowInstance/poster/user">
								<xsl:text> | Inskickat av: </xsl:text>
								<xsl:value-of select="FlowInstance/poster/user/firstname"/>
								<xsl:text> </xsl:text>
								<xsl:value-of select="FlowInstance/poster/user/lastname"/>
							</xsl:if>
							
							<xsl:text> | Datum: </xsl:text>
							<xsl:value-of select="SubmitDate"/>
						</p>
					</div>
					
				</div>	
				<div class="content">
		
					<xsl:apply-templates select="ManagerResponses/PDFManagerResponse" mode="xhtml"/>
		
				</div>
			</body>
		</html>
		
	</xsl:template>			
	
	<xsl:template match="PDFManagerResponse" mode="bookmark">
	
		<xsl:variable name="stepID" select="currentStepID"/>
	
		<bookmark name="{currentStepIndex + 1}. {../../FlowInstance/Flow/Steps/Step[stepID = $stepID]/name}" href="#step{currentStepIndex + 1}">
	
			<xsl:apply-templates select="QueryResponses/PDFQueryResponse" mode="bookmark"/>
	
		</bookmark>
		
	</xsl:template>	
	
	<xsl:template match="PDFQueryResponse" mode="bookmark">
	
		<bookmark name="{QueryDescriptor/name}" href="#query{QueryDescriptor/queryID}"/>
	
	</xsl:template>
	
	<xsl:template match="PDFManagerResponse" mode="xhtml">
	
		<xsl:variable name="stepID" select="currentStepID"/>
	
		<div class="step">
			
			 <a name="step{currentStepIndex + 1}"/>
		
			<h1>
				<img src="classpath://com/nordicpeak/flowengine/pdf/staticcontent/pics/check.png"/>
				<xsl:value-of select="currentStepIndex + 1"/>
				<xsl:text>. </xsl:text>
				<xsl:value-of select="../../FlowInstance/Flow/Steps/Step[stepID = $stepID]/name"/>	
			</h1>
			
			<xsl:apply-templates select="QueryResponses/PDFQueryResponse" mode="xhtml"/>			
		</div>
	
	</xsl:template>	
	
	<xsl:template match="PDFQueryResponse" mode="xhtml">
	
		<xsl:value-of select="XHTML" disable-output-escaping="yes"/>
	
	</xsl:template>	
	
</xsl:stylesheet>