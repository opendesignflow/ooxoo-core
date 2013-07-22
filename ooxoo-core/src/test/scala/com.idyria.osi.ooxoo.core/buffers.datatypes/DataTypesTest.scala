package com.idyria.osi.ooxoo.core.buffers.datatypes



import java.io.StringReader
import java.io.StringWriter
import org.scalatest._
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
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
        expect(42)(buffer.data)


        // To String
        //-----------------
        buffer.data = 2 * 42
        expect("84")(buffer.dataToString)

    }

    test("Long Buffer") {

        var buffer = new LongBuffer

        // From String
        //--------------------
        buffer.dataFromString("42")
        expect(42)(buffer.data)


        // To String
        //-----------------
        buffer.data = 2 * 42
        expect("84")(buffer.dataToString)

    }

    test("Float Buffer") {


        var buffer = new FloatBuffer

        // From String
        //--------------------
        buffer.dataFromString("42.0")
        expect(42.0)(buffer.data)


        // To String
        //-----------------
        buffer.data = (2.0 * 42.0).toFloat
        expect("84.0")(buffer.dataToString)

    }

    test("Double Buffer") {


        var buffer = new DoubleBuffer

        // From String
        //--------------------
        buffer.dataFromString("42.0")
        expect(42.0)(buffer.data)


        // To String
        //-----------------
        buffer.data = 2.0 * 42.0
        expect("84.0")(buffer.dataToString)


    }



}
