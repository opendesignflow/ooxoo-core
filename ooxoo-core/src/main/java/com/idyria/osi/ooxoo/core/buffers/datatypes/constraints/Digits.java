/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes.constraints;

import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * @author Rtek
 *
 */
public class Digits extends ZaxCAnnotableType {

	/**
	 * The element's Id
	 */
	protected IDBuffer id = null;

	/**
	 * If {fixed} is true, then types for which the current type is the {base
	 * type definition} cannot specify a value for length other than {value}.
	 */
	protected boolean fixed = false;

	/**
	 * The length constraint
	 */
	protected int value = -1;
	
	/**
	 * 
	 */
	public Digits() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return the fixed
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * @param fixed the fixed to set
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	/**
	 * @return the id
	 */
	public IDBuffer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(IDBuffer id) {
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}


	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
		// TODO Auto-generated method stub
		
	}

	
	
}
