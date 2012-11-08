/**
 * this class represents a type derived from an xml schema
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;


/**
 * This class is a common type for ComplexType or SimpleType
 * @author Rtek
 * 
 */
public abstract class ZaxCAbstractType extends ZaxCAnnotableType {

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

	public ZaxCAbstractType() {
		
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

}
