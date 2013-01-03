/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.datatypes

import com.idyria.osi.ooxoo3.core.buffers.structural.AbstractDataBuffer

/**
 * @author rleys
 *
 */
class XSDStringBuffer extends AbstractDataBuffer[String] with Comparable[String] {

  def this(str: String) = { this(); dataFromString(str) }

  def dataToString: String = {
    this.data
  }
  
  /**
   * Append provided string to existing one
   */
  def dataFromString(str: String): String = {
    
    if (this.data==null)
    	this.data = str
	else
    	this.data+=str  
    this.data
  }

  override def toString: String = {
    if (this.data==null)
      super.toString
    this.data
    
  }

  def equals(comp: XSDStringBuffer): Boolean = {
    this.data == comp.data
  }
  
  def compareTo(comp:String) : Int = {
     this.data.compareTo(comp)
  } 

}
object XSDStringBuffer {

  implicit def convertAnyToXSDStringBuffer(str: Any): XSDStringBuffer = new XSDStringBuffer(str.toString)
  implicit def convertStringToXSDStringBuffer(str: String): XSDStringBuffer = new XSDStringBuffer(str)
  implicit def convertXSDStringBufferToString(str: XSDStringBuffer): String = str.toString

}