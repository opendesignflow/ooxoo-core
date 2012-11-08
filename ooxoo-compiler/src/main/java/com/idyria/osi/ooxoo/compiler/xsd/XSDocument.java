/**
 * this class represents an xsd document an is a repository for types, groups and elements
 */
package com.idyria.osi.ooxoo.compiler.xsd;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idyria.osi.ooxoo.compiler.CompilerException;



/**
 * @author Rtek
 * 
 */
public abstract class XSDocument extends XSDResolvableDocument {


	public XSDocument() {

	}

	// ! the prefix/namespaces map
	protected HashMap<String, String> nsmap = new HashMap<String, String>();

	// namespace+compiled document to find referenced types
	protected HashMap<String, XSDImportedDocument> importMap = new HashMap<String, XSDImportedDocument>(); // The

	// List of included xml documents
	protected HashMap<String,XSDIncludedDocument> includes = new HashMap<String,XSDIncludedDocument>();


	

	
	public XSDocument(String path) throws CompilerException {
		super(path);

		// Check we have an XMLSchema document
		if (this.doc!=null) {
			
			Element root = doc.getDocumentElement();
			
			if (!root.getNamespaceURI().equals(XSDocument.XSD_NS) || !root.getLocalName().equals("schema")) {
				
				throw new CompilerException("Not an XML Schema Document!!");
				
			}
			
			
		}
		
	}
	

	

	
	

	/**
	 * @return Returns the importMap.
	 */
	public HashMap<String, XSDImportedDocument> getImportMap() {
		return importMap;
	}

	/**
	 * @return Returns the nsmap.
	 */
	public HashMap<String, String> getNsmap() {
		return nsmap;
	}



	/**
	 * @return Returns the includes.
	 */
	public HashMap<String,XSDIncludedDocument> getIncludes() {
		return includes;
	}

	protected void signalAppinfo(Node node,Node parent){
		
	}
	
	
}
