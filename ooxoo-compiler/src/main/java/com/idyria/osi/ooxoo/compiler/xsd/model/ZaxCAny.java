/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;

/**
 * @author Rtek
 *
 */
public class ZaxCAny extends ZaxCWildCard {

	protected NonNegativeIntegerBuffer minOccurs = new NonNegativeIntegerBuffer(1);

	protected MaxOccursBuffer maxOccurs = new MaxOccursBuffer(1);
	
	/**
	 * 
	 */
	public ZaxCAny() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return the maxOccurs
	 */
	public MaxOccursBuffer getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @param maxOccurs the maxOccurs to set
	 */
	public void setMaxOccurs(MaxOccursBuffer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	/**
	 * @return the minOccurs
	 */
	public NonNegativeIntegerBuffer getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @param minOccurs the minOccurs to set
	 */
	public void setMinOccurs(NonNegativeIntegerBuffer minOccurs) {
		this.minOccurs = minOccurs;
	}

	
	
}
