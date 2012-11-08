/**
 * 
 */
package com.idyria.osi.ooxoo.core.translator;

import java.util.Collection;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.ElementBuffer;
import com.idyria.osi.ooxoo.core.io.AbstractXMLIO;
import com.idyria.osi.ooxoo.core.io.XMLIOFactory;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;



/**
 * @author rtek
 * 
 */
public class Translator {

	/**
	 * The IO Factory
	 */
	private XMLIOFactory ioFactory = null;

	/**
	 * The base element to wrap from or unwrap to
	 */
	private ElementBuffer<?> element = null;

	/**
	 * 
	 */
	public Translator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ioFactory
	 * @param element
	 */
	public Translator(XMLIOFactory ioFactory, ElementBuffer<?> element) {
		super();
		this.ioFactory = ioFactory;
		this.element = element;
	}

	public AbstractXMLIO toXML() {

		// Create First target IO
		AbstractXMLIO rootio = this.ioFactory.createQualifiedIO(
				"urn:idyria:test", "test");

		// End
		return rootio;
	}

	/**
	 * 
	 * @param buffers
	 *            Buffers to transform
	 * @param xio
	 *            the IO context involved
	 */
	protected void toXML(Collection<Buffer<?>> buffers, AbstractXMLIO xio) {

		for (Buffer<?> buffer : buffers) {
			
			this.toXML(buffer, xio);
			
		}
		
	}

	/**
	 * 
	 * @param buffer
	 * @param xio
	 */
	protected void toXML(Buffer<?> buffer, AbstractXMLIO xio) {

		// Check buffer
		if (buffer==null)
			return;
		
		// Get Leaf in horizontal buffer
		Buffer<?> rearBuffer = buffer.getRearBuffer();
		
		// Connect to a subIOContext
		rearBuffer.setNextBuffer(xio.createSubContext());
		
		TransferUnit tu = buffer.wrap();
		
		
	}

	public void fromXML(AbstractXMLIO io) {

	}

}
