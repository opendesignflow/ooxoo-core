/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2008 - 2014 OSI / Computer Architecture Group @ Uni. Heidelberg
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
  
  root.streamOut() 
  
  println("Res Streamout1: ")
  println(outBuffer.output.toString())
  
  
}
