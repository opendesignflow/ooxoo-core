/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * 
 * 
 * 
 * @author Rtek
 * 
 */
public class IntegerBuffer extends AbstractDecimalBuffer<java.lang.Integer> {

	/**
	 * 
	 */
	public IntegerBuffer() {
		// TODO Auto-generated constructor stub
		this(0);
	}

	public IntegerBuffer(String val) {
		this(java.lang.Integer.parseInt(val));
	}
	

	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String value) {
		this.setValue(java.lang.Integer.parseInt(value.length()==0?"0":value));
		
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected java.lang.Integer convertFromString(String value) {
		// TODO Auto-generated method stub
		return java.lang.Integer.parseInt(value);
	}

	/**
	 * Constructor with inital value
	 * 
	 * @param val
	 */
	public IntegerBuffer(int val) {
		this.setValue(val);
	}

	public String toString() {
		return java.lang.Integer.toString(value);
	}


	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return new TransferUnit() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return IntegerBuffer.this.getValue().toString();
			}
			
		};
	}

}
