/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;


/**
 * 
 * 
 * @author Rtek
 * 
 */
public class NCNameBuffer extends NameBuffer {

	/**
	 * This constructor builds the NCName from a String an validates it
	 * 
	 * @throws SyntaxException
	 * @see XSDType#validate()
	 */
	public NCNameBuffer(String name) throws SyntaxException {
		super(name);
		this.validate();
	}

	/**
	 * 
	 */
	public NCNameBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}

	

}
