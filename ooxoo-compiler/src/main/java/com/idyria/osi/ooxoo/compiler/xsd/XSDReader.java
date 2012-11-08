/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.idyria.osi.ooxoo.compiler.CompilerException;
import com.idyria.osi.ooxoo.compiler.CompilerUtils;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAll;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAny;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAnyAttribute;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAttribute;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAttributeGroup;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCChoice;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCComplexContent;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCComplexType;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCElement;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCGroup;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCSequence;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCSimpleContent;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCSimpleType;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCXSSchema;
import com.idyria.osi.ooxoo.core.buffers.datatypes.AnySimpleTypeBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.LanguageBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.buffers.datatypes.TokenBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Enumeration;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.FractionDigits;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Inclusive;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Length;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Pattern;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.TotalDigits;
import com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.WhiteSpace;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation.AppInfo;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation.Documentation;
import com.idyria.utils.java.logging.TeaLogging;

/**
 * @author Rtek
 * 
 */
public class XSDReader extends XSDocument {

	private boolean appendType = false;

	private XPath xp = null;

	private ZaxCXSSchema schema = new ZaxCXSSchema();

	/**
	 * Do we validate syntax?
	 */
	private boolean validate = false;

	/**
	 * Ignore for compilation?
	 */
	private boolean ignore = false;

	public XSDReader(String path) throws Exception {
		super(path);

		resolve();
		if (this.isResolved())
			initialize();

	}

