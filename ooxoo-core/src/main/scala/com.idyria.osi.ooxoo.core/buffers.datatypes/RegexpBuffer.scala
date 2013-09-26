package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import scala.util.matching.Regex
import scala.language.implicitConversions

/**
 * A Buffer to Set Regular expressions as XML datatype
 */
class RegexpBuffer extends AbstractDataBuffer[Regex] {
  
   def dataFromString(str: String) : Regex = {
     
     str.r
     
   }
   
   def dataToString : String = if (data!=null) data.pattern.pattern(); else "No value"

   override def toString : String = this.dataToString
  
}
object RegexpBuffer {
  
  // Conversions
  //-----------------
  
  implicit def convertFromRegexpToRegexpBuffer(r:Regex) : RegexpBuffer = {
    
    var b = new RegexpBuffer
    b.data = r
    b
  }
  
  implicit def convertFromStringToRegexpBuffer(r:String) : RegexpBuffer = this.convertFromRegexpToRegexpBuffer(r.r)
  
  implicit def convertFromRegexpBufferToRegex(b:RegexpBuffer) : Regex = b.data
  
}