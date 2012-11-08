/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * 
 * Represents a group particle
 * 
 * 
 * @author Rtek
 * 
 */
public class ZaxCGroup extends ZaxCAnnotableType {

	/**
	 * The Name Of the type
	 */
	protected NCNameBuffer name = null;

	/**
	 * reference to an other attribute definition
	 */
	protected QNameBuffer ref = null;

	protected NonNegativeIntegerBuffer minOccurs = new NonNegativeIntegerBuffer(1);

	protected MaxOccursBuffer maxOccurs = new MaxOccursBuffer(1);

	protected ZaxCAll all = null;

	protected ZaxCSequence sequence = null;

	protected ZaxCChoice choice = null;

	/**
	 * 
	 */
	public ZaxCGroup() {
		// TODO Auto-generated constructor stub
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
	 * @return the name
	 */
	public NCNameBuffer getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(NCNameBuffer name) {
		this.name = name;
	}

	/**
	 * @return the ref
	 */
	public QNameBuffer getRef() {
		return ref;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setRef(QNameBuffer ref) {
		this.ref = ref;
	}

	/**
	 * @return the all
	 */
	public ZaxCAll getAll() {
		return all;
	}

	/**
	 * @param all
	 *            the all to set
	 */
	public void setAll(ZaxCAll all) {
		this.all = all;
	}

	/**
	 * @return the choice
	 */
	public ZaxCChoice getChoice() {
		return choice;
	}

	/**
	 * @param choice
	 *            the choice to set
	 */
	public void setChoice(ZaxCChoice choice) {
		this.choice = choice;
	}

	/**
	 * @return the sequence
	 */
	public ZaxCSequence getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(ZaxCSequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema)
			throws SyntaxException {

		
//		 1 name and ref cannot be both set
		if (this.name != null && this.ref != null) {
			throw new SyntaxException("group declaration [in "
					+ parentsName
					+ "] is not valid : name and ref cannot be both set");
		}

		// 2 name can only be set on top level attributeGroup
		if (this.name != null && !parentSchema) {
			throw new SyntaxException(
					"group declaration [in "
							+ parentsName
							+ "] is not valid : name can only be set on top level attributeGroups");
		}

		// 3 children attributes can only be set on top level attributeGroup
		if (!parentSchema
				&& (this.all != null || this.choice != null || this.sequence != null)) {
			throw new SyntaxException(
					"group declaration [in "
							+ parentsName
							+ "] is not valid : children attributes can only be set on top level attributeGroup");
		}

		// 4 top level attributeGroup cannot reference another group (only
		// allowed in redefinitions)
		if (parentSchema && this.ref != null) {
			throw new SyntaxException(
					"group declaration [in "
							+ parentsName
							+ "] is not valid : top level attributeGroup cannot reference another group (only allowed in redefinitions)");
		}
		
		// Check (all | choice | sequence)?)
		if (this.all != null && (choice != null || sequence != null)
				|| this.choice != null && (all != null || sequence != null)
				|| this.sequence != null && (choice != null || all != null)) {
			throw new SyntaxException(
					"group Element in ["
							+ parentsName
							+ "] doest not respect this children constraints : (all | choice | sequence)?)");
		}

	}
}
