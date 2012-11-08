/**
 * This is a repository for ZaxCElements representing a group
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;


/**
 * @author Rtek
 *
 */
public class ZaxCElementGroup {

	private String name = null;
	
	private LinkedList<ZaxCElement> elements = new LinkedList<ZaxCElement>();

	/**
	 * @return Returns the elements.
	 */
	public LinkedList<ZaxCElement> getElements() {
		return elements;
	}

	/**
	 * @param elements The elements to set.
	 */
	public void setElements(LinkedList<ZaxCElement> elements) {
		this.elements = elements;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
