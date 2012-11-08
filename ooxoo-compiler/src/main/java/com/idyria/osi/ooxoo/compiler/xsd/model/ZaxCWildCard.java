/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;


/**
 * @author Rtek
 * 
 */
public class ZaxCWildCard extends ZaxCAnnotableType {

	/**
	 * The element's Id
	 */
	protected IDBuffer id = null;

	/**
	 * Namespace limitation to anyAttribute : <BR>
	 * ((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) ) :
	 * ##any
	 */
	protected Object namespace = "##any";

	/**
	 * Instructions on how to process anyAttribute content : <br>
	 * (lax | skip | strict) : strict <BR>
	 * <BR>
	 * lax = can-do validation <BR>
	 * strict = MUST validate <BR>
	 * skip = MUST NOT validate<BR>
	 * <BR>
	 * For example, is the namespace is set to xhtml namespace, we will try to
	 * validate content against xhtml schemas
	 * 
	 */
	protected String processContents = "strict";

	/**
	 * 
	 */
	public ZaxCWildCard() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the id
	 */
	public IDBuffer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(IDBuffer id) {
		this.id = id;
	}

	/**
	 * @return the namespace
	 */
	public Object getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 * @throws SyntaxException 
	 */
	public void setNamespace(String ns) throws SyntaxException {
		
		// Parse namespace to get the correct values
		if (ns==null || ns.length()==0) ;
		else {
			
			// Split in a list
			String[] lst = ns.split(" ");
			if (lst.length==1 && ns.equals("##any"))
				this.namespace = "##any";
			else if (lst.length==1 && ns.equals("##other"))
				this.namespace = "##other";
			else if (lst.length>1) {
				
				this.namespace = new LinkedList<Object>();
				for (String str : lst) {
					if (str.equals("##targetNamespace"))
						((List<Object>)this.namespace).add("##targetNamespace");
					else if (str.equals("##local"))
						((List<Object>)this.namespace).add("##local");
					else
						((List<Object>)this.namespace).add(new AnyURIBuffer(str));
				}
				
			}
			
		}
		
		this.namespace = namespace;
	}

	/**
	 * @return the processContents
	 */
	public String getProcessContents() {
		return processContents;
	}

	/**
	 * 
	 */
	public void setProcessContentsLax() {
		this.processContents = "lax";
	}

	/**
	 * 
	 */
	public void setProcessContentsStrict() {
		this.processContents = "strict";
	}

	/**
	 * <dt class="label">skip</dt>
	 * <dd>No constraints at all: the item must simply be well-formed XML.</dd>
	 */
	public void setProcessContentsSkip() {
		this.processContents = "skip";
	}

	/**
	 * @param processContents the processContents to set
	 * @throws SyntaxException 
	 */
	public void setProcessContents(String pc) throws SyntaxException {
		
		if (pc==null || pc.length()==0) ;
		else if (pc.equals("skip"))
			this.setProcessContentsSkip();
		else if (pc.equals("lax"))
			this.setProcessContentsLax();
		else if (pc.equals("strict"))
			this.setProcessContentsStrict();
		else
			throw new SyntaxException("processContent syntax is not correct, allowed values : (lax | skip | strict)");
		
		this.processContents = pc;
	}

	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String, boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema) throws SyntaxException {
		
	}
	
	
}
