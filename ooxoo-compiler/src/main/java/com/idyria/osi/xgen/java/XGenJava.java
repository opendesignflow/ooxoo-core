/**
 * 
 */
package com.idyria.osi.xgen.java;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;

/**
 * 
 * Transforms a XGEN document to a set of java classes
 * @author rleys
 *
 */
public class XGenJava {

	/**
	 * 
	 */
	public XGenJava() {
		// TODO Auto-generated constructor stub
	}

	
	public void compile(URL schemaPath) {

		try {
			// Prepare transform
			// --------------------
			Configuration saxonConfiguration = Configuration.newConfiguration();
			saxonConfiguration.setXIncludeAware(true);

			TransformerFactory factory = TransformerFactory.newInstance(
					"net.sf.saxon.TransformerFactoryImpl", Thread
							.currentThread().getContextClassLoader());
			((net.sf.saxon.TransformerFactoryImpl) factory)
					.setConfiguration(saxonConfiguration);

			// Readin Stylesheets
			// ---------------------------
			StreamSource stylesheetSource = new StreamSource(getClass()
					.getResourceAsStream("xgen-to-java.xsl"));
			// stylesheetSource.setSystemId(systemId);
			Templates stylesheetTemplates = factory
					.newTemplates(stylesheetSource);
			
			
			// Do transformation
			//-------------------
			StreamResult result = new StreamResult(new ByteArrayOutputStream());
			Transformer trans = stylesheetTemplates.newTransformer();
			trans.setParameter("baseFolder","src/test/java/com/idyria/osi/ooxoo/compiler/emitter/java/");
			trans.setParameter("basePackage","com.idyria.osi.ooxoo.compiler.emitter.java/");
			trans.transform(new StreamSource(schemaPath.openStream()), result);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
