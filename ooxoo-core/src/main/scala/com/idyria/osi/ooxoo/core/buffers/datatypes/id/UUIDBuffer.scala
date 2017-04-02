package com.idyria.osi.ooxoo.core.buffers.datatypes.id

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import java.util.UUID

class UUIDBuffer extends AbstractDataBuffer[UUID] {
  
  // Init
  //this.set(UUID.randomUUID().)
  
  def init = {
    this.set(UUID.randomUUID())
  }
  
  def dataToString: String = {
    this.data.toString()

  }

  /**
   * Set provided string to actual data
   */
  def dataFromString(str: String): UUID = str match {
    case null => null
    case s => 
      UUID.fromString(str)

      //data
  }

  override def toString: String = {
    if (this.data == null)
      super.toString
    this.data.toString()

  }
  
}


object UUIDBuffer {
  
  def apply() = {
    var id = new UUIDBuffer
    id.init
    id
  }
  
}