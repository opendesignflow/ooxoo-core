/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural.io

import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import scala.collection.mutable.Stack

/**
 * @author rleys
 *
 */
abstract class BaseIOBuffer extends IOBuffer with BaseBufferTrait {

  
  var previousStack = new Stack[Buffer]
  
  /**
   * 
   * IO is supposed to be cloned, but can work with the same instance everywhere
   * 
   * - So when appending to a sub buffer chain, stack the actual previous
   * - When inserting null as previous (because removing), readd to previous in stack
   * 
   * 
   */
  override def setPreviousBuffer(buffer: Buffer) : Buffer = {
    
    var finalBuffer = buffer
    
    // If setting null, try to restore a previous buffer
    //----------------------
    if (buffer == null && this.previousStack.size>0) {
      
      
      
      finalBuffer = this.previousStack.pop() 
      
     // println(s"Buffer removed, so trying to readd to previous hierarchy, remaining on stack: ${this.previousStack.size}")
      
      finalBuffer.setNextBuffer(this)
    }
    // If a previous exists -> save it
    //--------
    else if (this.previousBuffer!=null)
    	this.previousStack.push(this.previousBuffer)
   
      

    // Keep standart behavior
    super.setPreviousBuffer(finalBuffer)
    
    
  }
  
  
  /*override def remove = {
    
    //this.getPreviousBuffer.setNextBuffer(null)
    this.setPreviousBuffer(null)
    
  }*/
  
  
}