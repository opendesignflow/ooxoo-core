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
        var dateString="1998-12-14T23:54:02+0200"

        var buffer = new DateTimeBuffer
        buffer.dataFromString(dateString)

        // Check fields
        //------------------
        expectResult(1998)(buffer.data.get(java.util.Calendar.YEAR))
        expectResult(12)(buffer.data.get(java.util.Calendar.MONTH)+1)
        expectResult(14)(buffer.data.get(java.util.Calendar.DAY_OF_MONTH))

        // The returned hour is the local hour based on the target timezone (the /(3600*1000) is to convert from milliseconds to hours)
        //println(s"parsed timezone: ${buffer.data.get(java.util.Calendar.ZONE_OFFSET)}")
        //println(s"local timezone: ${java.util.TimeZone.getDefault.getRawOffset/(3600*1000)}")
        expectResult(23)(buffer.data.get(java.util.Calendar.HOUR_OF_DAY)+(2-(java.util.TimeZone.getDefault.getRawOffset)/(3600*1000)))

        expectResult(54)(buffer.data.get(java.util.Calendar.MINUTE))
        expectResult(2)(buffer.data.get(java.util.Calendar.SECOND))

        // Reformat and check output, which is now in our local timezone
        //-------------------
        expectResult("1998-12-14T22:54:02+0100")(buffer.toString)

    }



}
