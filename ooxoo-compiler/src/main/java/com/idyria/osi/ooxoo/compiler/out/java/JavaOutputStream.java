/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.out.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Rtek
 * 
 */
public class JavaOutputStream {

	private SectionOutputStream importsOut = null;

	private SectionOutputStream classdefOut = null;

	private SectionOutputStream fieldsDeclarationOut = null;

	private SectionOutputStream initialisationOut = null;

	private SectionOutputStream nestedClassesOut = null;

	private SectionOutputStream getSettersOut = null;

	/**
	 * 
	 */
	public JavaOutputStream() {
		importsOut = new SectionOutputStream();
		classdefOut = new SectionOutputStream();
		fieldsDeclarationOut = new SectionOutputStream();
		initialisationOut = new SectionOutputStream();
		nestedClassesOut = new SectionOutputStream();
		getSettersOut = new SectionOutputStream();
	}

	/**
	 * @return the classdefOut
	 */
	public SectionOutputStream getClassdefOut() {
		return classdefOut;
	}

	/**
	 * @return the fieldsDeclarationOut
	 */
	public SectionOutputStream getFieldsDeclarationOut() {
		return fieldsDeclarationOut;
	}

	/**
	 * @return the getSettersOut
	 */
	public SectionOutputStream getGetSettersOut() {
		return getSettersOut;
	}

	/**
	 * @return the importsOut
	 */
	public SectionOutputStream getImportsOut() {
		return importsOut;
	}

	/**
	 * @return the initialisationOut
	 */
	public SectionOutputStream getInitialisationOut() {
		return initialisationOut;
	}

	/**
	 * @return the nestedClassesOut
	 */
	public SectionOutputStream getNestedClassesOut() {
		return nestedClassesOut;
	}

	public byte[] flush() {

		try {
			// Create an output stream to which writing the result
			ByteArrayOutputStream outs = new ByteArrayOutputStream();
			outs.write(getImportsOut().returnAndClose());
			outs.write(getClassdefOut().returnAndClose());
			outs.write(getNestedClassesOut().returnAndClose());
			outs.write(getFieldsDeclarationOut().returnAndClose());
			outs.write(getInitialisationOut().returnAndClose());
			outs.write(getGetSettersOut().returnAndClose());
			
			// Create the input stream to read from the outputed values
			//ByteArrayInputStream inps = new ByteArrayInputStream(outs.toByteArray());
			
			return outs.toByteArray();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;

	}

}
