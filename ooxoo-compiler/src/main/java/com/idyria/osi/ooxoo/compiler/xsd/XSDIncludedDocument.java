package com.idyria.osi.ooxoo.compiler.xsd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.idyria.osi.ooxoo.compiler.CompilerException;
import com.idyria.tools.xml.utils.XMLUtils;
import com.idyria.utils.java.logging.TeaLogging;

public class XSDIncludedDocument extends XSDResolvableDocument {

	
	private boolean ignore = false; // ignore included xml
	private Element includeNode = null; //! the node that we will replace by correct data
	
	public XSDIncludedDocument(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public XSDIncludedDocument(String path,String base) throws CompilerException {
		super(path);
		this.setBaseLocation(base);
		try {
			resolve();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public XSDIncludedDocument() {
		super();
		// TODO Auto-generated constructor stub
	}



	public void replaceDocument() {
		
		TeaLogging.teaLogInfo("Replacing include node by include content ("+this.doc+"),("+this.includeNode+")");
		if (this.doc!=null && includeNode!=null) {
			
			TeaLogging.teaLogInfo("Replacing include node by include content");
			
			// Get original node's Document
			Document dc = this.includeNode.getOwnerDocument();
			// Get original node's parent
			Element parent = (Element) this.includeNode.getParentNode();
			
			// Get root of to include document
			Element root = this.doc.getDocumentElement();
			
			// Append to include document content just befor source xs:include node
			NodeList toadd = root.getChildNodes();
			for (int i=0;i<toadd.getLength();i++) {
				// append and content
				parent.appendChild(dc.importNode(toadd.item(i),true));
				
			}
			
			// Wipe the include node
			parent.removeChild(this.includeNode);
			
			XMLUtils xut;
			/*try {
			//	System.out.println("New doc:"+XMLUtils.renderAsIndentedString(root));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}
		
	}
	
	/**
	 * @return Returns the ignore.
	 */
	public boolean isIgnore() {
		return ignore;
	}
	/**
	 * @param ignore The ignore to set.
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	
	/**
	 * @return Returns the includeNode.
	 */
	public Element getIncludeNode() {
		return includeNode;
	}
	/**
	 * @param includeNode The includeNode to set.
	 */
	public void setIncludeNode(Element includeNode) {
		this.includeNode = includeNode;
	}
	
	
	
}
