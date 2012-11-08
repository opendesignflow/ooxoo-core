/**
 * 
 */
package com.idyria.osi.ooxoo.core;

/**
 * @author rtek
 *
 */
public abstract class FrontBuffer<FT> extends Buffer {

	protected Class<FT> frontType = null;
	
	protected FT frontValue = null;
	
	/**
	 * 
	 */
	public FrontBuffer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the frontType
	 */
	public Class<?> getFrontType() {
		return frontType;
	}

	/**
	 * @param frontType the frontType to set
	 */
	public void setFrontType(Class<FT> frontType) {
		this.frontType = frontType;
	}

	/**
	 * @return the frontValue
	 */
	public FT getFrontValue() {
		return frontValue;
	}

	/**
	 * @param frontValue the frontValue to set
	 */
	public void setFrontValue(FT frontValue) {
		this.frontValue = frontValue;
	}

	
	
	

}
