/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.common;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.TransientBuffer;
import com.idyria.osi.ooxoo.core.tu.AttributeTransferUnit;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * @author rtek
 *
 */

public class AttributeDefinitionBuffer extends Buffer<Object> implements TransientBuffer {

	/**
	 * 
	 */
	public AttributeDefinitionBuffer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public AttributeDefinitionBuffer(Object value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	
	
	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		return new AttributeTransferUnit(super.createTransferUnit());
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#doWrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return new AttributeTransferUnit(tu);
	}

	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return new AttributeTransferUnit(tu);
	}

	@Override
	protected Object convertFromString(String value) {
		// TODO Auto-generated method stub
		return value;
	}

}
