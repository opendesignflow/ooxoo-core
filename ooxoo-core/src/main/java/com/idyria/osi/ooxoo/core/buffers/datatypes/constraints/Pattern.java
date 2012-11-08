/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes.constraints;

import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * Regexp Pattern constraining facet
 * 
 *
 * 
 * @author Rtek
 * 
 */
public class Pattern extends ZaxCAnnotableType {

	/**
	 * The regexp pattern value
	 */
	protected java.util.regex.Pattern value = null;

	/**
	 * 
	 */
	public Pattern() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the value
	 */
	public java.util.regex.Pattern getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(java.util.regex.Pattern value) {
		this.value = value;
	}

	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
		// TODO Auto-generated method stub
		
	}

}
