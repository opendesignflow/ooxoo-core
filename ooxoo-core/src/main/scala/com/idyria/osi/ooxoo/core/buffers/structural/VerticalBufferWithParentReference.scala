package com.idyria.osi.ooxoo.core.buffers.structural

trait VerticalBufferWithParentReference[PT <: ElementBuffer] extends VerticalBuffer {
  
  var parentReference : Option[PT] = None
  
  /**
   * Gets a data unit from chain
   */
  override def streamIn(du: DataUnit) = {
    
   // println("REfparent streamin")
    
    // Feed parent
    du("parent") match {
      case Some(p) =>
        this.parentReference = Some(p.asInstanceOf[PT])
      case other => 
    }
    
    super.streamIn(du)
    
  }
  
}