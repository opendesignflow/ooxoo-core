/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;


/**
 * @author Rtek
 * 
 */
public class ZaxCComplexType extends ZaxCAbstractSelectElementsAndAttributes {

	/**
	 * 
	 * @see {@link http://www.w3.org/TR/xmlschema-1/#Complex_Type_Definitions}
	 */
	protected boolean Abstract = false;

	/**
	 * The Name Of the type
	 */
	protected NCNameBuffer name = null;

	/**
	 * Final means cannot be extended or restricted for all types if the value
	 * is #all, or just for restriction or extension if restriction or extension
	 * is used as a value : (#all | List of (extension | restriction))
	 */
	protected List<String> Final = null;

	/**
	 * Same as for Final
	 * 
	 * @see ZaxCComplexType#Final
	 */
	protected List<String> block = null;

	/**
	 * TODO Did not understood
	 */
	protected boolean mixed = false;

	/** @name Elements */
	/** @{ */

	protected ZaxCAll all = null;

	protected ZaxCSequence sequence = null;

	protected ZaxCChoice choice = null;

	protected ZaxCGroup group = null;

	protected ZaxCSimpleContent simpleContent = null;

	protected ZaxCComplexContent complexContent = null;

	/** }@ */

	/** @name Attributes */
	/** @{ */

	/**
	 * The attribute elements
	 */
	protected List<ZaxCAttribute> attributes = null;

	/**
	 * The attributeGroup elements
	 */
	protected List<ZaxCAttributeGroup> attributeGroups = null;

	/**
	 * an anyAttribute element
	 */
	protected ZaxCAnyAttribute anyAttribute = null;

	/** }@ */

	/**
	 * 
	 */
	public ZaxCComplexType() {
		// TODO Auto-generated constructor stub
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
	 * @return the block
	 */
	public List<String> getBlock() {
		if (block == null)
			block = new LinkedList<String>();
		return block;
	}

	/**
	 * @return the mixed
	 */
	public boolean isMixed() {
		return mixed;
	}

	/**
	 * @param mixed
	 *            the mixed to set
	 */
	public void setMixed(boolean mixed) {
		this.mixed = mixed;
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
	 * @return the group
	 */
	public ZaxCGroup getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(ZaxCGroup group) {
		this.group = group;
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
	 * @return the anyAttribute
	 */
	public ZaxCAnyAttribute getAnyAttribute() {
		return anyAttribute;
	}

	/**
	 * @param anyAttribute
	 *            the anyAttribute to set
	 */
	public void setAnyAttribute(ZaxCAnyAttribute anyAttribute) {
		this.anyAttribute = anyAttribute;
	}

	/**
	 * @return the attributeGroups
	 */
	public List<ZaxCAttributeGroup> getAttributeGroups() {
		if (attributeGroups == null)
			attributeGroups = new LinkedList<ZaxCAttributeGroup>();
		return attributeGroups;
	}

	/**
	 * @return Returns the attributes.
	 */
	public List<ZaxCAttribute> getAttributes() {
		if (attributes == null)
			attributes = new LinkedList<ZaxCAttribute>();
		return attributes;
	}

	/**
	 * @return the complexContent
	 */
	public ZaxCComplexContent getComplexContent() {
		return complexContent;
	}

	/**
	 * @param complexContent
	 *            the complexContent to set
	 */
	public void setComplexContent(ZaxCComplexContent complexContent) {
		this.complexContent = complexContent;
	}

	/**
	 * @return the simpleContent
	 */
	public ZaxCSimpleContent getSimpleContent() {
		return simpleContent;
	}

	/**
	 * @param simpleContent
	 *            the simpleContent to set
	 */
	public void setSimpleContent(ZaxCSimpleContent simpleContent) {
		this.simpleContent = simpleContent;
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
	 * @return the final
	 */
	public List<String> getFinal() {
		if (Final == null)
			Final = new LinkedList<String>();
		return Final;
	}

	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema)
			throws SyntaxException {
		// TODO Perform validation for complexType

	}

}
