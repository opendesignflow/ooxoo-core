/**
 * 
 */
package com.idyria.osi.ooxoo.core.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;

/**
 * A type that can contain an xs:annotation element
 * @author Rtek
 *
 */
public abstract class  ZaxCAnnotableType {

	private ZaxCAnnotation annotation = null;
	
	/**
	 * The element's Id
	 */
	protected IDBuffer id = null;
	
	/**
	 * 
	 */
	public ZaxCAnnotableType() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the annotation
	 */
	public ZaxCAnnotation getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(ZaxCAnnotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * @return the id
	 */
	public IDBuffer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(IDBuffer id) {
		this.id = id;
	}

	/**
	 * This method is used to check the recorded properties are OK with schema constraints
	 * @param parentsName The name of the parent [used when throwing an exception]
	 * @param parentSchema is an xs:schema element parent of this element
	 * @throws SyntaxException In case of error
	 */
	public abstract void validateSchema(String parentsName,boolean parentSchema) throws SyntaxException;
	
	
	
}
