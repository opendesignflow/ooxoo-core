/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.out.java;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import com.idyria.osi.ooxoo.compiler.out.OOXCompilerOutput;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCAttribute;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCComplexType;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCElement;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCSimpleType;
import com.idyria.osi.ooxoo.compiler.xsd.model.ZaxCXSSchema;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotation.AppInfo;
import com.idyria.utils.java.file.TeaFileUtils;



/**
 * @author Rtek
 * 
 */
public class CompilerJavaOutput extends OOXCompilerOutput {

	// ! The document to marshall
	private ZaxCXSSchema schema = null;

	/**
	 * The package in which to output
	 */
	private String packageName = null;

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
	 * this is the toto list that holds all the elements to output associated
	 * with their referenceable name
	 */
	private HashMap<String, Object> todo = new HashMap<String, Object>();

	/**
	 * Forbidden names associated with a replacement part
	 */
	public static HashMap<String, String> forbiddennames = new HashMap<String, String>();

	static {
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
	}

	/** @name Writers */
	/** @{ */

	private SectionOutputStream importsOut = null;

	private SectionOutputStream classdefOut = null;

	private SectionOutputStream fieldsDeclarationOut = null;

	private SectionOutputStream initialisationOut = null;

	private SectionOutputStream nestedClassesOut = null;

	private SectionOutputStream getSettersOut = null;

	/** }@ */

	// The base directory
	private String basedir = "";

	// this map registers imports to register
	// private HashMap<String, Boolean> imports = new HashMap<String,
	// Boolean>();

	/**
	 * @param schema
	 * @param packageName
	 */
	public CompilerJavaOutput(ZaxCXSSchema schema, String packageName) {
		super();
		this.schema = schema;
		// Try to discover targetPackage in schema
		for (ZaxCAnnotation ann : schema.getAnnotations()) {
			for (AppInfo ainfo : ann.getAppInfos()) {
				Node targetNode = ainfo.getElement("targetPackage",
						"urn:idyria:utilites:java:ooxoo");
				if (targetNode != null && targetNode.getTextContent() != null
						&& targetNode.getTextContent().length() > 0) {
					this.packageName = targetNode.getTextContent();
					System.out
							.println("Found declared In schema targetPackage: "
									+ this.packageName);
					break;
				}
			}
			if (this.packageName != null)
				break;
		}
		if (this.packageName == null)
			this.packageName = packageName;
	}

	public void output() throws IOException {

		// create package
		String pack = this.packageName;

		// go output

		System.out.println("[OOXOUT] Package is : " + pack);

//		if (basedir==null || basedir.length()==0)
//			basedir = "src/main/java/";
		
		basedir = TeaFileUtils.buildPath(basedir ,pack.replaceAll("\\.", "/"));
		File bdir = new File(basedir);

		if (!bdir.exists()) {
			bdir.mkdirs();
		}

		/*
		 * Read all the types to marshall and add them to a todo list Then
		 * foreach the todolist and output each element
		 */

		// ComplexTypes
		// ------------------------------------------------
		System.out.println("[OOXOUT] Recording complexTypes =====");
		for (ZaxCComplexType tp : this.schema.getComplexTypes()) {

			System.out.println("[OOXOUT] Recording " + tp.getName() + "....");

			this.todo.put(JavaOutputCell.PRE_CTYPE + ":"
					+ tp.getName().toString(), tp);

		}
		System.out.println("***********");

		// Elements
		// ------------------------------------------
		System.out.println("[OOXOUT] Recording Elements =====");
		for (ZaxCElement tp : this.schema.getElements()) {

			System.out.println("[OOXOUT] Recording " + tp.getName() + "....");
			this.todo.put(JavaOutputCell.PRE_ETYPE + ":"
					+ tp.getName().toString(), tp);

		}
		System.out.println("***********");

		// Simple Types
		// -------------------------------
		System.out.println("[OOXOUT] Recording SimpleTypes =====");
		for (ZaxCSimpleType tp : this.schema.getSimpleTypes()) {

			System.out.println("[OOXOUT] Recording " + tp.getName() + "....");
			this.todo.put(JavaOutputCell.PRE_STYPE + ":"
					+ tp.getName().toString(), tp);

		}
		System.out.println("***********");

		// Attributes
		// --------------------------------
		for (ZaxCAttribute tp : this.schema.getAttributes()) {

			System.out.println("[OOXOUT] Writing " + tp.getName() + "....");
			this.todo.put(JavaOutputCell.PRE_ATYPE + ":"
					+ tp.getName().toString(), tp);

		}
		System.out.println("***********");

		// Now output everybody
		Iterator<String> it = this.todo.keySet().iterator();
		while (it.hasNext()) {

			// String key = it.
			this.resolveTodo(it.next());

			Runtime.getRuntime().gc();

		}

		// for (String key : this.todo.keySet())
		// this.resolveTodo(key);

	}

	/**
	 * This method outputs an element from the todo list
	 * 
	 * @param name
	 * @throws IOException
	 * @throws ZaxbException
	 */
	public void resolveTodo(String name) throws IOException {

		// Get object
		Object o = this.todo.get(name);

		// Output depending on its type
		if (o instanceof ZaxCComplexType)
			this.outputComplexType((ZaxCComplexType) o);
		else if (o instanceof ZaxCSimpleType)
			this.outputSimpleType((ZaxCSimpleType) o);
		else if (o instanceof ZaxCElement)
			this.outputElement((ZaxCElement) o);
		else if (o instanceof ZaxCAttribute)
			this.outputAttribute((ZaxCAttribute) o);

		// Remove from list
		// this.todo.remove(name);

	}

	/**
	 * Writes a complex Type
	 * 
	 * @param tp
	 * @throws ZaxbException
	 * @throws IOException
	 */
	private void outputComplexType(ZaxCComplexType tp) throws IOException {

		JavaOutputCell cell = new JavaOutputCell(this.packageName,
				this.basedir, this.schema, this);
		cell.outputComplexType(tp);
		cell.commitFile();

	}

	/**
	 * Writes an element
	 * 
	 * @param tp
	 */
	private void outputElement(ZaxCElement tp) {

		JavaOutputCell cell = new JavaOutputCell(this.packageName,
				this.basedir, this.schema, this);
		cell.outputElement(tp);
		cell.commitFile();
	}

	/**
	 * Outputs a simpleType
	 * 
	 * @param tp
	 */
	private void outputSimpleType(ZaxCSimpleType tp) {

		JavaOutputCell cell = new JavaOutputCell(this.packageName,
				this.basedir, this.schema, this);
		cell.outputSimpleType(tp);
		cell.commitFile();

	}

	/**
	 * FIXME Output attributes
	 * 
	 * @param tp
	 */
	public void outputAttribute(ZaxCAttribute tp) {
		JavaOutputCell cell = new JavaOutputCell(this.packageName,
				this.basedir, this.schema, this);
		cell.outputAttribute(tp);
		cell.commitFile();
	}

	
	
	/**
	 * @return the basedir
	 */
	public String getBasedir() {
		return basedir;
	}

	/**
	 * @param basedir the basedir to set
	 */
	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}

	/**
	 * @return the todo
	 */
	public HashMap<String, Object> getTodo() {
		return todo;
	}

}
