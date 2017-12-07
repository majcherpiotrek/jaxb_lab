<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html"/>

<xsl:template match="/">
    <html><body>
       <xsl:apply-templates/>
    </body></html>
 </xsl:template>
  
<xsl:template match="/invoice/invoice_number">
   <h1 align="center"> <xsl:apply-templates/> </h1>
</xsl:template>

<xsl:template match="/invoice/vendor">
   <h2 align="left">Vendor</h2>
   <xsl:value-of select="name"/>&#160;<xsl:value-of select="surname"/><br/>
   <xsl:value-of select="address/street"/>&#160;<xsl:value-of select="address/house_number"/><br/>
   <xsl:value-of select="address/post_code"/>&#160;<xsl:value-of select="address/city"/><br/>
	<xsl:value-of select="address/country"/><br/><br/>
</xsl:template>
<xsl:template match="/invoice/buyer">
   <h2 align="left">Buyer</h2>
   <xsl:value-of select="name"/>&#160;<xsl:value-of select="surname"/><br/>
   <xsl:value-of select="address/street"/>&#160;<xsl:value-of select="address/house_number"/><br/>
   <xsl:value-of select="address/post_code"/>&#160;<xsl:value-of select="address/city"/><br/>
   <xsl:value-of select="address/country"/><br/>
   <br/><h1>Order:</h1>
</xsl:template>

<xsl:template match="/invoice/products">
		<strong>Product: </strong><xsl:value-of select="product_name"/><br/>
		<strong>Price: </strong><xsl:value-of select="price"/><strong> Quantity: </strong><xsl:value-of select="quantity"/>
		<br/><br/>
</xsl:template>
  	
</xsl:stylesheet>