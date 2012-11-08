/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

/**
 * 
 * This abstract class regroups this data schema : <br><br>
 * 
 * (annotation?, (group | all | choice | sequence)?, ((attribute | attributeGroup)*, anyAttribute?))
 * 
 * @author Rtek
 *
 */
public abstract class ZaxCAbstractSelectElementsAndAttributes extends ZaxCAbstractSelectAttributes {

	
	
	protected ZaxCAll all = null;
	protected ZaxCSequence sequence = null;
	protected ZaxCChoice choice = null;
	protected ZaxCGroup group = null;
	
	
	/**
	 * 
	 */
	public ZaxCAbstractSelectElementsAndAttributes() {
		// TODO Auto-generated constructor stub
	}


	
	/**
	 * @return the all
	 */
	public ZaxCAll getAll() {
		return all;
	}


	/**
	 * @param all the all to set
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
	 * @param choice the choice to set
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
	 * @param group the group to set
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
	 * @param sequence the sequence to set
	 */
	public void setSequence(ZaxCSequence sequence) {
		this.sequence = sequence;
	}

	
	
}
