/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;

/**
 * @author Rtek
 * 
 */
public class ZaxCAttribute extends ZaxCAbstractElementOrAttribute {

	/**
	 * (optional | prohibited | required) : optional
	 */
	protected String use = "optional";

	/**
	 * 
	 */
	public ZaxCAttribute() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the use
	 */
	public String getUse() {
		return use;
	}

	/**
	 * Set use to "prohibited" value
	 */
	public void setUseProhibited() {
		this.use = "prohibited";
	}

	/**
	 * Set use to "required" value
	 */
	public void setUseRequired() {
		this.use = "required";
	}

	/**
	 * Set use to "optional" value
	 */
	public void setUseOptional() {
		this.use = "optional";
	}

	public void setUse(String use) throws SyntaxException {
		if (use!=null && use.equals("prohibited"))
			this.setUseProhibited();
		else if (use!=null && use.equals("optional"))
			this.setUseOptional();
		else if (use!=null && use.equals("required"))
			this.setUseRequired();
		else if (use==null || use.length()==0) ;
		else
			throw new SyntaxException("use attribute in <attribute> declaration MUSt be one of these values : (optional | prohibited | required)");
		
	}
	
	/**
	 * This method validates the attribute declaration against schema
	 * constraints <BR>
	 *
	 * 
	 * @param parentsName
	 * @throws SyntaxException
	 */
	public void validateSchema(String parentsName,boolean parentsSchema) throws SyntaxException {

		// 1 default and fixed must not both be present.
		if (this.Default != null && this.fixed != null) {
			throw new SyntaxException(
					"attribute declaration [in "
							+ parentsName
							+ "] is not valid : default and fixed must not both be present.");
		}

		// 2 If default and use are both present, use must have the �actual
		// value� optional.
		if (this.Default != null && use != null && !use.equals("optional")) {
			throw new SyntaxException(
					"attribute declaration [in "
							+ parentsName
							+ "] is not valid : If default and use are both present, use must have the �actual value� optional.");
		}

		// 3 If the item's parent is not <schema>, then all of the following must be true:
		if (!parentsSchema) {
			// 3.1 One of ref or name must be present, but not both.
			if ( (ref!=null && name!=null) || ref==null && name==null)
				throw new SyntaxException(
						"attribute declaration [in "
								+ parentsName
								+ "] is not valid : One of ref or name must be present, but not both.");
			
			
			// 3.2 If ref is present, then all of <simpleType>, form and type must be absent.
			if (ref!=null && (form!=null || type!=null || simpleType!=null)) {
				throw new SyntaxException(
						"attribute declaration [in "
								+ parentsName
								+ "] is not valid : If ref is present, then all of <simpleType>("+simpleType+"), form ("+form+") and type ("+type+") MUST be absent");
			}
		}
		
		
		// 4 type and <simpleType> must not both be present.
		if (type!=null && simpleType!=null) {
			throw new SyntaxException(
					"attribute declaration [in "
							+ parentsName
							+ "] is not valid : type and <simpleType> must not both be present");
		}
		
		// TODO 5 The corresponding attribute declaration must satisfy the conditions set out in Constraints on Attribute Declaration Schema Components (�3.2.6).
		
		
		// 3 If the {type definition} is or is derived from ID then there must not be a {value constraint}.
		// 2 if there is a {value constraint}, the canonical lexical representation of its value must be �valid� with respect to the {type definition} as defined in String Valid (�3.14.4).
		// The {name} of an attribute declaration must not match xmlns.
		if (name!=null && name.toString().equals("xmlns"))
			throw new SyntaxException(
					"attribute declaration [in "
							+ parentsName
							+ "] is not valid : The {name} of an attribute declaration must not match xmlns.");
		
	}

}
