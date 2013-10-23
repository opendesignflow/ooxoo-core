/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import scala.language.implicitConversions
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit

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
    //println("Called equals to xsdstringbuffer")
    this.data.equals(comp.data) 
  }
  
 
  def equals(comp: String): Boolean = {
    
    //println("Called equals to String")
    this.data == comp
  }

  def compareTo(comp:String) : Int = {
    
    //println("Called compare to to xsdstringbuffer")
     this.data.compareTo(comp)
  }
  
 /* implicit def convertSubClassesToStringBufferType[T <: XSDStringBuffer](str:String) : T = {
    

   var r = this.getClass.newInstance()
    r.dataFromString(str)
   r.asInstanceOf[T]
  }*/
  

}
object XSDStringBuffer {

  def apply(str:String) = new XSDStringBuffer(str)
  
  implicit def convertAnyToXSDStringBuffer(str: Any): XSDStringBuffer = new XSDStringBuffer(str.toString)
  implicit def convertStringToXSDStringBuffer(str: String): XSDStringBuffer = new XSDStringBuffer(str)
  implicit def convertXSDStringBufferToString(str: XSDStringBuffer): String = str.toString


   
}

class CDataBuffer extends XSDStringBuffer {
  
   def this(str: String) = { this(); dataFromString(str) }
  
   /**
    * Override streamout to add cdata parameter to data unit
    */
   override def streamOut(du:DataUnit) = {
     
     du("cdata"->true)
     
     super.streamOut(du)
   }
   
}
object CDataBuffer {
  
  implicit def convertStringToCDataBuffer(str: String): CDataBuffer = new CDataBuffer(str)
  
}

class StringMapBuffer extends MapBuffer[XSDStringBuffer]( { du => new XSDStringBuffer} )

