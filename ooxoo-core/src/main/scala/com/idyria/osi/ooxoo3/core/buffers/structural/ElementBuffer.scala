package com.idyria.osi.ooxoo3.core.buffers.structural



class ElementBuffer extends VerticalBuffer {

  
  def createDataUnit : DataUnit = {
    
    // Get Element annotation
    //------------------
    var element = xelement.get(this).head
    require(element!=null)
    
   
    
    
    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.element = element
    du.hierarchical = true
    du
  }
  
  
}