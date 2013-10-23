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
import org.scalatest.matchers.ShouldMatchers
import com.idyria.osi.ooxoo.core.utils._
import com.idyria.osi.ooxoo.core.buffers.datatypes.MapBuffer
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream

class DataTypesTest extends FunSuite with ShouldMatchers with GivenWhenThen {

  test("Int Buffer") {

    var buffer = new IntegerBuffer

    // From String
    //--------------------
    buffer.dataFromString("42")
    expectResult(42)(buffer.data)

    // To String
    //-----------------
    buffer.data = 2 * 42
    expectResult("84")(buffer.dataToString)

  }

  test("Integer Buffer Hexadecimal input") {

    var buffer = new IntegerBuffer

    // From String
    //--------------------
    buffer.dataFromString("0x42")
    expectResult(66)(buffer.data)

  }

  test("Long Buffer") {

    var buffer = new LongBuffer

    // From String
    //--------------------
    buffer.dataFromString("42")
    expectResult(42)(buffer.data)

    // To String
    //-----------------
    buffer.data = 2 * 42
    expectResult("84")(buffer.dataToString)

  }

  test("Long Buffer Hexadecimal input") {

    var buffer = new LongBuffer

    // From String
    //--------------------
    buffer.dataFromString("0x42")
    expectResult(66)(buffer.data)

  }

  test("Float Buffer") {

    var buffer = new FloatBuffer

    // From String
    //--------------------
    buffer.dataFromString("42.0")
    expectResult(42.0)(buffer.data)

    // To String
    //-----------------
    buffer.data = (2.0 * 42.0).toFloat
    expectResult("84.0")(buffer.dataToString)

  }

  test("Double Buffer") {

    var buffer = new DoubleBuffer

    // From String
    //--------------------
    buffer.dataFromString("42.0")
    expectResult(42.0)(buffer.data)

    // To String
    //-----------------
    buffer.data = 2.0 * 42.0
    expectResult("84.0")(buffer.dataToString)

  }

  test("DateTimeBuffer") {

    // Parse String
    //------------------
    var dateString = "1998-12-14T23:54:02+0200"

    var buffer = new DateTimeBuffer
    buffer.dataFromString(dateString)

    // Check fields
    //------------------
    expectResult(1998)(buffer.data.get(java.util.Calendar.YEAR))
    expectResult(12)(buffer.data.get(java.util.Calendar.MONTH) + 1)
    expectResult(14)(buffer.data.get(java.util.Calendar.DAY_OF_MONTH))

    // The returned hour is the local hour based on the target timezone (the /(3600*1000) is to convert from milliseconds to hours)
    //println(s"parsed timezone: ${buffer.data.get(java.util.Calendar.ZONE_OFFSET)}")
    //println(s"local timezone: ${java.util.TimeZone.getDefault.getRawOffset/(3600*1000)}")
    expectResult(23)(buffer.data.get(java.util.Calendar.HOUR_OF_DAY) + (2 - (java.util.TimeZone.getDefault.getRawOffset) / (3600 * 1000)))

    expectResult(54)(buffer.data.get(java.util.Calendar.MINUTE))
    expectResult(2)(buffer.data.get(java.util.Calendar.SECOND))

    // Reformat and check output, which is now in our local timezone
    //-------------------
    expectResult("1998-12-14T22:54:02+0100")(buffer.toString)

  }

  @xelement
  class MapContainer extends ElementBuffer {

    @xelement(name = "MapContent")
    var mapContent = MapBuffer { new IntegerBuffer}

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
    var io = StAXIOBuffer(outStream)
    container.appendBuffer(io)
    container.streamOut()

    println("Result: " + new String(outStream.toByteArray()))

    // Streamin
    //-----------------
    var reread = new MapContainer
    io = new StAXIOBuffer(new ByteArrayInputStream(outStream.toByteArray()))
    reread.appendBuffer(io)
    io.streamIn

    // Check
    //--------------
    expectResult(4)(reread.mapContent.size)
  }
  
  @xelement
  class DataMapContainer extends ElementBuffer {

    @xelement(name = "MapContent")
    var mapContent = DataMapBuffer[IntegerBuffer,IntegerBuffer](new IntegerBuffer, new IntegerBuffer)

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
    expectResult(4)(reread.mapContent.size)
  }
  
  @xelement
  class EnumContainer extends ElementBuffer {

    @xelement(name = "TestEnum")
    var testEnum : TestEnum = null

  }
  
  class TestEnum extends EnumerationBuffer {
     
     type TestEnum = Value
    val A,B,C,D = Value
    
  }
  object TestEnum extends Enumeration {
    
      type TestEnum = Value
      val A,B,C,D = Value
      
      def unapply[FT <: EnumerationBuffer](value: FT#Value) : Boolean = {
    
    println("in unapply")
    
    value match {
      case null => true
      case v if (v.toString()==value.toString()) => true
      case _ => false
    }
    
  }
    
    
  }
  
  
  test("Enum Buffer") {
    
    // Create Container
    var container = new EnumContainer
    container.testEnum = new TestEnum
   // container.testEnum.selectedValue = container.testEnum.A

   // container.testEnum select container.testEnum.B
    
    container.testEnum select TestEnum.C
    
   /* container.testEnum.selectedValue match {
      case TestEnum.C => println("Value is C")
      case _ => println("Value is not C")
    }*/
    
    // Streamout
    //-----------------
    var outStream = new ByteArrayOutputStream
    var io = StAXIOBuffer(outStream)
    container.appendBuffer(io)
    container.streamOut()

    println("Result: " + new String(outStream.toByteArray()))

    // Streamin
    //-----------------
    /*var reread = new MapContainer
    io = new StAXIOBuffer(new ByteArrayInputStream(outStream.toByteArray()))
    reread.appendBuffer(io)
    io.streamIn*/

    // Check
    //--------------
    //expectResult(4)(reread.mapContent.size)
    
    
  }

}
