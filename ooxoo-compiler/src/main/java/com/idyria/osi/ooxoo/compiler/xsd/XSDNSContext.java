/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd;

import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.namespace.NamespaceContext;

/**
 * @author Rtek
 * 
 */
public class XSDNSContext implements NamespaceContext {

	/**
	 * 
	 */
	public XSDNSContext() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
	 */
	public String getNamespaceURI(String prefix) {
		if (prefix.equals("xs"))
			return XSDocument.XSD_NS;
		else if (prefix.equals("xml"))
			return XSDocument.XML_NS;
		else
			return null;
	}

	/**
	 * 
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	public String getPrefix(String namespaceURI) {
		
		if (namespaceURI == null)
			throw new IllegalArgumentException("namespaceURI MUST NOT be null");
		else if (namespaceURI.equals(XSDocument.XSD_NS))
			return "xs";
		else if (namespaceURI.equals(XSDocument.XML_NS))
			return "xml";
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	public Iterator getPrefixes(String namespaceURI) {
		LinkedList<String> list = new LinkedList<String>();
		list.add("xs");
		list.add("xml");

		return list.iterator();
	}

}
