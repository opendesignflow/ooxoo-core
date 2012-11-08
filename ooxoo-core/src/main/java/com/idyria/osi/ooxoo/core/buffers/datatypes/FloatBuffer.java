/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;


/**
 * @author Richnou
 *
 */
public class FloatBuffer extends AbstractDataTypesBuffer<java.lang.Float> {

	
	
	/**
	 * 
	 */
	public FloatBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public FloatBuffer(java.lang.Float value) {
		super(value);
		// TODO Auto-generated constructor stub
	}
	
	

	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.impl.AnySimpleType#_setValueFromString(java.lang.String)
	 */
	public void _setValueFromString(String value) {
		this.setValue(this.convertFromString(value));
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected java.lang.Float convertFromString(String value) {
		// TODO Auto-generated method stub
		return java.lang.Float.valueOf(value);
	}
	

	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.impl.AnySimpleType#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.value.toString();
	}

	@Override
	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return false;
	}

	

}
