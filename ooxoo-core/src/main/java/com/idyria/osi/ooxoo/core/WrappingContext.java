/**
 * 
 */
package com.idyria.osi.ooxoo.core;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A context to assist wrapping and unwrapping operations
 * 
 * @author rtek
 * 
 */
public class WrappingContext {

	/**
	 * Maps Buffers types whith a certain buffer chain they should have after them
	 */
	private HashMap<Class<? extends Buffer<?>>,Class<? extends Buffer<?>>> injectionBuffersClassMap = new HashMap<Class<? extends Buffer<?>>,Class<? extends Buffer<?>>>(); 
	
	/**
	 * Maps Buffers types whith a certain already instanciated buffer chain they should have after them
	 */
	private HashMap<Class<Buffer<?>>,Buffer<?>> injectionBuffersInstancesMap = new HashMap<Class<Buffer<?>>,Buffer<?>>(); 
	
	
	/**
	 * Maps Buffers types whith a certain buffer type that should be used instead:
	 * 
	 * Generated type <-> Replacement type
	 * 
	 * The replacing Buffer MUST be extending the base Buffer type, or runtime exceptions are going to happen
	 */
	private HashMap<Class<? extends Buffer>,Class<? extends Buffer>> replacementBuffersClassMap = new HashMap<Class<? extends Buffer>,Class<? extends Buffer>>(); 
	
	
	/**
	 * Maps namespaces and prefixes, to help having a nice unified XML document output
	 */
	private HashMap<String,String> namespacesToPrefixMap = new HashMap<String,String>();
	
	/**
	 * Turns on/off the automatic prefix generation if none exists
	 */
	private boolean namespaceToPrefixGenerate = true;
	
	
	/**
	 * The defered bit indicates to the vertical buffers they should wait for accession to run unwrapping.
	 * This should be useful to avoid unwrapping of document parts that are not accessed
	 */
	private boolean deffered = true;
	
	
	/**
	 * 
	 */
	public WrappingContext() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * From a buffer class, determines the extra buffer chain to be returned for insertion
	 * @param class1
	 * @return The first buffer in the chain, or null if no chain
	 */
	public Buffer<?> resolveBufferChain(Class<? extends Buffer> class1) {
		
		// The resulting chain
		Buffer<?> chain = null;
		
		//-- First look for a first buffer
		Class<? extends Buffer> lookupSource = class1;
		Class<? extends Buffer<?>> nextBuffer = null;
		Buffer<?> lastBuffer = null;
//		do {
			//-- Get the Next buffer in chain
			// That is a key buffer class in map that the studied buffer implements
			for (Entry<Class<? extends Buffer<?>>,Class<? extends Buffer<?>>> entry : this.injectionBuffersClassMap.entrySet()) {
				if (entry.getKey().isAssignableFrom(lookupSource)) {
					nextBuffer = entry.getValue();
				}
			}
			
			// -- If one, make an instance and add to chain
			if (nextBuffer!=null) {
				try {
					//-- Instanciate
					Buffer<?> instance = nextBuffer.newInstance();
					
					//-- Create chain if none, add to lastbuffer
					if (chain==null)
						chain = instance;
					else
						lastBuffer.setNextBuffer(instance);
					// Last buffer is this one
					lastBuffer = instance;
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			//-- Next source is the buffer found
			lookupSource = nextBuffer;
			
//		} while (lookupSource!=null);
		
		
		
		
		return chain;
		
	}


	/**
	 * Maps the namespace to the provided prefix.
	 * This will override any previously set mapping
	 * @param namespace
	 * @param prefix
	 */
	public void addNamespacePrefix(String namespace,String prefix) {
		this.namespacesToPrefixMap.put(namespace, prefix);
	}
	
	/**
	 * Returns a prefix for the provided namespace, or null if none has been mapped
	 * @param namespace
	 * @return The prefix or null if none has been maped
	 */
	public String getPrefixForNamespace(String namespace) {
	
		return this.namespacesToPrefixMap.get(namespace);
	}
	
	
	/**
	 * @return the injectionBuffersClassMap
	 */
	public HashMap<Class<? extends Buffer<?>>, Class<? extends Buffer<?>>> getInjectionBuffersClassMap() {
		return injectionBuffersClassMap;
	}


	/**
	 * @return the injectionBuffersInstancesMap
	 */
	public HashMap<Class<Buffer<?>>, Buffer<?>> getInjectionBuffersInstancesMap() {
		return injectionBuffersInstancesMap;
	}

	/**
	 * @return the replacementBuffersClassMap
	 */
	public HashMap<Class<? extends Buffer>, Class<? extends Buffer>> getReplacementBuffersClassMap() {
		return replacementBuffersClassMap;
	}


	/**
	 * @return the namespaceToPrefixGenerate
	 */
	public boolean isNamespaceToPrefixGenerate() {
		return namespaceToPrefixGenerate;
	}


	/**
	 * @param namespaceToPrefixGenerate the namespaceToPrefixGenerate to set
	 */
	public void setNamespaceToPrefixGenerate(boolean namespaceToPrefixGenerate) {
		this.namespaceToPrefixGenerate = namespaceToPrefixGenerate;
	}


	/**
	 * @return the deffered
	 */
	public boolean isDeffered() {
		return deffered;
	}


	/**
	 * @param deffered the deffered to set
	 */
	public void setDeffered(boolean deffered) {
		this.deffered = deffered;
	}
	
	

}
