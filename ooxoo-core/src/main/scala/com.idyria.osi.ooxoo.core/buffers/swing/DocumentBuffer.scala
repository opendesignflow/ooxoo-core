package com.idyria.osi.ooxoo.core.buffers.swing

 
import com.idyria.osi.ooxoo.core.buffers.structural._
import javax.swing.text._


class DocumentBuffer extends BaseBuffer {

  var pd = new PlainDocument

  


  def createDataUnit : DataUnit = {
    
    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du
  }


  override def pushOut(du: DataUnit) = {
  
    du.value = "Hello World"
    super.pushOut(du);
    
  }
}


