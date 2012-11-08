/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;


/**
 * @author rtek
 *
 */
public class XSDBooleanBuffer extends AbstractDataTypesBuffer<Boolean> implements XSDType {

	
	public XSDBooleanBuffer() {
		this(true);
	}
	
	public XSDBooleanBuffer(String str) {
		super();
		this._setValueFromString(str);
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String str) {
		super.setValue(this.convertFromString(str));
		
	}

	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected Boolean convertFromString(String value) {
		// TODO Auto-generated method stub
		return Boolean.valueOf(value);
	}
	
	/**
	 * 
	 */
	public XSDBooleanBuffer(boolean bool) {
		super();
		this.value = bool;
	}

	
	public void setTrue() {
		this.value = Boolean.TRUE;
	}
	
	public void setFalse() {
		this.value = Boolean.FALSE;
	}
	
	public void setNull() {
		this.value = null;
	}
	
	public static XSDBooleanBuffer fromStringNoValidation(String val) {
		if (val==null)
			return null;
		else
			return new XSDBooleanBuffer(Boolean.parseBoolean(val));
		
	}
	
	/**
	 * @return the value
	 */
	public Boolean getValue() {
		return value;
	}


	/* (non-Javadoc)
	 * @see com.znw.xml.zaxb.datatypes.XSDType#validate()
	 */
	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return false;
	}


	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.value.toString();
	}


	
	
	
}
