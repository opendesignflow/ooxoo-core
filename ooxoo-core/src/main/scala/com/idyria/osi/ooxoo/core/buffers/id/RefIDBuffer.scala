package com.idyria.osi.ooxoo.core.buffers.id

import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer

class RefIDBuffer[TT <: ElementWithID] extends XSDStringBuffer {
  
  var referencedBufferInstance : Option[TT] = None
  
  def getReferencedBuffer = referencedBufferInstance
  
}