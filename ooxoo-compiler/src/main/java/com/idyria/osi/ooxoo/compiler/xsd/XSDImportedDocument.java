/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd;

import com.idyria.osi.ooxoo.compiler.CompilerException;


/**
 * @author Rtek
 * 
 */
public class XSDImportedDocument extends XSDocument {

	private XSDocument dereferencedDocument = null;

	private String prefix = null; // ! A possible prefix for documents

	private String nameSpace = null; // ! the nameSpace associated

	private boolean compile = false; // ! do we compile?

	private boolean fromPackage = false; // ! do we take from package

	public XSDImportedDocument() {

	}

	public XSDImportedDocument(String path,String base) throws CompilerException {
		super(path);
		this.setBaseLocation(base);
		try {
			resolve();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public XSDImportedDocument(String path) throws CompilerException {
		this(path,null);
	}

	
	
	
	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.engines.ZaxCResolvableDocument#resolve()
	 */
	@Override
	public void resolve() throws Exception {
		// First Resolve
		super.resolve();
		
		
		
	}

	/**
	 * @return Returns the dereferencedDocument.
	 */
	public XSDocument getDereferencedDocument() {
		return dereferencedDocument;
	}

	/**
	 * @param dereferencedDocument
	 *            The dereferencedDocument to set.
	 */
	public void setDereferencedDocument(XSDocument dereferencedDocument) {
		this.dereferencedDocument = dereferencedDocument;
	}

	/**
	 * @return Returns the nameSpace.
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	/**
	 * @param nameSpace
	 *            The nameSpace to set.
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	/**
	 * @return Returns the prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix
	 *            The prefix to set.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return Returns the compile.
	 */
	public boolean isCompile() {
		return compile;
	}

	/**
	 * @param compile
	 *            The compile to set.
	 */
	public void setCompile(boolean compile) {
		this.compile = compile;
		this.fromPackage = !compile;
	}

	/**
	 * @return Returns the fromPackage.
	 */
	public boolean isFromPackage() {
		return fromPackage;
	}

	/**
	 * @param fromPackage
	 *            The fromPackage to set.
	 */
	public void setFromPackage(boolean fromPackage) {
		this.fromPackage = fromPackage;
		this.compile = !fromPackage;
	}

}
