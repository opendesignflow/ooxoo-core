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
package com.idyria.osi.ooxoo.model

import java.io._
 
import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._
import org.scalatest._

import com.idyria.osi.ooxoo.model.writers._
import com.idyria.osi.ooxoo.model.out.scala._


import scala.language.postfixOps
import scala.collection.JavaConversions._


class TestModelCompiler extends FunSuite with GivenWhenThen {

    test("Compiler test") {

        var modelInfos = ModelCompiler.compile(new File("src/test/scala/com/idyria/osi/ooxoo/model/SOAP.xmodel"))

        assertResult("SOAP")(modelInfos.name)

        assert(modelInfos.producers!=null,"ModelInfos should contain a producers annotation")
        assertResult(1)(modelInfos.producers.value.length)
        assertResult(classOf[ScalaProducer])(modelInfos.producers.value()(0).value)



    }

    test("Producer test") {

        // Prepare
        //------------------
        var scalaProducer = new ScalaProducer
        scalaProducer.targetPackage = "com.idyria.osi.ooxoo.model.test"
        var writer = new StdoutWriter

        ModelCompiler.produce(new File("src/test/scala/com/idyria/osi/ooxoo/model/SOAP.xmodel"),scalaProducer,writer)

    }


}
