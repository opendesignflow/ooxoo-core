package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;

/**
 * This describes a basic element which is a common type for elements or attributes
 * (we need a type, a name,an originalName if it is conflicting with language specific issues)
 * @author Rtek
 *
 */
public class ZaxCBasicElement {

	/** this is the complete path to the type of the attribute*/
	private String type = null;

	/** the attribute name*/
	private String name = null;

	/** The original name stored in case of a replacement*/
	private String originalName = null;
	
	/** the namespace*/
	private String namespaceURI = null;
	
	/***/
	private String baseType = null;

	// ! The constraint field to know about ZaxbCompiler constraint syntax
	//private LinkedList<ZaxbElementConstraint> constraint = new LinkedList<ZaxbElementConstraint>();

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}


	
	



	/**
	 * @return Returns the namespaceURI.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * @param namespaceURI
	 *            The namespaceURI to set.
	 */
	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	/**
	 * @return Returns the baseType.
	 */
	public String getBaseType() {
		return baseType;
	}

	/**
	 * @param baseType
	 *            The baseType to set.
	 */
	public void setBaseType(String baseType) {
		this.baseType = baseType;
	}

	/**
	 * @return the originalName
	 */
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * @param originalName the originalName to set
	 */
	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	
	
}
