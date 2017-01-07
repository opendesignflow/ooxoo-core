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
/**
 *
 */
package com.idyria.osi.ooxoo.core

import java.io.StringReader
import java.io.StringWriter
import org.scalatest.FunSuite
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.VerticalBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.XList
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural._
import org.scalatest.Matchers


/**
 * @author rleys
 *
 */
class StreaminTest extends FunSuite with  Matchers {


  @xelement
  class TestRoot extends ElementBuffer {

    // Base Fields
    //---------------------

    @xattribute
    var attr1 : XSDStringBuffer = null

    @xattribute
    var attr2 : XSDStringBuffer = null

    @xelement
    var subStringMultiple : XList[XSDStringBuffer] =  XList[XSDStringBuffer] {  new XSDStringBuffer }


    @xelement
    var subStringSingle : XSDStringBuffer = null

    var irrelevantField : XSDStringBuffer = null



  }


  test("Annotation Test") {


    var root = new TestRoot

    // Check element presence
    //-----------------------------
    assert(xelement_base(root)!=null)






  }


  test("Stream in a simple element") {

     var xml =  <TestRoot attr1="attr" attr2="attr2">

    		 <subStringMultiple>0</subStringMultiple>
    		 <subStringMultiple>1</subStringMultiple>
    		 <subStringMultiple>2</subStringMultiple>

    		 <subStringSingle>single</subStringSingle>

       </TestRoot>


      /*xml =  <TestRoot attr1="attr" attr2="attr2">



       </TestRoot>*/


     //-- Create StxAx IO
     var xout = new StringWriter
     scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
     var staxio = new StAXIOBuffer(new StringReader(xout.toString()))

     //-- Instanciate Root and stream in
     var root = new TestRoot
     root.appendBuffer(staxio)
     staxio.streamIn

     //-- Check attributes
     //----------------------------
     root.attr1 should not be (null)
     root.attr1.toString should equal("attr")

     root.attr2 should not be(null)
     root.attr2.toString should be ("attr2")


     //-- Check sub elements
     //----------------------------

     //-- Single
     root.subStringSingle should not equal(null)
     root.subStringSingle.toString should equal("single")

     //-- List
     root.subStringMultiple should not equal(null)
     root.subStringMultiple should have length(3)



  }



    test("Stream in a element with attribute in parent class") {


        class Named extends ElementBuffer {

            @xattribute
            var name : XSDStringBuffer = null

         }

        @xelement
        class Test extends Named {

        }


        var xml =  <Test name="hello">
                    </Test>


        //-- Create StxAx IO
        var xout = new StringWriter
        scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
        var staxio = new StAXIOBuffer(new StringReader(xout.toString()))

        //-- Instanciate Root and stream in
        var root = new Test
        root.appendBuffer(staxio)
        staxio.streamIn

        //-- Check attributes
        //----------------------------
        root.name should not be (null)
        root.name.toString should equal("hello")

    }


    test("Stream in a element with attribute in trait class") {


        trait Named  {

            @xattribute
            var name : XSDStringBuffer = null

         }

        @xelement
        class Test extends ElementBuffer with Named {

        }


        var xml =  <Test name="hello">
                    </Test>


        //-- Create StxAx IO
        var xout = new StringWriter
        scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
        var staxio = new StAXIOBuffer(new StringReader(xout.toString()))

        //-- Instanciate Root and stream in
        var root = new Test
        root.appendBuffer(staxio)
        staxio.streamIn

        //-- Check attributes
        //----------------------------
        root.name should not be (null)
        root.name.toString should equal("hello")

    }





    @xelement
    class SubLevelRoot extends ElementBuffer {

      @xelement
	    class SingleSubRoot extends ElementBuffer {

	      @xattribute
	      var attr1 : XSDStringBuffer = null

	    }

	    @xelement
	    class MultipleSubRoot extends ElementBuffer {


	      @xattribute
	      var attr1 : XSDStringBuffer = null

	    }

      @xelement
      var SingleSubRoot : SingleSubRoot = null

