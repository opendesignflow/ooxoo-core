package com.idyria.osi.ooxoo.core

import scala.beans.BeanProperty

import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo.core.buffers.structural.xelement


object SimpleSAXBufferTry extends App {

  
  println("Trying simple Stream out")
  
  // Element Definition
  //-------------------------
  @xelement(name="SimpleRoot")
  class SimpleRoot extends ElementBuffer {
   
    @xattribute(name="test")
    @BeanProperty
    var test = new XSDStringBuffer("Hello World")
    
    @xelement(name="sub1")
    var sub1 = new XSDStringBuffer("Sub Element")
    
    @xelement(name="Sub")
    var sub : Sub = null
    
  }
  
  
  class Sub extends ElementBuffer {
    
    
  } 
  
  
  class MySub extends Sub {
    
    
    
    
    
  }
  
  // Streamout 1
  //-------------------
  var outBuffer = new StAXIOBuffer
  var root = new SimpleRoot()
  root.appendBuffer(outBuffer)
  
  root ->
  
  println("Res Streamout1: ")
  println(outBuffer.output.toString())
  
  
}