package com.idyria.osi.ooxoo.core.buffers.structural

import com.idyria.osi.tea.logging.TLog

class ElementBuffer extends VerticalBuffer {


  /**
    Creates a DataUnit representing an Element
  */
  override def createDataUnit : DataUnit = {

    // Get Element annotation
    //------------------
    var element = xelement_base(this)
    if (element==null) {
    	TLog.logFine(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")

    }
    require(element!=null)


    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.element = element
    du.hierarchical = true
    du
  }


}
