/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.out.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;


import com.idyria.osi.ooxoo.compiler.CompilerUtils;
import com.idyria.osi.ooxoo.compiler.out.OutputUtils;
import com.idyria.osi.ooxoo.compiler.xsd.XSDImportedDocument;
import com.idyria.osi.ooxoo.compiler.xsd.XSDReader;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAbstractSelectAttributes;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAbstractSelectElementsAndAttributes;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAny;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAnyAttribute;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAttribute;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAttributeGroup;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCChoice;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCComplexType;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCElement;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCGroup;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCSequence;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCSimpleType;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCWildCard;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCXSSchema;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCSimpleContent.extension;
import com.idyria.osi.ooxoo.core.ElementBuffer;
import com.idyria.osi.ooxoo.core.buffers.common.ObjectBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.AbstractDataTypesBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NCNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.TypeAnalyzer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDType;
import com.idyria.osi.ooxoo.core.wrap.OOXAny;
import com.idyria.osi.ooxoo.core.wrap.OOXChoice;
import com.idyria.osi.ooxoo.core.wrap.OOXList;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxany;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxattribute;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxchoice;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxelement;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxtextcontent;
import com.idyria.osi.ooxoo.core.wrap.constraints.OOXNillable;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation.AppInfo;
import com.idyria.utils.java.logging.TeaLogging;

/**
 * @author rtek
 * 
 */
public class JavaOutputCell {

	// ! The document to marshall
	private ZaxCXSSchema schema = null;

	/**
	 * The base application package
	 */
	private String basepackageName = null;

	/**
	 * The package in which to output
	 */
	private String realpackageName = null;

	private int choicesct = 1;

	// The base source dir
	private String srcDir = "src/main/java/";

	/**
	 * Prefixes arre used in todo list to identify categories of types
	 * (elements,attributes...) This is designed to avoid type confusion is
	 * elements and types have the same name
	 */
	public static String PRE_CTYPE = "ctypes";

	public static String PRE_STYPE = "stypes";

	public static String PRE_ETYPE = "elements";

	public static String PRE_ATYPE = "attributes";

	/**
	 * The imports to register
	 */
	private List<String> imports = new LinkedList<String>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2591053080056665228L;

