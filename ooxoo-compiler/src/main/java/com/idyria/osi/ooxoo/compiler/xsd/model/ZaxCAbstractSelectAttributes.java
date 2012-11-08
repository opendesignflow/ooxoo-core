/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;


/**
 * 
 * This abstract class regroups this data schema : <br>
 * <br>
 * 
 * (annotation?,((attribute | attributeGroup)*, anyAttribute?))
 * 
 * @author Rtek
 * 
 */
public abstract class ZaxCAbstractSelectAttributes extends ZaxCAnnotableType {

	/**
	 * The attribute elements
	 */
	protected List<ZaxCAttribute> attributes = null;

	/**
	 * The attributeGroup elements
	 */
	protected List<ZaxCAttributeGroup> attributeGroups = null;

	/**
	 * an anyAttribute element
	 */
	protected ZaxCAnyAttribute anyAttribute = null;

	/**
	 * 
	 */
	public ZaxCAbstractSelectAttributes() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the anyAttribute
	 */
	public ZaxCAnyAttribute getAnyAttribute() {
		return anyAttribute;
	}

	/**
	 * @param anyAttribute
	 *            the anyAttribute to set
	 */
	public void setAnyAttribute(ZaxCAnyAttribute anyAttribute) {
		this.anyAttribute = anyAttribute;
	}

	/**
	 * @return the attributeGroups
	 */
	public List<ZaxCAttributeGroup> getAttributeGroups() {
		if (attributeGroups == null)
			attributeGroups = new LinkedList<ZaxCAttributeGroup>();
		return attributeGroups;
	}

	/**
	 * @return Returns the attributes.
	 */
	public List<ZaxCAttribute> getAttributes() {
		if (attributes == null)
			attributes = new LinkedList<ZaxCAttribute>();
		return attributes;
	}

	/**
	 * Validate conformance to attributes schema : ((attribute |
	 * attributeGroup)*, anyAttribute?))
	 * 
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema)
			throws SyntaxException {
//		if (anyAttribute != null
//				&& ((attributes != null && attributes.size() > 0) || (attributeGroups != null && attributeGroups
//						.size() > 0))) {
//
//			throw new SyntaxException(
//					"attributes schema : ((attribute | attributeGroup)*, anyAttribute?)) is not respected in ["
//							+ parentsName + "]");
//
//		}

	}

}
