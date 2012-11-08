/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.emitter.java;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.saxon.serialize.TEXTEmitter;
import net.sf.saxon.trans.XPathException;
import de.hunsicker.jalopy.Jalopy;

/**
 * @author rleys
 *
 */
public class JalopyReceiver extends TEXTEmitter {

	private PipedInputStream inputStream = null;
	
	private PipedOutputStream outputStream = null;
	
	private OutputStream targetOutputStream = null;
	
	/**
	 * 
	 */
	public JalopyReceiver() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.sf.saxon.serialize.Emitter#makeOutputStream()
	 */
	@Override
	protected OutputStream makeOutputStream() throws XPathException {
		//System.out.println("Making ostream for: "+this.getOutputProperties().getProperty("href"));
		//return super.makeOutputStream();
		super.makeOutputStream();
		//-- Create Stream Pipes
		try {
			this.inputStream = new PipedInputStream();
			this.outputStream = new PipedOutputStream(inputStream);
			//this.inputStream.connect(this.outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this.outputStream;
	}

	
	
	
	/* (non-Javadoc)
	 * @see net.sf.saxon.serialize.XMLEmitter#close()
	 */
	@Override
	public void close() throws XPathException {
		
		//-- Let super close
		super.close();
		
		//-- Now format Code
		//System.out.println("Target file: "+getSystemId());
		
		//-- Process through jalopy
		Jalopy jalopy = new Jalopy();
		try {
			jalopy.setInput(new URL(this.getSystemId()).openStream(),new URL(this.getSystemId()).getFile());
			jalopy.setOutput(new File(new URL(this.getSystemId()).getFile()));
			
			
			jalopy.format(true);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	/* (non-Javadoc)
	 * @see net.sf.saxon.serialize.Emitter#setOutputStream(java.io.OutputStream)
	 */
	@Override
	public void setOutputStream(OutputStream os) throws XPathException {
		this.targetOutputStream =  os;
		super.setOutputStream(os);
	}

	

}
