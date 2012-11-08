/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;

/**
 * @author Rtek
 * 
 */
public class ZaxCAttributeGroup extends ZaxCAbstractSelectAttributes {

	/**
	 * The group name
	 */
	protected NCNameBuffer name = null;

	/**
	 * reference to an other attribute definition
	 */
	protected QNameBuffer ref = null;

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
	 * @return the ref
	 */
	public QNameBuffer getRef() {
		return ref;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setRef(QNameBuffer ref) {
		this.ref = ref;
	}

	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentsSchema)
			throws SyntaxException {
		
		// 1 name and ref cannot be both set
		if (this.name != null && this.ref != null) {
			throw new SyntaxException("attributeGroup declaration [in "
					+ parentsName
					+ "] is not valid : name and ref cannot be both set");
		}

		// 2 name can only be set on top level attributeGroup
		if (this.name != null && !parentsSchema) {
			throw new SyntaxException(
					"attributeGroup declaration [in "
							+ parentsName
							+ "] is not valid : name can only be set on top level attributeGroups");
		}

		// 3 children attributes can only be set on top level attributeGroup
		if (!parentsSchema
				&& (this.anyAttribute != null || (this.attributeGroups != null && !this.attributeGroups.isEmpty()) || (this.attributes != null && !this.attributes.isEmpty())   )) {
			throw new SyntaxException(
					"attributeGroup declaration [in "
							+ parentsName
							+ "] is not valid : children attributes can only be set on top level attributeGroup");
		}

		// 4 top level attributeGroup cannot reference another group (only
		// allowed in redefinitions)
		if (parentsSchema && this.ref != null) {
			throw new SyntaxException(
					"attributeGroup declaration [in "
							+ parentsName
							+ "] is not valid : top level attributeGroup cannot reference another group (only allowed in redefinitions)");
		}

	}

}
