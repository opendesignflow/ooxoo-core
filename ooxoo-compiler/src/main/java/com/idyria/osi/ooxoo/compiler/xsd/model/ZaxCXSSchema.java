/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.compiler.xsd.XSDImportedDocument;
import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.LanguageBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.buffers.datatypes.TokenBuffer;


/**
 * <B>Represents an XSD Schema: </B><BR>
 * <BR>
 * 
 * 
 * 
 * TODO Support for annotations
 * 
 * @author Rtek
 * 
 */
public class ZaxCXSSchema extends ZaxCAbstractSelectMultipleTypesAndGroups {

	protected String attributeFormDefault = "unqualified";

	protected String elementFormDefault = "unqualified";

	/**
	 * TODO Block and final default
	 */
	protected List<String> blockDefault = null;

	protected List<String> finalDefault = null;

	/**
	 * The schema targetNamespace
	 */
	protected AnyURIBuffer targetNamespace = null;

	/**
	 * The version revision
	 */
	protected TokenBuffer version = null;

	/**
	 * xml:lang attribute
	 */
	protected LanguageBuffer lang = null;

	/**
	 * @see ZaxCImport
	 */
	protected List<ZaxCImport> imports = new LinkedList<ZaxCImport>();

	/**
	 * @see ZaxCInclude
	 */
	protected List<ZaxCInclude> includes =  new LinkedList<ZaxCInclude>();

	/**
	 * @see ZaxCRedefine
	 */
	protected List<ZaxCRedefine> redefines =  new LinkedList<ZaxCRedefine>();

	protected List<ZaxCElement> elements =  new LinkedList<ZaxCElement>();

	protected List<ZaxCAttribute> attributes =  new LinkedList<ZaxCAttribute>();

	/**
	 * the prefix/namespaces map. it's an extra not included in official schemas
	 */
	protected HashMap<String, String> nsmap = new HashMap<String, String>();

//	 namespace+compiled document to find referenced types
	protected HashMap<String, XSDImportedDocument> importMap = new HashMap<String, XSDImportedDocument>();
	
	
	protected String targetLanguage = "java";
	
	/**
	 * 
	 */
	public ZaxCXSSchema() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the attributeFormDefault
	 */
	public String getAttributeFormDefault() {
		return attributeFormDefault;
	}

	/**
	 * @param attributeFormDefault
	 *            the attributeFormDefault to set
	 * @throws SyntaxException
	 */
	public void setAttributeFormDefault(String attributeFormDefault)
			throws SyntaxException {
		if (attributeFormDefault == null || attributeFormDefault.length()==0)
			return;
		else if (!attributeFormDefault.equals("qualified")
				&& !attributeFormDefault.equals("unqualified"))
			throw new SyntaxException(
					"attribute @attributeFormDefault only accepts values : (qualified|unqualified)");

		this.attributeFormDefault = attributeFormDefault;
	}

	/**
	 * @return the finalDefault
	 */
	public List<String> getFinalDefault() {
		return finalDefault;
	}

	/**
	 * @return the id
	 */
	public IDBuffer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(IDBuffer id) {
		this.id = id;
	}

	/**
	 * @return the lang
	 */
	public LanguageBuffer getLang() {
		return lang;
	}

	/**
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(LanguageBuffer lang) {
		this.lang = lang;
	}

	/**
	 * @return the targetNamespace
	 */
	public AnyURIBuffer getTargetNamespace() {
		return targetNamespace;
	}

	/**
	 * @param targetNamespace
	 *            the targetNamespace to set
	 */
	public void setTargetNamespace(AnyURIBuffer targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	/**
	 * @return the version
	 */
	public TokenBuffer getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(TokenBuffer version) {
		this.version = version;
	}

	/**
	 * @return the blockDefault
	 */
	public List<String> getBlockDefault() {
		return blockDefault;
	}

	/**
	 * @return the elementFormDefault
	 */
	public String getElementFormDefault() {
		return elementFormDefault;
	}

	/**
	 * @param elementFormDefault
	 *            the elementFormDefault to set
	 * @throws SyntaxException
	 */
	public void setElementFormDefault(String elementFormDefault)
			throws SyntaxException {
		if (elementFormDefault == null)
			return;
		else if (!elementFormDefault.equals("qualified")
				&& !elementFormDefault.equals("unqualified"))
			throw new SyntaxException(
					"attribute @elementFormDefault only accepts values : (qualified|unqualified)");

		this.elementFormDefault = elementFormDefault;
	}

	/**
	 * @return Returns the attributes.
	 */
	public List<ZaxCAttribute> getAttributes() {
		if (attributes == null)
			attributes = new LinkedList<ZaxCAttribute>();
		return attributes;
	}

	/**
	 * @return the elements
	 */
	public List<ZaxCElement> getElements() {
		if (elements == null)
			elements = new LinkedList<ZaxCElement>();
		return elements;
	}

	/**
	 * @return the imports
	 */
	public List<ZaxCImport> getImports() {
		if (imports == null)
			imports = new LinkedList<ZaxCImport>();
		return imports;
	}

	/**
	 * @return the includes
	 */
	public List<ZaxCInclude> getIncludes() {
		if (includes == null)
			includes = new LinkedList<ZaxCInclude>();
		return includes;
	}

	/**
	 * @return the redefines
	 */
	public List<ZaxCRedefine> getRedefines() {
		if (redefines == null)
			redefines = new LinkedList<ZaxCRedefine>();
		return redefines;
	}

	
	/**
	 * @return the nsmap
	 */
	public HashMap<String, String> getNsmap() {
		return nsmap;
	}

	/**
	 * @param nsmap the nsmap to set
	 */
	public void setNsmap(HashMap<String, String> nsmap) {
		this.nsmap = nsmap;
	}

	/**
	 * @return the importMap
	 */
	public HashMap<String, XSDImportedDocument> getImportMap() {
		return importMap;
	}

	/**
	 * @param importMap the importMap to set
	 */
	public void setImportMap(HashMap<String, XSDImportedDocument> importMap) {
		this.importMap = importMap;
	}

	/**
	 * @return the targetLanguage
	 */
	public String getTargetLanguage() {
		return targetLanguage;
	}

	/**
	 * @param targetLanguage the targetLanguage to set
	 */
	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	
	
}
