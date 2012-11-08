/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Enumeration;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.FractionDigits;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Inclusive;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Length;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Pattern;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.TotalDigits;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.WhiteSpace;


/**
 * This is a base type for SimpleType Restriction
 * 
 * @author Rtek
 * 
 */
public abstract class ZaxCAbstractSimpleTypeRestriction extends ZaxCAbstractSelectAttributes {
	

	/**
	 * The base type for this restriction
	 */
	protected QNameBuffer base = null;

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
	 * @see com.idyria.ooxoo.core.buffers.datatypes.constraints.Pattern
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
	 * a simpleType child for anonymous declarations
	 */
	protected ZaxCSimpleType simpleType = null;
	
	/**
	 * 
	 */
	public ZaxCAbstractSimpleTypeRestriction() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the base
	 */
	public QNameBuffer getBase() {
		return base;
	}

	/**
	 * @param base
	 *            the base to set
	 */
	public void setBase(QNameBuffer base) {
		this.base = base;
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
	 * @return the simpleType
	 */
	public ZaxCSimpleType getSimpleType() {
		return simpleType;
	}

	/**
	 * @param simpleType the simpleType to set
	 */
	public void setSimpleType(ZaxCSimpleType simpleType) {
		this.simpleType = simpleType;
	}

	
	
}
