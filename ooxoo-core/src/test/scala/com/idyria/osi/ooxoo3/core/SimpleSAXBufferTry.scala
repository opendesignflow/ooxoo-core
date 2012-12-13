package com.idyria.osi.ooxoo3.core

import com.idyria.osi.ooxoo3.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.element
import com.idyria.osi.ooxoo3.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.attribute
import scala.beans.BeanProperty
import com.idyria.osi.ooxoo3.core.buffers.datatypes.XSDStringBuffer


object SimpleSAXBufferTry extends App {

  
  println("Trying simple Stream out")
  
  // Element Definition
  //-------------------------
  @element(name="SimpleRoot")
  class SimpleRoot extends ElementBuffer {
   
    @attribute(name="test")
    @BeanProperty
    var test = new XSDStringBuffer("Hello World")
    
    @element(name="sub1")
    var sub1 = new XSDStringBuffer("Sub Element")
    
    @element(name="Sub")
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