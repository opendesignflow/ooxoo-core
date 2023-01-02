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
package model

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.GivenWhenThen
import org.odfi.ooxoo.gradle.plugin.ModelCompiler
import java.io.File
import org.odfi.ooxoo.model.out.scala.ScalaProducer
import org.odfi.ooxoo.model.writers.StdoutWriter


class TestModelCompiler extends AnyFunSuite with GivenWhenThen {

    lazy val compiler = {
        val out = new File("build/tmp/oooxoo").getCanonicalFile
        out.mkdirs()
        val c = new ModelCompiler
        c.compiler.setCompilerOutput(out)
        c
    }

    test("Compiler test") {


        var modelInfos = compiler.compile(new File("src/test/scala/model/SOAP.xmodel.scala"))

        assertResult("SOAP")(modelInfos.name)

        assert(modelInfos.producers!=null,"ModelInfos should contain a producers annotation")
        assertResult(1)(modelInfos.producers.value.length)
        assertResult(classOf[ScalaProducer])(modelInfos.producers.value()(0).value)



    }

    test("Producer test") {

        // Prepare
        //------------------
        var scalaProducer = new ScalaProducer
        scalaProducer.targetPackage = "org.odfi.ooxoo.model.test"
        var writer = new StdoutWriter

        compiler.produce(new File("src/test/scala/model/SOAP.xmodel.scala"),scalaProducer,writer)

    }


}
