/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.common;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.TransientBuffer;
import com.idyria.osi.ooxoo.core.tu.ElementTransferUnit;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * @author rtek
 *
 */
public class ElementDefinitionBuffer extends Buffer<Object> implements TransientBuffer{

	/**
	 * 
	 */
	public ElementDefinitionBuffer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public ElementDefinitionBuffer(Object value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#doWrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// Don't wrap if already
		if (tu instanceof ElementTransferUnit)
			return tu;
		return new ElementTransferUnit(tu);
	}

	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return new ElementTransferUnit(tu);
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return new ElementTransferUnit(super.createTransferUnit());
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#doClone()
	 */
	@Override
	protected Buffer<?> doClone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return new ElementDefinitionBuffer();
	}

	@Override
	protected Object convertFromString(String value) {
		// TODO Auto-generated method stub
		return value;
	}
	
	
	
	

}
