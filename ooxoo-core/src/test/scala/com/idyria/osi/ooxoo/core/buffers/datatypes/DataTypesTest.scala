/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes

import java.io.StringReader
import java.io.StringWriter
import org.scalatest._
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.VerticalBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.ooxoo.core.utils._
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream

import scala.language.reflectiveCalls

class DataTypesTest extends FunSuite  with GivenWhenThen {

  test("Int Buffer") {

    var buffer = new IntegerBuffer

    // From String
    //--------------------
    buffer.dataFromString("42")
    assertResult(42)(buffer.data)

    // To String
    //-----------------
    buffer.data = 2 * 42
    assertResult("84")(buffer.dataToString)

  }

  test("Integer Buffer Hexadecimal input") {

    var buffer = new IntegerBuffer

    // From String
    //--------------------
    buffer.dataFromString("0x42")
    assertResult(66)(buffer.data)

  }

  test("Long Buffer") {

    var buffer = new LongBuffer

    // From String
    //--------------------
    buffer.dataFromString("42")
    assertResult(42)(buffer.data)

    // To String
    //-----------------
    buffer.data = 2 * 42
    assertResult("84")(buffer.dataToString)

  }

  test("Long Buffer Hexadecimal input") {

    var buffer = new LongBuffer

    // From String
    //--------------------
    buffer.dataFromString("0x42")
    assertResult(66)(buffer.data)

  }

  test("Float Buffer") {

    var buffer = new FloatBuffer

    // From String
    //--------------------
    buffer.dataFromString("42.0")
    assertResult(42.0)(buffer.data)

    // To String
    //-----------------
    buffer.data = (2.0 * 42.0).toFloat
    assertResult("84.0")(buffer.dataToString)

  }

  test("Double Buffer") {

    var buffer = new DoubleBuffer

    // From String
    //--------------------
    buffer.dataFromString("42.0")
    assertResult(42.0)(buffer.data)

    // To String
    //-----------------
    buffer.data = 2.0 * 42.0
    assertResult("84.0")(buffer.dataToString)

  }

  test("DateTimeBuffer") {

    // Parse String
    //------------------
    var dateString = "1998-12-14T23:54:02+0200"

    var buffer = new DateTimeBuffer
    buffer.dataFromString(dateString)

    // Check fields
    //------------------
    assertResult(1998)(buffer.data.get(java.util.Calendar.YEAR))
    assertResult(12)(buffer.data.get(java.util.Calendar.MONTH) + 1)
    assertResult(14)(buffer.data.get(java.util.Calendar.DAY_OF_MONTH))

    // The returned hour is the local hour based on the target timezone (the /(3600*1000) is to convert from milliseconds to hours)
    //println(s"parsed timezone: ${buffer.data.get(java.util.Calendar.ZONE_OFFSET)}")
    //println(s"local timezone: ${java.util.TimeZone.getDefault.getRawOffset/(3600*1000)}")
    assertResult(23)(buffer.data.get(java.util.Calendar.HOUR_OF_DAY) + (2 - (java.util.TimeZone.getDefault.getRawOffset) / (3600 * 1000)))

    assertResult(54)(buffer.data.get(java.util.Calendar.MINUTE))
    assertResult(2)(buffer.data.get(java.util.Calendar.SECOND))

    // Reformat and check output, which is now in our local timezone
    //-------------------
    // FIXME
    //assertResult("1998-12-14T22:54:02+0100")(buffer.toString)

  }

  @xelement
  class MapContainer extends ElementBuffer {

    @xelement(name = "MapContent")
    var mapContent = MapBuffer { new IntegerBuffer }

  }

  test("Map Buffer") {

    // Create Map Container
    var container = new MapContainer

    // Add some values
    //-----------
    container.mapContent("a") = 1
    container.mapContent("b") = 2
    container.mapContent("c") = 3
    container.mapContent("d") = 4

    // Streamout
    //-----------------
    var outStream = new ByteArrayOutputStream
    StAXIOBuffer.writeToOutputStream(container, outStream)

    println("Result: " + new String(outStream.toByteArray()))

    // Streamin
    //-----------------
    var reread = new MapContainer
    var io = new StAXIOBuffer(new ByteArrayInputStream(outStream.toByteArray()))
    reread.appendBuffer(io)
    io.streamIn

    // Check
    //--------------
    assertResult(4)(reread.mapContent.size)
  }

