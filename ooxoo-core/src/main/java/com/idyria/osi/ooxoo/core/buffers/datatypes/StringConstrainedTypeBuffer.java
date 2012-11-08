/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Enumeration;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Length;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Pattern;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.WhiteSpace;


/**
 * Represents a type that implements the default constraining facets :
 * <ul>
 * <li><a href="#rf-length">length</a></li>
 * <li><a href="#rf-minLength">minLength</a></li>
 * <li><a href="#rf-maxLength">maxLength</a></li>
 * <li><a href="#rf-pattern">pattern</a></li>
 * <li><a href="#rf-enumeration">enumeration</a></li>
 * <li><a href="#rf-whiteSpace">whiteSpace</a></li>
 * </ul>
 * 
 * @author Rtek
 * 
 */
public abstract class StringConstrainedTypeBuffer<T> extends AbstractDataTypesBuffer<T> implements XSDType {

	/**
	 * @see Length
	 */
	protected Length length = null;

	/**
	 * @see Length
	 */
	protected Length minLength = null;

	/**
	 * @see Length
	 */
	protected Length maxLength = null;

	/**
	 * @see com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Pattern
	 */
	protected Pattern pattern = null;

	/**
	 * @see Enumeration
	 */
	protected List<Enumeration> enumeration = null;

	/**
	 * @see WhiteSpace
	 */
	protected WhiteSpace whiteSpace = null;

	/**
	 * 
	 */
	public StringConstrainedTypeBuffer() {
		// TODO Auto-generated constructor stub
	}

	

	/**
	 * @see XSDType#validate()
	 */
	public boolean validate() throws SyntaxException {
		return true;
	}

	/**
	 * @return the length
	 */
	public Length getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(Length length) {
		this.length = length;
	}

	/**
	 * @return the maxLength
	 */
	public Length getMaxLength() {
		return maxLength;
	}

	/**
	 * @param maxLength
	 *            the maxLength to set
	 */
	public void setMaxLength(Length maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @return the minLength
	 */
	public Length getMinLength() {
		return minLength;
	}

	/**
	 * @param minLength
	 *            the minLength to set
	 */
	public void setMinLength(Length minLength) {
		this.minLength = minLength;
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the whiteSpace
	 */
	public WhiteSpace getWhiteSpace() {
		return whiteSpace;
	}

	/**
	 * @param whiteSpace
	 *            the whiteSpace to set
	 */
	public void setWhiteSpace(WhiteSpace whiteSpace) {
		this.whiteSpace = whiteSpace;
	}

	/**
	 * @return the enumeration
	 */
	public List<Enumeration> getEnumeration() {
		if (enumeration == null)
			enumeration = new LinkedList<Enumeration>();
		return enumeration;
	}

	@Override
	public String toString() {
		if (this.value != null)
			return this.value.toString();
		else
			return super.toString();
	}



	

	
	
}
