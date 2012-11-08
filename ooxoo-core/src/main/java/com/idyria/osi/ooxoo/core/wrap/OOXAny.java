/**
 * 
 */
package com.idyria.osi.ooxoo.core.wrap;

import java.util.HashMap;

import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Element;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxattribute;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxelement;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;



/**
 * @author rtek
 * 
 */
public class OOXAny extends OOXList<Buffer<?>> {

	/**
	 * Patterns against wich we will be testing targetNamespaces
	 */
	protected HashMap<String, MaxOccursBuffer> namespaces = new HashMap<String, MaxOccursBuffer>();

	protected NamespaceContext nscontext = null;
	
	/**
	 * The annotation to find
	 */
	protected Class annotation = Ooxelement.class;

	/**
	 * 
	 */
	public OOXAny() {
		super(new NonNegativeIntegerBuffer(0), new MaxOccursBuffer(true));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.znw.xml.zaxb.engines.translator.OOXList#addElement(java.lang.Object)
	 */
	@Override
	public synchronized void addElement(Buffer<?> o) {

		// TODO Check the object is a node with a targetNamespace that is OK

		// Get annotations
		// ---------------------------------
		Ooxnode nan = o.getClass().getAnnotation(Ooxnode.class);
		Ooxelement ean = o.getClass().getAnnotation(Ooxelement.class);

		// if (namespaces.containsKey(key))

		super.addElement(o);

	}

	protected void setWaitForElements() {
		this.annotation = Ooxelement.class;
	}

	protected void setWaitForAttributes() {
		this.annotation = Ooxattribute.class;
	}

	public Object lookForAnElement(QNameBuffer qualifiedName) {

		// The result
		Object res = null;
		//System.out.println("Comparing "+qualifiedName);
		// Look for a node in values that would match qualifiedName
		if (this.getValues() != null && this.getLength() > 0
				&& qualifiedName != null
				&& qualifiedName.getLocalPart() != null && this.nscontext!=null) {
			
			for (Object obj : this.getValues() ) {
				
				if (obj instanceof Element) {
					
					Element elt = (Element) obj;	
					//System.out.println("Comparing "+qualifiedName.getPrefix()+":"+qualifiedName.getLocalPart());
					//System.out.println("to "+elt.getNamespaceURI()+":"+elt.getLocalName());
					
					// First compare the local Name....
					if (!qualifiedName.getLocalPart().getValue().equals(elt.getLocalName()))
						continue;
					
					//...then the namespace uri
					if (qualifiedName.getPrefix()==null || (this.nscontext.getNamespaceURI(qualifiedName.getPrefix().getValue().toString()) !=null && this.nscontext.getNamespaceURI(qualifiedName.getPrefix().getValue().toString()).equals(elt.getNamespaceURI())) ) {
						res = elt;
						break;
					}
					
				} // End Element
				else {
					// Try with annotations
					Ooxnode ooxn = obj.getClass().getAnnotation(Ooxnode.class);
					if (ooxn!=null && qualifiedName.getLocalPart().getValue().equals(ooxn.localName()) &&
							( qualifiedName.getPrefix()==null || (this.nscontext.getNamespaceURI(qualifiedName.getPrefix().getValue().toString()) !=null && this.nscontext.getNamespaceURI(qualifiedName.getPrefix().getValue().toString()).equals(ooxn.targetNamespace())))
					) {
						res = obj;
						break;
					}
				}
			}
			
		}

		// Return no match
		return res;

	}

	/**
	 * Does nothing
	 * @return returns null
	 */
	@Override
	public Buffer<?> add() {
		// TODO Auto-generated method stub
//		return null;
		OOXAnyUnwrapped anyUnwrapped = new OOXAnyUnwrapped();
		add(anyUnwrapped);
		return anyUnwrapped;
	}

	/**
	 * @return the nscontext
	 */
	public NamespaceContext getNscontext() {
		return nscontext;
	}

	/**
	 * @param nscontext the nscontext to set
	 */
	public void setNscontext(NamespaceContext nscontext) {
		this.nscontext = nscontext;
	}

	@Override
	protected Buffer<?> convertFromString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