  @xelement
  class DataMapContainer extends ElementBuffer {

    @xelement(name = "MapContent")
    var mapContent = DataMapBuffer[IntegerBuffer, IntegerBuffer](new IntegerBuffer, new IntegerBuffer)

  }

  test("Data Map Buffer") {

    // Create Map Container
    var container = new DataMapContainer

    // Add some values
    //-----------
    container.mapContent(0) = 1
    container.mapContent(1) = 2
    container.mapContent(2) = 3
    container.mapContent(3) = 4

    // Streamout
    //-----------------
    var result = StAXIOBuffer(container)
    println("Result: " + result)

    // Streamin
    //-----------------
    var reread = new DataMapContainer
    var io = StAXIOBuffer(result)
    reread.appendBuffer(io)
    io.streamIn

    // Check
    //--------------
    assertResult(4)(reread.mapContent.size)
  }

  @xelement
  class EnumContainer extends ElementBuffer {

    @xelement(name = "TestEnum")
    var testEnum: TestEnum = new TestEnum

    @xattribute(name = "attr")
    var attr: TestEnum = new TestEnum

    @xattribute(name = "attr2")
    var attr2 = new com.idyria.osi.ooxoo.core.buffers.datatypes.EnumerationBuffer {

      type state = Value
      val stopped = Value("stopped")
      val running = Value("running")
      val closed = Value("closed")
      def selectstopped: Unit = this select this.stopped
      def selectrunning: Unit = this select this.running
      def selectclosed: Unit = this select this.closed
    }

  }

  class TestEnum extends EnumerationBuffer {

    type TestEnum = Value
    val A = Value("A")
    val C = Value("C")
    val B = Value("B")
    val D = Value("D")
   // val A, B, C, D = Value

  }
  object TestEnum extends Enumeration {

    type TestEnum = Value
    val A, B, C, D = Value

    def unapply[FT <: EnumerationBuffer](value: FT#Value): Boolean = {

      println("in unapply")

      value match {
        case null                                    => true
        case v if (v.toString() == value.toString()) => true
        case _                                       => false
      }

    }

  }

  test("Enum Buffer") {

    // Test
    /*TestEnum.values.foreach {
      v =>
        println("Value: "+v)
    }
    val te  = new TestEnum
    te.values.foreach {
      v =>
        println("Value: "+v)
    }*/

    // Create Container
    var container = new EnumContainer
    container.testEnum = new TestEnum
    // container.testEnum.selectedValue = container.testEnum.A

    // container.testEnum select container.testEnum.B

    container.testEnum.select(TestEnum.C)

    /* container.testEnum.selectedValue match {
      case TestEnum.C => println("Value is C")
      case _ => println("Value is not C")
    }*/

    // Streamout
    //-----------------
    var outStream = new ByteArrayOutputStream
    StAXIOBuffer.writeToOutputStream(container, outStream)

    println("Result: " + new String(outStream.toByteArray()))

    // Streamin
    //-----------------
    container = new EnumContainer
    var io = StAXIOBuffer("""<?xml version="1.0" ?><EnumContainer  attr="C" attr2="closed"><TestEnum>C</TestEnum></EnumContainer>""")
    container.appendBuffer(io)
    io.streamIn

    assert(container.testEnum != null)
    assertResult(container.testEnum.C)(container.testEnum.selectedValue)
    assertResult(container.attr.C)(container.attr.selectedValue)
    assertResult(container.attr2.closed)(container.attr2.selectedValue)

    /*var reread = new MapContainer
    io = new StAXIOBuffer(new ByteArrayInputStream(outStream.toByteArray()))
    reread.appendBuffer(io)
    io.streamIn*/

    // Check
    //--------------
    //assertResult(4)(reread.mapContent.size)

  }

}
