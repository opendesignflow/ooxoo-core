/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * 
 * 
 * @author Rtek
 * 
 */
public class ZaxCInclude extends ZaxCAnnotableType {

	/**
	 * The URI where we can find the schema
	 */
	protected AnyURIBuffer schemaLocation = null;

	/**
	 * 
	 */
	public ZaxCInclude() {
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




	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema)
			throws SyntaxException {
		// TODO Auto-generated method stub

	}

	
	
	
}
