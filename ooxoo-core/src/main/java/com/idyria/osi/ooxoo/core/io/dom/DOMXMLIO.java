/**
 * 
 */
package com.idyria.osi.ooxoo.core.io.dom;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.io.AbstractXMLIO;
import com.idyria.osi.ooxoo.core.tu.AttributeTransferUnit;
import com.idyria.osi.ooxoo.core.tu.ElementTransferUnit;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;
import com.idyria.tools.xml.utils.XMLUtils;
import com.idyria.utils.java.logging.TeaLogging;

/**
 * @author rtek
 * 
 */
public class DOMXMLIO extends AbstractXMLIO {

	/**
	 * The element concerned by this XMLIO
	 */
	private Element element = null;

	// private Element realelement = null;

	/**
	 * The parent to insert to
	 */
	private Element parent = null;

	/**
	 * The target/source document
	 */
	private Document xmlDocument = null;

	/**
	 * 
	 */
	public DOMXMLIO() {
		super();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		try {
			this.xmlDocument = domFactory.newDocumentBuilder().newDocument();

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param element
	 *            parent element (used for subcontexts)
	 */
	public DOMXMLIO(Element parent) {
		super();
		this.parent = parent;
		this.xmlDocument = parent.getOwnerDocument();
	}
	
	public DOMXMLIO(Element element,Element parent) {
		super();
		this.element = element;
		this.parent = parent;
		this.xmlDocument = element!=null ? element.getOwnerDocument() : parent.getOwnerDocument();
	}

	public DOMXMLIO(Document document) {
		super();
		this.xmlDocument = document;
		this.element = this.xmlDocument.getDocumentElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			String res = XMLUtils.renderAsIndentedString(element);
			return res;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.toString();
	}

	@Override
	public AbstractXMLIO createSubContext() {
		// TeaLogging.teaLogInfo("Subcontext with element as parent: "
		// + this.getElement());

		Element choosenparent = this.getParent() == null ? this.element
				: this.parent;

		// Create
		DOMXMLIO io = new DOMXMLIO(choosenparent);

		// Register in parent
		this.childrenIO.add(io);

		return io;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.io.AbstractXMLIO#createSameContext()
	 */
	@Override
	public AbstractXMLIO createSameContext() {

		Element choosenparent =  this.element == null ? this.parent
				: this.element;
		DOMXMLIO io = new DOMXMLIO(choosenparent);

		// Register in parent
		this.childrenIO.add(io);

		return io;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.io.AbstractXMLIO#reset()
	 */
	@Override
	public void reset() {
		// Parent returns to first element if not null
		if (this.element != null)
			this.parent = this.element;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.Buffer#doClone()
	 */
	@Override
	protected Buffer<?> doClone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return this.createSameContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		TeaLogging.teaLogInfo("Create TU");
		// System.exit(0);
		// return new ElementTransferUnit(super.createTransferUnit());
		if( parent !=null || this.element!=null) {
			return this.doUnwrapping(new ElementTransferUnit(super
					.createTransferUnit()));
		}
		else if (this.parent == null) {
			return super.createTransferUnit();
		} else
			return null;
		// return new ElementTransferUnit(super.createTransferUnit());
	}

	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		TeaLogging.teaLogInfo("Wrapping for DOM XML IO : "
				+ tu.getNodeAnnotation());

		// Element
		// ------------
		if (tu instanceof ElementTransferUnit) {
			TeaLogging.teaLogInfo("ElementUnit (" + tu.getValue() + ") // "
					+ ((ElementTransferUnit) tu).getNonWrappedSource() + " // "
					+ this.element);
			if (tu.getNodeAnnotation() != null) {

				// -- Create element
				if (this.element==null) {
					
					//-- Instanciate
					if (tu
						.getNodeAnnotation().targetNamespace().length()>0) {
						
						//-- NS
						this.element = this.xmlDocument.createElementNS(tu
								.getNodeAnnotation().targetNamespace(),this.createQualifiedName(tu.getNodeAnnotation()));
					} else {
						
						//-- Non NS
						this.element = this.xmlDocument.createElement(tu
								.getNodeAnnotation().localName());
					}
					
					
					//-- insert into hierarchy
					// -- Add to parent or document
					if (this.parent != null) {
						TeaLogging.teaLogInfo("Created XMl element in parent: "+this.parent);
						this.parent.appendChild(element);
					} else {
						this.xmlDocument.appendChild(element);
						// -- Created element becomes new parent
						this.parent = element;
					}
				}
				

				// Add text content if there is one
				if (tu.getValue() != null) {
					element.setTextContent(tu.getValue());
				}

			} else if (((ElementTransferUnit) tu).getNonWrappedSource() != null
					&& Element.class
							.isAssignableFrom(((ElementTransferUnit) tu)
									.getNonWrappedSource().getClass())) {
				// -- No Marshalling but we already have an Element, so just
				// adopt and add it
				TeaLogging.teaLogInfo("Recommiting a DOM XML Node");
				Node adopted = this.xmlDocument
						.adoptNode((Node) ((ElementTransferUnit) tu)
								.getNonWrappedSource());
				if (this.parent != null) {
					// -- Add it to parent
					this.parent.appendChild(adopted);
				} else {
					// -- With no parent, just add it to document
					this.xmlDocument.appendChild(adopted);
				}
			}
		}
		// Attribute
		// -----------------
		else if (this.parent != null && (tu instanceof AttributeTransferUnit)) {
			if (tu.getNodeAnnotation() != null) {
				
				//-- Add No NS
				if (tu.getNodeAnnotation().targetNamespace().length()==0) {
					this.parent.setAttribute(tu.getNodeAnnotation().localName(),
							tu.getValue());
				} 	
				//-- Add NS
				else {
					
					//-- Don't respecify NS if parent is already one
					if (this.parent.getNamespaceURI()!=null && this.parent.getNamespaceURI().equals(tu.getNodeAnnotation()
							.targetNamespace())) {
						this.parent.setAttribute(tu.getNodeAnnotation().localName(),
								tu.getValue());
					} else {
						
						//-- Then normal NS add
						this.parent.setAttributeNS(tu.getNodeAnnotation()
								.targetNamespace(), this.createQualifiedName(tu.getNodeAnnotation()),tu.getValue());
					
					}
					
				}
				
				
			}
		}

		return tu;
	}

	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		
		
		if (tu.getNodeAnnotation() != null) {

			TeaLogging.teaLogInfo("-- Going unwrap: "
					+ tu.getClass().getSimpleName());

			// Element
			// ------------
			if (tu instanceof ElementTransferUnit) {

				//-- If Element is not provided, search in parent
				//--------------------
				if (this.element==null && this.parent!=null) {
					
					// -- Search in Parent
					this.element = this.findElement(this.parent, tu
							.getNodeAnnotation());
					TeaLogging.teaLogInfo("Looking for element "+this.parent.getLocalName()+"/{"+tu
							.getNodeAnnotation().targetNamespace()+"}:"+tu
							.getNodeAnnotation().localName()+" result: "
							+ this.element,1);
					if (this.element == null) {
						return null;
					}
				
					// -- Found element becomes new virtual parent
					this.parent = this.element;

					// -- Get text content if one
					if (this.element.getTextContent() != null
							&& this.element.getTextContent().length() > 0) {
						tu.setValue(this.element.getTextContent().trim());
					}

					// -- Add DOM Element to Unit
					((ElementTransferUnit) tu).setNonWrappedSource(this.element);

					return tu;
					
				}
				//-- If Element is provided, then try to match it directly
				//------------
				else if (element!=null) {
					
					if (tu.getNodeAnnotation().targetNamespace().equals(this.element.getNamespaceURI()) && tu.getNodeAnnotation().localName().equals(element.getLocalName())) {
						
						//-- Element becomes new virtual parent
						this.parent = this.element;
						
						// -- Get text content if one
						if (this.element.getTextContent() != null
								&& this.element.getTextContent().length() > 0) {
							tu.setValue(this.element.getTextContent().trim());
						}
						
	
						
						//-- Add DOM Element to Unit
						((ElementTransferUnit) tu).setNonWrappedSource(this.element);
						
						return tu;
						
					} else 
						return null;
					
				} else 
					return null;
				
				
				

			}
			// Attribute
			// -----------------
			else if (this.parent != null
					&& (tu instanceof AttributeTransferUnit)) {

				//-- Get NS
				String value = "";
				if (tu.getNodeAnnotation().targetNamespace().length()>0) {
					value = this.parent.getAttributeNS(tu
							.getNodeAnnotation().targetNamespace(), tu
							.getNodeAnnotation().localName());
				}
				
				//-- Get Non NS if value="" (non ns or NS did not found)
				if (value.length()==0) {
					value = this.parent.getAttribute(tu
							.getNodeAnnotation().localName());
				}
			
				TeaLogging.teaLogInfo("Looking for attribute "+this.parent.getLocalName()+"/{"+tu
						.getNodeAnnotation().targetNamespace()+"}:"+tu
						.getNodeAnnotation().localName()+" with result: " + value,1);
				
				//-- If empty string, return null
				if (value.length()==0) {
					tu.setValue(null);
					return null;
				} else {
					tu.setValue(value);
				}
				
				return tu;
			}

		}
		return null;
	}

	
	
	/**
	 * Searches and returns All
	 * 
	 * @param parent
	 * @param node
	 * @return
	 */
	private Collection<Element> findElements(Element parent, Ooxnode nodeDesc) {
		
		LinkedList<Element> results = new LinkedList<Element>();
		
		NodeList lst = this.parent.getChildNodes();
		//TeaLogging.teaLogInfo("Finding among children: "+lst.getLength());
		for (int i = 0; i < lst.getLength(); i++) {

			// If not an element, go away
			// -------------
			Node node = lst.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element elt = null;
			Element fetchedElt = (Element) node;
			
	

			// -- if no local-name and no targetNamespace, fetch
			if (nodeDesc.localName().length() == 0
					&& nodeDesc.targetNamespace().length() == 0) {

				elt = fetchedElt;
			}

			// -- Do we match namespace?
			//TeaLogging.teaLogInfo("Checking against: "+fetchedElt);
			if (fetchedElt.getNamespaceURI().equals(nodeDesc.targetNamespace())
					&& fetchedElt.getLocalName().equals(nodeDesc.localName())) {
				elt = fetchedElt;
			}

			// -- Return
			if (elt != null) {
				// Remove from parent to avoid multiple fetch
				
				//this.parent.removeChild(elt);
				//return elt;
			}

		}
		return results;
		
	}
	
	/**
	 * Searches and returns only one
	 * 
	 * @param parent
	 * @param node
	 * @return
	 */
	private Element findElement(Element parent, Ooxnode nodeDesc) {
		NodeList lst = this.parent.getChildNodes();
		//TeaLogging.teaLogInfo("Finding among children: "+lst.getLength());
		for (int i = 0; i < lst.getLength(); i++) {

			// If not an element, go away
			// -------------
			Node node = lst.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element elt = null;
			Element fetchedElt = (Element) node;
			
			//-- Ignore if already seen
			if (fetchedElt.getUserData("seen")!=null)
				continue;

			// -- if no local-name and no targetNamespace, fetch
			if (nodeDesc.localName().length() == 0
					&& nodeDesc.targetNamespace().length() == 0) {

				elt = fetchedElt;
			}

			// -- Do we match namespace?
			//TeaLogging.teaLogInfo("Checking against: "+fetchedElt);
			if (fetchedElt.getNamespaceURI().equals(nodeDesc.targetNamespace())
					&& fetchedElt.getLocalName().equals(nodeDesc.localName())) {
				elt = fetchedElt;
			}

			// -- Return
			if (elt != null) {
				// Remove from parent to avoid multiple fetch
				elt.setUserData("seen",true, null);
				//this.parent.removeChild(elt);
				return elt;
			}

		}
		return null;
	}

	/**
	 * Creates a qName by searching for a prefix for localName in context
	 * @param namespace
	 * @param localName
	 * @return
	 */
	private String createQualifiedName(String namespace,String localName) {
		
		//-- Find Prefix
		String prefix = null;
		if (this.ooxooWrappingContext!=null) {
			prefix = this.ooxooWrappingContext.getPrefixForNamespace(namespace);
		}
		
		//-- Create qName
		String qName = prefix == null ? localName : prefix+":"+localName;
		
		return qName;
		
	}
	
	/**
	 * @see this{@link #createQualifiedName(String, String)}
	 * Does not check presence of namespace
	 */
	private String createQualifiedName(Ooxnode node) {
		
		return this.createQualifiedName(node.targetNamespace(),node.localName());
		
	}
	
	/**
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * @return the parent
	 */
	public Element getParent() {
		return parent;
	}

	/**
	 * @return the xmlDocument
	 */
	public Document getXmlDocument() {
		return xmlDocument;
	}

	@Override
	protected Object convertFromString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
