/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes.constraints;

import com.idyria.osi.ooxoo.core.buffers.datatypes.AnySimpleTypeBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * @author Rtek
 *
 */
public class Inclusive extends ZaxCAnnotableType {

	
	/**
	 * If {fixed} is true, then types for which the current type is the {base
	 * type definition} cannot specify a value other than {value}.
	 */
	protected boolean fixed = false;

	/**
	 * The value
	 */
	protected AnySimpleTypeBuffer value = null;

	/**
	 * The element's Id
	 */
	protected IDBuffer id = null;
	
	/**
	 * 
	 */
	public Inclusive() {
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
	public AnySimpleTypeBuffer getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(AnySimpleTypeBuffer value) {
		this.value = value;
	}


	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
