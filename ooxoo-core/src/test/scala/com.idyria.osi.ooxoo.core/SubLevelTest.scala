package com.idyria.osi.ooxoo.core.buffers.structural

import java.io.StringReader
import java.io.StringWriter
import org.scalatest._
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer

import org.scalatest.matchers.ShouldMatchers


@xelement(name="Group")
class TestGroup extends ElementBuffer {



    @xelement(name="Group")
    var groups=  XList {  new TestGroup }



}

class SubLevelTest extends FunSuite with ShouldMatchers  {

    

    test("SubGroups") {

         var xml =  <Group name="test">

                         <Group name="test">
                            <Group name="test">
                                <Group name="test">
                                </Group>
                            </Group>
                         </Group>

                         <Group name="test">
                            <Group name="test"></Group>
                            <Group name="test"/>
                            <Group name="test"></Group>
                         </Group>

                         <Group name="test"></Group>

                         <Group name="test">
                            <Group name="test">
                                <Group name="test"></Group>
                                <Group name="test"></Group>
                                <Group name="test"></Group>
                            </Group>
                         </Group>

       </Group>

        //-- Create StxAx IO
        var xout = new StringWriter
        scala.xml.XML.write(xout, xml,"UTF-8",true,null,null)
        var staxio = new StAXIOBuffer(new StringReader(xout.toString()))

        //-- Instanciate Root and stream in
        var root = new TestGroup
        root.appendBuffer(staxio)
        staxio.streamIn

        // Check
        //------------------

        //-- Top has 4 groups
        root.groups.size should be === (4)

        //-- Number 2 has 3 Subgroups
        root.groups.get(1).get.groups.size should be === (3)

        //-- Number 4 has one subgroup which has 3 subgroups in turn
        root.groups.get(3).get.groups.size should be === (1)
        root.groups.get(3).get.groups.head.groups.size should be === (3)

        //expectResut(4)(root.groups.size)




    }

}
