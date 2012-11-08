/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.emitter.scala;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;

import com.idyria.osi.ooxoo.compiler.emitter.java.TypeResolver;

/**
 * 
 * Compiles an XSD Schema to an XGEN document producing scala
 * 
 * @author rleys
 * 
 */
public class ScalaCompiler {

	private OutputStream outputStream = new ByteArrayOutputStream();
	
	/**
	 * 
	 */
	public ScalaCompiler() {
		// TODO Auto-generated constructor stub
	}

	public void compile(URL schemaPath) {

		try {
			// Prepare transform
			// --------------------
			Configuration saxonConfiguration = Configuration.newConfiguration();
			saxonConfiguration.setXIncludeAware(true);
			//saxonConfiguration.setStripsAllWhiteSpace(true);
			
			//-- Add Type resolver
			saxonConfiguration.registerExtensionFunction(new STypeResolver());
			
			TransformerFactory factory = TransformerFactory.newInstance(
					"net.sf.saxon.TransformerFactoryImpl", Thread
							.currentThread().getContextClassLoader());
			
			((net.sf.saxon.TransformerFactoryImpl) factory)
					.setConfiguration(saxonConfiguration);

			
			
			
			// Readin Stylesheets
			// ---------------------------
			StreamSource stylesheetSource = new StreamSource(getClass()
					.getResourceAsStream("xsd-to-xgen-scala.xsl"));
			// stylesheetSource.setSystemId(systemId);
			Templates stylesheetTemplates = factory
					.newTemplates(stylesheetSource);
			
			
			// Do transformation
			//-------------------
			StreamResult result = new StreamResult(outputStream);
			stylesheetTemplates.newTransformer().transform(new StreamSource(schemaPath.openStream()), result);
			outputStream.flush();
			outputStream.close();
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the outputStream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @param outputStream the outputStream to set
	 */
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	

}
