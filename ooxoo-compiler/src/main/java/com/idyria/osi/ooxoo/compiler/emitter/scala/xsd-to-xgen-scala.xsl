<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="2.0"
	xmlns="com:idyria:osi:xgen"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:ooxoo="com:idyria:osi:ooxoo"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema">
	<xsl:strip-space elements="*"/>
	
	<xsl:output method="xml" encoding="UTF-8" indent="no"></xsl:output>
	
    
	<!-- ### Schema Entry point -->
	<!-- ################################## -->
	<xsl:template match="//xsd:schema">
		<!-- ## Generate an XGen top -->
		<xgen target="scala">
			<xsl:apply-templates mode="schema"></xsl:apply-templates>
		</xgen>
	</xsl:template>
	
	<!-- ################################## -->
	<!-- #### TOP Elements -->
	<!-- #### Element == Class -->
	<xsl:template match="//xsd:schema/xsd:element" mode="schema">
		<xclass name="{@name}" classifier="elements" modifiers="public">	
        	
			<!-- Determine type -->
            <xsl:choose>
                <xsl:when test="@type">
                    <xtype><xsl:value-of select="@type"></xsl:value-of></xtype>
                </xsl:when>
                <xsl:otherwise>
                    <xtype><xsl:value-of select="ooxoo:resolveType('element')"/></xtype>
                </xsl:otherwise>
            </xsl:choose>
			<xsl:if test="@type">
				
			</xsl:if>
			<xsl:apply-templates mode="#default"></xsl:apply-templates>
		</xclass>
	</xsl:template> 
	<!-- ################################## -->
    
    
    <!-- ################################## -->
	<!-- #### ComplexType -->
	<!-- #### ComplexType == Class -->
	<xsl:template match="//xsd:schema/xsd:complexType" mode="schema">
		<xclass name="{@name}" classifier="ctypes"  modifiers="public">
			<xsl:apply-templates mode="#default"></xsl:apply-templates>	
		</xclass>
	</xsl:template>
	<!-- ################################## -->
    
    
	<!-- #### Elements -->
	<!-- ################################## -->
	
	<!-- #### Switch to complex Type -->
<!-- 	<xsl:template match="xsd:element/xsd:complexType" mode="element"> -->
<!--  		<xsl:apply-templates mode="complexType"></xsl:apply-templates> -->
<!--  	</xsl:template> -->
	
	<!-- #### Fetch Element in sequence  -->
	<xsl:template match="xsd:element[@type or @ref]">
		<!-- Add an attribute to target class -->
		<xattribute name="{@name}">
		
			<!-- Use name or ref attribute -->
			<xsl:choose>
				<xsl:when test="@name">
					<xsl:attribute name="name"><xsl:value-of select="@name"></xsl:value-of></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="name"><xsl:value-of select="fn:substring-after(@ref,':')"></xsl:value-of></xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
			<!-- Annotations for OOXOO -->
			<xannotation name="Ooxnode"></xannotation>
			<xannotation name="Ooxelement"></xannotation>
			
			<!-- Types -->
			<xsl:choose>
				<!-- List -->
				<xsl:when test="fn:compare(@maxOccurs,'unbounded')=0 or fn:number(@maxOccurs) > 1">
					<xtype name="com.idyria.osi.ooxoo3.core.buffers.structural.XList">
						<xtype name="{ooxoo:resolveType(@type|@ref)}"></xtype>
					</xtype>
				</xsl:when>
				<!-- Single type -->
				<xsl:otherwise>
					<xtype name="{ooxoo:resolveType(@type|@ref)}"></xtype>
				</xsl:otherwise>
			</xsl:choose>
			
			<!-- Documentation -->
			<xsl:apply-templates mode="element"></xsl:apply-templates>
		</xattribute>
	</xsl:template>

	<!-- #### Anonymous types from sequence -->
	<!-- #### Some elements with no type are to be in nested class -->
	<xsl:template match="xsd:element">
		<xclass name="{@name}" classifier="elements" modifiers="public">	
			<xsl:apply-templates  mode="element"></xsl:apply-templates>
		</xclass>
	</xsl:template>


	<!-- #### Complex Types -->
	<!-- ############################ -->
	
	<!-- Resolve extension and restriction -->
	<xsl:template match="xsd:extension|xsd:restriction">	
		<!-- Add Type extension -->
		<xextends>
			<xtype name="{@base}"></xtype>
		</xextends>
	</xsl:template>
	
	<!-- #### Annotations for all modes  -->
	<!-- ##################### -->
	<xsl:template match="xsd:annotation/xsd:documentation" mode="#all">
		<xdocumentation>
			<xsl:copy-of select="./*|text()"></xsl:copy-of>
		</xdocumentation>
	</xsl:template>
	
	<!-- Garbage -->
	<!-- ############ -->
	<xsl:template match="*|@*" mode="schema">
	</xsl:template>
<!-- 	<xsl:template match="*|@*"> -->
<!-- 	</xsl:template> -->
</xsl:stylesheet>