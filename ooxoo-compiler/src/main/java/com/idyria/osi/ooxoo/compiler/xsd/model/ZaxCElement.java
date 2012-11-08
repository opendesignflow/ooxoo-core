/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;


/**
 * 
 * TODO (unique | key | keyref)* reading
 * 
 * @author Rtek
 * 
 */
public class ZaxCElement extends ZaxCAbstractElementOrAttribute {

	/**
	 * Element declarations for which {abstract} is true can appear in content
	 * models only when substitution is allowed; such declarations may not
	 * themselves ever be used to validate element content.
	 * 
	 * @see {@link http://www.w3.org/TR/xmlschema-1/#Complex_Type_Definitions}
	 */
	protected boolean Abstract = false;

	/**
	 * Same as for Final
	 * 
	 * @see ZaxCAbstractType#Final
	 */
	protected List<String> block = null;

	/**
	 * Final means cannot be extended or restricted for all types if the value
	 * is #all, or just for restriction or extension if restriction or extension
	 * is used as a value : (#all | List of (extension | restriction))
	 */
	protected List<String> Final = null;

	protected NonNegativeIntegerBuffer minOccurs = new NonNegativeIntegerBuffer(1);

	protected MaxOccursBuffer maxOccurs = new MaxOccursBuffer(1);

	/**
	 * 
	 */
	protected boolean nillable = false;

	protected QNameBuffer substitutionGroup = null;

	/**
	 * A complexType
	 */
	protected ZaxCComplexType complexType = null;

	/**
	 * Default constructor
	 */
	public ZaxCElement() {
		// Default constructor
	}

	/**
	 * @return the block
	 */
	public List<String> getBlock() {
		if (block == null)
			block = new LinkedList<String>();
		return block;
	}

	/**
	 * @return the final
	 */
	public List<String> getFinal() {
		if (Final == null)
			Final = new LinkedList<String>();
		return Final;
	}

	/**
	 * @return the abstract
	 */
	public boolean isAbstract() {
		return Abstract;
	}

	/**
	 * @param abstract1
	 *            the abstract to set
	 */
	public void setAbstract(boolean abstract1) {
		Abstract = abstract1;
	}

	/**
	 * @return the maxOccurs
	 */
	public MaxOccursBuffer getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @param maxOccurs
	 *            the maxOccurs to set
	 */
	public void setMaxOccurs(MaxOccursBuffer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	/**
	 * @return the minOccurs
	 */
	public NonNegativeIntegerBuffer getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @param minOccurs
	 *            the minOccurs to set
	 */
	public void setMinOccurs(NonNegativeIntegerBuffer minOccurs) {
		this.minOccurs = minOccurs;
	}

	/**
	 * @return the nillable
	 */
	public boolean isNillable() {
		return nillable;
	}

	/**
	 * @param nillable
	 *            the nillable to set
	 */
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}

	/**
	 * @return the substitutionGroup
	 */
	public QNameBuffer getSubstitutionGroup() {
		return substitutionGroup;
	}

	/**
	 * @param substitutionGroup
	 *            the substitutionGroup to set
	 */
	public void setSubstitutionGroup(QNameBuffer substitutionGroup) {
		this.substitutionGroup = substitutionGroup;
	}

	/**
	 * @return the complexType
	 */
	public ZaxCComplexType getComplexType() {
		return complexType;
	}

	/**
	 * @param complexType
	 *            the complexType to set
	 */
	public void setComplexType(ZaxCComplexType complexType) {
		this.complexType = complexType;
	}

	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema)
			throws SyntaxException {

	}

}
