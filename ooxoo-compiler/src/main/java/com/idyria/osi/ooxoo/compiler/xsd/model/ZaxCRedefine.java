/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;

/**
 * <B>Describes a redefine block : </B><BR>
 * 
 * @see {@link http://www.w3.org/TR/xmlschema-1/#element-redefine XMLSchema standart description}
 * @author Leys Richard
 * 
 */
public class ZaxCRedefine extends ZaxCMultiplyAnnotable {

	/**
	 * The URI where we can find the schema
	 */
	protected AnyURIBuffer schemaLocation = null;
	
	/**
	 * 
	 */
	public ZaxCRedefine() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return the schemaLocation
	 */
	public AnyURIBuffer getSchemaLocation() {
		return schemaLocation;
	}

	/**
	 * @param schemaLocation the schemaLocation to set
	 */
	public void setSchemaLocation(AnyURIBuffer schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	
	
	
}
