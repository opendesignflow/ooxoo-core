/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;


/**
 * 
 * <a name="element-all" id="element-all">&lt;all</a><br>
 * &nbsp;&nbsp;id = <a
 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID">ID</a><br>
 * &nbsp;&nbsp;maxOccurs = <var>1</var>&nbsp;:&nbsp;1<br>
 * &nbsp;&nbsp;minOccurs = (<var>0</var> | <var>1</var>)&nbsp;:&nbsp;1<br>
 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
 * href="#element-annotation">annotation</a>?, <a class="eltref"
 * href="#element-element">element</a>*)<br>
 * &lt;/all&gt;
 * 
 * @author Rtek
 * 
 */
public class ZaxCAll extends ZaxCAnnotableType {

	protected NonNegativeIntegerBuffer minOccurs = new NonNegativeIntegerBuffer(1);

	protected MaxOccursBuffer maxOccurs = new MaxOccursBuffer(1);
	
	protected List<ZaxCElement> elements = null;
	
	/**
	 * 
	 */
	public ZaxCAll() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the elements
	 */
	public List<ZaxCElement> getElements() {
		if(elements==null)
			elements = new LinkedList<ZaxCElement>();
		return elements;
	}

	/**
	 * @return the maxOccurs
	 */
	public NonNegativeIntegerBuffer getMaxOccurs() {
		return maxOccurs;
	}

	/**
	 * @return the minOccurs
	 */
	public NonNegativeIntegerBuffer getMinOccurs() {
		return minOccurs;
	}

	public void setMinOccurs0() {
		this.minOccurs.setValue(0);
	}
	
	public void setMinOccurs1() {
		this.minOccurs.setValue(1);
	}

	/**
	 * @param minOccurs the minOccurs to set
	 */
	public void setMinOccurs(NonNegativeIntegerBuffer minOccurs) {
		this.minOccurs = minOccurs;
	}
	
	
	public void validateSchema(String parentsName, boolean parentsSchema) throws SyntaxException {
		
		if (this.minOccurs.getValue()!=0 && this.minOccurs.getValue()!=1 )
			throw new SyntaxException("all element's @minOccurs in ["+parentsName+"] MUST be (0|1)");
		
	}
	
	
}
