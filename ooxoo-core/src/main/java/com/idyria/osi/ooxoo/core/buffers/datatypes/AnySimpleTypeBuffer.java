/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * The anySimpleType type represents a value that is choosen among build-in
 * datatypes (a string, an NCName, an integer for example)
 * 
 * just fill its value as a string and call validation methods to check its content is ok
 * 
 * @author Rtek
 * 
 */
public class AnySimpleTypeBuffer extends AbstractDataTypesBuffer<String> implements XSDType {

	
	/**
	 * 
	 */
	public AnySimpleTypeBuffer() {
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * @param value
	 */
	public AnySimpleTypeBuffer(String value) {
		super();
		this._setValueFromString(value);
	}




	public String toString() {
		return value.toString();
	}


	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return true;
	}



	@Override
	public  void _setValueFromString(String value) {
		this.setValue(value);
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected String convertFromString(String value) {
		return value;
	}


	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.ElementBuffer#doUnwrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.ElementBuffer#doWrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return super.doWrapping(tu);
	}

	
	
}
