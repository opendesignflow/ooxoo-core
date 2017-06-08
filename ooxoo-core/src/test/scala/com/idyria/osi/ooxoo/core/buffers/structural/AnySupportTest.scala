/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._
import org.scalatest._
import java.io.ByteArrayOutputStream

@xelement(name = "RootTest")
class RootTest extends ElementBuffer {

  @any
  var content = AnyXList()
}

@xelement(name = "SomeModeledElement")
class SomeModeledElement extends ElementBuffer {

  @xattribute
  var name: XSDStringBuffer = null
}
@xelement(name = "SomeModeledElement2")
class SomeModeledElement2 extends ElementBuffer {

  @xattribute
  var name: XSDStringBuffer = null
}

/**
 * All the tests in this class parse two any content elements, because only one might hide a state error in the parser
 *
 */
class AnySupportTest extends FunSuite with GivenWhenThen {

  test("Streamin Any Content that does not match an existing model") {

    Given("A Simple XML Document")
    //---------------------------------
    var xml = """
            <RootTest>
                <SomeElement name="test"><SomeSubElement/></SomeElement>
                <SomeElement2 name="test2"></SomeElement2>
            </RootTest>"""

    var parsed = new RootTest()
    parsed - StAXIOBuffer(xml)
    parsed.lastBuffer.streamIn

    Then("The parsed model any content has two Elements")
    //--------------------
    assertResult(2)(parsed.content.size)
    assertResult("SomeElement")(parsed.content.head.asInstanceOf[AnyElementBuffer].name)
    assertResult("SomeElement2")(parsed.content.last.asInstanceOf[AnyElementBuffer].name)

    And("Both have an attribute called name")
    //-------------------
    assertResult(2)(parsed.content.head.asInstanceOf[AnyElementBuffer].content.size)
    assertResult(1)(parsed.content.last.asInstanceOf[AnyElementBuffer].content.size)

    assertResult("name")(parsed.content.head.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].name)
    assertResult("name")(parsed.content.last.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].name)

    And("Their string values match")
    //-------------------
    assertResult("test")(parsed.content.head.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].data)
    assertResult("test2")(parsed.content.last.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].data)

  }

  test("Streamin Any Content that does not match an existing model and streamout again") {

    Given("A Simple XML Document")
    //---------------------------------
    var xml = """
            <RootTest>
                <SomeElement name="test"><SomeSubElement/></SomeElement>
                <SomeElement2 name="test2"></SomeElement2>
            </RootTest>"""

    xml = """<RootTest><SomeElement name="test"><SomeSubElement/></SomeElement></RootTest>"""

    var parsed = new RootTest()
    parsed - StAXIOBuffer(xml)
    parsed.lastBuffer.streamIn

    println("Last buffer: " + parsed.lastBuffer)

    println("List last buffer: " + parsed.content.lastBuffer)

    var io = new StAXIOBuffer()
    parsed - io
    parsed.streamOut()

    io.output.flush()
    var res = new String(io.output.asInstanceOf[ByteArrayOutputStream].toByteArray)

    println("Result: " + res)

  }

  test("Streamin Any Content that matches an existing model") {

    AnyXList(classOf[SomeModeledElement])
    AnyXList(classOf[SomeModeledElement2])

    Given("A Simple XML Document, with Any Content matching a model class")
    //---------------------------------
    var xml = """
            <RootTest>
                <SomeModeledElement name="test"></SomeModeledElement>
                <SomeModeledElement2 name="test2"></SomeModeledElement2>
            </RootTest>"""

    var parsed = new RootTest()
    parsed - StAXIOBuffer(xml)
    parsed.lastBuffer.streamIn

    Then("The parsed model any content has two Elements")
    //--------------------
    assertResult(2)(parsed.content.size)

    And("Both must be of Class Model type")
    //--------------------------
    assertResult(classOf[SomeModeledElement])(parsed.content.head.getClass)
    assertResult(classOf[SomeModeledElement2])(parsed.content.last.getClass)
    //assertResult(true)(parsed.content.head.isInstanceOf[SomeModeledElement])

    And("Both must be have a correct name attribute")
    //--------------------------
    assertResult("test")(parsed.content.head.asInstanceOf[SomeModeledElement].name.toString)
    assertResult("test2")(parsed.content.last.asInstanceOf[SomeModeledElement2].name.toString)
  }

  // Streamout 
  //------------------------------------------

  test("Stream Out Any Content") {

    // Create
    var gen = new RootTest()

    var elt = new SomeModeledElement()
    gen.content += elt

    var anyelt = new AnyElementBuffer
    anyelt.name = "ANYELEMENT"
    anyelt.text = "VALUE"

    var anyelt2 = new AnyElementBuffer
    anyelt2.name = "ANYELEMENT2"
    //anyelt2.text

    gen.content += anyelt
    gen.content += anyelt2
    gen.content += new SomeModeledElement()
    gen.content += new SomeModeledElement()

    // Streamout
    var io = new StAXIOBuffer()
    gen - io
    gen.streamOut()

    var res = new String(io.output.asInstanceOf[ByteArrayOutputStream].toByteArray)

    // Check
    println(s"Res: $res")
    assertResult(true, "RootTest must contain an any content element")(res.matches(".*<SomeModeledElement.*"))

  }

  /*
    test("Streamin Any Content that matches an existing model with no default constructor") {

        Given("A Simple XML Document, with Any Content matching a model class")
        //---------------------------------
        var xml = """
            <RootTest>
                <SomeModeledElement name="test"></SomeModeledElement>
                <SomeModeledElement2 name="test"></SomeModeledElement2>
            </RootTest>"""

        var parsed = new RootTest()
        parsed - StAXIOBuffer(xml)
        parsed.lastBuffer.streamIn

         Then("The parsed model any content has two Elements")
        //--------------------
        assertResult(2)(parsed.content.size)

        And("Both must be of Class Model type")
        //--------------------------
        assertResult(classOf[SomeModeledElement])(parsed.content.head.getClass)
       // assertResult(true)(parsed.content.head.isInstanceOf[SomeModeledElement])
        //assertResult(true)(parsed.content.head.isInstanceOf[SomeModeledElement])
    }

*/
}
