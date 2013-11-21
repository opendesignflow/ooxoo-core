package com.idyria.osi.ooxoo.lib.json

import org.scalatest.FunSuite
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import java.io.StringReader
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit

@xelement(name = "Test")
class Test extends ElementBuffer {

  @xelement(name = "SimpleElement")
  var simpleElement: XSDStringBuffer = null

  @xelement(name = "MultipleElement")
  var MultipleElement = XList { new XSDStringBuffer }

  @xelement(name = "SubTest")
  var subTest: SubTest = null

  override def streamIn(du: DataUnit) = {
    super.streamIn(du)
    
    println(s"** Got DU in Test")
    du.element match {
      case null =>
        
      case _ => 
        println(s"** elt: "+du.element.name+", value: "+du.value)
    }
  }
  
}

@xelement(name = "SubTest")
class SubTest extends ElementBuffer {

  @xelement(name = "SimpleElement")
  var simpleElement: XSDStringBuffer = null

}

class JSONTest extends FunSuite {

  var input = """{
      
      "Test" : {
       
    		"SimpleElement" : "Value",
    		"MultipleElement": ["1","2",
    		 "3"],
      
    		"SubTest" : {
    			"SimpleElement" : "Value"
    		}
      }
      
      }"""

  test("Simple Input Parse") {

    var input2 = """{
      
      "Test" : {
       
    		"SimpleElement" : "Value",
      
    		"SubTest" : {
    			"SimpleElement" : "Value"
    		},
    		"SubTest" : {
    			"SimpleElement" : "Value"
    		}
      }
      
      }"""

    // Read
    //--------------
    var io = new JsonIO(new StringReader(input))
    io.streamIn
  }

  test("Simple Input to object") {

    // Create
    //------------------
    var top = new Test

    var io = new JsonIO(new StringReader(input))
    top.appendBuffer(io)

    io.streamIn
    
    // Check
    //--------------------
    expectResult("Value")(top.simpleElement.toString)
    
    expectResult(3)(top.MultipleElement.size)
    
    assert(top.subTest!=null)
    expectResult(top.subTest.simpleElement.toString())("Value")
    
    println(s"Test: "+top.simpleElement)
    
    println(s"Multiple: "+top.MultipleElement)

  }

}