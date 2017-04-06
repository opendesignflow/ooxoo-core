package com.idyria.osi.ooxoo.core.buffers.datatypes.hash

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.tea.hash.HashUtils

class SHA256StringBuffer extends XSDStringBuffer {
  
  
  override def set(data:String) = {
    super.set(HashUtils.hashBytesAsBase64(data.getBytes, "SHA-256"))
  }
  
  override def equals(comp:String) = {
    this.data == comp ||
    this.data == HashUtils.hashBytesAsBase64(comp.getBytes, "SHA-256")
  }
}

object SHA256StringBuffer {
  
  implicit def convertFromStringToSHA256Buffer(str:String) = {
    
    var b = new SHA256StringBuffer
    b.set(str)
    b
  }
}