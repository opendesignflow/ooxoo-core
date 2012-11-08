/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/** Represents a complexContent
 * @author Rtek
 *
 */
public class ZaxCComplexContent extends ZaxCAnnotableType {

	
	/**
	 * @author Rtek
	 *
	 */
	public class restrictionOrExtension extends ZaxCAbstractSelectElementsAndAttributes {

		/**
		 * The base type to extend
		 */
		protected QNameBuffer base = null;
		
		// TODO add elements selection
		
		/**
		 * 
		 */
		public restrictionOrExtension() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * @return the base
		 */
		public QNameBuffer getBase() {
			return base;
		}

		/**
		 * @param base the base to set
		 */
		public void setBase(QNameBuffer base) {
			this.base = base;
		}

		@Override
		public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
			super.validateSchema(parentsName, parentSchema);
			
		}

		
		
	}

	protected Boolean mixed = null;
	
	protected ZaxCComplexContent.restrictionOrExtension extension = null;
	protected ZaxCComplexContent.restrictionOrExtension restriction = null;
	
	
	/**
	 * 
	 */
	public ZaxCComplexContent() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the extension
	 */
	public ZaxCComplexContent.restrictionOrExtension getExtension() {
		return extension;
	}


	/**
	 * @param extension the extension to set
	 */
	public void setExtension(ZaxCComplexContent.restrictionOrExtension extension) {
		this.extension = extension;
	}


	/**
	 * @return the mixed
	 */
	public Boolean getMixed() {
		return mixed;
	}


	/**
	 * @param mixed the mixed to set
	 */
	public void setMixed(Boolean mixed) {
		this.mixed = mixed;
	}


	/**
	 * @return the restriction
	 */
	public ZaxCComplexContent.restrictionOrExtension getRestriction() {
		return restriction;
	}


	/**
	 * @param restriction the restriction to set
	 */
	public void setRestriction(ZaxCComplexContent.restrictionOrExtension restriction) {
		this.restriction = restriction;
	}


	
	
	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String, boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {


		if (this.restriction!=null && this.extension!=null)
			throw new SyntaxException(
					"Type ["
							+ parentsName
							+ "] MUST only contain one restriction OR extension element : (extension|restriction)?");
		
	}

	
	
}
