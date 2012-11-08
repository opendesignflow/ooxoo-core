/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

/**
 * This interface is to define methods to be implemented by all types representing XSD types
 * @author Rtek
 *
 */
public interface XSDType extends java.io.Serializable{

	
	/**
	 * Used to validate the value against contrianing facets or type dependent rules
	 * @return boolean true if the validation is successfull
	 * @throws SyntaxException If the validation fails, throws an exception whose message field contains the error
	 */
	public boolean validate() throws SyntaxException ;
	
	
	public String toString();
	
	public void _setValueFromString(String value);
	
}
