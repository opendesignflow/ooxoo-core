package com.idyria.osi.ooxoo.lib.json

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import java.io.CharArrayWriter
import java.io.StringReader

trait JSonUtilTrait extends ElementBuffer {
  
  
  def toJSONString = {
    
    var io = new JsonIO(outputArray = new CharArrayWriter)
    
    this.appendBuffer(io)
    this.streamOut()
    this.cleanIOChain
    
    io.finish
    

    
    
  }
  
  def fromJSONString(str:String) = {
    
    var io = new JsonIO(stringInput = new StringReader(str))
    
    this.appendBuffer(io)
    io.streamIn
    
    this.cleanIOChain
    
    this
  }
}