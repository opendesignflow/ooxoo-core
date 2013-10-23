package com.idyria.osi.ooxoo.core.buffers.structural


import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._
import org.scalatest._
import java.io.ByteArrayOutputStream


@xelement(name="RootTest")
class RootTest extends ElementBuffer {

    @any
    var content = AnyXList()
}

@xelement(name="SomeModeledElement")
class SomeModeledElement extends ElementBuffer {

    @xattribute
    var name : XSDStringBuffer = null
}
@xelement(name="SomeModeledElement2")
class SomeModeledElement2 extends ElementBuffer {

    @xattribute
    var name : XSDStringBuffer = null
}

/**
    All the tests in this class parse two any content elements, because only one might hide a state error in the parser

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
        expectResult(2)(parsed.content.size)
        expectResult("SomeElement")(parsed.content.head.asInstanceOf[AnyElementBuffer].name)
        expectResult("SomeElement2")(parsed.content.last.asInstanceOf[AnyElementBuffer].name)

        And("Both have an attribute called name")
        //-------------------
        expectResult(2)(parsed.content.head.asInstanceOf[AnyElementBuffer].content.size)
        expectResult(1)(parsed.content.last.asInstanceOf[AnyElementBuffer].content.size)

        expectResult("name")(parsed.content.head.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].name)
        expectResult("name")(parsed.content.last.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].name)

        And("Their string values match")
        //-------------------
        expectResult("test")(parsed.content.head.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].data)
        expectResult("test2")(parsed.content.last.asInstanceOf[AnyElementBuffer].content.head.asInstanceOf[AnyAttributeBuffer].data)

    }
    
     test("Streamin Any Content that does not match an existing model and streamout again") {

        Given("A Simple XML Document")
        //---------------------------------
        var xml = """
            <RootTest>
                <SomeElement name="test"><SomeSubElement/></SomeElement>
                <SomeElement2 name="test2"></SomeElement2>
            </RootTest>"""
        
          xml ="""<RootTest><SomeElement name="test"><SomeSubElement/></SomeElement></RootTest>"""
          
        var parsed = new RootTest()
        parsed - StAXIOBuffer(xml)
        parsed.lastBuffer.streamIn
        
        println("Last buffer: "+parsed.lastBuffer)
        
        println("List last buffer: "+parsed.content.lastBuffer)
        
        var io = new StAXIOBuffer()
        parsed - io
        parsed.streamOut()

        io.output.flush()
        var res = new String(io.output.asInstanceOf[ByteArrayOutputStream].toByteArray)
        
        println("Result: "+res)
       
       
     }

    test("Streamin Any Content that matches an existing model") {

        AnyXList( classOf[SomeModeledElement])
        AnyXList( classOf[SomeModeledElement2])

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
        expectResult(2)(parsed.content.size)

        And("Both must be of Class Model type")
        //--------------------------
        expectResult(classOf[SomeModeledElement])(parsed.content.head.getClass)
        expectResult(classOf[SomeModeledElement2])(parsed.content.last.getClass)
        //expectResult(true)(parsed.content.head.isInstanceOf[SomeModeledElement])

        And("Both must be have a correct name attribute")
        //--------------------------
        expectResult("test")(parsed.content.head.asInstanceOf[SomeModeledElement].name.toString)
        expectResult("test2")(parsed.content.last.asInstanceOf[SomeModeledElement2].name.toString)
    }

    // Streamout 
    //------------------------------------------


    test("Stream Out Any Content") {

        // Create
        var gen = new RootTest()
        gen.content += new SomeModeledElement()

        // Streamout
        var io = new StAXIOBuffer()
        gen - io
        gen.streamOut()

        var res = new String(io.output.asInstanceOf[ByteArrayOutputStream].toByteArray)

        // Check
        println(s"Res: $res") 
        expectResult(true,"RootTest must contain an any content element")(res.matches(".*<SomeModeledElement.*"))
        
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
        expectResult(2)(parsed.content.size)

        And("Both must be of Class Model type")
        //--------------------------
        expectResult(classOf[SomeModeledElement])(parsed.content.head.getClass)
       // expectResult(true)(parsed.content.head.isInstanceOf[SomeModeledElement])
        //expectResult(true)(parsed.content.head.isInstanceOf[SomeModeledElement])
    }

*/
}
