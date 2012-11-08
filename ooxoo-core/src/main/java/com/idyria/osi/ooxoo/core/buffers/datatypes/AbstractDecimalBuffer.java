/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Enumeration;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.FractionDigits;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Inclusive;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Pattern;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.TotalDigits;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.WhiteSpace;



/**
 * 
 * @author Rtek
 * 
 */
public abstract class AbstractDecimalBuffer<T> extends AbstractDataTypesBuffer<T> implements XSDType {


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

	protected Inclusive maxInclusive = null;

	protected Inclusive maxExclusive = null;

	protected Inclusive minInclusive = null;

	protected Inclusive minExclusive = null;

	/**
	 * @see TotalDigits
	 */
	protected TotalDigits totalDigits = null;

	/**
	 * @see FractionDigits
	 */
	protected FractionDigits fractionDigits = null;

	/**
	 * 
	 */
	public AbstractDecimalBuffer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the enumeration
	 */
	public List<Enumeration> getEnumeration() {
		if (enumeration == null)
			enumeration = new LinkedList<Enumeration>();
		return enumeration;
	}

	/**
	 * @return the fractionDigits
	 */
	public FractionDigits getFractionDigits() {
		return fractionDigits;
	}

	/**
	 * @param fractionDigits
	 *            the fractionDigits to set
	 */
	public void setFractionDigits(FractionDigits fractionDigits) {
		this.fractionDigits = fractionDigits;
	}

	/**
	 * @return the maxExclusive
	 */
	public Inclusive getMaxExclusive() {
		return maxExclusive;
	}

	/**
	 * @param maxExclusive
	 *            the maxExclusive to set
	 */
	public void setMaxExclusive(Inclusive maxExclusive) {
		this.maxExclusive = maxExclusive;
	}

	/**
	 * @return the maxInclusive
	 */
	public Inclusive getMaxInclusive() {
		return maxInclusive;
	}

	/**
	 * @param maxInclusive
	 *            the maxInclusive to set
	 */
	public void setMaxInclusive(Inclusive maxInclusive) {
		this.maxInclusive = maxInclusive;
	}

	/**
	 * @return the minExclusive
	 */
	public Inclusive getMinExclusive() {
		return minExclusive;
	}

	/**
	 * @param minExclusive
	 *            the minExclusive to set
	 */
	public void setMinExclusive(Inclusive minExclusive) {
		this.minExclusive = minExclusive;
	}

	/**
	 * @return the minInclusive
	 */
	public Inclusive getMinInclusive() {
		return minInclusive;
	}

	/**
	 * @param minInclusive
	 *            the minInclusive to set
	 */
	public void setMinInclusive(Inclusive minInclusive) {
		this.minInclusive = minInclusive;
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
	 * @return the totalDigits
	 */
	public TotalDigits getTotalDigits() {
		return totalDigits;
	}

	/**
	 * @param totalDigits
	 *            the totalDigits to set
	 */
	public void setTotalDigits(TotalDigits totalDigits) {
		this.totalDigits = totalDigits;
	}

	/**
	 * @return the value
	 */
	//protected abstract T getValueImpl();

	/**
	 * @param value
	 *            the value to set
	 */
	//protected abstract void setValueImpl(T value);

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
	 * @see com.idyria.osi.ooxoo.core.buffers.datatypes.XSDType#validate()
	 */
	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return true;
	}

	


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getValue().toString();
	}
	
	
}
