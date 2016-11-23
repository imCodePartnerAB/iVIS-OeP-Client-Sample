<?xml version="1.0" encoding="ISO-8859-1" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" version="4.0" encoding="ISO-8859-1" />

	<xsl:include href="PageViewModuleTemplates.xsl" />
	
	<!-- Naming template.mode.field.type -->
	
	<xsl:variable name="pageAdminModule.addlinktitle" select="'Add a new page in this section'" />
	<xsl:variable name="pageAdminModule.movelinktitle" select="'Move page:'" />
	<xsl:variable name="pageAdminModule.copylinktitle" select="'Copy page:'" />	
	<xsl:variable name="pageAdminModule.updatelinktitle" select="'Edit page:'" />
	<xsl:variable name="pageAdminModule.deletepagetitle" select="'Delete page:'" />
	<xsl:variable name="pageAdminModule.deletepagepopup" select="'Delete page?'" />
	<xsl:variable name="pageAdminModule.setFirstpage" select="'Set this page as firstpage in this section'" />

</xsl:stylesheet>