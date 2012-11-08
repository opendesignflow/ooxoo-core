/**
 * 
 */
package com.idyria.osi.ooxoo.core.tu;

import com.idyria.osi.ooxoo.core.WrappingContext;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;

/**
 * @author rtek
 * 
 */
public class WrappingTransferUnit extends TransferUnit {

	private TransferUnit wrappedTransferUnit = null;

	/**
	 * The object we carry along but not convertible in String
	 */
	private Object nonWrappedSource = null;

	/**
	 * @param tu
	 */
	public WrappingTransferUnit(TransferUnit tu) {
		super();
		this.wrappedTransferUnit = tu;
		this.nodeAnnotation = tu.nodeAnnotation;
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return wrappedTransferUnit.equals(obj);
	}

	/**
	 * @return
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#getNodeAnnotation()
	 */
	public Ooxnode getNodeAnnotation() {
		return wrappedTransferUnit.getNodeAnnotation();
	}

	/**
	 * @return
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#getValue()
	 */
	public String getValue() {
		return wrappedTransferUnit.getValue();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return wrappedTransferUnit.hashCode();
	}

	/**
	 * @param nodeAnnotation
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#setNodeAnnotation(com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode)
	 */
	public void setNodeAnnotation(Ooxnode nodeAnnotation) {
		wrappedTransferUnit.setNodeAnnotation(nodeAnnotation);
	}

	/**
	 * @param value
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		wrappedTransferUnit.setValue(value);
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return wrappedTransferUnit.toString();
	}

	/**
	 * @return the nonWrappedSource
	 */
	public Object getNonWrappedSource() {
		return nonWrappedSource;
	}

	/**
	 * @param nonWrappedSource the nonWrappedSource to set
	 */
	public void setNonWrappedSource(Object nonWrappedSource) {
		this.nonWrappedSource = nonWrappedSource;
	}

	/**
	 * @return
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#isReset()
	 */
	public boolean isReset() {
		return wrappedTransferUnit.isReset();
	}

	/**
	 * @param reset
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#setReset(boolean)
	 */
	public void setReset(boolean reset) {
		wrappedTransferUnit.setReset(reset);
	}

	/**
	 * @return
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#getWrappingContext()
	 */
	public WrappingContext getWrappingContext() {
		return wrappedTransferUnit.getWrappingContext();
	}

	/**
	 * @param wrappingContext
	 * @see com.idyria.osi.ooxoo.core.tu.TransferUnit#setWrappingContext(com.idyria.osi.ooxoo.core.WrappingContext)
	 */
	public void setWrappingContext(WrappingContext wrappingContext) {
		wrappedTransferUnit.setWrappingContext(wrappingContext);
	}

	
	
	
	
}
