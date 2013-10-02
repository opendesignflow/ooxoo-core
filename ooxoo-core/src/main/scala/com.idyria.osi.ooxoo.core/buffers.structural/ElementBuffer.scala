package com.idyria.osi.ooxoo.core.buffers.structural

 
 
trait ElementBuffer extends VerticalBuffer  {

 
  /**
    Creates a DataUnit representing an Element
  */
  override def createDataUnit : DataUnit = {

    // Get Element annotation
    //------------------
    var element = xelement_base(this)
    if (element==null) {
      throw new IllegalArgumentException(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")
    	logFine(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")

    }

    if (element.name==null || element.name=="") {
      throw new IllegalArgumentException(s"xelement annotation on ElementBuffer ${getClass().getCanonicalName()} did not reutnr any name")
    }


    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.element = element
    du.hierarchical = true
    du
  }
  
  /**
   * Make sure elementbuffer declares a hierarchical data unit
   */
  override def streamOut(du:DataUnit) = {
    
    du.hierarchical = true
    
    super.streamOut(du)
  }


}
