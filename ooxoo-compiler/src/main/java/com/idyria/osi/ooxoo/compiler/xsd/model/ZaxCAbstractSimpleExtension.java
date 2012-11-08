/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;

/**
 * This is a base type for extension elements -> to be extended by simpleContent |
 * complexContent nested classes.
 * It only contains attributes selections as they are common to all definitions
 * 
 * @author Rtek
 * 
 */
public abstract class ZaxCAbstractSimpleExtension extends ZaxCAbstractSelectAttributes {

	/**
	 * The base type to extend
	 */
	protected QNameBuffer base = null;


	
	/**
	 * 
	 */
	public ZaxCAbstractSimpleExtension() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the base
	 */
	public QNameBuffer getBase() {
		return base;
	}

	/**
	 * @param base
	 *            the base to set
	 */
	public void setBase(QNameBuffer base) {
		this.base = base;
	}

	

}
