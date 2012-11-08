/**
 * 
 */
package com.idyria.osi.ooxoo.core.tu;

import com.idyria.osi.ooxoo.core.WrappingContext;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;

/**
 * @author rtek
 *
 */
public class TransferUnit {

	/**
	 * 
	 */
	protected Ooxnode nodeAnnotation = null;
	
	/**
	 * 
	 */
	protected String value = null;
	
	/**
	 * The context to propagate through wrap/unwrap
	 */
	protected WrappingContext wrappingContext = null;
	
	/**
	 * Should we reset when we traverse
	 */
	protected boolean reset = true;
	
	/**
	 * 
	 */
	public TransferUnit() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @return the nodeAnnotation
	 */
	public Ooxnode getNodeAnnotation() {
		return nodeAnnotation;
	}



	/**
	 * @param nodeAnnotation the nodeAnnotation to set
	 */
	public void setNodeAnnotation(Ooxnode nodeAnnotation) {
		this.nodeAnnotation = nodeAnnotation;
	}



	/**
	 * Return the value of this TransferUnit as a String
	 * @return
	 */
	public String getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


	/**
	 * @return the wrappingContext
	 */
	public WrappingContext getWrappingContext() {
		return wrappingContext;
	}


	/**
	 * @param wrappingContext the wrappingContext to set
	 */
	public void setWrappingContext(WrappingContext wrappingContext) {
		this.wrappingContext = wrappingContext;
	}


	/**
	 * @return the reset
	 */
	public boolean isReset() {
		return reset;
	}


	/**
	 * @param reset the reset to set
	 */
	public void setReset(boolean reset) {
		this.reset = reset;
	}
	
	

}
