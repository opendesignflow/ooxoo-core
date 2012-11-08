/**
 * 
 */
package com.idyria.osi.ooxoo.core.io;

import java.util.LinkedList;

import com.idyria.osi.ooxoo.core.Buffer;



/**
 * @author rtek
 *
 */
public abstract class AbstractXMLIO extends Buffer<Object> {

	protected LinkedList<AbstractXMLIO> childrenIO = new LinkedList<AbstractXMLIO>();
	
	/**
	 * 
	 */
	public AbstractXMLIO() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 
	 * @return
	 */
	public abstract AbstractXMLIO createSubContext();

	/**
	 * Duplicate but so that the IO contexts remains available for a new wrapping operation
	 * @return
	 */
	public abstract AbstractXMLIO createSameContext();

	/**
	 * Replace the context in an initial state
	 */
	public abstract void reset();
	
	/**
	 * Remove and propagate to children
	 */
	@Override
	public void remove() {
		// Remove
		super.remove();
		
		// Propagate to children
		for (AbstractXMLIO childIO:this.childrenIO) {
			childIO.remove();
		}
		
		
	}

	
	
	
	
}
