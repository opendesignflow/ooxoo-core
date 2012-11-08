/**
 * 
 */
package com.idyria.osi.ooxoo.compiler.xsd.model;

import java.util.LinkedList;
import java.util.List;

import com.idyria.osi.ooxoo.core.buffers.datatypes.IDBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.QNameBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.SyntaxException;
import com.idyria.osi.ooxoo.core.xsd.model.ZaxCAnnotableType;


/**
 * Represents a Simple Type
 * 
 * 
 * @author Rtek
 * 
 */
public class ZaxCSimpleType extends ZaxCAbstractType {

	/**
	 * A restriction, found in a simpleType : <BR/>
	 * 
	 * @author Rtek
	 * 
	 */
	public class restriction extends ZaxCAbstractSimpleTypeRestriction {
		/**
		 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
		 *      boolean)
		 */
		@Override
		public void validateSchema(String parentsName, boolean parentSchema)
				throws SyntaxException {

		}
	}

	/**
	 * An union foud in a simpleType : <BR/>
	 * 
	 * <a name="element-union" id="element-union">&lt;union</a><br>
	 * &nbsp;&nbsp;id = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID">ID</a><br>
	 * &nbsp;&nbsp;memberTypes = List of <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#QName">QName</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, <a class="eltref"
	 * href="#element-simpleType">simpleType</a>*)<br>
	 * &lt;/union&gt;
	 * 
	 * <BR>
	 * <BR>
	 * 
	 * <a id="UnionDt" name="UnionDt">2.3.2 Union Types</a></h4>
	 * <p id="ref46">
	 * <a id="UnionDt" name="UnionDt">Atomic types and list types enable an
	 * element or an attribute value to be one or more instances of one atomic
	 * type. In contrast, a union type enables an element or attribute value to
	 * be one or more instances of one type drawn from the union of multiple
	 * atomic and list types. To illustrate, we create a union type for
	 * representing American states as singleton letter abbreviations or lists
	 * of numeric codes. The <code>
	 zipUnion</code> union type is built from
	 * one atomic type and one list type: </a>
	 * </p>
	 * <div class="exampleOuter"><a id="UnionTypeForZipCodes"
	 * name="UnionTypeForZipCodes"></a><div class="exampleHeader"><a
	 * id="UnionTypeForZipCodes" name="UnionTypeForZipCodes">Example</a></div><div
	 * class="exampleWrapper"><a id="UnionTypeForZipCodes"
	 * name="UnionTypeForZipCodes">Union Type for Zip Codes</a></div><div
	 * class="exampleInner">
	 * 
	 * <pre>
	 * &lt;a id=&quot;UnionTypeForZipCodes&quot; name=&quot;UnionTypeForZipCodes&quot;&gt;&lt;xsd:simpleType name=&quot;zipUnion&quot;&gt;
	 * 	 &lt;xsd:union memberTypes=&quot;USState listOfMyIntType&quot;/&gt;
	 * 	 &lt;/xsd:simpleType&gt;
	 * 
	 * 	 &lt;/a&gt;
	 * </pre>
	 * 
	 * </div></div>
	 * <p>
	 * <a id="UnionTypeForZipCodes" name="UnionTypeForZipCodes"> When we define
	 * a union type, the <code>memberTypes</code> attribute value is a list of
	 * all the types in the union. </a>
	 * </p>
	 * <p>
	 * <a id="UnionTypeForZipCodes" name="UnionTypeForZipCodes"> Now, assuming
	 * we have declared an element called <code>
	 zips</code> of type
	 * <code>zipUnion</code>, valid instances of the element are: </a>
	 * </p>
	 * <div class="exampleOuter"><div class="exampleHeader"><a
	 * id="UnionTypeForZipCodes" name="UnionTypeForZipCodes">Example</a></div><div
	 * class="exampleInner">
	 * 
	 * <pre>
	 * &lt;a id=&quot;UnionTypeForZipCodes&quot; name=&quot;UnionTypeForZipCodes&quot;&gt;&lt;zips&gt;CA&lt;/zips&gt;
	 * 
	 * 	 &lt;zips&gt;95630 95977 95945&lt;/zips&gt;
	 * 	 &lt;zips&gt;AK&lt;/zips&gt;
	 * 	 &lt;/a&gt;
	 * </pre>
	 * 
	 * </div></div>
	 * <p>
	 * <a id="UnionTypeForZipCodes" name="UnionTypeForZipCodes"> Two facets,
	 * <code></code></a><a href="#element-pattern"> pattern</a> and
	 * <code><a href="#element-enumeration">enumeration</a></code>, can be
	 * applied to a union type.
	 * 
	 * @author Rtek
	 * 
	 */
	public class union extends ZaxCAnnotableType {

