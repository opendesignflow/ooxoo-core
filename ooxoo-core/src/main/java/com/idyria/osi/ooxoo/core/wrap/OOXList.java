/**
 * 
 */
package com.idyria.osi.ooxoo.core.wrap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


import com.idyria.osi.ooxoo.core.Buffer;
import com.idyria.osi.ooxoo.core.UnwrapException;
import com.idyria.osi.ooxoo.core.VerticalBuffer;
import com.idyria.osi.ooxoo.core.WrappingContext;
import com.idyria.osi.ooxoo.core.buffers.common.ElementDefinitionBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.MaxOccursBuffer;
import com.idyria.osi.ooxoo.core.buffers.datatypes.NonNegativeIntegerBuffer;
import com.idyria.osi.ooxoo.core.io.AbstractXMLIO;
import com.idyria.osi.ooxoo.core.tu.TransferUnit;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxattribute;
import com.idyria.osi.ooxoo.core.wrap.annotations.Ooxnode;
import com.idyria.utils.java.logging.TeaLogging;
import com.idyria.utils.java.thread.TeaRunnable;

/**
 * Represents a list of elements
 * 
 * @author rtek
 * 
 */
public abstract class OOXList<E extends Buffer<?>> extends VerticalBuffer<E>
		implements java.io.Serializable, Collection<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4871863070601289693L;

	/**
	 * Underlying Values list
	 */
	private LinkedList<E> values = new LinkedList<E>();

	/**
	 * The minimum number of elements needed
	 */
	protected transient NonNegativeIntegerBuffer min = new NonNegativeIntegerBuffer(
			0);

	/**
	 * The maximum numberof elements needed
	 */
	protected transient MaxOccursBuffer max = new MaxOccursBuffer(0);

	/**
	 * 
	 */
	public OOXList() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param min
	 * @param max
	 */
	public OOXList(NonNegativeIntegerBuffer min, MaxOccursBuffer max) {
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * Implementing classes must Create a new Instance of the object of type E, add it to the list and return this result
	 * @return
	 */
	public abstract E add();

	/**
	 * This method adds a given element
	 * 
	 * @param o
	 */
	public synchronized void addElement(E o) {
		values.add(o);
	}

	public synchronized void removeElement(E o) {
		values.remove(o);
	}

	public int getLength() {
		return this.values.size();
	}

	/**
	 * Get the list content
	 * 
	 * @return
	 */
	public List<E> getValues() {

		// return (List<E>) this.values.clone();
		return this.values;
	}

	/**
	 * Get the element at index in list
	 * 
	 * @param index
	 * @return
	 */
	public E get(int index) {
		return this.values.get(index);
	}

	/**
	 * This method will sort all the elements in the list based on an XPATH-like
	 * expression
	 * 
	 * The sort is by growing order
	 * 
	 * FIXME Add more support - Only
	 * 
	 * @attribute sorting
	 * 
	 * @param xpath
	 */
	public void sort(String xpath) {

		if (xpath == null || !xpath.startsWith("@"))
			return;

		// The attribute to look for
		String attribute = xpath.substring(1);

		// the class
		Class<?> attype = null;

		// The new LinkedList
		LinkedList<E> newlist = new LinkedList<E>();
		// Map between new list objects and their attribute values
		HashMap<Object, Object> newvalues = new HashMap<Object, Object>();
		/*
		 * The first found object with the attribute will determine the type of
		 * the attribute. This type MUST implement Comparable to be able to
		 * enter the process
		 */
		for (int i = 0; i < this.values.size(); i++) {

			E obj = this.values.get(i);
			if (obj == null)
				continue;

			Object attval = findAttribute(obj, attribute);

			// Continue if no attribute found
			if (attval == null)
				continue;

			// Continue if value is not comparable
			if (!Comparable.class.isAssignableFrom(attval.getClass()))
				continue;

			// If it's the first attribute -> record the class
			if (attype == null)
				attype = attval.getClass();
			// Else fail if it is not the same class
			else if (!attype.isAssignableFrom(attval.getClass()))
				continue;

			// Remove the object from main list
			// this.values.remove(i);

			// Add value to map
			newvalues.put(obj, attval);

			// Compare
			boolean added = false;
			for (int j = 0; j < newlist.size(); j++) {

				// Get object
				E tocv = newlist.get(j);

				// Object value
				Object tocval = newvalues.get(tocv);

				// Compare
				int res = ((Comparable<Object>) attval).compareTo(tocval);

				// We need to be equal or smaller to be added, or to be at the
				// end
				if (res <= 0) {
					newlist.add(j, obj);
					added = true;
					break;
				}
			} // END COmpare to everybody

			// If not added, add to the last one
			if (!added)
				newlist.addLast(obj);

		} // End look at all the values

		// this.values.removeAll(newlist);

		// Finally, re-add the sorted list to the values
		// this.values.addAll(newlist);
		this.values.clear();
		this.values.addAll(newlist);

	}

	/**
	 * This method will find the attribute value for an element
	 * 
	 * @param element
	 * @param attribute
	 * @return
	 */
	private Object findAttribute(Object element, String attribute) {

		// All fields
		Vector<Field[]> fields = new Vector<Field[]>();

		// Foreach class chain to find all elements
		// --------------------------------------------------------------------
		Class lowest = element.getClass();
		while (lowest != null) {

			// Get regular fields
			// -----------------------------------
			Field[] fieldsarr = lowest.getDeclaredFields();
			fields.add(fieldsarr);
			lowest = lowest.getSuperclass();

		}

		for (Field[] farr : fields) {
			for (Field f : farr) {

				// Analyse field
				// -------------------------------
				Ooxnode nanno = f.getAnnotation(Ooxnode.class);
				Ooxattribute aanno = f.getAnnotation(Ooxattribute.class);

				if (nanno != null && aanno != null
						&& nanno.localName().equals(attribute)) {

					// Found attribute
					return getValue(f, null, element);

				}
			}
		}

		return null;
	}

	/**
	 * Get the provided field value through a call to a public getter
	 * 
	 * @param f
	 * @param choiceClassname
	 *            TODO
	 * @return
	 */
	private Object getValue(Field f, String Classname, Object target) {

		// Get the value
		// -------------------------------
		Object value = null;
		try {

			// Use the getter method
			Method getter = null;
			if (Classname == null || Classname.length() == 0) {
				getter = f.getDeclaringClass().getMethod(
						"get" + Character.toUpperCase(f.getName().charAt(0))
								+ f.getName().substring(1), (Class[]) null);
			} else {
				getter = f.getDeclaringClass().getMethod(
						"get" + Character.toUpperCase(Classname.charAt(0))
								+ Classname.substring(1), (Class[]) null);
			}

			value = getter.invoke(target, (Object[]) null);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();

		} catch (IllegalAccessException e) {
			e.printStackTrace();

		} catch (SecurityException e) {
			e.printStackTrace();

		} catch (NoSuchMethodException e) {
			e.printStackTrace();

		} catch (InvocationTargetException e) {
			e.printStackTrace();

		}

		return value;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		return this.values.add(e);
	}
	
	

	/**
	 * @param arg0
	 * @param arg1
	 * @see java.util.LinkedList#add(int, java.lang.Object)
	 */
	public void add(int arg0, E arg1) {
		values.add(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return this.values.addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.values.clear();

	}

	/**
	 *  Loop manualy because comparison must be done on the content, not on the compared object
	 */
	@Override
	public boolean contains(Object o) {
		
		// Loop manualy because comparison must be done on the content, not on the compared object
		//------------------
		for (E containedValue : this.values) {
			if ((o==null ? containedValue==null : containedValue.equals(o)))
				return true;
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return this.values.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.values.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return this.values.iterator();
	}

	/**
	 * Loop over the list ourselves to ensure comparison takes place against o, and not o against content
	 */
	@Override
	public boolean remove(Object o) {
		
		// Use an Iterator
		//----------------------
		Iterator<E> it = this.values.iterator();
		while (it.hasNext()) {
			E containedValue = it.next();
			// If equals matches, remove
			if ((o==null ? containedValue==null : containedValue.equals(o))) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Loop ourselves and call {@link #remove(Object)}
	 * 
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		
		boolean result = false;
		for (Object o : c) {
			if (this.remove(o))
				result = true;		
		}
		return false;
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return this.values.retainAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.values.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return this.values.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return this.values.toArray(a);
	}
	
	

	/**
	 * Loop over {@link #values} ourselves, to have {@link #values} content compared to o and not the contrary
	 * 
	 * @param o
	 * @return
	 * @see java.util.LinkedList#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		
		for (int i = 0 ; i < this.size(); i++) {
			if (o==null ? get(i)==null : get(i).equals(o))
				return i;
		}
		
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idyria.ooxoo.core.VerticalBuffer#unwrap(com.idyria.ooxoo.core.tu.
	 * TransferUnit)
	 */
	@Override
	public TransferUnit unwrap(TransferUnit tu, WrappingContext context)
			throws UnwrapException {
		
		//-- Save the context!
		this.ooxooWrappingContext = context;
		this.verticalActions.add(new TeaRunnable<Ooxnode>(OOXList.this
				.getNodeAnnotation()) {

			@Override
			public void run() {

				// -- The List Buffer has received an IO at pre-last
				AbstractXMLIO ioBuffer = OOXList.this
						.findBuffer(AbstractXMLIO.class);

				TransferUnit resTU = null; // Resulting TU

				// Create a Possible Buffer
				// -------------------
				TeaLogging.teaLogInfo("List interface for field "+this.getObject().localName(),1);

				
				// Unwrap
				// -----------
				// -- Unwrap until it fails
				do {
					
					// Create a searched Buffer type instance
					// (Use add to acces generated list instance, and remove. Will be readded later if successful)
					//-----------
					
					//-- Use List or Replacement buffer from context to create
					E buffer = OOXList.this.add();
					OOXList.this.removeElement(buffer);
			
					//--Use a replacement buffer if there is one
					if (ooxooWrappingContext!=null && ooxooWrappingContext.getReplacementBuffersClassMap().containsKey(buffer.getClass())) {
						try {
							buffer = (E) ooxooWrappingContext.getReplacementBuffersClassMap().get(buffer.getClass()).newInstance();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
					
					
					buffer.setNodeAnnotation(this.getObject());
					
					// Take the Chain from parent (after parent)
					// -------------------
					Buffer<?> chainStart = OOXList.this.getNextBuffer();
					// Reset to ensure the previous relation is right there
					OOXList.this.setNextBuffer(chainStart);
					

					// -- Replace IO in chain by a samecontext but different
					// instance
					AbstractXMLIO chainIO = chainStart
							.findBuffer(AbstractXMLIO.class);
					AbstractXMLIO nio = ioBuffer.createSameContext();
					chainIO.replace(nio);

					// -- Refetch chainStart to avoid IO in front problem
					chainStart = OOXList.this.getNextBuffer();

					// -- Don't forget to propagate Ooxnode information
					if (super.getObject() != null)
						chainStart.setNodeAnnotation(super.getObject());

					
					// UNwrap
					//------------
					
					//-- Add parent chain to our temp buffer
					buffer.setNextBuffer(chainStart);

					// Finish unwrap with it
					try {
						resTU = null;
						resTU = buffer.unwrap(ooxooWrappingContext);
						OOXList.this.add(buffer);
					} catch (UnwrapException e) {
						// If exception, then don't use the buffer
						TeaLogging
								.teaLogWarning("Unwrapping failed, pass away: "+e.getMessage());
					}

				} while (resTU != null);

			}

		});

		// Remove all Rear node any node info to avoid unwrapping on this buffer
		// chain
		OOXList.this.setNodeAnnotation(null);

		try {
			// Super will be called
			return super.unwrap(tu, context);
		} catch (UnwrapException e) {
			// Unwrap exception during top doesn't propagate because a list
			// can't be broken, only subelements

		}
		return tu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.idyria.ooxoo.core.VerticalBuffer#wrap(com.idyria.ooxoo.core.tu.
	 * TransferUnit)
	 */
	@Override
	public TransferUnit wrap(TransferUnit tu, WrappingContext context) {

		//-- Save the context!
		this.ooxooWrappingContext = context;
		this.verticalActions.add(new Runnable() {

			@Override
			public void run() {

				// -- The List Buffer has received an IO at last
				AbstractXMLIO ioBuffer = OOXList.this
						.findBuffer(AbstractXMLIO.class);

				TeaLogging.teaLogInfo("OOXList wrapping ("
						+ OOXList.this.toChainString() + ")");

				// -- Just after the OOXList buffer is the normal chain
				// applying to all list members
				Buffer<?> chainStart = OOXList.this.getNextBuffer();

				// Look through list
				for (E buffer : OOXList.this) {

					// Don't forget to propagate Ooxnode information
					// ------------------
					if (OOXList.this.getNodeAnnotation() != null) {

						// -- Given on the list
						buffer.setNodeAnnotation(OOXList.this
								.getNodeAnnotation());
					} else {
						// -- Maybe on the element itself
						buffer.setNodeAnnotation(buffer.getNodeAnnotation());
					}

					// -- Wrap this local buffer with a new io beeing in the
					// same context (~clone)
					// ------------------------------
					TeaLogging.teaLogInfo("OOXList chain start ("
							+ chainStart.toChainString() + ")");

					// Duplicate the chainStart
					try {
						chainStart = chainStart.clone();
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException(e);
					}

					// -- Replace last component of chain by the new IO
					if (chainStart.getNextBuffer() != null) {
						// -- Remove and set on rear buffer for >1 length chains
						AbstractXMLIO cio = chainStart
								.findBuffer(AbstractXMLIO.class);
						TeaLogging.teaLogInfo("\tCIO: " + cio.hashCode());
						AbstractXMLIO nio = ioBuffer.createSameContext();
						TeaLogging.teaLogInfo("\tNIO: " + nio.hashCode());
						cio.getPreviousBuffer().setNextBuffer(nio);
						// .replace(ioBuffer.createSameContext())
						// chainStart.getRearBuffer().remove();
						// chainStart.getRearBuffer().setNextBuffer(
						// ioBuffer.createSameContext());
					} else {
						// -- On single element chain, just replace the whole
						// chain
						chainStart = new ElementDefinitionBuffer();
						chainStart.insertNextBuffer(ioBuffer
								.createSameContext());
					}

					// -- Insert ElementDefinitionBuffer (lists only on
					// elements)
					// chainStart.insertNextBuffer(new
					// ElementDefinitionBuffer());

					// Add chain to buffer && wrap
					// ---------
					buffer.setNextBuffer(chainStart);
					// TeaLogging.teaLogInfo("OOXList after IO ("+chainStart.toChainString()+")");
					TeaLogging.teaLogInfo("OOXList wrap an element("
							+ buffer.toChainString() + ")");
					buffer.wrap(ooxooWrappingContext);

				}

			}

		});

		return super.wrap(null, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idyria.ooxoo.core.Buffer#createTransferUnit()
	 */
	@Override
	protected TransferUnit createTransferUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.idyria.ooxoo.core.Buffer#doUnwrapping(com.idyria.ooxoo.core.tu.
	 * TransferUnit)
	 */
	@Override
	protected TransferUnit doUnwrapping(TransferUnit tu) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.idyria.ooxoo.core.Buffer#doWrapping(com.idyria.ooxoo.core.tu.TransferUnit
	 * )
	 */
	@Override
	protected TransferUnit doWrapping(TransferUnit tu) {

		// Get Last Buffer which should be IO

		return null;
	}

	/* (non-Javadoc)
	 * @see uni.hd.cag.ooxoo.core.Buffer#convertFromString(java.lang.String)
	 */
	@Override
	protected E convertFromString(String value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
