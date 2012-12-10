/**
 * 
 */
package com.idyria.osi.ooxoo3.core.buffers.datatypes

import com.idyria.osi.ooxoo3.core.buffers.structural.AbstractDataBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo3.core.buffers.structural.element


/**
 * @author rleys
 *
 */
class XSDStringBuffer ( str : String) extends AbstractDataBuffer[String](str) {

    
  def dataToString : String = {
    	this.data
  }
  def dataFromString(str : String) : String = {
    	str
  }
 
  override def toString : String = this.data
  
  
  
  
}
object XSDStringBuffer {
  
  implicit def convertStringToXSDStringBuffer(str:String) : XSDStringBuffer = new XSDStringBuffer(str)
  implicit def convertXSDStringBufferToString(str:XSDStringBuffer) : String = str.toString
  
}