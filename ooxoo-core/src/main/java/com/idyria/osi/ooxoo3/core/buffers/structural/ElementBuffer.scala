package com.idyria.osi.ooxoo3.core.buffers.structural


import scala.reflect.runtime.JavaUniverse

class ElementBuffer extends VerticalBuffer {

  
  def createDataUnit : DataUnit = {
    
    // Get Element annotation
    //------------------
    var element = this.getClass().getAnnotation[element](classOf[element])
    require(element!=null)
    
    
    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.element = element
    
    du
  }
  
  
}