/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import uni.hd.cag.utils.security.utils.RandomID;

/**
 * 
 *
 * 
 * @author Rtek
 * 
 */
public class IDBuffer extends NCNameBuffer {
	
	
	
	/**
	 * 
	 */
	public IDBuffer() {
		super();
		
	}

	/**
	 * @param id The id value
	 * @throws SyntaxException
	 */
	public IDBuffer(String id) throws SyntaxException {
		super(id);
	
	}

	/**
	 *  Generate an auto ID
	 */
	public static IDBuffer generateID() {
		
		String id = RandomID.generate();
		try {
			return new IDBuffer(id);
		} catch (SyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new IDBuffer();
		}
		
	}
	
	/**
	 *  Generate an auto ID
	 */
	public static IDBuffer generateSmallID() {
		
		String id = RandomID.generateSmall();
		try {
			return new IDBuffer(id);
		} catch (SyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new IDBuffer();
		}
		
	}

	public static IDBuffer parseID(String attribute) {
		// TODO Auto-generated method stub
		return null;
	}
	

	


	
}
