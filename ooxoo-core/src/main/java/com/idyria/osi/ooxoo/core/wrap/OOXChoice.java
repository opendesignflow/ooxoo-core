/**
 * 
 */
package com.idyria.osi.ooxoo.core.wrap;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;



/**
 * @author Rtek This class is a chooser for nested class implementation when
 *         faction a choice xsd element
 */
public class OOXChoice {

	Vector<Object> vector = new Vector<Object>();

	private int minOccurs = 1;

	private MaxOccursBuffer maxOccurs = new MaxOccursBuffer(1);

	/**
	 * The count of current selection numbers
	 */
	private int current = 0;

	/**
	 * 
	 */
	public OOXChoice() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method is called by a chooser and decides wether this choose is
	 * authorized. If yes, it creates the object, registers it and returns it.
	 * If no, it throws an exception
	 * 
	 * @return
	 * @throws ZaxbException
	 */
	protected Object choose(Class classdef, NCNameBuffer name)
			throws OOXChoiceLimitExceeded {

		// Check choice count authorizes this choice
		// -------------------------------------------------
//		if (current >= maxOccurs.getValue() && !maxOccurs.isUnbounded()) {
//			throw new OOXChoiceLimitExceeded("Choice limits reached. Cannot proceed");
//		}

		// Proceed (instanciate and set nodename to allow good rendering when the
		// class is xxxType for example)
		// -------------------------------------------------
		Object elt;
		try {
			elt = classdef.getConstructor(new Class[]{NCNameBuffer.class}).newInstance(new Object[]{name});

			vector.add(elt);
			
			return elt;
		} catch (InstantiationException e) {
			throw new OOXChoiceLimitExceeded("Instanciation Exception occured, could not instanciate requested element");
		} catch (IllegalAccessException e) {
			throw new OOXChoiceLimitExceeded("Instanciation Exception occured, could not instanciate requested element");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}


	public Vector<Object> getVector() {
		return vector;
	}
}
