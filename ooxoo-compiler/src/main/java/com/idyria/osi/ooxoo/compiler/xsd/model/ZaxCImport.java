/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * <B>Descibes an import instruction in schema :</B><BR>
 * <BR>
 * 
 * 
 * 
 * @see {@link http://www.w3.org/TR/xmlschema-1/#element-import XMLSchema standart description}
 * @author Rtek
 * 
 */
public class ZaxCImport extends ZaxCAnnotableType {

	/**
	 * The namespace this import refers to
	 */
	protected AnyURIBuffer namespace = null;

	/**
	 * The URI where we can find the schema
	 */
	protected AnyURIBuffer schemaLocation = null;

	/**
	 * 
	 */
	public ZaxCImport() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.znw.xml.zaxb.engines.compiler.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema)
			throws SyntaxException {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the namespace
	 */
	public AnyURIBuffer getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(AnyURIBuffer namespace) {
		this.namespace = namespace;
	}

	/**
	 * @return the schemaLocation
	 */
	public AnyURIBuffer getSchemaLocation() {
		return schemaLocation;
	}

	/**
	 * @param schemaLocation
	 *            the schemaLocation to set
	 */
	public void setSchemaLocation(AnyURIBuffer schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

}
