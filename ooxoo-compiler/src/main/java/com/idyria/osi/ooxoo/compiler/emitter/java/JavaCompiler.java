/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.emitter.java;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

/**
 * @author rleys
 * 
 */
public class JavaCompiler {

	private OutputStream outputStream = new ByteArrayOutputStream();
	
	/**
	 * 
	 */
	public JavaCompiler() {
		// TODO Auto-generated constructor stub
	}

	public void compile(URL schemaPath) {

		try {
			// Prepare transform
			// --------------------
			Configuration saxonConfiguration = Configuration.newConfiguration();
			saxonConfiguration.setXIncludeAware(true);

			//-- Add Type resolver
			saxonConfiguration.registerExtensionFunction(new TypeResolver());
			
			TransformerFactory factory = TransformerFactory.newInstance(
					"net.sf.saxon.TransformerFactoryImpl", Thread
							.currentThread().getContextClassLoader());
			
			((net.sf.saxon.TransformerFactoryImpl) factory)
					.setConfiguration(saxonConfiguration)

			
			
			
			// Readin Stylesheets
			// ---------------------------
			StreamSource stylesheetSource = new StreamSource(getClass()
					.getResourceAsStream("xsd-to-xgen-java.xsl"));
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
