/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;


import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.utils.java.logging.TeaLogging;

/**
 * This class is designed to bring support for this pattern : (simpleType |
 * complexType | group | attributeGroup|annotation)*
 * 
 * @author Rtek
 * 
 */
public abstract class ZaxCAbstractSelectMultipleTypesAndGroups extends
		ZaxCMultiplyAnnotable {

	protected List<ZaxCSimpleType> simpleTypes = null;

	protected List<ZaxCComplexType> complexTypes = null;

	protected List<ZaxCGroup> groups = null;

	protected List<ZaxCAttributeGroup> attributeGroups = null;

	/**
	 * 
	 */
	public ZaxCAbstractSelectMultipleTypesAndGroups() {
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
	 * Retrieves an attribute group using a reference
	 * @param ref
	 * @return
	 */
	public ZaxCAttributeGroup getAtributeGroup(QNameBuffer ref) {

		for (ZaxCAttributeGroup grp:this.getAttributeGroups()) {
			TeaLogging.teaLogInfo("Trying "+ref.getLocalPart()+" against: "+grp.getName());
			if (grp.getName().toString().equals(ref.getLocalPart().toString())) {
				return grp;
			}
		}
		
		// null if none found
		return null;
		
	}
	
	/**
	 * @return the complexTypes
	 */
	public List<ZaxCComplexType> getComplexTypes() {
		if (complexTypes == null)
			complexTypes = new LinkedList<ZaxCComplexType>();
		return complexTypes;
	}

	/**
	 * @return the groups
	 */
	public List<ZaxCGroup> getGroups() {
		if (groups == null)
			groups = new LinkedList<ZaxCGroup>();
		return groups;
	}

	/**
	 * Retrieves a group using a reference
	 * @param ref
	 * @return
	 */
	public ZaxCGroup getGroup(QNameBuffer ref) {

//		System.out.println("Dereferencing group :"+ref);
		for (ZaxCGroup grp:this.getGroups()) {
			if (grp.getName().toString().equals(ref.getLocalPart().toString())) {
				return grp;
			}
		}
		
		// null if none found
		return null;
		
	}

	/**
	 * @return the simpleTypes
	 */
	public List<ZaxCSimpleType> getSimpleTypes() {
		if (simpleTypes == null)
			simpleTypes = new LinkedList<ZaxCSimpleType>();
		return simpleTypes;
	}

}
