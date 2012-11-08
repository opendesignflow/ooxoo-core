/**
 * 
 */
package com.idyria.osi.ooxoo.core;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * @author rtek
 *
 */
public class AttributeBuffer extends Buffer<Object> {

	/**
	 * 
	 */
	public AttributeBuffer() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TransferUnit doWrapping(final TransferUnit tu) {
		// Prepare a TransferUnit
		
		return new TransferUnit() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return "test:value="+tu.getValue()+"";
			}
			
		};
	}

	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		return tu;
	}

	@Override
	protected Object convertFromString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
