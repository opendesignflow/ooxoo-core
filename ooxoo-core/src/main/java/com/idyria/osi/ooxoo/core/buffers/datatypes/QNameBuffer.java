/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;


/**
 * @author Rtek
 * 
 */
public class QNameBuffer extends StringConstrainedTypeBuffer<String> {

	private NCNameBuffer prefix = null;

	private NCNameBuffer localPart = null;

	public QNameBuffer() {
		super();
	}

	public QNameBuffer(String value) {
		super();
		this._setValueFromString(value);
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String value) {
		this.setValue(this.convertFromString(value));
		
	}
	
	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected String convertFromString(String value) {
		try {
			// Check null value
			if (value != null && value.length() > 0) {

				// Split String
				String[] arr = value.split(":");

				// Error if form not correct
				if (arr.length > 2)
					throw new SyntaxException(
							"QName string representaiton is invalid, format MUST be : {{namespace}:{local-part}}");

				// Get values
				if (arr.length == 1)
					localPart = new NCNameBuffer(arr[0]);
				else {
					prefix = new NCNameBuffer(arr[0].replaceAll("[{}]*", ""));
					localPart = new NCNameBuffer(arr[1]);
				}
				this.value = prefix + ":" + localPart;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	

	public QNameBuffer(NCNameBuffer pr, NCNameBuffer local) throws SyntaxException {
		this();
		this.prefix = pr;
		this.localPart = local;
		this.value = prefix + ":" + localPart;
		this.validate();
	}

	public String toString() {
		// /return this.nameSpace + ":" + this.localPart;
		return this.value;
	}


	/**
	 * @return Returns the localPart.
	 */
	public NCNameBuffer getLocalPart() {
		return localPart;
	}

	/**
	 * @return Returns the nameSpace.
	 */
	public NCNameBuffer getPrefix() {
		return prefix;
	}
}
