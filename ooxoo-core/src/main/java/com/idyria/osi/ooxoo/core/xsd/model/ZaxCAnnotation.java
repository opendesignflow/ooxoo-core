/**
 * 
 */
package com.idyria.osi.ooxoo.core.xsd.model;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import com.idyria.osi.ooxoo.core.buffers.datatypes.AnyURIBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.LanguageBuffer;



/**
 * @author Rtek
 * 
 */
public class ZaxCAnnotation {

	/**
	 * Represents an appInfo element found in annotations : <br/> <a
	 * name="element-appinfo" id="element-appinfo">&lt;appinfo</a><br>
	 * &nbsp;&nbsp;source = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#anyURI">anyURI</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<em>{any}</em>)*<br>
	 * &lt;/appinfo&gt;
	 * 
	 * @author Rtek
	 * 
	 */
	public class AppInfo {

		/**
		 * 
		 */
		private AnyURIBuffer source = null;

		/**
		 * The element's textContent
		 */
		private String content = null;
		
		/**
		 * The children elements
		 */
		private List<Node> elements = new LinkedList<Node>();
		

		public AppInfo() {

		}

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content
		 *            the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

		/**
		 * @return the source
		 */
		public AnyURIBuffer getSource() {
			return source;
		}

		/**
		 * @param source
		 *            the source to set
		 */
		public void setSource(AnyURIBuffer source) {
			this.source = source;
		}

		/**
		 * @return the elements
		 */
		public List<Node> getElements() {
			return elements;
		}
		
		public void addElement(Node elt) {
			this.elements.add(elt);
		}
		
		public Node getElement(String localName,String namespace) {
			Node res = null;
			for (Node n : this.elements) {
				if (n.getLocalName().equals(localName) && n.getNamespaceURI()!=null && n.getNamespaceURI().equals(namespace)){
					res = n;
					break;
				}
			}
			return res;
		}

	}

	/**
	 * Represents a Documentation element found in annotations: <BR/>
	 * 
	 * <a name="element-documentation"
	 * id="element-documentation">&lt;documentation</a><br>
	 * &nbsp;&nbsp;source = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#anyURI">anyURI</a><br>
	 * &nbsp;&nbsp;xml:lang = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#language">language</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<em>{any}</em>)*<br>
	 * &lt;/documentation&gt;
	 * 
	 * @author Rtek
	 * 
	 */
	public class Documentation extends AppInfo {

		private LanguageBuffer lang = null;

		/**
		 * 
		 */
		public Documentation() {
			// TODO Auto-generated constructor stub
		}

		/**
		 * @return the lang
		 */
		public LanguageBuffer getLang() {
			return lang;
		}

		/**
		 * @param lang
		 *            the lang to set
		 */
		public void setLang(LanguageBuffer lang) {
			this.lang = lang;
		}

	}

	/**
	 * The element's Id
	 */
	protected IDBuffer id = null;

	/**
	 * The list of documentatin children
	 */
	protected List<ZaxCAnnotation.Documentation> docs = null;

	/**
	 * the list of appInfo children
	 */
	protected List<ZaxCAnnotation.AppInfo> appInfos = new LinkedList<ZaxCAnnotation.AppInfo>();

	/**
	 * 
	 */
	public ZaxCAnnotation() {
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
	 * @return the appInfos
	 */
	public List<ZaxCAnnotation.AppInfo> getAppInfos() {
		if (appInfos == null)
			appInfos = new LinkedList<ZaxCAnnotation.AppInfo>();
		return appInfos;
	}

	/**
	 * @return the docs
	 */
	public List<ZaxCAnnotation.Documentation> getDocs() {
		if (docs == null)
			docs = new LinkedList<ZaxCAnnotation.Documentation>();
		return docs;
	}

}
