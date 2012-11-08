/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;


/**
 * 
 * 
 * 
 * @author Rtek
 * 
 */
public class LongBuffer extends AbstractDecimalBuffer<java.lang.Long> {

	/**
	 * 
	 */
	public LongBuffer() {
		// TODO Auto-generated constructor stub
		this(0);
	}

	/**
	 * Constructor with inital value
	 * 
	 * @param val
	 */
	public LongBuffer(long val) {
		this.setValue(val);
	}

	
	

	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String value) {
		this.setValue(this.convertFromString(value));
		
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected java.lang.Long convertFromString(String value) {
		// TODO Auto-generated method stub
		return java.lang.Long.parseLong(value);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.value.toString();
	}


}
