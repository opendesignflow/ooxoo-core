package com.idyria.osi.ooxoo.core.wrap.constraints;

public interface OOXNillable {

	/**
	 * Tells wether this element has be nillabed (means has the attribute :
	 * xsi:nil = true and no content)
	 * 
	 */
	public boolean isNillabed();

	/**
	 * Defin the nilla state
	 * @param val
	 */
	public void setNillabed(boolean val);

}
