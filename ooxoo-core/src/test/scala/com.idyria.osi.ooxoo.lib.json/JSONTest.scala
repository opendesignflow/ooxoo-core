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
import com.idyria.osi.ooxoo.core.buffers.structural.AnyXList
import com.idyria.osi.ooxoo.core.buffers.structural.any
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.CharArrayWriter
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.tea.logging.TLog

@xelement(name = "Test")
class Test extends ElementBuffer {

  @xelement(name = "SimpleElement")
  var simpleElement: XSDStringBuffer = null
  
  @xattribute(name = "attr1")
  var attr1: XSDStringBuffer = null

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
        println(s"** elt: " + du.element.name + ", value: " + du.value)
    }
  }

}

@xelement(name = "SubTest")
class SubTest extends ElementBuffer {

  @xelement(name = "SimpleElement")
  var simpleElement: XSDStringBuffer = null

  @any
  var content = AnyXList()

}

@xelement(name = "SubSubTest")
class SubSubTest extends ElementBuffer {

  @xelement(name = "Path")
  var path: Path = null

}

@xelement(name = "Path")
class Path extends com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer with com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer {

}

class JSONTest extends FunSuite {

  var input = """{
      
      "Test" : {
		  	"_@attr1" : "AttributeValue",
    		"SimpleElement" : "/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript",
    		"MultipleElement": ["1","2",
    		 "3"],
      
    		"SubTest" : {
    			"SimpleElement" : "Value",
		  		
		  		"SubSubTest": {
    "Path" :"/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript"
		  		}	
    		}
      }
      
      }"""

  /**
   *
   *
   */
  test("Simple Input Parse") {

    var input2 = """{
      
      "Test" : {
       
    		"Header":{},
    		"SimpleElement" : "Value",
      
    		"SubTest" : {
    			"SimpleElement" : "Value"
    		},
    		"SubTest" : {
    			"SimpleElement" : "Value"
    		}
      
    		
      }
      
      }"""

    input2 = """{"Envelope":{"Header":{},"Body":{"RunScriptRequest":{"Path":"/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript"}}}}"""

    // Read
    //--------------
    var io = new JsonIO(new StringReader(input2))
    io.streamIn
    
    io = new JsonIO(new StringReader(input))
    io.streamIn
  }

  test("Simple Input to object") {

    TLog.setLevel(classOf[JsonIO],TLog.Level.FULL)
    
    // Create
    //------------------
    AnyXList(classOf[SubSubTest])
    var top = new Test

    var io = new JsonIO(new StringReader(input))
    top.appendBuffer(io)

    io.streamIn

    // Check
    //--------------------

    // Top
    expectResult("/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript")(top.simpleElement.toString)

    expectResult("AttributeValue")(top.attr1.toString)
    
    expectResult(3)(top.MultipleElement.size)

    // Sub
    assert(top.subTest != null)
    expectResult(top.subTest.simpleElement.toString())("Value")

    // Any
    expectResult(1)(top.subTest.content.size)
    expectResult(classOf[SubSubTest])(top.subTest.content.head.getClass())

    var subsub = top.subTest.content.head.asInstanceOf[SubSubTest]
    expectResult("/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript")(subsub.path.toString)

    println(s"Test: " + top.simpleElement)

    println(s"Multiple: " + top.MultipleElement)

  }

  test("Simple streamout output") {

    var top = new Test
    top.attr1 = "Val"
    top.simpleElement = "Test"
    top.MultipleElement += "1"
    top.MultipleElement += "2"
    top.MultipleElement += "3"
      
    top.subTest = new SubTest
    
    var subsub = new  SubSubTest
    subsub.path = new Path
    subsub.path.data = "/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript"
    top.subTest.content += subsub
    
    subsub = new  SubSubTest
    subsub.path = new Path
    subsub.path.data = "/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript"
    top.subTest.content += subsub

    var out = new CharArrayWriter
    var io = new JsonIO(outputArray =out)
    top.appendBuffer(io)

    top.streamOut()
    
    println(s"Result: "+io.finish)
    
  }

}