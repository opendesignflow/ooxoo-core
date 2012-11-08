/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.out;

import java.util.ResourceBundle;

import com.idyria.osi.ooxoo.compiler.xsd.InvalidDatatypesPackage;
import com.idyria.osi.ooxoo.core.buffers.datatypes.TypeAnalyzer;


/**
 * @author Rtek
 * 
 */
public class OOXCompilerOutput {

	protected TypeAnalyzer typeAnalyser = null; //! A package context dependent data
	

	protected OOXCompilerOutput() {
		
	}
	
	public OOXCompilerOutput(String datatypescontext)
			throws InvalidDatatypesPackage {
		// Use factory context information to retrieve the class that we have to
		// laod to get a typeconverterfactory
		ResourceBundle rb = ResourceBundle.getBundle(datatypescontext
				.replaceAll("\\.", "/")
				+ "/DatatypesRessources");
		String factoryname = rb.getString("DefaultTypeConverterFactory");
		String analysername = rb.getString("DefaultTypeAnalyser");
		try {
			// load the class
			Class clss = Class.forName(factoryname);

//		

			// load type analyser class
			Class acl = Class.forName(analysername);
			this.typeAnalyser = (TypeAnalyzer) acl.newInstance();

			// Create Constraint reader


		} catch (Exception ex) {
			// Cast an exception
			InvalidDatatypesPackage exe = new InvalidDatatypesPackage(
					"An error occured when loading type converter factory and Type analyser, check your properties file");
			throw exe;
		}
	}

	

	

	protected Class[] getElementParametrizationClass(Class baseclass,
			String superClassname) throws ClassNotFoundException {

		// only try if we are subtype of ZaxbElement

		// gene is the temp class object
		Class gene = baseclass;

		// Supertype is the generic supertype associated
		String supertype = gene.getCanonicalName();
		// System.out.println("Reading parametrization class 1st step :
		// "+supertype);
		// Go up in simple super class while the generic superclass is not of
		// zaxbElement type
		while (!supertype.contains(superClassname)) {

			// Get superclass generic type
			supertype = gene.getGenericSuperclass().toString();
			// Place cursor on superclass
			gene = gene.getSuperclass();

		}

		// supertype is of the form : ZaxbElement<PARAM>, extract PARAM:
		supertype = supertype.replaceAll("^[A-Za-z.0-9]*<", "").replaceAll(
				">$", "");

		// Prepare class array to return

		if (supertype.contains("java.util.LinkedList")) {
			Class[] classes = new Class[2];
			classes[1] = Class.forName("java.util.LinkedList");
			String basetype = supertype.replaceAll("java.util.LinkedList<", "")
					.replaceAll(">$", "");
			System.out
					.println("Parametrization class got parsing linkedlist : "
							+ basetype);
			classes[0] = Class.forName(supertype.replaceAll("^[A-Za-z.0-9]*<",
					"").replaceAll(">$", ""));
			return classes;
		} else {
			Class[] classes = new Class[1];
			classes[0] = Class.forName(supertype);
			return classes;
		}

	}

	

	protected String buildGetterExpression(String elementname) {

		String getter = "get"
				+ elementname.toString().replaceFirst(
						"" + elementname.charAt(0),
						("" + elementname.charAt(0)).toUpperCase());
		return getter;

	}

	

}
