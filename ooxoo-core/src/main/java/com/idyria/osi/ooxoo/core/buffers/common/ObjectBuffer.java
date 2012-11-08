/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.common;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * A buffer doing nothing, just to be used in a generated class when a type class is not found
 * @author rtek
 *
 */
public class ObjectBuffer extends Buffer<Object> {

	/**
	 * 
	 */
	public ObjectBuffer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public ObjectBuffer(Object value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#doUnwrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#doWrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object convertFromString(String value) {
		// TODO Auto-generated method stub
		return value;
	}

}
