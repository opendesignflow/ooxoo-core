package com.idyria.osi.ooxoo.core.buffers.structural



class ElementBuffer extends VerticalBuffer {

  
  def createDataUnit : DataUnit = {
    
    // Get Element annotation
    //------------------
    var annotations = xelement.get(this)
    if (annotations.size==0) {
    	println(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")
      
    }
    require(annotations.size>0)
    var element = annotations.head

    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.element = element
    du.hierarchical = true
    du
  }
  
  
}