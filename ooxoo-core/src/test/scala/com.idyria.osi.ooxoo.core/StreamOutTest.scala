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