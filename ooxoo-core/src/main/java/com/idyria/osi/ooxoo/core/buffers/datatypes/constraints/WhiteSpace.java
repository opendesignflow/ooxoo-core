/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes.constraints;

import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * Type for WhiteSpace constraining facet
 * 
 * 
 * @author Rtek
 * 
 */
public class WhiteSpace extends ZaxCAnnotableType {

	/**
	 * The element's Id
	 */
	protected IDBuffer id = null;

	/**
	 * If {fixed} is true, then types for which the current type is the {base
	 * type definition} cannot specify a value for whitespace other than
	 * {value}.
	 */
	protected boolean fixed = false;

	/**
	 * The constraint value :
	 * <code><i>(collapse | preserve | replace)</i></code>
	 */
	protected String value = null;

	/**
	 * 
	 */
	public WhiteSpace() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the fixed
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * @param fixed
	 *            the fixed to set
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
	 * @param id
	 *            the id to set
	 */
	public void setId(IDBuffer id) {
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * <dt class="label">preserve</dt>
	 * <dd> No normalization is done, the value is not changed (this is the
	 * behavior required by <a href="#XML">[XML 1.0 (Second Edition)]</a> for
	 * element content)
	 * 
	 */
	public void setPreserve() {
		this.value = "preserve";
	}

	/**
	 * <dt class="label">collapse</dt>
	 * <dd> After the processing implied by <b>replace</b>, contiguous
	 * sequences of #x20's are collapsed to a single #x20, and leading and
	 * trailing #x20's are removed.
	 * 
	 */
	public void setCollapse() {
		this.value = "collapse";
	}

	/**
	 * <dt class="label">replace</dt>
	 * <dd> All occurrences of #x9 (tab), #xA (line feed) and #xD (carriage
	 * return) are replaced with #x20 (space)
	 * 
	 */
	public void setReplace() {
		this.value = "replace";
	}

	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
		// TODO Auto-generated method stub
		
	}

}
