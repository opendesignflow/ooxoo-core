/**
 * 
 */
package com.idyria.osi.ooxoo.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;


import com.idyria.osi.ooxoo.core.buffers.common.AttributeDefinitionBuffer;
import com.idyria.osi.ooxoo.core.buffers.common.ElementDefinitionBuffer;
import com.idyria.osi.ooxoo.core.io.AbstractXMLIO;
import com.idyria.osi.ooxoo.core.tu.ElementTransferUnit;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.osi.ooxoo.core.wrap.OOXAny;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxattribute;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxelement;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;
import com.idyria.utils.java.logging.TeaLogging;
import com.idyria.utils.java.reflect.ReflectUtilities;
import com.idyria.utils.java.thread.TeaRunnable;

/**
 * @author rtek
 * 
 */
public abstract class ElementBuffer<VT> extends VerticalBuffer<VT> {

	/**
	 * 
	 */
	public ElementBuffer() {
		super();
	}

	/**
	 * @param value
	 */
	public ElementBuffer(VT value) {
		super(value);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.idyria.ooxoo.core.VerticalBuffer#wrap(com.idyria.ooxoo.core.tu.
	 * TransferUnit)
	 */
	@Override
	public TransferUnit wrap(TransferUnit tu, WrappingContext context) {

		// Record our vertical action
		super.ooxooWrappingContext = context;
		this.doWrapping(null);

		// Let Buffer do its job
		TransferUnit rtu = super.wrap(tu, context);

		return rtu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		//TeaLogging.teaLogInfo("Creating TU in ElementBuffer");
		// return this.doWrapping(new
		// ElementTransferUnit(super.createTransferUnit()));
		return new ElementTransferUnit(super.createTransferUnit());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idyria.ooxoo.core.Buffer#doWrapping(com.idyria.ooxoo.core.TransferUnit
	 * )
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {

		// Proceed to vertical buffering
		// -------------------------------
		//TeaLogging.teaLogInfo("Wraping in Element (" + this + ")");
		super.verticalActions.add(new TeaRunnable<WrappingContext>(
				ooxooWrappingContext) {

			@Override
			public void run() {
				LinkedList<Field> fields = ReflectUtilities
						.getAllFieldsListFromTop(ElementBuffer.this.getClass());

				while (!fields.isEmpty()) {

					try {

						// -- Get the field && set accessible
						Field f = fields.poll();
						f.setAccessible(true);

						// -- Check Field has a Buffer Type && not null && not
						// transient
						if (Modifier.isTransient(f.getModifiers())
								|| !Buffer.class.isAssignableFrom(f.getType())
								|| f.get(ElementBuffer.this) == null) {
							continue;
						}

//						TeaLogging.teaLogInfo("Elegible field for wrapping: "
//								+ f.getName() + "(in " + ElementBuffer.this
//								+ " ), with value: "
//								+ f.get(ElementBuffer.this));

						// // -- If it is an any buffer, then do at the end
						// if (OOXAny.class.isAssignableFrom(f.getType()) &&
						// !fields.isEmpty()) {
						// TeaLogging.teaLogInfo("\tAny Field, Tossing at the end "
						// + f.getName()
						// + "(in "
						// + ElementBuffer.this.getClass()
						// .getCanonicalName() + " )");
						//
						// fields.addLast(f);
						// continue;
						// }

						// -- Get The value
						Buffer<?> buffer = (Buffer<?>) f
								.get(ElementBuffer.this);

						//-- Check we have QualifiedName for content
						//------------------------

						//-- Do we have declaration on field?
						Ooxnode ooxnode = f.getAnnotation(Ooxnode.class);
						if (ooxnode != null
								&& buffer.getNodeAnnotation() == null) {
							buffer.setNodeAnnotation(ooxnode);
						}

						//-- Check we have the Element/Attribute first after Buffer
						//-------------------------
						Buffer<?> firstAfter = buffer.getNextBuffer();
						if (f.getAnnotation(Ooxelement.class) != null) {
							if (firstAfter == null
									|| !(firstAfter instanceof ElementDefinitionBuffer)) {
								firstAfter = new ElementDefinitionBuffer();
								buffer.insertNextBuffer(firstAfter);
							}
							// continue;

						} else if (f.getAnnotation(Ooxattribute.class) != null) {

							if (firstAfter == null
									|| !(firstAfter instanceof AttributeDefinitionBuffer)) {
								firstAfter = new AttributeDefinitionBuffer();
								buffer.insertNextBuffer(firstAfter);
							}
							// continue;
						}

						// -- Insert subcontext of IO for this buffer chain
						// -----------------------------

						// -- Get Last
						Buffer<?> rear = buffer.getRearBuffer();
						// -- Get First IO in this Element
						AbstractXMLIO parentIO = ElementBuffer.this
								.findBuffer(AbstractXMLIO.class);

						// -- Add/Replace to rear of child
						AbstractXMLIO childIOBuffer = buffer
								.findBuffer(AbstractXMLIO.class);
						if (childIOBuffer != null) {
							childIOBuffer.replace(parentIO.createSubContext());
						} else {
							rear.setNextBuffer(parentIO.createSubContext());
						}

						// -- Wrap
//						TeaLogging.teaLogFiner("");
//						TeaLogging.teaLogFiner("");
//						TeaLogging.teaLogFiner("Wrapping " + f.getName()
//								+ " with chain: " + buffer.toChainString());

						buffer.wrap(super.getObject());

//						TeaLogging.teaLogFiner("Wrapping " + f.getName()
//								+ " done with chain: " + buffer.toChainString());
//						TeaLogging.teaLogFiner("");
//						TeaLogging.teaLogFiner("");

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} // End of on all Fields

			}

		}); // End of Vertical Action

		return tu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.idyria.ooxoo.core.Buffer#doUnwrapping(com.idyria.ooxoo.core.tu.
	 * TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {

//		TeaLogging.teaLogInfo("Doing unwrapping in ElementBuffer");

		// Proceed to vertical buffering
		// -------------------------------
		this.verticalActions.add(new Runnable() {

			@Override
			public void run() {
				LinkedList<Field> fields = ReflectUtilities
						.getAllFieldsListFromTop(ElementBuffer.this.getClass());

				while (!fields.isEmpty()) {

					try {

						// -- Get the field && set accessible
						Field f = fields.poll();

						// Check Field has a Buffer Type && not transient
						if (Modifier.isTransient(f.getModifiers())
								|| !Buffer.class.isAssignableFrom(f.getType())) {
							continue;
						}

//						TeaLogging.teaLogInfo("Elegible field for unwrapping: "
//								+ getClass().getSimpleName() + "/"
//								+ f.getName());

						// -- If it is an any buffer, then do at the end
						// Use accessible boolean to mark already tossed
						if (OOXAny.class.isAssignableFrom(f.getType())
								&& !fields.isEmpty()) {
//							TeaLogging
//									.teaLogInfo("\tAny Field, Tossing at the end "
//											+ f.getName()
//											+ "(in "
//											+ ElementBuffer.this.getClass()
//													.getCanonicalName() + " )");
							f.setAccessible(true);
							fields.addLast(f);
							continue;
						}
						f.setAccessible(true);

						// -- Build the basic unwrapping chain:
						// (Target buffer chain) <-> IO <-> Element/Attribute
						// -------------------------------

						// -- Target and IO
						// ------------------------

						// -- First get Buffer as already instanciated
						Buffer<?> targetBuffer = (Buffer<?>) f
								.get(ElementBuffer.this);
						// TeaLogging.teaLogInfo("Got buffer: "+targetBuffer);

						//-- Instanciate target, or use getter and tru if fails
						if (targetBuffer == null)
							try {

								//-- Direct instance, or use a replacement buffer
								if (ooxooWrappingContext!=null && ooxooWrappingContext.getReplacementBuffersClassMap().containsKey(f.getType())) {
									targetBuffer = (Buffer<?>) ooxooWrappingContext.getReplacementBuffersClassMap().get(f.getType()).newInstance();
								} else
									targetBuffer = (Buffer<?>) f.getType().newInstance();
								
							} catch (InstantiationException e) {
								targetBuffer = (Buffer<?>) ReflectUtilities
										.callGetter(ElementBuffer.this, f, true);
								ReflectUtilities.callSetter(ElementBuffer.this,
										f, null);
							}

						// Parent IO may not be it, but one before the last
						AbstractXMLIO parentIO = ElementBuffer.this
								.findBuffer(AbstractXMLIO.class);
						AbstractXMLIO targetIO = parentIO.createSubContext();
						targetBuffer.getRearBuffer().setNextBuffer(targetIO);
						Buffer<?> nodeType = null;

						// -- Element/Attribute
						if (f.getAnnotation(Ooxelement.class) != null) {
							nodeType = new ElementDefinitionBuffer();
						} else if (f.getAnnotation(Ooxattribute.class) != null) {
							nodeType = new AttributeDefinitionBuffer();
						}
						targetIO.setNextBuffer(nodeType);

						// -- Determine QualifiedName to pickup
						// ------------------------
						Ooxnode ooxnode = f.getAnnotation(Ooxnode.class);
						if (ooxnode != null) {
							targetBuffer.setNodeAnnotation(ooxnode);
						}

						// -- Unwrap && set if no exception
						try {
							targetBuffer.unwrap(ooxooWrappingContext);
//							TeaLogging.teaLogWarning("Setting buffer ("
//									+ targetBuffer.getClass().getSimpleName()
//									+ ") on field " + f.getName());
							f.set(ElementBuffer.this, targetBuffer);

						} catch (UnwrapException e) {
							// Error Here doesn't mean complete error
//							TeaLogging.teaLogWarning("Unwrapping of field ("
//									+ f.getAnnotation(Ooxnode.class)
//											.localName() + ") failed: "
//									+ e.getMessage());
							// f.set(ElementBuffer.this,null);
							// f.set(ElementBuffer.this, targetBuffer);
							// e.printStackTrace();
						}

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		});

		return tu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected VT convertFromString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
