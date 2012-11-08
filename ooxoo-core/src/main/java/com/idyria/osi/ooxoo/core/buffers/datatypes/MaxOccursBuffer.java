/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

/**
 * @author Rtek
 *
 */
public class MaxOccursBuffer extends NonNegativeIntegerBuffer {

	private boolean unbounded = false;
	/**
	 * 
	 */
	public MaxOccursBuffer() {
		super();
	}

	/**
	 * @param val
	 */
	public MaxOccursBuffer(int val) {
		super(val);
		
	}

	/**
	 * @param unbounded
	 */
	public MaxOccursBuffer(boolean unbounded) {
		super();
		this.unbounded = unbounded;
	}

	public void setUnbounded() {
		this.unbounded = true;
	}
	
	public boolean isUnbounded() {
		return this.unbounded;
	}
	
	
	public static MaxOccursBuffer fromStringNoValidation(String val) {
		if (val==null)
			return null;
		if (val.equals("unbounded"))
			return new MaxOccursBuffer(true);
		else
			return new MaxOccursBuffer(java.lang.Integer.parseInt(val));
		
			
	}

	
	/**
	 * @see com.idyria.tools.xml.oox.datatypes.impl.Integer#toString()
	 */
	@Override
	public String toString() {
		if (this.isUnbounded())
			return "unbounded";
		else
			return super.toString();
	}
	
	
	
	
}
