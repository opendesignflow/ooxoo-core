/**
 * 
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes;

import java.net.URI;
import java.net.URISyntaxException;

import com.idyria.osi.ooxoo.core.tu.TransferUnit;



/**
 * @author Rtek
 *
 */
public class AnyURIBuffer extends StringConstrainedTypeBuffer<URI> {

	
	
	/**
	 * 
	 */
	public AnyURIBuffer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @throws SyntaxException 
	 * @throws SyntaxException 
	 * 
	 */
	public AnyURIBuffer(String uri) throws SyntaxException  {
		this._setValueFromString(uri);
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.XSDType#_setValueFromString(java.lang.String)
	 */
	@Override
	public void _setValueFromString(String uri) {
		this.setValue(this.convertFromString(uri));
		
	}

	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected URI convertFromString(String uri) {
		try {
			URI nuri = new URI(uri);
			return nuri;
		} catch (URISyntaxException e) {
			throw new SyntaxException("Provided AnyURi is not a valid URI : "+e.getMessage());
			//e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.idyria.tools.xml.oox.datatypes.impl.StringConstrainedType#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.value.toASCIIString();
	}

	
	
	/**
	 * @see com.idyria.tools.xml.oox.datatypes.impl.StringConstrainedType#validate()
	 */
	@Override
	public boolean validate() throws SyntaxException {
		// TODO Auto-generated method stub
		return super.validate();
	}



	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return new TransferUnit() {

			@Override
			public String getValue() {
				if (value==null)
					return null;
				return value.toString();
			}
			
		};
	}


	
	
	
}
