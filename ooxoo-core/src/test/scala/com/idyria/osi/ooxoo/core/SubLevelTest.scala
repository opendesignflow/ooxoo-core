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

import java.io.StringReader
import java.io.StringWriter

import org.scalatest.FunSuite
import org.scalatest.Matchers

import com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer



@xelement(name="Group")
class TestGroup extends ElementBuffer {



    @xelement(name="Group")
    var groups=  XList {  new TestGroup }



}

class SubLevelTest extends FunSuite   with  Matchers  {

    

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
        root.groups.size should be (4)

        //-- Number 2 has 3 Subgroups
        root.groups.get(1).get.groups.size should be (3)

        //-- Number 4 has one subgroup which has 3 subgroups in turn
        root.groups.get(3).get.groups.size should be (1)
        root.groups.get(3).get.groups.head.groups.size should be (3)

        //expectResut(4)(root.groups.size)




    }

}
