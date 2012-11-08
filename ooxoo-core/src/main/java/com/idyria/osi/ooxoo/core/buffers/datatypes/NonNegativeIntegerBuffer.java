/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

/**
 * 
 * 
 * @author Rtek
 * 
 */
public class NonNegativeIntegerBuffer extends IntegerBuffer {

	/**
	 * 
	 */
	public NonNegativeIntegerBuffer() {
		super();
	}

	
	/**
	 * @param val
	 */
	public NonNegativeIntegerBuffer(int val) {
		super(val);
	}
	
	public NonNegativeIntegerBuffer(java.lang.Integer val) {
		this(val.intValue());
	}
	
	
	public static NonNegativeIntegerBuffer fromStringNoValidation(String val) {
		if (val==null)
			return null;
		
		return new NonNegativeIntegerBuffer(java.lang.Integer.parseInt(val));
		
			
	}
	
}
