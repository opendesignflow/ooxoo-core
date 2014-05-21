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
import com.idyria.osi.ooxoo.core.buffers.structural.VerticalBuffer
import org.scalatest.BeforeAndAfter

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

  @xelement(name = "Action")
  var action: Action = new Action

  @xelement(name = "enumelt")
  var enumelt = new com.idyria.osi.ooxoo.core.buffers.datatypes.EnumerationBuffer {

    type state = Value
    val stopped, running, closed = Value
    def selectstopped: Unit = this select this.stopped
    def selectrunning: Unit = this select this.running
    def selectclosed: Unit = this select this.closed
  }

  /*override def streamIn(du: DataUnit) = {
    super.streamIn(du)

    println(s"** Got DU in Test")
    du.element match {
      case null =>

      case _ =>
        println(s"** elt: " + du.element.name + ", value: " + du.value)
    }
  }*/

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

@xelement(name = "Action")
class Action extends com.idyria.osi.ooxoo.core.buffers.datatypes.EnumerationBuffer with com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer {

  type Action = Value
  val stop, suspend, resume, join = Value
  def selectstop: Unit = this select this.stop
  def selectsuspend: Unit = this select this.suspend
  def selectresume: Unit = this select this.resume
  def selectjoin: Unit = this select this.join
}
object Action {

  def apply() = new Action

}

/**
 * Main test
 */
class JSONTest extends FunSuite with BeforeAndAfter {

  after {
    
	 TLog.resetLevels
    
  }
  
  
  
  var input = """{
      
      "Test" : {
		  	"_@attr1" : "AttributeValue",
		    "Action" : "join",
		  	"enumelt" : "running",
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

    TLog.setLevel(classOf[JsonIO], TLog.Level.FULL)
    TLog.setLevel(classOf[VerticalBuffer], TLog.Level.FULL)

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
    assertResult("/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript")(top.simpleElement.toString)

    assertResult("AttributeValue")(top.attr1.toString)

    assertResult(3)(top.MultipleElement.size)

    // Enum
    assertResult(top.enumelt.running)(top.enumelt.selectedValue)
    assertResult(top.action.join)(top.action.selectedValue)

    // Sub
    assert(top.subTest != null)
    assertResult(top.subTest.simpleElement.toString())("Value")

    // Any
    assertResult(1)(top.subTest.content.size)
    assertResult(classOf[SubSubTest])(top.subTest.content.head.getClass())

    var subsub = top.subTest.content.head.asInstanceOf[SubSubTest]
    assertResult("/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript")(subsub.path.toString)

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

    var subsub = new SubSubTest
    subsub.path = new Path
    subsub.path.data = "/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript"
    top.subTest.content += subsub

    subsub = new SubSubTest
    subsub.path = new Path
    subsub.path.data = "/local/home/rleys/git/extoll2/tourmalet-tester/www-inputdata/i2c.sscript"
    top.subTest.content += subsub

    var out = new CharArrayWriter
    var io = new JsonIO(outputArray = out)
    top.appendBuffer(io)

    top.streamOut()

    var res = io.finish
    
    println(s"Result: " + io.finish)
    
    // Checks
    //--------------
    
    //-- Unbalanced braces
    var lbc = res.count(_=='{')
    var rbc = res.count(_=='}')
    assertResult(lbc,"Left and Right curly braces must not be unbalanced")(rbc)
    println(s"Found ${res.count(_=='{')} {")
    println(s"Found ${res.count(_=='}')} }")

  }

}
