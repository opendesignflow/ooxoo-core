/**
 * 
 */
package com.idyria.osi.xgen.scala;

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
public class XGenScala {

	/**
	 * 
	 */
	public XGenScala() {
		// TODO Auto-generated constructor stub
	}

	
	public void compile(URL schemaPath) {

		try {
			// Prepare transform
			// --------------------
			Configuration saxonConfiguration = Configuration.newConfiguration();
			saxonConfiguration.setXIncludeAware(true);
			//saxonConfiguration.setStripsAllWhiteSpace(true);
			
			TransformerFactory factory = TransformerFactory.newInstance(
					"net.sf.saxon.TransformerFactoryImpl", Thread
							.currentThread().getContextClassLoader());
			((net.sf.saxon.TransformerFactoryImpl) factory)
					.setConfiguration(saxonConfiguration);

			// Readin Stylesheets
			// ---------------------------
			StreamSource stylesheetSource = new StreamSource(getClass()
					.getResourceAsStream("xgen-to-scala.xsl"));
			// stylesheetSource.setSystemId(systemId);
			Templates stylesheetTemplates = factory
					.newTemplates(stylesheetSource);
			
			
			// Do transformation
			//-------------------
			StreamResult result = new StreamResult(new ByteArrayOutputStream());
			Transformer trans = stylesheetTemplates.newTransformer();
			trans.setParameter("baseFolder","src/main/scala/com/idyria/osi/ooxoo/compiler/emitter/scala/");
			trans.setParameter("basePackage","com.idyria.osi.ooxoo.compiler.emitter.scala/");
			
			System.out.println("Base folder: "+trans.getParameter("baseFolder"));
			
			trans.transform(new StreamSource(schemaPath.openStream()), result);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
