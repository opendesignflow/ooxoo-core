package com.idyria.osi.ooxoo.core.buffers.datatypes;

import com.idyria.osi.ooxoo.core.ElementBuffer;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * 
 * @author rtek
 *
 * @param <VT>
 */
public abstract class AbstractDataTypesBuffer<VT> extends ElementBuffer<VT> implements XSDType {

	/**
	 * 
	 */
	public AbstractDataTypesBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public AbstractDataTypesBuffer(VT value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.ElementBuffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
//		TeaLogging.teaLogInfo("Creating TU in StringConstrainedType");
//		this.doWrapping(null);
		return new TransferUnit() {

			@Override
			public String getValue() {
				if (super.value == null && AbstractDataTypesBuffer.this.value != null)
					return AbstractDataTypesBuffer.this.value.toString();
				return super.value;
			}
		};
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.ElementBuffer#doUnwrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		this._setValueFromString(tu.getValue());
		
		super.doUnwrapping(tu);
		
		return tu;
	}

	



	
	
	

	
	
	
}
