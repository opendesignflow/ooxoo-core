/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
        case b : IOTransparentBuffer if(mode==0 && this.previousStack.size>0) => 
          
          finalBuffer = this.previousStack.pop() 
          finalBuffer.setNextBuffer(this)
          
        //  println("IO Jump Buffer now on: "+finalBuffer)
          
        case b => 
          
       //   println("IO Buffer now on: "+finalBuffer)
          
          finalBuffer.setNextBuffer(this)
      }
      
      
     // println(s"Buffer removed, so trying to readd to previous hierarchy, remaining on stack: ${this.previousStack.size}")
      
      
    }
    // If a previous exists -> save it, and ensure it has no reference to this anymore
    //--------
    else if (this.previousBuffer!=null) {
      
      this.previousBuffer.setNextBuffer(null)
      
      //println(s"IO Buffer (${hashCode}) leaves $previousBuffer for: "+finalBuffer)
      this.previousStack.push(this.previousBuffer)
      
      //-- Set new previous buffer next to this
      /*if(buffer!=null)
    	  buffer.setNextBuffer(this)*/
    }
    	
   
      

    // Keep standart behavior
    super.setPreviousBuffer(finalBuffer)
    
    
  }
  
  
  /*override def remove = {
    
    //this.getPreviousBuffer.setNextBuffer(null)
    this.setPreviousBuffer(null)
    
  }*/
  
  
}
