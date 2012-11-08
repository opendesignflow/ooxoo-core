/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;

/**
 * FIXME
 * @author Rtek
 * 
 */
public class HexBinaryBuffer extends AbstractDataTypesBuffer<byte[]>{


	/**
	 * 
	 */
	public HexBinaryBuffer() {

	}

	/**
	 * Constructor : -> raw data
	 * 
	 * @param datas
	 */
	public HexBinaryBuffer(byte[] datas) {

		this.value = datas;

	}

	/**
	 * Marshalling method : bytes => encoded String
	 */
	public String toString() {

		String res = "Error";

		try {

			// Create result
			res = new String();

			// Convert to Hexadecimal
			StringBuffer buf = new StringBuffer();
			for (byte bt : this.value) {

				int base = bt >>> 4 & 0x0F;
				char ch;
				if (base >= 0 && base <= 9)
					ch = (char) ('0' + base);
				else
					ch = (char) ('a' + (base - 10));
				buf.append(ch);

				base = bt & 0x0F;
				if (base >= 0 && base <= 9)
					ch = (char) ('0' + base);
				else
					ch = (char) ('a' + (base - 10));
				buf.append(ch);

			}

			res = buf.toString();

		} catch (Exception ex) {
			res = "Error (" + ex.toString() + ")";
		}

		return res;

	}

	


	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return new TransferUnit() {

			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return toString();
			}
			
		};
	}

	@Override
	public void _setValueFromString(String value) {
		super.setValue(this.convertFromString(value));
		
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected byte[] convertFromString(String value) {
		// TODO Auto-generated method stub
		return value.getBytes();
	}
	

	@Override
	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return false;
	}

}
