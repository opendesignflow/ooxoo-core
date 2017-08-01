package com.idyria.osi.ooxoo.core.buffers.id

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute

trait ElementWithReferenceID[TT <: ElementWithID] {

  @xattribute(name = "refId")
  var refId: RefIDBuffer[TT] = null
  
  def getReferencedBuffer : Option[TT] = refId match {
    case null => None
    case rid => rid.getReferencedBuffer
  }
  
  def references(id:String) = refId match {
    case null => false
    case other => other.toString == id
  }
  
  def reference(elt: TT) = {
    refId = new RefIDBuffer
    refId.data = elt.eid
    refId.referencedBufferInstance = Some(elt)
  }
}