package com.idyria.osi.ooxoo.core.buffers.swing


import com.idyria.osi.ooxoo.core.buffers.structural._
import javax.swing.text._

/**
  This is a special Buffer to connect a Swing Document to the buffer chain, and an XSDStringBuffer

*/
class DocumentBuffer extends BaseBuffer {

  // Plain Document used to connect to Swin UI
  var document = new PlainDocument



  override def streamOut(du: DataUnit) = {

    du.value = document.getText(0,document.getLength)
    super.streamOut(du);

  }
}


