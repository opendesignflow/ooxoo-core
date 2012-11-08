/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;


/**
 * 
 * 
 * 
 * @author Rtek
 * 
 */
public class NMToken extends TokenBuffer {

	

	/**
	 * 
	 */
	public NMToken() {
	}


	/**
	 * @param str
	 * @throws SyntaxException
	 */
	public NMToken(String str) throws SyntaxException {
	
		super(str);
	}

	
	public static NMToken fromStringNoValidation(String value) {

		NMToken res = null;

		// Check null value
		if (value == null || value.length()==0)
			return null;

		try {
			res = new NMToken(value);
		} catch (SyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return res;

	}
	
	
}
