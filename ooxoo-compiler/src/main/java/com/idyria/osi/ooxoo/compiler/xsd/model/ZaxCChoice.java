/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;



/**
 * The choice element : <BR>
 * <BR>
 * <a name="element-choice" id="element-choice">&lt;choice</a><br>
 * &nbsp;&nbsp;id = <a
 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID">ID</a><br>
 * &nbsp;&nbsp;maxOccurs =
 *  (<a
 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger">nonNegativeInteger</a> |
 * <var>unbounded</var>) &nbsp;:&nbsp;1<br>
 * &nbsp;&nbsp;minOccurs = <a
 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger">nonNegativeInteger</a>&nbsp;:&nbsp;1<br>
 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
 * href="#element-annotation">annotation</a>?, (<a class="eltref"
 * href="#element-element">element</a> | <a class="eltref"
 * href="#element-group">group</a> | <a class="eltref"
 * href="#element-choice">choice</a> | <a class="eltref"
 * href="#element-sequence">sequence</a> | <a class="eltref"
 * href="#element-any">any</a>)*)<br>
 * &lt;/choice&gt;
 * 
 * @author Rtek
 * 
 */
public class ZaxCChoice extends ZaxCAbstractChoiceAndSequence {

	public ZaxCChoice() {

	}

	
	
	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String, boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
