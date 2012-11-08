<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="2.0"
	xmlns:ooxoo="com:idyria:osi:ooxoo:compiler"
	xmlns:xgen="com:idyria:osi:xgen"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	
	<xsl:output method="text" encoding="UTF-8" indent="no" media-type="text/java"></xsl:output>
	<xsl:strip-space elements="*"/>
    
	<xsl:param name="baseFolder" required="yes"></xsl:param>
	<xsl:param name="basePackage" required="yes"></xsl:param>
	
	
	<!-- XGen for java entry point -->
	<!-- ############### -->
<xsl:template match="/xgen:xgen[@target='scala']">
	

<!-- For each xclass, generate a file in the classifier folder -->
<xsl:apply-templates></xsl:apply-templates>

</xsl:template>
	
<!-- ##################################################################### -->  
<!-- Class generator -->
<!-- ##################################################################### -->  
<xsl:template match="xgen:xclass">
	
<!-- <xsl:result-document href="{$baseFolder}/{@classifier}/{@name}.java"  method="ooxoo:com.idyria.osi.ooxoo.compiler.emitter.java.JalopyReceiver" encoding="UTF-8" media-type="text/java" > -->
 <xsl:result-document href="{$baseFolder}/{@classifier}/{@name}.scala"  method="text" encoding="UTF-8" media-type="text/java" >
/**
 *
 */
package <xsl:value-of select="fn:replace($basePackage,'/','.')"/><xsl:value-of select="@classifier"/>;
<!-- Imports -->
<!-- <xsl:apply-templates select="descendant::xgen:xtype" mode="jimports"></xsl:apply-templates> -->
<xsl:call-template name="outputClass"></xsl:call-template>  
</xsl:result-document>

</xsl:template>


<xsl:template name="outputClass">
<!-- Documentation and definition -->
<xsl:apply-templates mode="documentation"></xsl:apply-templates>
class <xsl:value-of select="@name"/> <xsl:call-template name="polymorphism"></xsl:call-template> {
    
    <!-- attributes -->
    <xsl:apply-templates mode="classfields"></xsl:apply-templates>

    <!-- Nested classes -->
    <xsl:apply-templates mode="class-content"></xsl:apply-templates>

}       
</xsl:template>

<!-- Polymorphism definition for the class: determine "extends" , "with" etc.. -->
<xsl:template name="polymorphism">
<!-- Extends -->
<xsl:if test="./xgen:xtype"> extends <xsl:value-of select="./xgen:xtype"/></xsl:if>
</xsl:template>

<!-- Class definition in a class content -->
<xsl:template match="xgen:xclass" mode="class-content">
<xsl:call-template name="outputClass"></xsl:call-template>     
</xsl:template>

<!-- ##################################################################### -->	


<!-- ##################################################################### -->  	
<!-- ## Class Imports -->
<!-- ################### -->
<xsl:template match="xgen:xtype" mode="jimports">
import <xsl:value-of select="@name"/>
</xsl:template>
<!-- ##################################################################### -->  

<!-- ##################################################################### -->  
<!-- ### Class Fields -->
<!-- ##################################################################### -->  
<xsl:template match="xgen:xattribute" mode="class-content">	
	<!-- Documentation -->
    <!-- Field declaration -->
	<xsl:apply-templates mode="documentation"></xsl:apply-templates> 
	var  <xsl:value-of select="@name"/><xsl:text> : </xsl:text><xsl:call-template name="type"></xsl:call-template> = null
</xsl:template>
<!-- ##################################################################### -->  	
	
<!-- ##################################################################### -->      
<!-- ## Utility Functions -->
<!-- ##################################################################### -->      

<!-- Type -->
<xsl:template name="type">
	<xsl:param name="base" required="no" select="."></xsl:param>
	<xsl:param name="generic" required="no" select="false"></xsl:param>
	<xsl:if test="$base/xgen:xtype">
		<xsl:if test="$generic = 'true'">[</xsl:if>
		<xsl:value-of select="$base/xgen:xtype/@name"/>
		<xsl:call-template name="type">
			<xsl:with-param name="base" select="$base/xgen:xtype"></xsl:with-param>
			<xsl:with-param name="generic">true</xsl:with-param>
		</xsl:call-template>
		<xsl:if test="$generic = 'true'">]</xsl:if>
	</xsl:if>
</xsl:template>
<!-- ##################################################################### -->      
    
<!-- ##################################################################### -->      
<!-- ## Documentation -->
<!-- ##################################################################### -->     
<xsl:template match="xgen:xdocumentation" mode="documentation">
/**	
 *<xsl:value-of select="fn:string(.)"></xsl:value-of>
 */</xsl:template>
	
	
<!-- ## Garbage -->
<!-- ########### -->
<xsl:template match="*|@*" mode="#all"></xsl:template>
	
</xsl:stylesheet>