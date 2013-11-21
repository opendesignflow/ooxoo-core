/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural.io

import com.idyria.osi.ooxoo.core.buffers.structural.Buffer

/**
 * 
 * IO Buffer are special kind of buffers that are involved only during push/fetch operations, and must be clonable 
 * for duplication over an object hierarchy.
 * Once used, they are removed from chain, to keep the object hierarchy clean for I/O references that may serialise or deserialise
 * from or to wrong places/data
 * 
 * @author rleys
 *
 */
trait IOBuffer extends Buffer {

  /**
   * Clone this IO for propagating over an object hierarchy
   */
  def cloneIO : IOBuffer
  
  
  
  
  
  
}

trait IOTransparentBuffer {
  
}