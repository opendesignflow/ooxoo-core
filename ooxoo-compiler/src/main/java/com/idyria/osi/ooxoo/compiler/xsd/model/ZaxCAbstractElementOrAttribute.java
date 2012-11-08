/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * This si a type that regroups common informations between an element and an
 * attribute
 * 
 * @author Rtek
 * 
 */
public abstract class ZaxCAbstractElementOrAttribute extends ZaxCAnnotableType {

	/**
	 * The Name Of the type
	 */
	protected NCNameBuffer name = null;

	/**
	 * reference to an other attribute definition
	 */
	protected QNameBuffer ref = null;
	
	/**
	 * 
	 */
	protected XSDStringBuffer Default = null;

	/**
	 * fixed indicates that the attribute value if present must equal the
	 * supplied constraint value, and if absent receives the supplied value as
	 * for default
	 */
	protected XSDStringBuffer fixed = null;

	/**
	 * (qualified|unqualified)
	 */
	protected String form = null;
	
	
	/**
	 * type of the attribute definition
	 */
	protected QNameBuffer type = null;
	
	/**
	 * A child simpleType declaration for anonymous type declaration
	 */
	protected ZaxCSimpleType simpleType = null;
	
	/**
	 * 
	 */
	public ZaxCAbstractElementOrAttribute() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the default
	 */
	public XSDStringBuffer getDefault() {
		return Default;
	}

	/**
	 * @param default1 the default to set
	 */
	public void setDefault(XSDStringBuffer default1) {
		Default = default1;
	}

	/**
	 * @return the fixed
	 */
	public XSDStringBuffer getFixed() {
		return fixed;
	}

	/**
	 * @param fixed the fixed to set
	 */
	public void setFixed(XSDStringBuffer fixed) {
		this.fixed = fixed;
	}

	/**
	 * @return the form
	 */
	public String getForm() {
		return form;
	}

	/**
	 * @param form the form to set
	 * @throws SyntaxException 
	 */
	public void setForm(String form) throws SyntaxException {
		if (form!=null &&  form.length()>0 && (!form.equals("qualified") && !form.equals("unqualified") ))
			throw new SyntaxException("form attribute's value is nor qualified nor unqualified");
		else if (form!=null &&  form.length()==0)
			this.form = null;
		else
			this.form = form;
	}


	/**
	 * @return the name
	 */
	public NCNameBuffer getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(NCNameBuffer name) {
		this.name = name;
	}

	/**
	 * @return the ref
	 */
	public QNameBuffer getRef() {
		return ref;
	}

	/**
	 * @param ref the ref to set
	 */
	public void setRef(QNameBuffer ref) {
		this.ref = ref;
	}

	/**
	 * @return the type
	 */
	public QNameBuffer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(QNameBuffer type) {
		this.type = type;
	}

	/**
	 * @return the simpleType
	 */
	public ZaxCSimpleType getSimpleType() {
		return simpleType;
	}

	/**
	 * @param simpleType the simpleType to set
	 */
	public void setSimpleType(ZaxCSimpleType simpleType) {
		this.simpleType = simpleType;
	}

	
	
	
}

