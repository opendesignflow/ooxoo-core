/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural.io

import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import scala.collection.mutable.Stack
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit

/**
 * @author rleys
 *
 */
abstract class BaseIOBuffer extends IOBuffer with BaseBufferTrait {

  
  var previousStack = new Stack[Buffer]
  
  /**
   * 0 = in
   * 1 = out
   */
  var mode = 1
  
  override def streamOut(du:DataUnit) = {
    
    this.mode = 1
    super.streamOut(du)
    
  }
  
  override def streamIn(du:DataUnit) = {
    
    this.mode = 0
    super.streamIn(du)
    
  }
  
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
      
      //-- If we would go back to a XList in Streamin Mode, skip it
      finalBuffer match {
        case b : XList[_] if(mode==0) => 
          
          finalBuffer = this.previousStack.pop() 
          finalBuffer.setNextBuffer(this)
          
        case b => 
          
          finalBuffer.setNextBuffer(this)
      }
      
      
     // println(s"Buffer removed, so trying to readd to previous hierarchy, remaining on stack: ${this.previousStack.size}")
      
      
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