      @xelement
      var MultipleSubRoot = XList[MultipleSubRoot]  {  new MultipleSubRoot   }

    }


  test("Stream in a Sublevel element") {

    var xml =  <SubLevelRoot>

    		 <SingleSubRoot attr1="1">0</SingleSubRoot>
    		 <MultipleSubRoot attr1="1">1</MultipleSubRoot>
    		 <MultipleSubRoot attr1="1">2</MultipleSubRoot>

    		 <MultipleSubRoot attr1="1">single</MultipleSubRoot>

       </SubLevelRoot>


     //-- Create StxAx IO
     var xout = new StringWriter
     scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
     var staxio = new StAXIOBuffer(new StringReader(xout.toString()))

     //-- Instanciate Root and stream in
     var root = new SubLevelRoot
     root.appendBuffer(staxio)
     staxio.streamIn

     // Checks
     //---------------------

     //-- Single
     root.SingleSubRoot should not equal(null)
     root.SingleSubRoot.attr1.toString should equal("1")
     //root.subStringSingle.toString should equal("single")

     //-- List
     root.MultipleSubRoot should not equal(null)
     root.MultipleSubRoot should have length(3)


  }

  test("Stream in a hierarchy with Class names different than XML names") {


        trait Named  {

            @xattribute
            var name : XSDStringBuffer = null

         }

        @xelement(name="Test")
        class TestTop extends ElementBuffer with Named {

            @xelement(name="Sub")
            var subElements = XList{new SubTop}

            /*@xelement
            var secondSubElements = XList{new SecondSubTop}*/

        }

        @xelement(name="Sub")
        class SubTop extends ElementBuffer with Named  {

        }

        @xelement(name="Sub2")
        class SecondSubTop extends ElementBuffer with Named  {

        }


        var xml =  <Test name="hello">
                        <Sub name="hello2">theval</Sub>
                    </Test>


        //-- Create StxAx IO
        var xout = new StringWriter
        scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
        var staxio = new StAXIOBuffer(new StringReader(xout.toString()))

        //-- Instanciate Root and stream in
        var root = new TestTop
        root.appendBuffer(staxio)
        staxio.streamIn

        //-- Check attributes
        //----------------------------
        root.name should not be (null)
        root.name.toString should equal("hello")

        //-- Check sub
        //----------------------
        root.subElements.size should be (1)
        root.subElements.head.name.toString should equal("hello2")

        /*root.secondSubElements.size should be (1)
        root.secondSubElements.head.name.toString should equal("hello3")*/
    }

    test("Search in a Sublevel element") {

    /*var xml =  <SubLevelRoot>

         <SingleSubRoot attr1="1">0</SingleSubRoot>
         <MultipleSubRoot attr1="1">1</MultipleSubRoot>
         <MultipleSubRoot attr1="1">2</MultipleSubRoot>

         <MultipleSubRoot attr1="1">single</MultipleSubRoot>

    </SubLevelRoot>


    //-- Create StxAx IO
    var xout = new StringWriter
    scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
    var staxio = new StAXIOBuffer(new StringReader(xout.toString()))

    //-- Instanciate Root and stream in
    var root = new SubLevelRoot
    root.appendBuffer(staxio)
    staxio.streamIn

    // Try Search
    //-----------------
    var res = root.search("MultipleSubRoot")

    // Checks
    //---------------*/

  }

  test("Stream out a simple element") {


    //-- Instanciate Root
    var root = new TestRoot
    root.attr1="test"
    root.attr2="test"
    root.subStringSingle = "test"

    root.subStringMultiple+=root.subStringMultiple.createBuffer
    root.subStringMultiple.last.dataFromString("testM")

    root.subStringMultiple+=root.subStringMultiple.createBuffer
    root.subStringMultiple.last.dataFromString("testM")

    root.subStringMultiple+=root.subStringMultiple.createBuffer
    root.subStringMultiple.last.dataFromString("testM")


    //-- Add IO
    var io = new StAXIOBuffer
    root.appendBuffer(io)

    //-- Streamout
    root.streamOut()

    println("Result: "+io.output.toString())

  }


}
