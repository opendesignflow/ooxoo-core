/**
 * 
 */
package com.idyria.osi.ooxoo.core.wrap;

import java.lang.annotation.Annotation;


import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.tu.ElementTransferUnit;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.osi.ooxoo.core.tu.WrappingTransferUnit;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;
import com.idyria.utils.java.logging.TeaLogging;

/**
 * @author rtek
 *
 */
public class OOXAnyUnwrapped extends Buffer<Object> {

	/**
	 * 
	 */
	public OOXAnyUnwrapped() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param value
	 */
	public OOXAnyUnwrapped(Object value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	
	
	
	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#detectXMLNaming()
	 */
	@Override
	protected void detectXMLNaming() {
		Ooxnode node = new Ooxnode() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				// TODO Auto-generated method stub
				return Ooxnode.class;
			}
			
			@Override
			public String targetNamespace() {
				// TODO Auto-generated method stub
				return "";
			}
			
			@Override
			public String localName() {
				// TODO Auto-generated method stub
				return "";
			}
		};
		this.setNodeAnnotation(node);
	}

	
	
	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		// Let parent create Unit
		TransferUnit tu = super.createTransferUnit();
	
		// Remove specific Nameing information (habdled by ELement TU content)
		tu.setNodeAnnotation(null);
		this.setNodeAnnotation(null);
		
		// Wrap in a ETU
		ElementTransferUnit etu = new ElementTransferUnit(tu);
		
		// Give content to rewrap in ETU
		etu.setNonWrappedSource(super.value);
		
		return etu;
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#doUnwrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		TeaLogging.teaLogInfo("Unwrapping Any");
		if (WrappingTransferUnit.class.isAssignableFrom(tu.getClass())) {
			this.setValue(((WrappingTransferUnit)tu).getNonWrappedSource());
		}
		return tu;
	}

	/* (non-Javadoc)
	 * @see com.idyria.ooxoo.core.Buffer#doWrapping(com.idyria.ooxoo.core.tu.TransferUnit)
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object convertFromString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
