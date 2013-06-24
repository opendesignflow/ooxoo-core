package com.idyria.osi.ooxoo.core.buffers.swing

 
import com.idyria.osi.ooxoo.core.buffers.structural._
import scala.swing._
import event._
import swing._
import swing.ListView._
import javax.swing.text._
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils
import scala.reflect.runtime.universe._

class DocumentBuffer extends BaseBuffer {

  var ucd = new PlainDocument

  


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