		public boolean add(String e) {

			if (!contains(e)) {
				super.add(e);
				return true;
			} else
				return false;

		}

	};

	/**
	 * Forbidden names associated with a replacement part
	 */
	public static HashMap<String, String> forbiddennames = new HashMap<String, String>();

	static {
		forbiddennames.put("abstract", "Abstract");
		forbiddennames.put("final", "Final");
		forbiddennames.put("class", "ClassVal");
		forbiddennames.put("default", "Default");
		forbiddennames.put("case", "Case");
		forbiddennames.put("true", "True");
		forbiddennames.put("false", "False");
		forbiddennames.put("if", "If");
		forbiddennames.put("else", "Else");
		forbiddennames.put("else if", "Elseif");
		forbiddennames.put("public", "Public");
		forbiddennames.put("private", "Private");
		forbiddennames.put("protected", "Protected");
		forbiddennames.put("throws", "Throws");
		forbiddennames.put("throw", "Throw");
		forbiddennames.put("interface", "Interface");
		forbiddennames.put("extends", "Extends");
		forbiddennames.put("import", "Import");

		forbiddennames.put("boolean", "Boolean");
		forbiddennames.put("string", "String");
		forbiddennames.put("byte", "Byte");
		forbiddennames.put("short", "Short");
		forbiddennames.put("int", "Int");
		forbiddennames.put("float", "Float");
		forbiddennames.put("long", "Long");
		forbiddennames.put("double", "Double");
	}

	/** @name Writers */
	/** @{ */

	private JavaOutputStream localOutputStream = null;

	/**
	 * The main class outputstream
	 */
	private JavaOutputStream rootOutputStream = null;

	/** }@ */

	// The base directory
	private String basedir = "";

	/**
	 * The file to which outputing will be done
	 */
	private File file = null;

	/**
	 * the source output to request for outputings
	 */
	private CompilerJavaOutput sourceoutput = null;

	/**
	 * The class Name of the file
	 */
	private String className = null;

	/**
	 * The current ClassName in a list, so that when classEnd is called -> we
	 * can jump one level higher
	 */
	private LinkedList<String> currentClassName = new LinkedList<String>();

	/**
	 * Name of the field that will be used for the toString method of the object
	 */
	private String ooxooSpecialToString = null;
	
	/**
	 * @param packageName
	 * @param basedir
	 */
	public JavaOutputCell(String packageName, String basedir,
			ZaxCXSSchema schema, CompilerJavaOutput sourceoutput) {
		this();
		this.basepackageName = packageName;
		this.basedir = basedir;
		this.schema = schema;
		this.sourceoutput = sourceoutput;
	}

	/**
	 * 
	 */
	public JavaOutputCell() {
		super();
		localOutputStream = new JavaOutputStream();
		rootOutputStream = localOutputStream;
	}

	/**
	 * This method will commit printwriter content's into the file
	 * 
	 * @param file
	 */
	public void commitFile() {

		try {

			// Write && Reset outputstreams
			File clearFile = new File(file.getAbsolutePath().replaceAll("\\s",
					""));
			System.out.println("Writing out:" + clearFile.getAbsolutePath());
			// Create parent folder if doesn't exist
			File packageFolder = clearFile.getParentFile();
			if (!packageFolder.exists()) {
				packageFolder.mkdirs();
			}

			FileOutputStream str = new FileOutputStream(clearFile
					.getAbsolutePath().replaceAll("\\s", ""));
			str.write(localOutputStream.flush());
			str.close();

			// Reset imports
			this.imports.clear();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Writes a complex Type
	 * 
	 * @param tp
	 * @throws ZaxbException
	 * @throws IOException
	 */
	public void outputComplexType(ZaxCComplexType tp) throws IOException {

		// Prepare file && output streams
		// --------------------------------------------
		if (tp.getName() == null)
			return;

		className = this._checkFieldName(tp.getName().toString());

		// Prepate file and output directory
		// ---------------------------------------
		File bdir = new File(basedir + "/ctypes/");
		if (!bdir.exists())
			bdir.mkdirs();

		file = new File(basedir + "/ctypes/" + className + "." + this.schema.getTargetLanguage());

		// Adjust real package name
		this.realpackageName = this.basepackageName + ".ctypes";

		System.out.println("\t[OOXOUT] Destination file : " + className
				+ ".java (" + file.getAbsolutePath() + ")");

		if (file.exists())
			file.delete();

		// Write package
		// --------------------------------
		this.writePackage();

		// Write start class definition
		// -----------------------------------
		this.writeClassStart(tp);

		// Write attributes and elements
		// ----------------------------------------
		this.writeElements(tp);

		// OOXOO SPECIALS
		//-------------------
		
		// to string
		if (this.ooxooSpecialToString!=null) {
			this.localOutputStream.getGetSettersOut().println("\tpublic String toString() {");
			
			this.localOutputStream.getGetSettersOut().println("\t\treturn this."+this.ooxooSpecialToString+".toString();");
			
			this.localOutputStream.getGetSettersOut().println("\t}");
		}
		
		// End class
		// ----------------------------------
		this.writeClassEnd();

		// Write imports
		// --------------------------------
		this.writeImports();

	}

	/**
	 * Writes an element
	 * 
	 * @param tp
	 */
	public void outputElement(ZaxCElement tp) {

		// Prepare file && output streams
		// --------------------------------------------
		if (tp.getName() == null)
			return;

		className = this._checkFieldName(tp.getName().toString());

		File bdir = new File(basedir + "/elements/");
		if (!bdir.exists())
			bdir.mkdirs();

		file = new File(basedir + "/elements/" + className + "." + this.schema.getTargetLanguage());

		// Adjust real package name
		this.realpackageName = this.basepackageName + ".elements";

		System.out.println("\t[OOXOUT] Destination file : " + className
				+ ".java (" + file.getAbsolutePath() + ")");

		if (file.exists())
			file.delete();

		// Write package
		// --------------------------------
		this.writePackage();

		// Write start class definition
		// -----------------------------------
		this.writeClassStart(tp);

		// Write attributes and elements
		// ----------------------------------------
		this.writeElements(tp);

		
		// OOXOO SPECIALS
		//-------------------
		
		// to string
		if (this.ooxooSpecialToString!=null) {
			this.localOutputStream.getGetSettersOut().println("\tpublic String toString() {");
			
			this.localOutputStream.getGetSettersOut().println("\t\treturn this."+this.ooxooSpecialToString+".toString();");
			
			this.localOutputStream.getGetSettersOut().println("\t}");
		}
		
		// End class
		// ----------------------------------
		this.writeClassEnd();

		// Write imports
		// --------------------------------
		this.writeImports();

	}

	/**
	 * Outputs a simpleType
	 * 
	 * @param tp
	 */
	public void outputSimpleType(ZaxCSimpleType tp) {

		// Prepare file && output streams
		// --------------------------------------------
		if (tp.getName() == null)
			return;

		className = this._checkFieldName(tp.getName().toString());

		File bdir = new File(basedir + "/stypes/");
		if (!bdir.exists())
			bdir.mkdirs();

		file = new File(basedir + "/stypes/" + className + "." + this.schema.getTargetLanguage());

		// Adjust real package name
		this.realpackageName = this.basepackageName + ".stypes";

		System.out.println("\t[OOXOUT] Destination file : " + className
				+ ".java (" + file.getAbsolutePath() + ")");

		if (file.exists())
			file.delete();

		// Write package
		// --------------------------------
		this.writePackage();

		// Write start class definition
		// -----------------------------------
		this.writeClassStart(tp);

		// Write elements
		// ----------------------------
		this.writeElements(tp);

		// End class
		// ----------------------------------
		this.writeClassEnd();

		// Write imports
		// --------------------------------
		this.writeImports();

	}

	/**
	 * FIXME Output attributes
	 * 
	 * @param tp
	 */
	public void outputAttribute(ZaxCAttribute tp) {

		// Prepare file && output streams
		// --------------------------------------------
		if (tp.getName() == null)
			return;

		className = this._checkFieldName(tp.getName().toString());

		File bdir = new File(basedir + "/attributes/");
		if (!bdir.exists())
			bdir.mkdirs();

		file = new File(basedir + "/attributes/" + tp.getName() + "." + this.schema.getTargetLanguage());

		// Adjust real package name
		this.realpackageName = this.basepackageName + ".attributes";

		System.out.println("\t[OOXOUT] Destination file : " + tp.getName()
				+ ".java (" + file.getAbsolutePath() + ")");

		if (file.exists())
			file.delete();

		// Write package
		// --------------------------------
		this.writePackage();

		// Write start class definition
		// -----------------------------------
		this.writeClassStart(tp);

		// TODO Write elements
		// ----------------------------
		this.writeAttribute(tp);

		
		
		
		
		// End class
		// ----------------------------------
		this.writeClassEnd();

		// Write imports
		// --------------------------------
		this.writeImports();

	}

	private void writePackage() {

		this.localOutputStream.getImportsOut().println(
				"package " + this.realpackageName + ";");
		this.localOutputStream.getImportsOut().println();
		this.localOutputStream.getImportsOut().println();
	}

	private void writeImports() {

		for (String imp : this.imports)
			this.localOutputStream.getImportsOut().println(
					"import " + imp + ";");
		this.localOutputStream.getImportsOut().println();
		this.localOutputStream.getImportsOut().println();
	}

	/**
	 * Writes class start doc
	 * 
	 * @param tp
	 */
	private void writeClassDoc(ZaxCAnnotableType tp) {
		// Write documentation
		if (tp.getAnnotation() != null
				&& tp.getAnnotation().getDocs().size() > 0) {

			// TODO add multiple doc declaration [currently just the first one]
			this.localOutputStream.getClassdefOut().println("/**");
			this.localOutputStream.getClassdefOut().println(" *");

			for (String docline : tp.getAnnotation().getDocs().get(0)
					.getContent().split("\n"))
				this.localOutputStream.getClassdefOut().println(
						" * " + docline.trim());

			this.localOutputStream.getClassdefOut().println(" *");
			this.localOutputStream.getClassdefOut().println(" */");

		}
	}

	/**
	 * Writes the class start for a complexType
	 * 
	 * @param tp
	 * @return the classname
	 */
	private String writeClassStart(ZaxCComplexType tp) {

		this.writeClassDoc(tp);

		// Write visibility
		this.localOutputStream.getClassdefOut().print("public ");

		// Write modifiers
		// ------------------

		// Abstract
		if (tp.isAbstract())
			this.localOutputStream.getClassdefOut().print("abstract ");
		// if (tp.getFinal().contains("#all") || tp.getBlock().contains("#all"))
		// this.localOutputStream.getClassdefOut().print("final ");

		// Get class name
		String className = this._checkFieldName(tp.getName().toString());

		// Write class name
		this.localOutputStream.getClassdefOut().print(
				"class " + className + " ");

		// Do we need to extend anyone ?
		// ----------------------------------
		String extension = "";
		if (tp.getSimpleContent() != null) {

			// -- Extension in simple content
			if (tp.getSimpleContent().getExtension() != null) {

				extension = QName2Type(tp.getSimpleContent().getExtension()
						.getBase(), null);
				this.localOutputStream.getClassdefOut().print(
						"extends " + extension);

			}
			// -- Restriction in simple content
			else if (tp.getSimpleContent().getRestriction() != null) {

				extension = QName2Type(tp.getSimpleContent().getRestriction()
						.getBase(), null);
				this.localOutputStream.getClassdefOut().print(
						"extends " + extension);

			}
			// -- No extension
			else {

				this.localOutputStream.getClassdefOut().print(
						"extends " + ElementBuffer.class.getSimpleName() + "<Object>");
				this.imports.add(ElementBuffer.class.getCanonicalName());
			}
		} else if (tp.getComplexContent() != null) {

			if (tp.getComplexContent().getExtension() != null) {

				extension = QName2Type(tp.getComplexContent().getExtension()
						.getBase(), null);
				this.localOutputStream.getClassdefOut().print(
						"extends " + extension);

			} else if (tp.getComplexContent().getRestriction() != null) {

				extension = QName2Type(tp.getComplexContent().getRestriction()
						.getBase(), null);
				this.localOutputStream.getClassdefOut().print(
						"extends " + extension);
			}
			// -- No extension
			else {
				this.localOutputStream.getClassdefOut().print(
						"extends " + ElementBuffer.class.getSimpleName()+ "<Object>");
				this.imports.add(ElementBuffer.class.getCanonicalName());
			}
		} else {
			// -- No extension
			this.localOutputStream.getClassdefOut().print(
					"extends " + ElementBuffer.class.getSimpleName() + "<Object>");
			this.imports.add(ElementBuffer.class.getCanonicalName());
		}

		this.localOutputStream.getClassdefOut().print("{\n");
		this.localOutputStream.getClassdefOut().println();
		this.localOutputStream.getClassdefOut().println();

		_writeEmptyConstructor(className);

		// If we are extending an XSD Simple type => add the contructor(String)
		try {
			Class extcl = Class.forName(extension);
			if (XSDType.class.isAssignableFrom(extcl)) {
				// Do we need to throw an exception??
				Constructor cst = extcl
						.getConstructor(new Class[] { String.class });
				_writeSuperStringConstructor(className, cst.getExceptionTypes());

			}
		} catch (ClassNotFoundException e) {
			// DO nothing
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		_appendCurrentClassName(className);

		return className;

	}

	/**
	 * Writes a class start for an element
	 * 
	 * @param tp
	 */
	private void writeClassStart(ZaxCElement tp) {

		this.writeClassDoc(tp);

		// Write && imports annotations
		this.localOutputStream.getClassdefOut().println(
				"@" + Ooxnode.class.getSimpleName() + "(localName=\""
						+ tp.getName() + "\",targetNamespace=\""
						+ this.schema.getTargetNamespace().toString() + "\")");
		this.localOutputStream.getClassdefOut().println(
				"@" + Ooxelement.class.getSimpleName() + "()");
		imports.add(Ooxnode.class.getCanonicalName());
		imports.add(Ooxelement.class.getCanonicalName());

		// Write visibility
		this.localOutputStream.getClassdefOut().print("public ");

		// Get class name
		String className = this._checkFieldName(tp.getName().toString());

		// Write modifiers
		// ---------------------

		// Abstract
		if (tp.isAbstract())
			this.localOutputStream.getClassdefOut().print("abstract ");

		// Final if local or in super type
		if (tp.getFinal().contains("#all") || tp.getBlock().contains("#all")
				|| tp.getComplexType() != null
				&& tp.getComplexType().getFinal().contains("#all"))
			this.localOutputStream.getClassdefOut().print("final ");

		// Write class name
		this.localOutputStream.getClassdefOut().print(
				"class " + className + " ");

		// Do we need to extend anyone ?
		String extension = "";
		if (tp.getType() != null) {
			String ext = this.QName2Type(tp.getType(), null);
			extension = ext;
			this.localOutputStream.getClassdefOut().print(
					"extends " + ext + " ");

		} else if (tp.getComplexType() != null
				&& tp.getComplexType().getComplexContent() != null
				&& tp.getComplexType().getComplexContent().getExtension() != null) {

			String ext = this.QName2Type(tp.getComplexType()
					.getComplexContent().getExtension().getBase(), null);
			extension = this._getSimpleName(ext);
			this.localOutputStream.getClassdefOut().print(
					"extends " + ext + " ");
			this.imports.add(ext);

		} else if (tp.getComplexType() != null
				&& tp.getComplexType().getSimpleContent() != null
				&& tp.getComplexType().getSimpleContent().getExtension() != null) {

			String ext = this.QName2Type(tp.getComplexType()
					.getSimpleContent().getExtension().getBase(), null);
			extension = this._getSimpleName(ext);
			this.localOutputStream.getClassdefOut().print(
					"extends " + ext + " ");
			this.imports.add(ext);

		} else {

			this.localOutputStream.getClassdefOut().print(
					"extends " + ElementBuffer.class.getSimpleName() + "<Object>");
			this.imports.add(ElementBuffer.class.getCanonicalName());
		}

		// Implements interfaces for special constraints
		if (tp.isNillable()) {
			this.localOutputStream.getClassdefOut().print(
					"implements " + OOXNillable.class.getSimpleName());
			this.imports.add(OOXNillable.class.getCanonicalName());
		}

		this.localOutputStream.getClassdefOut().print("{\n");
		this.localOutputStream.getClassdefOut().println();
		this.localOutputStream.getClassdefOut().println();

		// Output default empty constructor
		_writeEmptyConstructor(className);

		// If we are extending an XSD Simple type => add the contructor(String)
		try {
			Class extcl = Class.forName(extension);
			if (XSDType.class.isAssignableFrom(extcl)) {

				// Do we need to throw an exception??
				Constructor cst = extcl
						.getConstructor(new Class[] { String.class });

				_writeSuperStringConstructor(className, cst.getExceptionTypes());

			}
		} catch (ClassNotFoundException e) {
			// DO nothing
		} catch (SecurityException e) {

			// e.printStackTrace();
		} catch (NoSuchMethodException e) {

			// e.printStackTrace();
		}

		_appendCurrentClassName(className);

	}

	/**
	 * Writes a class start for a simpleType
	 * 
	 * @param tp
	 */
	private void writeClassStart(ZaxCSimpleType tp) {

		this.writeClassDoc(tp);

		// Write visibility
		this.localOutputStream.getClassdefOut().print("public ");

		// Write modifiers
		if (tp.getFinal().contains("#all"))
			this.localOutputStream.getClassdefOut().print("final ");

		// Get class name
		String className = this._checkFieldName(tp.getName().toString());

		// Write class name
		this.localOutputStream.getClassdefOut().print(
				"class " + className + " ");

		// Do we need to extend anyone ?
		String extension = "";
		if (tp.getRestriction() != null) {
			extension = QName2Type(tp.getRestriction().getBase(), null);
			this.localOutputStream.getClassdefOut().print(
					"extends " + extension);
		} else {
			this.localOutputStream.getClassdefOut().print(
					"extends " + ElementBuffer.class.getSimpleName()+ "<Object>");
			this.imports.add(ElementBuffer.class.getCanonicalName());
		}

		this.localOutputStream.getClassdefOut().print("{\n");
		this.localOutputStream.getClassdefOut().println();
		this.localOutputStream.getClassdefOut().println();

		_writeEmptyConstructor(className);

		// If we are extending an XSD Simple type => add the contructor(String)
		try {
			Class extcl = Class.forName(extension);
			if (XSDType.class.isAssignableFrom(extcl)) {
				// Do we need to throw an exception??
				Constructor cst = extcl
						.getConstructor(new Class[] { String.class });
				_writeSuperStringConstructor(className, cst.getExceptionTypes());

			}
		} catch (ClassNotFoundException e) {
			// DO nothing
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		_appendCurrentClassName(className);

	}

	/**
	 * Writes a class start for attributes
	 * 
	 * @param tp
	 */
	private void writeClassStart(ZaxCAttribute tp) {

		this.writeClassDoc(tp);

		// Write && imports annotations
		this.localOutputStream.getClassdefOut().println(
				"@" + Ooxnode.class.getSimpleName() + "(localName=\""
						+ tp.getName() + "\",targetNamespace=\""
						+ this.schema.getTargetNamespace().toString() + "\")");
		this.localOutputStream.getClassdefOut().println(
				"@" + Ooxattribute.class.getSimpleName() + "()");
		this.imports.add(Ooxnode.class.getCanonicalName());
		this.imports.add(Ooxattribute.class.getCanonicalName());

		// Write visibility
		this.localOutputStream.getClassdefOut().print("public ");

		// Get class name
		String className = this._checkFieldName(tp.getName().toString());

		// Write class name
		this.localOutputStream.getClassdefOut().print(
				"class " + className + " ");

		// TODO? Do we need to extend anyone ?
		String extension = "";
		if (tp.getType() != null) {
			String ext = this.QName2Type(tp.getType(), null);
			extension = this._getSimpleName(ext);
			this.localOutputStream.getClassdefOut().print(
					"extends " + ext + " ");
			// this.imports.add(ext);

		} else {
			this.localOutputStream.getClassdefOut().print(
					"extends " + ElementBuffer.class.getSimpleName()+ "<Object>");
			this.imports.add(ElementBuffer.class.getCanonicalName());
		}

		this.localOutputStream.getClassdefOut().print("{\n");
		this.localOutputStream.getClassdefOut().println();
		this.localOutputStream.getClassdefOut().println();

		_writeEmptyConstructor(className);

		// If we are extending an XSD Simple type => add the contructor(String)
		try {
			Class extcl = Class.forName(extension);
			if (XSDType.class.isAssignableFrom(extcl)) {
				// Do we need to throw an exception??
				Constructor cst = extcl
						.getConstructor(new Class[] { String.class });
				_writeSuperStringConstructor(className, cst.getExceptionTypes());

			}
		} catch (ClassNotFoundException e) {
			// DO nothing
		} catch (SecurityException e) {

		} catch (NoSuchMethodException e) {

		}

		_appendCurrentClassName(className);

	}

	/**
	 * Writes elements composing a ZaxCElement
	 * 
	 * @param tp
	 */
	private void writeElements(ZaxCElement tp) {

		// TODO : Get on with anonymous type declaration
		if (tp.getComplexType() != null)
			this.writeElements(tp.getComplexType());
		// Get on with specific implementations

		// Nillable
		if (tp.isNillable()) {

			// Implement Nillable
			// ---------------------------------
			this.localOutputStream.getGetSettersOut().println(
					"\tprivate boolean _nil = false;");
			this.localOutputStream.getGetSettersOut().println();
			this.localOutputStream.getGetSettersOut().println();

			this.localOutputStream
					.getGetSettersOut()
					.println(
							"\t\t\tpublic void setNillabed(boolean val) {\n\t\t\t\t this._nil = val; \n\t\t\t}");
			this.localOutputStream.getGetSettersOut().println();
			this.localOutputStream
					.getGetSettersOut()
					.println(
							"\t\t\tpublic boolean isNillabed() {\n\t\t\t\t return this._nil; \n\t\t\t}");

		}

		// TODO implements mixed possiblity (_value field to add to class and
		// set as text content)

	}

	/**
	 * Writes elements declarations
	 * 
	 * @param tp
	 */
	private void writeElements(ZaxCComplexType tp) {

		// Get on with complexContent
		// -------------------------------------------------
		if (tp.getComplexContent() != null) {

			// TODO Get on with restriction in complexContent
			// ------------------------------------------------

			// -> extension
			// ------------------------
			if (tp.getComplexContent().getExtension() != null) {

				// Write attributes
				this.writeAbstractSelectElementsAndAttributes(tp
						.getComplexContent().getExtension());
			}

			// -> restriction
			// -------------------
			if (tp.getComplexContent().getRestriction() != null) {

				// Write attributes
				this.writeAbstractSelectElementsAndAttributes(tp
						.getComplexContent().getRestriction());
			}

		} // END complexContent

		// Get it on with simpleContent
		// --------------------------------------
		if (tp.getSimpleContent() != null) {

			// -> extension
			// ------------------------
			if (tp.getSimpleContent().getExtension() != null) {

				// Write attributes
				this.writeAbstractSelectAttributes(tp.getSimpleContent()
						.getExtension());
			}

			// -> restriction
			// -------------------
			if (tp.getSimpleContent().getRestriction() != null) {

				// Write attributes
				this.writeAbstractSelectAttributes(tp.getSimpleContent()
						.getRestriction());
			}

		}

		// Output standart elements and attributes
		// ---------------------------------------------------------------
		this.writeAbstractSelectElementsAndAttributes(tp);

		// Get on with complextype attributes

		// Get on with mixed
		// -----------------------------------
		if (tp.isMixed()) {

			this.localOutputStream.getFieldsDeclarationOut().println(
					"\t@Ooxtextcontent");
			this._writeFieldDeclaration("_textContent", "java.lang.String",
					OutputUtils.extractDocumentation(tp),
					"This field is the textContent for this element");

			// Write getter and setter
			this.localOutputStream
					.getGetSettersOut()
					.println(
							"\tpublic java.lang.String get_textContent() {\n\t\treturn this._textContent;\n\t}");
			this.localOutputStream
					.getGetSettersOut()
					.println(
							"\tpublic void set_textContent(java.lang.String value) {\n\t\tthis._textContent= value;\n\t}");

			// import textContent
			this.imports.add(Ooxtextcontent.class.getCanonicalName());

		}

	}

	/**
	 * Write composing elements of a simpleType
	 * 
	 * @param tp
	 */
	private void writeElements(ZaxCSimpleType tp) {

		// TODO Handle restricitons, lists and unions

		// Get on with union
		// -----------------------------
		if (tp.getUnion() != null) {

			System.out.println("K001 found union in simpleType: "
					+ tp.getName());

			/*
			 * Add a value field that's a textContent
			 */
			LinkedList<String> types = new LinkedList<String>();
			Vector<String> annotations = new Vector<String>();

			annotations.add("@" + Ooxtextcontent.class.getName());
			types.add("java.lang.Object");

			this._writeFieldDeclaration("_value", this
					._getTypeRepresentation(types), null, OutputUtils
					.extractDocumentation(tp), annotations
					.toArray(new String[annotations.size()]));

			/*
			 * FIXME Write a setValue() method for each type allowed
			 */
			if (tp.getUnion().getMemberTypes() == null
					|| tp.getUnion().getMemberTypes().isEmpty())
				return;

			for (QNameBuffer qn : tp.getUnion().getMemberTypes()) {

				// Get class for matching class
				String cl = this.QName2Type(qn, null);
				this._writeGetterDeclaration(cl, "_value"
						+ this._getSimpleName(cl), null);

				this.localOutputStream.getGetSettersOut().println(
						"\t\tthis._value=new " + cl + "();");
				this.localOutputStream.getGetSettersOut().println(
						"\t\treturn (" + cl + ")this._value;");

				this._writeGetterEnd();

			}
			// Override toString() method in case we only have a union of
			// simpleTypes
			this.localOutputStream
					.getGetSettersOut()
					.println(
							"\tpublic String toString(){\n\treturn this._value.toString();\n\t}");

		}

		// Get on with restriction
		// ----------------------------
		if (tp.getRestriction() != null) {

			System.out.println("We have a restriction!");

			// Only Handle Enumeration
			if (tp.getRestriction().getEnumeration() != null) {
				for (com.idyria.osi.ooxoo.core.buffers.datatypes.constraints.Enumeration enu : tp
						.getRestriction().getEnumeration()) {
					String value = enu.getValue().toString();
					// System.out.println("ENU Value: "+value);

					// Write a setter
					// ----------------------

					// Declaration
					this.localOutputStream.getGetSettersOut().println(
							"\tpublic void set"
									+ value.replaceAll("[^0-9A-Za-z]", "")
									+ "(){");
					// Setter
					// Get Type
					String type = this
							.QName2Type(tp.getRestriction().getBase());
					// this.localOutputStream.getGetSettersOut().println(
					// "\t\tthis.value="
					// +CompilerUtils.class.getCanonicalName()+".createFromString("+
					// type
					// + ".class,\"" + value
					// + "\");");
					this.localOutputStream.getGetSettersOut().println(
							"\t\tthis._setValueFromString(\"" + value + "\");");
					// Declaration
					this.localOutputStream.getGetSettersOut().println("\t}");
					this.localOutputStream.getGetSettersOut().println();

				}

			}
		}

	}

	/**
	 * Writes the following pattern (attribute | attributeGroup)*,
	 * anyAttribute?). TODO (attribute | attributeGroup)*
	 * 
	 * @param tp
	 */
	protected void writeAbstractSelectAttributes(ZaxCAbstractSelectAttributes tp) {

		// AnyAttribute
		// --------------------------
		if (tp.getAnyAttribute() != null) {
			List<ZaxCAnyAttribute> anyl = new LinkedList<ZaxCAnyAttribute>();
			anyl.add(tp.getAnyAttribute());
			this.writeAny(anyl);
		}

		// attributes*
		// ------------------------
		if (tp.getAttributes().size() > 0) {
			// Import attribute
//			this.imports.add(OOXAttribute.class.getCanonicalName());
		}

		for (ZaxCAttribute att : tp.getAttributes()) {

			this.writeAttribute(att);

		}

		// attributeGroups
		// -------------------------
		for (ZaxCAttributeGroup attg : tp.getAttributeGroups()) {

			// Get reference
			TeaLogging.teaLogInfo("Type Mentions Attribute group: "
					+ attg.getRef());
			ZaxCAttributeGroup sattg = this.schema.getAtributeGroup(attg
					.getRef());
			for (ZaxCAttribute at : sattg.getAttributes())
				this.writeAttribute(at);

		}

	}

	/**
	 * writes the folowing pattern : (group | all | choice | sequence)?,
	 * ((attribute | attributeGroup)*, anyAttribute?)
	 * 
	 * @param tp
	 */
	protected void writeAbstractSelectElementsAndAttributes(
			ZaxCAbstractSelectElementsAndAttributes tp) {

		// Get on with sequence
		// ---------------------------
		if (tp.getSequence() != null) {

			/*
			 * Read sequence in a list because we can have nested sequences in
			 * the sequence
			 */
			LinkedList<ZaxCSequence> sequences = new LinkedList<ZaxCSequence>();
			sequences.add(tp.getSequence());

			while (!sequences.isEmpty()) {
				ZaxCSequence seq = sequences.poll();

				// Sequence -> group
				// -----------------------------------------------------------------
				if (seq.getGroups() != null && seq.getGroups().size() > 0) {

					for (ZaxCGroup grp : seq.getGroups()) {

						// Use ref attribute to get back to original
						if (grp.getRef() == null)
							continue;

						ZaxCGroup gref = this.schema.getGroup(grp.getRef());
						if (gref != null) {

							// (all | choice | sequence)?

							if (gref.getAll() != null) {

								// All -> add elements to sequence
								seq.getElements().addAll(
										gref.getAll().getElements());

							} else if (gref.getChoice() != null) {

								// Choice -> copy choice to sequence
								seq.getChoices().add(gref.getChoice());

							} else if (gref.getSequence() != null) {

								// Sequence -> copy sequence to sequence
								seq.getSequences().add(gref.getSequence());

							}

						}

					}

				} // END Sequence -> Group

				// Sequence -> elements
				// -----------------------
				for (ZaxCElement elt : seq.getElements()) {

					this.writeElement(elt);

				}

				// Sequence -> any
				// -----------------------
				if (seq.getAny().size() > 0) {
					writeAny(seq.getAny());
				} // END Sequence#any

				// Sequence -> Sequence
				// -----------------------
				if (seq.getSequences() != null && seq.getSequences().size() > 0)
					sequences.addAll(seq.getSequences());

				// Sequence -> Choices
				// -----------------------------------
				if (seq.getChoices() != null && seq.getChoices().size() > 0) {
					this
							.writeChoices((LinkedList<ZaxCChoice>) seq
									.getChoices());
				}

			}

		} // END sequence

		// Get on with choice
		// ----------------------------------
		else if (tp.getChoice() != null) {
			LinkedList<ZaxCChoice> chl = new LinkedList<ZaxCChoice>();
			chl.add(tp.getChoice());
			this.writeChoices(chl);
		}

		// FIXME Get on with all
		// ----------------------
		if (tp.getAll() != null) {

			for (ZaxCElement allelt : tp.getAll().getElements()) {
				this.writeElement(allelt);
			}

		}

		// Write attributes
		// ----------------------------
		this.writeAbstractSelectAttributes(tp);

	}

	/**
	 * Writes a single element as a field
	 * 
	 */
	private LinkedList<String> writeElement(ZaxCElement elt) {

		// Get the fieldName && localname
		String name = elt.getName() == null ? elt.getRef().getLocalPart()
				.toString() : elt.getName().toString();

		String fieldName = this._checkFieldName(name);
		TeaLogging.teaLogSevere("Writing element with name: " + elt.getName());

		// FIXME Prepare Annotations for attributes
		// ---------------------------------
		Vector<String> annotations = new Vector<String>();

		// Prepare target Namespace
		String targetNamespace = this.schema.getTargetNamespace().toString();

		// If we are referencing, maybe the namespace is not the same
		if (elt.getRef() != null && elt.getRef().getLocalPart() != null
				&& name.equals(elt.getRef().getLocalPart().toString())
				&& elt.getRef().getPrefix() != null) {
			targetNamespace = this.schema.getNsmap().get(
					elt.getRef().getPrefix().getValue());
		}
		// ooxnode
		annotations.add("@Ooxnode(localName=\"" + name
				+ "\",targetNamespace=\"" + targetNamespace + "\")");
		// ooxattribute
		String elementannotation = "@Ooxelement(";

		//-- Default value ?
		if (elt.getDefault()!=null) {
			elementannotation+="defaultValue=\""+elt.getDefault()+"\"";
		}
		
		/*
		 * For the type it is a bit more complicated, if there are no
		 * constraints (multiple or so) and we have a ref or name+type, then we
		 * can have a simple field name associated with a resolved type. If we
		 * have a constraint (multiple or so..) or an anonymous declaration,
		 * then we have to create a nested class
		 */

		// Define fisrt type
		// ------------------------------------------------------
		TeaLogging.teaLogInfo("Writting out element in class: " + elt.getName()
				+ " => " + elt.getType() + " // " + elt.getRef());
		LinkedList<String> types = new LinkedList<String>();
		if (elt.getRef() != null && elt.getRef().getLocalPart() != null) {

			// By reference
			// -------------------
			String cl = this.QName2Type(elt.getRef(), JavaOutputCell.PRE_ETYPE);
			TeaLogging.teaLogInfo("Element references class: " + cl);
			if (cl != null)
				types.add(cl);

		} else if (elt.getName() != null && elt.getType() != null) {

			// By name+type
			// -----------------------------
			String cl = this.QName2Type(elt.getType(), null);
			if (cl != null)
				types.add(cl);

		} else if (elt.getName() != null && elt.getType() == null
				&& elt.getRef() == null && elt.getComplexType() != null) {

			// FIXME Anonymous type declaration
			// ---------------------------------------------------------
			TeaLogging.teaLogInfo("Type " + elt.getName()
					+ " has no type and is then anonymously declared");
			// Check the base type is not hidden using extension
			// ------------------------------------
			if (elt.getComplexType().getSimpleContent() != null
					&& elt.getComplexType().getSimpleContent().getExtension() != null) {
				TeaLogging.teaLogInfo("Type was hidden in local definition");
				extension ext = elt.getComplexType().getSimpleContent()
						.getExtension();
				// if (ext.getSequence()==null && ext.getAll()==null &&
				// ext.getAttributes()==null) {
				elt.setType(ext.getBase());
				// By name+type
				// -----------------------------
				String cl = this.QName2Type(elt.getType(), null);
				if (cl != null)
					types.add(cl);
				// }
			} else {

				// Get the complex Type
				ZaxCComplexType acpt = elt.getComplexType();
				acpt.setName(elt.getName());

				// Create a new output stream
				JavaOutputStream jos = new JavaOutputStream();
				JavaOutputStream temp = this.localOutputStream;
				this.localOutputStream = jos;

				// Write nested class declaration
				String classname = this.writeClassStart(acpt);
				this.writeElements(acpt);

				// Set the type
				// types.add(this.currentClassName.getLast());
				types.add(classname);

				this.writeClassEnd();

				// Restore localoutputstream
				this.localOutputStream = temp;

				// Flush the nested class
				try {
					this.rootOutputStream.getNestedClassesOut().write(
							jos.flush());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// Get type representation
		// ----------------------------------------
		/*
		 * / We always have to have a type derivating from OOXElement If we have
		 * a defualt or fixed value, switch to String type
		 */
		if (elt.getDefault() != null || elt.getFixed() != null) {

			// Default value -> SimpleElement + XSDString
			types.remove();
			types.add(XSDStringBuffer.class.getCanonicalName());
			// FIXME Check
			// } else if (types.size() > 0
			// && !OOXNode.class.isAssignableFrom(types.peek())) {
			//
			// // Not an OOXNode -> OOXSimpleElement< type>
			// types.addFirst(OOXSimpleElement.class);

		} else if (types.size() == 0) {

			// No type => OOXElement for safety
//			types.add(OOXElement.class.getCanonicalName());
		}

		// Write element getter
		// ---------------------------------------

		// Declare getter
		// ------------------------------------

		// Prepare doc
		String getterdoc = null;
		if (elt.getDefault() != null || elt.getFixed() != null) {
			getterdoc = "This element has a default or fixed value. <BR>This getter will return this value and no setter is available";
		}

		if (elt.getMaxOccurs().isUnbounded()
				|| elt.getMaxOccurs().getValue() > 1) {
			types
					.addFirst(com.idyria.osi.ooxoo.core.wrap.OOXList.class
							.getCanonicalName());
		}

		_writeGetterDeclaration(this._getTypeRepresentation(types, null),
				fieldName, getterdoc);

		// FIXME Check constraints that would require a nested class
		// -----------------------------------------------------
		boolean nest = false;
		Vector<Class> implementations = new Vector<Class>();
		if (elt.isNillable()) {

			nest = true;
			implementations.add(OOXNillable.class);
		}

		// First nested class
		// --------------------------------
		if (nest) {

			// Field output: if(this.field == null) ...instanciate...in a nested form
			this.localOutputStream.getGetSettersOut().println(
					"\t\tif (this." + fieldName + "==null)");
			this.localOutputStream.getGetSettersOut()
					.print(
							"\t\t\t" + fieldName + "= ");
			
			// Declare nested class (we do exclude OOXList :)
			_writeGetterNestedClassDeclaration("_nested", this
					._getTypeRepresentation(types, OOXList.class
							.getCanonicalName()), implementations
					.toArray(new Class[implementations.size()]),
					"This nested class extends the base type to implement specific constraints");

			// Write default constructor used to give namespace and localname
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\tpublic _nested() {");

			// this.getSettersOut.println("\t\t\t\tsuper"
			// + "(AnyURI.fromStringNoValidation(\""
			// + this.schema.getTargetNamespace().toString()
			// + "\"),NCNameBuffer.fromStringNoValidation(\"" + name + "\");");

			// Default value if necessary
			if (elt.getDefault() != null || elt.getFixed() != null) {
				this.localOutputStream.getGetSettersOut().println(
						"\t\t\t\tthis.value = "
								+ CompilerUtils.class.getCanonicalName()
								+ "createFromString(Class<"
								+ XSDStringBuffer.class.getCanonicalName()
								+ ">" + elt.getDefault() == null ? elt
								.getFixed().toString() : elt.getDefault()
								.toString()
								+ ");");
			}

			this.localOutputStream.getGetSettersOut().println("\t\t\t}");

			// Override setValue in case of a default value
			if (elt.getDefault() != null || elt.getFixed() != null) {
				this.localOutputStream.getGetSettersOut().println(
						"\t\t\tpublic void setValue("
								+ XSDStringBuffer.class.getSimpleName()
								+ " val) {");
				this.localOutputStream.getGetSettersOut().println("\t\t\t}");
			}

			// Nillable implementation
			// ----------------------------------------------
			if (implementations.contains(OOXNillable.class)) {

				// Implement Nillable
				// ---------------------------------
				this.localOutputStream.getGetSettersOut().println(
						"\t\t\tprivate boolean nil = false;");
				this.localOutputStream.getGetSettersOut().println();
				this.localOutputStream.getGetSettersOut().println();

				this.localOutputStream
						.getGetSettersOut()
						.println(
								"\t\t\tpublic void setNillabed(boolean val) {\n\t\t\t\t this.nil = val; \n\t\t\t}");
				this.localOutputStream.getGetSettersOut().println();
				this.localOutputStream
						.getGetSettersOut()
						.println(
								"\t\t\tpublic boolean isNillabed() {\n\t\t\t\t return this.nil; \n\t\t\t}");
			}

			// End it up
			_writeGetterNestedClassEnd();

		}

		// Multiplicity => 2nd nested classe
		// -------------------------------------------------
		if (elt.getMaxOccurs().isUnbounded()
				|| elt.getMaxOccurs().getValue() > 1) {

			String paramtype = nest == true ? "_nested" : this
					._getTypeRepresentation(types, OOXList.class
							.getCanonicalName());

			// import
			if (!this.imports.contains(OOXList.class.getCanonicalName()))
				this.imports.add(OOXList.class.getCanonicalName());

			// Declare nested class
			_writeGetterNestedClassDeclaration(
					"_nestedmultiple",
					OOXList.class.getSimpleName() + "<" + paramtype + ">",
					null,
					"This nested class is used to implement multiple constraint. It extends the OOXList type with the base type as parametrization class");

			// default constructor for min and max
			// ---------------------------------------------------------------
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\tpublic _nestedmultiple() {");
			this.localOutputStream.getGetSettersOut().print(
					"\t\t\t\tsuper(new NonNegativeIntegerBuffer("
							+ elt.getMinOccurs().getValue() + "),");

			if (elt.getMaxOccurs().isUnbounded())
				this.localOutputStream.getGetSettersOut().print(
						"new MaxOccursBuffer(true));\n");
			else
				this.localOutputStream.getGetSettersOut().print(
						"new MaxOccursBuffer(" + elt.getMaxOccurs().getValue()
								+ "));\n");

			this.localOutputStream.getGetSettersOut().println("\t\t\t}");
			this.localOutputStream.getGetSettersOut().println();

			// Implements add
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\tpublic " + paramtype + " add() {");

			// this.getSettersOut.println("\t\t\t\t" + paramtype + " o = new "
			// + paramtype + "(AnyURI.fromString(\""
			// + this.schema.getTargetNamespace().toString()
			// + "\"),(NCNameBuffer)NCNameBuffer.fromString(\"" + name +
			// "\")) ;");
			this.localOutputStream.getGetSettersOut().print(
					"\t\t\t\t" + paramtype + " o = new " + paramtype + "()");
			// IF type is abstract, fake nestclass
			// if (elt.isAbstract()) {
//			this.localOutputStream.getGetSettersOut().println("{");
//			this.localOutputStream.getGetSettersOut().print("\t\t\t\t}");
			// }
			this.localOutputStream.getGetSettersOut().println(";");

			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t\tthis.addElement(o);");
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t\treturn o;");
			this.localOutputStream.getGetSettersOut().println("\t\t\t}");

			// import maxOccurs && NonNegativeInteger
			// -------------------------------
			this.imports.add(MaxOccursBuffer.class.getCanonicalName());
			this.imports.add(NonNegativeIntegerBuffer.class.getCanonicalName());

			// End it up
			this._writeGetterNestedClassEnd();

		}

		// Return type
		// -----------------------

		if (elt.getMaxOccurs().isUnbounded()
				|| elt.getMaxOccurs().getValue() > 1) {

			// For multiple, condition on null value the creation
			this.localOutputStream.getGetSettersOut().println(
					"\t\tif (this." + fieldName + "==null)");
			// Multiple -> return _nestedmultiple
			// ------------------------------------------------------
			this.localOutputStream.getGetSettersOut()
					.println(
							"\t\t\t" + fieldName + "= new " + "_nestedmultiple"
									+ "();");
		} else if (nest) {
			// Control creation only for simple fields
			this.localOutputStream.getGetSettersOut()
					.println("\t\tif (this."+fieldName+" == null && create)");
			// Nested -> return new _nested()
			// ------------------------------------------------------
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t" + fieldName + "= new " + "_nested();");
		} else {
			// Control creation only for simple fields
			this.localOutputStream.getGetSettersOut()
					.println("\t\tif (this."+fieldName+" == null && create)");
			// Other
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t" + fieldName + "= new "
							+ this._getTypeRepresentation(types) + "();");
			//-- Default Value ??
			if (elt.getDefault()!=null) {
				this.localOutputStream.getGetSettersOut().println(
						"\t\telse if(this."+fieldName+"==null)");
				this.localOutputStream.getGetSettersOut().println(
						"\t\t\treturn new "+this._getTypeRepresentation(types)+"(\""+elt.getDefault()+"\");");
			}
		}

		this.localOutputStream.getGetSettersOut().println(
				"\t\treturn " + fieldName + ";");

		_writeGetterEnd();

		// import types
		// for (String cl : types)
		// this.imports.add(cl);

		// Write getter proxy for getField(false)
		// -----------------------------------------------------------
		_writeGetterProxy(this._getTypeRepresentation(types, null), fieldName,
				getterdoc);

		// Write type field
		// -----------------
		// close attribute annotaiton && add
		elementannotation += ")";
		annotations.add(elementannotation);
		
		_writeFieldDeclaration(fieldName, this._getTypeRepresentation(types,
				null), "null", elt.getDefault()!=null?elt.getDefault().getValue():null, annotations.toArray(new String[annotations
				.size()]));

		// Write setter
		// -----------------------------
		_writeSetter(this._getTypeRepresentation(types), fieldName, getterdoc);

		return types;

	}

	/**
	 * writes a single attribute
	 * 
	 * @param att
	 */
	private void writeAttribute(ZaxCAttribute att) {

		// FIXME try to prohibitate the use of the attribute for this particular
		// type (find and override all related methods and make theam do
		// nothing:) )
		if (att.getUse() != null && att.getUse().equals("prohibited"))
			return;

		// Get the fieldName && localname
		// --------------------------------------
		String name = att.getName() == null ? att.getRef().getLocalPart()
				.toString() : att.getName().toString();

		String fieldName = this._checkFieldName(name);

		// FIXME Prepare Annotations for attributes
		// ---------------------------------
		Vector<String> annotations = new Vector<String>();

		// ooxnode
		annotations.add("@Ooxnode(localName=\"" + name
				+ "\",targetNamespace=\""
				+ this.schema.getTargetNamespace().toString() + "\")");
		// ooxattribute
		String attributeannotation = "@Ooxattribute(";

		//-- Default value ?
		if (att.getDefault()!=null) {
			attributeannotation+="defaultValue=\""+att.getDefault()+"\",";
		}
		
		// Define type
		// ------------------------------------------------------
		LinkedList<String> types = new LinkedList<String>();

		if (att.getRef() != null && att.getRef().getLocalPart() != null) {

			// By reference
			// -------------------
			String cl = this.QName2Type(att.getRef(), "attributes");
			if (cl != null)
				types.add(cl);

		} else if (att.getName() != null && att.getType() != null) {

			// By name+type
			// -----------------------------
			String cl = this.QName2Type(att.getType(), "attributes");
			if (cl != null)
				types.add(cl);

		} else if (att.getName() != null && att.getType() == null
				&& att.getRef() == null && att.getSimpleType() != null) {

			// FIXME Anonymous type declaration
			// ---------------------------------------------------------
			// this.nestedClassesOut.println("\t\tpublic class
			// "+att.getName()+"")
			if (att.getSimpleType().getRestriction() != null)
				types.add(this.QName2Type(att.getSimpleType().getRestriction()
						.getBase(), null));
		}

		// Get type representation
		// ----------------------------------------
		/*
		 * if the type is not an attribute, we have to switch to attribute with
		 * parametrization. This is also valid if we have a default value with
		 * fixed or default, then we'll have OOXAttribute<String> with an
		 * overriding of the setValue() method and the default value set in the
		 * construtor
		 */
		if (att.getDefault() != null || att.getFixed() != null) {

			// Default value
			// types.remove();
			// types.add(OOXAttribute.class);
			// types.add(XSDString.class);

		} /*
		 * else if (types.size()>0 &&
		 * !OOXAttribute.class.isAssignableFrom(types.getFirst())) { // Case we
		 * have something other than an OOXAtrtibute
		 * types.addFirst(OOXAttribute.class); }
		 */else if (types.size() == 0) {

			// Case we did not validate the type
//			types.addFirst(OOXAttribute.class.getCanonicalName());
		}

		// Make the import of type
		// for (String cl : types)
		// this.imports.add(cl);

		// Declare getter
		// ------------------------------------
		String getterdoc = null;
		if (att.getDefault() != null || att.getFixed() != null) {
			getterdoc = "This attribute has a default or fixed value. <BR>This getter will return this value and no setter is available";
		}
		_writeGetterDeclaration(this._getTypeRepresentation(types), fieldName,
				getterdoc);

		// FIXME Check constraints that would require a nested class
		// -----------------------------------------------------
		boolean nest = false;
		Vector<Class> implementations = new Vector<Class>();

		// Use attribute is designed to work with annotations
		// ----------------------------------------------------------------------------------
		if (att.getUse() != null
				&& (att.getUse().equals("required") || att.getUse().equals(
						"prohibited"))) {

			attributeannotation += "use=\"required\"";
		}

		// First nested class
		// --------------------------------
		if (nest) {

		}

		// Return the element
		// -------------------------------------
		this.localOutputStream.getGetSettersOut().println("\t\tif (this."+fieldName+" == null && create) {");
		if (nest) {

			// FIXME Contruct nested class

		} else if (att.getDefault() != null) {

			// FIXME Find the correct construction attitude
			// -------------------------------------------------
			String trep = this._getTypeRepresentation(types);

			// try to find fromStringNoValidation
			// -------------------------------------------
			// this.getSettersOut
			// .println("\t\t\ttry {");
			//			
			// this.getSettersOut
			// .println("\t\t\tif ("
			// + trep
			// + ".getClass().getMethod(\"fromStringNovalidation\")!=null) {");
			// this.getSettersOut
			// .println("\t\t\t\t"
			// + trep
			// + ".getClass().getMethod(\"fromStringNovalidation\")!=null) {");
			//
			//			
			// this.getSettersOut
			// .println("\t\t\tcatch (NoSuchMethodException ex {");

			// Construct default value
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t" + fieldName + "= new "
							+ this._getTypeRepresentation(types) + "();");

		} else if (att.getFixed() != null) {

			// FIXME Construct fixed value && override setValue() method
			// this.getSettersOut.println("\t\t\t"
			// + fieldName
			// + "= "
			// + this._getTypeRepresentation(types)
			// + ".fromStringNoValidation(\""
			// + (att.getDefault() != null ? att.getDefault().toString()
			// : att.getFixed().toString()) + "\") ");

			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t" + fieldName + "= "
							+ this._getTypeRepresentation(types) + "(); ");

		} else {
			// Construct type
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t" + fieldName + "= new "
							+ this._getTypeRepresentation(types) + "();");
		}

		this.localOutputStream.getGetSettersOut().println("\t\t}");
		
		//-- Default Value ??
		if (att.getDefault()!=null) {
			this.localOutputStream.getGetSettersOut().println(
					"\t\telse if(this."+fieldName+"==null)");
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\treturn new "+this._getTypeRepresentation(types)+"(\""+att.getDefault()+"\");");
		}
		
		
		this.localOutputStream.getGetSettersOut().println(
				"\t\treturn " + fieldName + ";");

		// Close getter
		// -----------------------------
		this._writeGetterEnd();

		// Write getter proxy
		// ---------------------------------
		_writeGetterProxy(this._getTypeRepresentation(types, null), fieldName,
				getterdoc);

		// Write field Declaration
		// ---------------------------------------

		// , remove last ',' close attribute annotaiton && add
		attributeannotation= attributeannotation.endsWith(",") ? attributeannotation.substring(0, attributeannotation.length()-1) : attributeannotation;
		attributeannotation += ")";
		annotations.add(attributeannotation);

		// write
		this._writeFieldDeclaration(fieldName, this
				._getTypeRepresentation(types), null, null, annotations
				.toArray(new String[annotations.size()]));

		// Write setter
		// -----------------------------
		_writeSetter(this._getTypeRepresentation(types), fieldName, getterdoc);

		// OOXOO Special Check toString appInfo
		//---------------
		if (att.getAnnotation()!=null && att.getAnnotation().getAppInfos()!=null)
			for (AppInfo appInfo : att.getAnnotation().getAppInfos()) {
				try {
					System.err.println("================== APPINFO in Attribute =====================");
					System.err.println(appInfo.getContent().trim());
				if (appInfo.getContent().trim().equals("toString")) 
					this.ooxooSpecialToString = att.getName().getValue();
				} catch (Exception e) {
					
				}
			}
		
	}

	/**
	 * Writes a ZaxcAny element
	 * 
	 * @param any
	 */
	private void writeAny(List<? extends ZaxCWildCard> anyl) {

		HashMap<String, MaxOccursBuffer> namespaces = new HashMap<String, MaxOccursBuffer>();

		// ! true = element, false =attribute
		boolean anyelt = true;
		boolean checked = false; // just to check any or anyattribute on the
		// first element

		// Fill the map for all anys informations
		for (ZaxCWildCard any : anyl) {

			// Do we have ZxCAnyAttribute or ZaxCAny ??
			if (!checked) {
				ZaxCWildCard tp = any;
				if (tp instanceof ZaxCAnyAttribute) {
					anyelt = false;
				}
				checked = true;
			}

			/*
			 * Regroup all any declarations in a single type and record each
			 * namespace allowed and its number allowed
			 */

			if (any == null) {
				System.out.println("Any value is NULL");
				// continue;
			}

			// Take care of namespace
			// TODO Ignored processContent
			// -----------------------------------------------
			Object ns = any.getNamespace();

			// If we have a single String
			// ------------------------------------------------
			if (ns instanceof String) {

				String nss = (String) ns;
				// Add key if not already defined
				if (!namespaces.containsKey(nss)) {
					int[] itbl = new int[2];
					itbl[0] = 0;
					itbl[1] = 0;
					namespaces.put(nss, new MaxOccursBuffer());
				}

				// increase authorized number if not already unbounded
				if (!anyelt) {
					namespaces.get(nss).setUnbounded();
				} else if (anyelt && !namespaces.get(nss).isUnbounded()) {
					if (((ZaxCAny) any).getMaxOccurs().isUnbounded())
						namespaces.get(nss).setUnbounded();
					else
						namespaces.get(nss).setValue(
								namespaces.get(nss).getValue()
										+ ((ZaxCAny) any).getMaxOccurs()
												.getValue());
				}

			} else if (ns instanceof List) {

				// Foreach List
				for (Object nso : (List) ns) {

					String nss = "";

					// if we have an URI or String, set the val value
					if (nso instanceof AnyURIBuffer) {
						nss = ((AnyURIBuffer) nso).toString();
					} else if (nso instanceof String)
						nss = (String) nso;

					// Update map
					if (!namespaces.containsKey(nss)) {
						int[] itbl = new int[2];
						itbl[0] = 0;
						itbl[1] = 0;
						namespaces.put(nss, new MaxOccursBuffer());
					}

					// / increase authorized number if not already unbounded
					if (!anyelt) {
						namespaces.get(nss).setUnbounded();
					} else if (anyelt && !namespaces.get(nss).isUnbounded()) {
						if (((ZaxCAny) any).getMaxOccurs().isUnbounded())
							namespaces.get(nss).setUnbounded();
						else
							namespaces.get(nss).setValue(
									namespaces.get(nss).getValue()
											+ ((ZaxCAny) any).getMaxOccurs()
													.getValue());
					}
				}

			}
		} // End Map filling

		// now write :)

		// Prepare Type
		// --
		LinkedList<String> types = new LinkedList<String>();
		types.add(OOXAny.class.getCanonicalName());
		String fieldname = "";
		String listype = null;
		String doc = null;
		if (!anyelt) {
			fieldname = "_anyAttribute";
			listype = AbstractDataTypesBuffer.class.getCanonicalName();
			doc = "This field is an interface to adding any attribute in respect with targetNamespaces restrictions";
		} else {
			listype = ElementBuffer.class.getCanonicalName();
			fieldname = "_any";
			doc = "This field is an interface to adding any element in respect with targetNamespaces restrictions";
		}
		types.add(listype);

		// Write field
		// --------------------
		this.localOutputStream.getFieldsDeclarationOut().println(
				"\t@" + Ooxany.class.getSimpleName());
		_writeFieldDeclaration(fieldname, this._getTypeRepresentation(types,
				listype), "null", doc);
		this.imports.add(Ooxany.class.getCanonicalName());

		// Import OOXAnyElement if necessary
		// ----------------------------
		this.imports.add(OOXAny.class.getCanonicalName());

		// Write getter
		// ------------------------------
		this._writeGetterDeclaration(this
				._getTypeRepresentation(types, listype), fieldname, null);

		// // Write nested class
		// this._writeGetterNestedClassDeclaration("_nested", this
		// ._getTypeRepresentation(types, null), null, null);
		//
		// // Write constructor defining params
		// this.getSettersOut.println("\t\t\tpublic _nested() {");
		//
		//		
		//
		// // Import OOXElementList
		// this.imports.add(listype.getCanonicalName());
		//
		// // close constructor
		// this.getSettersOut.println("\t\t\t}");
		//
		// // Close nested
		// this._writeGetterNestedClassEnd();

		// Return element
		this.localOutputStream.getGetSettersOut().println(
				"\t\t// Return element\n\t\t//--------------------");
		this.localOutputStream.getGetSettersOut().println("\t\tif (create)");

		this.localOutputStream.getGetSettersOut().println(
				"\t\t\t" + fieldname + "= new OOXAny() {");

		this.localOutputStream.getGetSettersOut().println("\t\t\t\t{");

		// add namespaces constraint declarations
		for (Entry<String, MaxOccursBuffer> ent : namespaces.entrySet()) {

			this.localOutputStream.getGetSettersOut().print(
					"\t\t\t\t\tsuper.namespaces.put(\"" + ent.getKey() + "\"");

			if (ent.getValue().isUnbounded())
				this.localOutputStream.getGetSettersOut().print(
						",new MaxOccursBuffer(true));\n");
			else
				this.localOutputStream.getGetSettersOut()
						.print(
								",new MaxOccursBuffer(" + ent.getValue().getValue()
										+ "));\n");
		}

		if (!anyelt)
			this.localOutputStream.getGetSettersOut().println(
					"\t\t\t\t\tsuper.setWaitForAttributes();");

		this.localOutputStream.getGetSettersOut().println("\t\t\t\t}");

		this.localOutputStream.getGetSettersOut().println("\t\t\t};");

		this.localOutputStream.getGetSettersOut().println(
				"\t\treturn " + fieldname + ";");

		// import maxOccurs
		// -------------------------------
		this.imports.add(MaxOccursBuffer.class.getCanonicalName());

		// Close getter
		// ---------------------
		this._writeGetterEnd();

		// Write Proxy
		// ----------------------
		this._writeGetterProxy(this._getTypeRepresentation(types, listype),
				fieldname, null);

	}

	/**
	 * Write choices FIXME Support for everything Currently, choice support is
	 * limited to finding all the &lt;elements && giving the unbounded access
	 * 
	 * FIXME Support for imbricated choices
	 * 
	 * @param choices
	 */
	private void writeChoices(LinkedList<ZaxCChoice> choices) {

		// Check list validity
		if (choices == null || choices.size() == 0)
			return;

		// Create a map to store elements member of the choice associated with
		// how many times they can be selected
		HashMap<ZaxCElement, MaxOccursBuffer> totalelements = new HashMap<ZaxCElement, MaxOccursBuffer>() {

			public MaxOccursBuffer put(ZaxCElement k, MaxOccursBuffer v) {

				boolean found = false;

				// Check the element has not already been selected
				for (ZaxCElement e : this.keySet()) {

					if (k.getName() != null
							&& e.getName() != null
							&& k.getName().toString().equals(
									e.getName().toString())) {
						found = true;
						break;
					} else if (k.getRef() != null
							&& e.getRef() != null
							&& k.getRef().toString().equals(
									e.getRef().toString())) {
						found = true;
						break;
					}
				}

				if (!found)
					return super.put(k, v);
				else
					return v;

			}

		};

		// this map will store the Any elements found
		HashMap<ZaxCAny, MaxOccursBuffer> totalany = new HashMap<ZaxCAny, MaxOccursBuffer>();

		// Foreach
		while (!choices.isEmpty()) {
			ZaxCChoice choice = choices.poll();

			NonNegativeIntegerBuffer minbase = choice.getMinOccurs();
			MaxOccursBuffer maxbase = choice.getMaxOccurs();

			// Choice
			// -----------------
			if (choice.getChoices().size() > 0)
				for (ZaxCChoice nestedchoice : choice.getChoices()) {

					nestedchoice.getMinOccurs().setValue(minbase.getValue());
					if (maxbase.isUnbounded())
						nestedchoice.getMaxOccurs().setUnbounded();
					else
						nestedchoice.getMaxOccurs()
								.setValue(maxbase.getValue());

					choices.add(nestedchoice);
				}

			// Groups
			// --------------------
			for (ZaxCGroup grp : choice.getGroups()) {

				// Use ref attribute to get back to original
				if (grp.getRef() == null)
					continue;
				ZaxCGroup gref = this.schema.getGroup(grp.getRef());

				if (gref != null) {

					if (gref.getAll() != null) {

						// Group -> all
						// -----------------------------------

						List<ZaxCElement> elts = gref.getAll().getElements();
						for (ZaxCElement toadd : elts) {

							// Add elements to the selection with max
							// possibilities = minbase * maxoccurs
							// because we can choose x times this group in which
							// we can put x times this element
							totalelements.put(toadd, toadd.getMaxOccurs());
						}

					} else if (gref.getChoice() != null)
						choices.addLast(gref.getChoice());
					else if (gref.getSequence() != null) {

						// TODO Group -> Sequence [add sequence to sequences to
						// study further in]
						// -----------------------------------------
						ZaxCSequence seq = gref.getSequence();
						// seq.getMaxOccurs().setValue(
						// maxbase * seq.getMaxOccurs().getValue());
						choice.getSequences().add(seq);
					}
				}

			} // END Choice -> Group

			// Sequences
			// ------------------------------------------
			LinkedList<ZaxCSequence> sequences = new LinkedList<ZaxCSequence>();
			sequences.addAll(choice.getSequences());

			while (!sequences.isEmpty()) {
				ZaxCSequence seq = sequences.poll();

				// Sequences
				// --------------------------
				sequences.addAll(seq.getSequences());

				// Choices
				// ---------------------
				choices.addAll(seq.getChoices());

				// Groups
				// --------------------
				for (ZaxCGroup grp : seq.getGroups()) {

					// Use ref attribute to get back to original
					if (grp.getRef() == null)
						continue;
					System.out.println("Reading groups (size list:"
							+ choice.getGroups().size() + ")");

					ZaxCGroup gref = this.schema.getGroup(grp.getRef());

					if (gref != null) {

						if (gref.getAll() != null) {

							// Group -> all
							// -----------------------------------

							List<ZaxCElement> elts = gref.getAll()
									.getElements();
							for (ZaxCElement toadd : elts) {

								// Add elements to the selection with max
								// possibilities = minbase * maxoccurs
								// because we can choose x times this group in
								// which
								// we can put x times this element
								totalelements.put(toadd, toadd.getMaxOccurs());
							}

						} else if (gref.getChoice() != null)
							choices.addLast(gref.getChoice());
						else if (gref.getSequence() != null) {

							// TODO Group -> Sequence [add sequence to sequences
							// to study further in]
							// -----------------------------------------
							ZaxCSequence nseq = gref.getSequence();
							// nseq.getMaxOccurs().setValue(
							// maxbase * seq.getMaxOccurs().getValue());
							sequences.add(nseq);
						}
					}

				} // END Sequence -> Group

				// Chocie -> Sequence -> Element
				// ------------------------------------
				for (ZaxCElement selt : seq.getElements()) {
					totalelements.put(selt, selt.getMaxOccurs());
				}

			} // END Choice -> Sequence

			// Elements
			// --------------------------
			for (ZaxCElement celt : choice.getElements()) {
				totalelements.put(celt, maxbase);
			}

			// Anys
			for (ZaxCAny an : choice.getAny()) {
				totalany.put(an, new MaxOccursBuffer());
			}

			// Now its read -> output elements
			// ---------------------------------------------------------

			// FIXME First, declare nested class
			// --------------------------------------------------------

			// Create a new output stream for the nested class
			JavaOutputStream nestedout = new JavaOutputStream();
			this.localOutputStream = nestedout;

			// Write class declaration (stays in main stream)
			String choiceclassName = "choice" + this.choicesct;
			nestedout.getClassdefOut().println(
					"\tpublic class " + choiceclassName + " extends "
							+ OOXChoice.class.getSimpleName() + " {");
			nestedout.getClassdefOut().println();

			// Write elements
			// --------------------------------------------

			// FIXME Adjust numbers to 0-unbounded
			MaxOccursBuffer max = new MaxOccursBuffer();
			max.setUnbounded();
			NonNegativeIntegerBuffer min = new NonNegativeIntegerBuffer();
			min.setValue(0);

			for (ZaxCElement elt : totalelements.keySet()) {

				elt.setMinOccurs(min);
				elt.setMaxOccurs(max);
				LinkedList<String> typerep = this.writeElement(elt);

				// Write a chooser in the real getter section
				// -------------------------------------------------------------------
				String name = elt.getName() == null ? elt.getRef()
						.getLocalPart().toString() : elt.getName().toString();

				// FIXME If y check one more, the returned name will be a new
				// one -> methoid to get le latest attributed name
				String fieldName = this._checkFieldName(name);

				nestedout.getGetSettersOut().println("\t/**");
				nestedout.getGetSettersOut().println(
						"\t  * Chooses the field : " + name);
				nestedout.getGetSettersOut().println(
						"\t  * @return A selected element instance ");
				nestedout
						.getGetSettersOut()
						.println(
								"\t  * @throws OOXChoiceLimitExceeded - If you have exceeded the choice limit for that element");
				nestedout.getGetSettersOut().println("\t */");
				nestedout.getGetSettersOut().println(
						"\tpublic "
								+ this._getTypeRepresentation(typerep,
										OOXList.class.getCanonicalName())
								+ " choose" + this.choicesct
								+ this.firstUpper(name) + "(){");

				nestedout.getGetSettersOut().println(
						"\t\tif (get_" + className + choiceclassName + "().get"
								+ this.firstUpper(fieldName) + "()==null) ");
				nestedout.getGetSettersOut().println(
						"\t\t\treturn get_" + className + choiceclassName
								+ "().get" + this.firstUpper(fieldName)
								+ "(true).add();");

				nestedout.getGetSettersOut().println("\t\telse");
				nestedout.getGetSettersOut().println(
						"\t\t\treturn get_" + className + choiceclassName
								+ "().get" + this.firstUpper(fieldName)
								+ "().add();");
				nestedout.getGetSettersOut().println("\t}");

			} // END FOR Elements

			// Write ANys
			// ------------------------
			// for (ZaxCAny an:totalany.keySet()) {
			// this.writeAny(any)
			//				
			// }
			LinkedList<ZaxCAny> anys = new LinkedList<ZaxCAny>();
			anys.addAll(totalany.keySet());
			this.writeAny(anys);

			// Close nested class (declaration stays in main class)
			this.writeClassEnd();

			// Restore the normal outputstreal
			this.localOutputStream = rootOutputStream;

			// Flush nestedout into the nested section of the local outputstream
			try {
				this.localOutputStream.getNestedClassesOut().write(
						nestedout.flush());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// declare a field for this choice
			// -----------------------------------------------------------
			this.localOutputStream.getFieldsDeclarationOut().println(
					"\t@Ooxchoice");
			this.localOutputStream.getFieldsDeclarationOut().println(
					"\tprivate " + className + "." + choiceclassName + " _"
							+ choiceclassName + " = new " + className + "."
							+ choiceclassName + "();");

			// Write a getter for this field
			// ---------------------------------------------
			this._writeGetterDeclaration(className + "." + choiceclassName, "_"
					+ className + choiceclassName,
					"Retrieve value for choice field");
			this.localOutputStream.getGetSettersOut().println(
					"\t\treturn this._" + choiceclassName + ";");
			this._writeGetterEnd();

			this._writeGetterProxy(className + "." + choiceclassName, "_"
					+ className + choiceclassName,
					"Retrieve value for choice field");

			// import OOXChoice & Ooxchoice annotation
			this.imports.add(OOXChoice.class.getCanonicalName());
			this.imports.add(Ooxchoice.class.getCanonicalName());

			this.choicesct++;

		}

	}

	private void writeClassEnd() {

		this.writeClassEnd(this.localOutputStream);

	}

	private void writeClassEnd(JavaOutputStream targetStream) {

		targetStream.getGetSettersOut().println('}');

		// Remove one level of classes
		if (currentClassName.size() > 0)
			currentClassName.removeLast();

	}

	/**
	 * Writes a filed declaration : private type name = init;
	 * 
	 * @param fieldName
	 * @param type
	 * @param inital
	 * @param doc
	 */

	private void _writeFieldDeclaration(String fieldName, String type,
			String inital, String doc) {

		this._writeFieldDeclaration(fieldName, type, inital, doc, null);

	}

	/**
	 * Writes a filed declaration : private type name = init;
	 * 
	 * @param fieldName
	 * @param type
	 * @param inital
	 * @param doc
	 */
	private void _writeFieldDeclaration(String fieldName, String type,
			String inital, String doc, String[] annotations) {

		if (doc != null) {
			// FIXME Write documentation
			// Write documentation

			// TODO add multiple doc declaration [currently just the first one]
			this.localOutputStream.getClassdefOut().println("/**");
			this.localOutputStream.getClassdefOut().println(" *");

			for (String docline : doc.split("\n"))
				this.localOutputStream.getClassdefOut()
						.println(" * " + docline);

			this.localOutputStream.getClassdefOut().println(" *");
			this.localOutputStream.getClassdefOut().println(" */");

		}

		// Write annotations
		if (annotations != null && annotations.length > 0) {

			for (String anno : annotations) {
				this.localOutputStream.getFieldsDeclarationOut().println(
						"\t" + anno);
			}

			// import
			this.imports.add(Ooxattribute.class.getCanonicalName());
			this.imports.add(Ooxelement.class.getCanonicalName());
			this.imports.add(Ooxnode.class.getCanonicalName());

		}

		this.localOutputStream.getFieldsDeclarationOut().println(
				"\tprivate " + type + " " + fieldName + " = " + inital + ";");
		this.localOutputStream.getFieldsDeclarationOut().println();

	}

	/**
	 * Writes a getter declaration : public ret getFieldname (boolean create) {
	 * 
	 * @param ret
	 * @param fieldname
	 * @param doc
	 *            - an associated documentation
	 */
	private void _writeGetterDeclaration(String ret, String fieldname,
			String doc) {

		// TODO Write doc
		this.localOutputStream.getGetSettersOut().println("\t/**");
		this.localOutputStream.getGetSettersOut().println("\t *");
		this.localOutputStream.getGetSettersOut().println(
				"\t * @return value for the field : " + fieldname);
		this.localOutputStream.getGetSettersOut().println("\t */");

		this.localOutputStream.getGetSettersOut().println(
				"\tpublic " + ret + " get" + firstUpper(fieldname)
						+ "(Boolean create) {");
		this.localOutputStream.getGetSettersOut().println();

	}

	private void _writeSetter(String type, String fieldname, String doc) {

		// TODO Write doc
		this.localOutputStream.getGetSettersOut().println("\t/**");
		this.localOutputStream.getGetSettersOut().println("\t *");
		this.localOutputStream.getGetSettersOut().println(
				"\t * @param value for the field : " + fieldname);
		this.localOutputStream.getGetSettersOut().println("\t */");

		this.localOutputStream.getGetSettersOut().println(
				"\tpublic void set" + firstUpper(fieldname) + "(" + type + " "
						+ fieldname + ") {");
		this.localOutputStream.getGetSettersOut().println(
				"\t\tthis." + fieldname + "=" + fieldname + ";");
		this.localOutputStream.getGetSettersOut().println("\t}");
		this.localOutputStream.getGetSettersOut().println();

	}

	/**
	 * Writes a getter proxy declaration : public ret getFieldname () {
	 * 
	 * @param ret
	 * @param fieldname
	 * @param doc
	 *            - an associated documentation
	 */
	private void _writeGetterProxy(String ret, String fieldname, String doc) {

		// TODO Write doc
		this.localOutputStream.getGetSettersOut().println("\t/**");
		this.localOutputStream.getGetSettersOut().println("\t *");
		this.localOutputStream.getGetSettersOut().println(
				"\t * @return value for the field : " + fieldname);
		this.localOutputStream.getGetSettersOut().println("\t */");

		this.localOutputStream.getGetSettersOut().println(
				"\tpublic " + ret + " get" + firstUpper(fieldname) + "() {");
		this.localOutputStream.getGetSettersOut().println(
				"\t\treturn get" + firstUpper(fieldname) + "(false);");
		this.localOutputStream.getGetSettersOut().println("\t}");
		this.localOutputStream.getGetSettersOut().println();

	}

	/**
	 * closes a getter
	 * 
	 */
	private void _writeGetterEnd() {

		this.localOutputStream.getGetSettersOut().println("\t}");
		this.localOutputStream.getGetSettersOut().println();

	}

	/**
	 * Writes the delcaration of a nested class inside a getter
	 * 
	 * @param name
	 * @param extension
	 * @param implementations
	 * @param doc
	 */
	private void _writeGetterNestedClassDeclaration(String name,
			String extension, Class[] implementations, String doc) {

		// TODO Write documentation
//		if (doc != null && doc.length() > 0) {
//
//			this.localOutputStream.getGetSettersOut().println("\t\t/**");
//			this.localOutputStream.getGetSettersOut().println("\t\t *");
//			this.localOutputStream.getGetSettersOut().println("\t\t * " + doc);
//			this.localOutputStream.getGetSettersOut().println("\t\t *");
//			this.localOutputStream.getGetSettersOut().println("\t\t */");
//
//		}
		
		this.localOutputStream.getGetSettersOut().print(
				"\t\tclass " + name + " ");

		// extensions
		if (extension != null && extension.length() > 0) {
			// extends
			this.localOutputStream.getGetSettersOut().print(
					"extends " + extension + " ");

			// Parametrization
			/*
			 * if (paramclass != null) { this.getSettersOut.print("<" +
			 * paramclass + ">"); } this.getSettersOut.print(" ");
			 */

		}

		// Implementations
		if (implementations != null && implementations.length > 0) {

			this.localOutputStream.getGetSettersOut().print("implements ");
			for (Class impl : implementations) {

				// declare implementation
				this.localOutputStream.getGetSettersOut().print(
						impl.getSimpleName() + " ");

				// import
				if (!this.imports.contains(impl.getCanonicalName()))
					this.imports.add(impl.getCanonicalName());
			}

		}

		this.localOutputStream.getGetSettersOut().print("{\n");
		this.localOutputStream.getGetSettersOut().println();

		// Write constructor deriving from OOXNode (if wa re deriving from)
		// --------------------------------
		// if (extension != null &&
		// OOXNode.class.isAssignableFrom(Class.forName(extension)) ){
		//
		// // public OOXNode(NCNameBuffer name)
		// // ------------------------
		// this.getSettersOut.println("\t\t\tpublic " + name
		// + "(NCNameBuffer localname) {");
		// this.getSettersOut.println("\t\t\t\tsuper(localname);\n\t\t\t}");
		//
		// // public OOXNode(AnyURI namespace)
		// // ------------------------
		// this.getSettersOut.println("\t\t\tpublic " + name
		// + "(AnyURI namespace) {");
		// this.getSettersOut.println("\t\t\t\tsuper(namespace);\n\t\t\t}");
		//
		// // public OOXNode(AnyURI namespace, NCNameBuffer name)
		// // ------------------------
		// this.getSettersOut.println("\t\t\tpublic " + name
		// + "(AnyURI namespace,NCNameBuffer name) {");
		// this.getSettersOut
		// .println("\t\t\t\tsuper(namespace,name);\n\t\t\t}");
		//
		// // imports
		// this.imports.add(NCNameBuffer.class.getCanonicalName());
		// this.imports.add(AnyURI.class.getCanonicalName());
		//
		// }
		// imports
		this.imports.add(NCNameBuffer.class.getCanonicalName());
		this.imports.add(AnyURIBuffer.class.getCanonicalName());

	}

	private void _writeGetterNestedClassEnd() {
		this.localOutputStream.getGetSettersOut().println("\t\t}");
		this.localOutputStream.getGetSettersOut().println();
	}

	/**
	 * Writes an empty constructor in classdefthis.localOutputStream
	 * 
	 * @param clname
	 *            the class name
	 */
	private void _writeEmptyConstructor(String clname) {
		this.localOutputStream.getClassdefOut().println("\t/**");
		this.localOutputStream.getClassdefOut().println(
				"\t * This is the default empty constructor");
		this.localOutputStream.getClassdefOut().println("\t */");
		this.localOutputStream.getClassdefOut().println(
				"\tpublic " + clname + "(){");
		this.localOutputStream.getClassdefOut().println("\t\t");
		this.localOutputStream.getClassdefOut().println("\t}");
	}

	/**
	 * Write a contructor taking a String argument and invoking the super String
	 * constructor. This is targeted at extensions of a basic XSDType so that
	 * they can be unmarshalled easily
	 */
	private void _writeSuperStringConstructor(String className,
			Class[] exceptions) {

		this.localOutputStream.getClassdefOut().println("\t/**");
		this.localOutputStream.getClassdefOut().println(
				"\t * This is the XSD String constructor");
		this.localOutputStream.getClassdefOut().println("\t */");

		this.localOutputStream.getClassdefOut().print(
				"\tpublic " + className + "(java.lang.String str) ");

		// Exceptions
		if (exceptions != null && exceptions.length > 0) {
			this.localOutputStream.getClassdefOut().print("throws ");
			String exes = "";
			for (Class ex : exceptions)
				exes += "," + ex.getCanonicalName();
			exes = exes.replaceFirst(",", "");
			this.localOutputStream.getClassdefOut().print(exes);
		}

		this.localOutputStream.getClassdefOut().print("{\n");

		this.localOutputStream.getClassdefOut().println("\t\tsuper(str);");
		this.localOutputStream.getClassdefOut().println("\t}");

	}

	/**
	 * This method adds a new level to the className
	 * 
	 * @param className
	 */
	private void _appendCurrentClassName(String className) {

		// No elements, cannot use the precedent
		if (this.currentClassName.size() == 0) {
			this.currentClassName.add(this.realpackageName + "." + className);

		} else {
			// take the precedent and add a new level
			this.currentClassName.add(this.currentClassName
					.get(this.currentClassName.size() - 1)
					+ "." + className);
		}
	}

	/**
	 * This method transforms a QNameBuffer to a type declaration, for example : <BR>
	 * prf:docType -> com.company.prf.impl.docType
	 * 
	 * @param name
	 * @return
	 */
	private String QName2Type(QNameBuffer name) {
		return QName2Type(name, null);
	}

	/**
	 * This method transforms a QNameBuffer to a type declaration, for example : <BR>
	 * prf:docType -> com.company.prf.impl.docType
	 * 
	 * @param name
	 * @param lookupPrefix
	 *            TODO
	 * @return
	 */
	private String QName2Type(QNameBuffer name, String lookupPrefix) {
		try {

			String res = "";
			NCNameBuffer prefix = name.getPrefix();
			NCNameBuffer local = name.getLocalPart();

			if (prefix == null && local == null)
				return ObjectBuffer.class.getCanonicalName();

			TeaLogging.teaLogInfo("Resolving QNameBuffer: " + name);

			res += this._checkFieldName(local.toString());

			// Special case when compiling XML Schema
			// ---------------------------------------------------
			if (this.schema.getNsmap().get("targetNamespace").equals(
					XSDReader.XSD_NS)) {

				Class bclass = TypeAnalyzer.analyse(local.toString());
				System.err.println("In XSD schema, checking type of : " + name);
				if (bclass != null) {
					System.err.println("In XSD schema, got a base type");
					// import and return
					this.imports.add(bclass.getCanonicalName());
					return bclass.getCanonicalName();

				} else {

					if (lookupPrefix != null) {
						if ((this.sourceoutput.getTodo().get(lookupPrefix + ":"
								+ local)) != null)
							return this.basepackageName + "." + lookupPrefix
									+ "." + res;
					}

					if ((this.sourceoutput.getTodo()
							.get(JavaOutputCell.PRE_CTYPE + ":" + local)) != null)
						return this.basepackageName + ".ctypes." + res;
					else if ((this.sourceoutput.getTodo()
							.get(JavaOutputCell.PRE_STYPE + ":" + local)) != null)
						return this.basepackageName + ".stypes." + res;
					else if ((this.sourceoutput.getTodo()
							.get(JavaOutputCell.PRE_ETYPE + ":" + local)) != null)
						return this.basepackageName + ".elements." + res;
					else if ((this.sourceoutput.getTodo()
							.get(JavaOutputCell.PRE_ATYPE + ":" + local)) != null)
						return this.basepackageName + ".attributes." + res;

				}

			}

			if ((this.schema.getNsmap().get("xmlns").equals(XSDReader.XSD_NS) && prefix == null)
					|| (prefix != null && this.schema.getNsmap().get(
							prefix.toString()).equals(XSDReader.XSD_NS))) {

				// Its an xsd type if we have the xsd prefix, or if xmlns = xsd
				// and the prefix is null

				// It is an xsd type, use a converter to get the base type
				Class bclass = TypeAnalyzer.analyse(local.toString());

				if (bclass != null) {

					// import and return
					this.imports.add(bclass.getCanonicalName());
					return bclass.getCanonicalName();

				} else
					return ObjectBuffer.class.getCanonicalName();

			} else if (prefix == null
					|| this.schema.getNsmap().get(prefix.toString()).equals(
							this.schema.getNsmap().get("targetNamespace"))) {

				// If the lookupprefix is provided, look into it first
				if (lookupPrefix != null) {
					if ((this.sourceoutput.getTodo().get(lookupPrefix + ":"
							+ local)) != null)
						return this.basepackageName + "." + lookupPrefix + "."
								+ res;
				}

				// Else try all the others
				if ((this.sourceoutput.getTodo().get(JavaOutputCell.PRE_CTYPE
						+ ":" + local)) != null)
					return this.basepackageName + ".ctypes." + res;
				else if ((this.sourceoutput.getTodo()
						.get(JavaOutputCell.PRE_STYPE + ":" + local)) != null)
					return this.basepackageName + ".stypes." + res;
				else if ((this.sourceoutput.getTodo()
						.get(JavaOutputCell.PRE_ETYPE + ":" + local)) != null)
					return this.basepackageName + ".elements." + res;
				else if ((this.sourceoutput.getTodo()
						.get(JavaOutputCell.PRE_ATYPE + ":" + local)) != null)
					return this.basepackageName + ".attributes." + res;

				return res = this.basepackageName + "." + res;

			} else if (this.schema.getNsmap().get(prefix.toString()) != null) {

				// FIXME Other package importing

				// Use Classes.forName to locate a class

				XSDImportedDocument idoc = this.schema.getImportMap().get(
						this.schema.getNsmap().get(prefix.toString()));

				if (idoc == null || idoc.getSourcePackage() == null)
					return ObjectBuffer.class.getCanonicalName();

				String ipackage = idoc.getSourcePackage();

				TeaLogging.teaLogInfo("Trying to dereference " + local
						+ " From : " + ipackage);

				String ipackagedir = ipackage.replaceAll("\\.", "/");

				// If the prefix is provided, look into it first
				if (lookupPrefix != null) {

					try {
						String classLookup = ipackage + "." + lookupPrefix
								+ "." + local;
						TeaLogging.teaLogInfo("Looking for class: "
								+ classLookup, 2);

						// Try with Class.forName
						Class<?> cl = Thread.currentThread()
								.getContextClassLoader().loadClass(classLookup);
						// Allright, return
						return cl.getCanonicalName();
					} catch (ClassNotFoundException e) {

						// try with local file else
						File cl = new File(this.srcDir + ipackagedir + "/"
								+ lookupPrefix + "/" + local + ".java");
						if (cl.exists())
							return ipackage + "." + lookupPrefix + "." + local;
						else
							return ObjectBuffer.class.getCanonicalName();
					}

				}

				// Else try all the others
				List<String> explore = Arrays.asList(JavaOutputCell.PRE_CTYPE,
						JavaOutputCell.PRE_STYPE, JavaOutputCell.PRE_ETYPE,
						JavaOutputCell.PRE_ATYPE);
				for (String pr : explore) {

					try {
						// Try with Class.forName
						Class<?> cl = Thread.currentThread()
								.getContextClassLoader().loadClass(
										ipackage + "." + pr + "." + local);
						// Allright, return
						return cl.getCanonicalName();
					} catch (ClassNotFoundException e) {

						// try with local file else
						System.out.println("Trying to find " + local + " into "
								+ this.srcDir + ipackagedir + pr + "/" + local
								+ ".java");

						File cl = new File(this.srcDir + ipackagedir + "/" + pr
								+ "/" + local + ".java");
						if (cl.exists())
							return ipackage + "." + pr + "." + local;
						else
							continue;
					}

				}

				return ObjectBuffer.class.getCanonicalName();

			} else
				return ObjectBuffer.class.getCanonicalName();

		} catch (NullPointerException ex) {
			System.err
					.println("[OOXOUT] Nullpointer while trying to get ns parameters over : "
							+ name);
			throw ex;
		}
	}

	/**
	 * Returns a copy of the string with teh first character in uppercase
	 * 
	 * @param base
	 * @return
	 */
	private String firstUpper(String base) {

		return "" + Character.toUpperCase(base.charAt(0)) + base.substring(1);

	}

	/**
	 * This method checks a fieldName is OK by looking in forbidden workds map
	 * and returning the corrrecte replacement
	 * 
	 * @param fname
	 * @return
	 */
	protected String _checkFieldName(String fname) {

		String res = null;

		// Check forbidden is authorized
		String repl = JavaOutputCell.forbiddennames.get(fname);
		if (repl != null)
			res = repl;
		else
			res = fname;

		// Res => non word chars replacing
		res = res.replaceAll("\\W", "");

		// if (this.isElement)
		// res = "OE_"+res;
		//		
		// if (this.isAttribute)
		// res = "OA_"+res;

		return res;

	}

	protected String _getTypeRepresentation(LinkedList<String> types) {
		return this._getTypeRepresentation(types, null);
	}

	protected String _getTypeRepresentation(LinkedList<String> types,
			String excluded) {

		// System.out.println("Returning type represetnation : " +
		// types.size());

		// Clone the list before processing to avoid list destruction with poll
		LinkedList<String> cls = (LinkedList<String>) types.clone();

		if (cls != null && cls.size() > 0) {

			// Get class and show it
			String res = "";
			String first = cls.poll();

			if (excluded == null || (!excluded.equals(first))) {
				res += first;

				// Go next
				// --------------------
				if (cls.size() > 0) {

					// Get descendant representation but do not add it if it is
					// empty
					// ----------------------------------------------------------
					String next = this._getTypeRepresentation(cls, excluded);
					if (next.length() > 0) {
						res += "<";
						// If current == OOXLis => param is < ? extends ...>
						// if (first.equals(OOXList.class.getCanonicalName()))
						// res += "? extends ";

						res += next;
						res += ">";
					}
				}
			} else {

				// This class is excluded-> go next

				if (cls.size() > 0) {
					res += this._getTypeRepresentation(cls, excluded);
				}
			}

			return res;

		} else
			return "";

	}

	public String _getSimpleName(String cano) {
		if (cano != null) {

			String[] arr = cano.split("\\.");
			return arr[arr.length - 1];

		} else
			return "";
	}

}
