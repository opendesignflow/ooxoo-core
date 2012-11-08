/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;



/**
 * Provides definition for string XSD type
 * 
 * 
 * 
 * @author Rtek
 * 
 */
public class XSDStringBuffer extends StringConstrainedTypeBuffer<String> implements Comparable<String>  {

	/**
	 * 
	 */
	public XSDStringBuffer(String str) {
		this._setValueFromString(str);
	}



	/**
	 * 
	 */
	public XSDStringBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String value) {
		super.setValue(value);
		
	}

	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected String convertFromString(String value) {
		// TODO Auto-generated method stub
		return value;
	}



	@Override
	public int compareTo(String o) {
		return this.getValue().compareTo(o);
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		// Delegate String equality to value
		//-------------------
		if (this.value != null && obj!=null && (obj instanceof String)) {
			return this.getValue().equals(obj);
		// Equality With ourselves is relegated to values comparison
		//--------------
		} else if (this.value != null && obj!=null && (XSDStringBuffer.class.isAssignableFrom(obj.getClass()))) {
			return this.getValue().equals(((XSDStringBuffer) obj).getValue());
		}
		// Otherwise let super manage this
		return super.equals(obj);
	}

	
	


/*	@Override
	protected void propagateFromNext(String value) {
		this._setValueFromString(value);
		super.propagateFromNext(value);
	}



	@Override
	protected void propagateFromPrevious(String value) {
		this._setValueFromString(value);
		super.propagateFromPrevious(value);
	}
*/


	/*@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		this.setValue(tu.getValue());
		return tu;
	}*/


	
}
