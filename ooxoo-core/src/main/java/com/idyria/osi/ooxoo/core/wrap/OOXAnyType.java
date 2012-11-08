/**
 * 
 */
package com.idyria.osi.ooxoo.core.wrap;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.ElementBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxany;

/**
 * This class is used to handle anyType elements
 * @author Richnou
 *
 */
public class OOXAnyType extends Buffer<ElementBuffer> {

	@Ooxany
	private com.idyria.osi.ooxoo.core.wrap.OOXAny any = null;
	
	/**
	 * 
	 */
	public OOXAnyType() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 *
	 * @return value for the field : anyAttribute
	 */
	public com.idyria.osi.ooxoo.core.wrap.OOXAny getAny(boolean create) {

		// Return element
		//--------------------
		if (create)
			any= new OOXAny() {
				{
					super.namespaces.put("##other",new MaxOccursBuffer(true));
				}
			};
		return any;
	}

	/**
	 *
	 * @return value for the field : anyAttribute
	 */
	public com.idyria.osi.ooxoo.core.wrap.OOXAny getAny() {
		return getAny(false);
	}


	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected ElementBuffer convertFromString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