	/**
	 * This method initializes the compilation by solving all include and import
	 * dependencies and registering namespaces
	 * 
	 * @throws XPathExpressionException
	 */
	private void initialize() throws XPathExpressionException {

		// get root
		// ---------------------------------------
		Element root = doc.getDocumentElement();

		// Prepare XPATH
		// ----------------
		XPathFactory xpf = XPathFactory.newInstance();
		xp = xpf.newXPath();
		xp.setNamespaceContext(new XSDNSContext());

		
		
		// Ignore compilation
		// --------------------------
		try {
		
			
			for (ZaxCAnnotation ann : this.fetchAnnotations(root, "")) {
				
				for (AppInfo ainfo : ann.getAppInfos()) {
					
					//-- Target Language ?
					Element targetLanguage = (Element) ainfo.getElement(
							"targetLanguage", "urn:idyria:utilites:java:ooxoo");
					if (targetLanguage != null) {
						this.schema.setTargetLanguage(targetLanguage.getTextContent());
					}
					
					//-- Do we have any ignore?
					Element targetPackage = (Element) ainfo.getElement(
							"targetPackage", "urn:idyria:utilites:java:ooxoo");
					if (targetPackage != null) {
						if (targetPackage.hasAttribute("ignore")) {
							ignore = true;
						}
					}
				}
			}
		} catch (SyntaxException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		// Compile imports
		// -------------------------------

		// List imports
		NodeList imports = (NodeList) xp.evaluate("./xs:import", root,
				XPathConstants.NODESET);
		for (int ii = 0; ii < imports.getLength(); ii++) {

			XSDImportedDocument dc;
			try {

				// Create Imported document structure
				dc = new XSDImportedDocument(((Element) imports.item(ii))
						.getAttribute("schemaLocation"), this.baseLocation);

				// The found target package
				String foundTargetPackage = null;

				if (dc == null) {
					dc = new XSDImportedDocument();
					dc.setNameSpace(((Element) imports.item(ii))
							.getAttribute("namespace"));
				}

				// dc.setResolved(true);

				// Record Imported document in this compiler structure
				this.importMap.put(((Element) imports.item(ii))
						.getAttribute("namespace"), dc);

				// Processing reserved to resolved documents
				// --------------------------------------------------
				if (dc != null && dc.getDoc() != null) {

					// Try to find sourcePackage informations from resolved
					// schema
					try {
						Element docelt = dc.getDoc().getDocumentElement();
						for (ZaxCAnnotation ann : this.fetchAnnotations(docelt,
								"")) {
							for (AppInfo ainfo : ann.getAppInfos()) {
								Element targetPackage = (Element) ainfo
										.getElement("targetPackage",
												"urn:idyria:utilites:java:ooxoo");
								if (targetPackage != null) {
									// Found source package
									TeaLogging
											.teaLogInfo("Imported document source package: "
													+ targetPackage
															.getTextContent());
									dc.setFromPackage(true);
									foundTargetPackage = targetPackage
											.getTextContent().replaceAll("\\s", "");

								}
							}
						}
					} catch (SyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					// Try to fetch some necessary inclusions like attribute
					// groups
					try {
						List<ZaxCAttributeGroup> glist = this
								.fetchAttributeGroups(dc.getDoc()
										.getDocumentElement(), "schema");
						TeaLogging.teaLogInfo("Imported attributegroups: "
								+ glist.size() + " from " + dc.getNameSpace());
						this.schema.getAttributeGroups().addAll(glist);
					} catch (SyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// If package is not found from resolved document, try to
				// resolve from local import
				// --------------------------------------------------
				if (foundTargetPackage == null) {

					try {
						List<ZaxCAnnotation> anns = this.fetchAnnotations(
								(Element) imports.item(ii), "");
						TeaLogging.teaLogInfo("Annotations under import: "
								+ anns.size());

						for (ZaxCAnnotation ann : anns) {
							for (AppInfo ainfo : ann.getAppInfos()) {
								Node targetPackage = ainfo.getElement(
										"targetPackage",
										"urn:idyria:utilites:java:ooxoo");
								if (targetPackage != null) {
									// Found source package
									TeaLogging
											.teaLogInfo("Imported document source package: "
													+ targetPackage
															.getTextContent());
									dc.setFromPackage(true);
									foundTargetPackage = targetPackage
											.getTextContent().replaceAll("\\s", "");
								}
							}
						}
					} catch (DOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Define source package
				if (foundTargetPackage != null) {
					dc.setFromPackage(true);
					dc.setSourcePackage(foundTargetPackage);
				}

			} catch (CompilerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Compile includes
		// -------------------------
		this.resolveIncludes(root);

		// register declared namespaces and adjust imported ones
		System.out.println("[CC] Registering xmlns declarations...");
		NamedNodeMap map = root.getAttributes();
		for (int i = 0; i < map.getLength(); i++) {

			Node att = map.item(i);
			String nodename = att.getNodeName();
			System.out.println("\t[CC] NS Attribute : " + nodename + " : "
					+ att.getTextContent());

			// Register base element attributes (namespaces and targetnamespace)
			if (nodename.equals("targetNamespace")) {
				this.nsmap.put("targetNamespace", att.getTextContent());
			} else if (nodename.equals("xmlns"))
				this.nsmap.put("xmlns", att.getTextContent());
			else if (nodename.startsWith("xmlns:")) {

				// register prefix into matching imported document (if it
				// exists)
				String prefix = nodename.split(":")[1];
				XSDImportedDocument dc = this.importMap.get(att
						.getTextContent());
				if (dc != null) {
					dc.setPrefix(nodename.split(":")[1]);
					TeaLogging.teaLogInfo("Associating prefix : " + prefix
							+ " to ns: " + att.getTextContent() + " (package: "
							+ dc.getSourcePackage() + ")");

				}

				// Register namespace+prefix
				this.nsmap.put(nodename.split(":")[1], att.getTextContent());
			}

		}

		// Add special xml prefix
		nsmap.put("xml", XSDReader.XML_NS);
		XSDImportedDocument xmldoc = new XSDImportedDocument();
		xmldoc.setFromPackage(true);
		xmldoc.setSourcePackage("org.w3.xml11.zaxb");
		importMap.put("xml", xmldoc);

		// if no xmlns, check root prefix is recorded
		if (this.nsmap.get("xmlns") == null && root.getPrefix() != null
				&& this.nsmap.get(root.getPrefix()) != null
				&& root.getNamespaceURI().equals(XSDReader.XSD_NS)) {
			this.nsmap.put("xmlns", XSDReader.XSD_NS);
		}

		// TODO Compile imports and define a new Imported Class source
		// or define a class source based on specific infos given in xml

	}

	private void resolveIncludes(Element root) {

		try {
			// List includes
			NodeList includes = (NodeList) xp.evaluate("./xs:include", root,
					XPathConstants.NODESET);
			for (int ii = 0; ii < includes.getLength(); ii++) {

				// Get Node
				Element include = (Element) includes.item(ii);

				// Get path
				String location = include.getAttribute("schemaLocation");

				// Resolve
				XSDIncludedDocument ires = new XSDIncludedDocument(location, this.baseLocation);
				ires.resolve();
				ires.setIncludeNode(include);

				// Replace
				ires.replaceDocument();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method compiles the provided document to output class files matching
	 * the types
	 * 
	 * @throws ZaxbException
	 * @throws XPathExpressionException
	 * @throws SyntaxException
	 * 
	 */
	public void compile() throws XPathExpressionException, SyntaxException {

		// Get root and go compilation
		Element root = doc.getDocumentElement();

		ZaxCXSSchema schema = this.readSchema(root);
		schema.setNsmap(this.nsmap);
		schema.setImportMap(this.importMap);
		// Output
	}

	// ---------------------------------------------------------------------

	private void fillBlockAndFinal() {

	}

	/**
	 * <B> Reads a schema element </B>
	 * 
	 * @param baseNode
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCXSSchema readSchema(Element baseNode) throws SyntaxException {

		// Create Object
		// --------------------------------
		String parentsName = "schema";

		/*
		 * TODO blockDefault && finalDefault attributeFormDefault = (qualified |
		 * unqualified) : unqualified blockDefault = (#all | List of (extension
		 * | restriction | substitution)) : '' elementFormDefault = (qualified |
		 * unqualified) : unqualified finalDefault = (#all | List of (extension
		 * | restriction | list | union)) : '' id = ID targetNamespace = anyURI
		 * version = token xml:lang = language
		 */
		schema.setAttributeFormDefault(baseNode
				.getAttribute("attributeFormDefault"));
		schema.setElementFormDefault(baseNode
				.getAttribute("elementFormDefault"));
		schema.setId(IDBuffer.parseID(baseNode.getAttribute("id")));
		schema.setTargetNamespace(new AnyURIBuffer(baseNode
				.getAttribute("targetNamespace")));
		schema.setVersion(new TokenBuffer(baseNode.getAttribute("version")));
		schema.setLang(new LanguageBuffer(baseNode.getAttributeNS(
				XSDReader.XML_NS, "lang")));

		// FIXME ((include | import | redefine )*
		// -------------------------------------------------

		// (simpleType | complexType | group | attributeGroup) | element |
		// attribute | notation)*
		// -------------------------------------------------
		schema.getSimpleTypes().addAll(
				this.fetchSimpleTypes(baseNode, parentsName));
		schema.getComplexTypes().addAll(
				this.fetchComplexTypes(baseNode, parentsName));
		schema.getGroups().addAll(this.fetchGroups(baseNode, parentsName));
		schema.getAttributeGroups().addAll(
				this.fetchAttributeGroups(baseNode, parentsName));
		schema.getElements().addAll(this.fetchElements(baseNode, parentsName));
		schema.getAttributes().addAll(
				this.fetchAttributes(baseNode, parentsName));

		// Fetch MultipleAnnotations
		// ------------------------------
		schema.getAnnotations().addAll(
				this.fetchAnnotations(baseNode, parentsName));

		return schema;

	}

	/**
	 * <B>Reads and returns an annotation</B>
	 * 
	 * @see ZaxCAnnotation
	 * @param annotation
	 * @param parentsName
	 * @return The read annotation or null if none is found
	 * @throws XPathExpressionException
	 * @throws SyntaxException
	 */
	private ZaxCAnnotation readAnnotation(Element annotation, String parentsName)
			throws XPathExpressionException, SyntaxException {

		// Create ZaxCAnnotation
		// ------------------------------------
		ZaxCAnnotation anno = new ZaxCAnnotation();

		// Get AppInfo as content + attributes
		// ----------------------
		NodeList appinfos = (NodeList) xp.evaluate("./xs:appinfo", annotation,
				XPathConstants.NODESET);

		for (int i = 0; i < appinfos.getLength(); i++) {

			Element appInfoElt = (Element) appinfos.item(i);
			// Get source
			ZaxCAnnotation.AppInfo appInfo = anno.new AppInfo();
			String source = (String) xp.evaluate("@source", appInfoElt,
					XPathConstants.STRING);
			if (source != null) {
				try {
					appInfo.setSource(new AnyURIBuffer(source));
				} catch (SyntaxException ex) {
					throw new SyntaxException(
							"The source attribute's syntax in appInfo [fetched from parent:"
									+ parentsName
									+ "] is not correct(need anyURI)");
				}
			}

			// Get content as Elements
			NodeList appsInfos = (NodeList) xp.evaluate("./*", appInfoElt,
					XPathConstants.NODESET);
			if (appsInfos != null && appsInfos.getLength() > 0) {
				for (int z = 0; z < appsInfos.getLength(); z++) {

					// Signal App info to compiler
					super.signalAppinfo(appsInfos.item(z), annotation
							.getParentNode());
					// Register
					appInfo.addElement(appsInfos.item(z));
				}
			}
			String content = (String) xp.evaluate("string(.)", appInfoElt,
					XPathConstants.STRING);
			if (content != null)
				appInfo.setContent(content);

			// Add appInfo to annotation
			anno.getAppInfos().add(appInfo);

		}

		// Get documenation
		// -----------------------------
		NodeList docs = (NodeList) xp.evaluate("./xs:documentation",
				annotation, XPathConstants.NODESET);

		for (int i = 0; i < docs.getLength(); i++) {

			Element docElt = (Element) docs.item(i);

			// Get source
			ZaxCAnnotation.Documentation doc = anno.new Documentation();
			String dsource = (String) xp.evaluate("@source", docElt,
					XPathConstants.STRING);
			if (dsource != null) {
				try {
					doc.setSource(new AnyURIBuffer(dsource));
				} catch (SyntaxException ex) {
					throw new SyntaxException(
							"The source attribute's syntax in Documentation [fetched from parent:"
									+ parentsName
									+ "] is not correct(need anyURI)");
				}
			}

			// Get xml:lang
			String lang = (String) xp.evaluate("@xml:lang", docElt,
					XPathConstants.STRING);
			if (lang != null)
				try {
					doc.setLang(new LanguageBuffer(lang));
				} catch (SyntaxException ex) {
					throw new SyntaxException(
							"The xml:lang attribute's syntax in Documentation [fetched from parent:"
									+ parentsName
									+ "] doest not represents a valid language code");
				}

			// Get content
			String dcontent = (String) xp.evaluate("string(.)", docElt,
					XPathConstants.STRING);

			// System.out.println("Go doc in annotation : " + dcontent);

			if (dcontent != null)
				doc.setContent(dcontent);

			// Add to annotation
			anno.getDocs().add(doc);
		}

		// Return
		return anno;

	}

	/**
	 * <B>Fetches a single annotation element</B>
	 * 
	 * @see XSDReader#readAnnotation(Element, String)
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCAnnotation fetchAnnotation(Element baseNode, String parentsName)
			throws SyntaxException {

		try {
			
			System.err.println("================== Annotation for "+parentsName+" =====================");
			
			// Fetch Annotation element from xpath
			// ----------------------------------------
			NodeList annotations = (NodeList) xp.evaluate("./xs:annotation",
					baseNode, XPathConstants.NODESET);

			if (annotations == null || annotations.getLength() == 0)
				return null;
			else if (annotations.getLength() > 1) {
				throw new SyntaxException(
						"Only one annotation is allowed in an annotable element [fetched from parent:"
								+ parentsName + "] ");
			} else {

				Element annotation = (Element) annotations.item(0);
				return this.readAnnotation(annotation, parentsName
						+ "#annotation");

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new SyntaxException(
					"XPath error while fetching annotation in [" + parentsName
							+ "] : " + e.toString());
		}

	}

	/**
	 * <B>Fetches multiple annotation elements</B>
	 * 
	 * @see XSDReader#readAnnotation(Element, String)
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCAnnotation> fetchAnnotations(Element baseNode,
			String parentsName) throws SyntaxException {

		try {

			List<ZaxCAnnotation> res = new LinkedList<ZaxCAnnotation>();

			// Fetch Annotation element from xpath
			// ----------------------------------------
			NodeList annotations = (NodeList) xp.evaluate("./xs:annotation",
					baseNode, XPathConstants.NODESET);

			if (annotations == null || annotations.getLength() == 0)
				return res;

			for (int i = 0; i < annotations.getLength(); i++) {
				res.add(this.readAnnotation((Element) annotations.item(i),
						parentsName + "#annotation(" + i + ")"));
			}

			return res;

		} catch (XPathExpressionException e) {
			throw new SyntaxException(
					"XPath error while fetching annotation in [" + parentsName
							+ "] : " + e.toString());
		}

	}

	/**
	 * This method checks that an element contains only one extension or
	 * restriction element
	 * 
	 * @return Element a restriction or extension element if found
	 */
	private Element checkRestrictionExtensionSyntax(Element baseNode,
			String parentname) throws SyntaxException {

		NodeList restriction = null;
		NodeList extension = null;
		try {
			restriction = (NodeList) this.xp.evaluate("./xs:restriction",
					baseNode, XPathConstants.NODESET);
			extension = (NodeList) this.xp.evaluate("./xs:extension", baseNode,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		if ((restriction != null && restriction.getLength() > 0
				&& extension != null && extension.getLength() > 0)
				|| (restriction != null && restriction.getLength() > 1)
				|| (extension != null && extension.getLength() > 1)) {
			throw new SyntaxException(
					"Type ["
							+ parentname
							+ "] MUST only contain one restriction OR extension element");
		} else if (restriction != null && restriction.getLength() == 1) {

			return (Element) restriction.item(0);

		} else if (extension != null && extension.getLength() == 1) {
			return (Element) extension.item(0);
		} else {
			return null;
		}
	}

	/**
	 * Analyses an element representing a complexType and returns a ZaxCType
	 * object<BR>
	 * 
	 * <P>
	 * <CODE>
	 * 
	 * <a name="element-complexType" id="element-complexType">&lt;complexType</a><br>
	 * &nbsp;&nbsp;abstract = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean">boolean</a>&nbsp;:&nbsp;false<br>
	 * &nbsp;&nbsp;block = (<var>#all</var> | List of (<var>extension</var> |
	 * <var>restriction</var>)) <br>
	 * &nbsp;&nbsp;final = (<var>#all</var> | List of (<var>extension</var> |
	 * <var>restriction</var>)) <br>
	 * &nbsp;&nbsp;id = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID">ID</a><br>
	 * &nbsp;&nbsp;mixed = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean">boolean</a>&nbsp;:&nbsp;false<br>
	 * &nbsp;&nbsp;name = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#NCName">NCName</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, (<a class="eltref"
	 * href="#element-simpleContent">simpleContent</a> | <a class="eltref"
	 * href="#element-complexContent">complexContent</a> | ((<a class="eltref"
	 * href="#element-group">group</a> | <a class="eltref"
	 * href="#element-all">all</a> | <a class="eltref"
	 * href="#element-choice">choice</a> | <a class="eltref"
	 * href="#element-sequence">sequence</a>)?, ((<a class="eltref"
	 * href="#element-attribute">attribute</a> | <a class="eltref"
	 * href="#element-attributeGroup">attributeGroup</a>)*, <a class="eltref"
	 * href="#element-anyAttribute">anyAttribute</a>?))))<br>
	 * &lt;/complexType&gt;
	 * 
	 * </CODE>
	 * </P>
	 * 
	 * @param cplx
	 * @return ZaxCType a new type if provided type is null, or the provided
	 *         type
	 * @throws ZaxbException
	 * @throws SyntaxException
	 */
	private ZaxCComplexType readComplexType(Element cplx, String parentsName)
			throws SyntaxException {

		if (cplx == null || !cplx.getLocalName().equals("complexType"))
			return null;

		// Create type
		// ------------------------
		ZaxCComplexType type = new ZaxCComplexType();

		// Get type name && id
		// ------------------------
		// System.out.println("[CC] Reading ComplexType : "+
		// cplx.getAttribute("name"));

		String name = cplx.getAttribute("name");
		if (name.length() > 0) {
			type.setName(CompilerUtils.createFromString(NCNameBuffer.class,name));
		} else
			// throw new SyntaxException(
			// "name Attribute in a complextype is not defined [in "
			// + parentsName + "]");
			type.setName(CompilerUtils.createFromString(NCNameBuffer.class,parentsName));

		// Adapt name
		if (appendType && type.getName() != null
				&& !type.getName().toString().endsWith("jkjef_eiijfe;,"))
			type.setName(CompilerUtils.createFromString(NCNameBuffer.class,type.getName().toString() + "Type"));

		if (cplx.getAttribute("id").length() > 0)
			type.setId(new IDBuffer(cplx.getAttribute("id")));

		parentsName = parentsName + "#complexType@" + name;

		// Get abstract and mixed
		// ----------------------------------
		if (cplx.getAttribute("mixed").equals("true"))
			type.setMixed(true);
		else if (cplx.getAttribute("mixed").equals("false"))
			type.setMixed(false);
		else if (cplx.getAttribute("mixed").length() > 0) {
//			throw new SyntaxException("mixed Attribute in complextype ["
//					+ parentsName + "] value is not (true|false)");
		}
		if (cplx.getAttribute("abstract").equals("true"))
			type.setAbstract(true);
		else if (cplx.getAttribute("abstract").equals("false"))
			type.setAbstract(false);
		else if (cplx.getAttribute("abstract").length() > 0)
			throw new SyntaxException("abstract Attribute in complextype ["
					+ parentsName + "] value is not (true|false)");

		// Get Final and block
		// ----------------------------------------------
		String Final = cplx.getAttribute("final");
		if (Final.equals("#all")) {
			type.getFinal().add("#all");
		} else if (Final.length() > 0) {

			String[] values = Final.split(" ");
			for (String val : values) {
				if (val.equals("restriction"))
					type.getFinal().add("restriction");
				else if (val.equals("extension"))
					type.getFinal().add("extension");
				else
					throw new SyntaxException(
							"final Attribute in complextype ["
									+ parentsName
									+ "] value is not (#all | List of (extension | restriction))");
			}
		}
		String block = cplx.getAttribute("block");
		if (block.equals("#all")) {
			type.getBlock().add("#all");
		} else if (block.length() > 0) {
			String[] values = block.split(" ");
			for (String val : values) {
				if (val.equals("restriction"))
					type.getBlock().add("restriction");
				else if (val.equals("extension"))
					type.getBlock().add("extension");
				else
					throw new SyntaxException(
							"block Attribute in complextype ["
									+ parentsName
									+ "] value is not (#all | List of (extension | restriction))");
			}
		}

		// NOW Get on with Content
		// Content: (annotation?, (simpleContent | complexContent | ((group |
		// all | choice | sequence)?, ((attribute | attributeGroup)*,
		// anyAttribute?))))
		// -------------------------------------

		// Fetch Annotation
		// ---------------------------------
		type.setAnnotation(this.fetchAnnotation(cplx, parentsName));

		// SimpleContent || ComplexContent ?
		// ------------------------------------------------
		NodeList isComplex = null;
		NodeList isSimple = null;
		try {
			isComplex = (NodeList) this.xp.evaluate("./xs:complexContent",
					cplx, XPathConstants.NODESET);
			isSimple = (NodeList) this.xp.evaluate("./xs:simpleContent", cplx,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		if ((isComplex != null && isComplex.getLength() > 0 && isSimple != null && isSimple
				.getLength() > 0)
				|| (isComplex != null && isComplex.getLength() > 1)
				|| (isSimple != null && isSimple.getLength() > 1)) {

			throw new SyntaxException(
					"complexType ["
							+ parentsName
							+ "] MUST NOT contain more than one complexContent OR simpleContent");

		} else if (isSimple != null && isSimple.getLength() == 1) {

			// Process Simple Content
			// Content: (annotation?, (restriction | extension))
			// --------------------- #### ------------------------------
			Element simpleContentElt = (Element) isSimple.item(0);
			ZaxCSimpleContent simpleContent = new ZaxCSimpleContent();

			// Check simpleContent restriction | extension syntax are
			// respected
			// ---------------------------------
			Element restOrext = this.checkRestrictionExtensionSyntax(
					simpleContentElt, type.getName().toString());

			// Fetch Annotation && id
			// ---------------------------------
			type.setAnnotation(this.fetchAnnotation(simpleContentElt,
					parentsName + "#simpleContent"));

			// id :)
			simpleContent
					.setId(IDBuffer.parseID(simpleContentElt.getAttribute("id")));

			// Extension || restriction
			// --------------------------------
			if (restOrext != null
					&& restOrext.getLocalName().equals("restriction")) {

				// Get on with restriction
				// ---------------------------
				this.readSimpleContentRestriction(restOrext, simpleContent,
						parentsName);

			} else if (restOrext != null
					&& restOrext.getLocalName().equals("extension")) {

				// Get on with extension
				// ---------------------------
				this.readSimpleContentExtension(restOrext, simpleContent,
						parentsName + "#simpleContent");

			} else {
				throw new SyntaxException("simpleContent [in complexType : "
						+ parentsName
						+ "] MUST contain one extension OR restriction");
			}

			// Add to complexType
			type.setSimpleContent(simpleContent);

		} else if (isComplex != null && isComplex.getLength() == 1) {

			// Process Complex Content
			// Content: (annotation?, (restriction | extension))
			// --------------------- #### ------------------------------
			Element complexContentElt = (Element) isComplex.item(0);
			ZaxCComplexContent complexContent = new ZaxCComplexContent();

			// Check simpleContent restriction | extension syntax are
			// respected
			// ---------------------------------
			Element restOrext = this.checkRestrictionExtensionSyntax(
					complexContentElt, type.getName().toString());

			// Fetch Annotation && id && mixed
			// ---------------------------------
			type.setAnnotation(this.fetchAnnotation(complexContentElt,
					parentsName + "#complexContent"));

			// id :)
			complexContent.setId(IDBuffer.parseID(complexContentElt
					.getAttribute("id")));

			// mixed :)
			complexContent.setMixed(Boolean.parseBoolean(complexContentElt
					.getAttribute("mixed")));

			// Extension || restriction
			// --------------------------------
			if (restOrext != null
					&& restOrext.getLocalName().equals("restriction")) {

				// Get on with restriction
				// ---------------------------
				ZaxCComplexContent.restrictionOrExtension roe = this
						.readComplexContentExtensionOrRestriction(restOrext,
								complexContent, parentsName + "[" + name
										+ "]#complexContent");
				complexContent.setRestriction(roe);

			} else if (restOrext != null
					&& restOrext.getLocalName().equals("extension")) {

				// Get on with extension
				// ---------------------------
				ZaxCComplexContent.restrictionOrExtension roe = this
						.readComplexContentExtensionOrRestriction(restOrext,
								complexContent, parentsName + "[name]"
										+ "#complexContent");
				complexContent.setExtension(roe);

			} else {
				throw new SyntaxException("complexContent [in complexType : "
						+ parentsName
						+ "] MUST contain one extension OR restriction");
			}

			// Add to complexType
			type.setComplexContent(complexContent);

		} else {

			// Get on with (group | all | choice | sequence)?, ((attribute |
			// attributeGroup)*, anyAttribute?)
			type.setGroup(this.fetchGroup(cplx, parentsName));
			type.setAll(this.fetchAll(cplx, parentsName));
			type.setChoice(this.fetchChoice(cplx, parentsName));
			type.setSequence(this.fetchSequence(cplx, parentsName));

			type.getAttributes()
					.addAll(this.fetchAttributes(cplx, parentsName));
			type.getAttributeGroups().addAll(
					this.fetchAttributeGroups(cplx, parentsName));
			type.setAnyAttribute(this.fetchAnyAttribute(cplx, parentsName));
		}

		// Return :)
		return type;

	}

	/**
	 * <B>Fetches multiple complexType definitions </B>
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws ZaxbException
	 * @throws SyntaxException
	 */
	private List<ZaxCComplexType> fetchComplexTypes(Element baseNode,
			String parentsName) throws SyntaxException {

		List<ZaxCComplexType> res = new LinkedList<ZaxCComplexType>();

		try {
			// Get NodeList
			NodeList ctypes = (NodeList) xp.evaluate("./xs:complexType",
					baseNode, XPathConstants.NODESET);

			if (ctypes == null || ctypes.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < ctypes.getLength(); i++) {

				res.add(this.readComplexType((Element) ctypes.item(i),
						parentsName + "#complexType(" + i + ")"));

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return res;

	}

	/**
	 * <B>Analyses a simpleType element and returns an associated
	 * ZaxCSimpleType</B>: <BR>
	 * <BR>
	 * 
	 * <a name="element-simpleType" id="element-simpleType">&lt;simpleType</a><br>
	 * &nbsp;&nbsp;final = (<var>#all</var> | List of (<var>list</var> |
	 * <var>union</var> | <var>restriction</var>)) <br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;name = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#NCName"
	 * >NCName</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, (<a class="eltref"
	 * href="#element-restriction">restriction</a> | <a class="eltref"
	 * href="#element-list">list</a> | <a class="eltref"
	 * href="#element-union">union</a>))<br>
	 * &lt;/simpleType&gt;
	 * 
	 * @param simple
	 * @return
	 * @throws ZaxbException
	 * @throws SyntaxException
	 */
	private ZaxCSimpleType readSimpleType(Element simpleElt, String parentsName)
			throws SyntaxException {

		if (simpleElt == null || !simpleElt.getLocalName().equals("simpleType"))
			return null;

		// Create type
		ZaxCSimpleType type = new ZaxCSimpleType();

		// Get type name && id
		// ------------------------
		System.out.println("[CC] Reading simpleType : "
				+ simpleElt.getAttribute("name"));
		String name = simpleElt.getAttribute("name");
		if (name.length() > 0) {
			type.setName(CompilerUtils.createFromString(NCNameBuffer.class,name));
		}

		// Adapt
		if (appendType && type.getName() != null
				&& !type.getName().toString().endsWith("jkfekfl-_,;ef"))
			type.setName(CompilerUtils.createFromString(NCNameBuffer.class,type.getName().toString() + "Type"));

		if (simpleElt.getAttribute("id").length() > 0)
			type.setId(new IDBuffer(simpleElt.getAttribute("id")));

		parentsName = parentsName + "@" + name;

		// Get Final
		// ----------------------------------------------
		String Final = simpleElt.getAttribute("final");
		if (Final.equals("#all")) {
			type.getFinal().add("#all");
		} else if (Final.length() > 0) {

			String[] values = Final.split(" ");
			for (String val : values) {
				if (val.equals("restriction"))
					type.getFinal().add("restriction");
				else if (val.equals("extension"))
					type.getFinal().add("extension");
				else
					throw new SyntaxException(
							"final Attribute in simpleType ["
									+ parentsName
									+ "] value MUST be matching (#all | List of (extension | restriction))");
			}
		}

		// Get on with : (restriction | list | union)
		// ---------------------------------------------
		type.setRestriction(this.fetchSimpleTypeRestriction(simpleElt, type,
				parentsName));
		type.setList(this.fetchSimpleTypeList(simpleElt, type, parentsName));
		type.setUnion(this.fetchSimpleTypeUnion(simpleElt, type, parentsName));

		return type;

	}

	/**
	 * <B> Fetches a single simpleType element<B><BR>
	 * 
	 * @see XSDReader#readSimpleType(Element, String)
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCSimpleType fetchSimpleType(Element baseNode, String parentsName)
			throws SyntaxException {

		ZaxCSimpleType res = null;

		try {
			// Get NodeList
			NodeList stypes = (NodeList) xp.evaluate("./xs:simpleType",
					baseNode, XPathConstants.NODESET);

			if (stypes == null || stypes.getLength() == 0)
				return null;

			if (stypes.getLength() > 1)
				throw new SyntaxException(
						"only ONE simpleType child is allowed [in "
								+ parentsName + "]");

			// Get Element and prepare new type
			Element stypeElt = (Element) stypes.item(0);
			res = this.readSimpleType(stypeElt, parentsName);

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;
	}

	/**
	 * <B>Fetches multiple SimpleTypes</B>
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCSimpleType> fetchSimpleTypes(Element baseNode,
			String parentsName) throws SyntaxException {

		List<ZaxCSimpleType> res = new LinkedList<ZaxCSimpleType>();

		try {
			// Get NodeList
			NodeList stypes = (NodeList) xp.evaluate("./xs:simpleType",
					baseNode, XPathConstants.NODESET);

			if (stypes == null || stypes.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < stypes.getLength(); i++) {

				res.add(this.readSimpleType((Element) stypes.item(i),
						parentsName + "#simpleType(" + i + ")"));

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * <B>Fetches a restriction element nested in a simpleType definition : </B><BR>
	 * <BR>
	 * 
	 * <a name="element-restriction"
	 * id="element-restriction">&lt;restriction</a><br>
	 * &nbsp;&nbsp;base = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#QName"
	 * >QName</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, (<a class="eltref"
	 * href="#element-simpleType">simpleType</a>?, (<a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-minExclusive"
	 * >minExclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-minInclusive"
	 * >minInclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-maxExclusive"
	 * >maxExclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-maxInclusive"
	 * >maxInclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-totalDigits"
	 * >totalDigits</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-fractionDigits"
	 * >fractionDigits</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-length"
	 * >length</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-minLength"
	 * >minLength</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-maxLength"
	 * >maxLength</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-enumeration"
	 * >enumeration</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-whiteSpace"
	 * >whiteSpace</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-pattern"
	 * >pattern</a>)*))<br>
	 * &lt;/restriction&gt;
	 * 
	 * @param baseNode
	 * @param simpleType
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCSimpleType.restriction fetchSimpleTypeRestriction(
			Element baseNode, ZaxCSimpleType simpleType, String parentsName)
			throws SyntaxException {

		ZaxCSimpleType.restriction restriction = null;

		try {
			// Get NodeList
			NodeList restrictions = (NodeList) xp.evaluate("./xs:restriction",
					baseNode, XPathConstants.NODESET);

			if (restrictions == null || restrictions.getLength() == 0)
				return null;

			if (restrictions.getLength() > 1)
				throw new SyntaxException(
						"only ONE restriction child is allowed [in "
								+ parentsName + "]");

			// Get Element and prepare new type
			Element restrictionElt = (Element) restrictions.item(0);
			restriction = simpleType.new restriction();

			System.out
					.println("[CC] Try to read restriction in simpleType nested in ["
							+ parentsName + "]: ");

			// Get base and id
			// ------------------------------------
			String base = restrictionElt.getAttribute("base");
			if (base.length() == 0) {
				// String str = "";
				// try {
				// str = (String)
				// this.xp.evaluate("ancestor::xs:simpleType[@name and
				// position()=1]/@name", baseNode,XPathConstants.STRING);
				// } catch (XPathExpressionException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// base = str;
				base = simpleType.getName().toString();
			}
			// if (base.length() == 0)
			// throw new SyntaxException("restriction element in ["
			// + parentsName
			// + "] simpleType MUST have a @base attribute");
			// else

			restriction.setBase(CompilerUtils.createFromString(QNameBuffer.class,base));

			// id :)
			restriction.setId(IDBuffer.parseID(restrictionElt.getAttribute("id")));

			parentsName = parentsName + "#restriction@" + base;

			// Fetch Annotation
			// --------------------------
			restriction.setAnnotation(this.fetchAnnotation(restrictionElt,
					parentsName));

			/*
			 * (simpleType?, (minExclusive | minInclusive | maxExclusive |
			 * maxInclusive | totalDigits | fractionDigits | length | minLength
			 * | maxLength | enumeration | whiteSpace | pattern))? =>
			 * SimpleType? and/or others
			 * ------------------------------------------------
			 */
			restriction.setMinExclusive(this.fetchMinExclusive(restrictionElt,
					parentsName));
			restriction.setMinInclusive(this.fetchMinInclusive(restrictionElt,
					parentsName));
			restriction.setMaxExclusive(this.fetchMaxExclusive(restrictionElt,
					parentsName));
			restriction.setMaxInclusive(this.fetchMaxInclusive(restrictionElt,
					parentsName));
			restriction.setTotalDigits(this.fetchTotalDigits(restrictionElt,
					parentsName));
			restriction.setFractionDigits(this.fetchFractionDigits(
					restrictionElt, parentsName));
			restriction
					.setLength(this.fetchLength(restrictionElt, parentsName));
			restriction.setMinLength(this.fetchMinLength(restrictionElt,
					parentsName));
			restriction.setMaxLength(this.fetchMaxLength(restrictionElt,
					parentsName));
			restriction.getEnumeration().addAll(
					(this.fetchEnumeration(restrictionElt, parentsName)));
			restriction.setWhiteSpace(this.fetchWhiteSpace(restrictionElt,
					parentsName));
			restriction.setPattern(this.fetchPattern(restrictionElt,
					parentsName));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			Runtime.getRuntime().exit(2);
			restriction = null;

		}

		return restriction;
	}

	/**
	 * <B>Fetches a single list element nested in a simpleType : <B><BR>
	 * <BR>
	 * 
	 * <a name="element-list" id="element-list">&lt;list</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;itemType = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#QName"
	 * >QName</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, <a class="eltref"
	 * href="#element-simpleType">simpleType</a>?)<br>
	 * &lt;/list&gt;
	 * 
	 * @param baseNode
	 * @param simpleType
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCSimpleType.list fetchSimpleTypeList(Element baseNode,
			ZaxCSimpleType simpleType, String parentsName)
			throws SyntaxException {

		ZaxCSimpleType.list list = null;

		try {
			// Get NodeList
			NodeList lists = (NodeList) xp.evaluate("./xs:list", baseNode,
					XPathConstants.NODESET);

			if (lists == null || lists.getLength() == 0)
				return null;

			if (lists.getLength() > 1)
				throw new SyntaxException("only ONE list child is allowed [in "
						+ parentsName + "]");

			// Get Element and prepare new type
			Element listElt = (Element) lists.item(0);
			list = simpleType.new list();

			System.out.println("[CC] Try to read list nested in ["
					+ parentsName + "] ");

			// Get itemType and id
			// ------------------------------------
			list
					.setItemType(CompilerUtils.createFromString(QNameBuffer.class,listElt
							.getAttribute("itemType")));

			// id :)
			list.setId(IDBuffer.parseID(listElt.getAttribute("id")));

			parentsName = parentsName
					+ "#list@"
					+ (list.getItemType() != null ? list.getItemType()
							.toString() : "anonymous");

			// Fetch Annotation
			// --------------------------
			list.setAnnotation(this.fetchAnnotation(listElt, parentsName));

			// Fetch SimpleType for anonymous declaration
			// --------------------------------------------
			list.setSimpleType(this.fetchSimpleType(listElt, parentsName));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			list = null;
		}

		return list;

	}

	/**
	 * <B>Fetches a single union element nested in a simpleType :<B><BR>
	 * <BR>
	 * 
	 * @see ZaxCSimpleType.union
	 * @param baseNode
	 * @param simpleType
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCSimpleType.union fetchSimpleTypeUnion(Element baseNode,
			ZaxCSimpleType simpleType, String parentsName)
			throws SyntaxException {

		ZaxCSimpleType.union union = null;

		try {
			// Get NodeList
			NodeList unions = (NodeList) xp.evaluate("./xs:union", baseNode,
					XPathConstants.NODESET);

			if (unions == null || unions.getLength() == 0)
				return null;

			if (unions.getLength() > 1)
				throw new SyntaxException(
						"only ONE union child is allowed [in " + parentsName
								+ "]");

			// Get Element and prepare new type
			Element unionElt = (Element) unions.item(0);
			union = simpleType.new union();

			System.out.println("[CC] Try to read union nested in ["
					+ parentsName + "] ");

			// Get memberTypes and id
			// ------------------------------------
			String memberTypes = unionElt.getAttribute("memberTypes");
			if (memberTypes.length() > 0) {
				String[] arr = memberTypes.split(" ");
				for (String val : arr) {
					union.getMemberTypes().add(CompilerUtils.createFromString(QNameBuffer.class,val));
				}
			}
			// id :)
			union.setId(IDBuffer.parseID(unionElt.getAttribute("id")));

			parentsName = parentsName + "#union";

			// Fetch Annotation
			// --------------------------
			union.setAnnotation(this.fetchAnnotation(unionElt, parentsName));

			// Fetch SimpleTypes
			// --------------------------------------------
			union.getSimpleTypes().addAll(
					this.fetchSimpleTypes(unionElt, parentsName));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			union = null;
		}

		return union;

	}

	/**
	 * <B>Reads a restriction nested in a simpleContent element</B><br>
	 * <br>
	 * <a name="element-simpleContent..restriction"
	 * id="element-simpleContent..restriction">&lt;restriction</a><br>
	 * &nbsp;&nbsp;<b>base</b> = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#QName"
	 * >QName</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, (<a class="eltref"
	 * href="#element-simpleType">simpleType</a>?, (<a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-minExclusive"
	 * >minExclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-minInclusive"
	 * >minInclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-maxExclusive"
	 * >maxExclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-maxInclusive"
	 * >maxInclusive</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-totalDigits"
	 * >totalDigits</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-fractionDigits"
	 * >fractionDigits</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-length"
	 * >length</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-minLength"
	 * >minLength</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-maxLength"
	 * >maxLength</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-enumeration"
	 * >enumeration</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-whiteSpace"
	 * >whiteSpace</a> | <a class="eltref" href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#element-pattern"
	 * >pattern</a>)*)?, ((<a class="eltref"
	 * href="#element-attribute">attribute</a> | <a class="eltref"
	 * href="#element-attributeGroup">attributeGroup</a>)*, <a class="eltref"
	 * href="#element-anyAttribute">anyAttribute</a>?))<br>
	 * &lt;/restriction&gt;
	 * 
	 * @param restriction
	 * @param base
	 * @throws SyntaxException
	 */
	private void readSimpleContentRestriction(Element restrictionElt,
			ZaxCSimpleContent simpleContent, String parentsName)
			throws SyntaxException {

		System.out
				.println("[CC] Try to read restriction in simpleContent nested in ["
						+ parentsName + "]: ");

		// Create restriction
		// ---------------------------
		ZaxCSimpleContent.restriction restriction = simpleContent.new restriction();

		// Get base and id
		// ------------------------------------
		String base = restrictionElt.getAttribute("base");
		if (base.length() == 0) {
			// Look for a parent with a base attribute
			String str = "";
			try {
				str = (String) this.xp.evaluate(
						"ancestor::*[@name and position()=1]/@name",
						restrictionElt, XPathConstants.STRING);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			base = str;
		}

		if (base.length() == 0)
			throw new SyntaxException("restriction element in [" + parentsName
					+ "] simpleContent MUST have a @base attribute");
		else
			restriction.setBase(CompilerUtils.createFromString(QNameBuffer.class,base));

		// Adapt name
		if (appendType && restriction.getBase() != null
				&& !restriction.getBase().toString().endsWith("jkfekfl-_,;ef"))
			restriction.setBase(CompilerUtils.createFromString(QNameBuffer.class,restriction.getBase()
					.toString()
					+ "Type"));

		// id :)
		restriction.setId(IDBuffer.parseID(restrictionElt.getAttribute("id")));

		// Fetch Annotation
		// --------------------------
		restriction.setAnnotation(this.fetchAnnotation(restrictionElt,
				parentsName + "#restriction"));

		/*
		 * (simpleType?, (minExclusive | minInclusive | maxExclusive |
		 * maxInclusive | totalDigits | fractionDigits | length | minLength |
		 * maxLength | enumeration | whiteSpace | pattern))? => SimpleType?
		 * and/or others ------------------------------------------------
		 */
		restriction.setMinExclusive(this.fetchMinExclusive(restrictionElt,
				parentsName + "#restriction"));
		restriction.setMinInclusive(this.fetchMinInclusive(restrictionElt,
				parentsName + "#restriction"));
		restriction.setMaxExclusive(this.fetchMaxExclusive(restrictionElt,
				parentsName + "#restriction"));
		restriction.setMaxInclusive(this.fetchMaxInclusive(restrictionElt,
				parentsName + "#restriction"));
		restriction.setTotalDigits(this.fetchTotalDigits(restrictionElt,
				parentsName + "#restriction"));
		restriction.setFractionDigits(this.fetchFractionDigits(restrictionElt,
				parentsName + "#restriction"));
		restriction.setLength(this.fetchLength(restrictionElt, parentsName
				+ "#restriction"));
		restriction.setMinLength(this.fetchMinLength(restrictionElt,
				parentsName + "#restriction"));
		restriction.setMaxLength(this.fetchMaxLength(restrictionElt,
				parentsName + "#restriction"));
		restriction.getEnumeration().addAll(
				(this.fetchEnumeration(restrictionElt, parentsName
						+ "#restriction")));
		restriction.setWhiteSpace(this.fetchWhiteSpace(restrictionElt,
				parentsName + "#restriction"));
		restriction.setPattern(this.fetchPattern(restrictionElt, parentsName
				+ "#restriction"));

		// Get children elements ((attribute | attributeGroup)*, anyAttribute?)
		// ------------------------------------------------------------
		restriction.getAttributeGroups().addAll(
				this.fetchAttributeGroups(restrictionElt, parentsName
						+ "#restriction"));
		restriction.getAttributes().addAll(
				this.fetchAttributes(restrictionElt, parentsName
						+ "#restriction"));
		restriction.setAnyAttribute(this.fetchAnyAttribute(restrictionElt,
				parentsName + "#restriction"));
		restriction.validateSchema(parentsName + "#restriction", false);

		simpleContent.setRestriction(restriction);

	}

	/**
	 * <B> Reads an extension nested in a simpleContent Element</B> <BR>
	 * <BR>
	 * 
	 * <a name="element-simpleContent..extension"
	 * id="element-simpleContent..extension">&lt;extension</a><br>
	 * &nbsp;&nbsp;<b>base</b> = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#QName"
	 * >QName</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, ((<a class="eltref"
	 * href="#element-attribute">attribute</a> | <a class="eltref"
	 * href="#element-attributeGroup">attributeGroup</a>)*, <a class="eltref"
	 * href="#element-anyAttribute">anyAttribute</a>?))<br>
	 * &lt;/extension&gt;
	 * 
	 * 
	 * @param restrictionElt
	 * @param simpleContent
	 * @param parentsName
	 * @throws SyntaxException
	 */
	private void readSimpleContentExtension(Element extensionElt,
			ZaxCSimpleContent simpleContent, String parentsName)
			throws SyntaxException {

		System.out
				.println("[CC] Try to read extension in simpleContent nested in ["
						+ parentsName + "]: ");

		// Create extension
		// ---------------------------
		ZaxCSimpleContent.extension extension = simpleContent.new extension();

		// Get base and id
		// ------------------------------------
		String base = extensionElt.getAttribute("base");
		if (base.length() == 0) {
			// Look for a parent with a base attribute
			String str = "";
			try {
				str = (String) this.xp.evaluate(
						"ancestor::*[@name and position()=1]/@name",
						extensionElt, XPathConstants.STRING);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			base = str;
		}

		if (base.length() == 0)
			throw new SyntaxException("extension element in [" + parentsName
					+ "] simpleContent MUST have a @base attribute");
		else
			extension.setBase(CompilerUtils.createFromString(QNameBuffer.class,base));

		// Adapt name
		if (appendType && extension.getBase() != null
				&& !extension.getBase().toString().endsWith("jkfekfl-_,;ef"))
			extension.setBase(CompilerUtils.createFromString(QNameBuffer.class,extension.getBase().toString()
					+ "Type"));

		// id :)
		extension.setId(IDBuffer.parseID(extensionElt.getAttribute("id")));

		// Fetch Annotation
		// --------------------------
		extension.setAnnotation(this.fetchAnnotation(extensionElt, parentsName
				+ "#extension"));

		// Get children elements ((attribute | attributeGroup)*, anyAttribute?)
		// && validate
		// ----------------------------------------------------------------------
		extension.getAttributeGroups().addAll(
				this.fetchAttributeGroups(extensionElt, parentsName
						+ "#restriction"));
		extension.getAttributes().addAll(
				this
						.fetchAttributes(extensionElt, parentsName
								+ "#restriction"));
		extension.setAnyAttribute(this.fetchAnyAttribute(extensionElt,
				parentsName + "#restriction"));
		extension.validateSchema(parentsName + "#restriction", false);

		simpleContent.setExtension(extension);

	}

	private ZaxCComplexContent.restrictionOrExtension readComplexContentExtensionOrRestriction(
			Element restOrExtElt, ZaxCComplexContent complexContent,
			String parentsName) throws SyntaxException {

		// Create return element but do not add it because we don"t know wether
		// it is an extension or restriction
		ZaxCComplexContent.restrictionOrExtension restorext = complexContent.new restrictionOrExtension();

		// Get base and id
		// ------------------------------------
		String base = restOrExtElt.getAttribute("base");
		if (base.length() == 0)
			throw new SyntaxException(restOrExtElt.getLocalName()
					+ " element in [" + parentsName
					+ "] complexContent MUST have a @base attribute");
		else
			restorext.setBase(CompilerUtils.createFromString(QNameBuffer.class,base));

		// Adapt name
		if (appendType && restorext.getBase() != null
				&& !restorext.getBase().toString().endsWith("jkfekfl-_,;ef"))
			restorext.setBase(CompilerUtils.createFromString(QNameBuffer.class,restorext.getBase().toString()
					+ "Type"));

		// id :)
		restorext.setId(IDBuffer.parseID(restOrExtElt.getAttribute("id")));

		// Get (group | all | choice | sequence)?
		// --------------------------------------------
		restorext.setGroup(this.fetchGroup(restOrExtElt, parentsName + "#"
				+ restOrExtElt.getLocalName()));
		restorext.setAll(this.fetchAll(restOrExtElt, parentsName + "#"
				+ restOrExtElt.getLocalName()));
		restorext.setChoice(this.fetchChoice(restOrExtElt, parentsName + "#"
				+ restOrExtElt.getLocalName()));
		restorext.setSequence(this.fetchSequence(restOrExtElt, parentsName
				+ "#" + restOrExtElt.getLocalName()));

		// Get (attribute | attributeGroup)*, anyAttribute?)
		// -----------------------------------------------------
		restorext.getAttributeGroups().addAll(
				this.fetchAttributeGroups(restOrExtElt, parentsName + "#"
						+ restOrExtElt.getLocalName()));
		restorext.getAttributes().addAll(
				this.fetchAttributes(restOrExtElt, parentsName + "#"
						+ restOrExtElt.getLocalName()));
		restorext.setAnyAttribute(this.fetchAnyAttribute(restOrExtElt,
				parentsName + "#" + restOrExtElt.getLocalName()));
		restorext.validateSchema(parentsName + "#"
				+ restOrExtElt.getLocalName(), false);

		return restorext;

	}

	/** @name Rrestriction constraining facets Handling methods */
	/** @{ */

	/**
	 * Searches in the given element for a MinExclusive element
	 * 
	 * @throws SyntaxException
	 */
	private Inclusive fetchMinExclusive(Element baseNode, String parentsName)
			throws SyntaxException {

		Inclusive res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:minExclusive[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new Inclusive();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(new AnySimpleTypeBuffer(node.getAttribute("value")));
			if (res.getValue().toString().length() == 0)
				throw new SyntaxException("MinExclusive facet in ["
						+ parentsName
						+ "] MUST have the value attribute defined");

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	/**
	 * Searches in the given element for a MaxExclusive element
	 * 
	 * @throws SyntaxException
	 */
	private Inclusive fetchMaxExclusive(Element baseNode, String parentsName)
			throws SyntaxException {

		Inclusive res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:maxExclusive[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new Inclusive();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(new AnySimpleTypeBuffer(node.getAttribute("value")));
			if (res.getValue().toString().length() == 0)
				throw new SyntaxException("MinExclusive facet in ["
						+ parentsName
						+ "] MUST have the value attribute defined");

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	/**
	 * Searches in the given element for a MinInclusive element
	 * 
	 * @throws SyntaxException
	 */
	private Inclusive fetchMinInclusive(Element baseNode, String parentsName)
			throws SyntaxException {

		Inclusive res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:minInclusive[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new Inclusive();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(new AnySimpleTypeBuffer(node.getAttribute("value")));
			if (res.getValue().toString().length() == 0)
				throw new SyntaxException("MinExclusive facet in ["
						+ parentsName
						+ "] MUST have the value attribute defined");

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	/**
	 * Searches in the given element for a MaxInclusive element
	 * 
	 * @throws SyntaxException
	 */
	private Inclusive fetchMaxInclusive(Element baseNode, String parentsName)
			throws SyntaxException {

		Inclusive res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:maxInclusive[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new Inclusive();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(new AnySimpleTypeBuffer(node.getAttribute("value")));
			if (res.getValue().toString().length() == 0)
				throw new SyntaxException("MinExclusive facet in ["
						+ parentsName
						+ "] MUST have the value attribute defined");

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	// --------------------------------------------------------------

	/**
	 * search total digits
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private TotalDigits fetchTotalDigits(Element baseNode, String parentsName)
			throws SyntaxException {

		TotalDigits res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:totalDigits[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new TotalDigits();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(Integer.parseInt(node.getAttribute("value")));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	/**
	 * search fraction digits
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private FractionDigits fetchFractionDigits(Element baseNode,
			String parentsName) throws SyntaxException {

		FractionDigits res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:fractionDigits[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new FractionDigits();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(Integer.parseInt(node.getAttribute("value")));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	// --------------------------------------------------------------

	/**
	 * Look for the length constraint facet
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private Length fetchLength(Element baseNode, String parentsName)
			throws SyntaxException {

		Length res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate("./xs:length[position()=1]",
					baseNode, XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new Length();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(Integer.parseInt(node.getAttribute("value")));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	/**
	 * Look for the minLength constraint facet
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private Length fetchMinLength(Element baseNode, String parentsName)
			throws SyntaxException {

		Length res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:minLength[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new Length();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(Integer.parseInt(node.getAttribute("value")));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	/**
	 * Look for the maxLength constraint facet
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private Length fetchMaxLength(Element baseNode, String parentsName)
			throws SyntaxException {

		Length res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:maxLength[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new Length();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));
			res.setValue(Integer.parseInt(node.getAttribute("value")));

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	// --------------------------------------------------------------

	/**
	 * looks for a whiteSpace facet
	 */
	private WhiteSpace fetchWhiteSpace(Element baseNode, String parentsName)
			throws SyntaxException {

		WhiteSpace res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			Element node = (Element) xp.evaluate(
					"./xs:whiteSpace[position()=1]", baseNode,
					XPathConstants.NODE);

			if (node == null)
				return null;

			// Create element
			res = new WhiteSpace();

			// get attributes
			res.setFixed(Boolean.parseBoolean(node.getAttribute("fixed")));
			res.setId(IDBuffer.parseID(node.getAttribute("id")));

			String value = node.getAttribute("value");

			if (value.equals("preserve"))
				res.setPreserve();
			else if (value.equals("collapse"))
				res.setCollapse();
			else if (value.equals("replace"))
				res.setReplace();
			else
				throw new SyntaxException(
						"whiteSpacece facet in ["
								+ parentsName
								+ "] MUST have the value attribute defined to : (preserve|collapse|replace)");

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = null;
		}

		return res;

	}

	/**
	 * looks for enumeration of values
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<Enumeration> fetchEnumeration(Element baseNode,
			String parentsName) throws SyntaxException {

		List<Enumeration> res = new LinkedList<Enumeration>();

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			NodeList enums = (NodeList) xp.evaluate("./xs:enumeration",
					baseNode, XPathConstants.NODESET);

			if (enums == null || enums.getLength() == 0)
				return res;

			// Create List && foreach
			res = new LinkedList<Enumeration>();

			for (int i = 0; i < enums.getLength(); i++) {

				Element en = (Element) enums.item(i);

				// Create element
				Enumeration enu = new Enumeration();

				// get attributes
				enu.setId(IDBuffer.parseID(en.getAttribute("id")));

				String value = en.getAttribute("value");

				// System.out.println("[COMP] Enumeration value: "+value);

				if (value.length() == 0)
					throw new SyntaxException(
							"enumeration facet in ["
									+ parentsName
									+ "] MUST have the value attribute defined to : (preserve|collapse|replace)");

				enu.setValue(new AnySimpleTypeBuffer(value));

				// Fetch annotation
				enu.setAnnotation(this.fetchAnnotation(en, parentsName
						+ "#enumeration"));

				res.add(enu);

			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = Collections.emptyList();
		}

		return res;

	}

	/**
	 * looks for patterns facets and concatenates into one if there are multiple
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private Pattern fetchPattern(Element baseNode, String parentsName)
			throws SyntaxException {

		Pattern res = null;

		// Search (only get the firstone, Do NOT exception for multiple found
		try {
			NodeList patterns = (NodeList) xp.evaluate("./xs:pattern",
					baseNode, XPathConstants.NODESET);

			if (patterns == null || patterns.getLength() == 0)
				return null;

			// Create element
			res = new Pattern();

			// Foreach patterns
			for (int i = 0; i < patterns.getLength(); i++) {

				// Get element and value
				Element pat = (Element) patterns.item(i);

				String value = pat.getAttribute("value");

				if (value.length() == 0)
					throw new SyntaxException("pattern facet in ["
							+ parentsName
							+ "] MUST have the value attribute defined");
				// Concatenate patterns
				java.util.regex.Pattern p = res.getValue();
				try {
					if (p == null)
						res.setValue(java.util.regex.Pattern.compile(value));
					else
						res.setValue(java.util.regex.Pattern.compile(res
								.getValue().pattern()
								+ value));
				} catch (PatternSyntaxException e) {
					res.setValue(java.util.regex.Pattern.compile(""));
				}
			}

		} catch (XPathExpressionException e) {
			res = null;
		}

		return res;

	}

	/** }@ */

	/** @name Elements and attributes fetching methods */
	/** @{ */

	/**
	 * fetches the folowing pattern: ((attribute | attributeGroup)*,
	 * anyAttribute?)
	 */
	private void fetchAllAttributes() {

	}

	/**
	 * fetches the following pattern : ((<b>attribute</b> | attributeGroup)*,
	 * anyAttribute?)
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCAttribute> fetchAttributes(Element baseNode,
			String parentsName) throws SyntaxException {

		List<ZaxCAttribute> res = new LinkedList<ZaxCAttribute>();

		try {
			// Get NodeList
			NodeList attributes = (NodeList) xp.evaluate("./xs:attribute",
					baseNode, XPathConstants.NODESET);

			if (attributes == null || attributes.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < attributes.getLength(); i++) {

				// Get Element and prepare new type
				Element attElt = (Element) attributes.item(i);
				ZaxCAttribute att = new ZaxCAttribute();

				/*
				 * default = string fixed = string form = (qualified |
				 * unqualified) id = ID name = NCName ref = QName type = QName
				 * use = (optional | prohibited | required) : optional
				 */
				att.setName(CompilerUtils.createFromString(NCNameBuffer.class,(String) xp.evaluate("@name",
						attElt, XPathConstants.STRING)));
				att.setRef(CompilerUtils.createFromString(QNameBuffer.class,(String) xp.evaluate("@ref",
						attElt, XPathConstants.STRING)));
				att.setType(CompilerUtils.createFromString(QNameBuffer.class,(String) xp.evaluate("@type",
						attElt, XPathConstants.STRING)));

				// Adapt type name
				if (appendType && att.getType() != null
						&& att.getType() != null
						&& !att.getType().toString().endsWith("jkfekfl-_,;ef"))
					att.setType(CompilerUtils.createFromString(QNameBuffer.class,att.getType().toString()
							+ "Type"));

				att.setDefault(CompilerUtils.createFromString(XSDStringBuffer.class,(String) xp.evaluate(
						"@default", attElt, XPathConstants.STRING)));
				att.setFixed(CompilerUtils.createFromString(XSDStringBuffer.class,(String) xp.evaluate(
						"@fixed", attElt, XPathConstants.STRING)));
				att.setForm((String) xp.evaluate("@form", attElt,
						XPathConstants.STRING));
				att.setId(IDBuffer.parseID((String) xp.evaluate("@id", attElt,
						XPathConstants.STRING)));
				att.setUse((String) xp.evaluate("@use", attElt,
						XPathConstants.STRING));

				// Get simpleType
				// ---------------------------------------
				NodeList stypes = (NodeList) xp.evaluate("./xs:simpleType",
						attElt, XPathConstants.NODESET);
				if (stypes != null && stypes.getLength() > 1)
					throw new SyntaxException(
							"only ONE simpleType is allowed in [" + parentsName
									+ "#attribute]");
				else if (stypes != null && stypes.getLength() == 1)
					att.setSimpleType(this.readSimpleType((Element) stypes
							.item(0), parentsName + "#attribute"));

				// Validate
				if (validate) {
					att.validateSchema(parentsName + "#attribute(" + i + ")",
							(Boolean) xp.evaluate(
									"local-name(..)='schema' and namespace-uri(..)='"
											+ XSDReader.XSD_NS + "'",
									attElt, XPathConstants.BOOLEAN));
				}

				// Fetch Annotation
				att.setAnnotation(this.fetchAnnotation(attElt, parentsName
						+ "#"
						+ (att.getName() == null ? att.getRef().toString()
								: att.getName().toString())));

				// Add to list
				res.add(att);

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();

		}

		return res;
	}

	/**
	 * fetches the following pattern : ((attribute | <b>attributeGroup</b>)*,
	 * anyAttribute?)
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCAttributeGroup> fetchAttributeGroups(Element baseNode,
			String parentsName) throws SyntaxException {

		List<ZaxCAttributeGroup> res = new LinkedList<ZaxCAttributeGroup>();

		try {
			// Get NodeList
			NodeList attributes = (NodeList) xp.evaluate("./xs:attributeGroup",
					baseNode, XPathConstants.NODESET);

			if (attributes == null || attributes.getLength() == 0)
				return res;

			TeaLogging.teaLogInfo("attributeGroups found: "
					+ attributes.getLength());

			// Foreach
			for (int i = 0; i < attributes.getLength(); i++) {

				// Get Element and prepare new type
				Element attElt = (Element) attributes.item(i);
				ZaxCAttributeGroup att = new ZaxCAttributeGroup();
				
				
				// Get children elements ((attribute | attributeGroup)*,
				// anyAttribute?)
				// --------------------------------------
				att.getAttributeGroups().addAll(
						this.fetchAttributeGroups(attElt, parentsName
								+ "#attributeGroup(" + i + ")"));
				att.getAttributes().addAll(
						this.fetchAttributes(attElt, parentsName
								+ "#attributeGroup(" + i + ")"));
				att.setAnyAttribute(this.fetchAnyAttribute(attElt, parentsName
						+ "#attributeGroup(" + i + ")"));

				att.validateSchema(parentsName + "#attributeGroup(" + i + ")",
						(Boolean) xp.evaluate(
								"local-name(..)='schema' and namespace-uri(..)='"
										+ XSDReader.XSD_NS + "'", attElt,
								XPathConstants.BOOLEAN));

				// Get Attributes
				// ------------------------
				att.setRef(CompilerUtils.createFromString(QNameBuffer.class,(String) xp.evaluate("@ref",
						attElt, XPathConstants.STRING)));
				att.setName(CompilerUtils.createFromString(NCNameBuffer.class,(String) xp.evaluate("@name",
						attElt, XPathConstants.STRING)));
				att.setId(IDBuffer.parseID((String) xp.evaluate("@id", attElt,
						XPathConstants.STRING)));

				// Validate
				if (validate) {
					att.validateSchema(parentsName + "#attributeGroup(" + i
							+ ")", (Boolean) xp.evaluate(
							"local-name(..)='schema' and namespace-uri(..)='"
									+ XSDReader.XSD_NS + "'", attElt,
							XPathConstants.BOOLEAN));
				}

				// Fetch Annotation
				att.setAnnotation(this.fetchAnnotation(attElt, parentsName
						+ "#"
						+ (att.getName() == null ? att.getRef().toString()
								: att.getName().toString())));

				TeaLogging.teaLogInfo("attributeGroup name: " + att.getName());

				// Add to list
				res.add(att);

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
			res = Collections.emptyList();
		}

		return res;
	}

	/**
	 * fetches the following pattern : ((attribute | attributeGroup)*,
	 * <b>anyAttribute?</b>)
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCAnyAttribute fetchAnyAttribute(Element baseNode,
			String parentsName) throws SyntaxException {
		ZaxCAnyAttribute res = null;

		try {
			// Get NodeList
			NodeList attributes = (NodeList) xp.evaluate("./xs:anyAttribute",
					baseNode, XPathConstants.NODESET);

			if (attributes == null || attributes.getLength() == 0)
				return null;

			if (attributes.getLength() > 1)
				throw new SyntaxException(
						"only ONE anyAttribute is allowed [in " + parentsName
								+ "#anyAttribute]");

			// Get Element and prepare new type
			Element attElt = (Element) attributes.item(0);
			res = new ZaxCAnyAttribute();

			res.setNamespace((String) xp.evaluate("@namespace", attElt,
					XPathConstants.STRING));
			res.setProcessContents((String) xp.evaluate("@processContents",
					attElt, XPathConstants.STRING));
			res.setId(IDBuffer.parseID((String) xp.evaluate("@id", attElt,
					XPathConstants.STRING)));

			// Fetch Annotation
			res.setAnnotation(this.fetchAnnotation(attElt, parentsName
					+ "#anyAttribute"));

		} catch (XPathExpressionException e) {
			res = null;
		}

		return res;
	}

	/**
	 * <B> reads and returns a group Element : </B><BR>
	 * <BR>
	 * 
	 * <a name="element-group" id="element-group">&lt;group</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;maxOccurs = (<a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a> | <var>unbounded</var>) &nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;minOccurs = <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a>&nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;name = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#NCName"
	 * >NCName</a><br>
	 * &nbsp;&nbsp;ref = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#QName"
	 * >QName</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, (<a class="eltref"
	 * href="#element-all">all</a> | <a class="eltref"
	 * href="#element-choice">choice</a> | <a class="eltref"
	 * href="#element-sequence">sequence</a>)?)<br>
	 * &lt;/group&gt;
	 * 
	 * @param groupElt
	 * @param parentsName
	 * @return
	 * @throws XPathExpressionException
	 * @throws SyntaxException
	 */
	private ZaxCGroup readGroup(Element groupElt, String parentsName)
			throws XPathExpressionException, SyntaxException {

		ZaxCGroup res = new ZaxCGroup();

		// Get Attributes
		// ------------------------
		res.setRef(CompilerUtils.createFromString(QNameBuffer.class,(String) xp.evaluate("@ref", groupElt,
				XPathConstants.STRING)));
		res.setName(CompilerUtils.createFromString(NCNameBuffer.class,(String) xp.evaluate("@name", groupElt,
				XPathConstants.STRING)));
		res.setId(IDBuffer.parseID((String) xp.evaluate("@id", groupElt,
				XPathConstants.STRING)));

		// Get minOccurs and maxOccurs
		// --------------------------------
		java.lang.Double minOccurs = (java.lang.Double) xp.evaluate(
				"@minOccurs", groupElt, XPathConstants.NUMBER);
		if (minOccurs != null)
			res.setMinOccurs(new NonNegativeIntegerBuffer(minOccurs.intValue()));

		String maxOccurs = (String) xp.evaluate("@maxOccurs", groupElt,
				XPathConstants.STRING);
		if (maxOccurs != null && maxOccurs.equals("unbounded")) {
			MaxOccursBuffer m = new MaxOccursBuffer();
			m.setUnbounded();
			res.setMaxOccurs(m);
		} else if (maxOccurs != null && maxOccurs.length() > 0)
			res.setMaxOccurs(new MaxOccursBuffer(Integer.parseInt(maxOccurs)));

		// Get (all | choice | sequence)?)
		res.setAll(this.fetchAll(groupElt, parentsName));
		res.setChoice(this.fetchChoice(groupElt, parentsName));
		res.setSequence(this.fetchSequence(groupElt, parentsName));

		// Validate
		if (validate) {
			res.validateSchema(parentsName, (Boolean) xp.evaluate(
					"local-name(..)='schema' and namespace-uri(..)='"
							+ XSDReader.XSD_NS + "'", groupElt,
					XPathConstants.BOOLEAN));
		}
		// Fetch Annotation
		res.setAnnotation(this.fetchAnnotation(groupElt, parentsName));

		return res;

	}

	/**
	 * Fetches a single <code>&lt;group&gt;</code> element
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCGroup fetchGroup(Element baseNode, String parentsName)
			throws SyntaxException {

		ZaxCGroup res = null;

		try {
			// Get NodeList
			NodeList groups = (NodeList) xp.evaluate("./xs:group", baseNode,
					XPathConstants.NODESET);

			if (groups == null || groups.getLength() == 0)
				return null;

			if (groups.getLength() > 1)
				throw new SyntaxException(
						"only ONE group child is allowed [in " + parentsName
								+ "]");

			// Get Element and prepare new type
			Element groupElt = (Element) groups.item(0);
			res = this.readGroup(groupElt, parentsName + "#group");

		} catch (XPathExpressionException e) {
			res = null;
		}

		return res;

	}

	/**
	 * Fetches multiple <code>&lt;group&gt;</code> elements
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCGroup> fetchGroups(Element baseNode, String parentsName)
			throws SyntaxException {

		List<ZaxCGroup> res = new LinkedList<ZaxCGroup>();

		try {
			// Get NodeList
			NodeList groups = (NodeList) xp.evaluate("./xs:group", baseNode,
					XPathConstants.NODESET);

			if (groups == null || groups.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < groups.getLength(); i++) {

				res.add(this.readGroup((Element) groups.item(i), parentsName
						+ "#group(" + i + ")"));

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * <B> Reads and returns an <code>&lt;all&gt;</code> element </B> <BR>
	 * <BR>
	 * 
	 * <a name="element-all" id="element-all">&lt;all</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;maxOccurs = <var>1</var>&nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;minOccurs = (<var>0</var> | <var>1</var>)&nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, <a class="eltref"
	 * href="#element-element">element</a>*)<br>
	 * &lt;/all&gt;
	 * 
	 * @param allElt
	 * @param parentsName
	 * @return
	 * @throws XPathExpressionException
	 * @throws SyntaxException
	 */
	private ZaxCAll readAll(Element allElt, String parentsName)
			throws XPathExpressionException, SyntaxException {

		ZaxCAll res = new ZaxCAll();

		// Get Attributes
		// ------------------------
		res.setId(IDBuffer.parseID((String) xp.evaluate("@id", allElt,
				XPathConstants.STRING)));

		// Get minOccurs (0|1) (maxOccurs == 1)
		// --------------------------------
		java.lang.Double minOccurs = (java.lang.Double) xp.evaluate(
				"@minOccurs", allElt, XPathConstants.NUMBER);
		if (minOccurs != null)
			res.setMinOccurs(new NonNegativeIntegerBuffer(minOccurs.intValue()));

		// Get element*
		res.getElements().addAll(this.fetchElements(allElt, parentsName));

		// validate
		res.validateSchema(parentsName, false);

		// Fetch Annotation
		// ----------------------
		res.setAnnotation(this.fetchAnnotation(allElt, parentsName));

		return res;

	}

	/**
	 * <B> Fetches an <code>&lt;all&gt;</code> element </B>
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCAll fetchAll(Element baseNode, String parentsName)
			throws SyntaxException {

		ZaxCAll res = null;

		try {
			// Get NodeList
			NodeList alls = (NodeList) xp.evaluate("./xs:all", baseNode,
					XPathConstants.NODESET);

			if (alls == null || alls.getLength() == 0)
				return null;

			if (alls.getLength() > 1)
				throw new SyntaxException("only ONE all child is allowed [in "
						+ parentsName + "]");

			// Get Element and prepare new type
			Element allElt = (Element) alls.item(0);
			res = this.readAll(allElt, parentsName + "#all");

		} catch (XPathExpressionException e) {
			res = null;
		}

		return res;
	}

	/**
	 * <B> Fetches multiple <code>&lt;all&gt;</code> elements </B>
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCAll> fetchAlls(Element baseNode, String parentsName)
			throws SyntaxException {

		List<ZaxCAll> res = new LinkedList<ZaxCAll>();

		try {
			// Get NodeList
			NodeList groups = (NodeList) xp.evaluate("./xs:all", baseNode,
					XPathConstants.NODESET);

			if (groups == null || groups.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < groups.getLength(); i++) {

				res.add(this.readAll((Element) groups.item(i), parentsName
						+ "#all(" + i + ")"));

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * <B>Reads and returns a &lt;choice&gt; element</B><BR>
	 * <BR>
	 * 
	 * <a name="element-choice" id="element-choice">&lt;choice</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;maxOccurs = (<a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a> | <var>unbounded</var>) &nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;minOccurs = <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a>&nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, (<a class="eltref"
	 * href="#element-element">element</a> | <a class="eltref"
	 * href="#element-group">group</a> | <a class="eltref"
	 * href="#element-choice">choice</a> | <a class="eltref"
	 * href="#element-sequence">sequence</a> | <a class="eltref"
	 * href="#element-any">any</a>)*)<br>
	 * &lt;/choice&gt;
	 * 
	 * @throws SyntaxException
	 * @throws XPathExpressionException
	 * 
	 */
	private ZaxCChoice readChoice(Element choiceElt, String parentsName)
			throws XPathExpressionException, SyntaxException {

		ZaxCChoice res = new ZaxCChoice();

		// Get Attributes
		// ------------------------
		res.setId(IDBuffer.parseID((String) xp.evaluate("@id", choiceElt,
				XPathConstants.STRING)));

		// Get minOccurs and maxOccurs
		// --------------------------------
		java.lang.Double minOccurs = (java.lang.Double) xp.evaluate(
				"@minOccurs", choiceElt, XPathConstants.NUMBER);
		if (minOccurs != null)
			res.setMinOccurs(new NonNegativeIntegerBuffer(minOccurs.intValue()));

		String maxOccurs = (String) xp.evaluate("@maxOccurs", choiceElt,
				XPathConstants.STRING);
		if (maxOccurs != null && maxOccurs.equals("unbounded")) {
			MaxOccursBuffer m = new MaxOccursBuffer();
			m.setUnbounded();
			res.setMaxOccurs(m);
		} else if (maxOccurs != null && maxOccurs.length() > 0)
			res.setMaxOccurs(new MaxOccursBuffer(Integer.parseInt(maxOccurs)));

		// Get (element | group | choice | sequence | any)*)
		res.getElements().addAll(this.fetchElements(choiceElt, parentsName));
		res.getGroups().addAll(this.fetchGroups(choiceElt, parentsName));
		res.getChoices().addAll(this.fetchChoices(choiceElt, parentsName));
		res.getSequences().addAll(this.fetchSequences(choiceElt, parentsName));
		res.getAny().addAll(this.fetchAnys(choiceElt, parentsName));

		// Fetch Annotation
		res.setAnnotation(this.fetchAnnotation(choiceElt, parentsName));

		return res;
	}

	/**
	 * <B>fetches a choice Element :<B><BR>
	 * <BR>
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCChoice fetchChoice(Element baseNode, String parentsName)
			throws SyntaxException {

		ZaxCChoice res = null;

		try {
			// Get NodeList
			NodeList choices = (NodeList) xp.evaluate("./xs:choice", baseNode,
					XPathConstants.NODESET);

			if (choices == null || choices.getLength() == 0)
				return null;

			if (choices.getLength() > 1)
				throw new SyntaxException(
						"only ONE choice child is allowed [in " + parentsName
								+ "]");

			// Get Element and prepare new type
			Element choiceElt = (Element) choices.item(0);
			res = this.readChoice(choiceElt, parentsName + "#choice");

		} catch (XPathExpressionException e) {
			res = null;
		}

		return res;
	}

	/**
	 * Fetches multiple choice elements
	 * 
	 * @see XSDReader#fetchChoice(Element, String)
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCChoice> fetchChoices(Element baseNode, String parentsName)
			throws SyntaxException {

		List<ZaxCChoice> res = new LinkedList<ZaxCChoice>();

		try {
			// Get NodeList
			NodeList choices = (NodeList) xp.evaluate("./xs:choice", baseNode,
					XPathConstants.NODESET);

			if (choices == null || choices.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < choices.getLength(); i++) {

				res.add(this.readChoice((Element) choices.item(i), parentsName
						+ "#choice(" + i + ")"));

			}

		} catch (XPathExpressionException e) {

		}

		return res;
	}

	/**
	 * <B>Reads and returns a &lt;sequence&gt; element : </B><BR>
	 * <BR>
	 * 
	 * <a name="element-sequence" id="element-sequence">&lt;sequence</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;maxOccurs = (<a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a> | <var>unbounded</var>) &nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;minOccurs = <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a>&nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, (<a class="eltref"
	 * href="#element-element">element</a> | <a class="eltref"
	 * href="#element-group">group</a> | <a class="eltref"
	 * href="#element-choice">choice</a> | <a class="eltref"
	 * href="#element-sequence">sequence</a> | <a class="eltref"
	 * href="#element-any">any</a>)*)<br>
	 * &lt;/sequence&gt;
	 * 
	 * @param sequenceElt
	 * @param parentsName
	 * @return
	 * @throws XPathExpressionException
	 * @throws SyntaxException
	 */
	private ZaxCSequence readSequence(Element sequenceElt, String parentsName)
			throws XPathExpressionException, SyntaxException {

		ZaxCSequence res = new ZaxCSequence();

		// Get Attributes
		// ------------------------
		res.setId(IDBuffer.parseID((String) xp.evaluate("@id", sequenceElt,
				XPathConstants.STRING)));

		// Get minOccurs and maxOccurs
		// --------------------------------
		java.lang.Double minOccurs = (java.lang.Double) xp.evaluate(
				"@minOccurs", sequenceElt, XPathConstants.NUMBER);
		if (minOccurs != null)
			res.setMinOccurs(new NonNegativeIntegerBuffer(minOccurs.intValue()));

		String maxOccurs = (String) xp.evaluate("@maxOccurs", sequenceElt,
				XPathConstants.STRING);
		if (maxOccurs != null && maxOccurs.equals("unbounded")) {
			MaxOccursBuffer m = new MaxOccursBuffer();
			m.setUnbounded();
			res.setMaxOccurs(m);
		} else if (maxOccurs != null && maxOccurs.length() > 0)
			res.setMaxOccurs(new MaxOccursBuffer(Integer.parseInt(maxOccurs)));

		// Get (element | group | choice | sequence | any)*
		res.getElements().addAll(this.fetchElements(sequenceElt, parentsName));
		res.getGroups().addAll(this.fetchGroups(sequenceElt, parentsName));
		res.getChoices().addAll(this.fetchChoices(sequenceElt, parentsName));
		res.getSequences()
				.addAll(this.fetchSequences(sequenceElt, parentsName));
		res.getAny().addAll(this.fetchAnys(sequenceElt, parentsName));

		// Fetch Annotation
		res.setAnnotation(this.fetchAnnotation(sequenceElt, parentsName));

		return res;
	}

	/**
	 * <B>Fetches a &lt;sequence&gt; elements</B><BR>
	 * <BR>
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCSequence fetchSequence(Element baseNode, String parentsName)
			throws SyntaxException {

		ZaxCSequence res = null;

		try {
			// Get NodeList
			NodeList sequences = (NodeList) xp.evaluate("./xs:sequence",
					baseNode, XPathConstants.NODESET);

			if (sequences == null || sequences.getLength() == 0)
				return null;

			if (sequences.getLength() > 1)
				throw new SyntaxException(
						"only ONE choice child is allowed [in " + parentsName
								+ "]");

			// Get Element and prepare new type
			Element sequenceElt = (Element) sequences.item(0);
			res = this.readSequence(sequenceElt, parentsName + "#sequence");

		} catch (XPathExpressionException e) {
			res = null;
		}

		return res;
	}

	/**
	 * <B>Fetches multiple &lt;sequence&gt; elements</B><BR>
	 * <BR>
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCSequence> fetchSequences(Element baseNode,
			String parentsName) throws SyntaxException {

		List<ZaxCSequence> res = new LinkedList<ZaxCSequence>();

		try {
			// Get NodeList
			NodeList sequences = (NodeList) xp.evaluate("./xs:sequence",
					baseNode, XPathConstants.NODESET);

			if (sequences == null || sequences.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < sequences.getLength(); i++) {

				res.add(this.readSequence((Element) sequences.item(i),
						parentsName + "#sequence(" + i + ")"));

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * <B>Read and returns an &lt;any&gt; element : </B><BR>
	 * <BR>
	 * 
	 * <a name="element-any" id="element-any">&lt;any</a><br>
	 * &nbsp;&nbsp;id = <a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID"
	 * >ID</a><br>
	 * &nbsp;&nbsp;maxOccurs = (<a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a> | <var>unbounded</var>) &nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;minOccurs = <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#nonNegativeInteger"
	 * >nonNegativeInteger</a>&nbsp;:&nbsp;1<br>
	 * &nbsp;&nbsp;namespace = ((<var>##any</var> | <var>##other</var>) | List
	 * of (<a href=
	 * "http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#anyURI"
	 * >anyURI</a> | (<var>##targetNamespace</var> | <var>##local</var>)) )
	 * &nbsp;:&nbsp;##any<br>
	 * &nbsp;&nbsp;processContents = (<var>lax</var> | <var>skip</var> |
	 * <var>strict</var>)&nbsp;:&nbsp;strict<br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?)<br>
	 * &lt;/any&gt;
	 * 
	 * @param anyElt
	 * @param parentsName
	 * @return
	 * @throws XPathExpressionException
	 * @throws SyntaxException
	 */
	private ZaxCAny readAny(Element anyElt, String parentsName)
			throws XPathExpressionException, SyntaxException {

		ZaxCAny res = new ZaxCAny();

		// Get Attributes
		// ------------------------
		res.setId(IDBuffer.parseID((String) xp.evaluate("@id", anyElt,
				XPathConstants.STRING)));
		res.setNamespace((String) xp.evaluate("@nameSpace", anyElt,
				XPathConstants.STRING));
		res.setProcessContents((String) xp.evaluate("@processContents", anyElt,
				XPathConstants.STRING));

		// Get minOccurs and maxOccurs
		// --------------------------------
		java.lang.Double minOccurs = (java.lang.Double) xp.evaluate(
				"@minOccurs", anyElt, XPathConstants.NUMBER);
		if (minOccurs != null)
			res.setMinOccurs(new NonNegativeIntegerBuffer(minOccurs.intValue()));

		String maxOccurs = (String) xp.evaluate("@maxOccurs", anyElt,
				XPathConstants.STRING);
		if (maxOccurs != null && maxOccurs.equals("unbounded")) {
			MaxOccursBuffer m = new MaxOccursBuffer();
			m.setUnbounded();
			res.setMaxOccurs(m);
		} else if (maxOccurs != null && maxOccurs.length() > 0)
			res.setMaxOccurs(new MaxOccursBuffer(Integer.parseInt(maxOccurs)));

		// Fetch Annotation
		res.setAnnotation(this.fetchAnnotation(anyElt, parentsName));

		return res;
	}

	/**
	 * Fetches a single any element
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private ZaxCAny fetchAny(Element baseNode, String parentsName)
			throws SyntaxException {

		ZaxCAny res = null;

		try {
			// Get NodeList
			NodeList anys = (NodeList) xp.evaluate("./xs:any", baseNode,
					XPathConstants.NODESET);

			if (anys == null || anys.getLength() == 0)
				return null;

			if (anys.getLength() > 1)
				throw new SyntaxException(
						"only ONE choice child is allowed [in " + parentsName
								+ "]");

			// Get Element and prepare new type
			Element anyElt = (Element) anys.item(0);
			res = this.readAny(anyElt, parentsName + "#any");

		} catch (XPathExpressionException e) {
			res = null;
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Fetches multiple any elements
	 * 
	 * @param baseNode
	 * @param parentsName
	 * @return
	 * @throws SyntaxException
	 */
	private List<ZaxCAny> fetchAnys(Element baseNode, String parentsName)
			throws SyntaxException {

		List<ZaxCAny> res = new LinkedList<ZaxCAny>();

		try {
			// Get NodeList
			NodeList anys = (NodeList) xp.evaluate("./xs:any", baseNode,
					XPathConstants.NODESET);

			if (anys == null || anys.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < anys.getLength(); i++) {
				TeaLogging.teaLogInfo("Found Any under : " + parentsName);
				res.add(this.readAny((Element) anys.item(i), parentsName
						+ "#any(" + i + ")"));

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return res;
	}

	private List<ZaxCElement> fetchElements(Element baseNode, String parentsName)
			throws SyntaxException {

		List<ZaxCElement> res = new LinkedList<ZaxCElement>();

		try {
			// Get NodeList
			NodeList choices = (NodeList) xp.evaluate("./xs:element", baseNode,
					XPathConstants.NODESET);

			if (choices == null || choices.getLength() == 0)
				return res;

			// Foreach
			for (int i = 0; i < choices.getLength(); i++) {

				// Get Element
				Element elementElt = (Element) choices.item(i);
				ZaxCElement elt = new ZaxCElement();

				// Get simpleType
				// ------------------------
				NodeList stypes = (NodeList) xp.evaluate("./xs:simpleType",
						elementElt, XPathConstants.NODESET);
				if (stypes != null && stypes.getLength() > 1)
					throw new SyntaxException(
							"only ONE simpleType is allowed in [" + parentsName
									+ "#element]");
				else if (stypes != null && stypes.getLength() == 1)
					elt.setSimpleType(this.readSimpleType((Element) stypes
							.item(0), parentsName + "#element"));

				// Get Complextype
				// --------------------------
				NodeList ctypes = (NodeList) xp.evaluate("./xs:complexType",
						elementElt, XPathConstants.NODESET);
				if (ctypes != null && ctypes.getLength() > 1)
					throw new SyntaxException(
							"only ONE simpleType is allowed in [" + parentsName
									+ "#element]");
				else if (ctypes != null && ctypes.getLength() == 1)
					elt.setComplexType((this.readComplexType((Element) ctypes
							.item(0), parentsName + "#element")));

				// Get attributes
				// ------------------------------
				/*
				 * abstract = boolean : false block = (#all | List of (extension
				 * | restriction | substitution)) default = string final = (#all
				 * | List of (extension | restriction)) fixed = string form =
				 * (qualified | unqualified) id = ID maxOccurs =
				 * (nonNegativeInteger | unbounded) : 1 minOccurs =
				 * nonNegativeInteger : 1 name = NCName nillable = boolean :
				 * false ref = QName substitutionGroup = QName type = QName
				 */
				elt.setName(CompilerUtils.createFromString(NCNameBuffer.class,(String) xp.evaluate("@name",
						elementElt, XPathConstants.STRING)));
				elt.setRef(CompilerUtils.createFromString(QNameBuffer.class,(String) xp.evaluate("@ref",
						elementElt, XPathConstants.STRING)));
				elt.setType(CompilerUtils.createFromString(QNameBuffer.class,(String) xp.evaluate("@type",
						elementElt, XPathConstants.STRING)));

				// Adapt type name
				if (appendType && elt.getType() != null
						&& elt.getType() != null
						&& !elt.getType().toString().endsWith("jkfekfl-_,;ef"))
					elt.setType(CompilerUtils.createFromString(QNameBuffer.class,elt.getType().toString()
							+ "Type"));

				elt.setDefault(CompilerUtils.createFromString(XSDStringBuffer.class,(String) xp.evaluate(
						"@default", elementElt, XPathConstants.STRING)));
				elt.setFixed(CompilerUtils.createFromString(XSDStringBuffer.class,(String) xp.evaluate(
						"@fixed", elementElt, XPathConstants.STRING)));
				elt.setForm((String) xp.evaluate("@form", elementElt,
						XPathConstants.STRING));
				elt.setId(IDBuffer.parseID((String) xp.evaluate("@id", elementElt,
						XPathConstants.STRING)));
				elt.setNillable(Boolean.parseBoolean(elementElt
						.getAttribute("nillable")));
				elt.setSubstitutionGroup(CompilerUtils.createFromString(QNameBuffer.class,elementElt
						.getAttribute("substitutionGroup")));

				// Get minOccurs and maxOccurs
				// --------------------------------
				java.lang.Double minOccurs = (java.lang.Double) xp.evaluate(
						"@minOccurs", elementElt, XPathConstants.NUMBER);
				if (minOccurs != null)
					elt.setMinOccurs(new NonNegativeIntegerBuffer(minOccurs
							.intValue()));

				String maxOccurs = (String) xp.evaluate("@maxOccurs",
						elementElt, XPathConstants.STRING);
				if (maxOccurs != null && maxOccurs.equals("unbounded")) {
					MaxOccursBuffer m = new MaxOccursBuffer();
					m.setUnbounded();
					elt.setMaxOccurs(m);
				} else if (maxOccurs != null && maxOccurs.length() > 0)
					elt
							.setMaxOccurs(new MaxOccursBuffer(Integer
									.parseInt(maxOccurs)));

				// Get abstract
				// ------------------------------------
				if (elementElt.getAttribute("abstract").equals("true"))
					elt.setAbstract(true);
				else if (elementElt.getAttribute("abstract").equals("false"))
					elt.setAbstract(false);
				else if (elementElt.getAttribute("abstract").length() > 0)
					throw new SyntaxException(
							"abstract Attribute in complextype ["
									+ elt.getName()
									+ "] value is not (true|false)");

				// Get Default
				//-----------------
				if (elementElt.getAttribute("default").length()>0) {
					elt.setDefault(new XSDStringBuffer(elementElt.getAttribute("default")));
				}
				// Get Final and block
				// ----------------------------------------------
				String Final = elementElt.getAttribute("final");
				if (Final.equals("#all")) {
					elt.getFinal().add("#all");
				} else if (Final.length() > 0) {

					String[] values = Final.split(" ");
					for (String val : values) {
						if (val.equals("restriction"))
							elt.getFinal().add("restriction");
						else if (val.equals("extension"))
							elt.getFinal().add("extension");
						else
							throw new SyntaxException(
									"final Attribute in complextype ["
											+ elt.getName()
											+ "] value is not (#all | List of (extension | restriction))");
					}
				}
				String block = elementElt.getAttribute("block");
				if (block.equals("#all")) {
					elt.getBlock().add("#all");
				} else if (block.length() > 0) {
					String[] values = block.split(" ");
					for (String val : values) {
						if (val.equals("restriction"))
							elt.getBlock().add("restriction");
						else if (val.equals("extension"))
							elt.getBlock().add("extension");
						else
							throw new SyntaxException(
									"block Attribute in complextype ["
											+ elt.getName()
											+ "] value is not (#all | List of (extension | restriction))");
					}
				}

				// Validate
				elt.validateSchema(parentsName + "#element(" + i + ")",
						(Boolean) xp.evaluate(
								"local-name(..)='schema' and namespace-uri(..)='"
										+ XSDReader.XSD_NS + "'",
								elementElt, XPathConstants.BOOLEAN));

				// Fetch Annotation
				elt.setAnnotation(this.fetchAnnotation(elementElt, parentsName
						+ "#"
						+ (elt.getName() == null ? elt.getRef().toString()
								: elt.getName().toString())));

				// Add to list
				res.add(elt);

				// TODO (unique | key | keyref)* reading

			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return res;

	}

	/**
	 * @return the schema
	 */
	public ZaxCXSSchema getSchema() {
		return schema;
	}

	/**
	 * @param schema
	 *            the schema to set
	 */
	public void setSchema(ZaxCXSSchema schema) {
		this.schema = schema;
	}

	/**
	 * @return the appendType
	 */
	public boolean isAppendType() {
		return appendType;
	}

	/**
	 * @param appendType
	 *            the appendType to set
	 */
	public void setAppendType(boolean appendType) {
		this.appendType = appendType;
	}

	/** @} */

	public String getTargetnamespace() {
		return this.nsmap.get("targetNamespace");
	}

	/**
	 * @return the ignore
	 */
	public boolean isIgnore() {
		return ignore;
	}

	/**
	 * @param ignore
	 *            the ignore to set
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

}
