/**
 * 
 */
package com.idyria.osi.ooxoo3.core

import java.io.StringReader
import java.io.StringWriter
import org.scalatest.FunSuite
import com.idyria.osi.ooxoo3.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.VerticalBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.XList
import com.idyria.osi.ooxoo3.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo3.core.buffers.structural.xelement
import org.scalatest.matchers.ShouldMatchers






/**
 * @author rleys
 *
 */
class StreaminTest extends FunSuite with ShouldMatchers{


  @xelement
  class TestRoot extends ElementBuffer {
    
    @xattribute
    var attr1 : XSDStringBuffer = null
    
    @xattribute
    var attr2 : XSDStringBuffer = null
    
    @xelement
    var subStringMultiple : XList[XSDStringBuffer] = new XList[XSDStringBuffer] { 
      
      
      p=> 
        
      def createBuffer : XSDStringBuffer = new XSDStringBuffer
    
    }
    
    @xelement
    var subStringSingle : XSDStringBuffer = null
    
    var irrelevantField : XSDStringBuffer = null 
     
  }
  
  
  
  
  
  

  test("Annotation Test") {
    

    var root = new TestRoot
    
    // Check element presence
    //-----------------------------
    println("In newroot: "+xelement.isPresent(root))
    
    assert(xelement.isPresent(root)===true)
    
    
    var xelements = xelement.get(root)
    assert(xelements.size === 1)
    
    xelements.foreach(a => println(s"Xelement: "+a.name))
    
    
    // Now find the fields of TestRoot
    //-------------
    assert(VerticalBuffer.allFields(root).size===4)
    
    
    
    
    
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
     staxio.fetchIn
     
     //-- Check attributes
     //----------------------------
     root.attr1 should not be === (null)
     root.attr1.toString should equal("attr")
     
     root.attr2 should not be === (null)
     root.attr2.toString should be === ("attr2")
    
     
     //-- Check sub elements
     //----------------------------
     
     //-- Single
     root.subStringSingle should not equal(null)
     root.subStringSingle.toString should equal("single")
     
     //-- List
     root.subStringMultiple should not equal(null)
     root.subStringMultiple should have length(3)
     
     
     
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
      var MultipleSubRoot : XList[MultipleSubRoot] =   new XList[MultipleSubRoot] { 
    		def createBuffer : MultipleSubRoot = new MultipleSubRoot
	    
	   }
      
    }
  
  
  test("Stream in a Sublevel element") {
    
    
    
    
    var xml =  <SubLevelRoot>

    		 <SingleSubRoot attr1="1">0</SingleSubRoot>
    		 <MultipleSubRoot attr1="1">1</MultipleSubRoot>
    		 <MultipleSubRoot attr1="1">2</MultipleSubRoot>
    		 
    		 <MultipleSubRoot attr1="1">single</MultipleSubRoot>

       </SubLevelRoot>
       
       
      /*xml =  <TestRoot attr1="attr" attr2="attr2">

    		

       </TestRoot>*/
       
     
     //-- Create StxAx IO
     var xout = new StringWriter
     scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
     var staxio = new StAXIOBuffer(new StringReader(xout.toString()))
     
     //-- Instanciate Root and stream in
     var root = new SubLevelRoot
     root.appendBuffer(staxio)
     staxio.fetchIn
    
     // Checks
     //---------------------
     
     //-- Single
     root.SingleSubRoot should not equal(null)
     //root.subStringSingle.toString should equal("single")
     
     //-- List
     root.MultipleSubRoot should not equal(null)
     root.MultipleSubRoot should have length(3)
     
    
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
    root ->
    
    println("Result: "+io.output.toString())
    
  }
  
  
}