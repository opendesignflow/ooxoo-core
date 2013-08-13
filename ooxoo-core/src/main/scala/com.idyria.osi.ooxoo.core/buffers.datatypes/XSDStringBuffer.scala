/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer

import scala.language.implicitConversions

/**
 * Buffer used to define a string
 * @author rleys
 *
 */
class XSDStringBuffer extends AbstractDataBuffer[String] with Comparable[String] {

  def this(str: String) = { this(); dataFromString(str) }

  def dataToString: String = {
    this.data
  }

  /**
   * Set provided string to actual data
   */
  def dataFromString(str: String): String =  {this.data = str;data}


  override def toString: String = {
    if (this.data==null)
      super.toString
    this.data

  }

  def equals(comp: XSDStringBuffer): Boolean = {
    this.data == comp.data
  }

  def equals(comp: String): Boolean = {
    this.data == comp
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
