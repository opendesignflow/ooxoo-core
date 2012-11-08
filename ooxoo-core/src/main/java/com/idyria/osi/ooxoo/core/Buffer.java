/**
 * 
 */
package com.idyria.osi.ooxoo.core;


import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;
import com.idyria.utils.java.logging.TeaLogging;

/**
 * Base element in the pipeline architecture
 * 
 * @author rtek
 * 
 */
public abstract class Buffer<VT> {

	/**
	 * 
	 */
	protected transient Buffer<?> nextBuffer = null;

	/**
	 * 
	 */
	protected transient Buffer<?> previousBuffer = null;

	/**
	 * The actual holded value  of this buffer
	 */
	protected transient VT value = null;

	/**
	 * The current wrapping context in use
	 */
	protected WrappingContext ooxooWrappingContext = null;
	
	/**
	 * The very importe node annotation containing namespace and node name
	 */
	protected Ooxnode nodeAnnotation = null;

	/**
	 * Marks if the buffer chain resolution added from wrapping context has
	 * already been done
	 */
	protected boolean ooxooAdditionalChainResolved = false;

	/**
	 * Wrapping status
	 */
	private boolean wrapped = false;
	
	/**
	 * Unwrapping status
	 */
	private boolean unwrapped = true;
	
	/**
	 * 
	 */
	public Buffer() {
		detectXMLNaming();
	}

	/**
	 * @param value
	 */
	public Buffer(VT value) {
		super();
		this.value = value;
		detectXMLNaming();
	}

	/**
	 * Detects Namespace and localname
	 */
	protected void detectXMLNaming() {
		// TeaLogging.teaLogInfo("Detecting XML Naming annotation on : "+getClass());
		nodeAnnotation = getClass().getAnnotation(Ooxnode.class);

	}

	/**
	 * 
	 * @return
	 */
	public TransferUnit wrap() {
		return this.wrap(null, null);

	}
	
	/**
	 * 
	 * @return
	 */
	public TransferUnit wrap(WrappingContext ctx) {
		return this.wrap(null, ctx);

	}

	/**
	 * 
	 * @param tu
	 * @param context TODO
	 * @return
	 */
	public TransferUnit wrap(TransferUnit tu, WrappingContext context) {

		// Additional Chain resolution
		//-----------
		this.ooxooResolveAdditionalChain(context);
		
		try {
			// Prepare Transfer Unit
			// ------------------------
			if (tu == null) {
				// -- Create
				tu = this.createTransferUnit();
			} else {	
				
				// -- Do the wrapping
				tu = this.doWrapping(tu);
				
				//-- Reset ?
				if (tu.isReset()) {
					this.reset();
				}
			}

			// If still no TU at this point, chain is broken
			// We do it as: if not null then go
			// ------------------
			if (tu != null) {

				// -- Set Naming information
				if (this.nodeAnnotation != null) {
					tu.setNodeAnnotation(nodeAnnotation);
				}

				// If we have a next buffer, transfer
				if (this.nextBuffer != null) {
					tu = this.nextBuffer.wrap(tu, this.ooxooWrappingContext);
				}

			}

		} finally {
			// -- ALWAYS Remove if transient
			removeIfTransient();
		}

		return tu;

	}

