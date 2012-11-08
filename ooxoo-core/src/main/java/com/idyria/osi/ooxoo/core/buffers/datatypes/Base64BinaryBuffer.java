/**
 * This class is the representation of te base type base64Binary
 * It holds a byte[] taht contains the binary value of the data, non converted.
 * 
 * It's role it to take an input of byte[] raw binary datas and convert it to base64
 * at the moment XML is marshalled.
 * 
 * On the countrary, it takes an input of String base64-encoded  datas and decodes it
 * to retrieve the raw byte[] datas
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;

import uni.hd.cag.utils.security.utils.Base64;

/**
 * @author Rtek
 * 
 */
public class Base64BinaryBuffer extends AbstractDataTypesBuffer<byte[]> implements XSDType {


	public Base64BinaryBuffer() {

	}

	/**
	 * Constructor : -> raw data
	 * 
	 * @param datas
	 */
	public Base64BinaryBuffer(byte[] datas) {

		this.value = datas;

	}

	public Base64BinaryBuffer(String encoded) {

		this._setValueFromString(encoded);
	}
	
	

	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String encoded) {
		this.setValue(this.convertFromString(encoded));
		
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected byte[] convertFromString(String date) {
		return Base64.decode(date);
	}
	

	/**
	 * Marshalling method : bytes => encoded String
	 */
	public String toString() {

		String res = "Error";
		res = Base64.encodeBytes(this.value);
		return res;

	}

	/**
	 * Extract the real bytes from encoded base64
	 */
	public static Base64BinaryBuffer fromString(String encoded) {

		Base64BinaryBuffer res = new Base64BinaryBuffer(encoded);
		return res;

	}

	public static Base64BinaryBuffer fromStringNoValidation(String encoded) {

		return Base64BinaryBuffer.fromString(encoded);

	}

	

	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return new TransferUnit() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return Base64BinaryBuffer.this.toString();
			}
			
		};
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.ElementBuffer#doUnwrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		this._setValueFromString(tu.getValue());
		return tu;
	}
	
	

}
