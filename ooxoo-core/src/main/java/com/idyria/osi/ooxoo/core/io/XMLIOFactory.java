/**
 * 
 */
package com.idyria.osi.ooxoo.core.io;

/**
 * @author rtek
 *
 */
public abstract class XMLIOFactory {

	/**
	 * 
	 */
	public XMLIOFactory() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract AbstractXMLIO createQualifiedIO(String namespace,String localName);
	

}