	/**
	 * @throws UnwrapException
	 * 
	 */
	public TransferUnit unwrap() throws UnwrapException {
		return this.unwrap(null, null);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws UnwrapException
	 */
	public TransferUnit unwrap(WrappingContext context) throws UnwrapException {
		return this.unwrap(null, context);
	}

	/**
	 * 
	 * @param tu
	 * @param context TODO
	 */
	public TransferUnit unwrap(TransferUnit tu, WrappingContext context) throws UnwrapException {

		// Additional Chain resolution
		//-----------
		this.ooxooResolveAdditionalChain(context);
		
		// First Propagate to last
		// If we have a next buffer, transfer
		// ----------------------
		if (this.nextBuffer != null) {

			// -- Propagate Naming information
			if (this.nodeAnnotation != null) {
				this.nextBuffer.setNodeAnnotation(nodeAnnotation);
			}

			// -- Propagate
			tu = this.nextBuffer.unwrap(tu, this.ooxooWrappingContext);

			// -- If null, break the chain up
			if (tu == null) {
				throw new UnwrapException("Chain broken at: "
						+ this.nextBuffer.getClass().getSimpleName());
			}
		}

		// Prepare Transfer Unit
		// ------------------------
		if (tu == null) {
			// -- Create
			tu = this.createTransferUnit();

			if (tu == null) {
				throw new UnwrapException("Chain broken at: "
						+ getClass().getSimpleName());
			}

			// -- Set Naming information
			if (this.nodeAnnotation != null) {
				tu.setNodeAnnotation(nodeAnnotation);
			}

		} else {
			
			// Reset ?
			if (tu.isReset()) {
				this.reset();
			}
			
			// -- Do the wrapping
			tu = this.doUnwrapping(tu);
		}

		// -- Remove if transient
		removeIfTransient();

		//-- Set Unwrapped
		this.unwrapped = true;
		
		return tu;
	}

	/**
	 * Do reset, to be overriden
	 */
	protected void reset() {
		
	}
	
	/**
	 * Removes itself if transient
	 */
	private void removeIfTransient() {

		// Don't do if front
		if (this.previousBuffer == null)
			return;

		// Propagate to next
		// if (this.nextBuffer != null)
		// this.nextBuffer.removeIfTransient();

		// Remove self
		if (TransientBuffer.class.isAssignableFrom(this.getClass())) {
//			TeaLogging.teaLogInfo("Removing " + getClass().getSimpleName()
//					+ "(" + hashCode() + ") as transient");
			remove();
		}
	}

	/**
	 * Remove itselft from chain
	 */
	public void remove() {
		// Previous -> Next
		if (getPreviousBuffer() != null) {
			getPreviousBuffer().setNextBuffer(this.nextBuffer);
		}
		// Next <- Previous
		if (getNextBuffer() != null) {
			getNextBuffer().setPreviousBuffer(this.previousBuffer);
		}
	}

	/**
	 * Replaces current Buffer by a new one
	 * 
	 * @param newBuffer
	 * @return the old buffer
	 */
	public Buffer<?> replace(Buffer<?> newBuffer) {

		// Previous Buffer gets new one
		if (this.getPreviousBuffer() != null) {
			TeaLogging.teaLogInfo("Setting newBuffer as next to previous");
			this.getPreviousBuffer().setNextBuffer(newBuffer);
		}
		// New buffer gets current Next as next
		newBuffer.setNextBuffer(this.getNextBuffer());

		return this;
	}

	/**
	 * Create a transfer Unit when there is none
	 * 
	 * @return
	 */
	protected TransferUnit createTransferUnit() {
		// -- Create TU
		TransferUnit tu = new TransferUnit() {

			@Override
			public String getValue() {
				if (super.value == null && Buffer.this.value != null)
					return Buffer.this.value.toString();
				return super.value;
			}
		};

		// -- Propagate Node annotation if necessary
		if (this.getNodeAnnotation() != null) {
			tu.setNodeAnnotation(nodeAnnotation);
		}
		
		//-- Propagate context if necessary
		if (this.ooxooWrappingContext!=null)
			tu.setWrappingContext(this.ooxooWrappingContext);
		
		return tu;
	}

	/**
	 * Do the real local wrapping of a transferUnit
	 * 
	 * @param tu
	 * @return
	 */
	protected abstract TransferUnit doWrapping(TransferUnit tu);

	/**
	 * Do the real local wrapping of a transferUnit
	 * 
	 * @param tu
	 * @return
	 */
	protected abstract TransferUnit doUnwrapping(TransferUnit tu);

	
	/**
	 * Propagation kickoff using convertToString as initial value
	 * @param value
	 */
	public void propagateToNext() {
		this.propagateToNext(this.convertToString());
	}
	
	/**
	 * Propagation kickoff using convertToString as initial value
	 * @param value
	 */
	protected void propagateToPrevious() {
		this.propagateToPrevious(this.convertToString());
	}
	
	/**
	 * 
	 * @param value
	 */
	protected void propagateToNext(String value) {
		// Call on next buffer
		if (this.getNextBuffer()!=null && value!=null)
			this.getNextBuffer().propagateFromPrevious(value);
	}
	
	/**
	 * 
	 * @param value
	 */
	protected void propagateFromNext(String value) {
		this.value = this.convertFromString(value);
		// Receive value from next, child should do something
		// Propagate to previous then
		this.propagateToPrevious(value);
		
	}
	
	/**
	 * 
	 * @param value
	 */
	protected void propagateToPrevious(String value) {
		// Call on previous
		if (this.getPreviousBuffer()!=null)
			this.getPreviousBuffer().propagateFromNext(value);
	}
	
	/**
	 * 
	 * @param value
	 */
	protected void propagateFromPrevious(String value) {
		this.value = this.convertFromString(value);
		// Receive value from previous, child should do something
		// Propagate to next then
		this.propagateToNext(value);
		
	}
	
	/**
	 * Per default calls tostring on the value if it is not null
	 * @return
	 */
	protected String convertToString() {
		if (this.value!=null)
			return this.value.toString();
		return null;
	}
	
	/**
	 * Set local value from string
	 * @param value
	 */
	protected abstract VT convertFromString(String value) ;
	
	
	/**
	 * Look in the context for buffers to insert after this one
	 * 
	 * @param context
	 */
	private void ooxooResolveAdditionalChain(WrappingContext context) {

		// -- Context == null, or resolution already done, fail
		this.ooxooWrappingContext = context;
		//TeaLogging.teaLogInfo("Resolve additional chain ? "+context);
		if (context == null || ooxooAdditionalChainResolved)
			return;
		
		//-- Record as it is there
		this.ooxooWrappingContext = context;

		// -- Get the buffer chain to insert
		//TeaLogging.teaLogInfo("Resolving chain");
		Buffer<?> chain = context.resolveBufferChain(getClass());

		// -- Insert it
		if (chain != null) {
			this.insertNextBuffer(chain);
		}

		// -- Mark done
		this.ooxooAdditionalChainResolved = true;

	}

	/**
	 * @return the nextBuffer
	 */
	public Buffer<?> getNextBuffer() {
		return nextBuffer;
	}

	/**
	 * Get the last buffer in chain from left to right
	 * 
	 * @return
	 */
	public Buffer<?> getRearBuffer() {

		Buffer<?> currentBuffer = this;
		while (currentBuffer.getNextBuffer() != null) {
			currentBuffer = currentBuffer.getNextBuffer();
		}

		return currentBuffer;
	}

	/**
	 * @param nextBuffer
	 *            the nextBuffer to set
	 * @return The Buffer we just set
	 */
	public <BT extends Buffer<?>> BT setNextBuffer(BT nextBuffer) {
		this.nextBuffer = nextBuffer;
		if (this.nextBuffer != null)
			this.nextBuffer.setPreviousBuffer(this);
		return nextBuffer;
	}

	/**
	 * Inserts the buffer as next, and previous next as next of new one
	 * 
	 * @param nextBuffer
	 * @return the inserted Buffer
	 */
	public Buffer<?> insertNextBuffer(Buffer<?> nextBuffer) {
		// Current Next after new one
		nextBuffer.setNextBuffer(this.nextBuffer);
		// Current Next is new one
		this.setNextBuffer(nextBuffer);
		// this.nextBuffer = nextBuffer;
		
		//-- Propagate
		this.propagateToNext();
		
		return nextBuffer;
	}

	/**
	 * Find a specific buffer type in this chain
	 * 
	 * @param bufferType
	 * @return the found buffer or null
	 */
	public <T extends Buffer<?>> T findBuffer(Class<T> bufferType) {

		Buffer<?> currentBuffer = this;
		do {
			// Compare && return if necessary
			if (bufferType.isAssignableFrom(currentBuffer.getClass()))
				return (T) currentBuffer;
			currentBuffer = currentBuffer.getNextBuffer();
		} while (currentBuffer != null);
		return null;

	}
	
	/**
	 * @see #findBuffer(Class)
	 * If not found, the buffer is created after the call point
	 * @param <T>
	 * @param bufferType
	 * @return
	 */
	public <T extends Buffer<?>> T findBufferOrCreate(Class<T> bufferType) {

		Buffer<?> currentBuffer = this;
		Buffer<?> previousBuffer = null;
		Buffer<?> foundBuffer = null;
		do {
			previousBuffer = currentBuffer;
			
			// Compare && return if necessary
			if (bufferType.isAssignableFrom(currentBuffer.getClass())) {
				foundBuffer = currentBuffer;
				break;
			}
			
			currentBuffer = currentBuffer.getNextBuffer();
		} while (currentBuffer != null);
		
		//-- If not found, create at the last one (previousBuffer)
		if (foundBuffer==null) {
			try {
				foundBuffer = this.insertNextBuffer(bufferType.newInstance());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return (T) foundBuffer;

	}

	/**
	 * @return the previousBuffer
	 */
	public Buffer<?> getPreviousBuffer() {
		return previousBuffer;
	}

	/**
	 * @param previousBuffer
	 *            the previousBuffer to set
	 */
	private void setPreviousBuffer(Buffer<?> previousBuffer) {
		this.previousBuffer = previousBuffer;
	}

	/**
	 * Returns a representation of this Buffers chain in a string
	 * 
	 * @return
	 */
	public String toChainString() {

		String res = "";
		Buffer<?> currentBuffer = this;
		while (currentBuffer != null) {
			if (currentBuffer != this)
				res += " <-> ";
			res += currentBuffer.getClass().getSimpleName() + "("
					+ currentBuffer.hashCode() + ")";
			currentBuffer = currentBuffer.getNextBuffer();
		}

		return res;
	}

	/**
	 * @return the value
	 */
	public VT getValue() {
		return value;
	}

	/**
	 * Sets the value. Warning, starts up the propagation in both direction
	 * @param value
	 *            the value to set
	 */
	public void setValue(VT value) {
		this.value = value;
		this.propagateToPrevious();
		this.propagateToNext();
		
	}

	/**
	 * @return the nodeAnnotation
	 */
	public Ooxnode getNodeAnnotation() {
		return nodeAnnotation;
	}

	/**
	 * @param nodeAnnotation
	 *            the nodeAnnotation to set
	 */
	public void setNodeAnnotation(Ooxnode nodeAnnotation) {
		this.nodeAnnotation = nodeAnnotation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Buffer<?> clone() throws CloneNotSupportedException {

		// Clone this
		Buffer<?> localClone = this.doClone();

		// Propagate to next
		if (this.nextBuffer != null) {
			localClone.setNextBuffer(this.nextBuffer.clone());
		}

		return localClone;
	}

	/**
	 * Implement Cloning
	 * 
	 * @return
	 */
	protected Buffer<?> doClone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	/**
	 * Returns true if the buffer has been wrapped
	 * @return the wrapped
	 */
	public synchronized boolean isWrapped() {
		return wrapped;
	}

	/**
	 * Returns true if the buffer has been un wrapped
	 * @return the unwrapped
	 */
	public synchronized boolean isUnwrapped() {
		return unwrapped;
	}
	
	

}