		/**
		 * The element's Id
		 */
		protected IDBuffer id = null;

		/**
		 * When we define a union type, the memberTypes attribute value is a
		 * list of all the types in the union.
		 */
		protected List<QNameBuffer> memberTypes = null;

		/**
		 * Simple Types to complete memberTypes declarations
		 */
		protected List<ZaxCSimpleType> simpleTypes = null;

		/**
		 * 
		 */
		public union() {
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
		 * @return the memberTypes
		 */
		public List<QNameBuffer> getMemberTypes() {
			if (memberTypes == null)
				memberTypes = new LinkedList<QNameBuffer>();
			return memberTypes;
		}

		/**
		 * @return the simpleTypes
		 */
		public List<ZaxCSimpleType> getSimpleTypes() {
			if (simpleTypes == null)
				simpleTypes = new LinkedList<ZaxCSimpleType>();
			return simpleTypes;
		}

		/**
		 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
		 *      boolean)
		 */
		@Override
		public void validateSchema(String parentsName, boolean parentSchema)
				throws SyntaxException {

		}

	}

	/**
	 * This class represents the <list element that we can find in simpleTypes :
	 * <BR/> <a name="element-list" id="element-list">&lt;list</a><br>
	 * &nbsp;&nbsp;id = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#ID">ID</a><br>
	 * &nbsp;&nbsp;itemType = <a
	 * href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#QName">QName</a><br>
	 * &nbsp;&nbsp;<em>{any attributes with non-schema namespace . . .}</em>&gt;<br>
	 * <em>&nbsp;&nbsp;Content: </em>(<a class="eltref"
	 * href="#element-annotation">annotation</a>?, <a class="eltref"
	 * href="#element-simpleType">simpleType</a>?)<br>
	 * &lt;/list&gt;
	 * 
	 * <P>
	 * In addition to using the built-in list types, you can create new list
	 * types by derivation from existing atomic types. (You cannot create list
	 * types from existing list types, nor from complex types.)
	 * 
	 * @author Rtek
	 * 
	 */
	public class list extends ZaxCAnnotableType {

		/**
		 * The element's Id
		 */
		protected IDBuffer id = null;

		/**
		 * The base type of this list
		 */
		protected QNameBuffer itemType = null;

		/**
		 * a simpleType child for anonymous declarations
		 */
		protected ZaxCSimpleType simpleType = null;

		/**
		 * 
		 */
		public list() {
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
		 * @return the itemType
		 */
		public QNameBuffer getItemType() {
			return itemType;
		}

		/**
		 * @param itemType
		 *            the itemType to set
		 */
		public void setItemType(QNameBuffer itemType) {
			this.itemType = itemType;
		}

		/**
		 * @return the simpleType
		 */
		public ZaxCSimpleType getSimpleType() {
			return simpleType;
		}

		/**
		 * @param simpleType
		 *            the simpleType to set
		 */
		public void setSimpleType(ZaxCSimpleType simpleType) {
			this.simpleType = simpleType;
		}

		/**
		 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
		 *      boolean)
		 */
		@Override
		public void validateSchema(String parentsName, boolean parentSchema)
				throws SyntaxException {

		}

	}

	/**
	 * @see ZaxCSimpleType.restriction
	 */
	private ZaxCSimpleType.restriction restriction = null;

	/**
	 * @see ZaxCSimpleType.list
	 */
	private ZaxCSimpleType.list list = null;

	/**
	 * @see ZaxCSimpleType.union
	 */
	private ZaxCSimpleType.union union = null;

	/**
	 * 
	 */
	public ZaxCSimpleType() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the list
	 */
	public ZaxCSimpleType.list getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(ZaxCSimpleType.list list) {
		this.list = list;
	}

	/**
	 * @return the restriction
	 */
	public ZaxCSimpleType.restriction getRestriction() {
		return restriction;
	}

	/**
	 * @param restriction
	 *            the restriction to set
	 */
	public void setRestriction(ZaxCSimpleType.restriction restriction) {
		this.restriction = restriction;
	}

	/**
	 * @return the union
	 */
	public ZaxCSimpleType.union getUnion() {
		return union;
	}

	/**
	 * @param union
	 *            the union to set
	 */
	public void setUnion(ZaxCSimpleType.union union) {
		this.union = union;
	}

	/**
	 * @see com.idyria.ooxoo.compiler.xsd.model.ZaxCAnnotableType#validateSchema(java.lang.String,
	 *      boolean)
	 */
	@Override
	public void validateSchema(String parentsName, boolean parentSchema)
			throws SyntaxException {

	}
}
