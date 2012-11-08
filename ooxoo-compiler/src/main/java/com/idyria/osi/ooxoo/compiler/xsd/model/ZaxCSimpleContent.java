/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;

/**
 * This class represents a SimpleContent
 * 
 * @author Rtek
 * 
 */
public class ZaxCSimpleContent extends ZaxCAnnotableType {

	/**
	 * a restriciton element in simpleContent : <BR>
	 * 
	 * 
	 * @author Rtek
	 * 
	 */
	public class restriction extends ZaxCAbstractSimpleTypeRestriction {

		/**
		 * 
		 */
		public restriction() {
			// TODO Auto-generated constructor stub
		}

		
		/**
		 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String, boolean)
		 */
		@Override
		public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
			
		}
		
	}

	/**
	 * Stricly equals the abstract extension description
	 * @author Rtek
	 *
	 */
	public class extension extends ZaxCAbstractSimpleExtension {
		
		/**
		 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String, boolean)
		 */
		@Override
		public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
			
		}
		
	
	}
	
	protected ZaxCSimpleContent.restriction restriction = null;

	protected ZaxCSimpleContent.extension extension = null;
	
	
	/**
	 * 
	 */
	public ZaxCSimpleContent() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the extension
	 */
	public ZaxCSimpleContent.extension getExtension() {
		return extension;
	}


	/**
	 * @param extension the extension to set
	 */
	public void setExtension(ZaxCSimpleContent.extension extension) {
		this.extension = extension;
	}


	/**
	 * @return the restriction
	 */
	public ZaxCSimpleContent.restriction getRestriction() {
		return restriction;
	}


	/**
	 * @param restriction the restriction to set
	 */
	public void setRestriction(ZaxCSimpleContent.restriction restriction) {
		this.restriction = restriction;
	}

	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String, boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
		
	}
	
}


