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

import org.scalatest.FunSuite
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import java.io.ByteArrayOutputStream

class StreamOutTest extends FunSuite {

  @xelement
  class TestRoot extends ElementBuffer {

    // Base Fields
    //---------------------

    @xattribute
    var attr1: XSDStringBuffer = null

    @xattribute
    var attr2: XSDStringBuffer = null

    @xelement(name = "subStringMultiple")
    var subStringMultiple: XList[XSDStringBuffer] = XList[XSDStringBuffer] { new XSDStringBuffer }

    @xelement
    var subSingle: TestRootSub = null

    @xelement
    var subMultiple = XList { new TestRootSub }

    @xelement
    var subStringSingle: XSDStringBuffer = null

    var irrelevantField: XSDStringBuffer = null

  }

  @xelement()
  class TestRootSub extends ElementBuffer {

    @xattribute
    var attr1: XSDStringBuffer = "Default Value"

  }
  
  
  @xelement
  class LazyValTestRoot extends ElementBuffer {

    // Base Fields
    //---------------------

    @xattribute
    var a: XSDStringBuffer = null

    @xattribute
    var b: XSDStringBuffer = null

    @xelement(name = "subStringMultiple")
    var subStringMultiple: XList[XSDStringBuffer] = XList[XSDStringBuffer] { new XSDStringBuffer }

    @xelement
    lazy val c : TestRootSub = new TestRootSub

    @xelement(name="TestRootSubD")
    lazy val d : TestRootSub = new TestRootSub


  }
  

  test("Stream out with lazy val") {
    
    var root = new LazyValTestRoot
    root.c
    
    //-- Add IO
    var outStream = new ByteArrayOutputStream
    var io = StAXIOBuffer(outStream)
    root.appendBuffer(io)

    //-- Streamout
    root.streamOut()

    // Results
    //---------------

    assert(outStream.toByteArray().length > 0, "Data must not be empty")

    println("Result: " + new String(outStream.toByteArray()))
    
  }
  
  test("Stream out a simple element") {

    //-- Instanciate Root
    var root = new TestRoot
    root.attr1 = "test"
    root.attr2 = "test"
    root.subStringSingle = "test"

    root.subStringMultiple += root.subStringMultiple.createBuffer
    root.subStringMultiple.last.dataFromString("testM")

    root.subStringMultiple += root.subStringMultiple.createBuffer
    root.subStringMultiple.last.dataFromString("testM")

    root.subStringMultiple += root.subStringMultiple.createBuffer
    root.subStringMultiple.last.dataFromString("testM")

    root.subSingle = new TestRootSub
    root.subMultiple += new TestRootSub
    root.subMultiple += new TestRootSub

    //-- Add IO
    var outStream = new ByteArrayOutputStream
    var io = StAXIOBuffer(outStream)
    root.appendBuffer(io)

    //-- Streamout
    root.streamOut()

    // Results
    //---------------

    assert(outStream.toByteArray().length > 0, "Data must not be empty")

    println("Result: " + new String(outStream.toByteArray()))

    assert(root.getNextBuffer == null, "IO Buffer must have dissappeard")

  }

  @xelement(name = "ElementSimpleDataType")
  class ElementSimpleDataType extends XSDStringBuffer with ElementBuffer {

    @xattribute
    var attr: XSDStringBuffer = null

  }
  test("Streamout with SimpleDataType as element") {

    var elt = new ElementSimpleDataType
    elt.data = "testelt"

    elt.attr = "testattribute"

    // Streamout
    var outStream = new ByteArrayOutputStream
    var io = StAXIOBuffer(outStream)
    elt.appendBuffer(io)
    
    elt.streamOut()
    
    println("Result: " + new String(outStream.toByteArray()))

  }

}
