package com.idyria.osi.ooxoo.lib.json

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import java.io.CharArrayWriter

trait JSonUtilTrait extends ElementBuffer {
  
  
  def toJSONString = {
    
    var io = new JsonIO(outputArray = new CharArrayWriter)
    
    this.appendBuffer(io)
    this.streamOut()
    this.cleanIOChain
    
    io.finish
    

    
    
  }
}