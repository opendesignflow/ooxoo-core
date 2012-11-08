/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * @author Rtek
 *
 */
public class DecimalBuffer extends AbstractDecimalBuffer<FloatBuffer> {

	/**
	 * 
	 */
	public DecimalBuffer() {
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void _setValueFromString(String value) {
		super.setValue(this.convertFromString(value));
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected FloatBuffer convertFromString(String value) {
		FloatBuffer fl = new FloatBuffer();
		fl._setValueFromString(value);
		return fl;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getValue().toString();
	}



	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return new TransferUnit() {
			
			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return toString();
			}
		};
	}

	
	

	

}
