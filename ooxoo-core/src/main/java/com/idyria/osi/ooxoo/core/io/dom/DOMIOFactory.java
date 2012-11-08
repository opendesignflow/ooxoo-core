/**
 * 
 */
package com.idyria.osi.ooxoo.core.io.dom;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.idyria.osi.ooxoo.core.io.AbstractXMLIO;
import com.idyria.osi.ooxoo.core.io.XMLIOFactory;



/**
 * @author rtek
 *
 */
public class DOMIOFactory extends XMLIOFactory {

	/**
	 * The target/source document
	 */
	private Document xmlDocument = null;
	
	/**
	 * 
	 */
	public DOMIOFactory() {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			this.xmlDocument = domFactory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@Override
	public AbstractXMLIO createQualifiedIO(String namespace, String localName) {
		
		// Create element and FIXME generate prefix
		Element elt = this.xmlDocument.createElementNS(namespace, "test:"+localName);
		return new DOMXMLIO(elt);
	}

}
