/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;


/**
 * @author Rtek
 *
 */
public abstract class ZaxCAbstractChoiceAndSequence extends ZaxCAnnotableType {

	protected NonNegativeIntegerBuffer minOccurs = new NonNegativeIntegerBuffer(1);
	protected MaxOccursBuffer maxOccurs = new MaxOccursBuffer(1);
	protected List<ZaxCElement> elements = null;
	protected List<ZaxCGroup> groups = null;
	protected List<ZaxCChoice> choices = null;
	protected List<ZaxCSequence> sequences = null;
	protected List<ZaxCAny> any = null;

	/**
	 * 
	 */
	public ZaxCAbstractChoiceAndSequence() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the maxOccurs
	 */
	public MaxOccursBuffer getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @param maxOccurs the maxOccurs to set
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
	 * @param minOccurs the minOccurs to set
	 */
	public void setMinOccurs(NonNegativeIntegerBuffer minOccurs) {
		this.minOccurs = minOccurs;
	}

	/**
	 * @return the any
	 */
	public List<ZaxCAny> getAny() {
		if (any==null)
			any = new LinkedList<ZaxCAny>();
		return any;
	}

	/**
	 * @return the choices
	 */
	public List<ZaxCChoice> getChoices() {
		if (choices==null)
			choices = new LinkedList<ZaxCChoice>();
		return choices;
	}

	/**
	 * @return the elements
	 */
	public List<ZaxCElement> getElements() {
		if (elements==null)
			elements = new LinkedList<ZaxCElement>();
		return elements;
	}

	/**
	 * @return the groups
	 */
	public List<ZaxCGroup> getGroups() {
		if (groups==null)
			groups = new LinkedList<ZaxCGroup>();
		return groups;
	}

	/**
	 * @return the sequences
	 */
	public List<ZaxCSequence> getSequences() {
		if (sequences==null)
			sequences = new LinkedList<ZaxCSequence>();
		return sequences;
	}

}
