/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation;


/**
 * Used for an element to support multiple annotation elements
 * @author Rtek
 *
 */
public class ZaxCMultiplyAnnotable {

	/**
	 * All found annotations
	 */
	protected List<ZaxCAnnotation> annotations = new LinkedList<ZaxCAnnotation>();
	
	/**
	 * The element's Id
	 */
	protected IDBuffer id = null;
	
	/**
	 * 
	 */
	public ZaxCMultiplyAnnotable() {
		// TODO Auto-generated constructor stub
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
	 * @return the annotations
	 */
	public List<ZaxCAnnotation> getAnnotations() {
		if (annotations == null)
			annotations = new LinkedList<ZaxCAnnotation>();
		return annotations;
	}

	
	
	
}
